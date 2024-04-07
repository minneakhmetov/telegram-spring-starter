package com.razzzil.telegram.botmanagement;

import com.razzzil.telegram.PropertyConfiguration;
import com.razzzil.telegram.bot.Bot;
import com.razzzil.telegram.configdto.BotTokenDto;
import com.razzzil.telegram.processor.HandlerProcessor;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;

@Configuration
@AllArgsConstructor
@ComponentScan("com.razzzil.telegram")
public class AutoConfig {

    private final PropertyConfiguration pc;
    private final HandlerProcessor handlerProcessor;

    @PostConstruct
    public void init() throws TelegramApiException {
        for (Map.Entry<String, BotTokenDto> entry : pc.getBots().entrySet()) {
            Bot bot = new Bot(entry.getKey(), entry.getValue(), handlerProcessor);
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
            BotsHolder.addBot(bot, telegramBotsApi);
        }
    }

}
