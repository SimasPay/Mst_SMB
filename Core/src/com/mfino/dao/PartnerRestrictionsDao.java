package com.mfino.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.PartnerRestrictionsQuery;
import com.mfino.domain.PartnerRestrictions;
import com.mfino.fix.CmFinoFIX;

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
    		partnerRestrictionsCriteria.add(Restrictions.eq(CmFinoFIX.CRPartnerRestrictions.FieldName_RecordID, query.getPartnerRestrictionsId()));
    	}
    	
    	if(null != query.getDctId()){
    		partnerRestrictionsCriteria.createAlias(CmFinoFIX.CRPartnerRestrictions.FieldName_DistributionChainTemplateByDCTID, "dct");
    		partnerRestrictionsCriteria.add(Restrictions.eq("dct."+CmFinoFIX.CRDistributionChainTemplate.FieldName_RecordID, query.getDctId()));
    	}
    	
    	if(null != query.getPartnerId()){
    		partnerRestrictionsCriteria.createAlias(CmFinoFIX.CRPartnerRestrictions.FieldName_Partner, "partner");
    		partnerRestrictionsCriteria.add(Restrictions.eq("partner."+CmFinoFIX.CRPartner.FieldName_RecordID, query.getPartnerId()));
    	}
    	
    	if(null != query.getTransactionTypeId()){
    		partnerRestrictionsCriteria.createAlias(CmFinoFIX.CRPartnerRestrictions.FieldName_TransactionType, "transactionType");
    		partnerRestrictionsCriteria.add(Restrictions.eq("transactionType."+CmFinoFIX.CRTransactionType.FieldName_RecordID, query.getTransactionTypeId()));
    	}

    	if(null != query.getRelationshipType()){
    		partnerRestrictionsCriteria.add(Restrictions.eq(CmFinoFIX.CRPartnerRestrictions.FieldName_RelationShipType, query.getRelationshipType()));
    	}
    	
    	if(null != query.getIsAllowed()){
    		partnerRestrictionsCriteria.add(Restrictions.eq(CmFinoFIX.CRPartnerRestrictions.FieldName_IsAllowed, query.getIsAllowed()));
    	}
    	
        processBaseQuery(query, partnerRestrictionsCriteria);

        //Paging
        processPaging(query, partnerRestrictionsCriteria);

        //applying Order
        partnerRestrictionsCriteria.addOrder(Order.desc(CmFinoFIX.CRPartner.FieldName_RecordID));
        applyOrder(query, partnerRestrictionsCriteria);

        partnerRestrictions = partnerRestrictionsCriteria.list();
        
        log.info("PartnerRestrictionsDao :: get() END dctRestrictions="+partnerRestrictions);
    	return partnerRestrictions;
    }
}
