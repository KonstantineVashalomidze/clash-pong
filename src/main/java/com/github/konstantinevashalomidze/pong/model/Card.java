package com.github.konstantinevashalomidze.pong.model;

public enum Card {
    BARRICADE(3, 12),
    BIG_PADDLE(3, 6),
    SHRINK(3, 6),
    FREEZE(4, 1),
    SMASH(2, 5),
    SLOW_FIELD(2, 3),
    STRAFE(3, 6),
    GOAL_SHIELD(4, 10);

    private final int cost;
    private final double duration;

    Card(int cost, double duration) {
        this.cost = cost; this.duration = duration;
    }

    public int cost() {
        return cost;
    }

    public double duration() {
        return duration;
    }

}
