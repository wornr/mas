package lab3.behaviours;

import java.util.Random;

import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import lab3.agents.PhilosopherAgent;

@SuppressWarnings("serial")
public class PhilosopherMainBehaviour extends TickerBehaviour {

	private PhilosopherAgent agent;

	public PhilosopherMainBehaviour(PhilosopherAgent agent, long period) {
		super(agent, period);
		
		this.agent = agent;
	}

	@Override
	protected void onTick() {
		// TODO wlasciwa logika
		if (!agent.isLeftPickedUp() && !agent.isRightPickedUp()) {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			
			if (new Random().nextInt(1) == 0) {
				msg.addReceiver(agent.getLeftFork());
			} else {
				msg.addReceiver(agent.getRightFork());
			}
			
			agent.send(msg);
		} else if (!agent.isLeftPickedUp() && agent.isRightPickedUp()) {
			// TODO obsluga w przypadku podniesienia prawego widelca
		} else if (agent.isLeftPickedUp() && !agent.isRightPickedUp()) {
			// TODO obsulga w przypadku podniesienia lewego widelca
		} else {
			// TODO obsluga w przypadku posiadania dwoch widelcow
		}
		
	}
}