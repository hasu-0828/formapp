package com.example.formapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.formapp.entity.Question;

public interface QuestionRepository
        extends JpaRepository<Question, Long> {

    List<Question> findByFormIdOrderByQuestionOrderAsc(Long formId);

}