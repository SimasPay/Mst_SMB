/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRBillPayments;

/**
 *
 * @author Maruthi
 */
public class BillPaymentsDAO extends BaseDAO<BillPayments> {
	
	public List<BillPayments> getBySctlList(List<Long> sctlLst) {        
		Criteria criteria = createCriteria();
		if (sctlLst != null) {
			List<Long> tempLst = new ArrayList<Long>(sctlLst);
			addCriteriaIn(CmFinoFIX.CRBillPayments.FieldName_SctlId, tempLst, criteria);
		}
		@SuppressWarnings("unchecked")
		List<BillPayments> results = criteria.list();
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
	
	 public List<BillPayments> get(BillPaymentsQuery query) {
		 Criteria criteria = createCriteria();
		 
		 if(query.getSctlID()!=null){
			 criteria.add(Restrictions.eq(CmFinoFIX.CRBillPayments.FieldName_SctlId,query.getSctlID()));
		 }
		 if(query.getBillerCode()!=null){
			 criteria.add(Restrictions.eq(CmFinoFIX.CRBillPayments.FieldName_BillerCode, query.getBillerCode()).ignoreCase());
		 }
		 if (StringUtils.isNotBlank(query.getIntegrationTxnRefId())) {
			 criteria.add(Restrictions.eq(CmFinoFIX.CRBillPayments.FieldName_INTxnId, query.getIntegrationTxnRefId()).ignoreCase());
		 }
		 if (StringUtils.isNotBlank(query.getIntegrationCode())) {
			 criteria.add(Restrictions.eq(CmFinoFIX.CRBillPayments.FieldName_IntegrationCode, query.getIntegrationCode()));
		 }
		         
		 
		 if((null != query.getBillPayStatuses()) && (query.getBillPayStatuses().size() > 0)){
			 criteria.add(Restrictions.in(CRBillPayments.FieldName_BillPayStatus, query.getBillPayStatuses()));
		 }
		 
		 processBaseQuery(query, criteria);
		 processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<BillPayments> results = criteria.list();

        return results;
    }
}
