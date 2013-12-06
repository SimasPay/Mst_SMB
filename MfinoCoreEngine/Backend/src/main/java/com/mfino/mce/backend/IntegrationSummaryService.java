package com.mfino.mce.backend;

import com.mfino.domain.IntegrationSummary;
import com.mfino.hibernate.Timestamp;

public interface IntegrationSummaryService {

	public void logIntegrationSummary(Long sctldId,Long pctId,String IntegrationType,String reconId1,String reconId2,String reconId3,String reconId4, Timestamp timestamp);
	
	public IntegrationSummary getIntegrationSummary(Long sctlId,Long pctId);
}