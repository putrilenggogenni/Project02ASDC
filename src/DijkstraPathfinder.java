import java.util.*;

public class DijkstraPathfinder {
    private int boardSize;
    private Ladder[] ladders;
    private Map<Integer, Integer> ladderMap; // from -> to

    public DijkstraPathfinder(int boardSize, Ladder[] ladders) {
        this.boardSize = boardSize;
        this.ladders = ladders;
        this.ladderMap = new HashMap<>();

        // Build ladder map for quick lookup
        for (Ladder ladder : ladders) {
            ladderMap.put(ladder.from, ladder.to);
        }
    }
    //test

    /**
     * Find shortest path from start to goal using Dijkstra's algorithm
     * Returns the distance (number of moves)
     */
    public int shortestDistance(int start, int goal) {
        if (start == goal) return 0;
        if (start > goal || start < 1 || goal > boardSize) return Integer.MAX_VALUE;

        // Priority queue: (distance, position)
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.distance - b.distance);
        Map<Integer, Integer> distances = new HashMap<>();

        // Initialize
        pq.offer(new Node(start, 0));
        distances.put(start, 0);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int pos = current.position;
            int dist = current.distance;

            // If we reached the goal
            if (pos == goal) {
                return dist;
            }

            // Skip if we've already processed this with a better distance
            if (dist > distances.getOrDefault(pos, Integer.MAX_VALUE)) {
                continue;
            }

            // Try all possible dice rolls (1-6)
            for (int diceRoll = 1; diceRoll <= 6; diceRoll++) {
                int nextPos = pos + diceRoll;

                // Check bounds
                if (nextPos > boardSize) continue;

                // Check if there's a ladder at this position
                if (ladderMap.containsKey(nextPos)) {
                    nextPos = ladderMap.get(nextPos);
                }

                int newDist = dist + 1;

                // Update if we found a shorter path
                if (newDist < distances.getOrDefault(nextPos, Integer.MAX_VALUE)) {
                    distances.put(nextPos, newDist);
                    pq.offer(new Node(nextPos, newDist));
                }
            }
        }

        return distances.getOrDefault(goal, Integer.MAX_VALUE);
    }

    /**
     * Find the complete shortest path from start to goal
     * Returns a list of positions
     */
    public List<Integer> shortestPath(int start, int goal) {
        if (start == goal) {
            return Arrays.asList(start);
        }

        // Priority queue and tracking
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.distance - b.distance);
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();

        pq.offer(new Node(start, 0));
        distances.put(start, 0);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int pos = current.position;
            int dist = current.distance;

            if (pos == goal) {
                break;
            }

            if (dist > distances.getOrDefault(pos, Integer.MAX_VALUE)) {
                continue;
            }

            for (int diceRoll = 1; diceRoll <= 6; diceRoll++) {
                int nextPos = pos + diceRoll;

                if (nextPos > boardSize) continue;

                // Apply ladder if present
                if (ladderMap.containsKey(nextPos)) {
                    nextPos = ladderMap.get(nextPos);
                }

                int newDist = dist + 1;

                if (newDist < distances.getOrDefault(nextPos, Integer.MAX_VALUE)) {
                    distances.put(nextPos, newDist);
                    previous.put(nextPos, pos);
                    pq.offer(new Node(nextPos, newDist));
                }
            }
        }

        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        Integer current = goal;

        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }

        return path.isEmpty() || path.get(0) != start ? new ArrayList<>() : path;
    }

    /**
     * Get step-by-step positions from current to target
     * This generates intermediate positions for animation
     */
    public List<Integer> getStepByStepPath(int from, int to) {
        List<Integer> path = new ArrayList<>();

        if (from == to) {
            path.add(from);
            return path;
        }

        int step = (to > from) ? 1 : -1;
        int current = from;

        while (current != to) {
            path.add(current);
            current += step;
        }
        path.add(to);

        return path;
    }

    /**
     * Inner class for priority queue nodes
     */
    private static class Node {
        int position;
        int distance;

        Node(int position, int distance) {
            this.position = position;
            this.distance = distance;
        }
    }

    /**
     * Calculate expected moves to finish from any position
     */
    public double expectedMovesToFinish(int position) {
        if (position >= boardSize) return 0;

        int minMoves = shortestDistance(position, boardSize);

        // Account for probability of backward moves
        // Expected moves is roughly minMoves * 1.5 due to 30% backward probability
        return minMoves * 1.5;
    }
}