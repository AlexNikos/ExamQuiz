package com.example.alnik.examquiz.models;

/**
 * Created by alnik on 25-Feb-18.
 */

public class TrueFalse {

    private String question;
    private Boolean answer;
    private String id;
    private String type;

    public TrueFalse() {
    }

    public TrueFalse(String question, Boolean answer, String id) {
        this.question = question;
        this.answer = answer;
        this.id = id;
        type = "TrueFalse";
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

}