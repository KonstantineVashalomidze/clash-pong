package com.github.konstantinevashalomidze.clashpong.model;

import java.util.ArrayList;
import java.util.List;

public record Brick(
        Vec2 center,
        double width,
        double height,
        double remainingSeconds
) {
    private static final int BARRICADE_BRICK_COUNT = 3;
    private static final int SPAWN_WIDTH = 16;
    private static final int SPAWN_HEIGHT = 40;
    private static final int SPAWN_AREA_X1 = 350, SPAWN_AREA_X2 = 450;
    private static final int SPAWN_AREA_Y1 = 100, SPAWN_AREA_Y2 = 400;

    public Brick {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid brick width or height");
        }

        if (!Double.isFinite(width)
            || !Double.isFinite(height)
            || !Double.isFinite(remainingSeconds)
        ) throw new IllegalArgumentException("Invalid brick argument");
    }

    public double topLeftX() {
        return center.x() - width / 2;
    }

    public double topLeftY() {
        return center.y() - height / 2;
    }

    public Brick tick(double dt) {
        return new Brick(center, width, height, remainingSeconds - dt);
    }

    public boolean isExpired() {
        return remainingSeconds <= 0;
    }

    public static List<Brick> spawnBarricades(List<Brick> existing) {
        var bricks = new ArrayList<>(existing);
        for (int i = 0; i < BARRICADE_BRICK_COUNT; i++) {
            bricks.add(spawnBarricade());
        }
        return bricks;
    }

    private static Brick spawnBarricade() {
        return new Brick(
                new Vec2(randomInRange(SPAWN_AREA_X1, SPAWN_AREA_X2), randomInRange(SPAWN_AREA_Y1, SPAWN_AREA_Y2)),
                SPAWN_WIDTH,
                SPAWN_HEIGHT,
                Card.BARRICADE.duration()
        );
    }

    private static double randomInRange(double lower, double upper) {
        return lower + (upper - lower) * Math.random();
    }

}
