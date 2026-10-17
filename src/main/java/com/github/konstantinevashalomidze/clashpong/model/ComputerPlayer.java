package com.github.konstantinevashalomidze.clashpong.model;


public class ComputerPlayer {
    public static GameAction makeAction(World world) {
        Ball ball = world.ball();
        Paddle right = world.right().paddle();


        if (right.center().y() > ball.position().y() + 10) {
            return GameAction.P2_UP;
        }

        if (right.center().y() < ball.position().y() - 10) {
            return GameAction.P2_DOWN;
        }

        return null;
    }
}
