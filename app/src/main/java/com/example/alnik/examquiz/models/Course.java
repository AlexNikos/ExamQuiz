package com.example.alnik.examquiz.models;

/**
 * Created by alnik on 13-Feb-18.
 */

public class Course {

    private String name;
    private String ownersID;
    private String ownersName;
    private String id;
    private String info;
    private String site;

    public Course() {
    }

    public Course(String name, String ownersID, String ownersName, String id, String info, String site) {
        this.name = name;
        this.ownersID = ownersID;
        this.ownersName = ownersName;
        this.id = id;
        this.info = info;
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnersID() {
        return ownersID;
    }

    public void setOwnersID(String ownersID) {
        this.ownersID = ownersID;
    }

    public String getOwnersName() {
        return ownersName;
    }

    public void setOwnersName(String ownersName) {
        this.ownersName = ownersName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

}
