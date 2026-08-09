package com.example.formapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Choice {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String choiceText;

@ManyToOne
@JoinColumn(name = "question_id")
private Question question;

@ManyToOne
@JoinColumn(name = "next_question_id")
private Question nextQuestion;

/*
 * フォーム終了フラグ
 *
 * true  → この選択肢を選んだら回答送信
 * false → 次の質問へ進む
 */
public Choice() {
}


public Long getId() {
    return id;
}


public String getChoiceText() {
    return choiceText;
}


public void setChoiceText(String choiceText) {
    this.choiceText = choiceText;
}


public Question getQuestion() {
    return question;
}


public void setQuestion(Question question) {
    this.question = question;
}


public Question getNextQuestion() {
    return nextQuestion;
}


public void setNextQuestion(Question nextQuestion) {
    this.nextQuestion = nextQuestion;
}


}
