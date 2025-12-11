import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class GameBoardPanel extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 70;
    private GameEngine gameEngine;
    private Map<Player, Point> playerAnimationPositions;
    private Map<Player, Integer> playerTargetPositions;
    private Timer animationTimer;
    private Ladder climbingLadder = null;
    private Ladder[] ladders;

    public GameBoardPanel(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.ladders = gameEngine.getLadders();
        this.playerAnimationPositions = new HashMap<>();
        this.playerTargetPositions = new HashMap<>();

        setPreferredSize(new Dimension(BOARD_SIZE * TILE_SIZE + 40,
                BOARD_SIZE * TILE_SIZE + 40));
        setBackground(new Color(240, 248, 255));

        // Initialize player positions at start (position 1)
        resetAllPlayerPositions();
    }

    /**
     * Reset all players to the starting position (tile 1)
     */
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

        // Snake pattern: even rows go left to right, odd rows go right to left
        if (row % 2 == 1) {
            col = BOARD_SIZE - 1 - col;
        }

        // Invert row (start from bottom)
        row = BOARD_SIZE - 1 - row;

        int x = 20 + col * TILE_SIZE + TILE_SIZE / 2;
        int y = 20 + row * TILE_SIZE + TILE_SIZE / 2;

        return new Point(x, y);
    }

    /**
     * Animate player moving step by step through positions with smooth interpolation
     */
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

        // Slower, smoother animation with interpolation
        animationTimer = new Timer(20, null);
        animationTimer.addActionListener(e -> {
            if (currentStep[0] >= path.size() - 1) {
                animationTimer.stop();
                if (onComplete != null) {
                    onComplete.run();
                }
                return;
            }

            // Smooth interpolation between positions
            progress[0] += 0.05; // Slightly faster for better responsiveness

            if (progress[0] >= 1.0) {
                // Move to next step
                progress[0] = 0.0;
                currentStep[0]++;

                // Play step sound when reaching a new tile
                if (currentStep[0] > 0 && currentStep[0] < path.size()) {
                    SoundManager.getInstance().playSound("step");
                }

                if (currentStep[0] >= path.size() - 1) {
                    // Final position
                    int finalPos = path.get(path.size() - 1);
                    Point finalPoint = getTileCenter(finalPos);
                    playerAnimationPositions.put(player, new Point(finalPoint));
                    repaint();
                    animationTimer.stop();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                    return;
                }
            }

            // Interpolate between current and next position
            int currentPos = path.get(currentStep[0]);
            int nextPos = path.get(currentStep[0] + 1);

            Point currentPoint = getTileCenter(currentPos);
            Point nextPoint = getTileCenter(nextPos);

            // Ease-in-out interpolation for smoother movement
            double eased = easeInOutQuad(progress[0]);

            int interpolatedX = (int)(currentPoint.x + (nextPoint.x - currentPoint.x) * eased);
            int interpolatedY = (int)(currentPoint.y + (nextPoint.y - currentPoint.y) * eased);

            playerAnimationPositions.put(player, new Point(interpolatedX, interpolatedY));
            repaint();
        });

        animationTimer.start();
    }

    /**
     * Instantly teleport player to destination with smooth snap animation
     * Used for ladder climbing - fast and immediate
     */
    public void animateInstantClimb(Player player, int fromPos, int toPos, Runnable onComplete) {
        Point startPoint = getTileCenter(fromPos);
        Point endPoint = getTileCenter(toPos);

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        final double[] progress = {0.0};
        final int ANIMATION_FRAMES = 15; // Quick smooth transition

        animationTimer = new Timer(16, null); // ~60 FPS
        animationTimer.addActionListener(e -> {
            progress[0] += 1.0 / ANIMATION_FRAMES;

            if (progress[0] >= 1.0) {
                // Snap to final position
                playerAnimationPositions.put(player, new Point(endPoint));
                repaint();
                animationTimer.stop();
                if (onComplete != null) {
                    onComplete.run();
                }
                return;
            }

            // Quick ease-out for snappy feel
            double eased = 1 - Math.pow(1 - progress[0], 3);

            int interpolatedX = (int)(startPoint.x + (endPoint.x - startPoint.x) * eased);
            int interpolatedY = (int)(startPoint.y + (endPoint.y - startPoint.y) * eased);

            playerAnimationPositions.put(player, new Point(interpolatedX, interpolatedY));
            repaint();
        });

        animationTimer.start();
    }

    /**
     * Ease-in-out quad function for smooth animation
     */
    private double easeInOutQuad(double t) {
        return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw board tiles
        for (int i = 0; i < 64; i++) {
            int row = i / BOARD_SIZE;
            int col = i % BOARD_SIZE;

            // Snake pattern
            if (row % 2 == 1) {
                col = BOARD_SIZE - 1 - col;
            }

            // Invert row
            row = BOARD_SIZE - 1 - row;

            int x = 20 + col * TILE_SIZE;
            int y = 20 + row * TILE_SIZE;

            // Gradient tile background
            GradientPaint gradient;
            if ((row + col) % 2 == 0) {
                gradient = new GradientPaint(x, y, new Color(230, 240, 255),
                        x + TILE_SIZE, y + TILE_SIZE, new Color(200, 220, 255));
            } else {
                gradient = new GradientPaint(x, y, new Color(255, 250, 240),
                        x + TILE_SIZE, y + TILE_SIZE, new Color(255, 240, 220));
            }
            g2d.setPaint(gradient);
            g2d.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, 10, 10);

            // Highlight prime-numbered tiles (where ladders can start)
            if (isPrime(i + 1)) {
                g2d.setColor(new Color(255, 215, 0, 40));
                g2d.fillRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 8, 8);
            }

            g2d.setColor(new Color(180, 180, 180));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, y, TILE_SIZE, TILE_SIZE, 10, 10);

            // Draw tile number
            g2d.setColor(new Color(100, 100, 100));
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            String tileNum = String.valueOf(i + 1);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (TILE_SIZE - fm.stringWidth(tileNum)) / 2;
            int textY = y + 18;
            g2d.drawString(tileNum, textX, textY);

            // Highlight special tiles
            if (i == 0) {
                // START tile
                GradientPaint startGrad = new GradientPaint(x, y, new Color(144, 238, 144, 150),
                        x, y + TILE_SIZE, new Color(50, 205, 50, 150));
                g2d.setPaint(startGrad);
                g2d.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, 10, 10);
                g2d.setColor(new Color(0, 120, 0));
                g2d.setFont(new Font("Arial", Font.BOLD, 13));
                g2d.drawString("START", x + 12, y + 42);
            } else if (i == 63) {
                // FINISH tile
                GradientPaint finishGrad = new GradientPaint(x, y, new Color(255, 215, 0, 180),
                        x, y + TILE_SIZE, new Color(255, 140, 0, 180));
                g2d.setPaint(finishGrad);
                g2d.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, 10, 10);
                g2d.setColor(new Color(184, 134, 11));
                g2d.setFont(new Font("Arial", Font.BOLD, 13));
                g2d.drawString("FINISH", x + 10, y + 42);
            }

            // Highlight tiles with special bonuses (multiples of 5)
            if (i > 0 && i < 63 && (i + 1) % 5 == 0) {
                g2d.setColor(new Color(138, 43, 226, 30));
                g2d.fillRoundRect(x + 3, y + 3, TILE_SIZE - 6, TILE_SIZE - 6, 8, 8);
            }
        }

        // Draw ladders
        g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (Ladder ladder : ladders) {
            drawLadder(g2d, ladder);
        }

        // Draw players with star animation
        List<Player> players = gameEngine.getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Point pos = playerAnimationPositions.get(player);

            if (pos != null) {
                // Draw glow effect
                g2d.setColor(new Color(Color.decode(player.getColor()).getRed(),
                        Color.decode(player.getColor()).getGreen(),
                        Color.decode(player.getColor()).getBlue(), 50));
                g2d.fillOval(pos.x - 22, pos.y - 22, 44, 44);

                // Draw star
                drawStar(g2d, pos.x, pos.y, 16, Color.decode(player.getColor()));

                // Draw player number
                g2d.setColor(Color.WHITE);
                g2d.fillOval(pos.x - 8, pos.y - 8, 16, 16);
                g2d.setColor(Color.decode(player.getColor()).darker());
                g2d.setFont(new Font("Arial", Font.BOLD, 11));
                String num = String.valueOf(i + 1);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(num, pos.x - fm.stringWidth(num) / 2, pos.y + 4);
            }
        }
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

    private void drawLadder(Graphics2D g2d, Ladder ladder) {
        Point fromPos = getTileCenter(ladder.from);
        Point toPos = getTileCenter(ladder.to);

        // Highlight if currently climbing
        if (climbingLadder != null && climbingLadder.from == ladder.from) {
            g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            GradientPaint ladderGrad = new GradientPaint(
                    fromPos.x, fromPos.y, ladder.color.brighter(),
                    toPos.x, toPos.y, ladder.color
            );
            g2d.setPaint(ladderGrad);
        } else {
            g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Color transparentColor = new Color(
                    ladder.color.getRed(),
                    ladder.color.getGreen(),
                    ladder.color.getBlue(),
                    150
            );
            g2d.setColor(transparentColor);
        }

        // Draw main ladder line
        g2d.drawLine(fromPos.x, fromPos.y, toPos.x, toPos.y);

        // Draw rungs
        g2d.setStroke(new BasicStroke(3));
        for (int i = 1; i <= 3; i++) {
            float t = i / 4.0f;
            int x = (int) (fromPos.x + (toPos.x - fromPos.x) * t);
            int y = (int) (fromPos.y + (toPos.y - fromPos.y) * t);
            g2d.drawLine(x - 8, y, x + 8, y);
        }

        // Draw arrow at top
        drawArrow(g2d, toPos.x, toPos.y, ladder.color);

        // Draw ladder position labels
        g2d.setColor(ladder.color.darker());
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString(String.valueOf(ladder.from), fromPos.x - 8, fromPos.y + 25);
    }

    private void drawArrow(Graphics2D g2d, int x, int y, Color color) {
        int[] xPoints = {x, x - 6, x + 6};
        int[] yPoints = {y - 15, y - 8, y - 8};
        g2d.setColor(color);
        g2d.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawStar(Graphics2D g2d, int x, int y, int radius, Color color) {
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];

        double angle = -Math.PI / 2;
        double angleIncrement = Math.PI / 5;

        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? radius : radius / 2.5;
            xPoints[i] = (int) (x + r * Math.cos(angle));
            yPoints[i] = (int) (y + r * Math.sin(angle));
            angle += angleIncrement;
        }

        g2d.setColor(color);
        g2d.fillPolygon(xPoints, yPoints, 10);
        g2d.setColor(color.darker().darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, 10);
    }
}

class Ladder {
    int from;
    int to;
    Color color;

    public Ladder(int from, int to, Color color) {
        this.from = from;
        this.to = to;
        this.color = color;
    }
}