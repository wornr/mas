package lab2.models;

import jade.core.AID;
import lab2.enums.CountingAgentStatus;

public class CountingAgentInfo {
	
	private AID agentId;
	private CountingAgentStatus status;
	
	public CountingAgentInfo(AID agentId) {
		this.agentId = agentId;
		this.status = CountingAgentStatus.Busy;
	}
	
	public AID getAgentId() {
		return agentId;
	}
	
	public CountingAgentStatus getStatus() {
		return status;
	}
	
	public void setStatus(CountingAgentStatus status) {
		this.status = status;
	}
}