import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class ColorfulSetupDialog {

    /**
     * Replace the showModernSetupDialog() method in DiceGameGUI.java with this version
     */
    public static void showColorfulSetupDialog(DiceGameGUI parent) {
        JDialog setupDialog = new JDialog(parent, "🎲 Game Setup", true);
        setupDialog.setLayout(new BorderLayout());
        setupDialog.setUndecorated(true);
        setupDialog.setBackground(new Color(0, 0, 0, 0));

        // Main panel with custom painting
        JPanel mainPanel = new AnimatedSetupPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        // Title with animated dice icons
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JLabel diceIcon1 = createAnimatedDiceLabel();
        JLabel titleLabel = new JLabel("Dice & Ladders");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 42));
        titleLabel.setForeground(new Color(255, 100, 150));
        JLabel diceIcon2 = createAnimatedDiceLabel();

        titlePanel.add(diceIcon1);
        titlePanel.add(titleLabel);
        titlePanel.add(diceIcon2);

        mainPanel.add(titlePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Subtitle with ladder emoji
        JLabel subtitleLabel = new JLabel("🪜 Ready to Climb? 🪜");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        subtitleLabel.setForeground(new Color(100, 180, 255));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Player count selector with icons
        JPanel playerSelectPanel = new JPanel();
        playerSelectPanel.setOpaque(false);
        playerSelectPanel.setLayout(new BoxLayout(playerSelectPanel, BoxLayout.Y_AXIS));

        JLabel playersLabel = new JLabel("👥 Number of Players:");
        playersLabel.setFont(new Font("Arial", Font.BOLD, 18));
        playersLabel.setForeground(new Color(70, 70, 100));
        playersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerSelectPanel.add(playersLabel);
        playerSelectPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JComboBox<String> playerCountCombo = new JComboBox<>();
        for (int i = 2; i <= 8; i++) {
            playerCountCombo.addItem("🎮 " + i + " Players");
        }
        playerCountCombo.setFont(new Font("Arial", Font.BOLD, 16));
        playerCountCombo.setMaximumSize(new Dimension(250, 50));
        playerCountCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleColorfulComboBox(playerCountCombo);
        playerSelectPanel.add(playerCountCombo);

        mainPanel.add(playerSelectPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Player names panel with colorful backgrounds
        JPanel namesPanel = new JPanel();
        namesPanel.setLayout(new BoxLayout(namesPanel, BoxLayout.Y_AXIS));
        namesPanel.setOpaque(false);

        JTextField[] nameFields = new JTextField[8];
        Color[] playerColors = {
                new Color(255, 107, 107), // Red
                new Color(78, 205, 196),  // Cyan
                new Color(69, 183, 209),  // Blue
                new Color(255, 160, 122), // Orange
                new Color(152, 216, 200), // Mint
                new Color(247, 220, 111), // Yellow
                new Color(187, 143, 206), // Purple
                new Color(133, 193, 226)  // Light Blue
        };

        for (int i = 0; i < 8; i++) {
            JPanel playerRow = createColorfulPlayerRow(i, playerColors[i], nameFields);
            namesPanel.add(playerRow);
            namesPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            if (i >= 2) {
                playerRow.setVisible(false);
            }
        }

        playerCountCombo.addActionListener(e -> {
            String selected = (String) playerCountCombo.getSelectedItem();
            int count = Integer.parseInt(selected.replaceAll("[^0-9]", ""));
            for (int i = 0; i < 8; i++) {
                Component row = namesPanel.getComponent(i * 2);
                row.setVisible(i < count);
            }
            setupDialog.pack();
        });

        JScrollPane namesScroll = new JScrollPane(namesPanel);
        namesScroll.setBorder(null);
        namesScroll.setOpaque(false);
        namesScroll.getViewport().setOpaque(false);
        namesScroll.setPreferredSize(new Dimension(450, 300));
        mainPanel.add(namesScroll);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Animated start button
        JButton startButton = createAnimatedStartButton(setupDialog, playerCountCombo, nameFields, parent);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(startButton);

        // Close button
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        JButton closeButton = new JButton("✖ Exit");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.setPreferredSize(new Dimension(120, 40));
        closeButton.setMaximumSize(new Dimension(120, 40));
        styleButton(closeButton, new Color(200, 100, 100), new Color(255, 150, 150));
        closeButton.addActionListener(e -> System.exit(0));
        mainPanel.add(closeButton);

        setupDialog.add(mainPanel);
        setupDialog.pack();
        setupDialog.setLocationRelativeTo(null);
        setupDialog.setVisible(true);
    }

    private static JPanel createColorfulPlayerRow(int index, Color color, JTextField[] nameFields) {
        JPanel playerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        playerRow.setOpaque(true);
        playerRow.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
        playerRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 3, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Player icon
        JLabel iconLabel = new JLabel(getPlayerEmoji(index));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        // Player number
        JLabel playerLabel = new JLabel("Player " + (index + 1));
        playerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerLabel.setForeground(color.darker().darker());
        playerLabel.setPreferredSize(new Dimension(90, 30));

        // Name field
        nameFields[index] = new JTextField("Player " + (index + 1));
        nameFields[index].setFont(new Font("Arial", Font.PLAIN, 16));
        nameFields[index].setPreferredSize(new Dimension(200, 40));
        nameFields[index].setBackground(new Color(255, 255, 255, 220));
        nameFields[index].setForeground(color.darker());
        nameFields[index].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Color indicator
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(30, 30));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(color.darker(), 3, true));

        playerRow.add(iconLabel);
        playerRow.add(playerLabel);
        playerRow.add(nameFields[index]);
        playerRow.add(colorBox);

        return playerRow;
    }

    private static String getPlayerEmoji(int index) {
        String[] emojis = {"🎮", "🎯", "🎪", "🎨", "🎭", "🎸", "🎺", "🎹"};
        return emojis[index % emojis.length];
    }

    private static JLabel createAnimatedDiceLabel() {
        JLabel label = new JLabel("🎲");
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));

        Timer timer = new Timer(500, null);
        timer.addActionListener(e -> {
            String[] dice = {"⚀", "⚁", "⚂", "⚃", "⚄", "⚅"};
            label.setText(dice[(int)(Math.random() * 6)]);
        });
        timer.start();

        return label;
    }

    private static JButton createAnimatedStartButton(JDialog dialog, JComboBox<String> combo,
                                                     JTextField[] fields, DiceGameGUI parent) {
        // Create custom button with rainbow gradient
        RainbowButton button = new RainbowButton("🚀 START GAME 🚀");
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(300, 70));
        button.setMaximumSize(new Dimension(300, 70));
        button.setForeground(Color.WHITE);

        // Pulsing animation
        Timer pulseTimer = new Timer(50, e -> button.repaint());
        pulseTimer.start();

        button.addActionListener(e -> {
            pulseTimer.stop();
            String selected = (String) combo.getSelectedItem();
            int numPlayers = Integer.parseInt(selected.replaceAll("[^0-9]", ""));

            // Initialize game engine
            parent.gameEngine = new GameEngine(numPlayers);

            // Set player names
            for (int i = 0; i < numPlayers; i++) {
                String name = fields[i].getText().trim();
                if (!name.isEmpty()) {
                    parent.gameEngine.getAllPlayers().get(i).setName(name);
                }
            }

            dialog.dispose();
            parent.initializeGame();
            SoundManager.getInstance().startBackgroundMusic();
        });

        return button;
    }

    private static void styleColorfulComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(new Color(255, 255, 255, 200));
        comboBox.setForeground(new Color(70, 70, 100));
        comboBox.setFont(new Font("Arial", Font.BOLD, 16));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 180, 255), 3, true),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
    }

    private static void styleButton(JButton button, Color color1, Color color2) {
        button.setBackground(color1);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color2);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color1);
            }
        });
    }
}

