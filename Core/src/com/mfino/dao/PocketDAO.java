/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.MfsLedger;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author Venkata Krishna Teja D
 */
public class PocketDAO extends BaseDAO<Pocket> {

    public static final String POCKETTEMPLATETABLE = "pocketTemplateByPockettemplateid";
    public static final String POCKETTEMPLATE_ALIAS = "PocketTemplateAlias";

    public PocketDAO() {
        super();
    }

    public List<Pocket> get(PocketQuery query) {
        Criteria criteria = createCriteria();

        if (query.getMdnIDSearch() != null) {
            //TODO : make the associate entity name a constant in the codegen
            criteria.createCriteria("subscriberMdn").add(Restrictions.eq(SubscriberMdn.FieldName_RecordID, query.getMdnIDSearch()));
        }

        if (query.isIsDefault() != null) {
            criteria.add(Restrictions.eq(Pocket.FieldName_IsDefault, query.isIsDefault()));
        }

        if (StringUtils.isNotBlank(query.getCardPan())) {
            if (Boolean.TRUE == query.isPocketCardPanLikeSearch()) {
                addLikeStartRestriction(criteria, Pocket.FieldName_CardPAN, query.getCardPan());
            } else {
                criteria.add(Restrictions.eq(Pocket.FieldName_CardPAN, query.getCardPan()));
            }            
        }
        if (StringUtils.isNotBlank(query.getCardAlias())){
            criteria.add(Restrictions.eq(Pocket.FieldName_CardAlias, query.getCardAlias()));
        }

        if (query.getPocketStatus() != null) {
            criteria.add(Restrictions.eq(Pocket.FieldName_PocketStatus, query.getPocketStatus()));
        }

        if (query.getPocketTemplateID() != null || query.getPocketType() != null || query.getCommodity() != null || 
        		query.getIsCollectorPocket() != null) {

            criteria.createAlias(POCKETTEMPLATETABLE, POCKETTEMPLATE_ALIAS);

            if (query.getPocketTemplateID() != null) {
                criteria.add(Restrictions.eq(POCKETTEMPLATE_ALIAS + DAOConstants.ALIAS_COLNAME_SEPARATOR + PocketTemplate.FieldName_RecordID, query.getPocketTemplateID()));
            }
            if (query.getPocketType() != null) {
                criteria.add(Restrictions.eq(POCKETTEMPLATE_ALIAS + DAOConstants.ALIAS_COLNAME_SEPARATOR + PocketTemplate.FieldName_PocketType, query.getPocketType()));
            }
            if (query.getCommodity() != null) {
                criteria.add(Restrictions.eq(POCKETTEMPLATE_ALIAS + DAOConstants.ALIAS_COLNAME_SEPARATOR + PocketTemplate.FieldName_Commodity, query.getCommodity()));
            }
            if (query.getIsCollectorPocket() != null) {
            	criteria.add(Restrictions.eq(POCKETTEMPLATE_ALIAS + DAOConstants.ALIAS_COLNAME_SEPARATOR + 
            			PocketTemplate.FieldName_IsCollectorPocket, query.getIsCollectorPocket()));
            }
            else  if(query.IsCollectorPocketAllowed()==null||!query.IsCollectorPocketAllowed()){
            	criteria.add(Restrictions.eq(POCKETTEMPLATE_ALIAS + DAOConstants.ALIAS_COLNAME_SEPARATOR + 
            			PocketTemplate.FieldName_IsCollectorPocket, Boolean.FALSE));
            }
            if (query.getIsSuspencePocketAllowed() == null || !query.getIsSuspencePocketAllowed()) {
            	criteria.add(Restrictions.eq(POCKETTEMPLATE_ALIAS + DAOConstants.ALIAS_COLNAME_SEPARATOR + PocketTemplate.FieldName_IsSuspencePocket, Boolean.FALSE));
            } else {
            	criteria.add(Restrictions.eq(POCKETTEMPLATE_ALIAS + DAOConstants.ALIAS_COLNAME_SEPARATOR + PocketTemplate.FieldName_IsSuspencePocket, Boolean.TRUE));
            }
        }
        if (query.getCompany() != null) {
            criteria.add(Restrictions.eq(Pocket.FieldName_Company, query.getCompany()));
        }
        if (query.getBankCode() != null) {
            criteria.createAlias(POCKETTEMPLATETABLE, POCKETTEMPLATE_ALIAS);
            criteria.add(Restrictions.eq(POCKETTEMPLATE_ALIAS + DAOConstants.ALIAS_COLNAME_SEPARATOR + PocketTemplate.FieldName_BankCodeForRouting, query.getBankCode()));
        }
        if (StringUtils.isNotBlank(query.getStatusSearchString())) {
        	String[] strArray = query.getStatusSearchString().split(",");
        	Integer[] intArray = new Integer[strArray.length];
        	int i = 0;
        	for (String s: strArray) {
        		intArray[i] = new Integer(s);
        		i++;
        	}
        	criteria.add(Restrictions.in(Pocket.FieldName_PocketStatus, intArray));
        }
        
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<Pocket> results = criteria.list();

        return results;
    }

