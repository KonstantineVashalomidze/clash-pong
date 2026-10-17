package com.github.konstantinevashalomidze.clashpong.model;

public record Effect(Card card, double remainingSeconds, boolean active) {

    public Effect {
        if (Double.isNaN(remainingSeconds)) throw new EffectArgumentException("Remaining time is NaN");
        if (Double.isInfinite(remainingSeconds)) throw new EffectArgumentException("Remaining time is Infinite");
    }

    public Effect tick(double dt) {
        return new Effect(card, remainingSeconds - dt, active);
    }

    public boolean isExpired() {
        return remainingSeconds <= 0;
    }

}
