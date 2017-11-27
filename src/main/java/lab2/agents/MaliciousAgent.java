package lab2.agents;

import lab2.models.MatrixFragment;

@SuppressWarnings("serial")
public class MaliciousAgent extends CountingAgent {
	
	@Override
	public double calculate(MatrixFragment mf) {
		return 0.0d;
	}
}