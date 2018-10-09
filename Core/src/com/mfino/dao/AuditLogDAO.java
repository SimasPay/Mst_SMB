package com.mfino.dao;
import com.mfino.domain.AuditLog;
import com.mfino.dao.query.AuditLogQuery;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Hemanth
 *
 */
public class AuditLogDAO extends BaseDAO<AuditLog> {
	
	private static Logger log = LoggerFactory.getLogger(AuditLogDAO.class);
	
	public List<AuditLog> get(AuditLogQuery query){
		log.debug("@kris: AuditLogDAO.get()");
		
        Criteria criteria = createCriteria();

//        if(null != query.getParentTransactionId()) {
//            criteria.add(Restrictions.eq(AuditLog.FieldName_ParentTransactionID, query.getParentTransactionId()));
//        }
        
        if(query.getCreatedBy() != null){
            criteria.add(Restrictions.eq(AuditLog.FieldName_CreatedBy,query.getCreatedBy()));
        }

        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        //if(query.isIDOrdered()) {
          criteria.addOrder(Order.desc(AuditLog.FieldName_CreateTime));
        //}
        
        //applying Order
        applyOrder(query, criteria);
        
        log.info("@kris print AuditLog query:"+printQueryFromCriteria(criteria));
        
        @SuppressWarnings("unchecked")
        List<AuditLog> results = criteria.list();

        return results;
    }
}
