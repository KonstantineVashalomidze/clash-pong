package com.github.konstantinevashalomidze.pong.ui;

import com.github.konstantinevashalomidze.pong.model.Ball;
import com.github.konstantinevashalomidze.pong.model.Paddle;
import com.github.konstantinevashalomidze.pong.model.World;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final World world;

    public GamePanel(World world) {
        this.world = world;

        setBackground(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Ball
        Ball ball = world.ball();
        int ballCenterX = (int) ball.position().x();
        int ballCenterY = (int) ball.position().y();
        int ballRadius = (int) ball.radius();

        // Ball color
        g.setColor(Color.WHITE);
        // Map ball into rectangle which is around that ball touching
        g.fillOval(
                ballCenterX - ballRadius,
                ballCenterY - ballRadius,
                2 * ballRadius,
                2 * ballRadius
        );

        // Left paddle
        Paddle leftPaddle = world.left();
        int leftPaddleCenterX = (int) leftPaddle.center().x();
        int leftPaddleCenterY = (int) leftPaddle.center().y();
        int leftPaddleWidth = (int) leftPaddle.width();
        int leftPaddleHeight = (int) leftPaddle.height();

        // Right paddle
        Paddle rightPaddle = world.right();
        int rightPaddleCenterX = (int) rightPaddle.center().x();
        int rightPaddleCenterY = (int) rightPaddle.center().y();
        int rightPaddleWidth = (int) rightPaddle.width();
        int rightPaddleHeight = (int) rightPaddle.height();

        // Paddle 1 color
        g.setColor(Color.WHITE);

        // Map paddle into rectangle
        g.fillRect(
                leftPaddleCenterX - leftPaddleWidth / 2,
                leftPaddleCenterY - leftPaddleHeight / 2,
                 leftPaddleWidth,
                 leftPaddleHeight
        );

        // Paddle 2 color
        g.setColor(Color.WHITE);

        g.fillRect(
                rightPaddleCenterX - rightPaddleWidth / 2,
                rightPaddleCenterY - rightPaddleHeight / 2,
                rightPaddleWidth,
                rightPaddleHeight
        );

    }

}
