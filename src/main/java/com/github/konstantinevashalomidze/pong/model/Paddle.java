package com.github.konstantinevashalomidze.pong.model;

public record Paddle(Vec2 center, double width, double height) {

    public Paddle {
        if (Double.isNaN(width)) {
            throw new PaddleArgumentException("width is NaN");
        }
        if (Double.isNaN(height)) {
            throw new PaddleArgumentException("height is NaN");
        }

        if (Double.isInfinite(width)) {
            throw new PaddleArgumentException("width is Infinite");
        }

        if (Double.isInfinite(height)) {
            throw new PaddleArgumentException("height is Infinite");
        }

        if (width <= 0) {
            throw new PaddleArgumentException("width is 0 or less");
        }

        if (height <= 0) {
            throw new PaddleArgumentException("height is 0 or less");
        }

    }

    public Paddle moveBy(double dy) {
        return new Paddle(center.plus(new Vec2(0, dy)), width, height);
    }
}
