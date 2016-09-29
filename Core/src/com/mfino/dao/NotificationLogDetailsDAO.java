package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.NotificationLogDetailsQuery;
import com.mfino.domain.NlogDetails;

/**
 * 
 * @author Amar
 *
 */
public class NotificationLogDetailsDAO extends BaseDAO<NlogDetails>{

    public List<NlogDetails> get(NotificationLogDetailsQuery query){
        Criteria criteria = createCriteria();

        if(query.getNotificationLog() != null) {
            criteria.add(Restrictions.eq(NlogDetails.FieldName_NotificationLog, query.getNotificationLog()));
        }
        if(query.getSendNotificationtatus() != null) {
            criteria.add(Restrictions.eq(NlogDetails.FieldName_SendNotificationStatus, query.getSendNotificationtatus()));
        }
        
        
        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        if(query.isIDOrdered()) {
          criteria.addOrder(Order.desc(NlogDetails.FieldName_RecordID));
        }
        
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<NlogDetails> results = criteria.list();

        return results;
    }
}
