package com.github.konstantinevashalomidze.pong.model;

public record Brick(
        Vec2 center,
        double width,
        double height
) {

    public Brick {
        if (width <= 0 || height <= 0) {
            throw new BrickArgumentException("Invalid brick width or height");
        }

        if (Double.isNaN(width)
            || Double.isInfinite(width)
            || Double.isNaN(height)
            || Double.isInfinite(height)
        ) throw new BrickArgumentException("Invalid brick argument");
    }



}
