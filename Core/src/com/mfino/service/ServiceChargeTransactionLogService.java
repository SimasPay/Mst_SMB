package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ServiceChargeTransactionLog;

public interface ServiceChargeTransactionLogService {
	public List<ServiceChargeTransactionLog> getByStatus(Integer[] status);
	 public void save(ServiceChargeTransactionLog sctl);
	 public List<ServiceChargeTransactionLog> get(ServiceChargeTransactionsLogQuery query);
	 public ServiceChargeTransactionLog getById(Long serviceChargeTransactionLogId);
}
