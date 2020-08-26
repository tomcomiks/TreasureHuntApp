package com.tommelani.treasurehunt.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class Treasure
 */
public class Treasure {

    private long id;
    private String title;
    private LatLng position;
    private int imageId;
    private Milestone milestone;


    public Milestone getMilestone() {
        return milestone;
    }

    public Treasure() {

    }

    public Treasure(String title, double latitude, double longitude, int imageId, Milestone milestone) {
        this.title = title;
        this.position = new LatLng(latitude, longitude);
        this.imageId = imageId;
        this.milestone = milestone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
