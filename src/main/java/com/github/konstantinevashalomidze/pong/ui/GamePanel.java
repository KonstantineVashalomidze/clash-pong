package com.github.konstantinevashalomidze.pong.ui;

import com.github.konstantinevashalomidze.pong.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import static com.github.konstantinevashalomidze.pong.model.ActionEvents.*;
import static com.github.konstantinevashalomidze.pong.model.GameMode.HUMAN_VS_COMPUTER;
import static com.github.konstantinevashalomidze.pong.model.GameMode.HUMAN_VS_HUMAN;

public class GamePanel extends JPanel {
    private final Set<String> actionKeys = new HashSet<>();
    private World world;
    private GameMode gameMode;
    public GamePanel(World world) {
        this.world = world;

        setBackground(Color.BLACK);

        Action wDown = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameMode != null) {
                    actionKeys.add(W_DOWN);
                }
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("pressed W"), W_DOWN);
        getActionMap().put(W_DOWN, wDown);

        Action wUp = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameMode != null) {
                    actionKeys.remove(W_DOWN);
                }
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("released W"), W_UP);
        getActionMap().put(W_UP, wUp);

        Action sDown = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameMode != null) {
                    actionKeys.add(S_DOWN);
                }
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("pressed S"), S_DOWN);
        getActionMap().put(S_DOWN, sDown);

        Action sUp = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameMode != null) {
                    actionKeys.remove(S_DOWN);
                }
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("released S"), S_UP);
        getActionMap().put(S_UP, sUp);

        Action upArrowDown = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameMode == HUMAN_VS_HUMAN) {
                    actionKeys.add(UP_ARROW_DOWN);
                }
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("pressed UP"), UP_ARROW_DOWN);
        getActionMap().put(UP_ARROW_DOWN, upArrowDown);

        Action upArrowUp = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameMode == HUMAN_VS_HUMAN) {
                    actionKeys.remove(UP_ARROW_DOWN);
                }
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("released UP"),  UP_ARROW_UP);
        getActionMap().put(UP_ARROW_UP, upArrowUp);

        Action downArrowDown = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameMode == HUMAN_VS_HUMAN) {
                    actionKeys.add(DOWN_ARROW_DOWN);
                }
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("pressed DOWN"), DOWN_ARROW_DOWN);
        getActionMap().put(DOWN_ARROW_DOWN, downArrowDown);

        Action downArrowUp = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameMode == HUMAN_VS_HUMAN) {
                    actionKeys.remove(DOWN_ARROW_DOWN);
                }
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("released DOWN"), DOWN_ARROW_UP);
        getActionMap().put(DOWN_ARROW_UP, downArrowUp);

        Action oneDown = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameMode = HUMAN_VS_HUMAN;
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("pressed 1"), ONE_DOWN);
        getActionMap().put(ONE_DOWN, oneDown);

        Action twoDown =  new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameMode = HUMAN_VS_COMPUTER;
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("pressed 2"), TWO_DOWN);
        getActionMap().put(TWO_DOWN, twoDown);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw menu
        if (gameMode == null) {
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
            g.drawString("Press 1: Two players", 150, 280);
            g.drawString("Press 2: Vs computer", 150, 330);
            return;
        }

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

        // Draw scores
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));

        g.drawString(Integer.toString(world.leftScore()), 350, 50);
        g.drawString(Integer.toString(world.rightScore()), 430, 50);

        // Draw energy bars
        // Outline
        g.setColor(Color.WHITE);
        g.drawRect(20, 570, 200, 20);

        g.setColor(Color.WHITE);
        g.drawRect(580, 570, 200, 20);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));

        Energy leftEnergy = world.leftEnergy();
        g.fillRect(20, 570, (int) (leftEnergy.amount() / 10 * 200), 20);
        g.drawString(String.format("%.1f", leftEnergy.amount()), 240, 570 + 20);
        g.setColor(Color.BLACK);
        for (int i = 1; i <= 9; i++) {
            g.drawLine(20 + i * 20, 570, 20 + i * 20, 570 + 20);
        }

        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        Energy rightEnergy = world.rightEnergy();
        g.fillRect(580, 570, (int) (rightEnergy.amount() / 10 * 200), 20);
        g.drawString(String.format("%.1f", leftEnergy.amount()), 540, 570 + 20);
        g.setColor(Color.BLACK);
        for (int i = 1; i <= 9; i++) {
            g.drawLine(580 + i * 20, 570, 580 + i * 20, 570 + 20);
        }

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

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        repaint();
    }
}
