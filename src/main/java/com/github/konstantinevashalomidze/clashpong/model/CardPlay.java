package com.github.konstantinevashalomidze.clashpong.model;

import java.util.List;
import java.util.Set;

public record CardPlay(
        Hand hand,
        Energy energy,
        Card played
) {

    public static CardPlay decidePlay(Energy energy, Hand hand, Set<GameAction> actionPressedKeys, List<GameAction> cardKeys) {
        Card playedCard = null;
        for (int i = 0; i < cardKeys.size(); i++) {
            var cardKey = cardKeys.get(i);
            if (actionPressedKeys.contains(cardKey)) {
                if (energy.canAfford(hand.cards().get(i).cost())) {
                    playedCard = hand.cards().get(i);
                    hand = hand.play(i);
                    energy = energy.spend(playedCard.cost());
                }
            }
        }
        return new CardPlay(hand, energy, playedCard);
    }

}
