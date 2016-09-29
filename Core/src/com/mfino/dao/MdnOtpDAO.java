package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.MdnOtpQuery;
import com.mfino.domain.MdnOtp;
import com.mfino.domain.NotificationLog;

/**
 * 
 * @author Amar
 *
 */
public class MdnOtpDAO extends BaseDAO<MdnOtp>{

    public List<MdnOtp> get(MdnOtpQuery query){
        Criteria criteria = createCriteria();

        if(query.getMdn() != null) {
            criteria.add(Restrictions.eq(MdnOtp.FieldName_MDN, query.getMdn().toString()));
        }
        
        if(query.getOtpStatus() != null) {
            criteria.add(Restrictions.eq(MdnOtp.FieldName_OTPStatus, query.getOtpStatus()));
        }
        
        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        if(query.isIDOrdered()) {
          criteria.addOrder(Order.desc(NotificationLog.FieldName_RecordID));
        }
        
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<MdnOtp> results = criteria.list();

        return results;
    }
    
    @SuppressWarnings("unchecked")
	public List<MdnOtp> getByMdn(String Mdn) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(MdnOtp.FieldName_MDN, Mdn));
    	criteria.addOrder(Order.desc(MdnOtp.FieldName_RecordID));
    	List<MdnOtp> mdnList = criteria.list();
   		return mdnList;
    }
    
    
    @SuppressWarnings("unchecked")
	public MdnOtp getByMDNAndId(String MDN,Long id) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(MdnOtp.FieldName_MDN, MDN));
    	criteria.add(Restrictions.eq(MdnOtp.FieldName_RecordID, id));
    	List<MdnOtp> mdnList = criteria.list();
    	if((null != mdnList) && (mdnList.size() > 0)){
    		return mdnList.get(0);
    	}
    	return null;
    }
}
