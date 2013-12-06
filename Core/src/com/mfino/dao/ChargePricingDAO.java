/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ChargePricingQuery;
import com.mfino.domain.ChargePricing;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class ChargePricingDAO extends BaseDAO<ChargePricing> {
	
	public List<ChargePricing> get(ChargePricingQuery query) {
		Criteria criteria = createCriteria();

		if (query.getChargeDefinitionId() != null ) {
			criteria.createAlias(CmFinoFIX.CRChargePricing.FieldName_ChargeDefinition, "cd");
			criteria.add(Restrictions.eq("cd." + CmFinoFIX.CRChargeDefinition.FieldName_RecordID, query.getChargeDefinitionId()));
		}

		criteria.addOrder(Order.asc(CmFinoFIX.CRChargePricing.FieldName_MinAmount));
		
		@SuppressWarnings("unchecked")
			List<ChargePricing> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(ChargePricing cp) {
        if (cp.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            cp.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(cp);
    }

}
