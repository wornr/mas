package lab3.agents;

import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import lab3.behaviours.PhilosopherInitBehaviour;

@SuppressWarnings("serial")
public class PhilosopherAgent extends Agent {
	
	private int id;
	private int reactionTime;
	
	private AID leftFork;
	private AID rightFork;
	
	private boolean leftPickedUp;
	private boolean rightPickedUp;
	
	private int eatenKebabs;
	
	@Override
	protected void setup() {
		id = Integer.parseInt(getLocalName().substring(11));
		reactionTime = new Random().nextInt(1000) + 500;
		
		addBehaviour(new PhilosopherInitBehaviour(this));
	}
	
	public void freeForks() {
		if (isLeftPickedUp()) {
			ACLMessage leftMsg = new ACLMessage(ACLMessage.INFORM);
			leftMsg.addReceiver(getLeftFork());
			send(leftMsg);
			setLeftPickedUp(false);
		}
		
		if (isRightPickedUp()) { 
			ACLMessage rightMsg = new ACLMessage(ACLMessage.INFORM);
			rightMsg.addReceiver(getRightFork());
			send(rightMsg);
			setRightPickedUp(false);
		}
	}
	
	public int getId() {
		return id;
	}
	
	public int getReactionTime() {
		return reactionTime;
	}

	public AID getLeftFork() {
		return leftFork;
	}
	
	public void setLeftFork(AID leftFork) {
		this.leftFork = leftFork;
	}
	
	public AID getRightFork() {
		return rightFork;
	}
	
	public void setRightFork(AID rightFork) {
		this.rightFork = rightFork;
	}
	
	public boolean isInitialized() {
		return leftFork != null && rightFork != null;
	}
	
	public boolean isLeftPickedUp() {
		return leftPickedUp;
	}
	
	public void setLeftPickedUp(boolean leftPickedUp) {
		this.leftPickedUp = leftPickedUp;
	}
	
	public boolean isRightPickedUp() {
		return rightPickedUp;
	}
	
	public void setRightPickedUp(boolean rightPickedUp) {
		this.rightPickedUp = rightPickedUp;
	}
	
	public int getEatenKebabs() {
		return eatenKebabs;
	}
	
	public void addEatenKebab() {
		eatenKebabs++;
	}
}