package com.example.dictionarybot.service;

import com.example.dictionarybot.entity.Book;
import com.example.dictionarybot.entity.Unit;
import com.example.dictionarybot.entity.Vocabulary;
import com.example.dictionarybot.repositories.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public boolean save(String name) {
        Book book = bookRepository.findByName(name).orElse(null);
        if (book != null) return false;
        book = new Book();
        book.setName(name);
        bookRepository.save(book);
        return true;
    }

    public List<Book> getAll() {
        return bookRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public Book getByName(String name){
        return bookRepository.findByName(name.trim()).orElse(null);
    }

    public boolean delete(String name){
        try {
            Book book = getByName(name);
            if (book == null)
                return false;
            bookRepository.delete(book);
            return true;
        }catch (Exception ignored){
            return false;
        }
    }
}
