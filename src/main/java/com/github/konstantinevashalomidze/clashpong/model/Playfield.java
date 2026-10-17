package com.github.konstantinevashalomidze.clashpong.model;

public record Playfield(double width, double height) {
    public Playfield {
        if (Double.isNaN(width)) {
            throw new PlayfieldArgumentException("width is NaN");
        }

        if (Double.isNaN(height)) {
            throw new PlayfieldArgumentException("height is NaN");
        }

        if (Double.isInfinite(width)) {
            throw new PlayfieldArgumentException("width is Infinite");
        }

        if (Double.isInfinite(height)) {
            throw new PlayfieldArgumentException("height is Infinite");
        }

        if (width <= 0) {
            throw new PlayfieldArgumentException("width is 0 or less");
        }

        if (height <= 0) {
            throw new PlayfieldArgumentException("height is 0 or less");
        }
    }
}
