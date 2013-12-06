/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ActivitiesLogQuery;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.Company;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class ActivitiesLogDAO extends BaseDAO<ActivitiesLog> {

    public List<ActivitiesLog> get(ActivitiesLogQuery query){
        Criteria criteria = createCriteria();

        if(null != query.getParentTransactionId()) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRActivitiesLog.FieldName_ParentTransactionID, query.getParentTransactionId()));
        }

        if(query.getTransferID() != null){
            criteria.add(Restrictions.eq(CmFinoFIX.CRActivitiesLog.FieldName_TransferID,
                    query.getTransferID()));
        }

        if(query.getMsgType() != null){
            criteria.add(Restrictions.eq(CmFinoFIX.CRActivitiesLog.FieldName_MsgType, query.getMsgType()));
        }
        
        if(query.getCommodity() != null){
          criteria.add(Restrictions.eq(CmFinoFIX.CRActivitiesLog.FieldName_Commodity, query.getCommodity()));
        }
        
        if (query.getCompany() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRActivitiesLog.FieldName_Company, query.getCompany()));
        }
        
        if(query.getBankRoutingCode()!=null){
        	criteria.add(Restrictions.eq(CmFinoFIX.CRActivitiesLog.FieldName_ISO8583_AcquiringInstIdCode, query.getBankRoutingCode()));
        }
        
        if(query.getSourceSubscriberID()!=null){
        	criteria.add(Restrictions.eq(CmFinoFIX.CRActivitiesLog.FieldName_SourceSubscriberID, query.getSourceSubscriberID()));
        }
        
        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        if(query.isIDOrdered()) {
          criteria.addOrder(Order.desc(CmFinoFIX.CRActivitiesLog.FieldName_RecordID));
        }
        
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<ActivitiesLog> results = criteria.list();

        return results;
    }

    public int getActivityCountBetween(Date start, Date end, Company company, Integer bankCode) {
      Criteria criteria = createCriteria();
      criteria.add(Restrictions.ge(CmFinoFIX.CRTransactionsLog.FieldName_LastUpdateTime, start));
      criteria.add(Restrictions.lt(CmFinoFIX.CRTransactionsLog.FieldName_LastUpdateTime, end));
      if(null != company)
    	  criteria.add(Restrictions.eq(CmFinoFIX.CRActivitiesLog.FieldName_Company, company));
      if(null != bankCode)
    	  criteria.add(Restrictions.eq(CmFinoFIX.CRActivitiesLog.FieldName_ISO8583_AcquiringInstIdCode, bankCode));
      criteria.setProjection(Projections.rowCount());
      Integer count = (Integer) criteria.uniqueResult();
      return count;
    }
    
}
