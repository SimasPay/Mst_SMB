package com.mfino.mce.backend;

import java.math.BigDecimal;
import java.util.List;

import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Ledger;
import com.mfino.domain.MfsLedger;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;

public interface LedgerService {
	@Deprecated
	public Ledger createLedgerEntry(Pocket srcPocket,Pocket destPocket,CommodityTransfer ct,PendingCommodityTransfer pct,BigDecimal amount);

	public List<MfsLedger> createLedgerEntries(boolean isSettlement,Long sctlId, Long ctID, Pocket srcPocket, Pocket destPocket, Pocket chargesPocket, BigDecimal amount, BigDecimal charges, boolean isNettingOn);
	
	public MfsLedger generateReverseLedgerEntry(MfsLedger mfsLedger);
	
	public boolean isImmediateUpdateRequiredForPocket(Pocket pocket);
}

