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
    private static final List<String> LEFT_CARD_KEYS = List.of(F_DOWN, G_DOWN, H_DOWN, J_DOWN);

    public World step(double dt, Set<String> actionKeys, Set<String> actionPressedKeys) {

        // Paddle movement
        Paddle leftPaddle = movedPaddle(
                this.left,
                actionKeys.contains(W_DOWN) && !actionKeys.contains(S_DOWN),
                actionKeys.contains(S_DOWN) && !actionKeys.contains(W_DOWN),
                dt);


        Paddle rightPaddle = movedPaddle(
                this.right,
                actionKeys.contains(UP_ARROW_DOWN) && !actionKeys.contains(DOWN_ARROW_DOWN),
                actionKeys.contains(DOWN_ARROW_DOWN) && !actionKeys.contains(UP_ARROW_DOWN),
                dt);

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
        Hand leftHand = this.left.hand();
        Card leftPlayedCard = null;

        for (int i = 0; i < LEFT_CARD_KEYS.size(); i++) {
            var cardKey = LEFT_CARD_KEYS.get(i);
            if (actionPressedKeys.contains(cardKey)) {
                if (leftEnergy.canAfford(this.left.hand().cards().get(i).cost())) {
                    leftPlayedCard = this.left.hand().cards().get(i);
                    leftHand = this.left.hand().play(i);
                    leftEnergy = leftEnergy.spend(this.left.hand().cards().get(i).cost());
                }
            }
        }

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
                        leftPaddle,
                        leftScore,
                        leftEnergy,
                        leftHand,
                        leftEffects
                ),
                new Player(
                        rightPaddle,
                        rightScore,
                        rightEnergy,
                        this.right.hand(),
                        rightEffects
                )
        );
    }

    private Paddle movedPaddle(Player player, boolean up, boolean down, double dt) {
        boolean isFrozen = hasEffect(player.effects(), Card.FREEZE);
        Paddle paddle = player.paddle();
        if (isFrozen) {
            return paddle;
        }
        if (up) {
            paddle = player.paddle().moveBy(-PADDLE_SPEED * dt);
        }

        if (down) {
            paddle = player.paddle().moveBy(PADDLE_SPEED * dt);
        }

        paddle = new Paddle(
                applyEffects(paddle, player.effects()).clampTo(playfield).center(),
                paddle.width(),
                paddle.height()
        );

        return paddle;
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
