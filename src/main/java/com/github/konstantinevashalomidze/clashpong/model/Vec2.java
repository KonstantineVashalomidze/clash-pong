package com.github.konstantinevashalomidze.clashpong.model;

public record Vec2(double x, double y) {

    public Vec2 {
        if (Double.isNaN(x)) {
            throw new Vec2ArgumentException("x is NaN");
        }

        if (Double.isNaN(y)) {
            throw new Vec2ArgumentException("y is NaN");
        }

        if (Double.isInfinite(x)) {
            throw new Vec2ArgumentException("x is infinite");
        }

        if (Double.isInfinite(y)) {
            throw new Vec2ArgumentException("y is infinite");
        }



    }

    public Vec2 plus(Vec2 other) {
        return new Vec2(x + other.x(), y + other.y());
    }

    public Vec2 times(double multiplier) {
        return new Vec2(x * multiplier, y * multiplier);
    }
}
