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
    private String courseID;
    private  long startDate;
    private long endDate;

    private ArrayList<Object> questions;
    //private ArrayList<Integer> maxGrades;

    public Test() {
    }

    public Test(String id, String title, String courseID, long startDate, long endDate, ArrayList questions) {
        this.id = id;
        this.title = title;
        this.courseID = courseID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.questions = questions;

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

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public ArrayList<Object> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Object> questions) {
        this.questions = questions;
    }

//    public ArrayList<Integer> getMaxGrades() {
//        return maxGrades;
//    }
//
//    public void setMaxGrades(ArrayList<Integer> maxGrades) {
//        this.maxGrades = maxGrades;
//    }
}
