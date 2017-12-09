package lab3.behaviours;

import java.util.Random;

import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import lab2.helpers.DFServiceHelper;
import lab3.agents.PhilosopherAgent;
import lab3.agents.TableAgent;

@SuppressWarnings("serial")
public class PhilosopherMainBehaviour extends TickerBehaviour {

	private PhilosopherAgent agent;

	public PhilosopherMainBehaviour(PhilosopherAgent agent, long period) {
		super(agent, period);
		
		this.agent = agent;
	}

	@Override
	protected void onTick() {
		if (TableAgent.getKebabs() > 0) {
			if (!agent.isLeftPickedUp() && !agent.isRightPickedUp()) {
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				
				int side = new Random().nextInt(1);
				if (side == 0) {
					msg.addReceiver(agent.getLeftFork());
				} else {
					msg.addReceiver(agent.getRightFork());
				}
				
				//System.out.println(agent.getLocalName() + ": Pobieram " + (side == 0 ? "lewy" : "prawy") + " widelec.");
				agent.send(msg);
			} else if (!agent.isLeftPickedUp() && agent.isRightPickedUp()) {
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.addReceiver(agent.getLeftFork());
				
				//System.out.println(agent.getLocalName() + ": Pobieram lewy widelec.");
				agent.send(msg);
			} else if (agent.isLeftPickedUp() && !agent.isRightPickedUp()) {
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.addReceiver(agent.getRightFork());
				
				//System.out.println(agent.getLocalName() + ": Pobieram prawy widelec.");
				agent.send(msg);
			} else {
				ACLMessage msg = new ACLMessage(ACLMessage.CFP);
				msg.addReceiver(DFServiceHelper.getInstance().findAgent(agent, "table"));
				
				//System.out.println(agent.getLocalName() + ": Zjadam kebaba i oddaje widelce.");
				agent.send(msg);
			}
		} else {
			agent.freeForks();
			System.out.println(agent.getLocalName() + " : " + agent.getEatenKebabs());
			agent.removeBehaviour(this);
		}
	}
}