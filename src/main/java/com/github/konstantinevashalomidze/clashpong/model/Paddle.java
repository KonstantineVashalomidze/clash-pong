package com.github.konstantinevashalomidze.clashpong.model;

public record Paddle(Vec2 center, double width, double height) {

    public Paddle {

        if (!Double.isFinite(width)) {
            throw new IllegalArgumentException("width is Infinite");
        }

        if (!Double.isFinite(height)) {
            throw new IllegalArgumentException("height is Infinite");
        }

        if (width <= 0) {
            throw new IllegalArgumentException("width is 0 or less");
        }

        if (height <= 0) {
            throw new IllegalArgumentException("height is 0 or less");
        }

    }

    public Paddle moveByY(double dy) {
        return new Paddle(center.plus(new Vec2(0, dy)), width, height);
    }

    public Paddle moveByX(double dx) {
        return new Paddle(center.plus(new Vec2(dx, 0)), width, height);
    }

    public Paddle clampTo(Playfield playfield) {
        if (center.y() < height / 2) {
            return new Paddle(
                    new Vec2(center.x(), height / 2),
                    width,
                    height
                    );
        }

        if (center.y() > playfield.height() - height / 2) {
            return new Paddle(
                    new Vec2(center.x(), playfield.height() - height / 2),
                    width,
                    height
            );
        }

        return this;
    }

    public Paddle clampXTo(double minX, double maxX) {
        if (center.x() < minX) {
            return new Paddle(
                    new Vec2(minX, center.y()),
                    width,
                    height
            );
        }

        if (center.x() > maxX) {
            return new Paddle(
                new Vec2(maxX, center.y()),
                width,
                height
            );
        }
        return this;
    }


    public Paddle scaleHeight(double factor) {
        return new Paddle(center, width, height * factor);
    }


}
