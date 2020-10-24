package graphics;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Node {

    private final int id;
    private List<Node> adjacentNodes = new ArrayList<>();

    private int x;
    private int y;

    boolean highlighted;

    Color partitionColor = Math.random() < 0.5 ? Graph.A_NODE_COLOR : Graph.B_NODE_COLOR;

    public Node(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getID() {
        return id;
    }

    public int getDegree() {
        return adjacentNodes.size();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addAdjacency(Node n) {
        this.adjacentNodes.add(n);
    }

    public void forEachAdjacent(BiConsumer<Node, Node> action) {
        for (Node n: this.adjacentNodes) {
            action.accept(this, n);
        }
    }

    public void render(Graphics graphic) {
        graphic.setColor(partitionColor);
        graphic.fillArc(x - Graph.NODE_RADIUS, y - Graph.NODE_RADIUS,
                Graph.NODE_RADIUS * 2,
                Graph.NODE_RADIUS * 2,
                0, 360);
        if (highlighted) {
            graphic.drawArc(x - Graph.HIGHLIGHT_RADIUS, y - Graph.HIGHLIGHT_RADIUS,
                    Graph.HIGHLIGHT_RADIUS * 2,
                    Graph.HIGHLIGHT_RADIUS * 2,
                    0, 360);
        }
    }

}
