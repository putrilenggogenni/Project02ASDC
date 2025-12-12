import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DiceGameGUI extends JFrame {
    private GameEngine gameEngine;
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
        JDialog setupDialog = new JDialog(this, "🎲 Game Setup", true);
        setupDialog.setLayout(new BorderLayout(20, 20));
        setupDialog.setBackground(new Color(250, 250, 255));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(new Color(250, 250, 255));

        JLabel titleLabel = new JLabel("🎲 Dice & Ladders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(90, 115, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel playersLabel = new JLabel("Number of Players:");
        playersLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(playersLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JComboBox<Integer> playerCountCombo = new JComboBox<>(new Integer[]{2, 3, 4, 5, 6, 7, 8});
        playerCountCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        playerCountCombo.setMaximumSize(new Dimension(200, 40));
        playerCountCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleComboBox(playerCountCombo);
        mainPanel.add(playerCountCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel namesPanel = new JPanel();
        namesPanel.setLayout(new BoxLayout(namesPanel, BoxLayout.Y_AXIS));
        namesPanel.setBackground(new Color(250, 250, 255));

        JTextField[] nameFields = new JTextField[8];
        for (int i = 0; i < 8; i++) {
            JPanel playerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            playerRow.setBackground(new Color(250, 250, 255));

            JLabel playerLabel = new JLabel("Player " + (i + 1) + ":");
            playerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            playerLabel.setPreferredSize(new Dimension(80, 30));

            nameFields[i] = new JTextField("Player " + (i + 1));
            nameFields[i].setFont(new Font("Arial", Font.PLAIN, 14));
            nameFields[i].setPreferredSize(new Dimension(200, 35));
            styleTextField(nameFields[i]);

            playerRow.add(playerLabel);
            playerRow.add(nameFields[i]);
            namesPanel.add(playerRow);

            if (i >= 2) {
                playerRow.setVisible(false);
            }
        }

        playerCountCombo.addActionListener(e -> {
            int count = (Integer) playerCountCombo.getSelectedItem();
            for (int i = 0; i < 8; i++) {
                namesPanel.getComponent(i).setVisible(i < count);
            }
            setupDialog.pack();
        });

        JScrollPane namesScroll = new JScrollPane(namesPanel);
        namesScroll.setBorder(null);
        namesScroll.setBackground(new Color(250, 250, 255));
        namesScroll.setPreferredSize(new Dimension(350, 250));
        mainPanel.add(namesScroll);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton startButton = new JButton("START GAME");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setPreferredSize(new Dimension(250, 50));
        startButton.setMaximumSize(new Dimension(250, 50));
        styleButton(startButton, new Color(90, 115, 150), new Color(170, 190, 230));

        startButton.addActionListener(e -> {
            int numPlayers = (Integer) playerCountCombo.getSelectedItem();
            gameEngine = new GameEngine(numPlayers);

            for (int i = 0; i < numPlayers; i++) {
                String name = nameFields[i].getText().trim();
                if (!name.isEmpty()) {
                    gameEngine.getAllPlayers().get(i).setName(name);
                }
            }

            setupDialog.dispose();
            initializeGame();
            SoundManager.getInstance().startBackgroundMusic();
        });

        mainPanel.add(startButton);

        setupDialog.add(mainPanel, BorderLayout.CENTER);
        setupDialog.pack();
        setupDialog.setLocationRelativeTo(null);
        setupDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setupDialog.setVisible(true);
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

    private void initializeGame() {
        setTitle("🎲 Dice & Ladders Game - Enhanced Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 248, 255));

        boardPanel = new GameBoardPanel(gameEngine);
        add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.EAST);

        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        updatePlayerInfo();
        updateCurrentPlayerLabel();
        updateScores();
        updatePathDistance();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(340, 0));
        panel.setBackground(new Color(248, 249, 250));

        currentPlayerLabel = new JLabel("Current: Player 1");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currentPlayerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(currentPlayerLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        currentScoreLabel = new JLabel("Score: 0");
        currentScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        currentScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(currentScoreLabel);

        highScoreLabel = new JLabel("🏆 High Score: 0");
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        highScoreLabel.setForeground(new Color(255, 140, 0));
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(highScoreLabel);

        pathDistanceLabel = new JLabel("📍 Distance to Finish: 0");
        pathDistanceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        pathDistanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(pathDistanceLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        dicePanel = new DicePanel();
        dicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(dicePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        playButton = new JButton("ROLL DICE");
        playButton.setFont(new Font("Arial", Font.BOLD, 18));
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.setPreferredSize(new Dimension(180, 50));
        playButton.setMaximumSize(new Dimension(180, 50));
        styleButton(playButton, new Color(170, 190, 230), new Color(90, 115, 150));
        playButton.addActionListener(e -> playTurn());
        panel.add(playButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        resetButton = new JButton("RESET GAME");
        resetButton.setFont(new Font("Arial", Font.BOLD, 13));
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.setPreferredSize(new Dimension(180, 35));
        resetButton.setMaximumSize(new Dimension(180, 35));
        styleButton(resetButton, new Color(108, 117, 125), new Color(88, 97, 105));
        resetButton.addActionListener(e -> resetGame());
        panel.add(resetButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Sound controls
        JPanel soundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        soundPanel.setBackground(new Color(248, 249, 250));

        musicCheckBox = new JCheckBox("🎵 Music", true);
        musicCheckBox.setFont(new Font("Arial", Font.PLAIN, 11));
        musicCheckBox.addActionListener(e ->
                SoundManager.getInstance().setMusicEnabled(musicCheckBox.isSelected()));

        soundCheckBox = new JCheckBox("🔊 Sound", true);
        soundCheckBox.setFont(new Font("Arial", Font.PLAIN, 11));
        soundCheckBox.addActionListener(e ->
                SoundManager.getInstance().setSoundEnabled(soundCheckBox.isSelected()));

        soundPanel.add(musicCheckBox);
        soundPanel.add(soundCheckBox);
        panel.add(soundPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new BoxLayout(playerInfoPanel, BoxLayout.Y_AXIS));
        playerInfoPanel.setBackground(Color.WHITE);
        playerInfoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                "Players & Scores",
                0, 0,
                new Font("Arial", Font.BOLD, 13),
                new Color(60, 60, 60)
        ));
        JScrollPane playerScrollPane = new JScrollPane(playerInfoPanel);
        playerScrollPane.setPreferredSize(new Dimension(320, 150));
        playerScrollPane.setBorder(null);
        panel.add(playerScrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        leaderboardPanel = new LeaderboardPanel();
        panel.add(leaderboardPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        moveHistoryArea = new JTextArea();
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        moveHistoryArea.setBackground(new Color(255, 255, 255));
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setPreferredSize(new Dimension(320, 200));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                "Move History",
                0, 0,
                new Font("Arial", Font.BOLD, 13),
                new Color(60, 60, 60)
        ));
        panel.add(scrollPane);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(248, 249, 250));

        statusLabel = new JLabel("Game in progress - Roll the dice!");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
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
        pathDistanceLabel.setText("📍 Min Moves to Finish: " + distance);
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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                "🏆 Leaderboard",
                0, 0,
                new Font("Arial", Font.BOLD, 13),
                new Color(60, 60, 60)
        ));
        setPreferredSize(new Dimension(320, 80));
        setMaximumSize(new Dimension(320, 80));

        topScoreLabel = new JLabel("👑 Top Score: N/A");
        topScoreLabel.setFont(new Font("Arial", Font.BOLD, 12));
        topScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        fastestTimeLabel = new JLabel("⚡ Fastest: N/A");
        fastestTimeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        fastestTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createRigidArea(new Dimension(0, 5)));
        add(topScoreLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(fastestTimeLabel);
    }

    public void updateLeaderboard(Leaderboard leaderboard) {
        if (leaderboard.getHighestScore() > 0) {
            topScoreLabel.setText("👑 Top: " + leaderboard.getHighestScorePlayer() +
                    " - " + leaderboard.getHighestScore() + " pts");
        }

        if (leaderboard.getFastestTime() != Long.MAX_VALUE) {
            fastestTimeLabel.setText("⚡ Fastest: " + leaderboard.getFastestPlayer() +
                    " - " + leaderboard.getFormattedFastestTime());
        }
    }
}