package lab3.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import lab3.agents.TableAgent;

@SuppressWarnings("serial")
public class TableBehaviour extends CyclicBehaviour {

	private TableAgent agent;
	
	public TableBehaviour(TableAgent agent) {
		this.agent = agent;
	}
	
	@Override
	public void action() {
		ACLMessage msg = agent.receive();
		if (msg != null) {
			if (ACLMessage.CFP == msg.getPerformative()) {
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				agent.send(reply);
				
				agent.removeKebab();
			}
		} else {
			block();
		}
	}
}