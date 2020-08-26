package com.tommelani.treasurehunt.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class Milestone
 */
public class Milestone {

    private long id; //Unique identifier
    private String title; //Title
    private String description; //Description
    private Track track; //Related track
    private LatLng position; //Position on the map

    @Override
    public String toString() {
        return this.title;
    }

    public Milestone() {
    }

    public Milestone(String title, double latitude, double longitude, String description, Track track) {
        this.title = title;
        this.description = description;
        this.position = new LatLng(latitude, longitude);
        this.track = track;
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

    public void setTrack(Track track) {
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

