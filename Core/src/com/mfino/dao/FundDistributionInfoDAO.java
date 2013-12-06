package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.FundDistributionInfoQuery;
import com.mfino.domain.FundDistributionInfo;
import com.mfino.fix.CmFinoFIX;

public class FundDistributionInfoDAO extends BaseDAO<FundDistributionInfo> {
	
	public List<FundDistributionInfo> get(FundDistributionInfoQuery query){
    	Criteria criteria = createCriteria();
    		
    		if(query.getTransferSCTLId()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRFundDistributionInfo.FieldName_TransferSCTLId, query.getTransferSCTLId()));
    		}

    		if(query.getTransferCTId()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRFundDistributionInfo.FieldName_TransferCTId, query.getTransferCTId()));
    		}
    		if(query.getDistributionStatus()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRFundDistributionInfo.FieldName_DistributionStatus, query.getDistributionStatus()));
    		}
    		if(query.getDistributionType()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRFundDistributionInfo.FieldName_DistributionType, query.getDistributionType()));
    		}
    		
	
    	  processBaseQuery(query, criteria);

          // Paging
          processPaging(query, criteria);

          if(query.isIDOrdered()) {
            criteria.addOrder(Order.desc(CmFinoFIX.CRFundDistributionInfo.FieldName_RecordID));
          }
          
          //applying Order
          applyOrder(query, criteria);
          @SuppressWarnings("unchecked")
          List<FundDistributionInfo> results = criteria.list();

          return results;
    	
    }
}
