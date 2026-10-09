package com.github.konstantinevashalomidze.pong.model;

public record Effect(Card card, double remainingSeconds) {

    public Effect {
        if (Double.isNaN(remainingSeconds)) throw new EffectArgumentException("Remaining time is NaN");
        if (Double.isInfinite(remainingSeconds)) throw new EffectArgumentException("Remaining time is Infinite");
    }

    public Effect tick(double dt) {
        return new Effect(card, remainingSeconds - dt);
    }

    public boolean isExpired() {
        return remainingSeconds <= 0;
    }

}
