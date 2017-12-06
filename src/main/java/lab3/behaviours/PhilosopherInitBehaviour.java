package lab3.behaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import lab3.agents.PhilosopherAgent;
import lab3.agents.TableAgent;

@SuppressWarnings("serial")
public class PhilosopherInitBehaviour extends CyclicBehaviour {

	private PhilosopherAgent agent;
	
	public PhilosopherInitBehaviour(PhilosopherAgent agent) {
		this.agent = agent;
	}
	
	@Override
	public void action() {
		if (!agent.isInitialized())
			findNearestForks();
		else {
			agent.addBehaviour(new PhilosopherMainBehaviour(agent, agent.getReactionTime()));
			agent.removeBehaviour(this);
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
}