package com.mfino.mce.backend.impl;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.IntegrationSummary;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.IntegrationSummaryService;

public class IntegrationSummaryServiceImpl extends BaseServiceImpl implements IntegrationSummaryService {
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRES_NEW,rollbackFor=Throwable.class)
	public void logIntegrationSummary(Long sctldId, Long pctId,String IntegrationType, String reconId1, String reconId2,String reconId3, String reconId4, Timestamp timestamp) {
		coreDataWrapper.getIntegrationSummaryDao().logIntegrationSummary(sctldId, pctId, IntegrationType, reconId1, reconId2, reconId3, reconId4, timestamp);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public IntegrationSummary getIntegrationSummary(Long sctlId,Long pctId){
		return coreDataWrapper.getIntegrationSummaryDao().getByScltId(sctlId,pctId);
	}
}
