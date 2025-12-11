import java.util.*;

public class GameEngine {
    private Queue<Player> playerQueue;
    private Stack<MoveRecord> moveHistory;
    private List<Player> players;
    private Random random;
    private boolean gameOver;
    private Player winner;

    // Points for each tile (1-64)
    private int[] tilePoints = {
            0, 10, 15, 20, 25, 30, 15, 20, 25, 30,  // 1-10
            35, 20, 25, 30, 35, 40, 25, 30, 35, 40,  // 11-20
            45, 30, 35, 40, 45, 50, 35, 40, 45, 50,  // 21-30
            55, 40, 45, 50, 55, 60, 45, 50, 55, 60,  // 31-40
            65, 50, 55, 60, 65, 70, 55, 60, 65, 70,  // 41-50
            75, 60, 65, 70, 75, 80, 65, 70, 75, 80,  // 51-60
            85, 90, 95, 100                           // 61-64
    };

    public GameEngine(int numPlayers) {
        this.playerQueue = new LinkedList<>();
        this.moveHistory = new Stack<>();
        this.players = new ArrayList<>();
        this.random = new Random();
        this.gameOver = false;
        this.winner = null;

        // Create players with different colors
        String[] colors = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#FFA07A",
                "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E2"};

        for (int i = 0; i < numPlayers; i++) {
            String color = colors[i % colors.length];
            Player player = new Player("Player " + (i + 1), color);
            players.add(player);
            playerQueue.offer(player);
        }
    }

    public Player getCurrentPlayer() {
        return playerQueue.peek();
    }

    public List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }

    public Stack<MoveRecord> getMoveHistory() {
        return moveHistory;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }

    public int getHighestScore() {
        int max = 0;
        for (Player p : players) {
            if (p.getPoints() > max) {
                max = p.getPoints();
            }
        }
        return max;
    }

    public List<Player> getRankedPlayers() {
        List<Player> ranked = new ArrayList<>(players);
        ranked.sort((p1, p2) -> Integer.compare(p2.getPoints(), p1.getPoints()));
        return ranked;
    }

    public MoveRecord playTurn() {
        if (gameOver) {
            return null;
        }

        // Get current player
        Player currentPlayer = playerQueue.poll();

        // Roll dice (1-6)
        int diceRoll = random.nextInt(6) + 1;

        // Get random probability (0.0 - 1.0)
        double probability = random.nextDouble();

        // Determine direction based on probability
        int stepsMoved;
        if (probability <= 0.7) {
            // Green: Move forward
            stepsMoved = diceRoll;
        } else {
            // Red: Move backward
            stepsMoved = -diceRoll;
        }

        // Record starting position
        int fromPosition = currentPlayer.getPosition();

        // Move the player
        currentPlayer.move(stepsMoved);

        // Get ending position
        int toPosition = currentPlayer.getPosition();

        // Calculate points earned
        int pointsEarned = 0;
        if (toPosition >= 1 && toPosition <= 64) {
            pointsEarned = tilePoints[toPosition - 1];

            // Bonus points for landing on multiples of 5
            if (toPosition % 5 == 0 && toPosition < 64) {
                pointsEarned += 50;
            }

            // Add points to player
            currentPlayer.addPoints(pointsEarned);
        }

        // Create move record
        MoveRecord record = new MoveRecord(
                currentPlayer.getName(),
                diceRoll,
                probability,
                stepsMoved,
                fromPosition,
                toPosition,
                pointsEarned
        );

        // Store in stack
        moveHistory.push(record);

        // Check for winner
        if (currentPlayer.getPosition() >= 64) {
            gameOver = true;
            winner = currentPlayer;
            // Bonus points for winning
            currentPlayer.addPoints(200);
        } else {
            // Add player back to queue if game continues
            playerQueue.offer(currentPlayer);
        }

        return record;
    }

    public void resetGame() {
        gameOver = false;
        winner = null;
        moveHistory.clear();
        playerQueue.clear();

        // Reset all player positions and points
        for (Player player : players) {
            player.setPosition(1);
            player.resetPoints();
            playerQueue.offer(player);
        }
    }
}