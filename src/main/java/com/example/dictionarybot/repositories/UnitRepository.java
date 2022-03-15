package com.example.dictionarybot.repositories;

import com.example.dictionarybot.entity.Book;
import com.example.dictionarybot.entity.Unit;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> getAllByBook(Book book);
    Optional<Unit> findByBookAndName(Book book, String name);
}