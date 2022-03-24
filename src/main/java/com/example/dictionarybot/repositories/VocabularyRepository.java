package com.example.dictionarybot.repositories;

import com.example.dictionarybot.entity.Unit;
import com.example.dictionarybot.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

    @Query(
            value = "select * from vocabularies where unit_id=?1 order by random() limit 1",
            nativeQuery = true
    )
    Vocabulary getRandomByUnit(Long id);

    @Query(
            value = "select * from vocabularies where unit_id=?1",
            nativeQuery = true
    )
    List<Vocabulary> getAllByUnit(Long id);

    @Query(
            value = "select * from vocabularies where unit_id in (select distinct id from units where book_id = ?1) order by random() limit 1",
            nativeQuery = true
    )
    Vocabulary getRandomByBook(Long id);

    @Query(
            value = "select * from vocabularies where unit_id in (select distinct id from units where book_id = ?1)",
            nativeQuery = true
    )
    List<Vocabulary> getAllByBook(Long id);

    @Query(
            value = "select * from vocabularies where unit_id in (select distinct id from units where book_id = ?1) and eng=?2 limit 1",
            nativeQuery = true
    )
    Vocabulary findByBookAndEng(Long id, String eng);

    Optional<Vocabulary> findByUnitAndEng(Unit unit, String eng);
}