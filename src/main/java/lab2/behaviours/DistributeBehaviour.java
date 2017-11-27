package lab2.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import lab2.agents.DistributorAgent;
import lab2.enums.CountingAgentStatus;
import lab2.enums.MatrixFragmentState;
import lab2.helpers.DFServiceHelper;
import lab2.models.CountingAgentInfo;
import lab2.models.MatrixFragment;

@SuppressWarnings("serial")
public class DistributeBehaviour extends CyclicBehaviour {
	
	private DistributorAgent agent;
	
	public DistributeBehaviour(DistributorAgent agent) {
		super();
		
		this.agent = agent;
	}

	@Override
	public void action() {
		refreshCountingAgents();
		sendMatrix();
		receiveResult();
		receiveFailure();
	}
	
	private void refreshCountingAgents() {
		List<CountingAgentInfo> newCountingAgents = DFServiceHelper.getInstance().findCountingAgents(agent, "calculator");
		List<CountingAgentInfo> tempCountingAgents = new ArrayList<>();
		
		for (CountingAgentInfo newCountingAgent : newCountingAgents) {
			boolean exists = false;
			if (agent.getCountingAgents() != null) {
				for (CountingAgentInfo countingAgent : agent.getCountingAgents()) {
					if (newCountingAgent.getAgentId().equals(countingAgent.getAgentId())) {
						tempCountingAgents.add(countingAgent);
						exists = true;
						break;
					}
				}
			}
			
			if (!exists) {
				tempCountingAgents.add(newCountingAgent);
			}
		}
		agent.setCountingAgents(tempCountingAgents);
		
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		ACLMessage msg = agent.receive(mt);
		if (msg != null) {
			for (CountingAgentInfo countingAgent : agent.getCountingAgents()) {
				if (countingAgent.getAgentId().equals(msg.getSender())) {
					ACLMessage reply = msg.createReply();
					if (!CountingAgentStatus.Banned.equals(countingAgent.getStatus())) {
						countingAgent.setStatus(CountingAgentStatus.Ready);
						reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					} else {
						reply.setPerformative(ACLMessage.CANCEL);
					}
					agent.send(reply);
				}
			}
		}
	}
	
	private void sendMatrix() {
		for (CountingAgentInfo countingAgent : agent.getCountingAgents()) {
			if (CountingAgentStatus.Ready.equals(countingAgent.getStatus())) {
				ACLMessage msg = new ACLMessage(ACLMessage.CFP);
				msg.addReceiver(countingAgent.getAgentId());
				
				try {
					for (MatrixFragment mf : agent.getMatrixFragments()) {
						if (MatrixFragmentState.InQueue.equals(mf.getState())) {
							mf.setState(MatrixFragmentState.Sent);
							msg.setContentObject(mf);
							msg.setReplyWith(countingAgent.getAgentId().getLocalName());
							countingAgent.setStatus(CountingAgentStatus.Busy);
							
							System.out.println(agent.getLocalName() + ": wysylam fragment (" + mf.getRowIndex() + ";" + mf.getColIndex() + ") do agenta " + countingAgent.getAgentId().getLocalName());
							agent.send(msg);
							
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void receiveResult() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
		ACLMessage msg = agent.receive(mt);
		if (msg != null) {
			try {
				MatrixFragment mf = (MatrixFragment) msg.getContentObject();
				if (MatrixFragmentState.Calculated.equals(mf.getState()))
					if (new Random().nextInt(100) > 20) {
						agent.addBehaviour(new VerificationBehaviour(msg.getSender(), mf));
					} else {
						agent.getResultMatrix().setValue(mf.getRowIndex(), mf.getColIndex(), mf.getResult());
						System.out.println(agent.getLocalName() + ": otrzymalem wynik fragmentu (" + mf.getRowIndex() + ";" + mf.getColIndex() + ") od agenta " + msg.getSender().getLocalName());
					}
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			
			System.out.println(agent.getResultMatrix()); // wyswietlanie macierzy
		}
	}
	
	private void receiveFailure() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.FAILURE);
		ACLMessage msg = agent.receive(mt);
		if (msg != null) {
			try {
				MatrixFragment received = (MatrixFragment) msg.getContentObject();
				for (MatrixFragment mf : agent.getMatrixFragments()) {
					if (mf.getColIndex() == received.getColIndex() && mf.getRowIndex() == received.getRowIndex()) {
						mf.setState(MatrixFragmentState.InQueue);
						break;
					}						
				}
				System.out.println(agent.getLocalName() + ": otrzymalem zgloszenie bledu od agenta " + msg.getSender().getLocalName());
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			
			// System.out.println(agent.getResultMatrix()); // wyswietlanie macierzy
		}
	}
}