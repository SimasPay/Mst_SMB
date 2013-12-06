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
import com.mfino.domain.TransactionRule;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.ConfigurationUtil;

/**
 * @author Bala Sunku
 *
 */
public class TransactionRuleDAO extends BaseDAO<TransactionRule> {
	
	public List<TransactionRule> get(TransactionRuleQuery query) {
		Criteria criteria = createCriteria();

		if (StringUtils.isNotBlank(query.getExactName())) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionRule.FieldName_Name, query.getExactName()).ignoreCase());
		}
		if (StringUtils.isNotBlank(query.getName())) {
			addLikeStartRestriction(criteria, CmFinoFIX.CRTransactionRule.FieldName_Name, query.getName());
		}
		if (query.getStartDate() != null) {
			criteria.add(Restrictions.ge(CmFinoFIX.CRTransactionRule.FieldName_CreateTime, query.getStartDate()));
		}
		if (query.getEndDate() != null) {
			criteria.add(Restrictions.le(CmFinoFIX.CRTransactionRule.FieldName_CreateTime, query.getEndDate()));
		}
		if (query.getServiceId() != null ) {
			criteria.createAlias(CmFinoFIX.CRTransactionRule.FieldName_Service, "s");
			criteria.add(Restrictions.eq("s." + CmFinoFIX.CRService.FieldName_RecordID, query.getServiceId()));
		}
		if (query.getTransactionTypeId() != null) {
			criteria.createAlias(CmFinoFIX.CRTransactionRule.FieldName_TransactionType, "tt");
			criteria.add(Restrictions.eq("tt."+CmFinoFIX.CRTransactionType.FieldName_RecordID, query.getTransactionTypeId()));
		}
		if (query.getChannelCodeId() != null) {
			criteria.createAlias(CmFinoFIX.CRTransactionRule.FieldName_ChannelCode, "cc");
			criteria.add(Restrictions.eq("cc."+CmFinoFIX.CRChannelCode.FieldName_RecordID, query.getChannelCodeId()));
		}
		if (query.getChargeMode() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionRule.FieldName_ChargeMode, query.getChargeMode()));
		}
		if (query.getSourceType() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionRule.FieldName_SourceType, query.getSourceType()));
		}
		if (query.getSourceKYC() != null) {
			criteria.createAlias(CmFinoFIX.CRTransactionRule.FieldName_KYCLevelBySourceKYC, "sourcekyc");
			criteria.add(Restrictions.eq("sourcekyc."+CmFinoFIX.CRKYCLevel.FieldName_RecordID, query.getSourceKYC()));
		}
		if (query.getDestType() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionRule.FieldName_DestType, query.getDestType()));
		}
		if (query.getDestKYC() != null) {
			criteria.createAlias(CmFinoFIX.CRTransactionRule.FieldName_KYCLevelByDestKYC, "destkyc");
			criteria.add(Restrictions.eq("destkyc."+CmFinoFIX.CRKYCLevel.FieldName_RecordID, query.getDestKYC()));
		}
		
		if(query.getSourceGroup() != null){
			if(query.isExactMatch()){
				criteria.createAlias(CmFinoFIX.CRTransactionRule.FieldName_GroupBySourceGroup, "sGroup");
				criteria.add(Restrictions.eq("sGroup."+CmFinoFIX.CRGroup.FieldName_RecordID, query.getSourceGroup()));
			}
			else{
				criteria.createAlias(CmFinoFIX.CRTransactionRule.FieldName_GroupBySourceGroup, "sGroup");
				criteria.add(Restrictions.or(Restrictions.eq("sGroup."+CmFinoFIX.CRGroup.FieldName_RecordID, query.getSourceGroup()),
						Restrictions.eq("sGroup."+CmFinoFIX.CRGroup.FieldName_RecordID, ConfigurationUtil.ANY_GROUP_ID)));
			}
		}
		
		if(query.getDestinationGroup() != null){
			if(query.isExactMatch()){
				criteria.createAlias(CmFinoFIX.CRTransactionRule.FieldName_GroupByDestinationGroup, "dGroup");
				criteria.add(Restrictions.eq("dGroup."+CmFinoFIX.CRGroup.FieldName_RecordID, query.getDestinationGroup()));
			}
			else
			{
				criteria.createAlias(CmFinoFIX.CRTransactionRule.FieldName_GroupByDestinationGroup, "dGroup");
				criteria.add(Restrictions.or(Restrictions.eq("dGroup."+CmFinoFIX.CRGroup.FieldName_RecordID, query.getDestinationGroup()),
												Restrictions.eq("dGroup."+CmFinoFIX.CRGroup.FieldName_RecordID, ConfigurationUtil.ANY_GROUP_ID)));
			}
		}
		
		criteria.addOrder(Order.asc(CmFinoFIX.CRTransactionRule.FieldName_RecordID));
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<TransactionRule> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(TransactionRule tr) {
        if (tr.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            tr.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(tr);
    }

}
