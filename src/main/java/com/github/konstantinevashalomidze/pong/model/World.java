package com.github.konstantinevashalomidze.pong.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.konstantinevashalomidze.pong.model.ActionEvents.*;

public record World(Playfield playfield, Ball ball, Player left, Player right) {
    private static final int PADDLE_SPEED = 300;
    private static final int SERVE_SPEED_X = 200;
    private static final int SERVE_SPEED_Y = 120;
    private static final double BIG_PADDLE_HEIGHT_SCALE = 1.5;
    private static final double SHRINK_HEIGHT_SCALE = 0.5;
    private static final double SMASH_TIME_SCALE = 1.6;
    private static final double SLOW_FIELD_TIME_SCALE = 0.5;

    public World step(double dt, Set<String> actionKeys, Set<String> actionPressedKeys) {

        // Paddle movement
        boolean leftIsFrozen = hasEffect(this.left.effects(), Card.FREEZE);
        Paddle left = null;
        if (actionKeys.contains(W_DOWN) && !actionKeys.contains(S_DOWN) && !leftIsFrozen) {
            left = this.left.paddle().moveBy(-PADDLE_SPEED * dt);
        }

        if (actionKeys.contains(S_DOWN) && !actionKeys.contains(W_DOWN) && !leftIsFrozen) {
            left = this.left.paddle().moveBy(PADDLE_SPEED * dt);
        }

        if (left == null) {
            left = this.left.paddle();
        }

        left = new Paddle(
                applyEffects(left, this.left.effects()).clampTo(playfield).center(),
                left.width(),
                left.height()
        );

        boolean rightIsFrozen = hasEffect(this.right.effects(), Card.FREEZE);
        Paddle right = null;
        if (actionKeys.contains(UP_ARROW_DOWN) && !actionKeys.contains(DOWN_ARROW_DOWN) && !rightIsFrozen) {
            right = this.right.paddle().moveBy(-PADDLE_SPEED * dt);
        }

        if (actionKeys.contains(DOWN_ARROW_DOWN) && !actionKeys.contains(UP_ARROW_DOWN) && !rightIsFrozen) {
            right = this.right.paddle().moveBy(PADDLE_SPEED * dt);
        }

        if (right == null) {
            right = this.right.paddle();
        }

        right = new Paddle(
                applyEffects(right, this.right.effects()).clampTo(playfield).center(),
                right.width(),
                right.height()
        );


        // Ball movement
        boolean leftShieldUsed = false;
        Ball ball = this.ball.advance(dt * ballTimeScale()).bounceOffWalls(playfield);
        int rightScore = this.right.score();
        if (ball.isOutLeft(playfield)) {
            if (hasEffect(this.left.effects(), Card.GOAL_SHIELD)) {
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
                        new Vec2(SERVE_SPEED_X, SERVE_SPEED_Y),
                        ball().radius()
                );
            }

        }

        int leftScore = this.left.score();
        if (ball.isOutRight(playfield)) {
            leftScore += 1;
            ball = new Ball(
                    new Vec2(playfield.width() / 2, playfield.height() / 2),
                    new Vec2(-SERVE_SPEED_X, SERVE_SPEED_Y),
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
                    && this.left.effects().stream().anyMatch(e -> e.card() == Card.SMASH && !e.active())) {
                leftSmashHit = true;
            }
            if (wasRightHit
                    && this.right.effects().stream().anyMatch(e -> e.card() == Card.SMASH && !e.active())) {
                rightSmashHit = true;
            }
            ball = new Ball(
                ball.position(),
                new Vec2((-1) * ball.velocity().x(), ball.velocity().y()),
                ball.radius()
            );
        }

        // Energy management
        Energy leftEnergy = this.left.energy().refill(dt);
        Energy rightEnergy = this.right.energy().refill(dt);

        // Card usages
        Hand leftHand = null;
        Card leftPlayedCard = null;
        if (actionPressedKeys.contains(F_DOWN)) {
            if (leftEnergy.canAfford(this.left.hand().cards().get(0).cost())) {
                leftPlayedCard = this.left.hand().cards().get(0);
                leftHand = this.left.hand().play(0);
                leftEnergy = leftEnergy.spend(this.left.hand().cards().get(0).cost());
            }
        }

