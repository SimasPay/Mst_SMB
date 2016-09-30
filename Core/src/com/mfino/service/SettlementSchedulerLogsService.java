package com.mfino.service;

import com.mfino.domain.SettlementScheduleLog;


public interface SettlementSchedulerLogsService {
	public SettlementScheduleLog getByPartnerServiceId(Long partnerServiceId);
	public void save(SettlementScheduleLog settlementSchedulerLogs);
	public SettlementScheduleLog getByJobId(String jobId);
}
