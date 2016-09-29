package com.mfino.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.MFSLedgerQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.LedgerBalance;
import com.mfino.domain.MFSLedger;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;

public class MFSLedgerDAO extends BaseDAO<MFSLedger> {

	@SuppressWarnings("unchecked")
	public List<MFSLedger> get(MFSLedgerQuery query) {
		 Criteria criteria = createCriteria();
		 
		 if (query.getPocketId() != null) {
			 criteria.add(Restrictions.eq(MFSLedger.FieldName_PocketID, query.getPocketId()));
		 }
		 
		 if (query.getMdnId() != null) {
			 PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
			 PocketQuery pocketQuery = new PocketQuery();
			 pocketQuery.setMdnIDSearch(query.getMdnId());
			 List<Pocket> lstPockets = pocketDAO.get(pocketQuery);
			 if (CollectionUtils.isNotEmpty(lstPockets)) {
				 Long[] pocketArray = new Long[lstPockets.size()];
				 int i = 0;
				 for (Pocket p:lstPockets) {
					 pocketArray[i] = p.getId().longValue();
					 i++;
				 }
				 criteria.add(Restrictions.in(MFSLedger.FieldName_PocketID, pocketArray));
			 }
		 }
		 
		 if(query.getCreateTimeLT()!=null){
			 criteria.add(Restrictions.le(MFSLedger.FieldName_CreateTime, query.getCreateTimeLT()));	 
		 }
		 if(query.getCreateTimeGE()!=null){
			 criteria.add(Restrictions.ge(MFSLedger.FieldName_CreateTime, query.getCreateTimeGE()));	 
		 }
		 if(query.getTransferIDs()!=null){
			 criteria.add(Restrictions.in(MFSLedger.FieldName_CommodityTransferID, query.getTransferIDs()));	 
		 }		 
		 
		 
		 criteria.addOrder(Order.desc(MFSLedger.FieldName_CreateTime));
		 criteria.addOrder(Order.asc(MFSLedger.FieldName_RecordID));
		 processBaseQuery(query, criteria);
		 processPaging(query, criteria);
		 applyOrder(query, criteria) ;
		 
		 List<MFSLedger> results =criteria.list();
		 return results;
	}
	
	/**
	 * Returns the MFS Ledger entries created as part of given Commodity Transfer Id
	 * @param ctId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MFSLedger> getLedgerEntriesByCommodityTransferId(Long ctId) {
		if(ctId == null){
			return null;
		}
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(MFSLedger.FieldName_CommodityTransferID, ctId));
		criteria.addOrder(Order.desc(MFSLedger.FieldName_CreateTime));
		return criteria.list();
	}
	
	/**
	 * Returns the MFS Ledger entries based on the given Ledger Status
	 * @param ledgerStatus
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MFSLedger> getLedgerEntriesByLedgerStatus(String ledgerStatus) {
		if(StringUtils.isBlank(ledgerStatus)) {
			return null;
		}
		
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(MFSLedger.FieldName_LedgerStatus, ledgerStatus));
		criteria.addOrder(Order.asc(MFSLedger.FieldName_CreateTime));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MFSLedger> getLedgerEntriesBySctlId(Long sctlId) {
		if (sctlId == null) {
			return null;
		}
		
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(MFSLedger.FieldName_SctlId, sctlId));
		criteria.addOrder(Order.asc(MFSLedger.FieldName_CreateTime));
		return criteria.list();
	}
	public List<LedgerBalance> getConsolidateBalance(Date startDate, Date endDate) {
		List<LedgerBalance> ledgerBalances=null;;
		String selectHql = "select  new com.mfino.domain.LedgerBalance( sum(l.Amount) ,l.PocketID, l.LedgerType) from MFSLedger l where l.CreateTime >= :startDate  and l.CreateTime < :endDate group by l.PocketID,l.LedgerType";
		Query query = getSession().createQuery(selectHql).setDate("startDate",
				startDate);
		query.setDate("endDate", endDate);

		if (query.list() != null && query.list().size() > 0) {
			ledgerBalances = new ArrayList<LedgerBalance>(query.list().size());

			for (Object object : query.list()) {
				if (object != null) {
					LedgerBalance ledgerbalance = (LedgerBalance) object;
					ledgerBalances.add(ledgerbalance);
				}
			}

		}
		return ledgerBalances;
	}
}
