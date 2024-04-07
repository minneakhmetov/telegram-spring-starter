package com.razzzil.telegram.botmanagement;

import com.razzzil.telegram.bot.Bot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.HashMap;
import java.util.Map;


@UtilityClass
class BotsHolder {

    @AllArgsConstructor
    private static class BotInstance {
        @Getter
        private Bot bot;
        private TelegramBotsApi telegramBotsApi;
    }

    private static Map<String, BotInstance> bots = new HashMap<>();

    void addBot(Bot bot, TelegramBotsApi telegramBotsApi) {
        bots.put(bot.getName(), new BotInstance(bot, telegramBotsApi));
    }

    Bot getBot(String name) {
        return bots.get(name).getBot();
    }

}
