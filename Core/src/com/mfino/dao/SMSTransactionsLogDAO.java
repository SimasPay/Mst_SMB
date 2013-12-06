/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import com.mfino.dao.query.SMSTransactionsLogQuery;
import com.mfino.domain.SMSTransactionsLog;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Srinu
 */
public class SMSTransactionsLogDAO extends BaseDAO<SMSTransactionsLog>{

     public List<SMSTransactionsLog> get(SMSTransactionsLogQuery query) {
        Criteria criteria = createCriteria();
        if (query.getPartnerID() != null) {
            criteria.createCriteria("SMSPartnerByPartnerID").add(Restrictions.eq(CmFinoFIX.CRSMSPartner.FieldName_RecordID, query.getPartnerID()));
        }
        if (query.getSMSCID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSMSTransactionsLog.FieldName_SmscID, query.getSMSCID()));
        }
        if (query.getFieldID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSMSTransactionsLog.FieldName_FieldID, query.getFieldID()).ignoreCase());
        }
        if (query.getSource() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSMSTransactionsLog.FieldName_Source, query.getSource()).ignoreCase());
        }
        if (query.getDestMdn() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSMSTransactionsLog.FieldName_DestMDN, query.getDestMdn()));
        }
        if (query.getId() !=null){
        	criteria.add(Restrictions.eq(CmFinoFIX.CRSMSTransactionsLog.FieldName_RecordID, query.getId()));
        }
        processBaseQuery(query, criteria);
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")

        List<SMSTransactionsLog> results = criteria.list();
        return results;
    }
}
