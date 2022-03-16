package com.example.dictionarybot.controller;

import com.example.dictionarybot.entity.Book;
import com.example.dictionarybot.entity.Unit;
import com.example.dictionarybot.entity.User;
import com.example.dictionarybot.entity.Vocabulary;
import com.example.dictionarybot.entity.enums.Menu;
import com.example.dictionarybot.entity.enums.Role;
import com.example.dictionarybot.service.BookService;
import com.example.dictionarybot.service.UnitService;
import com.example.dictionarybot.service.UserService;
import com.example.dictionarybot.service.VocabularyService;
import com.example.dictionarybot.utills.BotUtility;
import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/telegram")
@Slf4j
public class BotController {

    private final BotUtility botUtility;
    private final UserService userService;
    private final BookService bookService;
    private final UnitService unitService;
    private final VocabularyService vocabularyService;

    @PostMapping("/webhook")
    public void getUpdate(@RequestBody Update update) {
        try {
            if (update.hasMessage()) {
                if (update.getMessage().hasText()) {
                    String text = update.getMessage().getText().trim();
                    User user = userService.getUser(update.getMessage().getChatId());

                    if (text.equals("/start") || text.equals("/menu") || text.equals("⏫ Bosh sahifa")) {
                        if (user == null) {
                            user = userService.save(update.getMessage().getFrom());
                            botUtility.sendMessage("Botga xush kelibsiz!", user.getChatId());
                        }
                        user.setSelectedUnit(null);
                        user.setMenu(Menu.MAIN);
                        userService.saveUser(user);
                        sendBooksMenu(user);
                    } else if (user != null) {
                        if (text.startsWith("/create") && user.getRole().equals(Role.ADMIN) && user.getMenu().equals(Menu.MAIN)) {
                            text = text.replace("/create", "").trim();
                            if (bookService.save(text)) {
                                botUtility.sendMessage("Kitob saqlandi!", user.getChatId());
                            } else {
                                botUtility.sendMessage("Bunday kitob mavjud!", user.getChatId());
                            }

                        } else if (text.startsWith("/create") && user.getRole().equals(Role.ADMIN) && user.getMenu().equals(Menu.BOOK_VIEW)) {
                            text = text.replace("/create", "").trim();
                            if (unitService.save(text, user.getSelectedBook())) {
                                botUtility.sendMessage("Unit saqlandi!", user.getChatId());
                            } else {
                                botUtility.sendMessage("Bunday unit mavjud!", user.getChatId());
                            }

                        } else if ((text.startsWith("/create") || text.startsWith("#create")) && user.getRole().equals(Role.ADMIN) && user.getMenu().equals(Menu.UNIT_TEST)) {
                            text = text.replace("/create", "").trim();
                            text = text.replace("#create", "").trim();
                            String[] engUzb = text.split("\n");
                            if (vocabularyService.save(engUzb[0], engUzb[1], user.getSelectedUnit())) {
                                botUtility.sendMessage("Lug'at saqlandi!", user.getChatId());
                            } else {
                                botUtility.sendMessage("Lug'at saqlanmadi!", user.getChatId());
                            }

                        } else if (text.startsWith("/delete") && user.getRole().equals(Role.ADMIN) && user.getMenu().equals(Menu.MAIN)) {
                            text = text.replace("/delete", "").trim();
                            if (bookService.delete(text)) {
                                botUtility.sendMessage("Kitob o'chirildi!", user.getChatId());
                            } else {
                                botUtility.sendMessage("Kitob o'chirilmadi!", user.getChatId());
                            }

                        } else if (text.startsWith("/delete") && user.getRole().equals(Role.ADMIN) && user.getMenu().equals(Menu.BOOK_VIEW)) {
                            text = text.replace("/delete", "").trim();
                            if (unitService.delete(user.getSelectedBook(), text)) {
                                botUtility.sendMessage("Unit o'chirildi!", user.getChatId());
                            } else {
                                botUtility.sendMessage("Unit o'chirilmadi!", user.getChatId());
                            }

                        } else if (text.startsWith("/delete") && user.getRole().equals(Role.ADMIN) && user.getMenu().equals(Menu.UNIT_TEST)) {
                            text = text.replace("/delete", "").trim();
                            if (vocabularyService.delete(user.getSelectedUnit(), text)) {
                                botUtility.sendMessage("Lug'at o'chirildi!", user.getChatId());
                            } else {
                                botUtility.sendMessage("Lug'at o'chirilmadi!", user.getChatId());
                            }

                        } else {
                            switch (user.getMenu()) {
                                case MAIN:
                                    Book book = bookService.getByName(text);
                                    if (book == null) {
                                        botUtility.sendMessage("Bunday kitob mavjud emas!", user.getChatId());
                                        break;
                                    }
                                    user.setSelectedBook(book);
                                    user.setMenu(Menu.BOOK_VIEW);
                                    user = userService.saveUser(user);
                                    sendUnitsMenu(user);
                                    break;
                                case BOOK_VIEW:
                                    if (text.equals("Random word")) {
                                        Vocabulary vocabulary = vocabularyService.getRandomWordByBook(user.getSelectedBook());
                                        sendInlineQuestion(user, vocabulary, null);
                                        break;
                                    }
                                    Unit unit = unitService.getByNameAndBook(text, user.getSelectedBook());
                                    if (unit == null) {
                                        botUtility.sendMessage("Bunday unit mavjud emas!", user.getChatId());
                                        break;
                                    }
                                    user.setSelectedUnit(unit);
                                    user.setMenu(Menu.UNIT_TEST);
                                    userService.saveUser(user);
                                    sendUnitTestMenu(user);
                                    break;
                                case UNIT_TEST:

                            }
                        }
                    }
                }

            }
            if (update.hasCallbackQuery()) {
                User user = userService.getUser(update.getCallbackQuery().getFrom().getId());
                if (update.getCallbackQuery().getMessage().getText().equals("Random word")) {
                    Vocabulary vocabulary = user.getSelectedUnit() == null ?
                            vocabularyService.getRandomWordByBook(user.getSelectedBook()) :
                            vocabularyService.getRandomWord(user.getSelectedUnit());
                    sendInlineQuestion(user, vocabulary, Long.valueOf(update.getCallbackQuery().getMessage().getMessageId()));
                } else if (update.getCallbackQuery().getMessage().getText().equals("Javobini ko'rish")) {
                    String text = update.getCallbackQuery().getData().trim();
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.getCallbackQuery().getId());
                    answerCallbackQuery.setText(text);
                    answerCallbackQuery.setShowAlert(true);
                    botUtility.answerCallbackQuery(answerCallbackQuery);
                }

            }
        } catch (
                Exception e) {
            log.error(e.getMessage());
        }

    }

    private void sendBooksMenu(User user) {
        List<Book> bookList = bookService.getAll();
        String[][] books = new String[(bookList.size() + 1) / 2][2];
        for (int i = 0; i < bookList.size(); i++) {
            books[i / 2][i % 2] = bookList.get(i).getName();
        }
        SendMessage sendMessage = new SendMessage(String.valueOf(user.getChatId()), "Kitobni tanlang");
        sendMessage.setReplyMarkup(botUtility.buildKeyboardButtons(books));
        botUtility.sendMessage(sendMessage);
    }

    private void sendUnitsMenu(User user) {
        List<Unit> unitList = unitService.getAllByBook(user.getSelectedBook());
        String[][] units = new String[(unitList.size() + 5) / 2][2];
        units[(unitList.size() + 1) / 2][0] = "Random word";
        units[(unitList.size() + 3) / 2][0] = "⏫ Bosh sahifa";
        for (int i = 0; i < unitList.size(); i++) {
            units[i / 2][i % 2] = unitList.get(i).getName();
        }
        SendMessage sendMessage = new SendMessage(String.valueOf(user.getChatId()), "Kerakli darsni tanlang");
        sendMessage.setReplyMarkup(botUtility.buildKeyboardButtons(units));
        botUtility.sendMessage(sendMessage);
    }

    private void sendUnitTestMenu(User user) {
        String[][] units = {{"⏫ Bosh sahifa"}};
        SendMessage sendMessage = new SendMessage(String.valueOf(user.getChatId()), "Kerakli bo'limni tanlang");
        sendMessage.setReplyMarkup(botUtility.buildKeyboardButtons(units));
        botUtility.sendMessage(sendMessage);
        Vocabulary vocabulary = vocabularyService.getRandomWord(user.getSelectedUnit());
        sendInlineQuestion(user, vocabulary, null);
    }

    private void sendInlineQuestion(User user, Vocabulary vocabulary, Long messageId) {
        boolean isUzb = Math.random() > 0.33;
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttonRow = new ArrayList<>();
        List<InlineKeyboardButton> buttonRow2 = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("Javobini ko'rish");
        InlineKeyboardButton button2 = new InlineKeyboardButton("Random word");
        button.setCallbackData(isUzb ? vocabulary.getEng().substring(0, 1).toUpperCase() + vocabulary.getEng().substring(1) :
                vocabulary.getUzb().substring(0, 1).toUpperCase() + vocabulary.getUzb().substring(1));
        buttonRow.add(button);
        buttonRow2.add(button2);
        keyboard.add(buttonRow);
        keyboard.add(buttonRow2);
        markup.setKeyboard(keyboard);
        SendMessage sendInlineMessage = new SendMessage(String.valueOf(user.getChatId()), !isUzb ? vocabulary.getEng().substring(0, 1).toUpperCase() + vocabulary.getEng().substring(1) :
                vocabulary.getUzb().substring(0, 1).toUpperCase() + vocabulary.getUzb().substring(1));
        sendInlineMessage.setReplyMarkup(markup);
        if (messageId != null)
            botUtility.sendMessage(sendInlineMessage, user.getChatId(), messageId);
        else
            botUtility.sendMessage(sendInlineMessage);
    }
}
