package com.example.formapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.formapp.entity.Answer;

public interface AnswerRepository
        extends JpaRepository<Answer, Long> {

    List<Answer> findByResponseId(Long responseId);

}