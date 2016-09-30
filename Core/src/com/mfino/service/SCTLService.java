package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.AutoReversals;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.ServiceChargeTxnLog;

public interface SCTLService {

	List<ServiceChargeTxnLog> getBySCTLIntegrationTxnID(
			String paymentLogID, String custReference);

	CommodityTransfer getCTfromSCTL(ServiceChargeTxnLog sctl);

	void updateSCTLStatus(Integer sctlstatusReverseInitiated,
			ServiceChargeTxnLog sctl);
	
	public ServiceChargeTxnLog getBySCTLID(long id);
	
	public ServiceChargeTxnLog getByTransactionLogId(Long TxnLogID);
	
	public List<ServiceChargeTxnLog> getByQuery(ServiceChargeTransactionsLogQuery sctlQuery);
	
	public AutoReversals getAutoReversalsFromSCTL(ServiceChargeTxnLog sctl);
	
	public void saveSCTL(ServiceChargeTxnLog sctl);
	
	public List<ServiceChargeTxnLog> getSubscriberPendingTransactions(ServiceChargeTransactionsLogQuery query);

}
