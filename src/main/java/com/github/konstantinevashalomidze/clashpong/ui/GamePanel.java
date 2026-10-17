package com.github.konstantinevashalomidze.clashpong.ui;

import com.github.konstantinevashalomidze.clashpong.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static com.github.konstantinevashalomidze.clashpong.model.GameMode.HUMAN_VS_COMPUTER;
import static com.github.konstantinevashalomidze.clashpong.model.GameMode.HUMAN_VS_HUMAN;
import static java.awt.RenderingHints.*;

public class GamePanel extends JPanel {
    private static final List<String> LEFT_KEY_LABELS = List.of("F", "G", "H", "J");
    private static final List<String> RIGHT_KEY_LABELS = List.of("K", "L", ";", "'");

    private final Set<GameAction> actionKeys = new HashSet<>();
    private final Set<GameAction> actionPressedKeys = new HashSet<>();
    private World world;
    private GameMode gameMode;
    public GamePanel(Supplier<World> worldFactory) {
        this.world = worldFactory.get();

        setBackground(Color.BLACK);

        // Left movement
        bindKey("pressed W", "p1UpPress", () -> {
            if (gameMode != null) {
                actionKeys.add(GameAction.P1_UP);
            }
        });
        bindKey("released W", "p1UpRelease", () -> {
            if (gameMode != null) {
                actionKeys.remove(GameAction.P1_UP);
            }
        });

        bindKey("pressed S", "p1DownPress", () -> {
            if (gameMode != null) {
                actionKeys.add(GameAction.P1_DOWN);
            }
        });
        bindKey("released S", "p1DownRelease", () -> {
            if (gameMode != null) {
                actionKeys.remove(GameAction.P1_DOWN);
            }
        });

        // Right movement
        bindKey("pressed UP", "p2UpPress", () -> {
            if (gameMode == HUMAN_VS_HUMAN) {
                actionKeys.add(GameAction.P2_UP);
            }
        });
        bindKey("released UP", "p2UpRelease", () -> {
            if (gameMode == HUMAN_VS_HUMAN) {
                actionKeys.remove(GameAction.P2_UP);
            }
        });

        bindKey("pressed DOWN", "p2DownPress", () -> {
            if (gameMode == HUMAN_VS_HUMAN) {
                actionKeys.add(GameAction.P2_DOWN);
            }
        });
        bindKey("released DOWN", "p2DownRelease", () -> {
            if (gameMode == HUMAN_VS_HUMAN) {
                actionKeys.remove(GameAction.P2_DOWN);
            }
        });

        // Game mode select
        bindKey("pressed 1", "selectHumanVsHuman", () -> {
            if (gameMode == null) {
                gameMode = HUMAN_VS_HUMAN;
            }
        });
        bindKey("pressed 2", "selectHumanVsComputer", () -> {
            if (gameMode == null) {
                gameMode = HUMAN_VS_COMPUTER;
            }
        });

        // Left cards
        bindKey("pressed F", "p1Card1", () -> actionPressedKeys.add(GameAction.P1_CARD_1));
        bindKey("pressed G", "p1Card2", () -> actionPressedKeys.add(GameAction.P1_CARD_2));
        bindKey("pressed H", "p1Card3", () -> actionPressedKeys.add(GameAction.P1_CARD_3));
        bindKey("pressed J", "p1Card4", () -> actionPressedKeys.add(GameAction.P1_CARD_4));

        // Right cards
        bindKey("pressed K", "p2Card1", () -> {
            if (gameMode == HUMAN_VS_HUMAN) {
                actionPressedKeys.add(GameAction.P2_CARD_1);
            }
        });
        bindKey("pressed L", "p2Card2", () -> {
            if (gameMode == HUMAN_VS_HUMAN) {
                actionPressedKeys.add(GameAction.P2_CARD_2);
            }
        });
        bindKey("pressed SEMICOLON", "p2Card3", () -> {
            if (gameMode == HUMAN_VS_HUMAN) {
                actionPressedKeys.add(GameAction.P2_CARD_3);
            }
        });
        bindKey("pressed QUOTE", "p2Card4", () -> {
            if (gameMode == HUMAN_VS_HUMAN) {
                actionPressedKeys.add(GameAction.P2_CARD_4);
            }
        });

        // Play again
        bindKey("pressed R", "replay", () -> {
            if (world.winner().isPresent()) {
                actionKeys.clear();
                setWorld(worldFactory.get());
            }
        });

        // Left strafe
        bindKey("pressed A", "p1StrafeLeftPress", () -> {
            if (gameMode != null) {
                actionKeys.add(GameAction.P1_STRAFE_LEFT);
            }
        });
        bindKey("released A", "p1StrafeLeftRelease", () -> {
            if (gameMode != null) {
                actionKeys.remove(GameAction.P1_STRAFE_LEFT);
            }
        });

        bindKey("pressed D", "p1StrafeRightPress", () -> {
            if (gameMode != null) {
                actionKeys.add(GameAction.P1_STRAFE_RIGHT);
            }
        });
        bindKey("released D", "p1StrafeRightRelease", () -> {
            if (gameMode != null) {
                actionKeys.remove(GameAction.P1_STRAFE_RIGHT);
            }
        });

        // Right strafe
        bindKey("pressed LEFT", "p2StrafeLeftPress", () -> {
            if (gameMode == HUMAN_VS_HUMAN) {
                actionKeys.add(GameAction.P2_STRAFE_LEFT);
            }
        });
        bindKey("released LEFT", "p2StrafeLeftRelease", () -> {
            if (gameMode == HUMAN_VS_HUMAN) {
                actionKeys.remove(GameAction.P2_STRAFE_LEFT);
            }
        });

        bindKey("pressed RIGHT", "p2StrafeRightPress", () -> {
            if (gameMode == HUMAN_VS_HUMAN) {
                actionKeys.add(GameAction.P2_STRAFE_RIGHT);
            }
        });
        bindKey("released RIGHT", "p2StrafeRightRelease", () -> {
            if (gameMode == HUMAN_VS_HUMAN) {
                actionKeys.remove(GameAction.P2_STRAFE_RIGHT);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Make drawn strings smoother
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);

        // Draw menu
        if (gameMode == null) {
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
            g.drawString("Press 1: Two players", 150, 280);
            g.drawString("Press 2: Vs computer", 150, 330);
            drawMenuControls(g);
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

        // Bricks
        drawBricks(g, world.bricks());


        // Left paddle
        Paddle leftPaddle = world.effectiveLeft();
        int leftPaddleCenterX = (int) leftPaddle.center().x();
        int leftPaddleCenterY = (int) leftPaddle.center().y();
        int leftPaddleWidth = (int) leftPaddle.width();
        int leftPaddleHeight = (int) leftPaddle.height();

        // Right paddle
        Paddle rightPaddle = world.effectiveRight();
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

        g.drawString(Integer.toString(world.left().score()), 350, 50);
        g.drawString(Integer.toString(world.right().score()), 430, 50);

        // Draw energy bars
        // Outline
        g.setColor(Color.BLACK);
        g.drawRect(20, 660, 200, 20);

        g.setColor(Color.BLACK);
        g.drawRect(580, 660, 200, 20);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));

        g.setColor(Color.WHITE);
        Energy leftEnergy = world.left().energy();
        g.fillRect(20, 660, (int) (leftEnergy.amount() / 10 * 200), 20);
        g.drawString(String.valueOf((int) Math.floor(leftEnergy.amount())), 230, 660 + 20);
        for (int i = 1; i <= 9; i++) {
            g.setColor(Color.BLACK);
            g.drawLine(20 + i * 20, 660, 20 + i * 20, 660 + 20);
        }

        g.setColor(Color.WHITE);
        Energy rightEnergy = world.right().energy();
        g.fillRect(580, 660, (int) (rightEnergy.amount() / 10 * 200), 20);
        g.drawString(String.valueOf((int) Math.floor(rightEnergy.amount())), 555, 660 + 20);
        for (int i = 1; i <= 9; i++) {
            g.setColor(Color.BLACK);
            g.drawLine(580 + i * 20, 660, 580 + i * 20, 660 + 20);
        }

        g.setColor(Color.WHITE);
        g.drawLine(0, 600, getWidth(), 600);


        // Hand
        drawHand(g, world.left().hand(), 20, LEFT_KEY_LABELS, world.left().energy());
        drawHand(g, world.right().hand(), 485, RIGHT_KEY_LABELS, world.right().energy());

        // Draw effect hints
        drawEffects(g, world.left().effects(), 20);
        drawEffects(g, world.right().effects(), 485);

        // Show winner
        if (world.winner().isPresent()) {
            g.setColor(new Color(0, 0, 0, 170));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
            Side winner =  world.winner().get();
            String winnerLabel = winner + " PLAYER WINS";
            g.drawString(winnerLabel, (getWidth() - g.getFontMetrics().stringWidth(winnerLabel)) / 2, 300);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g.drawString("Press R to play again", (getWidth() - g.getFontMetrics().stringWidth(winnerLabel)) / 2, 350);
        }

        // For linux
        Toolkit.getDefaultToolkit().sync();

    }

    private void drawBricks(Graphics g, List<Brick> bricks) {
        g.setColor(Color.WHITE);
        for (var brick : bricks) {
            g.fillRect((int) brick.topLeftX(), (int) brick.topLeftY(), (int) brick.width(), (int) brick.height());
        }
    }

    private void drawHand(Graphics g, Hand hand, int startX, List<String> keyLabels, Energy energy) {
        g.setColor(Color.WHITE);
        var keyLabelFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
        var defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
        for (int i = 0; i < hand.cards().size(); i++) {
            if (!energy.canAfford(hand.cards().get(i).cost())) {
                g.setColor(Color.DARK_GRAY);
            }
            var cardX = startX + i * 75;
            var keyLabel = keyLabels.get(i);
            g.setFont(keyLabelFont);
            g.drawString(keyLabel, cardX + 4, 624);
            g.setFont(defaultFont);
            g.drawRect(startX + i * 75, 610, 70, 44);
            var cardName = hand.cards().get(i).name().replace('_', ' ').split(" ");
            g.drawString(cardName[0], cardX + 4, 638);
            if (cardName.length > 1) {
                g.drawString(cardName[1], cardX + 4, 650);
            }
            g.drawString(Integer.toString(hand.cards().get(i).cost()), cardX + 58, 625);
            g.setColor(Color.WHITE);
        }
    }

    private void bindKey(String keyStroke, String actionName, Runnable onTrigger) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyStroke), actionName);
        getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onTrigger.run();
            }
        });
    }

    private void drawEffects(Graphics g, List<Effect> effects, int startX) {
        for (var effect : effects) {
            StringBuilder sb = new StringBuilder(effect.card().name().replace('_', ' '))
                    .append(" ")
                    .append(String.format("%.1fs", effect.remainingSeconds()));
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            String effectLabel = sb.toString();
            g.drawString(effectLabel, startX, 705);
            startX += g.getFontMetrics().stringWidth(effectLabel) + 4;
        }
    }

    private void drawMenuControls(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        // Left
        g.drawString("Left player", 150, 420);
        g.drawString("Move: W / S", 150, 444);
        g.drawString("Strafe: A / D", 150, 468);
        g.drawString("Cards: F G H J", 150, 492);


        // Right
        g.drawString("Right player", 550, 420);
        g.drawString("Move: UP / DOWN arrows", 550, 444);
        g.drawString("Strafe: LEFT / RIGHT arrows", 550, 468);
        g.drawString("Cards: K L ; '", 550, 492);
    }

    public void setWorld(World world) {
        this.world = world;
        repaint();
    }

    public World getWorld() {
        return world;
    }

    public Set<GameAction> getActionKeys() {
        return actionKeys;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Set<GameAction> getActionPressedKeys() {
        return actionPressedKeys;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        repaint();
    }
}
