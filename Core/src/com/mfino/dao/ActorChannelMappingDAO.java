package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ActorChannelMappingQuery;
import com.mfino.domain.ActorChannelMapping;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Srikanth
 *
 */
public class ActorChannelMappingDAO extends BaseDAO<ActorChannelMapping> {
	
	public List<ActorChannelMapping> get(ActorChannelMappingQuery query) {
		Criteria criteria = createCriteria();

		if (query.getSubscriberType() != null ) {			
			criteria.add(Restrictions.eq(CmFinoFIX.CRActorChannelMapping.FieldName_SubscriberType, query.getSubscriberType()));
		}
		if (query.getPartnerType() != null ) {			
			criteria.add(Restrictions.eq(CmFinoFIX.CRActorChannelMapping.FieldName_BusinessPartnerType, query.getPartnerType()));
		}
		if (query.getServiceID() != null ) {
			criteria.createAlias(CmFinoFIX.CRActorChannelMapping.FieldName_Service , "s");
			criteria.add(Restrictions.eq("s." + CmFinoFIX.CRService.FieldName_RecordID, query.getServiceID()));
		}
		if (query.getTransactionTypeID() != null) {
			criteria.createAlias(CmFinoFIX.CRActorChannelMapping.FieldName_TransactionType, "tt");
			criteria.add(Restrictions.eq("tt."+CmFinoFIX.CRTransactionType.FieldName_RecordID, query.getTransactionTypeID()));
		}
		if (query.getChannelCodeID() != null) {
			criteria.createAlias(CmFinoFIX.CRActorChannelMapping.FieldName_ChannelCode , "cc");
			criteria.add(Restrictions.eq("cc."+CmFinoFIX.CRChannelCode.FieldName_RecordID, query.getChannelCodeID()));
		}
		if (query.getKycLevel() != null) {
			criteria.createAlias(CmFinoFIX.CRActorChannelMapping.FieldName_KYCLevelByKYCLevel, "kyc");
			criteria.add(Restrictions.eq("kyc."+CmFinoFIX.CRKYCLevel.FieldName_RecordID, query.getKycLevel()));
		}		
		if(query.getGroup() != null){
			criteria.createAlias(CmFinoFIX.CRActorChannelMapping.FieldName_Group, "group");
			criteria.add(Restrictions.eq("group."+CmFinoFIX.CRGroup.FieldName_RecordID, query.getGroup()));					
		}		
		criteria.addOrder(Order.asc(CmFinoFIX.CRActorChannelMapping.FieldName_RecordID));
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<ActorChannelMapping> lst = criteria.list();
			
		return lst;
	}
}
