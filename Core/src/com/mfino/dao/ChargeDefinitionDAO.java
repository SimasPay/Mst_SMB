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
import com.mfino.domain.ChargeType;
import com.mfino.domain.MfinoServiceProvider;

/**
 * @author Bala Sunku
 *
 */
public class ChargeDefinitionDAO extends BaseDAO<ChargeDefinition> {
	
	public List<ChargeDefinition> get(ChargeDefinitionQuery query) {
		Criteria criteria = createCriteria();

		if (StringUtils.isNotBlank(query.getExactName())) {
			criteria.add(Restrictions.eq(ChargeDefinition.FieldName_Name, query.getExactName()).ignoreCase());
		}
		if (StringUtils.isNotBlank(query.getName())) {
			addLikeStartRestriction(criteria, ChargeDefinition.FieldName_Name, query.getName());
		}
		if (query.getStartDate() != null) {
			criteria.add(Restrictions.ge(ChargeDefinition.FieldName_CreateTime, query.getStartDate()));
		}
		if (query.getEndDate() != null) {
			criteria.add(Restrictions.le(ChargeDefinition.FieldName_CreateTime, query.getEndDate()));
		}
		if (query.getChargeTypeId() != null ) {
			criteria.createAlias(ChargeDefinition.FieldName_ChargeType, "ct");
			criteria.add(Restrictions.eq("ct." + ChargeType.FieldName_RecordID, query.getChargeTypeId()));
		}
		if(query.isFundingPartnerAndPocketNotNull()){
			criteria.add(Restrictions.isNotNull(ChargeDefinition.FieldName_PartnerByFundingPartnerID));
			criteria.add(Restrictions.isNotNull(ChargeDefinition.FieldName_Pocket));
		}
		criteria.addOrder(Order.asc(ChargeDefinition.FieldName_RecordID));
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
			List<ChargeDefinition> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(ChargeDefinition cd) {
        if (cd.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            cd.setMfinoServiceProvider(msp);
        }
        super.save(cd);
    }

}
