package com.example.alnik.examquiz.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alnik on 02-Mar-18.
 */

public class Test {

    private String id;
    private String title;
    private Date startDate;
    private Date endDate;
    private boolean active;

    private ArrayList<Object> questions;

    public Test() {
    }

    public Test(String id, String title, Date startDate, Date endDate, ArrayList questions) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.questions = questions;

        if(startDate == null){
            startDate = new Date();
            Log.d("test", startDate.toString());
        }

        if(endDate == null){
            startDate = new Date();
        }

        if (startDate.before(endDate)){

            active = true;
        } else {
            active = false;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ArrayList<Object> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Object> questions) {
        this.questions = questions;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
