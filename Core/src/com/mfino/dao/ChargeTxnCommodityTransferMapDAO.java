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
import com.mfino.domain.ChargetxnTransferMap;

/**
 * @author Chaitanya
 *
 */
public class ChargeTxnCommodityTransferMapDAO extends
		BaseDAO<ChargetxnTransferMap> {

	public List<ChargetxnTransferMap> get(ChargeTxnCommodityTransferMapQuery query){
		
		Criteria criteria = createCriteria();
		if(query.getSctlID()!=null){
			criteria.add(Restrictions.eq(ChargetxnTransferMap.FieldName_SctlId, query.getSctlID()));
		}
		if(query.getCommodityTransferID()!=null){
			criteria.add(Restrictions.eq(ChargetxnTransferMap.FieldName_CommodityTransferID, query.getCommodityTransferID()));
		}
		
        processBaseQuery(query, criteria);
        processPaging(query, criteria);
		
        criteria.addOrder(Order.desc(ChargetxnTransferMap.FieldName_RecordID));
        
        List<ChargetxnTransferMap> map = criteria.list();
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
		criteria.add(Restrictions.eq(ChargetxnTransferMap.FieldName_CommodityTransferID, ctId));
		
		ChargetxnTransferMap ctMap = (ChargetxnTransferMap)criteria.uniqueResult();
		if (ctMap != null) {
			sctlId = ctMap.getSctlid().longValue();
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
		criteria.add(Restrictions.eq(ChargetxnTransferMap.FieldName_SctlId, sctlId));
		List<Long> transferIDs = null; 
		@SuppressWarnings("unchecked")
		List<ChargetxnTransferMap> ctMapList = criteria.list();
		if(ctMapList !=null && ctMapList.size()>0)
			transferIDs = new ArrayList<Long>();
		for(ChargetxnTransferMap ctxMap :ctMapList) {
			if (ctxMap.getCommoditytransferid() != null) {
				transferIDs.add(ctxMap.getCommoditytransferid().longValue());
			}
		}
		return transferIDs;
	}
}

