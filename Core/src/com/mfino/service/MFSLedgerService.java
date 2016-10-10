package com.mfino.service;

import java.util.Date;
import java.util.List;

import com.mfino.domain.LedgerBalance;
import com.mfino.domain.MfsLedger;

public interface MFSLedgerService {
	public List<MfsLedger> getLedgerEntriesByLedgerStatus(String ledgerStatus);
	public void save(MfsLedger mFSLedger);
	public List<LedgerBalance> getConsolidateBalance(Date startDate, Date endDate);
}
