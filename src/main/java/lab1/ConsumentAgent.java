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
	final private int interval = new Random().nextInt(1000) + 1000;
	private boolean sleep = false;
	private AID[] producerAgents;
	private int tokensTaken = 0;
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
					
					addBehaviour(new RequestPerformer(agent));
				} else {
					System.out.println("Czekam na zakonczenie cyklu obliczeniowego.");
					sleep = false;
				}
			}
		};
		
		addBehaviour(tickerBehaviour);

		return tickerBehaviour;
	}
	
	private class RequestPerformer extends Behaviour {
		private ConsumentAgent agent;
		private int step = 0;
		private MessageTemplate mt;
		
		public RequestPerformer(ConsumentAgent agent) {
			super();
			this.agent = agent;
		}
		
		@Override
		public void action() {
			switch(step) {
				case 0:
					ACLMessage cfp = new ACLMessage(ACLMessage.REQUEST);
					cfp.addReceiver(producerAgents[0]);
					cfp.setContent(agent.getLocalName());
					cfp.setReplyWith(agent.getName() + System.currentTimeMillis());
					agent.send(cfp);
					System.out.println("Prosze o przekazanie tokena");
					mt = MessageTemplate.MatchInReplyTo(cfp.getReplyWith());
					step = 1;
					break;
					
				case 1:
					ACLMessage reply = agent.receive(mt);
					if (reply != null) {
						if (reply.getPerformative() == ACLMessage.CONFIRM) {
							agent.tokensTaken++;
							System.out.println("Posiadam juz " + agent.tokensTaken + " tokenow");
						} else if (reply.getPerformative() == ACLMessage.FAILURE) {
							System.out.println("Nie bede juz odpytywal o kolejne tokeny");
							agent.removeBehaviour(agent.tickerBehaviour);
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