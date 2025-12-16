import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class LadderFallSetup {

    /**
     * Replace the showModernSetupDialog() method in DiceGameGUI.java with this version
     * This creates a full-screen setup with the waterfall background
     */
    public static void showSetupDialog(DiceGameGUI parent) {
        JDialog setupDialog = new JDialog(parent, "LadderFall Setup", true);
        setupDialog.setLayout(new BorderLayout());
        setupDialog.setUndecorated(true);

        // Get the same size as the game window (or full screen)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1200, screenSize.width);
        int height = Math.min(800, screenSize.height);
        setupDialog.setSize(width, height);

        // Start setup music
        SoundManager.getInstance().playSetupMusic();

        // Main panel with waterfall background
        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new BorderLayout());

        // Semi-transparent overlay for better readability
        JPanel overlayPanel = new JPanel();
        overlayPanel.setOpaque(false);
        overlayPanel.setLayout(new BoxLayout(overlayPanel, BoxLayout.Y_AXIS));
        overlayPanel.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80));

        // Add some spacing at the top
        overlayPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Title Panel with game name
        JPanel titlePanel = createTitlePanel();
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        overlayPanel.add(titlePanel);
        overlayPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Player selection panel with semi-transparent background
        JPanel selectionPanel = createSelectionPanel(setupDialog, parent);
        selectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        overlayPanel.add(selectionPanel);

        mainPanel.add(overlayPanel, BorderLayout.CENTER);

        setupDialog.add(mainPanel);
        setupDialog.setLocationRelativeTo(null);
        setupDialog.setVisible(true);
    }

    private static JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Main title with clean shadow effect
        JLabel titleLabel = new JLabel("LadderFall") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                FontMetrics fm = g2d.getFontMetrics(getFont());
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;

                // Strong shadow for readability
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.setFont(getFont());
                g2d.drawString(getText(), x + 5, y + 5);

                // Outline for better contrast
                g2d.setColor(new Color(20, 40, 60));
                g2d.setStroke(new BasicStroke(4));
                for (int i = -2; i <= 2; i++) {
                    for (int j = -2; j <= 2; j++) {
                        if (i != 0 || j != 0) {
                            g2d.drawString(getText(), x + i, y + j);
                        }
                    }
                }

                // Main text with bright gradient
                GradientPaint gradient = new GradientPaint(
                        0, y - fm.getAscent(), new Color(255, 255, 150),
                        0, y, new Color(150, 220, 255)
                );
                g2d.setPaint(gradient);
                g2d.drawString(getText(), x, y);
            }
        };
        // Try modern fonts including Poppins, fallback to system fonts
        String[] fontNames = {"Poppins", "Montserrat", "Roboto", "Segoe UI", "Helvetica Neue", "Century Gothic", "Arial"};
        Font titleFont = null;
        for (String fontName : fontNames) {
            Font tryFont = new Font(fontName, Font.BOLD, 80);
            if (tryFont.getFamily().toLowerCase().contains(fontName.toLowerCase()) ||
                    tryFont.getFontName().toLowerCase().contains(fontName.toLowerCase())) {
                titleFont = tryFont;
                break;
            }
        }
        if (titleFont == null) titleFont = new Font("SansSerif", Font.BOLD, 80);
        titleLabel.setFont(titleFont);
        titleLabel.setPreferredSize(new Dimension(800, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Subtitle with shadow
        JLabel subtitleLabel = new JLabel("Climb Your Way to Victory") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                FontMetrics fm = g2d.getFontMetrics(getFont());
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.setFont(getFont());
                g2d.drawString(getText(), x + 2, y + 2);

                // Main text
                g2d.setColor(new Color(255, 255, 180));
                g2d.drawString(getText(), x, y);
            }
        };
        // Use modern font for subtitle
        String[] subtitleFonts = {"Poppins", "Montserrat", "Roboto", "Segoe UI", "Helvetica", "Century Gothic", "Arial"};
        Font subFont = null;
        for (String fontName : subtitleFonts) {
            Font tryFont = new Font(fontName, Font.PLAIN, 24);
            if (tryFont.getFamily().toLowerCase().contains(fontName.toLowerCase()) ||
                    tryFont.getFontName().toLowerCase().contains(fontName.toLowerCase())) {
                subFont = tryFont;
                break;
            }
        }
        if (subFont == null) subFont = new Font("SansSerif", Font.PLAIN, 24);
        subtitleLabel.setFont(subFont);
        subtitleLabel.setPreferredSize(new Dimension(800, 40));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitleLabel);

        return panel;
    }

    private static JPanel createSelectionPanel(JDialog dialog, DiceGameGUI parent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(15, 35, 25, 220)); // Darker forest green
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 180, 120, 180), 3),
                BorderFactory.createEmptyBorder(20, 35, 20, 35) // Reduced padding from 30, 50
        ));

        // Player count selector
        JLabel playersLabel = new JLabel("Select Number of Players:");
        playersLabel.setFont(getModernFont(Font.BOLD, 16)); // Smaller font
        playersLabel.setForeground(Color.WHITE);
        playersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(playersLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Reduced from 15

        JComboBox<String> playerCountCombo = new JComboBox<>();
        for (int i = 2; i <= 8; i++) {
            playerCountCombo.addItem(i + " Players");
        }
        playerCountCombo.setFont(getModernFont(Font.BOLD, 14)); // Smaller font
        playerCountCombo.setMaximumSize(new Dimension(240, 40)); // Smaller combo box
        playerCountCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleComboBox(playerCountCombo);
        panel.add(playerCountCombo);

        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Reduced from 20

        // Player names panel with reduced size
        JPanel namesPanel = new JPanel();
        namesPanel.setLayout(new BoxLayout(namesPanel, BoxLayout.Y_AXIS));
        namesPanel.setOpaque(false);

        JTextField[] nameFields = new JTextField[8];
        Color[] playerColors = {
                new Color(255, 107, 107), new Color(78, 205, 196),
                new Color(69, 183, 209), new Color(255, 160, 122),
                new Color(152, 216, 200), new Color(247, 220, 111),
                new Color(187, 143, 206), new Color(133, 193, 226)
        };

        for (int i = 0; i < 8; i++) {
            JPanel playerRow = createPlayerRow(i, playerColors[i], nameFields);
            namesPanel.add(playerRow);
            namesPanel.add(Box.createRigidArea(new Dimension(0, 6))); // Reduced from 8
            if (i >= 2) playerRow.setVisible(false);
        }

        playerCountCombo.addActionListener(e -> {
            String selected = (String) playerCountCombo.getSelectedItem();
            int count = Integer.parseInt(selected.replaceAll("[^0-9]", ""));
            for (int i = 0; i < 8; i++) {
                Component row = namesPanel.getComponent(i * 2);
                row.setVisible(i < count);
            }
            dialog.revalidate();
        });

        JScrollPane namesScroll = new JScrollPane(namesPanel);
        namesScroll.setBorder(null);
        namesScroll.setOpaque(false);
        namesScroll.getViewport().setOpaque(false);
        namesScroll.setPreferredSize(new Dimension(420, 180)); // Much smaller: 480→420, 220→180
        panel.add(namesScroll);

        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Reduced from 25

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // Reduced: 25→20
        buttonsPanel.setOpaque(false);

        // Start button with forest green theme
        JButton startButton = createGlowButton("► START GAME", new Color(34, 139, 34));
        startButton.addActionListener(e -> {
            // Play button click sound
            SoundManager.getInstance().playButtonClick();

            String selected = (String) playerCountCombo.getSelectedItem();
            int numPlayers = Integer.parseInt(selected.replaceAll("[^0-9]", ""));

            SoundManager.getInstance().stopSetupMusic();
            parent.gameEngine = new GameEngine(numPlayers);

            for (int i = 0; i < numPlayers; i++) {
                String name = nameFields[i].getText().trim();
                if (!name.isEmpty()) {
                    parent.gameEngine.getAllPlayers().get(i).setName(name);
                }
            }

            dialog.dispose();
            parent.initializeGame();
            SoundManager.getInstance().startBackgroundMusic();
        });
        buttonsPanel.add(startButton);

        // Exit button with dark red theme
        JButton exitButton = createGlowButton("✕ EXIT", new Color(178, 34, 34));
        exitButton.addActionListener(e -> {
            // Play button click sound
            SoundManager.getInstance().playButtonClick();
            System.exit(0);
        });
        buttonsPanel.add(exitButton);

        panel.add(buttonsPanel);

        return panel;
    }

    private static JPanel createPlayerRow(int index, Color color, JTextField[] nameFields) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 3)) { // Reduced padding: 10→7, 5→3
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background with rounded corners - subtle glow effect
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                super.paintComponent(g);
            }
        };
        row.setOpaque(false);

        // More subtle border colors that match the forest theme
        Color borderColor = new Color(
                Math.min(255, color.getRed() + 30),
                Math.min(255, color.getGreen() + 30),
                Math.min(255, color.getBlue() + 30),
                180
        );

        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2, true),
                BorderFactory.createEmptyBorder(3, 8, 3, 8) // Reduced: 5→3, 12→8
        ));

        // Use colored icons with different shapes for each player
        final String[] shapes = {"●", "■", "▲", "♦", "★", "⬢", "▼", "◆"};
        JLabel iconLabel = new JLabel(shapes[index]) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Draw colored circle background (smaller)
                g2d.setColor(color.brighter());
                g2d.fillOval(0, 0, 28, 28);

                // Draw darker inner circle for depth
                g2d.setColor(color);
                g2d.fillOval(2, 2, 24, 24);

                // Draw white border
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(1, 1, 26, 26);

                super.paintComponent(g);
            }
        };
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 16)); // Smaller: 18→16
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setPreferredSize(new Dimension(28, 28)); // Smaller: 32→28
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        JLabel playerLabel = new JLabel("Player " + (index + 1));
        playerLabel.setFont(getModernFont(Font.BOLD, 13)); // Smaller: 14→13
        playerLabel.setForeground(Color.WHITE);
        playerLabel.setPreferredSize(new Dimension(65, 22)); // Smaller: 75→65, 25→22

        nameFields[index] = new JTextField("Player " + (index + 1));
        nameFields[index].setFont(getModernFont(Font.PLAIN, 13)); // Smaller: 14→13
        nameFields[index].setPreferredSize(new Dimension(145, 28)); // Smaller: 160→145, 32→28
        nameFields[index].setBackground(new Color(245, 250, 245, 250));
        nameFields[index].setForeground(new Color(40, 60, 40));

        // Subtle border that matches theme
        Color fieldBorderColor = new Color(
                Math.min(255, color.getRed() + 50),
                Math.min(255, color.getGreen() + 50),
                Math.min(255, color.getBlue() + 50),
                150
        );

        nameFields[index].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fieldBorderColor, 2, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8) // Reduced: 6→5, 10→8
        ));

        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20)); // Smaller: 24→20
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createLineBorder(color.darker(), 1)
        ));

        row.add(iconLabel);
        row.add(playerLabel);
        row.add(nameFields[index]);
        row.add(colorBox);

        return row;
    }

    private static JButton createGlowButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            private float hoverScale = 1.0f;
            private Timer hoverTimer;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        // Play subtle hover sound
                        SoundManager.getInstance().playButtonHover();

                        if (hoverTimer != null) hoverTimer.stop();
                        hoverTimer = new Timer(20, evt -> {
                            hoverScale = Math.min(1.05f, hoverScale + 0.01f);
                            repaint();
                        });
                        hoverTimer.start();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (hoverTimer != null) hoverTimer.stop();
                        hoverTimer = new Timer(20, evt -> {
                            hoverScale = Math.max(1.0f, hoverScale - 0.01f);
                            repaint();
                            if (hoverScale <= 1.0f) hoverTimer.stop();
                        });
                        hoverTimer.start();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int w = getWidth();
                int h = getHeight();

                // Scale effect on hover
                if (hoverScale > 1.0f) {
                    int offset = (int)((hoverScale - 1.0f) * 20);
                    g2d.translate(offset, offset);
                    w = (int)(w * hoverScale);
                    h = (int)(h * hoverScale);
                }

                // Multi-layer glow effect
                for (int i = 15; i > 0; i--) {
                    int alpha = (int)(40 - i * 2.5);
                    g2d.setColor(new Color(
                            baseColor.getRed(),
                            baseColor.getGreen(),
                            baseColor.getBlue(),
                            Math.max(0, alpha)
                    ));
                    g2d.fillRoundRect(-i, -i, w + i * 2, h + i * 2, 35 + i, 35 + i);
                }

                // Button shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(3, 3, w, h, 35, 35);

                // Button gradient with 3D effect
                GradientPaint gradient = new GradientPaint(
                        0, 0, baseColor.brighter().brighter(),
                        0, h, baseColor.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, w, h, 35, 35);

                // Top shine for 3D effect
                GradientPaint topShine = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 100),
                        0, h / 3, new Color(255, 255, 255, 0)
                );
                g2d.setPaint(topShine);
                g2d.fillRoundRect(0, 0, w, h / 3, 35, 35);

                // Bottom shadow for depth
                GradientPaint bottomShadow = new GradientPaint(
                        0, h * 2 / 3, new Color(0, 0, 0, 0),
                        0, h, new Color(0, 0, 0, 60)
                );
                g2d.setPaint(bottomShadow);
                g2d.fillRoundRect(0, h * 2 / 3, w, h / 3, 35, 35);

                // Glossy inner border
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(2, 2, w - 4, h - 4, 32, 32);

                // Outer border with theme color
                Color borderColor = baseColor.brighter().brighter();
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(0, 0, w - 1, h - 1, 35, 35);

                super.paintComponent(g);
            }
        };

        button.setFont(getModernFont(Font.BOLD, 17)); // Smaller: 18→17
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(180, 50)); // Smaller: 200→180, 55→50
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(new Color(255, 255, 255, 240));
        comboBox.setForeground(new Color(40, 60, 40));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 200, 120), 3, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Helper method to get modern fonts with fallbacks
     */
    private static Font getModernFont(int style, int size) {
        String[] fontNames = {"Poppins", "Montserrat", "Roboto", "Segoe UI", "Helvetica Neue", "Century Gothic", "Arial"};
        for (String fontName : fontNames) {
            Font tryFont = new Font(fontName, style, size);
            if (tryFont.getFamily().toLowerCase().contains(fontName.toLowerCase()) ||
                    tryFont.getFontName().toLowerCase().contains(fontName.toLowerCase())) {
                return tryFont;
            }
        }
        // Fallback to SansSerif which looks better than Arial
        return new Font("SansSerif", style, size);
    }
}

/**
 * Panel with waterfall background image
 */
class BackgroundPanel extends JPanel {
    private BufferedImage backgroundImage;

    public BackgroundPanel() {
        try {
            // Try to load the waterfall image
            // Put your image file in the same directory as your Java files
            backgroundImage = ImageIO.read(new File("waterfall_background.png"));
        } catch (Exception e) {
            System.err.println("Could not load background image: " + e.getMessage());
            // Will use gradient fallback
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundImage != null) {
            // Draw the waterfall image scaled to fit
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            // Add a slight dark overlay for better text readability
            g2d.setColor(new Color(0, 20, 10, 60));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // Fallback: beautiful forest green gradient
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(40, 80, 60),
                    0, getHeight(), new Color(20, 50, 40)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}