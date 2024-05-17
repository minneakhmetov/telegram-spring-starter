package com.razzzil.telegram.processor;

import com.razzzil.telegram.dto.HandlerInvocation;
import com.razzzil.telegram.dto.InvocationParameters;
import com.razzzil.telegram.dto.MessageHandlerInvocation;
import com.razzzil.telegram.exception.BotInitializingException;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class BotMethods {
    private final Map<String, HandlerInvocation> callbackQueryHandlers = new HashMap<>();
    private final Map<Pattern, HandlerInvocation> messageHandlers = new HashMap<>();

    HandlerInvocation getCallbackQueryHandler(String callbackQueryName) {
        return callbackQueryHandlers.get(callbackQueryName);
    }

    void addCallbackHandler(HandlerInvocation invocation, String handlerName) throws BotInitializingException {
        if (!callbackQueryHandlers.containsKey(handlerName)) {
            callbackQueryHandlers.put(handlerName, invocation);
        } else
            throw new BotInitializingException(String.format("CallbackQuery Handler %s is duplicated in methods [%s, %s]", handlerName,
                    invocation.getMethod().getDeclaringClass() + "#" + invocation.getMethod().getName(),
                    callbackQueryHandlers.get(handlerName).getMethod().getDeclaringClass().getCanonicalName() + "#" + invocation.getMethod().getName()));
    }

    List<MessageHandlerInvocation> getMessageHandlerByRegex(String message) {
        List<MessageHandlerInvocation> messageHandlerInvocations = new ArrayList<>();
        for (Pattern pattern : messageHandlers.keySet()) {
            List<String> variables = new ArrayList<>();
            Matcher matcher = pattern.matcher(message);
            if (matcher.matches()) {
                for (int i = 0; i < matcher.groupCount(); i++) {
                    variables.add(matcher.group(i));
                }
                messageHandlerInvocations.add(new MessageHandlerInvocation(messageHandlers.get(pattern),
                        new InvocationParameters(variables, message)));
            }
        }
        return messageHandlerInvocations;
    }

    void addMessageHandler(HandlerInvocation invocation, String handlerName) throws BotInitializingException {
        Pattern pattern = Pattern.compile(handlerName);
        if (!messageHandlers.containsKey(pattern)) {
            messageHandlers.put(pattern, invocation);
        } else
            throw new BotInitializingException(String.format("CallbackQuery Handler %s is duplicated in methods [%s, %s]", pattern,
                    invocation.getMethod().getDeclaringClass() + "#" + invocation.getMethod().getName(),
                    messageHandlers.get(pattern).getMethod().getDeclaringClass().getCanonicalName() + "#" + invocation.getMethod().getName()));
    }

}
