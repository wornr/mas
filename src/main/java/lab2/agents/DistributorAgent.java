package lab2.agents;

import java.util.ArrayList;
import java.util.List;

import jade.core.Agent;
import lab2.behaviours.DistributeBehaviour;
import lab2.helpers.DFServiceHelper;
import lab2.helpers.MatrixHelper;
import lab2.models.CountingAgentInfo;
import lab2.models.Matrix;
import lab2.models.MatrixFragment;

@SuppressWarnings("serial")
public class DistributorAgent extends Agent {
	
	private List<MatrixFragment> matrixFragments;
	private List<CountingAgentInfo> countingAgents;
	private Matrix resultMatrix;
	
	@Override
	protected void setup() {
		DFServiceHelper.getInstance().register(this, "matrixDistributor", "distributor");
		
		Matrix mA = MatrixHelper.getInstance().readMatrixFile("A.txt");
		Matrix mB = MatrixHelper.getInstance().readMatrixFile("B.txt");
		matrixFragments = MatrixHelper.getInstance().generateFragments(mA, mB);
		resultMatrix = MatrixHelper.getInstance().prepareResultMatrix(mA, mB);
		
		countingAgents = new ArrayList<>();
		
		addBehaviour(new DistributeBehaviour(this));
	}

	public List<MatrixFragment> getMatrixFragments() {
		return matrixFragments;
	}
	
	public List<CountingAgentInfo> getCountingAgents() {
		return countingAgents;
	}
	
	public void setCountingAgents(List<CountingAgentInfo> countingAgents) {
		this.countingAgents = countingAgents;
	}
	
	public Matrix getResultMatrix() {
		return resultMatrix;
	}
}