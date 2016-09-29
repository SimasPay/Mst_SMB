package com.mfino.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.PartnerRestrictionsQuery;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerRestrictions;

/**
 * @author Sasi
 *
 */
public class PartnerRestrictionsDao extends BaseDAO<PartnerRestrictions>{
	
    public List<PartnerRestrictions> get(PartnerRestrictionsQuery query) {
    	log.info("PartnerRestrictionsDao get BEGIN");
    	List<PartnerRestrictions> partnerRestrictions = new ArrayList<PartnerRestrictions>();
    	
    	Criteria partnerRestrictionsCriteria = createCriteria();
    	
    	if(null != query.getPartnerRestrictionsId()){
    		partnerRestrictionsCriteria.add(Restrictions.eq(PartnerRestrictions.FieldName_RecordID, query.getPartnerRestrictionsId()));
    	}
    	
    	if(null != query.getDctId()){
    		partnerRestrictionsCriteria.add(Restrictions.eq(PartnerRestrictions.FieldName_DistributionChainTemplateByDCTID, query.getDctId()));
    	}
    	
    	if(null != query.getPartnerId()){
    		partnerRestrictionsCriteria.add(Restrictions.eq(PartnerRestrictions.FieldName_Partner, query.getPartnerId()));
    	}
    	
    	if(null != query.getTransactionTypeId()){
    		partnerRestrictionsCriteria.add(Restrictions.eq(PartnerRestrictions.FieldName_TransactionType, query.getTransactionTypeId()));
    	}

    	if(null != query.getRelationshipType()){
    		partnerRestrictionsCriteria.add(Restrictions.eq(PartnerRestrictions.FieldName_RelationShipType, query.getRelationshipType()));
    	}
    	
    	if(null != query.getIsAllowed()){
    		partnerRestrictionsCriteria.add(Restrictions.eq(PartnerRestrictions.FieldName_IsAllowed, query.getIsAllowed()));
    	}
    	
        processBaseQuery(query, partnerRestrictionsCriteria);

        //Paging
        processPaging(query, partnerRestrictionsCriteria);

        //applying Order
        partnerRestrictionsCriteria.addOrder(Order.desc(Partner.FieldName_RecordID));
        applyOrder(query, partnerRestrictionsCriteria);

        partnerRestrictions = partnerRestrictionsCriteria.list();
        
        log.info("PartnerRestrictionsDao :: get() END dctRestrictions="+partnerRestrictions);
    	return partnerRestrictions;
    }
}
