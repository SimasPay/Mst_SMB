package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.IntegrationSummaryQuery;
import com.mfino.domain.IntegrationSummary;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;

/**
 * 
 * @author Srikanth
 */
public class IntegrationSummaryDao extends BaseDAO<IntegrationSummary> {
	public List<IntegrationSummary> get(IntegrationSummaryQuery query) {
		Criteria criteria = createCriteria();
		if (query.getSctlID() != null) {
			criteria.add(Restrictions.eq(
					CmFinoFIX.CRIntegrationSummary.FieldName_SctlId,
					query.getSctlID()));
		}
		if (StringUtils.isNotBlank(query.getIntegrationType())) {
			criteria.add(Restrictions
					.eq(CmFinoFIX.CRIntegrationSummary.FieldName_IntegrationType,
							query.getIntegrationType()));
		}
		if (StringUtils.isNotBlank(query.getReconcilationID1())) {
			criteria.add(Restrictions
					.eq(CmFinoFIX.CRIntegrationSummary.FieldName_ReconcilationID1,
							query.getReconcilationID1()));
		}
		if (StringUtils.isNotBlank(query.getReconcilationID2())) {
			criteria.add(Restrictions
					.eq(CmFinoFIX.CRIntegrationSummary.FieldName_ReconcilationID2,
							query.getReconcilationID2()));
		}
		if (StringUtils.isNotBlank(query.getReconcilationID3())) {
			criteria.add(Restrictions
					.eq(CmFinoFIX.CRIntegrationSummary.FieldName_ReconcilationID3,
							query.getReconcilationID3()));
		}
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<IntegrationSummary> results = criteria.list();
		return results;
	}
	
	public IntegrationSummary getByScltId(Long sctlId,Long pctId){
		
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRIntegrationSummary.FieldName_SctlId,sctlId));
		List<IntegrationSummary> results = criteria.list();
		IntegrationSummary iSummary = null;
		
		if((null != results) && (results.size() > 0)){
			iSummary = results.get(0);
		}
		
		return iSummary;
	}
	
	public void logIntegrationSummary(Long sctldId, Long pctId,String IntegrationType, String reconId1, String reconId2,String reconId3, String reconId4, Timestamp timestamp){
		IntegrationSummary iSummary = getByScltId(sctldId, pctId);
		if(iSummary == null){
			iSummary=new IntegrationSummary();
			iSummary.setSctlId(sctldId);
			iSummary.setPctId(pctId);
			iSummary.setCreateTime(timestamp);
			iSummary.setCreatedBy("System");
		}
		iSummary.setIntegrationType(IntegrationType);
		iSummary.setReconcilationID1(reconId1);
		iSummary.setReconcilationID2(reconId2);
		iSummary.setReconcilationID3(reconId3);
		iSummary.setReconcilationID4(reconId4);
		if(timestamp!=null){
		iSummary.setLastUpdateTime(timestamp);
		}else{
		iSummary.setLastUpdateTime(new Timestamp());
		}
		iSummary.setUpdatedBy("System");
		getSession().saveOrUpdate(iSummary);
	}
}
