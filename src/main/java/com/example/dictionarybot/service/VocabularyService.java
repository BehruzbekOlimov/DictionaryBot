package com.example.dictionarybot.service;

import com.example.dictionarybot.entity.Book;
import com.example.dictionarybot.entity.Unit;
import com.example.dictionarybot.entity.Vocabulary;
import com.example.dictionarybot.repositories.VocabularyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VocabularyService {
    private final VocabularyRepository vocabularyRepository;

    public boolean save(String en, String uz, Unit unit) {
        try {
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setEng(en.trim());
            vocabulary.setUzb(uz.trim());
            vocabulary.setUnit(unit);
            vocabularyRepository.save(vocabulary);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public Vocabulary getRandomWord(Unit selectedUnit) {
        List<Vocabulary> vocabularyList = vocabularyRepository.getAllByUnit(selectedUnit.getId());
        return vocabularyList.get((int) Math.round(Math.random() * vocabularyList.size()));
    }

    public Vocabulary getRandomWordByBook(Book book) {
        List<Vocabulary> vocabularyList = vocabularyRepository.getAllByBook(book.getId());
        return vocabularyList.get((int) Math.round(Math.random() * vocabularyList.size()));
    }

    public boolean delete(Unit unit, String eng) {
        try {
            Vocabulary vocabulary = vocabularyRepository.findByUnitAndEng(unit, eng).orElse(null);
            if (vocabulary == null)
                return false;
            vocabularyRepository.delete(vocabulary);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public Vocabulary findByUnit(Unit unit, String eng) {
        return vocabularyRepository.findByUnitAndEng(unit, eng).orElse(null);
    }

    public Vocabulary findByBook(Book book, String eng) {
        return vocabularyRepository.findByBookAndEng(book.getId(), eng);
    }
}
