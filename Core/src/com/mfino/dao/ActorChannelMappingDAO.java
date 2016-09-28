package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ActorChannelMappingQuery;
import com.mfino.domain.ActorChannelMapping;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Group;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Service;
import com.mfino.domain.TransactionType;

/**
 * @author Srikanth
 *
 */
public class ActorChannelMappingDAO extends BaseDAO<ActorChannelMapping> {
	
	public List<ActorChannelMapping> get(ActorChannelMappingQuery query) {
		Criteria criteria = createCriteria();

		if (query.getSubscriberType() != null ) {			
			criteria.add(Restrictions.eq(ActorChannelMapping.FieldName_SubscriberType, query.getSubscriberType()));
		}
		if (query.getPartnerType() != null ) {			
			criteria.add(Restrictions.eq(ActorChannelMapping.FieldName_BusinessPartnerType, query.getPartnerType()));
		}
		if (query.getServiceID() != null ) {
			criteria.createAlias(ActorChannelMapping.FieldName_Service, "s");
			criteria.add(Restrictions.eq("s." + Service.FieldName_RecordID, query.getServiceID()));
		}
		if (query.getTransactionTypeID() != null) {
			criteria.createAlias(ActorChannelMapping.FieldName_TransactionType, "tt");
			criteria.add(Restrictions.eq("tt."+TransactionType.FieldName_RecordID, query.getTransactionTypeID()));
		}
		if (query.getChannelCodeID() != null) {
			criteria.createAlias(ActorChannelMapping.FieldName_ChannelCode, "cc");
			criteria.add(Restrictions.eq("cc."+ChannelCode.FieldName_RecordID, query.getChannelCodeID()));
		}
		if (query.getKycLevel() != null) {
			criteria.createAlias(ActorChannelMapping.FieldName_KYCLevelByKYCLevel, "kyc");
			criteria.add(Restrictions.eq("kyc."+KYCLevel.FieldName_RecordID, query.getKycLevel()));
		}		
		if(query.getGroup() != null){
			criteria.createAlias(ActorChannelMapping.FieldName_Group, "group");
			criteria.add(Restrictions.eq("group."+Group.FieldName_RecordID, query.getGroup()));					
		}		
		criteria.addOrder(Order.asc(ActorChannelMapping.FieldName_RecordID));
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<ActorChannelMapping> lst = criteria.list();
			
		return lst;
	}
}
