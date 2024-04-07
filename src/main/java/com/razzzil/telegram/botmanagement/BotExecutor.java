package com.razzzil.telegram.botmanagement;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@UtilityClass
public class BotExecutor {

    public TelegramLongPollingBot getBot(String botName) {
        return BotsHolder.getBot(botName);
    }
}
