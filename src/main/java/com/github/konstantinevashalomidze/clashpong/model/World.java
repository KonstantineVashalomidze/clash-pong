package com.github.konstantinevashalomidze.clashpong.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.konstantinevashalomidze.clashpong.model.Card.*;
import static com.github.konstantinevashalomidze.clashpong.model.Side.LEFT;
import static com.github.konstantinevashalomidze.clashpong.model.Side.RIGHT;

public record World(Playfield playfield, Ball ball, Player left, Player right, List<Brick> bricks) {
    private static final int SERVE_SPEED_X = 200;
    private static final int SERVE_SPEED_Y = 120;
    private static final double SMASH_TIME_SCALE = 1.6;
    private static final double SLOW_FIELD_TIME_SCALE = 0.5;
    private static final List<GameAction> LEFT_CARD_KEYS = List.of(GameAction.P1_CARD_1, GameAction.P1_CARD_2,  GameAction.P1_CARD_3, GameAction.P1_CARD_4);
    private static final List<GameAction> RIGHT_CARD_KEYS = List.of(GameAction.P2_CARD_1, GameAction.P2_CARD_2,  GameAction.P2_CARD_3, GameAction.P2_CARD_4);
    private static final int WIN_SCORE = 7;
    private static final int BARRICADE_BRICK_COUNT = 3;
    private static final int BRICK_WIDTH = 16;
    private static final int BRICK_HEIGHT = 40;
    private static final int BRICK_SPAWNING_AREA_X1 = 350, BRICK_SPAWNING_AREA_X2 = 450;
    private static final int BRICK_SPAWNING_AREA_Y1 = 100, BRICK_SPAWNING_AREA_Y2 = 400;

    public World step(double dt, Set<GameAction> actionKeys, Set<GameAction> actionPressedKeys) {
        if (winner().isPresent()) {
            return this;
        }

        // Paddle movement
        Paddle leftPaddle = this.left.movedPaddle(
                actionKeys.contains(GameAction.P1_UP) && !actionKeys.contains(GameAction.P1_DOWN),
                actionKeys.contains(GameAction.P1_DOWN) && !actionKeys.contains(GameAction.P1_UP),
                actionKeys.contains(GameAction.P1_STRAFE_LEFT) && !actionKeys.contains(GameAction.P1_STRAFE_RIGHT),
                actionKeys.contains(GameAction.P1_STRAFE_RIGHT) && !actionKeys.contains(GameAction.P1_STRAFE_LEFT),
                dt,
                playfield);

        Paddle rightPaddle = this.right.movedPaddle(
                actionKeys.contains(GameAction.P2_UP) && !actionKeys.contains(GameAction.P2_DOWN),
                actionKeys.contains(GameAction.P2_DOWN) && !actionKeys.contains(GameAction.P2_UP),
                actionKeys.contains(GameAction.P2_STRAFE_LEFT) && !actionKeys.contains(GameAction.P2_STRAFE_RIGHT),
                actionKeys.contains(GameAction.P2_STRAFE_RIGHT) && !actionKeys.contains(GameAction.P2_STRAFE_LEFT),
                dt,
                playfield);

        // Ball movement
        Ball ball = this.ball.advance(dt * ballTimeScale()).bounceOffWalls(playfield);

        boolean leftShieldUsed = false;
        int rightScore = this.right.score();
        if (ball.isOutLeft(playfield)) {
            if (this.left.hasEffect(GOAL_SHIELD)) {
                leftShieldUsed = true;
                ball = ball.reverseX();
            } else {
                rightScore += 1;
                ball = serve(ball, SERVE_SPEED_X);
            }

        }

        boolean rightShieldUsed = false;
        int leftScore = this.left.score();
        if (ball.isOutRight(playfield)) {
            if (this.right.hasEffect(GOAL_SHIELD)) {
                rightShieldUsed = true;
                ball = ball.reverseX();
            } else {
                leftScore += 1;
                ball = serve(ball, -SERVE_SPEED_X);
            }
        }

        // Bounce ball off the paddles
        boolean leftSmashHit = false;
        boolean rightSmashHit = false;
        boolean wasLeftHit = ball.overlaps(this.left.effectivePaddle()) && ball.velocity().x() < 0;
        boolean wasRightHit = ball.overlaps(this.right.effectivePaddle()) && ball.velocity().x() > 0;
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

        if (rightShieldUsed) {
            rightEffects.removeIf(e -> e.card() == GOAL_SHIELD);
        }

        applyPlayedCard(leftPlay.played(), leftEffects, rightEffects);
        applyPlayedCard(rightPlay.played(), rightEffects, leftEffects);

        List<Brick> newBricks = bricks;

        newBricks = newBricks.stream()
                .map(b -> b.tick(dt))
                .filter(b -> !b.isExpired())
                .collect(Collectors.toList());

        for (int i = 0; i < newBricks.size(); i++) {
            Brick currentBrick = newBricks.get(i);
            if (ball.overlaps(currentBrick)) {
                ball = ball.bounceOffBrick(currentBrick);
                break;
            }
        }

        newBricks.removeIf(ball::overlaps);

        if (leftPlay.played() == BARRICADE) {
            newBricks = spawnBricks(newBricks);
        }

        if (rightPlay.played() == BARRICADE) {
            newBricks = spawnBricks(newBricks);
        }


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
                ),
                newBricks
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

    private static CardPlay playCard(Energy energy, Hand hand, Set<GameAction> actionPressedKeys, List<GameAction> cardKeys) {
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
        if (this.left.hasEffect(SLOW_FIELD)
                || this.left.hasEffect(SLOW_FIELD)) {
            scale *= SLOW_FIELD_TIME_SCALE;
        }
        return scale;
    }

    public Optional<Side> winner() {
        if (left.score() >= WIN_SCORE) {
            return Optional.of(LEFT);
        } else if (right.score() >= WIN_SCORE) {
            return Optional.of(RIGHT);
        } else {
            return Optional.empty();
        }
    }

    public static List<Brick> spawnBricks(List<Brick> bricks) {
        List<Brick> newBricks = new ArrayList<>(bricks);
        for (int i = 0; i < BARRICADE_BRICK_COUNT; i++) {
            newBricks.add(new Brick(
                    new Vec2(
                            randomInRange(BRICK_SPAWNING_AREA_X1, BRICK_SPAWNING_AREA_X2),
                            randomInRange(BRICK_SPAWNING_AREA_Y1, BRICK_SPAWNING_AREA_Y2)
                            ),
                    BRICK_WIDTH,
                    BRICK_HEIGHT,
                    BARRICADE.duration()
            ));
        }
        return newBricks;
    }

    private static double randomInRange(double lower, double upper) {
        return lower + (upper - lower) * Math.random();
    }

}
