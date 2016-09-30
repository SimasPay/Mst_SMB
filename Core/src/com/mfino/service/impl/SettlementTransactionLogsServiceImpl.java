package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SettlementTransactionLogsDao;
import com.mfino.domain.SettlementTxnLog;
import com.mfino.service.SettlementTransactionLogsService;

@Service("SettlementTransactionLogsServiceImpl")
public class SettlementTransactionLogsServiceImpl implements SettlementTransactionLogsService{
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(SettlementTxnLog settlementTransactionLogs){
		SettlementTransactionLogsDao setTraDao = DAOFactory.getInstance().getSettlementTransactionLogDao();
		setTraDao.save(settlementTransactionLogs);
	}

}
