/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SMSTransactionsLogQuery;
import com.mfino.domain.SMSPartner;
import com.mfino.domain.SmsTransactionLog;

/**
 *
 * @author Srinu
 */
public class SMSTransactionsLogDAO extends BaseDAO<SmsTransactionLog>{

     public List<SmsTransactionLog> get(SMSTransactionsLogQuery query) {
        Criteria criteria = createCriteria();
        if (query.getPartnerID() != null) {
            criteria.createCriteria("SMSPartnerByPartnerID").add(Restrictions.eq(SMSPartner.FieldName_RecordID, query.getPartnerID()));
        }
        if (query.getSMSCID() != null) {
            criteria.add(Restrictions.eq(SmsTransactionLog.FieldName_SmscID, query.getSMSCID()));
        }
        if (query.getFieldID() != null) {
            criteria.add(Restrictions.eq(SmsTransactionLog.FieldName_FieldID, query.getFieldID()).ignoreCase());
        }
        if (query.getSource() != null) {
            criteria.add(Restrictions.eq(SmsTransactionLog.FieldName_Source, query.getSource()).ignoreCase());
        }
        if (query.getDestMdn() != null) {
            criteria.add(Restrictions.eq(SmsTransactionLog.FieldName_DestMDN, query.getDestMdn()));
        }
        if (query.getId() !=null){
        	criteria.add(Restrictions.eq(SmsTransactionLog.FieldName_RecordID, query.getId()));
        }
        processBaseQuery(query, criteria);
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")

        List<SmsTransactionLog> results = criteria.list();
        return results;
    }
}
