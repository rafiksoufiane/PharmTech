package com.bhcc.app.pharmtech.data.model;


import java.io.Serializable;

public class Note implements Serializable {
    String genericName;
    String note;



    // constructors
    public Note() {
    }

    public Note(String genericName, String note) {
        this.genericName = genericName;
        this.note = note;

    }

    // getters
    public String getGenericName() {
        return genericName;
    }

    public String getNote() {
        return note;
    }



    // setters
    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public void setNote(String note) {
        this.note = note;
    }


}
