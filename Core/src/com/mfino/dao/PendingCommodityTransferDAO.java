/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.Company;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;

/**
 * 
 * @author sunil
 */
public class PendingCommodityTransferDAO extends BaseDAO<PendingCommodityTransfer> {

    private static final String PocketBySourcePocketID = "PocketBySourcePocketID";
    private static final String SubscriberMDNBySourceMDNID = "SubscriberMDNBySourceMDNID";

    public PendingCommodityTransferDAO() {
        super();
    }

    public void applyQuery(CommodityTransferQuery query, Criteria criteria) throws Exception {

        // Adding Restrictions
        // TODO: this is very bad and confusing
        if (query.isOperatorActionRequired() && query.isBankReversalRequired()) {
            criteria.add(Restrictions.disjunction().add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_OperatorActionRequired, query.isOperatorActionRequired())).add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_BankReversalRequired, query.isBankReversalRequired())));

        } else if (query.isOperatorActionRequired()) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_OperatorActionRequired, query.isOperatorActionRequired()));
        } else if (query.isBankReversalRequired()) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_BankReversalRequired, query.isBankReversalRequired()));
        }
        if (query.getTransferStatus() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_TransferStatus, query.getTransferStatus()));
        }
        if (query.getStartDate() != null) {
            criteria.add(Restrictions.gt(CmFinoFIX.CRPendingCommodityTransfer.FieldName_StartTime, query.getStartDate()));
        }
        if (query.getEndDate() != null) {
            criteria.add(Restrictions.lt(CmFinoFIX.CRPendingCommodityTransfer.FieldName_EndTime, query.getEndDate()));
        }
                
        // Added the Starttime >= starttime and starttime < starttime to criteria.
        if (query.getStartTimeGE() != null) {
          criteria.add(Restrictions.ge(CmFinoFIX.CRPendingCommodityTransfer.FieldName_StartTime, query.getStartTimeGE()));
        }
        if (query.getStartTimeLT() != null) {
          criteria.add(Restrictions.lt(CmFinoFIX.CRPendingCommodityTransfer.FieldName_StartTime, query.getStartTimeLT()));
        }
        
        if (query.getTransactionAmount() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_TransferAmount, query.getTransactionAmount()));
        }
        if (query.getTransactionID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_TransactionID, query.getTransactionID()));
        }
        if (query.getSubscriberMDN() != null) {
            criteria.add(Restrictions.disjunction().add(
            		Restrictions.eq(CmFinoFIX.CRCommodityTransfer.FieldName_SourceMDN, query.getMDN()))
            		.add(Restrictions.eq(CmFinoFIX.CRCommodityTransfer.FieldName_DestMDN, query.getMDN()))
            		.add(Restrictions.eq(SubscriberMDNBySourceMDNID , query.getSubscriberMDN()))
            		.add(Restrictions.eq(CmFinoFIX.CRCommodityTransfer.FieldName_DestMDNID, query.getSubscriberMDN().getID())));
        }
        if (query.getMsgType() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_TransactionUICategory, query.getMsgType()));
        }

       
        if (null != query.hasCSRAction()) {
          if(query.hasCSRAction()) {
            criteria.add(Restrictions.isNotNull(CmFinoFIX.CRCommodityTransfer.FieldName_CSRAction));
          } else {
            criteria.add(Restrictions.isNull(CmFinoFIX.CRCommodityTransfer.FieldName_CSRAction));
          }
        }
        if (query.getBulkUploadLineNumber() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRCommodityTransfer.FieldName_BulkUploadLineNumber, query.getBulkUploadLineNumber()));
        }
        if (query.getCompany() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_Company, query.getCompany()));
        }
        criteria.createAlias(SubscriberMDNBySourceMDNID, SubscriberMDNBySourceMDNID + DAOConstants.ALIAS_SUFFIX);

        if (query.getSourceDestnMDN() != null) {
            criteria.add(Restrictions.disjunction().add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_SourceMDN, query.getSourceDestnMDN())).add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_DestMDN, query.getSourceDestnMDN())));
        }
        if (query.getSourceDestMDNAndID() != null) {
            boolean isBadRequest = false;
            if (!(query.getSourceDestMDNAndID() instanceof Object[])) {
                isBadRequest = true;
            }
            Object[] mdnAndID = (Object[]) query.getSourceDestMDNAndID();
            if (mdnAndID.length != 2 || !(mdnAndID[0] instanceof String) || !(mdnAndID[1] instanceof Long)) {
                isBadRequest = true;
            }
            if (isBadRequest) {
                throw new Exception("Bad query parameter for CommodityTransferDAO - SourceDestMDNAndID");
            }

            criteria.add(Restrictions.disjunction().add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_DestMDN, mdnAndID[0])).add(Restrictions.eq(SubscriberMDNBySourceMDNID + DAOConstants.ALIAS_SUFFIX + DAOConstants.ALIAS_COLNAME_SEPARATOR + "ID", mdnAndID[1])).add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_DestMDNID, mdnAndID[1])));
        }
        if (query.getSourceMDN() != null) {
            if (!query.getSourceMDN().equals("")) {
                addLikeStartRestriction(criteria, CmFinoFIX.CRPendingCommodityTransfer.FieldName_SourceMDN, query.getSourceMDN());
            }
        }
        if (query.getDestinationMDN() != null) {
            if (!query.getDestinationMDN().equals("")) {
                addLikeStartRestriction(criteria, CmFinoFIX.CRPendingCommodityTransfer.FieldName_DestMDN, query.getDestinationMDN());
            }
        }
        if (query.getSourceReferenceID() != null) {
            if (!query.getSourceReferenceID().equals("")) {
                criteria.add(Restrictions.disjunction().add(Restrictions.ilike(CmFinoFIX.CRPendingCommodityTransfer.FieldName_SourceReferenceID, query.getSourceReferenceID())).add(Restrictions.ilike(CmFinoFIX.CRPendingCommodityTransfer.FieldName_ISO8583_SystemTraceAuditNumber, query.getSourceReferenceID())));
            }
        }

        if (query.getDestinationRefID() != null) {
            if (!query.getDestinationRefID().equals("")) {
                criteria.add(Restrictions.disjunction().add(Restrictions.ilike(CmFinoFIX.CRPendingCommodityTransfer.FieldName_OperatorAuthorizationCode, query.getDestinationRefID())).add(Restrictions.ilike(CmFinoFIX.CRPendingCommodityTransfer.FieldName_BankAuthorizationCode, query.getDestinationRefID())).add(Restrictions.ilike(CmFinoFIX.CRPendingCommodityTransfer.FieldName_BankReversalAuthorizationCode, query.getDestinationRefID())));
            }
        }
        if (query.getSourcePocket() != null) {
//            criteria.createAlias(PocketBySourcePocketID, PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX);
            criteria.add(Restrictions.eq(PocketBySourcePocketID , query.getSourcePocket()));
        }
        if (query.getDestinationPocketID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_DestPocketID, query.getDestinationPocketID()));
        }
        // Mapping Source application(Access Method) to Source Application.
        if (query.getSourceApplicationSearch() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_SourceApplication, query.getSourceApplicationSearch()));
        }
