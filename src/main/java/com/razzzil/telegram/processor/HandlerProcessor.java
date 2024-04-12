package com.razzzil.telegram.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razzzil.telegram.PropertyConfiguration;
import com.razzzil.telegram.annotation.CallbackQueryHandler;
import com.razzzil.telegram.annotation.MessageQueryHandler;
import com.razzzil.telegram.annotation.TelegramBot;
import com.razzzil.telegram.bot.Bot;
import com.razzzil.telegram.dto.CallbackDataDto;
import com.razzzil.telegram.dto.HandlerInvocation;
import com.razzzil.telegram.dto.InvocationParameters;
import com.razzzil.telegram.dto.MessageHandlerInvocation;
import com.razzzil.telegram.exception.BotInitializingException;
import com.razzzil.telegram.exception.BotProcessingException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandlerProcessor {

    private final ApplicationContext applicationContext;
    private final PropertyConfiguration propertyConfiguration;
    private final TemplateEngine templateEngine;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    public void invokeCallbackQuery(Bot bot, Update update) {
        BotMethodsHolder.BotMethods botMethods = BotMethodsHolder.getBotMethods(bot.getName());
        String message = update.getCallbackQuery().getData();
        try {
            CallbackDataDto callbackDataDto = OBJECT_MAPPER.readValue(message, CallbackDataDto.class);
            String callbackName = callbackDataDto.getCallbackName();
            HandlerInvocation invocation = botMethods.getCallbackQueryHandler(callbackName);
            invoke(invocation, bot, update, (params, parameter) -> {
                try {
                    Class<?> clazz = parameter.getType();
                    Object object = OBJECT_MAPPER.readValue(callbackDataDto.getData(), clazz);
                    params.add(object);
                } catch (JsonProcessingException e) {
                    throw new BotProcessingException("Unable parse response", e);
                }
            });
        } catch (JsonProcessingException e) {
            throw new BotProcessingException("Unable parse response", e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new BotProcessingException("Internal exception", e);
        }
    }

    public void invokeMessage(Bot bot, Update update) {
        BotMethodsHolder.BotMethods botMethods = BotMethodsHolder.getBotMethods(bot.getName());
        String message = update.getMessage().getText();
        List<MessageHandlerInvocation> invocations = botMethods.getMessageHandlerByRegex(message);
        for (MessageHandlerInvocation messageHandlerInvocation : invocations) {
            HandlerInvocation invocation = messageHandlerInvocation.getHandlerInvocation();
            InvocationParameters invocationParameters = messageHandlerInvocation.getInvocationParameters();
            try {
                invoke(invocation, bot, update, (params, parameter) -> {
                    if (parameter.getType().equals(InvocationParameters.class)) {
                        params.add(invocationParameters);
                    }
                });
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new BotProcessingException("Internal exception", e);
            }
        }
    }

    public Message sendMessage(Bot bot, String templateName, Context ctx, Long chatId) throws TelegramApiException {
        String text = templateEngine.process(templateName, ctx).trim()
                .replace("<plain>", "")
                .replaceAll("\n+", "\n")
                .replace("</plain>", "");
        return bot.sendMessage(text, chatId);
    }

    @PostConstruct
    public void init() throws BotInitializingException {
        log.info("Initializing Telegram Bots");
        for (String basePackage : propertyConfiguration.getBasePackages()) {
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(TelegramBot.class));
            Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(basePackage);
            try {
                for (BeanDefinition bd : beanDefinitions) {
                    Class<?> handlerClass = Class.forName(bd.getBeanClassName());
                    Object bean = applicationContext.getBean(handlerClass);
                    TelegramBot telegramBot = handlerClass.getAnnotation(TelegramBot.class);
                    String botName = telegramBot.value();
                    log.info("Initializing Telegram Bot: [{}]", botName);
                    if (!propertyConfiguration.getBots().containsKey(botName)) {
                        throw new BotInitializingException("Bot [" + botName + "] is not configured");
                    }
                    for (Method method : handlerClass.getMethods()) {
                        log.info("Initializing handlers for {}", botName);
                        for (Annotation annotation : method.getAnnotations()) {
                            BotMethodsHolder.BotMethods botMethods = BotMethodsHolder.createBotMethods(botName);
                            processHandlerAnnotations(method, annotation, bean, botMethods);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new BotInitializingException("Error during initialization. Cannot find class: " + e.getMessage(), e);
            }
        }
    }

    private void processHandlerAnnotations(Method method, Annotation annotation, Object bean,
                                           BotMethodsHolder.BotMethods botMethods) throws BotInitializingException {
        HandlerInvocation handlerInvocation = new HandlerInvocation(method, bean);
        if (annotation.annotationType().equals(CallbackQueryHandler.class)) {
            botMethods.addCallbackHandler(handlerInvocation);
        }
        if (annotation.annotationType().equals(MessageQueryHandler.class)) {
            botMethods.addMessageHandler(handlerInvocation);
        }
    }

    private void invoke(HandlerInvocation invocation, Bot bot, Update update,
                        BiConsumer<ArrayList<Object>, Parameter> consumer) throws InvocationTargetException, IllegalAccessException {
        ArrayList<Object> params = new ArrayList<>();
        for (Parameter methodParameter : invocation.getMethodParameters()) {
            if (methodParameter.getType().equals(Bot.class)) {
                params.add(bot);
            } else if (methodParameter.getType().equals(Update.class)) {
                params.add(update);
            } else consumer.accept(params, methodParameter);
        }
        invocation.invoke(params.toArray());
    }


}
