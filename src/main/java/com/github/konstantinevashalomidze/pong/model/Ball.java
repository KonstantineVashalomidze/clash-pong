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

    public Ball bounceOffWalls(Playfield playfield) {
        if (position.y() - radius < 0) {
            return new Ball(
                new Vec2(position.x(), position.y() + 2 * (radius - position.y())),
                new Vec2(velocity.x(), (-1) * velocity.y()),
                radius
            );
        }

        if (position.y() + radius > playfield.height()) {
            return new Ball(
                    new Vec2(position.x(),  position.y() - 2 * (position.y() + radius - playfield.height())),
                    new Vec2(velocity.x(), (-1) * velocity.y()),
                    radius
            );
        }
        return this;
    }

    public boolean isOutRight(Playfield playfield) {
        return position.x() - radius > playfield.width();
    }

    public boolean isOutLeft(Playfield playfield) {
        return position.x() + radius < 0;
    }

}
