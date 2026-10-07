package com.github.konstantinevashalomidze.pong.model;

public record Energy(double amount) {

    public Energy {

        if (Double.isNaN(amount)) {
            throw new EnergyArgumentException("Amount is NaN");
        }

        if (Double.isInfinite(amount)) {
            throw new EnergyArgumentException("Amount is Infinite");
        }

        if (amount < 0 || amount > 10) {
            throw new EnergyArgumentException("Amount must be between 0 and 10");
        }
    }

    public Energy refill(double dt) {
        return new Energy(Double.min(10, amount + dt / 1.5));
    }



}
