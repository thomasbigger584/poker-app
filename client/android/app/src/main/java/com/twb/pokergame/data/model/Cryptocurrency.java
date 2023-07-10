package com.twb.pokergame.data.model;

public class Cryptocurrency {
    private final String name;
    private final String image;

    public Cryptocurrency(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
