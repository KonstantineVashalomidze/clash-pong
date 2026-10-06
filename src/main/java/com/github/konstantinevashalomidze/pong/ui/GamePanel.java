package com.github.konstantinevashalomidze.pong.ui;

import com.github.konstantinevashalomidze.pong.model.Ball;
import com.github.konstantinevashalomidze.pong.model.Paddle;
import com.github.konstantinevashalomidze.pong.model.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import static com.github.konstantinevashalomidze.pong.model.ActionEvents.W_DOWN;
import static com.github.konstantinevashalomidze.pong.model.ActionEvents.W_UP;

public class GamePanel extends JPanel {
    private World world;
    private final Set<String> actionKeys = new HashSet<>();

    public GamePanel(World world) {
        this.world = world;

        setBackground(Color.BLACK);

        Action wDown = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionKeys.add(W_DOWN);
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("pressed W"), W_DOWN);
        getActionMap().put(W_DOWN, wDown);

        Action wUp = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionKeys.remove(W_DOWN);
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("released W"), W_UP);
        getActionMap().put(W_UP, wUp);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (world == null) {
            throw new IllegalStateException("World is null");
        }

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

        // For linux
        Toolkit.getDefaultToolkit().sync();

    }

    public void setWorld(World world) {
        this.world = world;
        repaint();
    }

    public World getWorld() {
        return world;
    }

    public Set<String> getActionKeys() {
        return actionKeys;
    }


}
