public class Player {
    private String name;
    private int position;
    private String color;
    private int points;
    private long completionTime; // Time to finish game in milliseconds
    private int totalMoves;

    public Player(String name, String color) {
        this.name = name;
        this.position = 1; // Start at position 1
        this.color = color;
        this.points = 0;
        this.completionTime = 0;
        this.totalMoves = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        // Ensure position stays within bounds
        if (position < 1) {
            this.position = 1;
        } else if (position > 64) {
            this.position = 64;
        } else {
            this.position = position;
        }
    }

    public String getColor() {
        return color;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void resetPoints() {
        this.points = 0;
    }

    public void move(int steps) {
        setPosition(position + steps);
        totalMoves++;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public void resetMoves() {
        this.totalMoves = 0;
    }

    public String getFormattedCompletionTime() {
        if (completionTime <= 0) return "N/A";

        long seconds = completionTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return name + " (Position: " + position + ", Points: " + points + ")";
    }
}