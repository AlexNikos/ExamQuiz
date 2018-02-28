package com.example.alnik.examquiz.models;

/**
 * Created by alnik on 25-Feb-18.
 */

public class ShortAnswer {

    private String question;
    private String answer;
    private String id;
    private String type;

    public ShortAnswer() {
    }

    public ShortAnswer(String question, String id) {
        this.question = question;
        this.answer = "";
        this.id = id;
        type = "ShortAnswer";
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getId() {
        return id;
    }


    public String getType() {
        return type;
    }

}
