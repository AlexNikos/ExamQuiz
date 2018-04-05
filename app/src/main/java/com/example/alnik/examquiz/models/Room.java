package com.example.alnik.examquiz.models;

public class Room {

    private String name;
    private String ownersID;
    private String ownersName;
    private String id;

    public Room() {
    }

    public Room(String name, String ownersID, String ownersName, String id) {
        this.name = name;
        this.ownersID = ownersID;
        this.ownersName = ownersName;
        this.id = id;

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

}
