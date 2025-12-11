import javax.swing.*;
import java.awt.*;

public class DicePanel extends JPanel {
    private int currentValue = 1;
    private boolean isRolling = false;
    private Color diceColor = new Color(220, 220, 220);

    public DicePanel() {
        setPreferredSize(new Dimension(100, 100));
        setMaximumSize(new Dimension(100, 100));
        setBackground(new Color(248, 249, 250));
    }

    public void rollDice(Runnable onComplete) {
        isRolling = true;
        diceColor = new Color(220, 220, 220);

        Timer rollTimer = new Timer(50, null);
        final int[] rollCount = {0};

        rollTimer.addActionListener(e -> {
            currentValue = (int)(Math.random() * 6) + 1;
            repaint();

            rollCount[0]++;
            if (rollCount[0] > 20) {
                rollTimer.stop();
                isRolling = false;
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
        repaint();
    }

    public void reset() {
        currentValue = 1;
        diceColor = new Color(220, 220, 220);
        isRolling = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = 80;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        // Draw dice shadow
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fillRoundRect(x + 4, y + 4, size, size, 15, 15);

        // Draw dice
        GradientPaint gradient = new GradientPaint(x, y, diceColor, x + size, y + size, diceColor.darker());
        g2d.setPaint(gradient);
        g2d.fillRoundRect(x, y, size, size, 15, 15);

        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, size, size, 15, 15);

        // Draw dots
        if (!isRolling) {
            g2d.setColor(new Color(50, 50, 50));
            int dotSize = 12;
            int padding = 18;

            switch (currentValue) {
                case 1:
                    g2d.fillOval(x + size/2 - dotSize/2, y + size/2 - dotSize/2, dotSize, dotSize);
                    break;
                case 2:
                    g2d.fillOval(x + padding, y + padding, dotSize, dotSize);
                    g2d.fillOval(x + size - padding - dotSize, y + size - padding - dotSize, dotSize, dotSize);
                    break;
                case 3:
                    g2d.fillOval(x + padding, y + padding, dotSize, dotSize);
                    g2d.fillOval(x + size/2 - dotSize/2, y + size/2 - dotSize/2, dotSize, dotSize);
                    g2d.fillOval(x + size - padding - dotSize, y + size - padding - dotSize, dotSize, dotSize);
                    break;
                case 4:
                    g2d.fillOval(x + padding, y + padding, dotSize, dotSize);
                    g2d.fillOval(x + size - padding - dotSize, y + padding, dotSize, dotSize);
                    g2d.fillOval(x + padding, y + size - padding - dotSize, dotSize, dotSize);
                    g2d.fillOval(x + size - padding - dotSize, y + size - padding - dotSize, dotSize, dotSize);
                    break;
                case 5:
                    g2d.fillOval(x + padding, y + padding, dotSize, dotSize);
                    g2d.fillOval(x + size - padding - dotSize, y + padding, dotSize, dotSize);
                    g2d.fillOval(x + size/2 - dotSize/2, y + size/2 - dotSize/2, dotSize, dotSize);
                    g2d.fillOval(x + padding, y + size - padding - dotSize, dotSize, dotSize);
                    g2d.fillOval(x + size - padding - dotSize, y + size - padding - dotSize, dotSize, dotSize);
                    break;
                case 6:
                    g2d.fillOval(x + padding, y + padding, dotSize, dotSize);
                    g2d.fillOval(x + size - padding - dotSize, y + padding, dotSize, dotSize);
                    g2d.fillOval(x + padding, y + size/2 - dotSize/2, dotSize, dotSize);
                    g2d.fillOval(x + size - padding - dotSize, y + size/2 - dotSize/2, dotSize, dotSize);
                    g2d.fillOval(x + padding, y + size - padding - dotSize, dotSize, dotSize);
                    g2d.fillOval(x + size - padding - dotSize, y + size - padding - dotSize, dotSize, dotSize);
                    break;
            }
        }
    }
}