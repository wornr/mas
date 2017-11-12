package lab1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

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
	private int maxTokens = 100;
	private Queue<String> tokens = new LinkedList<String>();
	private Map<String, Integer> givenTokens = new HashMap<String, Integer>();
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
		
		Object[] args = getArguments();
		if (args != null && args.length > 0) maxTokens = Integer.parseInt(args[0].toString());
		produceTokens(100);
		giveTokens();
		listGivenTokens();
	}
	
	private void produceTokens(int interval) {
		addBehaviour(new TickerBehaviour(this, interval) {
			
			@Override
			protected void onTick() {
				if (id < maxTokens) {
					tokens.add(id.toString());
					System.out.println("\t" + getAID().getLocalName() + ":\tcreated token " + id);
					id++;
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
						System.out.println("\t" + getAID().getLocalName() + ":\tgiving token " + token);
						if (givenTokens.get(msg.getContent()) == null) {
							givenTokens.put(msg.getContent(), 1);
						} else {
							int count = givenTokens.get(msg.getContent());
							givenTokens.remove(msg.getContent());
							givenTokens.put(msg.getContent(), count+1);
						}
						
					} else {
						if (id == maxTokens) {
							reply.setPerformative(ACLMessage.FAILURE);
							reply.setContent("not-available");
							System.out.println("\t" + getAID().getLocalName() + ":\tno more tokens will be available");
						} else {
							reply.setPerformative(ACLMessage.INFORM);
							reply.setContent("not-available");
							System.out.println("\t" + getAID().getLocalName() + ":\ttokens currently not available");
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
				if (tokens.isEmpty() && id == maxTokens) {
					System.out.println("\n\n\nAll tokens have been distributed:");
					givenTokens.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByKey()).forEach(System.out::println);
					removeBehaviour(this);
				}
			}
		});
	}
}