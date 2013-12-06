package com.mfino.service;

import java.util.Date;
import java.util.List;

import com.mfino.domain.LedgerBalance;
import com.mfino.domain.MFSLedger;

public interface MFSLedgerService {
	public List<MFSLedger> getLedgerEntriesByLedgerStatus(String ledgerStatus);
	public void save(MFSLedger mFSLedger);
	public List<LedgerBalance> getConsolidateBalance(Date startDate, Date endDate);
}
