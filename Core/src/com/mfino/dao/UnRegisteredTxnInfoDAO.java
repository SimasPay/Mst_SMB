package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.fix.CmFinoFIX;

/**
 * 
 * @author Sasi
 *
 */
public class UnRegisteredTxnInfoDAO extends BaseDAO<UnRegisteredTxnInfo> {

    
    public List<UnRegisteredTxnInfo> get(UnRegisteredTxnInfoQuery query){
    	Criteria criteria = createCriteria();
    		
    		if(query.getSubscriberMDNID()!=null){
    			criteria.createAlias(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_SubscriberMDNByMDNID, "SMDN");
    			criteria.add(Restrictions.eq("SMDN." + CmFinoFIX.CRSubscriberMDN.FieldName_RecordID, query.getSubscriberMDNID()));
    		}
    		if(query.getTransferSctlId()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_TransferSCTLId, query.getTransferSctlId()));
    		}

    		if(query.getTransferCTId()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_TransferCTId, query.getTransferCTId()));
    		}
    		
    		if (query.getCashoutSCTLId() != null) {
    			criteria.add(Restrictions.eq(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_CashoutSCTLId, query.getCashoutSCTLId()));
    		}
    		
    		if(query.getStatus() != null){
   				criteria.add(Restrictions.eq(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_UnRegisteredTxnStatus, query.getStatus()));
    		}
    		
    		if (StringUtils.isNotBlank(query.getFundAccessCode())) {
    			criteria.add(Restrictions.eq(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_DigestedPIN, query.getFundAccessCode()));
    		}
    		
    		if (query.getAmount() != null) {
    			criteria.add(Restrictions.eq(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_Amount, query.getAmount()));
    		}
    		
    		if (query.getMultiStatus() != null) {
    			criteria.add(Restrictions.in(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_UnRegisteredTxnStatus, query.getMultiStatus()));
    		}
    		
    		if (query.getWithdrawalMDN() != null) {
    			criteria.add(Restrictions.eq(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_WithdrawalMDN,query.getWithdrawalMDN()));
    		}
    		if(query.getExpiryTime()!=null){
    			criteria.add(Restrictions.le(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_ExpiryTime, query.getExpiryTime()));
    		}
    		if(query.getFundDefinitionID() != null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_FundDefinitionID, query.getFundDefinitionID()));
    		}
//    		if(query.getMultiPartnerCode() != null){
//    			criteria.add(Restrictions.in(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_PartnerCode, query.getMultiPartnerCode()));
//    		}
    		if(query.getMultiPartnerCode() != null){
    			Disjunction finalDisjunction = Restrictions.disjunction();
    			for(int i=0;i<query.getMultiPartnerCode().length;i++){
    				SimpleExpression se = Restrictions.eq(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_PartnerCode, query.getMultiPartnerCode()[i]).ignoreCase();    
    				finalDisjunction.add(se);  
    			}
    			criteria.add(finalDisjunction);
    		}
    		if (StringUtils.isNotBlank(query.getTransactionName())) {
    			criteria.add(Restrictions.eq(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_TransactionName, query.getTransactionName()));
    		}
    		
    	  processBaseQuery(query, criteria);

          // Paging
          processPaging(query, criteria);

          if(query.isIDOrdered()) {
            criteria.addOrder(Order.desc(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_RecordID));
          }
          
          //applying Order
          applyOrder(query, criteria);
          @SuppressWarnings("unchecked")
          List<UnRegisteredTxnInfo> results = criteria.list();

          return results;
    	
    }
	 
}
