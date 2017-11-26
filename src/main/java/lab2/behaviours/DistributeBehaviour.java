package lab2.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import lab2.agents.DistributorAgent;
import lab2.enums.CountingAgentStatus;
import lab2.enums.MatrixFragmentState;
import lab2.helpers.DFServiceHelper;
import lab2.models.CountingAgentInfo;
import lab2.models.Matrix;
import lab2.models.MatrixFragment;

@SuppressWarnings("serial")
public class DistributeBehaviour extends CyclicBehaviour {
	
	private DistributorAgent agent;
	private List<CountingAgentInfo> countingAgents;
	
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
			if (countingAgents != null) {
				for (CountingAgentInfo countingAgent : countingAgents) {
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
		countingAgents = tempCountingAgents;
		
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		ACLMessage msg = agent.receive(mt);
		if (msg != null) {
			for (CountingAgentInfo countingAgent : countingAgents) {
				if (countingAgent.getAgentId().equals(msg.getSender())) {
					countingAgent.setStatus(CountingAgentStatus.Ready);
				}
			}
		}
	}
	
	private void sendMatrix() {
		for (CountingAgentInfo countingAgent : countingAgents) {
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
					agent.getResultMatrix().setValue(mf.getRowIndex(), mf.getColIndex(), mf.getResult());
				System.out.println(agent.getLocalName() + ": otrzymalem wynik fragmentu (" + mf.getRowIndex() + ";" + mf.getColIndex() + ") od agenta " + msg.getSender().getLocalName());
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			
			//printMatrix(agent.getResultMatrix()); // wyswietlanie macierzy
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
			
			//printMatrix(agent.getResultMatrix()); // wyswietlanie macierzy
		}
	}
	
	// TODO przeniesc
	private void printMatrix(Matrix m) {
		for (double[] r : m.getValues()) {
			for (double v : r) {
				System.out.print(v + "\t");
			}
			System.out.println();
		}
	}
}