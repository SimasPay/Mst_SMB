package com.mfino.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.DCTRestrictionsQuery;
import com.mfino.domain.DCTRestrictions;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.Partner;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Sasi
 *
 */
public class DCTRestrictionsDao extends BaseDAO<DCTRestrictions>{
	
    public List<DCTRestrictions> get(DCTRestrictionsQuery query) {
    	log.info("DCTRestrictionsDao get BEGIN");
    	List<DCTRestrictions> dctRestrictions = new ArrayList<DCTRestrictions>();
    	
    	Criteria dctRestrictionsCriteria = createCriteria();
    	
    	if(null != query.getDctRestrictionsId()){
    		dctRestrictionsCriteria.add(Restrictions.eq(DCTRestrictions.FieldName_RecordID, query.getDctRestrictionsId()));
    	}
    	
    	if(null != query.getDctId()){
    		dctRestrictionsCriteria.add(Restrictions.eq(DCTRestrictions.FieldName_DistributionChainTemplateByDCTID, query.getDctId()));
    	}
    	
    	if(null != query.getTransactionTypeId()){
    		dctRestrictionsCriteria.add(Restrictions.eq(DCTRestrictions.FieldName_TransactionType, query.getTransactionTypeId()));
    	}
    	
    	if(null != query.getRelationshipType()){
    		dctRestrictionsCriteria.add(Restrictions.eq(DCTRestrictions.FieldName_RelationShipType, query.getRelationshipType()));
    	}
    	
    	if(null != query.getLevel()){
    		dctRestrictionsCriteria.add(Restrictions.eq(DCTRestrictions.FieldName_DistributionLevel, query.getLevel()));
    	}
    	
    	if(null != query.getIsAllowed()){
    		dctRestrictionsCriteria.add(Restrictions.eq(DCTRestrictions.FieldName_IsAllowed, query.getIsAllowed()));
    	}
    	
        processBaseQuery(query, dctRestrictionsCriteria);

        //Paging
        processPaging(query, dctRestrictionsCriteria);

        //applying Order
        dctRestrictionsCriteria.addOrder(Order.desc(Partner.FieldName_RecordID));
        applyOrder(query, dctRestrictionsCriteria);

        dctRestrictions = dctRestrictionsCriteria.list();
        
        log.info("DCTRestrictionsDao :: get() END dctRestrictions="+dctRestrictions);
    	return dctRestrictions;
    }
}
