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
        Ball advancedAndBounced = ball.advance(dt).bounceOffWalls(playfield);
        return new World(
            playfield,
            advancedAndBounced,
            left,
            right,
            leftScore,
            rightScore
        );
    }
}
