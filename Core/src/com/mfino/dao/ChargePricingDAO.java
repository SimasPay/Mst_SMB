/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ChargePricingQuery;
import com.mfino.domain.ChargeDefinition;
import com.mfino.domain.ChargePricing;
import com.mfino.domain.MfinoServiceProvider;

/**
 * @author Bala Sunku
 *
 */
public class ChargePricingDAO extends BaseDAO<ChargePricing> {
	
	public List<ChargePricing> get(ChargePricingQuery query) {
		Criteria criteria = createCriteria();

		if (query.getChargeDefinitionId() != null ) {
			criteria.createAlias(ChargePricing.FieldName_ChargeDefinition, "cd");
			criteria.add(Restrictions.eq("cd." + ChargeDefinition.FieldName_RecordID, query.getChargeDefinitionId()));
		}

		criteria.addOrder(Order.asc(ChargePricing.FieldName_MinAmount));
		
		@SuppressWarnings("unchecked")
			List<ChargePricing> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(ChargePricing cp) {
        if (cp.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            cp.setMfinoServiceProvider(msp);
        }
        super.save(cp);
    }

}
