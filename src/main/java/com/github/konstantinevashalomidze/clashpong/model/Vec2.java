package com.github.konstantinevashalomidze.clashpong.model;

public record Vec2(double x, double y) {

    public Vec2 {
        if (!Double.isFinite(x)) {
            throw new IllegalArgumentException("x is infinite");
        }

        if (!Double.isFinite(y)) {
            throw new IllegalArgumentException("y is infinite");
        }
    }

    public Vec2 plus(Vec2 other) {
        return new Vec2(x + other.x(), y + other.y());
    }

    public Vec2 times(double multiplier) {
        return new Vec2(x * multiplier, y * multiplier);
    }
}
