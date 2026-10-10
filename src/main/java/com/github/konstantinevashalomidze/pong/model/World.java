package com.github.konstantinevashalomidze.pong.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        // Paddle movement
        boolean leftIsFrozen = hasEffect(leftEffects, Card.FREEZE);
        Paddle left = null;
        if (actionKeys.contains(W_DOWN) && !actionKeys.contains(S_DOWN) && !leftIsFrozen) {
            left = this.left.moveBy(-300 * dt);
        }

        if (actionKeys.contains(S_DOWN) && !actionKeys.contains(W_DOWN) && !leftIsFrozen) {
            left = this.left.moveBy(300 * dt);
        }

        if (left == null) {
            left = this.left;
        }

        left = new Paddle(
                applyEffects(left, this.leftEffects).clampTo(playfield).center(),
                left.width(),
                left.height()
        );

        boolean rightIsFrozen = hasEffect(rightEffects, Card.FREEZE);
        Paddle right = null;
        if (actionKeys.contains(UP_ARROW_DOWN) && !actionKeys.contains(DOWN_ARROW_DOWN) && !rightIsFrozen) {
            right = this.right.moveBy(-300 * dt);
        }

        if (actionKeys.contains(DOWN_ARROW_DOWN) && !actionKeys.contains(UP_ARROW_DOWN) && !rightIsFrozen) {
            right = this.right.moveBy(300 * dt);
        }

        if (right == null) {
            right = this.right;
        }

        right = new Paddle(
                applyEffects(right, this.rightEffects).clampTo(playfield).center(),
                right.width(),
                right.height()
        );


        // Ball movement
        boolean leftShieldUsed = false;
        Ball ball = this.ball.advance(dt * ballTimeScale()).bounceOffWalls(playfield);
        int rightScore = this.rightScore;
        if (ball.isOutLeft(playfield)) {
            if (hasEffect(leftEffects, Card.GOAL_SHIELD)) {
                leftShieldUsed = true;
                ball = new Ball(
                        ball.position(),
                        new Vec2((-1) * ball.velocity().x(), ball.velocity().y()),
                        ball.radius()
                );
            } else {
                rightScore += 1;
                ball = new Ball(
                        new Vec2(playfield.width() / 2, playfield.height() / 2),
                        new Vec2(200, 120),
                        ball().radius()
                );
            }

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
        boolean leftSmashHit = false;
        boolean rightSmashHit = false;
        boolean wasLeftHit = ball.overlaps(effectiveLeft()) && ball.velocity().x() < 0;
        boolean wasRightHit = ball.overlaps(effectiveRight()) && ball.velocity().x() > 0;
        if (wasLeftHit || wasRightHit) {
            if (wasLeftHit
                    && leftEffects.stream().anyMatch(e -> e.card() == Card.SMASH && !e.active())) {
                leftSmashHit = true;
            }
            if (wasRightHit
                    && rightEffects.stream().anyMatch(e -> e.card() == Card.SMASH && !e.active())) {
                rightSmashHit = true;
            }
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
        Card leftPlayedCard = null;
        if (actionPressedKeys.contains(F_DOWN)) {
            if (leftEnergy.canAfford(this.leftHand.hand().get(0).cost())) {
                leftPlayedCard = this.leftHand.hand().get(0);
                leftHand = this.leftHand.play(0);
                leftEnergy = leftEnergy.spend(this.leftHand.hand().get(0).cost());
            }
        }

        if (actionPressedKeys.contains(G_DOWN)) {
            if (leftEnergy.canAfford(this.leftHand.hand().get(1).cost())) {
                leftPlayedCard = this.leftHand.hand().get(1);
                leftHand = this.leftHand.play(1);
                leftEnergy = leftEnergy.spend(this.leftHand.hand().get(1).cost());
            }
        }

        if (actionPressedKeys.contains(H_DOWN)) {
            if (leftEnergy.canAfford(this.leftHand.hand().get(2).cost())) {
                leftPlayedCard = this.leftHand.hand().get(2);
                leftHand = this.leftHand.play(2);
                leftEnergy = leftEnergy.spend(this.leftHand.hand().get(2).cost());
            }
        }

        if (actionPressedKeys.contains(J_DOWN)) {
            if (leftEnergy.canAfford(this.leftHand.hand().get(3).cost())) {
                leftPlayedCard = this.leftHand.hand().get(3);
                leftHand = this.leftHand.play(3);
                leftEnergy = leftEnergy.spend(this.leftHand.hand().get(3).cost());
            }
        }

        leftHand = leftHand == null ? this.leftHand : leftHand;

        // Manage effects
        List<Effect> leftEffects = this.leftEffects.stream().map(e -> e.tick(dt)).filter(e -> !e.isExpired()).collect(Collectors.toList());
        List<Effect> rightEffects = this.rightEffects.stream().map(e -> e.tick(dt)).filter(e -> !e.isExpired()).collect(Collectors.toList());

        if (leftSmashHit) {
            leftEffects.removeIf(e -> e.card() == Card.SMASH && !e.active());
            leftEffects.add(new Effect(Card.SMASH, Card.SMASH.duration(), true));
        }

        if (leftShieldUsed) {
            leftEffects.removeIf(e -> e.card() == Card.GOAL_SHIELD);
        }

        switch (leftPlayedCard) {
            case BARRICADE -> {
                leftEffects.removeIf(e -> e.card() == Card.BARRICADE);
                leftEffects.add(new Effect(Card.BARRICADE, Card.BARRICADE.duration(), true));
            }
            case BIG_PADDLE -> {
                leftEffects.removeIf(e -> e.card() == Card.BIG_PADDLE);
                leftEffects.add(new Effect(Card.BIG_PADDLE, Card.BIG_PADDLE.duration(), true));
            }
            case SHRINK -> {
                rightEffects.removeIf(e -> e.card() == Card.SHRINK);
                rightEffects.add(new Effect(Card.SHRINK, Card.SHRINK.duration(), true));
            }
            case FREEZE -> {
                rightEffects.removeIf(e -> e.card() == Card.FREEZE);
                rightEffects.add(new Effect(Card.FREEZE, Card.FREEZE.duration(), true));
            }
            case SMASH -> {
                leftEffects.removeIf(e -> e.card() == Card.SMASH);
                leftEffects.add(new Effect(Card.SMASH, Card.SMASH.duration(), false));
            }
            case SLOW_FIELD -> {
                leftEffects.removeIf(e -> e.card() == Card.SLOW_FIELD);
                leftEffects.add(new Effect(Card.SLOW_FIELD, Card.SLOW_FIELD.duration(), true));
            }
            case STRAFE -> {
                leftEffects.removeIf(e -> e.card() == Card.STRAFE);
                leftEffects.add(new Effect(Card.STRAFE, Card.STRAFE.duration(), true));
            }
            case GOAL_SHIELD -> {
                leftEffects.removeIf(e -> e.card() == Card.GOAL_SHIELD);
                leftEffects.add(new Effect(Card.GOAL_SHIELD, Card.GOAL_SHIELD.duration(), true));
            }
            case null, default -> {

            }
        }

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

    private boolean hasEffect(List<Effect> effects, Card card) {
        return effects.stream().anyMatch(e -> e.card() == card);
    }

    public Paddle effectiveLeft() {
        return applyEffects(left, leftEffects);
    }

    public Paddle effectiveRight() {
        return applyEffects(right, rightEffects);
    }

    private Paddle applyEffects(Paddle paddle, List<Effect> effects) {
        for (Effect effect : effects) {
            switch (effect.card()) {
                case BARRICADE -> {

                }
                case BIG_PADDLE -> {
                    paddle = paddle.scaleHeight(1.5);
                }
                case SHRINK -> {
                    paddle = paddle.scaleHeight(0.5);
                }
                case FREEZE -> {

                }
                case SMASH -> {

                }
                case SLOW_FIELD -> {

                }
                case STRAFE -> {

                }
                case GOAL_SHIELD -> {

                }
                case null, default -> {

                }
            }
        }
        return paddle;
    }

    private double ballTimeScale() {
        double scale = 1;
        if (leftEffects.stream().anyMatch(e -> e.card() == Card.SMASH && e.active())
                || rightEffects.stream().anyMatch(e -> e.card() == Card.SMASH && e.active())) {
            scale *= 1.6;
        }
        if (hasEffect(leftEffects, Card.SLOW_FIELD)
                || hasEffect(rightEffects, Card.SLOW_FIELD)) {
            scale *= 0.5;
        }
        return scale;
    }

}
