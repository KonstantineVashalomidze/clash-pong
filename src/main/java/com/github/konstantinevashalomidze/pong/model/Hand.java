package com.github.konstantinevashalomidze.pong.model;

import java.util.ArrayList;
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

    public Hand play(int slot) {
        if (slot < 0 || slot >= hand.size()) {
            throw new HandArgumentException("Invalid slot argument");
        }
        List<Card> hand = new ArrayList<>(this.hand);
        List<Card> queue = new ArrayList<>(this.queue);
        Card cardFromHand = hand.remove(slot);
        Card cardFromQueue = queue.removeFirst();
        hand.add(slot, cardFromQueue);
        queue.add(cardFromHand);
        return new Hand(hand, queue);
    }

}
