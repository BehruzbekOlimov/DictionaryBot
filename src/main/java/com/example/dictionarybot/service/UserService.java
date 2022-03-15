package com.example.dictionarybot.service;

import com.example.dictionarybot.entity.User;
import com.example.dictionarybot.entity.enums.Menu;
import com.example.dictionarybot.entity.enums.Role;
import com.example.dictionarybot.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(Long chatId) {
        return userRepository.findUserByChatId(chatId).orElse(null);
    }

    public User save(org.telegram.telegrambots.meta.api.objects.User req) {
        User user = new User();
        user.setName(req.getFirstName() + (req.getLastName() != null ? " " + req.getLastName() : ""));
        if (req.getUserName() != null)
            user.setUsername(req.getUserName());
        user.setChatId(req.getId());
        user.setRole(Role.GUEST);
        user = userRepository.save(user);
        return user;
    }

    public User saveUser(User user){
        user = userRepository.save(user);
        if (user.getMenu().equals(Menu.MAIN)) {
            user.setSelectedBook(null);
            user.setSelectedUnit(null);
        }
        if (user.getMenu().equals(Menu.BOOK_VIEW))
            user.setSelectedUnit(null);
        return user;
    }

}
