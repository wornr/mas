package lab3.behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import lab3.agents.PhilosopherAgent;
import lab3.agents.TableAgent;

@SuppressWarnings("serial")
public class PhilosopherInitBehaviour extends CyclicBehaviour {

	private PhilosopherAgent agent;
	private boolean startTicker;
	
	public PhilosopherInitBehaviour(PhilosopherAgent agent) {
		this.agent = agent;
		this.startTicker = true;
	}
	
	@Override
	public void action() {
		if (!agent.isInitialized()) {
			findNearestForks();
		} else {
			if (startTicker) {
				agent.addBehaviour(new PhilosopherMainBehaviour(agent, agent.getReactionTime()));
				startTicker = false;
			}
			
			processMessages();
		}
	}
	
	private void findNearestForks() {
		if (TableAgent.getForks() != null && !TableAgent.getForks().isEmpty()) {
			int forksCount = TableAgent.getForks().size();
			
			for (AID fork : TableAgent.getForks()) {
				if (agent.getId() == Integer.parseInt(fork.getLocalName().substring(4))) {
					agent.setLeftFork(fork);
				} else {
					if (agent.getId() == 0) {
						if ((forksCount - 1) == Integer.parseInt(fork.getLocalName().substring(4))) {
							agent.setRightFork(fork);
						}
					} else {
						if ((agent.getId() - 1) == Integer.parseInt(fork.getLocalName().substring(4))) {
							agent.setRightFork(fork);
						}
					}
				}
			}
		}
	}
	
	private void processMessages() {
		ACLMessage msg = agent.receive();
		if (msg != null) {
			if (ACLMessage.AGREE == msg.getPerformative()) {
				if (msg.getSender().equals(agent.getLeftFork())) {
					agent.setLeftPickedUp(true);
					System.out.println(agent.getLocalName() + ": wzialem lewy widelec.");
				} else if (msg.getSender().equals(agent.getRightFork())) {
					System.out.println(agent.getLocalName() + ": wzialem prawy widelec.");
					agent.setRightPickedUp(true);
				}
			} else if (ACLMessage.CANCEL == msg.getPerformative()) {
				System.out.println(agent.getLocalName() + ": widelec zajety, odkladam wszystkie widelce.");
				agent.freeForks();
			} else if (ACLMessage.ACCEPT_PROPOSAL == msg.getPerformative()) {
				System.out.println(agent.getLocalName() + ": zjadlem kebaba i odlozylem wszystkie widelce.");
				agent.addEatenKebab();
				agent.freeForks();
			}
		} else {
			block();
		}
	}
}