package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.FundDefinition;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.UnregisteredTxnInfo;

/**
 * 
 * @author Sasi
 *
 */
public class UnRegisteredTxnInfoDAO extends BaseDAO<UnregisteredTxnInfo> {

    
    public List<UnregisteredTxnInfo> get(UnRegisteredTxnInfoQuery query){
    	Criteria criteria = createCriteria();
    		
    		if(query.getSubscriberMDNID()!=null){
    			criteria.createAlias(UnregisteredTxnInfo.FieldName_SubscriberMDNByMDNID, "SMDN");
    			criteria.add(Restrictions.eq("SMDN." + SubscriberMdn.FieldName_RecordID, query.getSubscriberMDNID()));
    		}
    		if(query.getTransferSctlId()!=null){
    			criteria.add(Restrictions.eq(UnregisteredTxnInfo.FieldName_TransferSCTLId, query.getTransferSctlId()));
    		}

    		if(query.getTransferCTId()!=null){
    			criteria.add(Restrictions.eq(UnregisteredTxnInfo.FieldName_TransferCTId, query.getTransferCTId()));
    		}
    		
    		if (query.getCashoutSCTLId() != null) {
    			criteria.add(Restrictions.eq(UnregisteredTxnInfo.FieldName_CashoutSCTLId, query.getCashoutSCTLId()));
    		}
    		
    		if(query.getStatus() != null){
   				criteria.add(Restrictions.eq(UnregisteredTxnInfo.FieldName_UnRegisteredTxnStatus, query.getStatus()));
    		}
    		
    		if (StringUtils.isNotBlank(query.getFundAccessCode())) {
    			criteria.add(Restrictions.eq(UnregisteredTxnInfo.FieldName_DigestedPIN, query.getFundAccessCode()));
    		}
    		
    		if (query.getAmount() != null) {
    			criteria.add(Restrictions.eq(UnregisteredTxnInfo.FieldName_Amount, query.getAmount()));
    		}
    		
    		if (query.getMultiStatus() != null) {
    			criteria.add(Restrictions.in(UnregisteredTxnInfo.FieldName_UnRegisteredTxnStatus, query.getMultiStatus()));
    		}
    		
    		if (query.getWithdrawalMDN() != null) {
    			criteria.add(Restrictions.eq(UnregisteredTxnInfo.FieldName_WithdrawalMDN,query.getWithdrawalMDN()));
    		}
    		if(query.getExpiryTime()!=null){
    			criteria.add(Restrictions.le(UnregisteredTxnInfo.FieldName_ExpiryTime, query.getExpiryTime()));
    		}
    		if(query.getFundDefinitionID() != null){
    			criteria.createAlias(UnregisteredTxnInfo.FieldName_FundDefinition, "fundDefinition");
    			criteria.add(Restrictions.eq("fundDefinition."+FundDefinition.FieldName_RecordID, query.getFundDefinitionID()));
    		}
//    		if(query.getMultiPartnerCode() != null){
//    			criteria.add(Restrictions.in(UnregisteredTxnInfo.FieldName_PartnerCode, query.getMultiPartnerCode()));
//    		}
    		if(query.getMultiPartnerCode() != null){
    			Disjunction finalDisjunction = Restrictions.disjunction();
    			for(int i=0;i<query.getMultiPartnerCode().length;i++){
    				SimpleExpression se = Restrictions.eq(UnregisteredTxnInfo.FieldName_PartnerCode, query.getMultiPartnerCode()[i]).ignoreCase();    
    				finalDisjunction.add(se);  
    			}
    			criteria.add(finalDisjunction);
    		}
    		if (StringUtils.isNotBlank(query.getTransactionName())) {
    			criteria.add(Restrictions.eq(UnregisteredTxnInfo.FieldName_TransactionName, query.getTransactionName()));
    		}
    		
    	  processBaseQuery(query, criteria);

          // Paging
          processPaging(query, criteria);

          if(query.isIDOrdered()) {
            criteria.addOrder(Order.desc(UnregisteredTxnInfo.FieldName_RecordID));
          }
          
          //applying Order
          applyOrder(query, criteria);
          @SuppressWarnings("unchecked")
          List<UnregisteredTxnInfo> results = criteria.list();

          return results;
    	
    }
	 
}
