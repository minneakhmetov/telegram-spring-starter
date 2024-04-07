package com.razzzil.telegram.bot;

import com.razzzil.telegram.configdto.BotTokenDto;
import com.razzzil.telegram.processor.HandlerProcessor;
import com.razzzil.telegram.util.TelegramUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.thymeleaf.context.Context;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    @Getter
    private String name;
    private BotTokenDto botTokenDto;
    private HandlerProcessor processor;

    public Bot(String name, BotTokenDto botTokenDto, HandlerProcessor processor) {
        super(botTokenDto.getToken());
        this.name = name;
        this.botTokenDto = botTokenDto;
        this.processor = processor;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            processor.invokeCallbackQuery(this, update);
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            processor.invokeMessage(this, update);
        }
    }

    @Override
    public String getBotUsername() {
        return botTokenDto.getUsername();
    }

    public Message sendMessage(String text, Long chatId) throws TelegramApiException {
        return execute(TelegramUtils.generateMessage(text, chatId));
    }

    public Message sendMessage(String templateName, Context ctx, Long chatId) throws TelegramApiException {
        return processor.sendMessage(this, templateName, ctx, chatId);
    }
}
