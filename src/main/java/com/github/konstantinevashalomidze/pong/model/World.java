package com.github.konstantinevashalomidze.pong.model;

public record World(Playfield playfield, Ball ball, Paddle left, Paddle right, int leftScore, int rightScore) {
    public World {
        if (leftScore < 0) {
            throw new WorldArgumentException("Left score cannot be negative");
        }

        if (rightScore < 0) {
            throw new  WorldArgumentException("Right score cannot be negative");
        }
    }

    public World step(double dt) {
        return new World(
            playfield,
            ball.advance(dt),
            left,
            right,
            leftScore,
            rightScore
        );
    }
}
