package com.github.konstantinevashalomidze.clashpong.model;

public record Effect(Card card, double remainingSeconds, boolean active) {

    public Effect {
        if (!Double.isFinite(remainingSeconds)) throw new IllegalArgumentException("Remaining time is Infinite");
    }

    public Effect tick(double dt) {
        return new Effect(card, remainingSeconds - dt, active);
    }

    public boolean isExpired() {
        return remainingSeconds <= 0;
    }

}
