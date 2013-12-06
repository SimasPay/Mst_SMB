package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.NotificationLogDetailsQuery;
import com.mfino.dao.query.NotificationLogQuery;
import com.mfino.domain.NotificationLog;
import com.mfino.domain.NotificationLogDetails;
import com.mfino.fix.CmFinoFIX;

/**
 * 
 * @author Amar
 *
 */
public class NotificationLogDetailsDAO extends BaseDAO<NotificationLogDetails>{

    public List<NotificationLogDetails> get(NotificationLogDetailsQuery query){
        Criteria criteria = createCriteria();

        if(query.getNotificationLog() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRNotificationLogDetails.FieldName_NotificationLog, query.getNotificationLog()));
        }
        if(query.getSendNotificationtatus() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRNotificationLogDetails.FieldName_SendNotificationStatus, query.getSendNotificationtatus()));
        }
        
        
        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        if(query.isIDOrdered()) {
          criteria.addOrder(Order.desc(CmFinoFIX.CRNotificationLogDetails.FieldName_RecordID));
        }
        
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<NotificationLogDetails> results = criteria.list();

        return results;
    }
}
