package lab2.agents;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import lab2.behaviours.DistributeBehaviour;
import lab2.helpers.DFServiceHelper;
import lab2.helpers.MatrixFragmentGenerator;
import lab2.models.Matrix;
import lab2.models.MatrixFragment;

@SuppressWarnings("serial")
public class DistributorAgent extends Agent {
	
	private List<MatrixFragment> matrixFragments;
	private List<AID> countingAgents;
	private Matrix resultMatrix;
	
	@Override
	protected void setup() {
		
		// TODO dodac jakies sensowniejsze ladowanie macierzy
		Matrix mA = new Matrix(2, 3, new double[][] {{8, 2 ,6}, {1, 5, 3}});
		Matrix mB = new Matrix(3, 2, new double[][] {{8, 2}, {1, 5}, {4, 0}});
		resultMatrix = new Matrix(2, 2);
		
		countingAgents = new ArrayList<>();
		matrixFragments = MatrixFragmentGenerator.getInstance().generateFragments(mA, mB);
		
		DFServiceHelper.getInstance().register(this, "matrixDistributor", "distributor");
		
		addBehaviour(new DistributeBehaviour(this));
	}

	public List<MatrixFragment> getMatrixFragments() {
		return matrixFragments;
	}
	
	public List<AID> getCountingAgents() {
		return countingAgents;
	}
	
	public Matrix getResultMatrix() {
		return resultMatrix;
	}
}