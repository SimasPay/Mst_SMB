package com.mfino.service;

import com.mfino.domain.SettlementSchedulerLogs;

public interface SettlementSchedulerLogsService {
	public SettlementSchedulerLogs getByPartnerServiceId(Long partnerServiceId);
	public void save(SettlementSchedulerLogs settlementSchedulerLogs);
	public SettlementSchedulerLogs getByJobId(String jobId);
}
