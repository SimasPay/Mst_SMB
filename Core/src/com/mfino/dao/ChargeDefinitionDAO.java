/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ChargeDefinitionQuery;
import com.mfino.domain.ChargeDefinition;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class ChargeDefinitionDAO extends BaseDAO<ChargeDefinition> {
	
	public List<ChargeDefinition> get(ChargeDefinitionQuery query) {
		Criteria criteria = createCriteria();

		if (StringUtils.isNotBlank(query.getExactName())) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRChargeDefinition.FieldName_Name, query.getExactName()).ignoreCase());
		}
		if (StringUtils.isNotBlank(query.getName())) {
			addLikeStartRestriction(criteria, CmFinoFIX.CRChargeDefinition.FieldName_Name, query.getName());
		}
		if (query.getStartDate() != null) {
			criteria.add(Restrictions.ge(CmFinoFIX.CRChargeDefinition.FieldName_CreateTime, query.getStartDate()));
		}
		if (query.getEndDate() != null) {
			criteria.add(Restrictions.le(CmFinoFIX.CRChargeDefinition.FieldName_CreateTime, query.getEndDate()));
		}
		if (query.getChargeTypeId() != null ) {
			criteria.createAlias(CmFinoFIX.CRChargeDefinition.FieldName_ChargeType, "ct");
			criteria.add(Restrictions.eq("ct." + CmFinoFIX.CRChargeType.FieldName_RecordID, query.getChargeTypeId()));
		}
		if(query.isFundingPartnerAndPocketNotNull()){
			criteria.add(Restrictions.isNotNull(CmFinoFIX.CRChargeDefinition.FieldName_PartnerByFundingPartnerID));
			criteria.add(Restrictions.isNotNull(CmFinoFIX.CRChargeDefinition.FieldName_Pocket));
		}
		criteria.addOrder(Order.asc(CmFinoFIX.CRChargeDefinition.FieldName_RecordID));
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
			List<ChargeDefinition> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(ChargeDefinition cd) {
        if (cd.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            cd.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(cd);
    }

}
