package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ServiceChargeTxnLog;

public interface ServiceChargeTransactionLogService {
	public List<ServiceChargeTxnLog> getByStatus(Integer[] status);
	 public void save(ServiceChargeTxnLog sctl);
	 public List<ServiceChargeTxnLog> get(ServiceChargeTransactionsLogQuery query);
	 public ServiceChargeTxnLog getById(Long serviceChargeTransactionLogId);
}
