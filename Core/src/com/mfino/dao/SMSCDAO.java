/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SMSCQuery;
import com.mfino.domain.SmsPartner;
import com.mfino.domain.SmscConfiguration;

/**
 * @author Maruthi
 *
 */
public class SMSCDAO extends BaseDAO<SmscConfiguration> {

    public List<SmscConfiguration> get(SMSCQuery query) {
        Criteria criteria = createCriteria();
        if (query.getPartnerID() != null) {
            criteria.createCriteria("SMSPartnerByPartnerID").add(Restrictions.eq(SmsPartner.FieldName_RecordID, query.getPartnerID()));
        }
        if (query.getShortCode() != null) {
            criteria.add(Restrictions.eq(SmscConfiguration.FieldName_ShortCode, query.getShortCode()).ignoreCase());
        }
        if (query.getCharging() != null) {
            criteria.add(Restrictions.eq(SmscConfiguration.FieldName_Charging, query.getCharging()));
        }
        if (query.getSmartfrenSMSCID() != null) {
            criteria.add(Restrictions.eq(SmscConfiguration.FieldName_SmartfrenSMSCID, query.getSmartfrenSMSCID()).ignoreCase());
        }
        if (query.getOtherLocalOperatorSMSCID()!= null) {
            criteria.add(Restrictions.eq(SmscConfiguration.FieldName_OtherLocalOperatorSMSCID, query.getOtherLocalOperatorSMSCID()).ignoreCase());
        }
        
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        
        List<SmscConfiguration> results = criteria.list();
        return results;
    }

}
