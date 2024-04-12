package com.razzzil.telegram.botmanagement;

import com.razzzil.telegram.PropertyConfiguration;
import com.razzzil.telegram.bot.Bot;
import com.razzzil.telegram.configdto.BotTokenDto;
import com.razzzil.telegram.exception.BotInitializingException;
import com.razzzil.telegram.exception.BotProcessingException;
import com.razzzil.telegram.processor.HandlerProcessor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@Scope("singleton")
public class BotsHolder {

    @AllArgsConstructor
    private static class BotInstance {
        @Getter
        private Bot bot;
        private TelegramBotsApi telegramBotsApi;
    }

    private static Map<String, BotInstance> bots = new HashMap<>();

    public BotsHolder(PropertyConfiguration pc, HandlerProcessor handlerProcessor) throws TelegramApiException {
        for (Map.Entry<String, BotTokenDto> entry : pc.getBots().entrySet()) {
            Bot bot = new Bot(entry.getKey(), entry.getValue(), handlerProcessor);
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
            addBot(bot, telegramBotsApi);
            log.info("Telegram Bot found: [{}]", entry.getKey());
        }
    }

    private void addBot(Bot bot, TelegramBotsApi telegramBotsApi) {
        bots.put(bot.getName(), new BotInstance(bot, telegramBotsApi));
    }

    public Set<String> getBotNames() {
        return bots.keySet();
    }

    public Bot getBot(String name) {
        return bots.get(name).getBot();
    }

    private static Bot getSingetonBot() {
        if (bots.size() == 1) {
            return bots.get(bots.keySet().iterator().next()).getBot();
        } else {
            throw new BotProcessingException("There is not Singleton bot");
        }
    }

    @Bean
    @ConditionalOnProperty(name = "razzzil.singleton-bot", havingValue = "true")
    public Bot bot() {
        return getSingetonBot();
    }


}
