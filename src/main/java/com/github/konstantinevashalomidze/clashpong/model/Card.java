package com.github.konstantinevashalomidze.clashpong.model;

import java.util.List;

public enum Card {
    BARRICADE(3, 12, false, true),
    BIG_PADDLE(3, 6, false, true),
    SHRINK(3, 6, true, true),
    FREEZE(4, 1, true, true),
    SMASH(2, 5, false, false),
    SLOW_FIELD(2, 3, false, true),
    STRAFE(3, 6, false, true),
    GOAL_SHIELD(4, 10, false, true);

    private final int cost;
    private final double duration;
    private final boolean targetsOpponent;
    private final boolean startsActive;

    Card(int cost, double duration, boolean targetsOpponent, boolean startsActive) {
        this.cost = cost; this.duration = duration;
        this.targetsOpponent = targetsOpponent; this.startsActive = startsActive;
    }

    public int cost() {
        return cost;
    }

    public double duration() {
        return duration;
    }

    public void applyEffect(List<Effect> ownEffects, List<Effect> opponentEffects) {
        List<Effect> target = targetsOpponent ? opponentEffects : ownEffects;
        target.removeIf(e -> e.card() == this);
        target.add(new Effect(this, duration, startsActive));
    }

}
