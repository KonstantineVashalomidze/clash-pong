package com.github.konstantinevashalomidze.clashpong.model;


import java.util.ArrayList;
import java.util.List;

public class ComputerPlayer {
    private static final double REACTION_DEAD_ZONE = 20;
    private static final double ERROR_RATE = 0.05;
    private static final double CARD_PLAY_RATE = 0.005;
    private static final List<GameAction> RIGHT_CARD_ACTIONS =
            List.of(GameAction.P2_CARD_1, GameAction.P2_CARD_2, GameAction.P2_CARD_3,  GameAction.P2_CARD_4);


    public static GameAction makeAction(World world) {

        if (Math.random() < ERROR_RATE) {
            return null;
        }

        Ball ball = world.ball();
        Paddle right = world.right().paddle();


        if (right.center().y() > ball.position().y() + REACTION_DEAD_ZONE) {
            return GameAction.P2_UP;
        }

        if (right.center().y() < ball.position().y() - REACTION_DEAD_ZONE) {
            return GameAction.P2_DOWN;
        }

        return null;
    }


    public static GameAction chooseCard(World world) {
        if (Math.random() >= CARD_PLAY_RATE) {
            return null;
        }

        Hand hand = world.right().hand();
        Energy energy = world.right().energy();


        List<Integer> affordableSlots = new ArrayList<>();
        for (int i = 0; i < hand.cards().size(); i++) {
            if (energy.canAfford(hand.cards().get(i).cost())) {
                affordableSlots.add(i);
            }
        }

        if (affordableSlots.isEmpty()) {
            return null;
        }

        int chosenSlot = affordableSlots.get((int) (Math.random() * affordableSlots.size()));
        return RIGHT_CARD_ACTIONS.get(chosenSlot);

    }


}
