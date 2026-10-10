package com.github.konstantinevashalomidze;

import com.github.konstantinevashalomidze.pong.model.*;
import com.github.konstantinevashalomidze.pong.ui.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.konstantinevashalomidze.pong.model.Card.*;
import static com.github.konstantinevashalomidze.pong.model.GameMode.HUMAN_VS_COMPUTER;
import static com.github.konstantinevashalomidze.pong.model.GameMode.HUMAN_VS_HUMAN;

public class Main {
    static void main() {
        EventQueue.invokeLater(() -> {
            int width = 800, height = 720;
            JFrame frame = new JFrame("Pong");

            World world = new World(
                    new Playfield(800, 600),
                    new Ball(new Vec2(400, 300), new Vec2(200, 120), 8),
                    new Paddle(new Vec2(30, 300), 10, 80),
                    new Paddle(new Vec2(770, 300), 10, 80),
                    0,
                    0,
                    new Energy(5),
                    new Energy(5),
                    new Hand(List.of(BARRICADE, BIG_PADDLE, SHRINK, FREEZE),
                            List.of(SMASH, SLOW_FIELD, STRAFE, GOAL_SHIELD)),
                    new Hand(List.of(GOAL_SHIELD, STRAFE, SLOW_FIELD, SMASH),
                            List.of(FREEZE, SHRINK, BIG_PADDLE, BARRICADE)),
                    List.of(),
                    List.of()
            );

            GamePanel gamePanel = new GamePanel(world);
            gamePanel.setPreferredSize(new Dimension(width, height));

            Timer loop = new Timer(16, (e) -> {
                if (gamePanel.getGameMode() == HUMAN_VS_HUMAN) {
                    gamePanel.setWorld(gamePanel.getWorld().step(0.016, gamePanel.getActionKeys(), gamePanel.getActionPressedKeys()));
                } else if (gamePanel.getGameMode() == HUMAN_VS_COMPUTER) {
                    Set<String> copiedActionKeys = new HashSet<>(gamePanel.getActionKeys());
                    String computerAction = ComputerPlayer.makeAction(gamePanel.getWorld());
                    if (computerAction != null) {
                        copiedActionKeys.add(computerAction);
                    }
                    gamePanel.setWorld(gamePanel.getWorld().step(0.016, copiedActionKeys, gamePanel.getActionPressedKeys()));
                }
                gamePanel.getActionPressedKeys().clear();
            });

            frame.getContentPane().add(gamePanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            loop.start();

        });
    }
}
