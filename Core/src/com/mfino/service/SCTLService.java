package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.AutoReversals;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;

public interface SCTLService {

	List<ServiceChargeTransactionLog> getBySCTLIntegrationTxnID(
			String paymentLogID, String custReference);

	CommodityTransfer getCTfromSCTL(ServiceChargeTransactionLog sctl);

	void updateSCTLStatus(Integer sctlstatusReverseInitiated,
			ServiceChargeTransactionLog sctl);
	
	public ServiceChargeTransactionLog getBySCTLID(long id);
	
	public ServiceChargeTransactionLog getByTransactionLogId(Long TxnLogID);
	
	public List<ServiceChargeTransactionLog> getByQuery(ServiceChargeTransactionsLogQuery sctlQuery);
	
	public AutoReversals getAutoReversalsFromSCTL(ServiceChargeTransactionLog sctl);
	
	public void saveSCTL(ServiceChargeTransactionLog sctl);

}
