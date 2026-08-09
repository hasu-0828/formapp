package com.example.formapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Question {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String questionText;

private String questionType;

@ManyToOne
@JoinColumn(name = "form_id")
private Form form;

@ManyToOne
@JoinColumn(name = "next_question_id")
private Question nextQuestion;

/*
 * フォーム終了フラグ
 *
 * true  → この質問の後で回答送信
 * false → 通常の次の質問へ進む
 */
private boolean required;

private Integer questionOrder;


public Question() {
}


public Long getId() {
    return id;
}


public String getQuestionText() {
    return questionText;
}


public void setQuestionText(String questionText) {
    this.questionText = questionText;
}


public String getQuestionType() {
    return questionType;
}


public void setQuestionType(String questionType) {
    this.questionType = questionType;
}


public Form getForm() {
    return form;
}


public void setForm(Form form) {
    this.form = form;
}


public Question getNextQuestion() {
    return nextQuestion;
}


public void setNextQuestion(Question nextQuestion) {
    this.nextQuestion = nextQuestion;
}


public boolean isRequired() {
    return required;
}


public void setRequired(boolean required) {
    this.required = required;
}


public Integer getQuestionOrder() {
    return questionOrder;
}


public void setQuestionOrder(Integer questionOrder) {
    this.questionOrder = questionOrder;
}

}
