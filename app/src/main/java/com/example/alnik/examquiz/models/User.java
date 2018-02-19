package com.example.alnik.examquiz.models;

/**
 * Created by alnik on 04-Feb-18.
 */

public class User {

    private String type;
    private String id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String fullname;


    public User(String id, String name, String surname, String email, String password, String type) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.type = type;
        this.password = password;
        fullname = name +" " +surname;
    }

    public User() {
    }

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        fullname = name +" " +surname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
        fullname = name +" " +surname;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public String getPassword() {
        return password;
    }

    public String getFullname() {
        return fullname;
    }
}
