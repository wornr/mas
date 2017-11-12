package lab1;

import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class ConsumentAgent extends Agent {
	final private int interval = new Random().nextInt(75) + 25;
	private boolean sleep = false;
	private AID[] producerAgents;
	public TickerBehaviour tickerBehaviour;

	protected void setup() {
		tickerBehaviour = takeToken(this, interval);
	}
	
	private TickerBehaviour takeToken(final ConsumentAgent agent, int interval) {
		TickerBehaviour tickerBehaviour = new TickerBehaviour(this, interval) {
			@Override
			protected void onTick() {
				if (!sleep) {
					ServiceDescription sd = new ServiceDescription();
					sd.setType("token-producing");
					
					DFAgentDescription dfd = new DFAgentDescription();
					dfd.addServices(sd);
					
					try {
						DFAgentDescription[] result = DFService.search(agent, dfd);
						producerAgents = new AID[result.length];
						for (int i = 0; i < result.length; ++i) {
							producerAgents[i] = result[0].getName();
						}
					} catch (FIPAException ex) {
						ex.printStackTrace();
					}
					
					if (producerAgents.length > 0)
						addBehaviour(new RequestPerformer(agent));
				} else {
					//System.out.println(getAID().getLocalName() + ":WAITING...");
					sleep = false;
				}
			}
		};
		
		addBehaviour(tickerBehaviour);

		return tickerBehaviour;
	}
	
	private class RequestPerformer extends Behaviour {
		private int step = 0;
		private MessageTemplate mt;
		
		public RequestPerformer(ConsumentAgent agent) {
			super(agent);
		}

		@Override
		public void action() {
			switch(step) {
				case 0:
					ACLMessage cfp = new ACLMessage(ACLMessage.REQUEST);
					cfp.addReceiver(producerAgents[0]);
					cfp.setContent(getLocalName());
					cfp.setReplyWith(getName() + System.currentTimeMillis());
					send(cfp);
					System.out.println(getAID().getLocalName() + ": REQUEST");
					mt = MessageTemplate.MatchInReplyTo(cfp.getReplyWith());
					step = 1;
				break;
					
				case 1:
					ACLMessage reply = receive(mt);
					if (reply != null) {
						if (reply.getPerformative() == ACLMessage.CONFIRM) {
						} else if (reply.getPerformative() == ACLMessage.FAILURE) {
							//System.out.println(getAID().getLocalName() + "I won't ask for more tokens");
							removeBehaviour(tickerBehaviour);
						}
						step = 2;
						sleep = true;
					} else {
						block();
					}
				break;
			}
		}

		@Override
		public boolean done() {
			return step == 2;
		}
	}
}