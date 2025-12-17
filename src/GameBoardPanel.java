import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Replace your GameBoardPanel.java with this version
 * Features: Path-style board with nature theme, better player tokens
 */
public class GameBoardPanel extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 80;
    private GameEngine gameEngine;
    private Map<Player, Point> playerAnimationPositions;
    private Map<Player, Integer> playerTargetPositions;
    private Timer animationTimer;
    private Ladder climbingLadder = null;
    private Ladder[] ladders;
    private BufferedImage pathTexture;

    public GameBoardPanel(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.ladders = gameEngine.getLadders();
        this.playerAnimationPositions = new HashMap<>();
        this.playerTargetPositions = new HashMap<>();

        setPreferredSize(new Dimension(BOARD_SIZE * TILE_SIZE + 80,
                BOARD_SIZE * TILE_SIZE + 80));
        setBackground(new Color(120, 160, 100));

        loadPathTexture();
        resetAllPlayerPositions();
    }

    private void loadPathTexture() {
        try {
            pathTexture = ImageIO.read(new File("path_tile.png"));
        } catch (Exception e) {
            System.err.println("Could not load path texture, using generated texture");
        }
    }

    public void resetAllPlayerPositions() {
        for (Player player : gameEngine.getAllPlayers()) {
            Point startPos = getTileCenter(1);
            playerAnimationPositions.put(player, new Point(startPos));
            playerTargetPositions.put(player, 1);
        }
        repaint();
    }

    public void updateLadders(Ladder[] newLadders) {
        this.ladders = newLadders;
        repaint();
    }

    public Ladder checkForLadder(int position) {
        for (Ladder ladder : ladders) {
            if (ladder.from == position) {
                return ladder;
            }
        }
        return null;
    }

    public void setClimbingLadder(Ladder ladder) {
        this.climbingLadder = ladder;
        repaint();
    }

    private Point getTileCenter(int position) {
        if (position < 1) position = 1;
        if (position > 64) position = 64;

        int row = (position - 1) / BOARD_SIZE;
        int col = (position - 1) % BOARD_SIZE;

        if (row % 2 == 1) {
            col = BOARD_SIZE - 1 - col;
        }

        row = BOARD_SIZE - 1 - row;

        int x = 40 + col * TILE_SIZE + TILE_SIZE / 2;
        int y = 40 + row * TILE_SIZE + TILE_SIZE / 2;

        return new Point(x, y);
    }

    public void animateStepByStep(Player player, List<Integer> path, Runnable onComplete) {
        if (path.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        final int[] currentStep = {0};
        final double[] progress = {0.0};

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(20, null);
        animationTimer.addActionListener(e -> {
            if (currentStep[0] >= path.size() - 1) {
                animationTimer.stop();
                if (onComplete != null) onComplete.run();
                return;
            }

            progress[0] += 0.05;

            if (progress[0] >= 1.0) {
                progress[0] = 0.0;
                currentStep[0]++;

                if (currentStep[0] > 0 && currentStep[0] < path.size()) {
                    SoundManager.getInstance().playSound("step");
                }

                if (currentStep[0] >= path.size() - 1) {
                    int finalPos = path.get(path.size() - 1);
                    Point finalPoint = getTileCenter(finalPos);
                    playerAnimationPositions.put(player, new Point(finalPoint));
                    repaint();
                    animationTimer.stop();
                    if (onComplete != null) onComplete.run();
                    return;
                }
            }

            int currentPos = path.get(currentStep[0]);
            int nextPos = path.get(currentStep[0] + 1);

            Point currentPoint = getTileCenter(currentPos);
            Point nextPoint = getTileCenter(nextPos);

            double eased = easeInOutQuad(progress[0]);

            int interpolatedX = (int)(currentPoint.x + (nextPoint.x - currentPoint.x) * eased);
            int interpolatedY = (int)(currentPoint.y + (nextPoint.y - currentPoint.y) * eased);

            playerAnimationPositions.put(player, new Point(interpolatedX, interpolatedY));
            repaint();
        });

        animationTimer.start();
    }

    public void animateInstantClimb(Player player, int fromPos, int toPos, Runnable onComplete) {
        Point startPoint = getTileCenter(fromPos);
        Point endPoint = getTileCenter(toPos);

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        final double[] progress = {0.0};
        final int ANIMATION_FRAMES = 15;

        animationTimer = new Timer(16, null);
        animationTimer.addActionListener(e -> {
            progress[0] += 1.0 / ANIMATION_FRAMES;

            if (progress[0] >= 1.0) {
                playerAnimationPositions.put(player, new Point(endPoint));
                repaint();
                animationTimer.stop();
                if (onComplete != null) onComplete.run();
                return;
            }

            double eased = 1 - Math.pow(1 - progress[0], 3);

            int interpolatedX = (int)(startPoint.x + (endPoint.x - startPoint.x) * eased);
            int interpolatedY = (int)(startPoint.y + (endPoint.y - startPoint.y) * eased);

            playerAnimationPositions.put(player, new Point(interpolatedX, interpolatedY));
            repaint();
        });

        animationTimer.start();
    }

    private double easeInOutQuad(double t) {
        return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Draw nature background
        drawNatureBackground(g2d);

        // Draw path tiles
        for (int i = 0; i < 64; i++) {
            int row = i / BOARD_SIZE;
            int col = i % BOARD_SIZE;

            if (row % 2 == 1) {
                col = BOARD_SIZE - 1 - col;
            }

            row = BOARD_SIZE - 1 - row;

            int x = 40 + col * TILE_SIZE;
            int y = 40 + row * TILE_SIZE;

            drawPathTile(g2d, x, y, i + 1);
        }

        // Draw ladders
        g2d.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (Ladder ladder : ladders) {
            drawNatureLadder(g2d, ladder);
        }

        // Draw players with better tokens
        List<Player> players = gameEngine.getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Point pos = playerAnimationPositions.get(player);

            if (pos != null) {
                drawPlayerToken(g2d, player, pos, i + 1);
            }
        }
    }

    private void drawNatureBackground(Graphics2D g2d) {
        // Multi-layer grass background
        GradientPaint grassGradient = new GradientPaint(
                0, 0, new Color(95, 145, 75),
                0, getHeight(), new Color(70, 120, 60)
        );
        g2d.setPaint(grassGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Darker patches of grass
        g2d.setColor(new Color(75, 115, 65, 80));
        for (int i = 0; i < 15; i++) {
            int patchX = (int)(Math.random() * getWidth());
            int patchY = (int)(Math.random() * getHeight());
            int patchSize = 40 + (int)(Math.random() * 60);
            g2d.fillOval(patchX, patchY, patchSize, patchSize);
        }

        // Grass blades texture
        g2d.setColor(new Color(85, 135, 70, 60));
        for (int i = 0; i < 300; i++) {
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            int height = 6 + (int)(Math.random() * 8);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawLine(x, y, x + (int)(Math.random() * 3) - 1, y - height);
        }

        // Small flowers scattered
        Color[] flowerColors = {
                new Color(255, 105, 180, 150), // Pink
                new Color(255, 255, 100, 150), // Yellow
                new Color(150, 150, 255, 150), // Purple
                new Color(255, 160, 100, 150)  // Orange
        };

        for (int i = 0; i < 25; i++) {
            int flowerX = (int)(Math.random() * getWidth());
            int flowerY = (int)(Math.random() * getHeight());
            Color flowerColor = flowerColors[(int)(Math.random() * flowerColors.length)];

            // Flower petals
            g2d.setColor(flowerColor);
            for (int petal = 0; petal < 5; petal++) {
                double angle = (Math.PI * 2 * petal) / 5;
                int petalX = flowerX + (int)(Math.cos(angle) * 4);
                int petalY = flowerY + (int)(Math.sin(angle) * 4);
                g2d.fillOval(petalX - 3, petalY - 3, 6, 6);
            }

            // Flower center
            g2d.setColor(new Color(255, 215, 0, 180));
            g2d.fillOval(flowerX - 2, flowerY - 2, 4, 4);
        }

        // Small rocks scattered
        g2d.setColor(new Color(120, 110, 100, 120));
        for (int i = 0; i < 20; i++) {
            int rockX = (int)(Math.random() * getWidth());
            int rockY = (int)(Math.random() * getHeight());
            int rockSize = 3 + (int)(Math.random() * 5);
            g2d.fillOval(rockX, rockY, rockSize, rockSize);
        }
    }

    private void drawPathTile(Graphics2D g2d, int x, int y, int tileNumber) {
        // Stone path tile with nature theme
        if (pathTexture != null) {
            g2d.drawImage(pathTexture, x, y, TILE_SIZE, TILE_SIZE, null);
        } else {
            // Generate beautiful stone path tile
            drawStoneTexture(g2d, x, y, tileNumber);
        }

        // Tile number with shadow
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String num = String.valueOf(tileNumber);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (TILE_SIZE - fm.stringWidth(num)) / 2;
        int textY = y + 20;

        // Shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(num, textX + 1, textY + 1);

        // Main text
        g2d.setColor(new Color(80, 60, 40));
        g2d.drawString(num, textX, textY);

        // Special tiles
        if (tileNumber == 1) {
            drawStartTile(g2d, x, y);
        } else if (tileNumber == 64) {
            drawFinishTile(g2d, x, y);
        } else if (tileNumber % 5 == 0) {
            drawBonusTile(g2d, x, y);
        }

        // Prime number tiles (where ladders start) - golden glow
        if (isPrime(tileNumber)) {
            g2d.setColor(new Color(255, 215, 0, 80));
            g2d.fillRoundRect(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8, 15, 15);

            // Golden border
            g2d.setColor(new Color(255, 215, 0, 120));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8, 15, 15);
        }
    }

    private void drawStoneTexture(Graphics2D g2d, int x, int y, int tileNumber) {
        // Base stone color with variation
        int variation = (tileNumber * 13) % 30;
        Color baseStone = new Color(190 + variation, 180 + variation, 160 + variation);

        // Stone shape - irregular hexagon
        int[] xPoints = {
                x + 8, x + TILE_SIZE - 8, x + TILE_SIZE - 5,
                x + TILE_SIZE - 8, x + 8, x + 5
        };
        int[] yPoints = {
                y + 5, y + 8, y + TILE_SIZE / 2,
                y + TILE_SIZE - 8, y + TILE_SIZE - 5, y + TILE_SIZE / 2
        };

        // Shadow under stone
        g2d.setColor(new Color(60, 80, 50, 100));
        Polygon shadowPoly = new Polygon(xPoints, yPoints, 6);
        g2d.translate(3, 3);
        g2d.fillPolygon(shadowPoly);
        g2d.translate(-3, -3);

        // Main stone with gradient
        GradientPaint stoneGradient = new GradientPaint(
                x, y, baseStone.brighter(),
                x + TILE_SIZE, y + TILE_SIZE, baseStone.darker()
        );
        g2d.setPaint(stoneGradient);
        g2d.fillPolygon(xPoints, yPoints, 6);

        // Stone texture - random dark spots
        g2d.setColor(new Color(140, 130, 110, 80));
        for (int i = 0; i < 8; i++) {
            int spotX = x + 15 + (int)(Math.random() * (TILE_SIZE - 30));
            int spotY = y + 15 + (int)(Math.random() * (TILE_SIZE - 30));
            g2d.fillOval(spotX, spotY, 3 + (int)(Math.random() * 5), 3 + (int)(Math.random() * 5));
        }

        // Highlight on top edge
        g2d.setColor(new Color(255, 255, 240, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);

        // Dark edge on bottom
        g2d.setColor(new Color(100, 90, 70, 120));
        g2d.drawLine(xPoints[3], yPoints[3], xPoints[4], yPoints[4]);

        // Cracks in stone
        g2d.setColor(new Color(120, 110, 90, 150));
        g2d.setStroke(new BasicStroke(1.5f));
        if (Math.random() > 0.5) {
            int crackX1 = x + 20 + (int)(Math.random() * 20);
            int crackY1 = y + 20 + (int)(Math.random() * 20);
            int crackX2 = crackX1 + 15 + (int)(Math.random() * 15);
            int crackY2 = crackY1 + 10 + (int)(Math.random() * 10);
            g2d.drawLine(crackX1, crackY1, crackX2, crackY2);
        }

        // Moss/grass on edges
        if (tileNumber % 3 == 0) {
            g2d.setColor(new Color(80, 120, 60, 100));
            g2d.fillOval(x + 5, y + TILE_SIZE - 15, 12, 8);
            g2d.fillOval(x + TILE_SIZE - 15, y + 8, 10, 10);
        }

        // Stone border
        g2d.setColor(new Color(120, 110, 90));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, 6);
    }

    private void drawStartTile(Graphics2D g2d, int x, int y) {
        // Green flag
        g2d.setColor(new Color(50, 200, 50, 150));
        g2d.fillRect(x + 10, y + TILE_SIZE - 35, 30, 25);
        g2d.setColor(new Color(100, 80, 60));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x + 10, y + TILE_SIZE - 35, x + 10, y + TILE_SIZE - 10);
    }

    private void drawFinishTile(Graphics2D g2d, int x, int y) {
        // Checkered flag
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((i + j) % 2 == 0) {
                    g2d.setColor(Color.BLACK);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fillRect(x + 10 + i * 10, y + TILE_SIZE - 35 + j * 8, 10, 8);
            }
        }
        g2d.setColor(new Color(100, 80, 60));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x + 10, y + TILE_SIZE - 35, x + 10, y + TILE_SIZE - 10);
    }

    private void drawBonusTile(Graphics2D g2d, int x, int y) {
        // Star sparkle for bonus
        g2d.setColor(new Color(255, 215, 0, 100));
        int cx = x + TILE_SIZE - 20;
        int cy = y + TILE_SIZE - 20;
        drawStar(g2d, cx, cy, 8, new Color(255, 215, 0, 150));
    }

    private void drawNatureLadder(Graphics2D g2d, Ladder ladder) {
        Point fromPos = getTileCenter(ladder.from);
        Point toPos = getTileCenter(ladder.to);

        // Wooden ladder style
        if (climbingLadder != null && climbingLadder.from == ladder.from) {
            g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(new Color(139, 90, 43)); // Wood color
        } else {
            g2d.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(new Color(160, 110, 60, 200));
        }

        // Draw two side rails
        int offset = 12;
        g2d.drawLine(fromPos.x - offset, fromPos.y, toPos.x - offset, toPos.y);
        g2d.drawLine(fromPos.x + offset, fromPos.y, toPos.x + offset, toPos.y);

        // Draw rungs
        g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int numRungs = Math.max(3, (toPos.y - fromPos.y) / 30);
        for (int i = 1; i <= numRungs; i++) {
            float t = i / (float)(numRungs + 1);
            int rx = (int)(fromPos.x + (toPos.x - fromPos.x) * t);
            int ry = (int)(fromPos.y + (toPos.y - fromPos.y) * t);
            g2d.drawLine(rx - offset, ry, rx + offset, ry);
        }

        // Arrow at top
        g2d.setColor(new Color(50, 200, 50));
        int[] xPoints = {toPos.x, toPos.x - 10, toPos.x + 10};
        int[] yPoints = {toPos.y - 20, toPos.y - 10, toPos.y - 10};
        g2d.fillPolygon(xPoints, yPoints, 3);

        // Ladder label
        g2d.setColor(new Color(100, 70, 40));
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.drawString(String.valueOf(ladder.from), fromPos.x - 12, fromPos.y + 30);
    }

    private void drawPlayerToken(Graphics2D g2d, Player player, Point pos, int playerNumber) {
        Color playerColor = Color.decode(player.getColor());

        // Shadow
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillOval(pos.x - 20, pos.y - 15, 40, 30);

        // Glow effect
        for (int i = 3; i > 0; i--) {
            g2d.setColor(new Color(
                    playerColor.getRed(),
                    playerColor.getGreen(),
                    playerColor.getBlue(),
                    30 - i * 8
            ));
            g2d.fillOval(pos.x - 22 - i * 3, pos.y - 22 - i * 3, 44 + i * 6, 44 + i * 6);
        }

        // Main token - rounded pawn shape
        GradientPaint tokenGradient = new GradientPaint(
                pos.x, pos.y - 20, playerColor.brighter(),
                pos.x, pos.y + 20, playerColor.darker()
        );
        g2d.setPaint(tokenGradient);

        // Pawn body
        g2d.fillOval(pos.x - 18, pos.y - 18, 36, 36);

        // Pawn top (head)
        g2d.fillOval(pos.x - 12, pos.y - 25, 24, 24);

        // Shine effect
        g2d.setColor(new Color(255, 255, 255, 120));
        g2d.fillOval(pos.x - 10, pos.y - 20, 12, 12);

        // Border
        g2d.setColor(playerColor.darker().darker());
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(pos.x - 18, pos.y - 18, 36, 36);
        g2d.drawOval(pos.x - 12, pos.y - 25, 24, 24);

        // Player number badge
        g2d.setColor(Color.WHITE);
        g2d.fillOval(pos.x - 10, pos.y - 8, 20, 20);
        g2d.setColor(playerColor.darker().darker());
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String num = String.valueOf(playerNumber);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(num, pos.x - fm.stringWidth(num) / 2, pos.y + 5);
    }

    private void drawStar(Graphics2D g2d, int x, int y, int radius, Color color) {
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];

        double angle = -Math.PI / 2;
        double angleIncrement = Math.PI / 5;

        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? radius : radius / 2.5;
            xPoints[i] = (int)(x + r * Math.cos(angle));
            yPoints[i] = (int)(y + r * Math.sin(angle));
            angle += angleIncrement;
        }

        g2d.setColor(color);
        g2d.fillPolygon(xPoints, yPoints, 10);
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}

