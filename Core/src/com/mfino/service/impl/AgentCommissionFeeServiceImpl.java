package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.AgentCommissionFeeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.AgentCommissionFee;
import com.mfino.service.AgentCommissionFeeService;

@Service("AgentCommissionFeeServiceImpl")
public class AgentCommissionFeeServiceImpl implements AgentCommissionFeeService {
	private AgentCommissionFeeDAO agentCommissionFeeDAO = DAOFactory.getInstance().getAgentCommissionFeeDAO();

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(AgentCommissionFee agentCommissionFee) {
		agentCommissionFeeDAO.save(agentCommissionFee);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public AgentCommissionFee getAgentCommissionFee(Long partnerId, String month, int year) {
		return agentCommissionFeeDAO.getAgentCommissionFee(partnerId, month, year);
	}

}
