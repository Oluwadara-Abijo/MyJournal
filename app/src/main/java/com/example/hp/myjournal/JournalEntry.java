package com.example.hp.myjournal;

import java.io.Serializable;

public class JournalEntry implements Serializable{

    private String title;
    private String body;
    private String dateModified;
    private String key;

    private int id;

    public JournalEntry() {}

    public JournalEntry(String title, String body, String dateModified, String key) {
        this.title = title;
        this.body = body;
        this.dateModified = dateModified;
        this.key = key;
    }

    public String getKey() { return key; }

    public void setKey(String id) { this.key = id; }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public void setId(int id) {
        this.id = id;
    }
}
