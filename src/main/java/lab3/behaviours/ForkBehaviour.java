package lab3.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import lab3.agents.ForkAgent;

@SuppressWarnings("serial")
public class ForkBehaviour extends CyclicBehaviour {

	private ForkAgent agent;
	
	public ForkBehaviour(ForkAgent agent) {
		this.agent = agent;
	}
	
	@Override
	public void action() {
		ACLMessage msg = agent.receive();
		if (msg != null) {
			if (ACLMessage.REQUEST == msg.getPerformative()) {
				ACLMessage reply = msg.createReply();
				
				if (!agent.isPickedUp()) {
					reply.setPerformative(ACLMessage.AGREE);
					agent.setPickedUp(true);
				} else {
					reply.setPerformative(ACLMessage.CANCEL);
				}
				
				agent.send(reply);
			} else if (ACLMessage.INFORM == msg.getPerformative()) {
				agent.setPickedUp(false);
			}
		} else {
			block();
		}
	}
}