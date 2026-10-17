package com.github.konstantinevashalomidze.clashpong.model;

import static com.github.konstantinevashalomidze.clashpong.model.ActionEvents.*;

public class ComputerPlayer {
    public static String makeAction(World world) {
        Ball ball = world.ball();
        Paddle right = world.right().paddle();


        if (right.center().y() > ball.position().y() + 10) {
            return UP_ARROW_DOWN;
        }

        if (right.center().y() < ball.position().y() - 10) {
            return DOWN_ARROW_DOWN;
        }

        return null;
    }
}
