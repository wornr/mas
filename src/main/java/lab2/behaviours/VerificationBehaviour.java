package lab2.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import lab2.agents.DistributorAgent;
import lab2.enums.CountingAgentStatus;
import lab2.enums.MatrixFragmentState;
import lab2.enums.VerificationStep;
import lab2.models.CountingAgentInfo;
import lab2.models.MatrixFragment;

@SuppressWarnings("serial")
public class VerificationBehaviour extends Behaviour {
	
	private List<AID> testedAgents;
	private MatrixFragment mf;
	private MessageTemplate mt;
	private double result;
	private VerificationStep step;
	
	public VerificationBehaviour(AID a, MatrixFragment mf) {
		this.testedAgents = new ArrayList<>();
		this.testedAgents.add(a);
		this.mf = mf;
		this.step = VerificationStep.FirstPassSend;
	}

	@Override
	public void action() {
		switch(step) {
			case FirstPassSend:
				firstPassSend();
				break;
			
			case FirstPassReceive:
				firstPassReceive();
				break;
				
			case SecondPassSend:
				secondPassSend();
				break;
				
			case SecondPassReceive:
				secondPassReceive();
				break;
				
			default:
				break;
		}
	}

	@Override
	public boolean done() {
		return VerificationStep.Verified.equals(step);
	}
	
	private void firstPassSend() {
		boolean sent = false;
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		
		try {
			msg.setContentObject(mf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (CountingAgentInfo countingAgent : ((DistributorAgent)getAgent()).getCountingAgents()) {
			if (CountingAgentStatus.Ready.equals(countingAgent.getStatus()) && !testedAgents.contains(countingAgent.getAgentId())) {
				msg.addReceiver(countingAgent.getAgentId());
				msg.setReplyWith(mf.toString());
				
				System.out.println(getAgent().getLocalName() + ": wysylam agentowi " + countingAgent.getAgentId().getLocalName() + " probke (" + mf.getRowIndex() + ";" + mf.getColIndex() + ") do weryfikacji");
				getAgent().send(msg);
				
				sent = true;
				
				mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.AGREE), MessageTemplate.MatchReplyWith(msg.getReplyWith()));
				break;
			}
		}
		
		if (sent)
			step = VerificationStep.FirstPassReceive;
		else
			block();
	}
	
	private void firstPassReceive() {
		ACLMessage msg = getAgent().receive(mt);
		
		if (msg != null) {
			try {
				MatrixFragment mf = (MatrixFragment) msg.getContentObject();
				if (MatrixFragmentState.Calculated.equals(mf.getState()))
					if (this.mf.getResult() == mf.getResult()) {
						System.out.println(getAgent().getLocalName() + ": probka otrzymana od agenta " + msg.getSender().getLocalName() + " przeszla weryfikacje");
						((DistributorAgent)getAgent()).getResultMatrix().setValue(mf.getRowIndex(), mf.getColIndex(), mf.getResult());
						step = VerificationStep.Verified;
						getAgent().removeBehaviour(this);
					} else {
						System.out.println(getAgent().getLocalName() + ": probka otrzymana od agenta " + msg.getSender().getLocalName() + " nie przeszla weryfikacji");
						testedAgents.add(msg.getSender());
						step = VerificationStep.SecondPassSend;
					}
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		} else {
			block();
		}
	}
	
	private void secondPassSend() {
		boolean sent = false;
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		
		try {
			msg.setContentObject(mf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (CountingAgentInfo countingAgent : ((DistributorAgent)getAgent()).getCountingAgents()) {
			if (CountingAgentStatus.Ready.equals(countingAgent.getStatus()) && !testedAgents.contains(countingAgent.getAgentId())) {
				msg.addReceiver(countingAgent.getAgentId());
				msg.setReplyWith(mf.toString());
				
				System.out.println(getAgent().getLocalName() + ": powtornie wysylam do weryfikacji probke (" + mf.getRowIndex() + ";" + mf.getColIndex() + ") agentowi " + countingAgent.getAgentId().getLocalName());
				getAgent().send(msg);
				
				sent = true;
				
				mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.AGREE), MessageTemplate.MatchReplyWith(msg.getReplyWith()));
				break;
			}
		}
		
		if (sent)
			step = VerificationStep.SecondPassReceive;
		else
			block();
	}
	
	private void secondPassReceive() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
		ACLMessage msg = getAgent().receive(mt);
		
		if (msg != null) {
			try {
				MatrixFragment mf = (MatrixFragment) msg.getContentObject();
				if (MatrixFragmentState.Calculated.equals(mf.getState()))
					if (this.mf.getResult() == mf.getResult()) {
						System.out.println(getAgent().getLocalName() + ": powtorny test wykazal ze probka (" + mf.getRowIndex() + ";" + mf.getColIndex() + ") jest zgodna z pierwszym wynikiem, banuje agenta " + testedAgents.get(1).getLocalName());
						((DistributorAgent)getAgent()).getResultMatrix().setValue(mf.getRowIndex(), mf.getColIndex(), mf.getResult());
						for (CountingAgentInfo countingAgent : ((DistributorAgent)getAgent()).getCountingAgents()) {
							if (testedAgents.get(1).equals(countingAgent)) {
								countingAgent.setStatus(CountingAgentStatus.Banned);
							}
						}
						step = VerificationStep.Verified;
						getAgent().removeBehaviour(this);
					} else {
						System.out.println(getAgent().getLocalName() + ": powtorny test wykazal ze probka (" + mf.getRowIndex() + ";" + mf.getColIndex() + ") nie jest zgodna z pierwszym wynikiem, banuje agenta " + testedAgents.get(0).getLocalName());
						for (CountingAgentInfo countingAgent : ((DistributorAgent)getAgent()).getCountingAgents()) {
							if (testedAgents.get(0).equals(countingAgent)) {
								countingAgent.setStatus(CountingAgentStatus.Banned);
							}
						}
					}
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		} else {
			block();
		}
	}
}