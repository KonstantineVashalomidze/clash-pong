package com.github.konstantinevashalomidze;

import com.github.konstantinevashalomidze.pong.model.*;
import com.github.konstantinevashalomidze.pong.ui.GamePanel;

import javax.swing.*;
import java.awt.*;

public class Main {
    static void main() {
        EventQueue.invokeLater(() -> {
            int width = 800, height = 600;
            JFrame frame = new JFrame("Pong");

            World world = new World(
                    new Playfield(800, 600),
                    new Ball(new Vec2(400, 300), new Vec2(200, 120), 8),
                    new Paddle(new Vec2(30, 300), 10, 80),
                    new Paddle(new Vec2(770, 300), 10, 80),
                    0,
                    0
            );

            GamePanel gamePanel = new GamePanel(world);
            gamePanel.setPreferredSize(new Dimension(width, height));

            Timer loop = new Timer(16, (e) -> {
                gamePanel.setWorld(gamePanel.getWorld().step(0.016, gamePanel.getActionKeys()));
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
