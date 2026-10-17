package com.github.konstantinevashalomidze.clashpong.model;

import java.util.List;

public record Player(
        Paddle paddle,
        int score,
        Energy energy,
        Hand hand,
        List<Effect> effects
) {
    private static final int PADDLE_SPEED = 300;
    private static final double BIG_PADDLE_HEIGHT_SCALE = 1.5;
    private static final double SHRINK_PADDLE_HEIGHT_SCALE = 0.5;
    private static final int LEFT_HOME_X = 30;
    private static final int RIGHT_HOME_X = 770;

    public Player {
        if (score < 0) {
            throw new IllegalArgumentException("Player score is negative");
        }
        effects = List.copyOf(effects);
    }

    public boolean hasEffect(Card card) {
        return effects.stream().anyMatch(e -> e.card() == card);
    }

    public Paddle effectivePaddle() {
        return applyEffectsTo(paddle);
    }

    private Paddle applyEffectsTo(Paddle initial) {
        Paddle applied = initial;
        for (var effect : effects) {
            switch (effect.card()) {
                case BIG_PADDLE -> applied = applied.scaleHeight(BIG_PADDLE_HEIGHT_SCALE);
                case SHRINK -> applied = applied.scaleHeight(SHRINK_PADDLE_HEIGHT_SCALE);
                case null, default -> {}
            }
        }
        return applied;
    }

    public Paddle movedPaddle(boolean up, boolean down, boolean left, boolean right, double dt, Playfield playfield) {
        if (hasEffect(Card.FREEZE)) {
            return paddle;
        }

        Paddle moved = paddle;
        if (up) {
            moved = moved.moveByY(-PADDLE_SPEED * dt);
        }
        if (down) {
            moved = moved.moveByY(PADDLE_SPEED * dt);
        }

        boolean hasStrafe = hasEffect(Card.STRAFE);
        if (hasStrafe && left) {
            moved = moved.moveByX(-PADDLE_SPEED * dt);
        }
        if (hasStrafe && right) {
            moved = moved.moveByX(PADDLE_SPEED * dt);
        }

        boolean onLeftHalf = paddle.center().x() < playfield.width() / 2;
        double minX, maxX;
        if (onLeftHalf) {
            minX = LEFT_HOME_X;
            maxX = playfield.width() / 2 - moved.width() / 2;
        } else {
            minX = playfield.width() / 2 + moved.width() / 2;
            maxX = RIGHT_HOME_X;
        }

        if (!hasStrafe) {
            moved = onLeftHalf ? moved.moveByX(-PADDLE_SPEED * dt) : moved.moveByX(PADDLE_SPEED * dt);
        }

        Paddle scaledForClamping = applyEffectsTo(moved);
        return new Paddle(
            scaledForClamping.clampTo(playfield).clampXTo(minX, maxX).center(),
            moved.width(),
            moved.height()
        );
    }

}
