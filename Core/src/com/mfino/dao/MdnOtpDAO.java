package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.MdnOtpQuery;
import com.mfino.domain.MdnOtp;
import com.mfino.fix.CmFinoFIX;

/**
 * 
 * @author Amar
 *
 */
public class MdnOtpDAO extends BaseDAO<MdnOtp>{

    public List<MdnOtp> get(MdnOtpQuery query){
        Criteria criteria = createCriteria();

        if(query.getMdn() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRMdnOtp.FieldName_MDN, query.getMdn().toString()));
        }
        
        if(query.getOtpStatus() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRMdnOtp.FieldName_OTPStatus, query.getOtpStatus()));
        }
        
        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        if(query.isIDOrdered()) {
          criteria.addOrder(Order.desc(CmFinoFIX.CRNotificationLog.FieldName_RecordID));
        }
        
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<MdnOtp> results = criteria.list();

        return results;
    }
    
    
    @SuppressWarnings("unchecked")
	public MdnOtp getByMDNAndId(String MDN,Long id) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(CmFinoFIX.CRMdnOtp.FieldName_MDN, MDN));
    	criteria.add(Restrictions.eq(CmFinoFIX.CRMdnOtp.FieldName_RecordID, id));
    	List<MdnOtp> mdnList = criteria.list();
    	if((null != mdnList) && (mdnList.size() > 0)){
    		return mdnList.get(0);
    	}
    	return null;
    }
}
