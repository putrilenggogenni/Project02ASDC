import java.util.*;

public class Leaderboard {
    private List<LeaderboardEntry> entries;
    private int highestScore;
    private String highestScorePlayer;
    private long fastestTime;
    private String fastestPlayer;

    public Leaderboard() {
        this.entries = new ArrayList<>();
        this.highestScore = 0;
        this.highestScorePlayer = "";
        this.fastestTime = Long.MAX_VALUE;
        this.fastestPlayer = "";
    }

    public void addScore(String playerName, int score, long completionTime) {
        LeaderboardEntry entry = new LeaderboardEntry(playerName, score, completionTime);
        entries.add(entry);

        // Update highest score
        if (score > highestScore) {
            highestScore = score;
            highestScorePlayer = playerName;
        }

        // Update fastest time
        if (completionTime < fastestTime && completionTime > 0) {
            fastestTime = completionTime;
            fastestPlayer = playerName;
        }

        // Sort entries by score (descending)
        entries.sort((a, b) -> Integer.compare(b.score, a.score));
    }

    public List<LeaderboardEntry> getTopScores(int limit) {
        return entries.stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<LeaderboardEntry> getFastestTimes(int limit) {
        return entries.stream()
                .filter(e -> e.completionTime > 0)
                .sorted((a, b) -> Long.compare(a.completionTime, b.completionTime))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    public int getHighestScore() {
        return highestScore;
    }

    public String getHighestScorePlayer() {
        return highestScorePlayer;
    }

    public long getFastestTime() {
        return fastestTime;
    }

    public String getFastestPlayer() {
        return fastestPlayer;
    }

    public String getFormattedFastestTime() {
        if (fastestTime == Long.MAX_VALUE) return "N/A";

        long seconds = fastestTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%d:%02d", minutes, seconds);
    }

    public void clear() {
        entries.clear();
        highestScore = 0;
        highestScorePlayer = "";
        fastestTime = Long.MAX_VALUE;
        fastestPlayer = "";
    }

    public static class LeaderboardEntry {
        public final String playerName;
        public final int score;
        public final long completionTime;
        public final Date timestamp;

        public LeaderboardEntry(String playerName, int score, long completionTime) {
            this.playerName = playerName;
            this.score = score;
            this.completionTime = completionTime;
            this.timestamp = new Date();
        }

        public String getFormattedTime() {
            if (completionTime <= 0) return "N/A";

            long seconds = completionTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;

            return String.format("%d:%02d", minutes, seconds);
        }
    }
}