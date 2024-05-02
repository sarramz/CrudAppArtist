package com.example.demoapp;

public class Artist {
    private String name;
    private String biography;
    private String imageUrl;

    private int id;

    public Artist(int id, String name, String biography, String imageUrl) {
        this.id = id;
        this.name = name;
        this.biography = biography;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Add this method in the Artist class

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
