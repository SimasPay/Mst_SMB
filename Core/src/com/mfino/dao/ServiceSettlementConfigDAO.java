/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ServiceSettlementConfigQuery;
import com.mfino.domain.ServiceSettlementConfig;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class ServiceSettlementConfigDAO extends BaseDAO<ServiceSettlementConfig> {
	
	public List<ServiceSettlementConfig> get(ServiceSettlementConfigQuery query) {
		Criteria criteria = createCriteria();
		
		if (query.getPartnerServiceId() != null) {
			criteria.createAlias(CmFinoFIX.CRServiceSettlementConfig.FieldName_PartnerServicesByPartnerServiceID, "sc");
			criteria.add(Restrictions.eq("sc."+CmFinoFIX.CRServiceSettlementConfig.FieldName_RecordID, query.getPartnerServiceId()));
		}

		if (query.getCollectorPocket() != null) {
			criteria.createAlias(CmFinoFIX.CRServiceSettlementConfig.FieldName_PocketByCollectorPocket, "cp");
			criteria.add(Restrictions.eq("cp."+CmFinoFIX.CRServiceSettlementConfig.FieldName_RecordID, query.getCollectorPocket().getID()));
		}
		
		if(query.getSchedulerStatus()>=0){
			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceSettlementConfig.FieldName_SchedulerStatus, query.getSchedulerStatus()));
		}
		criteria.addOrder(Order.asc(CmFinoFIX.CRServiceSettlementConfig.FieldName_StartDate));
		
		@SuppressWarnings("unchecked")
			List<ServiceSettlementConfig> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(ServiceSettlementConfig sc) {
        if (sc.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            sc.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(sc);
    }

}
