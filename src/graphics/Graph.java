package graphics;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

public class Graph {
	
	public static enum NodeGroup {
		NODE_A,
		NODE_B;
	}

    public static final Color A_NODE_COLOR = Color.decode("0xf55c45");
    public static final Color B_NODE_COLOR = Color.decode("0x45cff5");
    public static final Color EDGE_COLOR = Color.WHITE;

    public static final int NODE_RADIUS = 10;
    public static final int HIGHLIGHT_RADIUS = 15;

    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    private int firstSelectedNode = -1;

    private final Window window;
    private final Position currentMousePosition;
    
    private final SpectralPartitioningManager partitionManager;

    public Graph(Window window) {
        this.window = window;
        this.currentMousePosition = window.getMouse().currentPos;
        
        this.partitionManager = new SpectralPartitioningManager(this);
    }

    public int getTotalNodes() {
        return nodes.size();
    }

    public Node getNode(int nodeID) {
        return nodes.get(nodeID);
    }
    
    private void updateNodePartitions(NodeGroup[] partitionGroups) {
    	for (int i = 0; i < partitionGroups.length; i++) {
    		switch (partitionGroups[i]) {
    		case NODE_A:
    			nodes.get(i).partitionColor = A_NODE_COLOR;
    			break;
    		case NODE_B:
    			nodes.get(i).partitionColor = B_NODE_COLOR;
    			break;
    		}
    	}
    }

    public Node addNode(int x, int y) {
        Node node = new Node(nodes.size(), x, y);
        nodes.add(node);
        
        this.partitionManager.calculatePartitioning();
        this.updateNodePartitions(this.partitionManager.getPartitionGroups());
        return node;
    }

    public void addEdge(int n1, int n2) {
        Node node1 = nodes.get(n1);
        Node node2 = nodes.get(n2);
        Edge edge = new Edge(node1, node2);
        edges.add(edge);
        node1.addAdjacency(node2);
        node2.addAdjacency(node1);
        
        this.partitionManager.calculatePartitioning();
        this.updateNodePartitions(this.partitionManager.getPartitionGroups());
    }

    public void update() {
        if (this.window.getMouse().isRightClicked()) {
            addNode(currentMousePosition.x, currentMousePosition.y);
        } else if (this.window.getMouse().isLeftClicked()) {
            if (firstSelectedNode == -1) {

            }
            boolean hasSelected = false;
            for (Node node: nodes) {
                if (hasSelected) {
                    node.highlighted = false;
                    continue;
                }
                if (MathUtils.distance(
                        currentMousePosition.x, currentMousePosition.y,
                        node.getX(), node.getY()) < HIGHLIGHT_RADIUS * 1.5) {
                    if (firstSelectedNode == -1) {
                        node.highlighted = true;
                        this.firstSelectedNode = node.getID();
                    } else {
                        this.addEdge(firstSelectedNode, node.getID());
                        firstSelectedNode = -1;
                    }
                    hasSelected = true;
                } else {
                    node.highlighted = false;
                }
            }
            if (!hasSelected) this.firstSelectedNode = -1;
        }
    }

    public void render(Graphics graphics) {
        for (Edge edge: edges) {
            edge.render(graphics);
        }
        for (Node node: nodes) {
            node.render(graphics);
        }
    }
}
