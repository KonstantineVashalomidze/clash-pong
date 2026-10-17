package com.github.konstantinevashalomidze.clashpong.model;

public record Playfield(double width, double height) {
    public Playfield {


        if (!Double.isFinite(width)) {
            throw new IllegalArgumentException("width is Infinite");
        }

        if (!Double.isFinite(height)) {
            throw new IllegalArgumentException("height is Infinite");
        }

        if (width <= 0) {
            throw new IllegalArgumentException("width is 0 or less");
        }

        if (height <= 0) {
            throw new IllegalArgumentException("height is 0 or less");
        }
    }
}
