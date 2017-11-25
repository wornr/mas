package lab2.agents;

import jade.core.Agent;
import lab2.models.Matrix;

@SuppressWarnings("serial")
public class DistributorAgent extends Agent {
	
	@Override
	protected void setup() {
		
		Matrix mA = new Matrix(2, 3, new double[][] {{8, 2 ,6}, {1, 5, 3}});
		Matrix mB = new Matrix(3, 2, new double[][] {{8, 2}, {1, 5}, {4, 0}});
	}
}