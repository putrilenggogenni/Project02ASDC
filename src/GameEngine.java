import java.util.*;

public class GameEngine {
    private Queue<Player> playerQueue;
    private Stack<MoveRecord> moveHistory;
    private List<Player> players;
    private Random random;
    private boolean gameOver;
    private Player winner;
    private Ladder[] ladders;
    private DijkstraPathfinder pathfinder;
    private Leaderboard leaderboard;
    private long gameStartTime;

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
        this.leaderboard = new Leaderboard();
        this.gameStartTime = System.currentTimeMillis();

        // Generate exactly 5 random ladders on prime-numbered positions
        this.ladders = generateRandomLadders();

        // Initialize pathfinder with board size and ladders
        this.pathfinder = new DijkstraPathfinder(64, ladders);

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

    private Ladder[] generateRandomLadders() {
        List<Integer> primes = getPrimesUpTo(63);
        List<Ladder> ladderList = new ArrayList<>();

        // Colors for ladders
        java.awt.Color[] colors = {
                new java.awt.Color(255, 215, 0),    // Gold
                new java.awt.Color(255, 99, 71),    // Tomato
                new java.awt.Color(50, 205, 50),    // Lime Green
                new java.awt.Color(255, 105, 180),  // Hot Pink
                new java.awt.Color(0, 206, 209)     // Turquoise
        };

        Collections.shuffle(primes, random);

        // Create EXACTLY 5 ladders
        int numLadders = 5;
        int colorIndex = 0;
        int attempts = 0;

        for (int i = 0; i < primes.size() && ladderList.size() < numLadders && attempts < 100; i++) {
            int from = primes.get(i);
            attempts++;

            // Make sure ladder doesn't start too close to the end
            if (from >= 58) continue;

            // Ladder goes up between 8-20 positions
            int ladderLength = 8 + random.nextInt(13);
            int to = Math.min(from + ladderLength, 63);

            // Make sure 'to' is reasonable and we have a good ladder
            if (to - from >= 8) {
                ladderList.add(new Ladder(from, to, colors[colorIndex % colors.length]));
                colorIndex++;
            }
        }

        // If we couldn't get 5 ladders, fill up with any available primes
        while (ladderList.size() < 5 && primes.size() > ladderList.size()) {
            for (int prime : primes) {
                if (ladderList.size() >= 5) break;

                // Check if this prime is already used
                boolean used = false;
                for (Ladder l : ladderList) {
                    if (l.from == prime) {
                        used = true;
                        break;
                    }
                }

                if (!used && prime < 58) {
                    int to = Math.min(prime + 10, 63);
                    ladderList.add(new Ladder(prime, to, colors[ladderList.size() % colors.length]));
                }
            }
            break;
        }

        return ladderList.toArray(new Ladder[0]);
    }

    private List<Integer> getPrimesUpTo(int max) {
        List<Integer> primes = new ArrayList<>();
        for (int num = 2; num <= max; num++) {
            if (isPrime(num)) {
                primes.add(num);
            }
        }
        return primes;
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

    public Ladder[] getLadders() {
        return ladders;
    }

    public DijkstraPathfinder getPathfinder() {
        return pathfinder;
    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
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

        // Move the player (step by step animation will be handled by GUI)
        currentPlayer.move(stepsMoved);

        // Get ending position
        int toPosition = currentPlayer.getPosition();

        // Calculate points earned
        int pointsEarned = 0;
        boolean bonusTurn = false;

        if (toPosition >= 1 && toPosition <= 64) {
            pointsEarned = tilePoints[toPosition - 1];

            // Bonus points for landing on multiples of 5
            if (toPosition % 5 == 0 && toPosition < 64) {
                pointsEarned += 50;
                bonusTurn = true; // Player gets another turn!
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

        // Set bonus turn flag in record
        record.setBonusTurn(bonusTurn);

        // Store in stack
        moveHistory.push(record);

        // Check for winner
        if (currentPlayer.getPosition() >= 64) {
            gameOver = true;
            winner = currentPlayer;
            // Bonus points for winning
            currentPlayer.addPoints(200);

            // Calculate game time
            long gameTime = System.currentTimeMillis() - gameStartTime;
            currentPlayer.setCompletionTime(gameTime);

            // Update leaderboard
            leaderboard.addScore(currentPlayer.getName(), currentPlayer.getPoints(), gameTime);
        } else {
            // IMPORTANT: Only add player back if NOT getting bonus turn OR if they are
            // We'll handle bonus turn in GUI by calling playTurn again with same player
            playerQueue.offer(currentPlayer);
        }

        return record;
    }

    public void giveBonusTurn(Player player) {
        // Remove all players from queue temporarily
        List<Player> tempPlayers = new ArrayList<>();
        while (!playerQueue.isEmpty()) {
            tempPlayers.add(playerQueue.poll());
        }

        // Put bonus player first
        playerQueue.offer(player);

        // Add rest of players back (except the bonus player)
        for (Player p : tempPlayers) {
            if (p != player) {
                playerQueue.offer(p);
            }
        }
    }

    public void resetGame() {
        gameOver = false;
        winner = null;
        moveHistory.clear();
        playerQueue.clear();
        gameStartTime = System.currentTimeMillis();

        // Generate new random ladders (exactly 5)
        this.ladders = generateRandomLadders();
        this.pathfinder = new DijkstraPathfinder(64, ladders);

        // Reset all player positions and points to starting state
        for (Player player : players) {
            player.setPosition(1);
            player.resetPoints();
            player.setCompletionTime(0);
            player.resetMoves();
            playerQueue.offer(player);
        }
    }
}