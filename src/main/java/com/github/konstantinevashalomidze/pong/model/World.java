package com.github.konstantinevashalomidze.pong.model;

import java.util.List;
import java.util.Set;

import static com.github.konstantinevashalomidze.pong.model.ActionEvents.*;

public record World(Playfield playfield, Ball ball, Paddle left, Paddle right, int leftScore, int rightScore, Energy leftEnergy, Energy rightEnergy, Hand leftHand, Hand rightHand, List<Effect> leftEffects, List<Effect> rightEffects) {
    public World {
        if (leftScore < 0) {
            throw new WorldArgumentException("Left score cannot be negative");
        }

        if (rightScore < 0) {
            throw new  WorldArgumentException("Right score cannot be negative");
        }

        leftEffects = List.copyOf(leftEffects);
        rightEffects = List.copyOf(rightEffects);
    }

    public World step(double dt, Set<String> actionKeys, Set<String> actionPressedKeys) {

        Paddle left = null;
        // Paddle movement
        if (actionKeys.contains(W_DOWN) && !actionKeys.contains(S_DOWN)) {
            left = this.left.moveBy(-300 * dt);
        }

        if (actionKeys.contains(S_DOWN) && !actionKeys.contains(W_DOWN)) {
            left = this.left.moveBy(300 * dt);
        }

        if (left == null) {
            left = this.left;
        }

        left = left.clampTo(playfield);

        Paddle right = null;
        if (actionKeys.contains(UP_ARROW_DOWN) && !actionKeys.contains(DOWN_ARROW_DOWN)) {
            right = this.right.moveBy(-300 * dt);
        }

        if (actionKeys.contains(DOWN_ARROW_DOWN) && !actionKeys.contains(UP_ARROW_DOWN)) {
            right = this.right.moveBy(300 * dt);
        }

        if (right == null) {
            right = this.right;
        }

        right = right.clampTo(playfield);


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

        // Bounce ball off the paddles
        if (ball.overlaps(left) && ball.velocity().x() < 0
                || ball.overlaps(right) && ball.velocity().x() > 0) {
            ball = new Ball(
                ball.position(),
                new Vec2((-1) * ball.velocity().x(), ball.velocity().y()),
                ball.radius()
            );
        }

        // Energy management
        Energy leftEnergy = this.leftEnergy.refill(dt);
        Energy rightEnergy = this.rightEnergy.refill(dt);

        // Card usages
        Hand leftHand = null;
        if (actionPressedKeys.contains(F_DOWN)) {
            if (leftEnergy.canAfford(this.leftHand.hand().get(0).cost())) {
                leftHand = this.leftHand.play(0);
                leftEnergy = leftEnergy.spend(this.leftHand.hand().get(0).cost());
            }
        }

        if (actionPressedKeys.contains(G_DOWN)) {
            if (leftEnergy.canAfford(this.leftHand.hand().get(1).cost())) {
                leftHand = this.leftHand.play(1);
                leftEnergy = leftEnergy.spend(this.leftHand.hand().get(1).cost());
            }
        }

        if (actionPressedKeys.contains(H_DOWN)) {
            if (leftEnergy.canAfford(this.leftHand.hand().get(2).cost())) {
                leftHand = this.leftHand.play(2);
                leftEnergy = leftEnergy.spend(this.leftHand.hand().get(2).cost());
            }
        }

        if (actionPressedKeys.contains(J_DOWN)) {
            if (leftEnergy.canAfford(this.leftHand.hand().get(3).cost())) {
                leftHand = this.leftHand.play(3);
                leftEnergy = leftEnergy.spend(this.leftHand.hand().get(3).cost());
            }
        }

        leftHand = leftHand == null ? this.leftHand : leftHand;


        // Manage effects
        List<Effect> leftEffects = this.leftEffects.stream().map(e -> e.tick(dt)).filter(e -> !e.isExpired()).toList();
        List<Effect> rightEffects = this.rightEffects.stream().map(e -> e.tick(dt)).filter(e -> !e.isExpired()).toList();


        return new World(
            playfield,
            ball,
            left,
            right,
            leftScore,
            rightScore,
            leftEnergy,
            rightEnergy,
            leftHand,
            rightHand,
            leftEffects,
            rightEffects
        );
    }
}
