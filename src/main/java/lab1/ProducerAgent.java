package lab1;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class ProducerAgent extends Agent {
	private Integer id = 0;
	private Queue<String> tokens = new LinkedList<String>();
	private Hashtable<String, Integer> givenTokens = new Hashtable<String, Integer>();
	public CyclicBehaviour mainBehaviour;
	
	protected void setup() {
		ServiceDescription sd = new ServiceDescription();
		sd.setType("token-producing");
		sd.setName("JADE-token-giving");
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());		
		dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd);
		} catch (FIPAException ex) {
			ex.printStackTrace();
		}
		
		produceTokens(2500);
		giveTokens();
		listGivenTokens();
	}
	
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException ex) {
			ex.printStackTrace();
		}
	}
	
	private void produceTokens(int interval) {
		addBehaviour(new TickerBehaviour(this, interval) {
			
			@Override
			protected void onTick() {
				if (id < 100) {
					tokens.add(id.toString());
					System.out.println("Successfully produced token " + id);
					id++;
					System.out.println("Next token will be " + id);
					System.out.println();
				} else {
					removeBehaviour(this);
				}
			}
		});
	}
	
	private void giveTokens() {
		addBehaviour(new CyclicBehaviour(this) {
			
			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage msg = receive(mt);
				if (msg != null) {
					ACLMessage reply = msg.createReply();

					Iterator<String> itr = tokens.iterator();
					if (itr.hasNext()) {
						String token = itr.next().toString();
						reply.setPerformative(ACLMessage.CONFIRM);
						reply.setContent(token);
						itr.remove();
						System.out.println("Przekazuje token " + token);
						if (givenTokens.get(msg.getContent()) == null) {
							givenTokens.put(msg.getContent(), 1);
						} else {
							int count = givenTokens.get(msg.getContent());
							givenTokens.remove(msg.getContent());
							givenTokens.put(msg.getContent(), count+1);
						}
						
					} else {
						if (id == 100) {
							reply.setPerformative(ACLMessage.FAILURE);
							reply.setContent("not-available");
							System.out.println("Nie bedzie juz wiecej tokenow");
						} else {
							reply.setPerformative(ACLMessage.INFORM);
							reply.setContent("not-available");
							System.out.println("Brak dostepnych tokenow");
						}
					}
					send(reply);
				} else {
					block();
				}
			}
		});
	}
	
	private void listGivenTokens() {
		addBehaviour(new TickerBehaviour(this, 100) {

			@Override
			protected void onTick() {
				if (tokens.isEmpty() && id == 100) {
					System.out.println("Rozdano wszystkie tokeny");
					Enumeration<String> enumeration = givenTokens.keys();
					while (enumeration.hasMoreElements()) {
						String key = enumeration.nextElement();
						System.out.println(key + ": " + givenTokens.get(key));
					}
					removeBehaviour(this);
				}
			}
		});
	}
}