//        if (query.getSourceApplicationSearch() != null && 0 != query.getSourceApplicationSearch()) {
//            List<Integer> property = new ArrayList<Integer>();
//
//            if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_Web) == CmFinoFIX.SourceApplicationSearch_Web) {
//                property.add(CmFinoFIX.SourceApplication_Web);
//            }
//            if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_BackEnd) == CmFinoFIX.SourceApplicationSearch_BackEnd) {
//                property.add(CmFinoFIX.SourceApplication_BackEnd);
//            }
//            if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_Phone) == CmFinoFIX.SourceApplicationSearch_Phone) {
//                property.add(CmFinoFIX.SourceApplication_Phone);
//            }
//            if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_WebService) == CmFinoFIX.SourceApplicationSearch_WebService) {
//                property.add(CmFinoFIX.SourceApplication_WebService);
//            }
//            if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_SMS) == CmFinoFIX.SourceApplicationSearch_SMS) {
//                property.add(CmFinoFIX.SourceApplication_SMS);
//            }
//            if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_WebAPI) == CmFinoFIX.SourceApplicationSearch_WebAPI) {
//
//                property.add(CmFinoFIX.SourceApplication_WebAPI);
//            }
//            criteria.add(Restrictions.in(CmFinoFIX.CRPendingCommodityTransfer.FieldName_SourceApplication, property));
//        }

        if (query.getSourceDestnPocket() != null) {
//            criteria.createAlias(PocketBySourcePocketID, PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX);
            criteria.add(Restrictions.disjunction().add(Restrictions.eq(PocketBySourcePocketID , query.getSourceDestnPocket())).add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_DestPocketID, query.getSourceDestnPocket().getID())));
        }
        if (query.getBulkuploadID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_BulkUploadID, query.getBulkuploadID()));
        }

        if (query.isIsBankChannel() != null && query.isIsBankChannel()) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_SourceApplication, CmFinoFIX.SourceApplication_BankChannel));
        }

        if (null != query.getExactBankCode()) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_ISO8583_AcquiringInstIdCode, query.getExactBankCode()));
        }
        if (null != query.getBankRoutingCode()) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_BankCodeForRouting, query.getBankRoutingCode()));
        }
        if (query.getCreateTimeSearchLE() != null) {
            criteria.add(Restrictions.le(CmFinoFIX.CRPendingCommodityTransfer.FieldName_CreateTime, query.getCreateTimeSearchLE()));
            criteria.add(Restrictions.gt(CmFinoFIX.CRPendingCommodityTransfer.FieldName_CreateTime, query.getCreateTimeSearchGT()));
        }

        // Add the Only Bank Criteria
        if (query.isOnlyBankTxns()) {
            Set<Integer> collection = new HashSet<Integer>();

            collection.add(CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf);
            collection.add(CmFinoFIX.TransactionUICategory_Dompet_Money_Transfer);
            collection.add(CmFinoFIX.TransactionUICategory_Dompet_Self_Topup);
            collection.add(CmFinoFIX.TransactionUICategory_Dompet_Topup_Another);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_Purchase);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_CashIn);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_CashOut);

            criteria.add(Restrictions.in(CmFinoFIX.CRCommodityTransfer.FieldName_TransactionUICategory, collection));
        }

        // Add the Only E-Money Criteria
        if (query.isOnlyEmoneyTxns()) {
            Set<Integer> collection = new HashSet<Integer>();
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_Purchase);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_CashIn);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_CashOut);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_EMoney_Trf);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf);
            collection.add(CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_Self_Topup);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_Topup_Another);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_Empty_SVA);

            criteria.add(Restrictions.in(CmFinoFIX.CRCommodityTransfer.FieldName_TransactionUICategory, collection));
        }
        
        if(StringUtils.isNotBlank(query.getBankRRN())){
       	 criteria.add(Restrictions.eq(CmFinoFIX.CRCommodityTransfer.FieldName_BankRetrievalReferenceNumber, query.getBankRRN()).ignoreCase());
       }
        
       if(query.getUiCategory() != null){
    	   criteria.add(Restrictions.eq(CmFinoFIX.CRCommodityTransfer.FieldName_TransactionUICategory, query.getUiCategory()));
       }
    }

    public List<PendingCommodityTransfer> get(CommodityTransferQuery query) throws Exception {

        Criteria criteria = createCriteria();

        applyQuery(query, criteria);

        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);

        // applying Order
        criteria.addOrder(Order.desc(CmFinoFIX.CRPendingCommodityTransfer.FieldName_RecordID));
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<PendingCommodityTransfer> results = criteria.list();

        return results;
    }

    public List<Object[]> groupBy(CommodityTransferQuery query) throws Exception {
        Criteria criteria = createCriteria();
        applyQuery(query, criteria);

        ProjectionList projList = Projections.projectionList();

        if (query.getSubTotalBy() == CmFinoFIX.SubtotalBy_Access_Method) {
            projList.add(Projections.groupProperty(CmFinoFIX.CRPendingCommodityTransfer.FieldName_SourceApplication));
        } else if (query.getSubTotalBy() == CmFinoFIX.SubtotalBy_Commodity_Type) {
            projList.add(Projections.groupProperty(CmFinoFIX.CRPendingCommodityTransfer.FieldName_Commodity));
        } else if (query.getSubTotalBy() == CmFinoFIX.SubtotalBy_Transaction_Status) {
            projList.add(Projections.groupProperty(CmFinoFIX.CRPendingCommodityTransfer.FieldName_TransferStatus));
        } else if (query.getSubTotalBy() == CmFinoFIX.SubtotalBy_Transaction_Type) {
            projList.add(Projections.groupProperty(CmFinoFIX.CRPendingCommodityTransfer.FieldName_TransactionUICategory));
        } else if (query.getSubTotalBy() == CmFinoFIX.SubtotalBy_Buy_Sell) {
            if (query.getSourcePocket() != null) {
                projList.add(Projections.groupProperty(PocketBySourcePocketID ));
            } else {
                projList.add(Projections.groupProperty(CmFinoFIX.CRPendingCommodityTransfer.FieldName_DestPocketID));
            }
        }
        projList.add(Projections.sum(CmFinoFIX.CRPendingCommodityTransfer.FieldName_TransferAmount));

        criteria.setProjection(projList);
        @SuppressWarnings("unchecked")
        List<Object[]> results = criteria.list();

        return results;
    }

    public List<Integer> getAllDistinctBankCodes(Date startGE, Date startLT) {
        String sqlQuery = "SELECT distinct " + CmFinoFIX.CRPendingCommodityTransfer.FieldName_ISO8583_AcquiringInstIdCode + " FROM PendingCommodityTransfer  where startTime >= :startGE and startTime < :startLT";

        Query queryObj = getQuery(sqlQuery);
        queryObj.setTimestamp("startGE", startGE);
        queryObj.setTimestamp("startLT", startLT);
        @SuppressWarnings("unchecked")
        List<Integer> results = (List<Integer>) queryObj.list();
        return results;
    }

    public PendingCommodityTransfer getLastTransferBefore(Pocket pocket, Date end) {
        // The best indicator would be the source pocket balance captured in the
        // immediate next transfer after this time.
        // But we might not have any, so make do with the immediate transfer
        // preceding this time.
        Criteria criteria = createCriteria();
        // criteria.add(Restrictions.eq(CmFinoFIX.CRCommodityTransfer.FieldName_PocketBySourcePocketID,
        // pocket));
        // Adding an OR condition for the SourcepocketId or DestPocketID.
        criteria.createAlias(PocketBySourcePocketID, PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX);
        Criterion sourcePocketIDCriterion = Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX + DAOConstants.ALIAS_COLNAME_SEPARATOR + "ID", pocket.getID());
        Criterion destPocketIDCriterion = Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_DestPocketID, pocket.getID());
        LogicalExpression orExp = Restrictions.or(sourcePocketIDCriterion, destPocketIDCriterion);
        criteria.add(orExp);
        criteria.add(Restrictions.le(CmFinoFIX.CRPendingCommodityTransfer.FieldName_StartTime, end));
        criteria.addOrder(Order.desc(CmFinoFIX.CRPendingCommodityTransfer.FieldName_RecordID));

        criteria.setMaxResults(1);
        @SuppressWarnings("unchecked")
        List<PendingCommodityTransfer> results = criteria.list();

        if (null == results || 0 == results.size()) {
            return null;
        }

        PendingCommodityTransfer pct = results.get(0);
        return pct;
    }
    
    public PendingCommodityTransfer getFirstTransferAfter(Pocket pocket, Date end) {
        Criteria criteria = createCriteria();
        criteria.createAlias(PocketBySourcePocketID, PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX);
        Criterion sourcePocketIDCriterion = Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX + DAOConstants.ALIAS_COLNAME_SEPARATOR + "ID", pocket.getID());
        Criterion destPocketIDCriterion = Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_DestPocketID, pocket.getID());
        LogicalExpression orExp = Restrictions.or(sourcePocketIDCriterion, destPocketIDCriterion);
        criteria.add(orExp);
        criteria.add(Restrictions.ge(CmFinoFIX.CRPendingCommodityTransfer.FieldName_StartTime, end));
        criteria.addOrder(Order.asc(CmFinoFIX.CRPendingCommodityTransfer.FieldName_RecordID));
        criteria.setMaxResults(1);

        @SuppressWarnings("unchecked")
        List<PendingCommodityTransfer> results = criteria.list();
        if (null == results || 0 == results.size()) {
            return null;
        }

        return results.get(0);       
    }
    
    public List<PendingCommodityTransfer> getAllAirtimeTrfsOrderedByStartBefore(Date end){
      Criteria criteria = createCriteria();
      Integer[] uiCategories = new Integer[] { 
          CmFinoFIX.TransactionUICategory_BulkTopup,
          CmFinoFIX.TransactionUICategory_BulkTransfer,
          CmFinoFIX.TransactionUICategory_MA_Topup,
          CmFinoFIX.TransactionUICategory_MA_Transfer,
          CmFinoFIX.TransactionUICategory_Empty_SVA,
          CmFinoFIX.TransactionUICategory_Distribute_LOP
      };
      
      criteria.add(Restrictions.le(CmFinoFIX.CRPendingCommodityTransfer.FieldName_StartTime, end));
      criteria.addOrder(Order.desc(CmFinoFIX.CRPendingCommodityTransfer.FieldName_StartTime));
      criteria.add(Restrictions.in(CmFinoFIX.CRPendingCommodityTransfer.FieldName_TransactionUICategory,uiCategories));
       @SuppressWarnings("unchecked")
      List<PendingCommodityTransfer> results = criteria.list();
      return results;
     }
    /**
     * 
     * @param startDate
     * @param endDate
     * @param company
     * @return List 
     * all the pending transactions based on the inputs startdate, enddate and company.  
     */
    public List<PendingCommodityTransfer> getAllPendingCCTransactions(Date startDate,Date endDate,Company company){
        Criteria criteria = createCriteria();
        List<Integer> collection = new ArrayList<Integer>();
        collection.add(CmFinoFIX.TransactionUICategory_CC_Payment);
        collection.add(CmFinoFIX.TransactionUICategory_CC_Topup);
        criteria.add(Restrictions.in(CmFinoFIX.CRPendingCommodityTransfer.FieldName_TransactionUICategory, collection));
        if(startDate!=null){
        criteria.add(Restrictions.ge(CmFinoFIX.CRPendingCommodityTransfer.FieldName_CreateTime, startDate));
        }
        if(endDate!=null){
        criteria.add(Restrictions.lt(CmFinoFIX.CRPendingCommodityTransfer.FieldName_CreateTime, endDate));
        }
        if(company!=null){
        	criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_Company, company));
        }
        @SuppressWarnings("unchecked")
        List<PendingCommodityTransfer> results = criteria.list();
        return results;
        }
    public List getAllPendingTransactionCount(CommodityTransferQuery query){
		Criteria criteria = createCriteria();
		if (query.getStartTimeGE() != null) {
			criteria.add(Restrictions.ge(CmFinoFIX.CRPendingCommodityTransfer.FieldName_StartTime,query.getStartTimeGE()));
		}
		if (query.getStartTimeLT() != null) {
			criteria.add(Restrictions.lt(CmFinoFIX.CRPendingCommodityTransfer.FieldName_StartTime,query.getStartTimeLT()));
		}
		if (query.isOperatorActionRequired()) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_OperatorActionRequired, query.isOperatorActionRequired()));
        }
		criteria.setProjection(Projections.rowCount());
		List results = criteria.list();
		return results;
    }
    
    @Override
    public void save(PendingCommodityTransfer pct) {    	
         	if(StringUtils.isBlank(pct.getCreatedBy())){
         		 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                 String userName = (auth != null) ? auth.getName() : "System";
                 pct.setCreatedBy(userName);
         	}
         	if(pct.getCreateTime()==null){
         		pct.setCreateTime(new Timestamp());
         	}
         super.save(pct);
    }

    public int getCountOfPendingPCT() {
		String countQuery = "select count(*) from PendingCommodityTransfer where TransferStatus = 21";
		int countOfPct = getSession().createQuery(countQuery).executeUpdate();
		getSession().flush();
		getSession().clear();
		return countOfPct;
    }
	
	public List<PendingCommodityTransfer> getAll21NonPendingTransfers() {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRPendingCommodityTransfer.FieldName_TransferStatus,CmFinoFIX.TransferStatus_Pending));
		@SuppressWarnings("unchecked")
		List<PendingCommodityTransfer> results = criteria.list();
		return results;
	}
}
