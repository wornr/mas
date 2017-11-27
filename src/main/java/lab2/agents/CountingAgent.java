package lab2.agents;

import java.util.Random;

import jade.core.Agent;
import lab2.behaviours.CountingBehaviour;
import lab2.enums.CountingAgentStatus;
import lab2.helpers.DFServiceHelper;
import lab2.models.MatrixFragment;

@SuppressWarnings("serial")
public class CountingAgent extends Agent {
	
	private int delay;
	private CountingAgentStatus status;
	
	@Override
	protected void setup() {
		
		delay = new Random().nextInt(1000) + 500;
		status = CountingAgentStatus.Busy;
		
		DFServiceHelper.getInstance().register(this, "matrixCalculator", "calculator");
		
		addBehaviour(new CountingBehaviour(this));
	}
	
	public double calculate(MatrixFragment mf) {
		double result = 0.0d;
		
		for (int i = 0; i < mf.getSize(); i++) {
			result += mf.getCol()[i] * mf.getRow()[i];
		}
		
		return result;
	}
	
	public int getDelay() {
		return delay;
	}

	public CountingAgentStatus getStatus() {
		return status;
	}

	public void setStatus(CountingAgentStatus status) {
		this.status = status;
	}
}