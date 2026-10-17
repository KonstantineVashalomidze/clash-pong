package com.github.konstantinevashalomidze.clashpong.model;

public record Energy(double amount) {

    public Energy {

        if (!Double.isFinite(amount)) {
            throw new IllegalArgumentException("Amount is Infinite");
        }

        if (amount < 0 || amount > 10) {
            throw new IllegalArgumentException("Amount must be between 0 and 10");
        }
    }

    public Energy refill(double dt) {
        return new Energy(Double.min(10, amount + dt / 1.5));
    }

    public boolean canAfford(double cost) {
        return amount >= cost;
    }

    public Energy spend(double cost) {
        return new Energy(amount - cost);
    }

}