/**
 * Animated background panel with floating dice and ladders
 */
class AnimatedSetupPanel extends JPanel {
    private java.util.List<FloatingIcon> floatingIcons;
    private Timer animationTimer;

    public AnimatedSetupPanel() {
        setOpaque(true);
        floatingIcons = new java.util.ArrayList<>();

        // Create floating icons
        for (int i = 0; i < 15; i++) {
            floatingIcons.add(new FloatingIcon());
        }

        // Animation timer
        animationTimer = new Timer(30, e -> {
            for (FloatingIcon icon : floatingIcons) {
                icon.update();
            }
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Multi-layer gradient background
        GradientPaint bg1 = new GradientPaint(
                0, 0, new Color(255, 240, 245),
                width, height, new Color(230, 240, 255)
        );
        g2d.setPaint(bg1);
        g2d.fillRect(0, 0, width, height);

        // Overlay gradient
        GradientPaint bg2 = new GradientPaint(
                width / 2, 0, new Color(255, 220, 240, 100),
                width / 2, height, new Color(220, 230, 255, 100)
        );
        g2d.setPaint(bg2);
        g2d.fillRect(0, 0, width, height);

        // Draw floating icons
        for (FloatingIcon icon : floatingIcons) {
            icon.draw(g2d, width, height);
        }

        // Border
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(new Color(150, 180, 255));
        g2d.drawRoundRect(2, 2, width - 4, height - 4, 30, 30);
    }
}

class FloatingIcon {
    private double x, y;
    private double vx, vy;
    private String icon;
    private float alpha;
    private double rotation;
    private double rotationSpeed;

    public FloatingIcon() {
        reset();
        y = Math.random() * 800; // Start at random position
    }

    private void reset() {
        x = Math.random() * 600;
        y = -50;
        vx = (Math.random() - 0.5) * 0.5;
        vy = 0.3 + Math.random() * 0.5;

        String[] icons = {"🎲", "🪜", "⭐", "🎮", "🎯", "🏆"};
        icon = icons[(int)(Math.random() * icons.length)];

        alpha = 0.3f + (float)Math.random() * 0.4f;
        rotation = Math.random() * Math.PI * 2;
        rotationSpeed = (Math.random() - 0.5) * 0.05;
    }

    public void update() {
        x += vx;
        y += vy;
        rotation += rotationSpeed;

        // Reset when off screen
        if (y > 900) {
            reset();
        }
    }

    public void draw(Graphics2D g2d, int panelWidth, int panelHeight) {
        if (x < 0 || x > panelWidth) return;

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        AffineTransform old = g2d.getTransform();
        g2d.translate(x, y);
        g2d.rotate(rotation);

        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        g2d.drawString(icon, -20, 20);

        g2d.setTransform(old);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}

/**
 * Custom rainbow gradient button
 */
class RainbowButton extends JButton {

    public RainbowButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setOpaque(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Animated gradient
        long time = System.currentTimeMillis();
        float hue1 = (time % 3000) / 3000f;
        float hue2 = ((time + 1500) % 3000) / 3000f;

        GradientPaint gradient = new GradientPaint(
                0, 0, Color.getHSBColor(hue1, 0.8f, 0.9f),
                width, height, Color.getHSBColor(hue2, 0.8f, 0.9f)
        );

        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, width, height, 25, 25);

        // Shine effect
        GradientPaint shine = new GradientPaint(
                0, 0, new Color(255, 255, 255, 100),
                0, height / 2, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(shine);
        g2d.fillRoundRect(0, 0, width, height / 2, 25, 25);

        // Border
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.drawRoundRect(1, 1, width - 2, height - 2, 25, 25);

        // Text
        super.paintComponent(g);
    }
}