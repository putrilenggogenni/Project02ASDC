import java.awt.Color;

public class Ladder {
    int from;
    int to;
    Color color;
    String name;

    public Ladder(int from, int to, Color color) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.name = "Ladder " + from + "→" + to;
    }

    public int getFrom() { return from; }
    public int getTo() { return to; }
    public Color getColor() { return color; }
    public String getName() { return name; }
    public int getLength() { return to - from; }

    @Override
    public String toString() {
        return name + " (+" + getLength() + " tiles)";
    }
}