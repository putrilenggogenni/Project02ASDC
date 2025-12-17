import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DiceGameGUI extends JFrame {
    public GameEngine gameEngine;
    private GameBoardPanel boardPanel;
    private JButton playButton;
    private JButton resetButton;
    private JTextArea moveHistoryArea;
    private JLabel statusLabel;
    private JLabel currentPlayerLabel;
    private JPanel playerInfoPanel;
    private DicePanel dicePanel;
    private JLabel highScoreLabel;
    private JLabel currentScoreLabel;
    private JLabel pathDistanceLabel;
    private LeaderboardPanel leaderboardPanel;
    private JCheckBox musicCheckBox;
    private JCheckBox soundCheckBox;

    public DiceGameGUI() {
        showModernSetupDialog();
    }

    private void showModernSetupDialog() {
        LadderFallSetup.showSetupDialog(this);
    }

    private void styleButton(JButton button, Color color1, Color color2) {
        button.setBackground(color1);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color2);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color1);
            }
        });
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.BLACK);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleTextField(JTextField textField) {
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        textField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(90, 115, 150), 2),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 210), 2),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }

    public void initializeGame() {
        setTitle("🪜 LadderFall - Nature Adventure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        // Set background to nature theme
        getContentPane().setBackground(new Color(100, 140, 80));

        boardPanel = new GameBoardPanel(gameEngine);
        add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = createThemedControlPanel();
        add(controlPanel, BorderLayout.EAST);

        JPanel statusPanel = createThemedStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        updatePlayerInfo();
        updateCurrentPlayerLabel();
        updateScores();
        updatePathDistance();
    }

    private JPanel createThemedControlPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Forest green gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(40, 70, 50),
                        0, getHeight(), new Color(30, 60, 40)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Add subtle texture
                g2d.setColor(new Color(50, 80, 60, 30));
                for (int i = 0; i < 100; i++) {
                    int x = (int)(Math.random() * getWidth());
                    int y = (int)(Math.random() * getHeight());
                    g2d.fillOval(x, y, 2, 4);
                }
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(360, 0));

        // Title label
        JLabel titleLabel = new JLabel("🪜 LadderFall") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                FontMetrics fm = g2d.getFontMetrics(getFont());
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = fm.getAscent();

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.setFont(getFont());
                g2d.drawString(getText(), x + 2, y + 2);

                // Main text
                g2d.setColor(new Color(255, 255, 200));
                g2d.drawString(getText(), x, y);
            }
        };
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Current player info with themed box
        JPanel currentPlayerBox = createThemedInfoBox();
        currentPlayerBox.setLayout(new BoxLayout(currentPlayerBox, BoxLayout.Y_AXIS));
        currentPlayerBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        currentPlayerLabel = new JLabel("Current: Player 1");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currentPlayerLabel.setForeground(Color.WHITE);
        currentPlayerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentPlayerBox.add(currentPlayerLabel);

        currentPlayerBox.add(Box.createRigidArea(new Dimension(0, 5)));

        currentScoreLabel = new JLabel("Score: 0");
        currentScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        currentScoreLabel.setForeground(new Color(255, 255, 200));
        currentScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentPlayerBox.add(currentScoreLabel);

        currentPlayerBox.add(Box.createRigidArea(new Dimension(0, 5)));

        highScoreLabel = new JLabel("🏆 High Score: 0");
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        highScoreLabel.setForeground(new Color(255, 215, 0));
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentPlayerBox.add(highScoreLabel);

        currentPlayerBox.add(Box.createRigidArea(new Dimension(0, 5)));

        pathDistanceLabel = new JLabel("📍 Distance: 0");
        pathDistanceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        pathDistanceLabel.setForeground(new Color(200, 255, 200));
        pathDistanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentPlayerBox.add(pathDistanceLabel);

        panel.add(currentPlayerBox);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Dice panel
        dicePanel = new DicePanel();
        dicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(dicePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Themed buttons
        playButton = createThemedButton("🎲 ROLL DICE", new Color(34, 139, 34));
        playButton.addActionListener(e -> playTurn());
        panel.add(playButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        resetButton = createThemedButton("🔄 RESET", new Color(178, 34, 34));
        resetButton.addActionListener(e -> resetGame());
        panel.add(resetButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Sound controls with themed styling
        JPanel soundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        soundPanel.setOpaque(false);

        musicCheckBox = new JCheckBox("🎵 Music", true);
        musicCheckBox.setFont(new Font("Arial", Font.BOLD, 12));
        musicCheckBox.setForeground(Color.WHITE);
        musicCheckBox.setOpaque(false);
        musicCheckBox.setFocusPainted(false);
        musicCheckBox.addActionListener(e ->
                SoundManager.getInstance().setMusicEnabled(musicCheckBox.isSelected()));

        soundCheckBox = new JCheckBox("🔊 Sound", true);
        soundCheckBox.setFont(new Font("Arial", Font.BOLD, 12));
        soundCheckBox.setForeground(Color.WHITE);
        soundCheckBox.setOpaque(false);
        soundCheckBox.setFocusPainted(false);
        soundCheckBox.addActionListener(e ->
                SoundManager.getInstance().setSoundEnabled(soundCheckBox.isSelected()));

        soundPanel.add(musicCheckBox);
        soundPanel.add(soundCheckBox);
        panel.add(soundPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Player info panel with themed styling
        playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new BoxLayout(playerInfoPanel, BoxLayout.Y_AXIS));
        playerInfoPanel.setBackground(new Color(25, 50, 35));
        playerInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 200, 120), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane playerScrollPane = new JScrollPane(playerInfoPanel);
        playerScrollPane.setPreferredSize(new Dimension(330, 150));
        playerScrollPane.setBorder(null);
        playerScrollPane.setOpaque(false);
        playerScrollPane.getViewport().setOpaque(false);
        panel.add(playerScrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Leaderboard
        leaderboardPanel = new LeaderboardPanel();
        panel.add(leaderboardPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Move history
        moveHistoryArea = new JTextArea();
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        moveHistoryArea.setBackground(new Color(25, 50, 35));
        moveHistoryArea.setForeground(new Color(200, 255, 200));
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setPreferredSize(new Dimension(330, 180));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 200, 120), 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        panel.add(scrollPane);

        return panel;
    }
    private JPanel createThemedInfoBox() {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Semi-transparent background
                g2d.setColor(new Color(20, 40, 30, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Border
                g2d.setColor(new Color(100, 200, 120));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        box.setMaximumSize(new Dimension(330, 150));
        return box;
    }

    private JButton createThemedButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Glow
                for (int i = 8; i > 0; i--) {
                    g2d.setColor(new Color(
                            baseColor.getRed(),
                            baseColor.getGreen(),
                            baseColor.getBlue(),
                            30 - i * 3
                    ));
                    g2d.fillRoundRect(-i, -i, w + i * 2, h + i * 2, 25, 25);
                }

                // Button gradient
                GradientPaint gradient = new GradientPaint(
                        0, 0, baseColor.brighter(),
                        0, h, baseColor.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, w, h, 25, 25);

                // Shine
                GradientPaint shine = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 80),
                        0, h / 2, new Color(255, 255, 255, 0)
                );
                g2d.setPaint(shine);
                g2d.fillRoundRect(0, 0, w, h / 2, 25, 25);

                // Border
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, w - 2, h - 2, 25, 25);

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }


    private JPanel createThemedStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Dark green gradient
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(30, 60, 40),
                        0, getHeight(), new Color(20, 50, 30)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 200, 120), 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        statusLabel = new JLabel("Game in progress - Roll the dice!");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(new Color(255, 255, 200));
        panel.add(statusLabel);

        return panel;
    }

    private void playTurn() {
        if (gameEngine.isGameOver()) {
            return;
        }

        playButton.setEnabled(false);
        SoundManager.getInstance().playSound("dice_roll");

        dicePanel.rollDice(() -> {
            MoveRecord record = gameEngine.playTurn();

            if (record != null) {
                boolean isGreen = record.getProbability() <= 0.7;
                dicePanel.showResult(record.getDiceRoll(), isGreen);

                Player movedPlayer = null;
                for (Player p : gameEngine.getAllPlayers()) {
                    if (p.getName().equals(record.getPlayerName())) {
                        movedPlayer = p;
                        break;
                    }
                }

                final Player finalMovedPlayer = movedPlayer;
                updateMoveHistory(record);

                // Animate step-by-step movement
                if (finalMovedPlayer != null) {
                    List<Integer> path = gameEngine.getPathfinder().getStepByStepPath(
                            record.getFromPosition(), record.getToPosition());
                    boardPanel.animateStepByStep(finalMovedPlayer, path, () -> {
                        // After movement animation, check for ladder IMMEDIATELY
                        Ladder usedLadder = boardPanel.checkForLadder(record.getToPosition());
                        if (usedLadder != null && record.isForward()) {
                            // Highlight the ladder being climbed
                            boardPanel.setClimbingLadder(usedLadder);

                            // Update player position and record
                            finalMovedPlayer.setPosition(usedLadder.to);
                            record.setLadder(usedLadder.from, usedLadder.to);
                            SoundManager.getInstance().playSound("ladder");

                            // INSTANT CLIMB - teleport directly from bottom to top
                            boardPanel.animateInstantClimb(finalMovedPlayer, usedLadder.from, usedLadder.to, () -> {
                                // After instant climb animation completes
                                boardPanel.setClimbingLadder(null);
                                finalizeTurn(record, finalMovedPlayer, usedLadder);
                            });
                        } else {
                            finalizeTurn(record, finalMovedPlayer, null);
                        }
                    });
                }
            }
        });
    }

    private void finalizeTurn(MoveRecord record, Player movedPlayer, Ladder usedLadder) {
        updatePlayerInfo();
        updateCurrentPlayerLabel();
        updateScores();
        updatePathDistance();
        leaderboardPanel.updateLeaderboard(gameEngine.getLeaderboard());

        boolean doubleTurn = (record.getToPosition() % 5 == 0 &&
                record.getToPosition() > 0 &&
                record.getToPosition() < 64);

        if (gameEngine.isGameOver()) {
            SoundManager.getInstance().playSound("win");
            showWinnerDialog();
        } else {
            if (usedLadder != null) {
                String ladderMsg = String.format("🪜 %s climbed a ladder! (%d→%d)",
                        movedPlayer.getName(), usedLadder.from, usedLadder.to);
                statusLabel.setText(ladderMsg);
                statusLabel.setForeground(new Color(255, 140, 0));
                updateMoveHistory(record);
            } else if (doubleTurn) {
                SoundManager.getInstance().playSound("bonus");
                statusLabel.setText("DOUBLE TURN! " + movedPlayer.getName() + " goes again!");
                statusLabel.setForeground(new Color(138, 43, 226));
            } else {
                statusLabel.setText("Game in progress - Roll the dice!");
                statusLabel.setForeground(Color.BLACK);
            }
            playButton.setEnabled(true);
        }
    }

    private void showWinnerDialog() {
        List<Player> ranked = gameEngine.getRankedPlayers();

        JDialog winDialog = new JDialog(this, "🏆 Game Over!", true);
        winDialog.setLayout(new BorderLayout(20, 20));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        contentPanel.setBackground(new Color(255, 250, 240));

        JLabel winnerLabel = new JLabel("🎉 " + ranked.get(0).getName() + " WINS! 🎉");
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        winnerLabel.setForeground(new Color(255, 140, 0));
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(winnerLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel rankingsLabel = new JLabel("🏆 Final Rankings 🏆");
        rankingsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        rankingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(rankingsLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        for (int i = 0; i < ranked.size(); i++) {
            Player p = ranked.get(i);
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "  ";
            String time = p.getCompletionTime() > 0 ? " [" + p.getFormattedCompletionTime() + "]" : "";
            JLabel playerLabel = new JLabel(String.format("%s #%d - %s: %d pts%s",
                    medal, i + 1, p.getName(), p.getPoints(), time));
            playerLabel.setFont(new Font("Arial", Font.BOLD, 16));
            playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(playerLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleButton(closeButton, new Color(76, 175, 80), new Color(56, 155, 60));
        closeButton.addActionListener(e -> winDialog.dispose());
        contentPanel.add(closeButton);

        winDialog.add(contentPanel);
        winDialog.pack();
        winDialog.setLocationRelativeTo(this);
        winDialog.setVisible(true);

        playButton.setEnabled(false);
        statusLabel.setText("🎉 " + ranked.get(0).getName() + " WINS! 🏆");
        statusLabel.setForeground(new Color(0, 150, 0));
    }

    private void updateMoveHistory(MoveRecord record) {
        String direction = record.isForward() ? "✅ GREEN" : "❌ RED";
        String color = record.isForward() ? "Forward" : "Backward";

        String entry = String.format("%s: 🎲=%d, P=%.2f %s\n   %s %d (%d→%d) +%d pts%s\n\n",
                record.getPlayerName(),
                record.getDiceRoll(),
                record.getProbability(),
                direction,
                color,
                Math.abs(record.getStepsMoved()),
                record.getFromPosition(),
                record.getToPosition(),
                record.getPointsEarned(),
                record.hasLadder() ? " 🪜" : ""
        );

        moveHistoryArea.insert(entry, 0);
    }

    private void updatePlayerInfo() {
        playerInfoPanel.removeAll();

        List<Player> ranked = gameEngine.getRankedPlayers();

        for (Player player : ranked) {
            JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            playerPanel.setBackground(Color.WHITE);

            JPanel colorBox = new JPanel();
            colorBox.setPreferredSize(new Dimension(18, 18));
            colorBox.setBackground(Color.decode(player.getColor()));
            colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            JLabel label = new JLabel(String.format("%s - T:%d P:%d",
                    player.getName(), player.getPosition(), player.getPoints()));
            label.setFont(new Font("Arial", Font.BOLD, 12));

            playerPanel.add(colorBox);
            playerPanel.add(label);
            playerInfoPanel.add(playerPanel);
        }

        playerInfoPanel.revalidate();
        playerInfoPanel.repaint();
    }

    private void updateCurrentPlayerLabel() {
        if (!gameEngine.isGameOver()) {
            Player current = gameEngine.getCurrentPlayer();
            currentPlayerLabel.setText("▶ Current: " + current.getName());
            currentPlayerLabel.setForeground(Color.decode(current.getColor()));
        }
    }

    private void updateScores() {
        Player current = gameEngine.getCurrentPlayer();
        currentScoreLabel.setText("Score: " + current.getPoints());
        highScoreLabel.setText("High: " + gameEngine.getHighestScore());
    }

    private void updatePathDistance() {
        Player current = gameEngine.getCurrentPlayer();
        int distance = gameEngine.getPathfinder().shortestDistance(current.getPosition(), 64);
        pathDistanceLabel.setText("Min Moves to Finish: " + distance);
    }

    private void resetGame() {
        gameEngine.resetGame();
        moveHistoryArea.setText("");
        statusLabel.setText("Game in progress - Roll the dice!");
        statusLabel.setForeground(Color.BLACK);
        playButton.setEnabled(true);
        dicePanel.reset();
        boardPanel.updateLadders(gameEngine.getLadders());
        boardPanel.setClimbingLadder(null);

        // Reset all player visual positions to start (position 1)
        boardPanel.resetAllPlayerPositions();

        updatePlayerInfo();
        updateCurrentPlayerLabel();
        updateScores();
        updatePathDistance();
        leaderboardPanel.updateLeaderboard(gameEngine.getLeaderboard());
        boardPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new DiceGameGUI();
        });
    }
}

class LeaderboardPanel extends JPanel {
    private JLabel topScoreLabel;
    private JLabel fastestTimeLabel;

    public LeaderboardPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 200, 120), 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        setPreferredSize(new Dimension(330, 90));
        setMaximumSize(new Dimension(330, 90));

        topScoreLabel = new JLabel("👑 Top Score: N/A");
        topScoreLabel.setFont(new Font("Arial", Font.BOLD, 13));
        topScoreLabel.setForeground(new Color(255, 215, 0));
        topScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        fastestTimeLabel = new JLabel("⚡ Fastest: N/A");
        fastestTimeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        fastestTimeLabel.setForeground(new Color(150, 255, 150));
        fastestTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createRigidArea(new Dimension(0, 5)));
        add(topScoreLabel);
        add(Box.createRigidArea(new Dimension(0, 8)));
        add(fastestTimeLabel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dark background
        g2d.setColor(new Color(25, 50, 35, 200));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
    }

    public void updateLeaderboard(Leaderboard leaderboard) {
        if (leaderboard.getHighestScore() > 0) {
            topScoreLabel.setText("👑 Top: " + leaderboard.getHighestScorePlayer() +
                    " - " + leaderboard.getHighestScore() + " pts!");
        }

        if (leaderboard.getFastestTime() != Long.MAX_VALUE) {
            fastestTimeLabel.setText("⚡ Fastest: " + leaderboard.getFastestPlayer() +
                    " - " + leaderboard.getFormattedFastestTime());
        }
    }
}