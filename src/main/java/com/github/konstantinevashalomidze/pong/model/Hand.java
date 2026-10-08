package com.github.konstantinevashalomidze.pong.model;

import java.util.List;

public record Hand(List<Card> hand, List<Card> queue) {
    public Hand {
        if (hand.size() != 4) {
            throw new HandArgumentException("Invalid hand argument");
        }

        if (queue.size() != 4) {
            throw new HandArgumentException("Invalid queue argument");
        }

        hand = List.copyOf(hand);
        queue = List.copyOf(queue);
    }



}
