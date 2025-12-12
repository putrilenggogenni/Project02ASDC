import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class DicePanel extends JPanel {
    private int currentValue = 1;
    private boolean isRolling = false;
    private Color diceColor = new Color(255, 255, 255);
    private double rotationAngle = 0;
    private double perspectiveScale = 1.0;
    private int bounceOffset = 0;
    private Timer visualEffectTimer;

    // 3D rotation angles for cool effect
    private double rotationX = 0;
    private double rotationY = 0;

    // Particle effects
    private java.util.List<Particle> particles = new java.util.ArrayList<>();

    public DicePanel() {
        setPreferredSize(new Dimension(120, 120));
        setMaximumSize(new Dimension(120, 120));
        setBackground(new Color(248, 249, 250));
        setOpaque(false);
    }

    public void rollDice(Runnable onComplete) {
        isRolling = true;
        diceColor = new Color(255, 255, 255);
        particles.clear();

        Timer rollTimer = new Timer(50, null);
        final int[] rollCount = {0};

        rollTimer.addActionListener(e -> {
            currentValue = (int)(Math.random() * 6) + 1;
            rotationAngle += 45;
            rotationX += 30;
            rotationY += 25;
            perspectiveScale = 0.9 + Math.random() * 0.2;

            // Add sparkle particles during roll
            if (rollCount[0] % 3 == 0) {
                addSparkles();
            }

            repaint();

            rollCount[0]++;
            if (rollCount[0] > 20) {
                rollTimer.stop();
                isRolling = false;
                rotationX = 0;
                rotationY = 0;
                perspectiveScale = 1.0;

                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });

        rollTimer.start();
    }

    public void showResult(int value, boolean isGreen) {
        currentValue = value;
        diceColor = isGreen ? new Color(144, 238, 144) : new Color(255, 160, 160);

        // Create celebration particles
        createCelebrationParticles(isGreen);

        // Bounce animation
        final int[] bounceCount = {0};
        if (visualEffectTimer != null) visualEffectTimer.stop();

        visualEffectTimer = new Timer(30, null);
        visualEffectTimer.addActionListener(e -> {
            bounceOffset = (int)(Math.sin(bounceCount[0] * 0.3) * 10 * Math.exp(-bounceCount[0] * 0.05));

            // Update particles
            for (int i = particles.size() - 1; i >= 0; i--) {
                Particle p = particles.get(i);
                p.update();
                if (p.isDead()) {
                    particles.remove(i);
                }
            }

            repaint();
            bounceCount[0]++;

            if (bounceCount[0] > 50 && particles.isEmpty()) {
                visualEffectTimer.stop();
                bounceOffset = 0;
            }
        });

        visualEffectTimer.start();
    }

    public void reset() {
        currentValue = 1;
        diceColor = new Color(255, 255, 255);
        isRolling = false;
        rotationAngle = 0;
        rotationX = 0;
        rotationY = 0;
        perspectiveScale = 1.0;
        bounceOffset = 0;
        particles.clear();
        if (visualEffectTimer != null) visualEffectTimer.stop();
        repaint();
    }

    private void addSparkles() {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        for (int i = 0; i < 3; i++) {
            double angle = Math.random() * Math.PI * 2;
            double speed = 2 + Math.random() * 3;
            particles.add(new Particle(
                    centerX, centerY,
                    Math.cos(angle) * speed,
                    Math.sin(angle) * speed,
                    new Color(255, 255, 100, 200),
                    15
            ));
        }
    }

    private void createCelebrationParticles(boolean isGreen) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        Color particleColor = isGreen ?
                new Color(50, 255, 50, 200) :
                new Color(255, 50, 50, 200);

        for (int i = 0; i < 12; i++) {
            double angle = (Math.PI * 2 * i) / 12;
            double speed = 3 + Math.random() * 2;
            particles.add(new Particle(
                    centerX, centerY,
                    Math.cos(angle) * speed,
                    Math.sin(angle) * speed,
                    particleColor,
                    30
            ));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int size = 90;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2 - bounceOffset;

        // Draw particles first (behind dice)
        for (Particle p : particles) {
            p.draw(g2d);
        }

        // Draw glow effect
        if (!isRolling) {
            for (int i = 3; i > 0; i--) {
                g2d.setColor(new Color(diceColor.getRed(), diceColor.getGreen(), diceColor.getBlue(), 30 - i * 8));
                g2d.fillRoundRect(x - i * 4, y - i * 4, size + i * 8, size + i * 8, 20 + i * 2, 20 + i * 2);
            }
        }

        if (isRolling) {
            // 3D rotation effect while rolling
            drawRotatingDice(g2d, x, y, size);
        } else {
            // Static beautiful dice
            drawStaticDice(g2d, x, y, size);
        }

        // Draw shine effect on top
        drawShineEffect(g2d, x, y, size);
    }

    private void drawRotatingDice(Graphics2D g2d, int x, int y, int size) {
        g2d.translate(x + size/2, y + size/2);
        g2d.rotate(Math.toRadians(rotationAngle));
        g2d.scale(perspectiveScale, perspectiveScale);
        g2d.translate(-size/2, -size/2);

        // Dice shadow with blur
        g2d.setColor(new Color(0, 0, 0, 40));
        for (int i = 0; i < 3; i++) {
            g2d.fillRoundRect(3 + i, 3 + i, size, size, 18 - i * 2, 18 - i * 2);
        }

        // Dice body with gradient
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(255, 255, 255),
                size, size, new Color(220, 220, 220)
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, size, size, 18, 18);

        // Border
        g2d.setColor(new Color(180, 180, 180));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(0, 0, size, size, 18, 18);

        // Random dots during rolling
        g2d.setColor(new Color(60, 60, 60, 150));
        int dotSize = 10;
        for (int i = 0; i < currentValue; i++) {
            int dx = (int)(Math.random() * (size - 30)) + 15;
            int dy = (int)(Math.random() * (size - 30)) + 15;
            g2d.fillOval(dx - dotSize/2, dy - dotSize/2, dotSize, dotSize);
        }

        g2d.setTransform(new AffineTransform());
    }

    private void drawStaticDice(Graphics2D g2d, int x, int y, int size) {
        // Multi-layer shadow for depth
        for (int i = 6; i > 0; i--) {
            g2d.setColor(new Color(0, 0, 0, 8 - i));
            g2d.fillRoundRect(x + i, y + i, size, size, 20, 20);
        }

        // Dice body with beautiful gradient
        GradientPaint bodyGradient = new GradientPaint(
                x, y, diceColor.brighter(),
                x + size, y + size, diceColor
        );
        g2d.setPaint(bodyGradient);
        g2d.fillRoundRect(x, y, size, size, 20, 20);

        // Inner shadow for depth
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x + 3, y + 3, size - 6, size - 6, 17, 17);

        // Outer border with shine
        g2d.setColor(new Color(200, 200, 200));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(x, y, size, size, 20, 20);

        // Draw dots with 3D effect
        drawDotsWithShadow(g2d, x, y, size, currentValue);
    }

    private void drawDotsWithShadow(Graphics2D g2d, int x, int y, int size, int value) {
        int dotSize = 14;
        int padding = 20;

        // Dot positions
        int[][] positions = getDotPositions(value, x, y, size, padding);

        for (int[] pos : positions) {
            // Dot shadow
            g2d.setColor(new Color(0, 0, 0, 60));
            g2d.fillOval(pos[0] - dotSize/2 + 2, pos[1] - dotSize/2 + 2, dotSize, dotSize);

            // Dot with gradient
            RadialGradientPaint dotGradient = new RadialGradientPaint(
                    pos[0] - 2, pos[1] - 2, dotSize/2,
                    new float[]{0f, 1f},
                    new Color[]{new Color(80, 80, 80), new Color(30, 30, 30)}
            );
            g2d.setPaint(dotGradient);
            g2d.fillOval(pos[0] - dotSize/2, pos[1] - dotSize/2, dotSize, dotSize);

            // Dot highlight
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.fillOval(pos[0] - dotSize/2 + 2, pos[1] - dotSize/2 + 2, dotSize/3, dotSize/3);
        }
    }

    private int[][] getDotPositions(int value, int x, int y, int size, int padding) {
        int cx = x + size/2;
        int cy = y + size/2;
        int left = x + padding;
        int right = x + size - padding;
        int top = y + padding;
        int bottom = y + size - padding;

        switch (value) {
            case 1:
                return new int[][]{{cx, cy}};
            case 2:
                return new int[][]{{left, top}, {right, bottom}};
            case 3:
                return new int[][]{{left, top}, {cx, cy}, {right, bottom}};
            case 4:
                return new int[][]{{left, top}, {right, top}, {left, bottom}, {right, bottom}};
            case 5:
                return new int[][]{{left, top}, {right, top}, {cx, cy}, {left, bottom}, {right, bottom}};
            case 6:
                return new int[][]{{left, top}, {right, top}, {left, cy}, {right, cy}, {left, bottom}, {right, bottom}};
            default:
                return new int[][]{};
        }
    }

    private void drawShineEffect(Graphics2D g2d, int x, int y, int size) {
        // Top-left shine
        GradientPaint shine = new GradientPaint(
                x + 10, y + 10, new Color(255, 255, 255, 100),
                x + size/2, y + size/2, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(shine);
        g2d.fillRoundRect(x + 8, y + 8, size/2, size/2, 15, 15);
    }

    // Particle class for special effects
    private class Particle {
        double x, y;
        double vx, vy;
        Color color;
        int life;
        int maxLife;
        double size;

        Particle(double x, double y, double vx, double vy, Color color, int maxLife) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.life = maxLife;
            this.maxLife = maxLife;
            this.size = 3 + Math.random() * 4;
        }

        void update() {
            x += vx;
            y += vy;
            vy += 0.3; // gravity
            vx *= 0.98; // air resistance
            vy *= 0.98;
            life--;
        }

        boolean isDead() {
            return life <= 0;
        }

        void draw(Graphics2D g2d) {
            float alpha = (float)life / maxLife;
            Color fadeColor = new Color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    (int)(alpha * color.getAlpha())
            );

            g2d.setColor(fadeColor);
            double currentSize = size * alpha;
            g2d.fill(new Ellipse2D.Double(x - currentSize/2, y - currentSize/2, currentSize, currentSize));

            // Glow effect
            g2d.setColor(new Color(fadeColor.getRed(), fadeColor.getGreen(), fadeColor.getBlue(), (int)(alpha * 50)));
            g2d.fill(new Ellipse2D.Double(x - currentSize, y - currentSize, currentSize * 2, currentSize * 2));
        }
    }
}