package com.github.konstantinevashalomidze.pong.model;

import java.util.Set;

import static com.github.konstantinevashalomidze.pong.model.ActionEvents.W_DOWN;
import static com.github.konstantinevashalomidze.pong.model.ActionEvents.W_UP;

public record World(Playfield playfield, Ball ball, Paddle left, Paddle right, int leftScore, int rightScore) {
    public World {
        if (leftScore < 0) {
            throw new WorldArgumentException("Left score cannot be negative");
        }

        if (rightScore < 0) {
            throw new  WorldArgumentException("Right score cannot be negative");
        }
    }

    public World step(double dt, Set<String> actionKeys) {

        Paddle left = null;
        // Paddle movement
        if (actionKeys.contains(W_DOWN)) {
            left = this.left.moveBy(-300 * dt);
        }

        // Ball movement
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
            left != null ? left : this.left,
            right,
            leftScore,
            rightScore
        );
    }
}
