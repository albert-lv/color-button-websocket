package com.example.event;

public class ColorEvent {
    private final String color;

    public ColorEvent(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
