package com.mfino.service;

import com.mfino.domain.AgentCommissionFee;

public interface AgentCommissionFeeService {

	public void save(AgentCommissionFee agentCommissionFee);
	
	public AgentCommissionFee getAgentCommissionFee(Long partnerId, String month, int year);
	
}
