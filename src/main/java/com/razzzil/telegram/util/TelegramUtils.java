package com.razzzil.telegram.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.razzzil.telegram.dto.CallbackDataDto;
import com.razzzil.telegram.dto.TelegramButtonDto;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class TelegramUtils {

    private static final ObjectMapper OM = new ObjectMapper();

    public SendMessage generateMessage(String text, Long chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public ReplyKeyboardMarkup createReplyKeyboard(String... strings){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        for (String string : strings) {
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText(string);
            keyboardRow.add(keyboardButton);
        }
        keyboardRowList.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        return replyKeyboardMarkup;
    }

    @SneakyThrows
    public InlineKeyboardMarkup createInlineKeyboard4(List<TelegramButtonDto> data, String callbackName){
        return createInlineKeyboard(4, data, callbackName);
    }

    public InlineKeyboardMarkup createInlineKeyboard3(List<TelegramButtonDto> data, String callbackName){
        return createInlineKeyboard(3, data, callbackName);
    }

    public InlineKeyboardMarkup createInlineKeyboard2(List<TelegramButtonDto> data, String callbackName){
        return createInlineKeyboard(2, data, callbackName);
    }

    @SneakyThrows
    private InlineKeyboardMarkup createInlineKeyboard(int horizontalMax, List<TelegramButtonDto> data, String callbackName){
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (int i = 0; i < data.size(); ) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            for (int j = 0; j < horizontalMax && i < data.size(); j++) {
                TelegramButtonDto telegramButtonDto = data.get(i);
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(telegramButtonDto.getText());
                CallbackDataDto callbackDataDto = new CallbackDataDto(callbackName, telegramButtonDto.getData());
                inlineKeyboardButton.setCallbackData(OM.writeValueAsString(callbackDataDto));
                rowInline.add(inlineKeyboardButton);
                i++;
            }
            rowsInline.add(rowInline);
        }
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

}
