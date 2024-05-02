package com.example.demoapp;

public class Artist {
    private String name;
    private String biography;
    private String imageUrl;

    public Artist(String name, String biography, String imageUrl) {
        this.name = name;
        this.biography = biography;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

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
