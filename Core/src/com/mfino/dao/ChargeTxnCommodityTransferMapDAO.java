/**
 * 
 */
package com.mfino.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.domain.ChargeTxnCommodityTransferMap;

/**
 * @author Chaitanya
 *
 */
public class ChargeTxnCommodityTransferMapDAO extends
		BaseDAO<ChargeTxnCommodityTransferMap> {

	public List<ChargeTxnCommodityTransferMap> get(ChargeTxnCommodityTransferMapQuery query){
		
		Criteria criteria = createCriteria();
		if(query.getSctlID()!=null){
			criteria.add(Restrictions.eq(ChargeTxnCommodityTransferMap.FieldName_SctlId, query.getSctlID()));
		}
		if(query.getCommodityTransferID()!=null){
			criteria.add(Restrictions.eq(ChargeTxnCommodityTransferMap.FieldName_CommodityTransferID, query.getCommodityTransferID()));
		}
		
        processBaseQuery(query, criteria);
        processPaging(query, criteria);
		
        criteria.addOrder(Order.desc(ChargeTxnCommodityTransferMap.FieldName_RecordID));
        
        List<ChargeTxnCommodityTransferMap> map = criteria.list();
 		return map;
	}
	
	/**
	 * Returns the service Charge Transaction Log id for the Commodity Transfer Id
	 * @param ctId
	 * @return
	 */
	public Long getSCTLIdByCommodityTransferId(Long ctId) {
		Long sctlId = null;
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(ChargeTxnCommodityTransferMap.FieldName_CommodityTransferID, ctId));
		
		ChargeTxnCommodityTransferMap ctMap = (ChargeTxnCommodityTransferMap)criteria.uniqueResult();
		if (ctMap != null) {
			sctlId = ctMap.getSctlId();
		}
		return sctlId;
	}
	/**
	 * Returns the service Charge Transaction Log id for the Commodity Transfer Id
	 * @param ctId
	 * @return
	 */
	public List<Long> geTransferIdsBySCTLId(Long sctlId) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(ChargeTxnCommodityTransferMap.FieldName_SctlId, sctlId));
		List<Long> transferIDs = null; 
		@SuppressWarnings("unchecked")
		List<ChargeTxnCommodityTransferMap> ctMapList = criteria.list();
		if(ctMapList !=null && ctMapList.size()>0)
			transferIDs = new ArrayList<Long>();
		for(ChargeTxnCommodityTransferMap ctxMap :ctMapList) {
			transferIDs.add(ctxMap.getCommodityTransferID());
		}
		return transferIDs;
	}
}

