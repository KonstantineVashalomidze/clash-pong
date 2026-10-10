package com.github.konstantinevashalomidze.pong.model;

import java.util.List;

public record Player(
        Paddle paddle,
        int score,
        Energy energy,
        Hand hand,
        List<Effect> effects
) {
    public Player {
        if (score < 0) {
            throw new PlayerArgumentException("Player score is negative");
        }
        effects = List.copyOf(effects);
    }
}
