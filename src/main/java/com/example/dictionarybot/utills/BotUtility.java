package com.example.dictionarybot.utills;

import com.vdurmont.emoji.EmojiParser;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class BotUtility {


    @Value("${app.telegrambot.token}")
    private String botToken;
    private final String BASE_URL = "https://api.telegram.org/bot";

    private RestTemplate restTemplate;

    @Autowired
    public BotUtility(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendMessage(SendMessage sendMessage) {
        restTemplate.postForObject(BASE_URL + botToken + "/sendMessage", sendMessage, Void.class);
    }


    public void answerCallbackQuery(AnswerCallbackQuery answerCallbackQuery) {
        restTemplate.postForObject(BASE_URL + botToken + "/answerCallbackQuery", answerCallbackQuery, Void.class);
    }


    @SneakyThrows
    public void sendMessage(SendMessage sendMessage, long chat_id, long message_id) {
        try {
            restTemplate.getForObject((BASE_URL + botToken + "/deleteMessage?chat_id=" + chat_id + "&message_id=" + message_id), Void.class);
        } catch (Exception ignored) {
        }
        sendMessage.setChatId(String.valueOf(chat_id));
        sendMessage(sendMessage);
    }

    @SneakyThrows
    public void sendMessage(SendMessage sendMessage, Long id) {
        sendMessage.setChatId(String.valueOf(id));
        sendMessage(sendMessage);
    }

    @SneakyThrows
    public void sendMessage(String message, Long id) {
        SendMessage sendMessage = new SendMessage(String.valueOf(id), message);
        sendMessage(sendMessage);
    }

//    public void sendPhoto(SendPhoto photo, long chat_id, long message_id) {
//        try {
//            restTemplate.getForObject((BASE_URL + botToken + "/deleteMessage?chat_id=" + chat_id + "&message_id=" + message_id), Void.class);
//        } catch (Exception ignored) {
//        }
//        sendPhoto(photo);
//    }
//
//    public void sendPhoto(SendPhoto photo) {
//        try {
//            restTemplate.postForObject(BASE_URL + botToken + "/sendPhoto", photo, Void.class);
//        } catch (Exception ignored) {
//        }
//    }

    public InlineKeyboardMarkup buildInlineKeyboard(String[][] arr) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (String[] row : arr) {
            List<InlineKeyboardButton> buttonRow = new ArrayList<>();
            for (String buttonString : row) {
                if (buttonString != null) {
                    InlineKeyboardButton button = new InlineKeyboardButton(EmojiParser.parseToUnicode(buttonString));
                    button.setCallbackData(buttonString);
                    buttonRow.add(button);
                }
            }
            keyboard.add(buttonRow);
        }

        markup.setKeyboard(keyboard);
        return markup;
    }
    public ReplyKeyboardMarkup buildKeyboardButtons(String[][] arr) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        for (String[] row : arr) {
            KeyboardRow buttonRow = new KeyboardRow();
            for (String buttonString : row) {
                if (buttonString != null)
                    buttonRow.add(EmojiParser.parseToUnicode(buttonString));
                }
            keyboard.add(buttonRow);
        }
        markup.setOneTimeKeyboard(false);
        markup.setResizeKeyboard(true);
        markup.setKeyboard(keyboard);
        return markup;
    }
}