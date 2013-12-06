package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SettlementTransactionSCTLMapDAO;
import com.mfino.domain.SettlementTransactionSCTLMap;
import com.mfino.service.SettlementTransactionSCTLMapService;

@Service("SettlementTransactionSCTLMapServiceImpl")
public class SettlementTransactionSCTLMapServiceImpl implements
		SettlementTransactionSCTLMapService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(SettlementTransactionSCTLMap settlementTransactionSCTLMap){
		SettlementTransactionSCTLMapDAO settlementTransactionSCTLMapDao = DAOFactory.getInstance().getSettlementTransactionSCTLMapDao();
		settlementTransactionSCTLMapDao.save(settlementTransactionSCTLMap);
	}
}
