package lab2.agents;

import java.util.Random;

import jade.core.Agent;
import lab2.models.MatrixFragment;

@SuppressWarnings("serial")
public class CountingAgent extends Agent {
	
	private int delay = new Random().nextInt(1000) + 500;
	
	@Override
	protected void setup() {
		
	}
	
	private double calculate(MatrixFragment mf) {
		double result = 0.0d;
		
		for (int i = 0; i < mf.getSize(); i++) {
			result += mf.getCol()[i] * mf.getRow()[i];
		}
		
		return result;
	}
}