package com.github.konstantinevashalomidze.clashpong.model;

import java.util.ArrayList;
import java.util.List;

public record Hand(List<Card> cards, List<Card> queue) {
    public Hand {
        if (cards.size() != 4) {
            throw new HandArgumentException("Invalid cards argument");
        }

        if (queue.size() != 4) {
            throw new HandArgumentException("Invalid queue argument");
        }

        cards = List.copyOf(cards);
        queue = List.copyOf(queue);
    }

    public Hand play(int slot) {
        if (slot < 0 || slot >= cards.size()) {
            throw new HandArgumentException("Invalid slot argument");
        }
        List<Card> hand = new ArrayList<>(this.cards);
        List<Card> queue = new ArrayList<>(this.queue);
        Card cardFromHand = hand.remove(slot);
        Card cardFromQueue = queue.removeFirst();
        hand.add(slot, cardFromQueue);
        queue.add(cardFromHand);
        return new Hand(hand, queue);
    }

}
