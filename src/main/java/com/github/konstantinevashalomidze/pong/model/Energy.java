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

    public boolean canAfford(double cost) {
        return amount >= cost;
    }

    public Energy spend(double cost) {
        if (!canAfford(cost)) {
            throw new EnergyArgumentException("Need energy of %.1f, have %.1f".formatted(cost, amount));
        }
        return new Energy(amount - cost);
    }

}
