package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.LedgerQuery;
import com.mfino.domain.Ledger;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRLedger;

@Deprecated
public class LedgerDAO extends BaseDAO<Ledger> {

	@Deprecated
	@SuppressWarnings("unchecked")
	public List<Ledger> get(LedgerQuery query) {
		 Criteria criteria = createCriteria();
		 
		 if(query.getSourceDestnPocketID()!=null){
			 criteria.add(Restrictions.disjunction().add(Restrictions.eq(CRLedger.FieldName_SourcePocketID, query.getSourceDestnPocketID())).add(Restrictions.eq(CmFinoFIX.CRLedger.FieldName_DestPocketID, query.getSourceDestnPocketID())));
		 }
		 if(query.getSourcenDestMDN()!=null){
			 criteria.add(Restrictions.disjunction().add(Restrictions.eq(CRLedger.FieldName_SourceMDN, query.getSourcenDestMDN())).add(Restrictions.eq(CmFinoFIX.CRLedger.FieldName_DestMDN, query.getSourcenDestMDN()))); 
		 }
		 if(query.getCreateTimeLT()!=null){
			 criteria.add(Restrictions.le(CRLedger.FieldName_CreateTime, query.getCreateTimeLT()));	 
		 }
		 if(query.getCreateTimeGE()!=null){
			 criteria.add(Restrictions.ge(CRLedger.FieldName_CreateTime, query.getCreateTimeGE()));	 
		 }
		 if(query.getTransferIDs()!=null){
			 criteria.add(Restrictions.in(CRLedger.FieldName_CommodityTransferID, query.getTransferIDs()));	 
		 }
		 criteria.addOrder(Order.desc(CmFinoFIX.CRLedger.FieldName_RecordID));
		 processBaseQuery(query, criteria);
		processPaging(query, criteria);
		applyOrder(query, criteria) ;
		 List<Ledger> results =criteria.list();
		 return results;
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public List<Ledger> getByCommmodityTransferID(Long commodityTransferId) {
		if(commodityTransferId==null){
			return null;
		}
		 Criteria criteria = createCriteria();
		 criteria.add(Restrictions.eq(CmFinoFIX.CRLedger.FieldName_CommodityTransferID, commodityTransferId));
		 criteria.addOrder(Order.asc(CmFinoFIX.CRLedger.FieldName_RecordID));
		return criteria.list();
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	public List<Ledger> getBySctlID(Long sctlId) {
		if(sctlId==null){
			return null;
		}
		String hqlString = "Select l from Ledger as l," +
				  "ChargeTxnCommodityTransferMap as ctm" + 
				  " where l.CommodityTransferID = ctm.CommodityTransferID and ctm.SctlId = :sctlId";
		Query queryObj = getQuery(hqlString);
		queryObj.setLong("sctlId", sctlId);		
		return queryObj.list();
	}
	
	
}
