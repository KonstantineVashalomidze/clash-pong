package com.github.konstantinevashalomidze.pong.model;

public enum Card {
    BARRICADE(3),
    BIG_PADDLE(3),
    SHRINK(3),
    FREEZE(4),
    SMASH(2),
    SLOW_FIELD(2),
    STRAFE(3),
    GOAL_SHIELD(4);

    private final int cost;

    Card(int cost) {
        this.cost = cost;
    }

    public int cost() {
        return cost;
    }

}
