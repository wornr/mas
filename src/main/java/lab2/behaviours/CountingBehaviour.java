package lab2.behaviours;

import java.io.IOException;
import java.util.Random;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import lab2.agents.CountingAgent;
import lab2.enums.CountingAgentStatus;
import lab2.helpers.DFServiceHelper;
import lab2.models.MatrixFragment;

@SuppressWarnings("serial")
public class CountingBehaviour extends CyclicBehaviour {

	private CountingAgent agent;
	private MatrixFragment mf;
	
	public CountingBehaviour(CountingAgent agent) {
		super();
		
		this.agent = agent;
	}
	
	@Override
	public void action() {
		switch(agent.getStatus()) {
			case Busy:
				updateStatus();
				break;
			case Ready:
				processMatrix();
				break;
		}
	}
	
	private void updateStatus() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(DFServiceHelper.getInstance().findAgent(agent, "distributor"));
		
		System.out.println(agent.getLocalName() + ": zglaszam gotowosc");
		agent.send(msg);
		
		agent.setStatus(CountingAgentStatus.Ready);
	}
	
	private void processMatrix() {
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchReplyWith(agent.getLocalName()), MessageTemplate.MatchPerformative(ACLMessage.CFP));
		ACLMessage msg = agent.receive(mt);
		if (msg != null) {
			ACLMessage reply = msg.createReply();
			agent.setStatus(CountingAgentStatus.Busy);
			try {
				mf = (MatrixFragment) msg.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			
			if (new Random().nextInt(100) > 20) {
				reply.setPerformative(ACLMessage.CONFIRM);
				
				System.out.println(agent.getLocalName() + ": rozpoczynam obliczenia");
				mf.setResult(calculate(mf));
				try {
					reply.setContentObject(mf);
				} catch (IOException e1) {
					e1.printStackTrace();
				}				
				
				System.out.println(agent.getLocalName() + ": oczekuje " + agent.getDelay() + "ms");
				agent.doWait(agent.getDelay());
				
				System.out.println(agent.getLocalName() + ": przesylam wynik");
				agent.send(reply);
			} else {
				reply.setPerformative(ACLMessage.FAILURE);
				
				System.out.println(agent.getLocalName() + ": nastapila awaria");
				try {
					reply.setContentObject(mf);
				} catch (IOException e) {
					e.printStackTrace();
				}
				agent.send(reply);
				
				System.out.println(agent.getLocalName() + ": oczekuje 2000 ms");
				agent.doWait(agent.getDelay());
			}
		}
	}
	
	private double calculate(MatrixFragment mf) {
		double result = 0.0d;
		
		for (int i = 0; i < mf.getSize(); i++) {
			result += mf.getCol()[i] * mf.getRow()[i];
		}
		
		return result;
	}
}