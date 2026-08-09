package com.example.formapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.formapp.entity.Choice;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {

    List<Choice> findByQuestionId(Long questionId);

}