/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ServiceSettlementConfigQuery;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.ServiceSettlementCfg;

/**
 * @author Bala Sunku
 *
 */
public class ServiceSettlementConfigDAO extends BaseDAO<ServiceSettlementCfg> {
	
	public List<ServiceSettlementCfg> get(ServiceSettlementConfigQuery query) {
		Criteria criteria = createCriteria();
		
		if (query.getPartnerServiceId() != null) {
			criteria.createAlias(ServiceSettlementCfg.FieldName_PartnerServicesByPartnerServiceID, "sc");
			criteria.add(Restrictions.eq("sc."+ServiceSettlementCfg.FieldName_RecordID, query.getPartnerServiceId()));
		}

		if (query.getCollectorPocket() != null) {
			criteria.createAlias(ServiceSettlementCfg.FieldName_PocketByCollectorPocket, "cp");
			criteria.add(Restrictions.eq("cp."+ServiceSettlementCfg.FieldName_RecordID, query.getCollectorPocket().getId()));
		}
		
		if(query.getSchedulerStatus()>=0){
			criteria.add(Restrictions.eq(ServiceSettlementCfg.FieldName_SchedulerStatus, query.getSchedulerStatus()));
		}
		criteria.addOrder(Order.asc(ServiceSettlementCfg.FieldName_StartDate));
		
		@SuppressWarnings("unchecked")
			List<ServiceSettlementCfg> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(ServiceSettlementCfg sc) {
        if (sc.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            sc.setMfinoServiceProvider(msp);
        }
        super.save(sc);
    }

}
