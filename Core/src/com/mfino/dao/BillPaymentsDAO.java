/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
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
