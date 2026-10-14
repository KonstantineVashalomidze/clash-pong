package com.github.konstantinevashalomidze.pong.model;

public record Brick(
        Vec2 center,
        double width,
        double height,
        double remainingSeconds
) {

    public Brick {
        if (remainingSeconds < 0) {
            throw new BrickArgumentException("Invalid remaining seconds");
        }

        if (width <= 0 || height <= 0) {
            throw new BrickArgumentException("Invalid brick width or height");
        }

        if (Double.isNaN(width)
            || Double.isInfinite(width)
            || Double.isNaN(height)
            || Double.isInfinite(height)
            || Double.isNaN(remainingSeconds)
            || Double.isInfinite(remainingSeconds)
        ) throw new BrickArgumentException("Invalid brick argument");
    }

    public double topLeftX() {
        return center.x() - width / 2;
    }

    public double topLeftY() {
        return center.y() - height / 2;
    }

}
