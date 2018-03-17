package com.example.alnik.examquiz.models;

/**
 * Created by alnik on 09-Mar-18.
 */

public class Announcement {

    String id;
    String title;
    String body;
    long time;


    public Announcement() {
    }

    public Announcement(String id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
        time = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTime() {
        return time;
    }
}
