package com.github.konstantinevashalomidze.pong.model;

public record Ball(Vec2 position, Vec2 velocity, double radius) {

    public Ball {
        if (Double.isNaN(radius)) {
            throw new BallArgumentException("radius is NaN");
        }

        if (Double.isInfinite(radius)) {
            throw new BallArgumentException("radius is infinite");
        }

        if (radius <= 0) {
            throw new BallArgumentException("radius is less than or equal to 0");
        }
    }

    public Ball advance(double dt) {
        return new Ball(position.plus(velocity.times(dt)), velocity, radius);
    }
}
