package com.example.dictionarybot.service;

import com.example.dictionarybot.entity.Book;
import com.example.dictionarybot.entity.Unit;
import com.example.dictionarybot.entity.Vocabulary;
import com.example.dictionarybot.repositories.BookRepository;
import com.example.dictionarybot.repositories.UnitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;

    public boolean save(String name, Book book){
        Unit unit = unitRepository.findByBookAndName(book, name.trim()).orElse(null);
        if (unit!=null)
            return false;
        unit = new Unit();
        unit.setName(name);
        unit.setBook(book);
        unitRepository.save(unit);
        return true;
    }

    public List<Unit> getAllByBook(Book book){
        return unitRepository.getAllByBook(book);
    }

    public Unit getByNameAndBook(String name, Book book) {
        return unitRepository.findByBookAndName(book, name.trim()).orElse(null);
    }

    public boolean delete(Book book, String name){
        try {
            Unit unit = getByNameAndBook(name, book);
            if (unit == null)
                return false;
            unitRepository.delete(unit);
            return true;
        }catch (Exception ignored){
            return false;
        }
    }
}
