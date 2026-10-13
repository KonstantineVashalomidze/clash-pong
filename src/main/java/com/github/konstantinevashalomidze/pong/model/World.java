package com.github.konstantinevashalomidze.pong.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.konstantinevashalomidze.pong.model.ActionEvents.*;
import static com.github.konstantinevashalomidze.pong.model.Card.*;

public record World(Playfield playfield, Ball ball, Player left, Player right) {
    private static final int PADDLE_SPEED = 300;
    private static final int SERVE_SPEED_X = 200;
    private static final int SERVE_SPEED_Y = 120;
    private static final double BIG_PADDLE_HEIGHT_SCALE = 1.5;
    private static final double SHRINK_HEIGHT_SCALE = 0.5;
    private static final double SMASH_TIME_SCALE = 1.6;
    private static final double SLOW_FIELD_TIME_SCALE = 0.5;
    private static final List<String> LEFT_CARD_KEYS = List.of(F_DOWN, G_DOWN, H_DOWN, J_DOWN);
    private static final List<String> RIGHT_CARD_KEYS = List.of(K_DOWN, L_DOWN, SEMI_DOWN, SINGLE_QUOTE_DOWN);

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
            if (hasEffect(this.left.effects(), GOAL_SHIELD)) {
                leftShieldUsed = true;
                ball = ball.reverseX();
            } else {
                rightScore += 1;
                ball = serve(ball, SERVE_SPEED_X);
            }

        }

        int leftScore = this.left.score();
        if (ball.isOutRight(playfield)) {
            leftScore += 1;
            ball = serve(ball, -SERVE_SPEED_X);
        }

        // Bounce ball off the paddles
        boolean leftSmashHit = false;
        boolean rightSmashHit = false;
        boolean wasLeftHit = ball.overlaps(effectiveLeft()) && ball.velocity().x() < 0;
        boolean wasRightHit = ball.overlaps(effectiveRight()) && ball.velocity().x() > 0;
        if (wasLeftHit || wasRightHit) {
            if (wasLeftHit
                    && this.left.effects().stream().anyMatch(e -> e.card() == SMASH && !e.active())) {
                leftSmashHit = true;
            }
            if (wasRightHit
                    && this.right.effects().stream().anyMatch(e -> e.card() == SMASH && !e.active())) {
                rightSmashHit = true;
            }
            ball = ball.reverseX();
        }

        // Energy management
        Energy leftEnergy = this.left.energy().refill(dt);
        Energy rightEnergy = this.right.energy().refill(dt);

        // Card usages
        CardPlay leftPlay = playCard(leftEnergy, this.left.hand(), actionPressedKeys, LEFT_CARD_KEYS);
        CardPlay rightPlay = playCard(rightEnergy, this.right.hand(), actionPressedKeys, RIGHT_CARD_KEYS);

        // Manage effects
        List<Effect> leftEffects = this.left.effects().stream().map(e -> e.tick(dt)).filter(e -> !e.isExpired()).collect(Collectors.toList());
        List<Effect> rightEffects = this.right().effects().stream().map(e -> e.tick(dt)).filter(e -> !e.isExpired()).collect(Collectors.toList());

        if (leftSmashHit) {
            leftEffects.removeIf(e -> e.card() == SMASH && !e.active());
            leftEffects.add(new Effect(SMASH, SMASH.duration(), true));
        }

        if (rightSmashHit) {
            rightEffects.removeIf(e -> e.card() == SMASH && !e.active());
            rightEffects.add(new Effect(SMASH, SMASH.duration(), true));
        }

        if (leftShieldUsed) {
            leftEffects.removeIf(e -> e.card() == GOAL_SHIELD);
        }

        applyPlayedCard(leftPlay.played(), leftEffects, rightEffects);
        applyPlayedCard(rightPlay.played(), rightEffects, leftEffects);

        return new World(
                playfield,
                ball,
                new Player(
                        leftPaddle,
                        leftScore,
                        leftPlay.energy(),
                        leftPlay.hand(),
                        leftEffects
                ),
                new Player(
                        rightPaddle,
                        rightScore,
                        rightPlay.energy(),
                        rightPlay.hand(),
                        rightEffects
                )
        );
    }

    private void applyPlayedCard(Card played, List<Effect> ownEffects, List<Effect> opponentEffects) {
        switch (played) {
            case BARRICADE -> refresh(ownEffects, BARRICADE, true);
            case BIG_PADDLE -> refresh(ownEffects, BIG_PADDLE, true);
            case SHRINK -> refresh(opponentEffects, SHRINK, true);
            case FREEZE -> refresh(opponentEffects, FREEZE, true);
            case SMASH -> refresh(ownEffects, SMASH, false);
            case SLOW_FIELD -> refresh(ownEffects, SLOW_FIELD, true);
            case STRAFE -> refresh(ownEffects, STRAFE, true);
            case GOAL_SHIELD -> refresh(ownEffects, GOAL_SHIELD, true);
            case null, default -> {

            }
        }
    }

    private Ball serve(Ball ball, double velocityX) {
        return new Ball(
                new Vec2(playfield.width() / 2, playfield.height() / 2),
                new Vec2(velocityX, SERVE_SPEED_Y),
                ball.radius()
        );
    }

    private Paddle movedPaddle(Player player, boolean up, boolean down, double dt) {
        boolean isFrozen = hasEffect(player.effects(), FREEZE);
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

    private static CardPlay playCard(Energy energy, Hand hand, Set<String> actionPressedKeys, List<String> cardKeys) {
        Card playedCard = null;
        for (int i = 0; i < cardKeys.size(); i++) {
            var cardKey = cardKeys.get(i);
            if (actionPressedKeys.contains(cardKey)) {
                if (energy.canAfford(hand.cards().get(i).cost())) {
                    playedCard = hand.cards().get(i);
                    hand = hand.play(i);
                    energy = energy.spend(playedCard.cost());
                }
            }
        }
        return new CardPlay(hand, energy, playedCard);
    }

    private static void refresh(List<Effect> effects, Card card, boolean active) {
        effects.removeIf(e -> e.card() == card);
        effects.add(new Effect(card, card.duration(), active));
    }

    private double ballTimeScale() {
        double scale = 1;
        if (this.left.effects().stream().anyMatch(e -> e.card() == SMASH && e.active())
                || this.right.effects().stream().anyMatch(e -> e.card() == SMASH && e.active())) {
            scale *= SMASH_TIME_SCALE;
        }
        if (hasEffect(this.left.effects(), SLOW_FIELD)
                || hasEffect(this.right.effects(), SLOW_FIELD)) {
            scale *= SLOW_FIELD_TIME_SCALE;
        }
        return scale;
    }

}
