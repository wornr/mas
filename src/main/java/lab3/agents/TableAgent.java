package lab3.agents;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import lab3.behaviours.TableBehaviour;
import lab3.helpers.DFServiceHelper;

@SuppressWarnings("serial")
public class TableAgent extends Agent {

	private static List<AID> philosophers;
	private static List<AID> forks;
	private static int kebabs;
	
	@Override
	protected void setup() {
		kebabs = 25;
		
		DFServiceHelper.getInstance().register(this, "table", "table");
		addBehaviour(new TableBehaviour(this));
		
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
			// TODO przerwac dzialanie aplikacji (liczba filozofow musi byc rowna liczbie widelcow)
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
	
	public static int getKebabs() {
		return kebabs;
	}
	
	public void removeKebab() {
		kebabs--;
		System.out.println("Zjedzono kebaba, " + (kebabs > 4 || kebabs == 0 ? "pozostalo: " : (kebabs > 1 ? "pozostaly: " : "pozostał: ")) + kebabs);
	}
}