        if (actionPressedKeys.contains(G_DOWN)) {
            if (leftEnergy.canAfford(this.left.hand().cards().get(1).cost())) {
                leftPlayedCard = this.left.hand().cards().get(1);
                leftHand = this.left.hand().play(1);
                leftEnergy = leftEnergy.spend(this.left.hand().cards().get(1).cost());
            }
        }

        if (actionPressedKeys.contains(H_DOWN)) {
            if (leftEnergy.canAfford(this.left.hand().cards().get(2).cost())) {
                leftPlayedCard = this.left.hand().cards().get(2);
                leftHand = this.left.hand().play(2);
                leftEnergy = leftEnergy.spend(this.left.hand().cards().get(2).cost());
            }
        }

        if (actionPressedKeys.contains(J_DOWN)) {
            if (leftEnergy.canAfford(this.left.hand().cards().get(3).cost())) {
                leftPlayedCard = this.left.hand().cards().get(3);
                leftHand = this.left.hand().play(3);
                leftEnergy = leftEnergy.spend(this.left.hand().cards().get(3).cost());
            }
        }

        leftHand = leftHand == null ? this.left.hand() : leftHand;

        // Manage effects
        List<Effect> leftEffects = this.left.effects().stream().map(e -> e.tick(dt)).filter(e -> !e.isExpired()).collect(Collectors.toList());
        List<Effect> rightEffects = this.right().effects().stream().map(e -> e.tick(dt)).filter(e -> !e.isExpired()).collect(Collectors.toList());

        if (leftSmashHit) {
            leftEffects.removeIf(e -> e.card() == Card.SMASH && !e.active());
            leftEffects.add(new Effect(Card.SMASH, Card.SMASH.duration(), true));
        }

        if (leftShieldUsed) {
            leftEffects.removeIf(e -> e.card() == Card.GOAL_SHIELD);
        }

        switch (leftPlayedCard) {
            case BARRICADE -> refresh(leftEffects, Card.BARRICADE, true);
            case BIG_PADDLE -> refresh(leftEffects, Card.BIG_PADDLE, true);
            case SHRINK -> refresh(rightEffects, Card.SHRINK, true);
            case FREEZE -> refresh(rightEffects, Card.FREEZE, true);
            case SMASH -> refresh(leftEffects, Card.SMASH, false);
            case SLOW_FIELD -> refresh(leftEffects, Card.SLOW_FIELD, true);
            case STRAFE -> refresh(leftEffects, Card.STRAFE, true);
            case GOAL_SHIELD -> refresh(leftEffects, Card.GOAL_SHIELD, true);
            case null, default -> {

            }
        }

        return new World(
            playfield,
            ball,
            new Player(
                left,
                leftScore,
                leftEnergy,
                leftHand,
                leftEffects
            ),
            new Player(
                    right,
                    rightScore,
                    rightEnergy,
                    this.right.hand(),
                    rightEffects
            )
        );
    }

    private static boolean hasEffect(List<Effect> effects, Card card) {
        return effects.stream().anyMatch(e -> e.card() == card);
    }

    public Paddle effectiveLeft() {
        return applyEffects(left.paddle(), this.left.effects());
    }

    public Paddle effectiveRight() {
        return applyEffects(right.paddle(), this.right.effects());
    }

    private Paddle applyEffects(Paddle paddle, List<Effect> effects) {
        for (Effect effect : effects) {
            switch (effect.card()) {
                case BARRICADE -> {

                }
                case BIG_PADDLE -> paddle = paddle.scaleHeight(BIG_PADDLE_HEIGHT_SCALE);
                case SHRINK -> paddle = paddle.scaleHeight(SHRINK_HEIGHT_SCALE);
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

    private static void refresh(List<Effect> effects, Card card, boolean active) {
        effects.removeIf(e -> e.card() == card);
        effects.add(new Effect(card, card.duration(), active));
    }

    private double ballTimeScale() {
        double scale = 1;
        if (this.left.effects().stream().anyMatch(e -> e.card() == Card.SMASH && e.active())
                || this.right.effects().stream().anyMatch(e -> e.card() == Card.SMASH && e.active())) {
            scale *= SMASH_TIME_SCALE;
        }
        if (hasEffect(this.left.effects(), Card.SLOW_FIELD)
                || hasEffect(this.right.effects(), Card.SLOW_FIELD)) {
            scale *= SLOW_FIELD_TIME_SCALE;
        }
        return scale;
    }

}
