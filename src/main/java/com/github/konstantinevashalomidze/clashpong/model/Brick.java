package com.github.konstantinevashalomidze.clashpong.model;

public record Brick(
        Vec2 center,
        double width,
        double height,
        double remainingSeconds
) {

    public Brick {
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

    public Brick tick(double dt) {
        return new Brick(center, width, height, remainingSeconds - dt);
    }

    public boolean isExpired() {
        return remainingSeconds <= 0;
    }

}
