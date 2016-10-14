package com.mfino.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
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
	
	public List<IntegrationSummary> getBySctlList(List<Long> sctlLst) {        
		Criteria criteria = createCriteria();
		if (sctlLst != null) {
			List<Long> tempLst = new ArrayList<Long>(sctlLst);
			addCriteriaIn(IntegrationSummary.FieldName_SctlId, tempLst, criteria);
		}
		@SuppressWarnings("unchecked")
		List<IntegrationSummary> results = criteria.list();
		return results;
	}
	
	 private void addCriteriaIn (String propertyName, List<?> list,Criteria criteria)
	  {
	    Disjunction or = Restrictions.disjunction();
	    if(list.size()>1000)
	    {        
	      while(list.size()>1000)
	      {
	        List<?> subList = list.subList(0, 1000);
	        or.add(Restrictions.in(propertyName, subList));
	        list.subList(0, 1000).clear();
	      }
	    }
	    or.add(Restrictions.in(propertyName, list));
	    criteria.add(or);
	  }
	
	public List<IntegrationSummary> get(IntegrationSummaryQuery query) {
		Criteria criteria = createCriteria();
		if (query.getSctlID() != null) {
			criteria.add(Restrictions.eq(
					IntegrationSummary.FieldName_SctlId,
					query.getSctlID()));
		}
		if (StringUtils.isNotBlank(query.getIntegrationType())) {
			criteria.add(Restrictions
					.eq(IntegrationSummary.FieldName_IntegrationType,
							query.getIntegrationType()));
		}
		if (StringUtils.isNotBlank(query.getReconcilationID1())) {
			criteria.add(Restrictions
					.eq(IntegrationSummary.FieldName_ReconcilationID1,
							query.getReconcilationID1()));
		}
		if (StringUtils.isNotBlank(query.getReconcilationID2())) {
			criteria.add(Restrictions
					.eq(IntegrationSummary.FieldName_ReconcilationID2,
							query.getReconcilationID2()));
		}
		if (StringUtils.isNotBlank(query.getReconcilationID3())) {
			criteria.add(Restrictions
					.eq(IntegrationSummary.FieldName_ReconcilationID3,
							query.getReconcilationID3()));
		}
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<IntegrationSummary> results = criteria.list();
		return results;
	}
	
	public IntegrationSummary getByScltId(Long sctlId,Long pctId){
		log.info("getByScltId: getting the integration summary details for sctlid = "+ sctlId + " pctid = "+ pctId);
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(IntegrationSummary.FieldName_SctlId,sctlId));
		List<IntegrationSummary> results = criteria.list();
		IntegrationSummary iSummary = null;
		
		if((null != results) && (results.size() > 0)){
			log.info("got the result if size "+ results.size());
			iSummary = results.get(0);
		}
		
		return iSummary;
	}
	
	public void logIntegrationSummary(Long sctldId, Long pctId,String IntegrationType, String reconId1, String reconId2,String reconId3, String reconId4, Timestamp timestamp){
		log.info("logIntegrationSummary: getting the integration summary details for sctlid = "+ sctldId + " pctid = "+ pctId);
		IntegrationSummary iSummary = getByScltId(sctldId, pctId);
		if(iSummary == null){
			iSummary=new IntegrationSummary();
			iSummary.setSctlid(sctldId);
			iSummary.setPctid(new BigDecimal(pctId));
			iSummary.setCreatetime(timestamp != null ? timestamp : new Timestamp());
			iSummary.setCreatedby("System");
		}
		iSummary.setIntegrationtype(IntegrationType);
		iSummary.setReconcilationid1(reconId1);
		iSummary.setReconcilationid2(reconId2);
		iSummary.setReconcilationid3(reconId3);
		iSummary.setReconcilationid4(reconId4);
		if(timestamp!=null){
		iSummary.setLastupdatetime(timestamp);
		}else{
		iSummary.setLastupdatetime(new Timestamp());
		}
		iSummary.setUpdatedby("System");
		getSession().saveOrUpdate(iSummary);
	}
}
