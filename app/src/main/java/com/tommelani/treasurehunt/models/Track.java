package com.tommelani.treasurehunt.models;

/**
 * Class Track
 */
public class Track {

    private long id; //Unique identifier
    private String title; //Title

    public Track() {

    }

    public Track(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
