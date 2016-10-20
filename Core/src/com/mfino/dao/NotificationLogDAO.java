package com.mfino.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.NotificationLogQuery;
import com.mfino.domain.NotificationLog;

public class NotificationLogDAO extends BaseDAO<NotificationLog>{

    public List<NotificationLog> get(NotificationLogQuery query){
        Criteria criteria = createCriteria();

        if(query.getSctlID() != null) {
            criteria.add(Restrictions.eq(NotificationLog.FieldName_SctlId, 
            		new BigDecimal(query.getSctlID())));
        }
        if(query.getCode() != null) {
            criteria.add(Restrictions.eq(NotificationLog.FieldName_NotificationCode, 
            		query.getCode().longValue()));
        }
        if(query.getNotificationMethod() != null) {
            criteria.add(Restrictions.eq(NotificationLog.FieldName_NotificationMethod, 
            		query.getNotificationMethod().longValue()));
        }
        if(query.getSourceAddress() != null) {
            criteria.add(Restrictions.eq(NotificationLog.FieldName_SourceAddress, 
            		query.getSourceAddress()));
        }
        if(query.getNotificationReceiverType() != null) {
            criteria.add(Restrictions.eq(NotificationLog.FieldName_NotificationReceiverType, 
            		query.getNotificationReceiverType().intValue()));
        }
        if(query.isSensitiveData() != null) {
            criteria.add(Restrictions.eq(NotificationLog.FieldName_IsSensitiveData, (short) (query.isSensitiveData() ? '1': '0')));
        }
        
        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        if(query.isIDOrdered()) {
          criteria.addOrder(Order.desc(NotificationLog.FieldName_RecordID));
        }
        
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<NotificationLog> results = criteria.list();

        return results;
    }
}