    public List<Pocket> getDompetMerchantByHQL(Long companyId) {
        String hqlString = "Select p from Pocket as p join p.pocketTemplate as pt where bitwise_and(pt.Allowance " + "," + CmFinoFIX.PocketAllowance_MerchantDompet + ") > 0 " +
                "and p.company = :companyid";

        Query queryObj = getQuery(hqlString);
        queryObj.setLong("companyid", companyId);
        @SuppressWarnings("unchecked")
        List<Pocket> results = queryObj.list();

        return results;
    }
    
    @Override
    public void save(Pocket pocket) {
       //initial balance to 0
    	if (pocket.getCurrentbalance()==null) {
            pocket.setCurrentbalance("0");
         }
        super.save(pocket);
    }
    
    @Override
    public void saveWithoutFlush(Pocket pocket){
    	 //initial balance to 0
    	if (pocket.getCurrentbalance()==null) {
            pocket.setCurrentbalance("0");
         }
        super.saveWithoutFlush(pocket);
    }

	@SuppressWarnings("unchecked")
	public List<Pocket> getLastUpdatedPockets(Date start, Date end) {
		Criteria criteria = createCriteria();
		if(start!=null&&end!=null){
			criteria.add(Restrictions.between(Pocket.FieldName_LastUpdateTime, start, end));
		}else{
			if(start!=null){
				criteria.add(Restrictions.gt(Pocket.FieldName_LastUpdateTime, start));
			}
			if(end!=null){
				criteria.add(Restrictions.lt(Pocket.FieldName_LastUpdateTime, end));
			}
		}
		 List<Pocket> results = criteria.list();
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Pocket> getDailylimitUtilizedPockets(Date start, Date end, BigDecimal utilizationPercent) {
		 String hqlString = "Select p from Pocket as p join p.PocketTemplate as pt where p.LastTransactionTime between :start and :end and p.CurrentDailyExpenditure>=pt.MaxAmountPerDay* :percent";
	        Query queryObj = getQuery(hqlString);
	        queryObj.setDate("start", start);
	        queryObj.setDate("end", end);
	        queryObj.setBigDecimal("percent", utilizationPercent);
	        List<Pocket> results = queryObj.list();

	        return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<Pocket> getPocketsForCommodityTransferAdjustments(Long sctlId, Long globalAccountPocketId) {
		/*
		 * hsql query - not used as hsql doesnt support UNION
		 * 
		 * String hqlString = "Select distinct p from Pocket as p, " +
												  "ChargeTxnCommodityTransferMap as ctm, " + 
												  "Ledger as l where " + 
												  "(p.id = l.SourcePocketID or p.id = l.DestPocketID) and l.CommodityTransferID = ctm.CommodityTransferID and ctm.SctlId = :sctlId " +
									"union " +
							"Select distinct p from Pocket as p, " +
							  					"ChargeTxnCommodityTransferMap as ctm, " + 
							  					"CommodityTransfer as ct where " + 
							  					"(p.id = ct.PocketBySourcePocketID.id or p.id = ct.DestPocketID) and ct.id = ctm.CommodityTransferID and ctm.SctlId = :sctlId " +
							  		"union " +
							"Select p from Pocket as p where " +
							  					"p.id = :globalAccountPocketId";*/
		// using Native sql query to use "UNION"
		String queryString = "Select distinct p.* from pocket p, chargetxn_transfer_map ctm, commodity_transfer  ct " +
									  "where ctm.commoditytransferid = ct.id and ctm.sctlid = :sctlId and (p.id = ct.sourcepocketid or p.id = ct.DestPocketID) " +
									  		"union " +
							 "Select distinct p.* from pocket p, ledger l, chargetxn_transfer_map ctm " +
							  		  "where l.commoditytransferid = ctm.commoditytransferid and ctm.sctlid = :sctlId and (p.id = l.SourcePocketID or p.id = l.DestPocketID) " +
							  		  		"union " +
							 "Select p.* from pocket p where p.id = :globalAccountPocketId";
		SQLQuery queryObj = getSession().createSQLQuery(queryString).addEntity(Pocket.class);		
		queryObj.setParameter("sctlId", sctlId);	
		queryObj.setParameter("globalAccountPocketId", globalAccountPocketId);		
		return queryObj.list();
	}
	
		public BigDecimal getActualCurrentBalanceForPocket(Pocket pocket){
			BigDecimal currentBalance = new BigDecimal(pocket.getCurrentbalance());
			if(currentBalance ==null){
				currentBalance = BigDecimal.ZERO;
			}
			Criteria crCriteria=getSession().createCriteria(MfsLedger.class).add(Restrictions.eq("pocketid", pocket.getId())).add(Restrictions.eq("ledgertype", "Cr.")).add(Restrictions.eq("ledgerstatus", "R"));
			crCriteria.setProjection(Projections.sum("amount"));
			BigDecimal crBalance = (BigDecimal) crCriteria.uniqueResult();
			if(crBalance == null){
				crBalance = BigDecimal.ZERO;
			}
			
			Criteria drCriteria=getSession().createCriteria(MfsLedger.class).add(Restrictions.eq("pocketid", pocket.getId())).add(Restrictions.eq("ledgertype", "Cr.")).add(Restrictions.eq("ledgerstatus", "R"));
			drCriteria.setProjection(Projections.sum("amount"));
			BigDecimal drBalance = (BigDecimal) drCriteria.uniqueResult();
			if(drBalance == null){
				drBalance = BigDecimal.ZERO;
			}
			
			return currentBalance.add(crBalance).subtract(drBalance);
		}
		
		public Pocket getPocketAfterEvicting(Pocket pocket) {			
			getSession().evict(pocket);
			return getById(pocket.getId().longValue());
		}
		
	/**
	 * Returns the Default bank pockets for the given MDN list
	 * @param mdnlist
	 * @return
	 */
	public List<Pocket> getDefaultBankPocketByMdnList(List<Long> mdnlist) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(Pocket.FieldName_IsDefault, Boolean.TRUE));
		criteria.createAlias(Pocket.FieldName_PocketTemplate, "pt");
		criteria.add(Restrictions.eq("pt."+ PocketTemplate.FieldName_PocketType, CmFinoFIX.PocketType_BankAccount));
		criteria.createAlias(Pocket.FieldName_SubscriberMDNByMDNID, "smdn");
		addCriteriaIn("smdn."+ SubscriberMdn.FieldName_RecordID, mdnlist, criteria);
        @SuppressWarnings("unchecked")
        List<Pocket> results = criteria.list();

        return results;
	}
	
	 private void addCriteriaIn (String propertyName, List<?> list,Criteria criteria)
	  {
	    Disjunction or = Restrictions.disjunction();
	    if(list.size()>1000)
	    {        
	      while(list.size()>1000)
	      {
	        List<?> subList = list.subList(0, 1000);
	        or.add(Restrictions.in(propertyName, subList));
	        list.subList(0, 1000).clear();
	      }
	    }
	    or.add(Restrictions.in(propertyName, list));
	    criteria.add(or);
	  }
	 
	 public List<Long> getLakuPandaiPockets() {
		 List<Long> result = null;
		 String sql= "select p.ID from Pocket p where PocketTemplateID in (select ID from PocketTemplate where Type = :pocketType)"; 
		 Query queryObj = getSession().createQuery(sql);
		 queryObj.setParameter("pocketType", CmFinoFIX.PocketType_LakuPandai);
		 List lst = queryObj.list();
		 if (CollectionUtils.isNotEmpty(lst)) {
			 result = new ArrayList<Long>(lst.size());
			 for(int i=0; i<lst.size(); i++) {
				 Long id = (Long)lst.get(i);
				 result.add(id);
    		 }
		 }
		 return result;
	}
}
