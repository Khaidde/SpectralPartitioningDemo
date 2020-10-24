package graphics;

import java.awt.*;

public class Edge {

    private Node n1;
    private Node n2;

    public Edge(Node n1, Node n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    public void render(Graphics graphic) {
        graphic.setColor(Graph.EDGE_COLOR);
        graphic.drawLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
    }
}
