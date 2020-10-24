package graphics;

import java.util.Arrays;
import java.util.List;

import org.ejml.simple.SimpleBase;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

import graphics.Graph.NodeGroup;

public final class SpectralPartitioningManager {

	private Graph graph;

	private NodeGroup[] nodeGroups;
	
    public SpectralPartitioningManager(Graph graph) {
    	this.graph = graph;
    }

    private SimpleMatrix getDegreeMatrix() {
    	double[] diagValues = new double[graph.getTotalNodes()];
    	for (int i = 0; i < graph.getTotalNodes(); i++) {
    		Node node = graph.getNode(i);
    		diagValues[i] = node.getDegree();
    	}
    	return SimpleMatrix.diag(diagValues);
    }

    private SimpleMatrix getAdjacencyMatrix() {
        int n = graph.getTotalNodes();
        SimpleMatrix adjacencyMatrix = new SimpleMatrix(n, n);

        for (int i = 0; i < n; i++) {
            graph.getNode(i).forEachAdjacent((node1, node2) -> {
            	adjacencyMatrix.set(node1.getID() + node2.getID() * n, 1);
            });
        }
        return adjacencyMatrix;
    }

    public void calculatePartitioning() {
    	//LaplacianMatrix = DegreeMatrix - AdjacencyMatrix
    	SimpleMatrix laplacianMatrix = new SimpleMatrix(graph.getTotalNodes(), graph.getTotalNodes());
        laplacianMatrix.set(getDegreeMatrix().minus(getAdjacencyMatrix()));
        
        //Get eigenpairs from the laplacian matrix
        SimpleEVD<SimpleMatrix> eigenPairs = laplacianMatrix.eig();
        
        //Find the second lowest eigenValue
        //	Note(1): 0 is always an eigenvalue of a laplacian matrix (trivial case)
        //  Note(2): all eigenvalues of a laplacian matrix are real constants
        double minEigenValue = Double.MAX_VALUE;
        int indexOfMin = -1;
        final int totalEigenValues = eigenPairs.getNumberOfEigenvalues();
        for (int i = 0; i < totalEigenValues; i++) {
        	double eigenValue = eigenPairs.getEigenvalue(i).real;
        	if (eigenValue != 0 && eigenValue < minEigenValue) {
        		minEigenValue = eigenValue;
        		indexOfMin = i;
        	}
        }
        if (indexOfMin == -1) { //In this case, the only eigenValue is 0 => graph has no connections
        	return;
        }
        
        //Get corresponding eigenVector of second lowest eigenvalue
        SimpleBase<SimpleMatrix> eigenVector = eigenPairs.getEigenVector(indexOfMin); 
        
        //Calculate median value and preserve node index order
        double[] relevantSpectrum = new double[eigenVector.numRows()];
        int[] relevantIndices = new int[eigenVector.numRows()];
        for (int i = 0; i < eigenVector.numRows(); i++) {
        	relevantSpectrum[i] = eigenVector.get(i, 0);
        	relevantIndices[i] = i;
        }
        for (int i = 0; i < relevantSpectrum.length - 1; i++) { //Bubble sort
        	for (int j = 0; j < relevantSpectrum.length - i - 1; j++) {
        		if (relevantSpectrum[j] > relevantSpectrum[j + 1]) {
        			//Swap relevantSpectrum values and indices
        			double tempValue = relevantSpectrum[j];
        			relevantSpectrum[j] = relevantSpectrum[j + 1];
        			relevantSpectrum[j + 1] = tempValue; 
        			
        			int tempIndex = relevantIndices[j];
        			relevantIndices[j] = relevantIndices[j + 1];
        			relevantIndices[j + 1] = tempIndex;
        		}
        	}
        }
//        double median; //Calculate median from sorted list
//        if (relevantSpectrum.length % 2 == 1) {
//        	median = relevantSpectrum[(int) (relevantSpectrum.length / 2)];
//        } else {
//        	median = relevantSpectrum[relevantSpectrum.length / 2] + relevantSpectrum[relevantSpectrum.length / 2]
//        }
        
        System.out.println("---------------");
        for (int i = 0; i < relevantIndices.length; i++) {
        	System.out.println(relevantIndices[i] + "::" + relevantSpectrum[i]);
        }
        
        this.nodeGroups = new NodeGroup[graph.getTotalNodes()];
    	for (int i = 0; i < relevantIndices.length; i++) {
    		
    		if (i < relevantIndices.length / 2) {
    			this.nodeGroups[relevantIndices[i]] = NodeGroup.NODE_A;
    		} else {
    			this.nodeGroups[relevantIndices[i]] = NodeGroup.NODE_B;
    		}
//    		double entryOfEigenVector = eigenVector.get(i, 0);
//    		if (entryOfEigenVector < 0) {
//    			this.nodeGroups[i] = NodeGroup.NODE_A;
//    		} else {
//    			this.nodeGroups[i] = NodeGroup.NODE_B;
//    		}
    	}
    }
    
    public NodeGroup[] getPartitionGroups() {
    	if (this.nodeGroups == null) return new NodeGroup[0];
    	return Arrays.copyOf(this.nodeGroups, this.nodeGroups.length);
    }
    
}
