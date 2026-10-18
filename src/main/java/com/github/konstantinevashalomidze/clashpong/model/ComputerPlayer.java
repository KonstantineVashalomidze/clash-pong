package com.github.konstantinevashalomidze.clashpong.model;


public class ComputerPlayer {
    private static final double REACTION_DEAD_ZONE = 20;
    private static final double ERROR_RATE = 0.05;

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
}
