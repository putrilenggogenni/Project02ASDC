public class MoveRecord {
    private String playerName;
    private int diceRoll;
    private double probability;
    private int stepsMoved;
    private int fromPosition;
    private int toPosition;
    private boolean usedLadder;
    private int ladderFrom;
    private int ladderTo;
    private int pointsEarned;

    public MoveRecord(String playerName, int diceRoll, double probability,
                      int stepsMoved, int fromPosition, int toPosition, int pointsEarned) {
        this.playerName = playerName;
        this.diceRoll = diceRoll;
        this.probability = probability;
        this.stepsMoved = stepsMoved;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.usedLadder = false;
        this.pointsEarned = pointsEarned;
    }

    public void setLadder(int from, int to) {
        this.usedLadder = true;
        this.ladderFrom = from;
        this.ladderTo = to;
        this.toPosition = to; // Update final position
    }

    public boolean hasLadder() {
        return usedLadder;
    }

    public int getLadderFrom() {
        return ladderFrom;
    }

    public int getLadderTo() {
        return ladderTo;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getDiceRoll() {
        return diceRoll;
    }

    public double getProbability() {
        return probability;
    }

    public int getStepsMoved() {
        return stepsMoved;
    }

    public int getFromPosition() {
        return fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public boolean isForward() {
        return stepsMoved > 0;
    }
    private boolean bonusTurn = false;

    public void setBonusTurn(boolean bonusTurn) {
        this.bonusTurn = bonusTurn;
    }

    public boolean hasBonusTurn() {
        return bonusTurn;
    }
    @Override
    public String toString() {
        String direction = isForward() ? "forward" : "backward";
        String result = String.format("%s rolled %d (prob: %.2f) - moved %s %d steps (%d → %d) +%d pts",
                playerName, diceRoll, probability, direction,
                Math.abs(stepsMoved), fromPosition, toPosition, pointsEarned);

        if (usedLadder) {
            result += String.format(" 🪜 CLIMBED LADDER (%d → %d)", ladderFrom, ladderTo);
        }

        return result;
    }
}