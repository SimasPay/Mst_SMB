package com.mfino.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSLedgerDAO;
import com.mfino.domain.LedgerBalance;
import com.mfino.domain.MFSLedger;
import com.mfino.service.MFSLedgerService;

@Service("MFSLedgerServiceImpl")
public class MFSLedgerServiceImpl implements MFSLedgerService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<MFSLedger> getLedgerEntriesByLedgerStatus(String ledgerStatus){
		MFSLedgerDAO mfsLedgerDAO = DAOFactory.getInstance().getMFSLedgerDAO();
		return mfsLedgerDAO.getLedgerEntriesByLedgerStatus(ledgerStatus);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)	
	public void save(MFSLedger mFSLedger) {
		MFSLedgerDAO mfsLedgerDAO = DAOFactory.getInstance().getMFSLedgerDAO();
		mfsLedgerDAO.save(mFSLedger);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)	
	public List<LedgerBalance> getConsolidateBalance(Date startDate, Date endDate) {
		MFSLedgerDAO mfsLedgerDAO = DAOFactory.getInstance().getMFSLedgerDAO();
		return mfsLedgerDAO.getConsolidateBalance(startDate, endDate);
	}
}
