package lab3.agents;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;

@SuppressWarnings("serial")
public class TableAgent extends Agent {

	private static List<AID> philosophers;
	private static List<AID> forks;
	private static int kebabs;
	
	@Override
	protected void setup() {
		kebabs = 250;
		
		doWait(250); // wait for Agents to register
		findAllAgents();
	}
	
	private void findAllAgents() {
		AMSAgentDescription[] agents = null;
		
		try {
	        SearchConstraints c = new SearchConstraints();
	        c.setMaxResults(-1L);
	        
	        agents = AMSService.search(this, new AMSAgentDescription(), c);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		
		if (agents.length > 0) {
			findAllPhilosophers(agents);
			findAllForks(agents);
		}
		
		if (philosophers.size() != forks.size()) {
			// TODO zabić agentow i wyswietlic komunikat
		}
	}
	
	private void findAllPhilosophers(AMSAgentDescription[] agents) {
		philosophers = new ArrayList<>();
		
		for (int i = 0; i < agents.length; i++) {
			if (agents[i].getName().getLocalName().startsWith("philosopher")) {
				philosophers.add(agents[i].getName());
			}
		}
	}
	
	private void findAllForks(AMSAgentDescription[] agents) {
		forks = new ArrayList<>();
		
		for (int i = 0; i < agents.length; i++) {
			if (agents[i].getName().getLocalName().startsWith("fork")) {
				forks.add(agents[i].getName());
			}
		}
	}
	
	public static List<AID> getPhilosophers() {
		return philosophers;
	}
	
	public static List<AID> getForks() {
		return forks;
	}
}