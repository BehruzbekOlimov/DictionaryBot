package com.example.dictionarybot.service;

import com.example.dictionarybot.entity.Book;
import com.example.dictionarybot.entity.Unit;
import com.example.dictionarybot.entity.Vocabulary;
import com.example.dictionarybot.repositories.VocabularyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
        return vocabularyRepository.getRandomByUnit(selectedUnit.getId());
    }
    public Vocabulary getRandomWordByBook(Book book) {
        return vocabularyRepository.getRandomByBook(book.getId());
    }

    public boolean delete(Unit unit, String eng){
        try {
            Vocabulary vocabulary = vocabularyRepository.findByUnitAndEng(unit, eng).orElse(null);
            if (vocabulary == null)
                return false;
            vocabularyRepository.delete(vocabulary);
            return true;
        }catch (Exception ignored){
            return false;
        }
    }

    public Vocabulary findByUnit(Unit unit, String eng){
        return vocabularyRepository.findByUnitAndEng(unit, eng).orElse(null);
    }

    public Vocabulary findByBook(Book book, String eng){
        return vocabularyRepository.findByBookAndEng(book.getId(), eng);
    }
}
