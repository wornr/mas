package lab3.agents;

import jade.core.Agent;

@SuppressWarnings("serial")
public class ForkAgent extends Agent {
	
	private boolean pickedUp;
	
	@Override
	protected void setup() {
		
	}
	
	public boolean isPickedUp() {
		return pickedUp;
	}
}