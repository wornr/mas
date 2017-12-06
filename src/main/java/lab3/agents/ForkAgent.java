package lab3.agents;

import jade.core.Agent;
import lab3.behaviours.ForkBehaviour;

@SuppressWarnings("serial")
public class ForkAgent extends Agent {
	
	private boolean pickedUp;
	
	@Override
	protected void setup() {
		addBehaviour(new ForkBehaviour(this));
	}
	
	public boolean isPickedUp() {
		return pickedUp;
	}
	
	public void setPickedUp(boolean pickedUp) {
		this.pickedUp = pickedUp;
	}
}