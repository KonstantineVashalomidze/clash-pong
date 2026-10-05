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
        Ball ball = this.ball.advance(dt).bounceOffWalls(playfield);
        int rightScore = this.rightScore;
        if (ball.isOutLeft(playfield)) {
            rightScore += 1;
            ball = new Ball(
                    new Vec2(playfield.width() / 2, playfield.height() / 2),
                    new Vec2(200, 120),
                    ball().radius()
            );
        }

        int leftScore = this.leftScore;
        if (ball.isOutRight(playfield)) {
            leftScore += 1;
            ball = new Ball(
                    new Vec2(playfield.width() / 2, playfield.height() / 2),
                    new Vec2(-200, 120),
                    ball().radius()
            );
        }

        return new World(
            playfield,
            ball,
            left,
            right,
            leftScore,
            rightScore
        );
    }
}
