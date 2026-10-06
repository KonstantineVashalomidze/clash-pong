package com.github.konstantinevashalomidze.pong.model;

import static com.github.konstantinevashalomidze.pong.model.ActionEvents.DOWN_ARROW_DOWN;
import static com.github.konstantinevashalomidze.pong.model.ActionEvents.UP_ARROW_UP;

public class ComputerPlayer {
    public static String makeAction(World world) {
        Ball ball = world.ball();
        Paddle right = world.right();

        if (right.center().y() > ball.position().y() + 10) {
            return UP_ARROW_UP;
        }

        if (right.center().y() < ball.position().y() - 10) {
            return DOWN_ARROW_DOWN;
        }

        return null;
    }
}
