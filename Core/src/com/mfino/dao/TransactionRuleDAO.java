/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.TransactionRuleQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Groups;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.Service;
import com.mfino.domain.TransactionRule;
import com.mfino.domain.TransactionType;
import com.mfino.util.ConfigurationUtil;

/**
 * @author Bala Sunku
 *
 */
public class TransactionRuleDAO extends BaseDAO<TransactionRule> {
	
	public List<TransactionRule> get(TransactionRuleQuery query) {
		Criteria criteria = createCriteria();

		if (StringUtils.isNotBlank(query.getExactName())) {
			criteria.add(Restrictions.eq(TransactionRule.FieldName_Name, query.getExactName()).ignoreCase());
		}
		if (StringUtils.isNotBlank(query.getName())) {
			addLikeStartRestriction(criteria, TransactionRule.FieldName_Name, query.getName());
		}
		if (query.getStartDate() != null) {
			criteria.add(Restrictions.ge(TransactionRule.FieldName_CreateTime, query.getStartDate()));
		}
		if (query.getEndDate() != null) {
			criteria.add(Restrictions.le(TransactionRule.FieldName_CreateTime, query.getEndDate()));
		}
		if (query.getServiceId() != null ) {
			criteria.createAlias(TransactionRule.FieldName_Service, "s");
			criteria.add(Restrictions.eq("s." + Service.FieldName_RecordID, query.getServiceId()));
		}
		if (query.getTransactionTypeId() != null) {
			criteria.createAlias(TransactionRule.FieldName_TransactionType, "tt");
			criteria.add(Restrictions.eq("tt."+TransactionType.FieldName_RecordID, query.getTransactionTypeId()));
		}
		if (query.getChannelCodeId() != null) {
			criteria.createAlias(TransactionRule.FieldName_ChannelCode, "cc");
			criteria.add(Restrictions.eq("cc."+ChannelCode.FieldName_RecordID, query.getChannelCodeId()));
		}
		if (query.getChargeMode() != null) {
			criteria.add(Restrictions.eq(TransactionRule.FieldName_ChargeMode, query.getChargeMode()));
		}
		if (query.getSourceType() != null) {
			criteria.add(Restrictions.eq(TransactionRule.FieldName_SourceType, query.getSourceType()));
		}
		if (query.getSourceKYC() != null) {
			criteria.createAlias(TransactionRule.FieldName_KYCLevelBySourceKYC, "sourcekyc");
			criteria.add(Restrictions.eq("sourcekyc."+KYCLevel.FieldName_RecordID, query.getSourceKYC()));
		}
		if (query.getDestType() != null) {
			criteria.add(Restrictions.eq(TransactionRule.FieldName_DestType, query.getDestType()));
		}
		if (query.getDestKYC() != null) {
			criteria.createAlias(TransactionRule.FieldName_KYCLevelByDestKYC, "destkyc");
			criteria.add(Restrictions.eq("destkyc."+KYCLevel.FieldName_RecordID, query.getDestKYC()));
		}
		
		if(query.getSourceGroup() != null){
			if(query.isExactMatch()){
				criteria.createAlias(TransactionRule.FieldName_GroupBySourceGroupID, "sGroup");
				criteria.add(Restrictions.eq("sGroup."+Groups.FieldName_RecordID, query.getSourceGroup()));
			}
			else{
				criteria.createAlias(TransactionRule.FieldName_GroupBySourceGroupID, "sGroup");
				criteria.add(Restrictions.or(Restrictions.eq("sGroup."+Groups.FieldName_RecordID, query.getSourceGroup()),
						Restrictions.eq("sGroup."+Groups.FieldName_RecordID, ConfigurationUtil.ANY_GROUP_ID)));
			}
		}
		
		if(query.getDestinationGroup() != null){
			if(query.isExactMatch()){
				criteria.add(Restrictions.eq(TransactionRule.FieldName_GroupByDestinationGroupID, query.getDestinationGroup()));
			}
			else
			{
				criteria.add(Restrictions.or(Restrictions.eq(TransactionRule.FieldName_GroupByDestinationGroupID, query.getDestinationGroup()),
												Restrictions.eq(TransactionRule.FieldName_GroupByDestinationGroupID, ConfigurationUtil.ANY_GROUP_ID)));
			}
		}
		
		criteria.addOrder(Order.asc(TransactionRule.FieldName_RecordID));
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<TransactionRule> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(TransactionRule tr) {
        if (tr.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            tr.setMfinoServiceProvider(msp);
        }
        super.save(tr);
    }

}
