package com.github.konstantinevashalomidze.clashpong.model;

public record Brick(
        Vec2 center,
        double width,
        double height,
        double remainingSeconds
) {

    public Brick {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid brick width or height");
        }

        if (!Double.isFinite(width)
            || !Double.isFinite(height)
            || !Double.isFinite(remainingSeconds)
        ) throw new IllegalArgumentException("Invalid brick argument");
    }

    public double topLeftX() {
        return center.x() - width / 2;
    }

    public double topLeftY() {
        return center.y() - height / 2;
    }

    public Brick tick(double dt) {
        return new Brick(center, width, height, remainingSeconds - dt);
    }

    public boolean isExpired() {
        return remainingSeconds <= 0;
    }

}
