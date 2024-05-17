package com.razzzil.telegram.processor;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
class BotMethodsHolder {

    private static Map<String, BotMethods> botMethodsMap = new HashMap<>();

    void addBotMethods(String botName, BotMethods botMethods) {
        botMethodsMap.put(botName, botMethods);
    }

    BotMethods getBotMethods(String botName) {
        return botMethodsMap.get(botName);
    }

    BotMethods createBotMethods(String botName) {
        BotMethods botMethods = new BotMethods();
        botMethodsMap.put(botName, botMethods);
        return botMethods;
    }






}
