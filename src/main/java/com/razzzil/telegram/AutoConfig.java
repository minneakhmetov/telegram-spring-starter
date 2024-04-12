package com.razzzil.telegram;

import com.razzzil.telegram.PropertyConfiguration;
import com.razzzil.telegram.bot.Bot;
import com.razzzil.telegram.configdto.BotTokenDto;
import com.razzzil.telegram.processor.HandlerProcessor;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;

@Slf4j
@Configuration
@ComponentScan
public class AutoConfig {



}
