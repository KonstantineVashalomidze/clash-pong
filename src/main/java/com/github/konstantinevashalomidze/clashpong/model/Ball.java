package com.github.konstantinevashalomidze.clashpong.model;

import java.util.List;

public record Ball(Vec2 position, Vec2 velocity, double radius) {

    public Ball {
        if (!Double.isFinite(radius)) {
            throw new IllegalArgumentException("invalid radius");
        }

        if (radius <= 0) {
            throw new IllegalArgumentException("radius is less than or equal to 0");
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

    public Ball bounceOffBrick(Brick brick) {
        // Check from which side ball collided
        double xRange = Double.min(brick.center().x() + brick.width() / 2, position.x() + radius) - Double.max(brick.center().x() - brick.width() / 2, position.x() - radius);
        double yRange = Double.min(brick.center().y() + brick.height() / 2, position.y() + radius) - Double.max(brick.center().y() - brick.height() / 2, position.y() - radius);

        // X side collided
        if (xRange <= yRange) {
            return this.reverseX();
        } else { // Y side collided
            return this.reverseY();
        }
    }

    public boolean touchesWall(Playfield playfield) {
        return position.y() - radius < 0 || position.y() + radius > playfield.height();
    }

    public Ball speedUp(double factor, double maxSpeed) {
        Vec2 scaled = velocity.times(factor);
        double speed = Math.sqrt(scaled.x() * scaled.x() + scaled.y() * scaled.y());
        if (speed > maxSpeed) {
            scaled = scaled.times(maxSpeed / speed);
        }
        return new Ball(position, scaled, radius);
    }

    public boolean isOutRight(Playfield playfield) {
        return position.x() - radius > playfield.width();
    }

    public boolean isOutLeft(Playfield playfield) {
        return position.x() + radius < 0;
    }

    public boolean overlaps(Paddle paddle) {
        return Double.max(paddle.center().x() - paddle.width() / 2, position.x() - radius)
                <= Double.min(paddle.center().x() + paddle.width() / 2, position.x() + radius)
                && Double.max(paddle.center().y() - paddle.height() / 2, position.y() - radius)
                <= Double.min(paddle.center().y() + paddle.height() / 2, position.y() + radius);
    }

    public boolean overlaps(Brick brick) {
        return Double.max(brick.center().x() - brick.width() / 2, position.x() - radius)
                <= Double.min(brick.center().x() + brick.width() / 2, position.x() + radius)
                && Double.max(brick.center().y() - brick.height() / 2, position.y() - radius)
                <= Double.min(brick.center().y() + brick.height() / 2, position.y() + radius);
    }

    public Ball reverseX() {
        return new Ball(
                position,
                new Vec2((-1) * velocity.x(), velocity.y()),
                radius
        );
    }

    public Ball reverseY() {
        return new Ball(
            position,
            new Vec2(velocity.x(), (-1) * velocity.y()),
            radius
        );
    }

    public static Ball servedFrom(Playfield playfield, double velocityX, double velocityY, double radius) {
        return new  Ball(
                new Vec2(playfield.width() / 2, playfield.height() / 2),
                new Vec2(velocityX, velocityY),
                radius
        );
    }

    public Ball bounceOffFirstOverlapping(List<Brick> bricks) {
        for (var brick : bricks) {
            if (overlaps(brick)) {
                return bounceOffBrick(brick);
            }
        }
        return this;
    }

}
