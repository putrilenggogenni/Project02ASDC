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

        // Title
        JLabel titleLabel = new JLabel("🎲 Dice & Ladders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(90, 115, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Number of players
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

        // Player names panel
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

        // Start button
        JButton startButton = new JButton("🚀 START GAME");
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
        setTitle("🎲 Dice & Ladders Game");
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
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBackground(new Color(248, 249, 250));

        currentPlayerLabel = new JLabel("Current: Player 1");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currentPlayerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(currentPlayerLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Score labels
        currentScoreLabel = new JLabel("Score: 0");
        currentScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        currentScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(currentScoreLabel);

        highScoreLabel = new JLabel("🏆 High Score: 0");
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        highScoreLabel.setForeground(new Color(255, 140, 0));
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(highScoreLabel);
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
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

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
        playerScrollPane.setPreferredSize(new Dimension(300, 180));
        playerScrollPane.setBorder(null);
        panel.add(playerScrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        moveHistoryArea = new JTextArea();
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        moveHistoryArea.setBackground(new Color(255, 255, 255));
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setPreferredSize(new Dimension(300, 250));
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

                Ladder usedLadder = null;
                if (record.isForward()) {
                    usedLadder = boardPanel.checkForLadder(record.getToPosition());
                    if (usedLadder != null) {
                        boardPanel.setClimbingLadder(usedLadder);
                        movedPlayer.setPosition(usedLadder.to);
                        record.setLadder(usedLadder.from, usedLadder.to);
                        SoundManager.getInstance().playSound("ladder");
                    }
                }

                final Player finalMovedPlayer = movedPlayer;
                final Ladder finalUsedLadder = usedLadder;

                updateMoveHistory(record);

                if (finalMovedPlayer != null) {
                    boardPanel.animateMove(finalMovedPlayer);
                }

                boolean doubleTurn = (record.getToPosition() % 5 == 0 &&
                        record.getToPosition() > 0 &&
                        record.getToPosition() < 64);

                Timer delayTimer = new Timer(finalUsedLadder != null ? 800 : 500, e -> {
                    boardPanel.setClimbingLadder(null);
                    updatePlayerInfo();
                    updateCurrentPlayerLabel();
                    updateScores();

                    if (gameEngine.isGameOver()) {
                        SoundManager.getInstance().playSound("win");
                        showWinnerDialog();
                    } else {
                        if (finalUsedLadder != null) {
                            statusLabel.setText("🪜 " + finalMovedPlayer.getName() +
                                    " climbed a ladder! (" + finalUsedLadder.from + "→" + finalUsedLadder.to + ")");
                            statusLabel.setForeground(new Color(255, 140, 0));
                        } else if (doubleTurn) {
                            statusLabel.setText("⚡ DOUBLE TURN! " + finalMovedPlayer.getName() + " goes again!");
                            statusLabel.setForeground(new Color(138, 43, 226));
                        } else {
                            statusLabel.setText("Game in progress - Roll the dice!");
                            statusLabel.setForeground(Color.BLACK);
                        }
                        playButton.setEnabled(true);
                    }
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        });
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
            JLabel playerLabel = new JLabel(String.format("%s #%d - %s: %d points",
                    medal, i + 1, p.getName(), p.getPoints()));
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

        String entry = String.format("%s: 🎲 Dice=%d, Prob=%.2f %s\n   %s %d steps (%d→%d) +%d pts%s\n\n",
                record.getPlayerName(),
                record.getDiceRoll(),
                record.getProbability(),
                direction,
                color,
                Math.abs(record.getStepsMoved()),
                record.getFromPosition(),
                record.getToPosition(),
                record.getPointsEarned(),
                record.hasLadder() ? " 🪜 LADDER!" : ""
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

            JLabel label = new JLabel(String.format("%s - Tile %d - %d pts",
                    player.getName(), player.getPosition(), player.getPoints()));
            label.setFont(new Font("Arial", Font.BOLD, 13));

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
        highScoreLabel.setText("🏆 High Score: " + gameEngine.getHighestScore());
    }

    private void resetGame() {
        gameEngine.resetGame();
        moveHistoryArea.setText("");
        statusLabel.setText("Game in progress - Roll the dice!");
        statusLabel.setForeground(Color.BLACK);
        playButton.setEnabled(true);
        dicePanel.reset();
        boardPanel.setClimbingLadder(null);

        updatePlayerInfo();
        updateCurrentPlayerLabel();
        updateScores();
        boardPanel.repaint();

        for (Player player : gameEngine.getAllPlayers()) {
            boardPanel.animateMove(player);
        }
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