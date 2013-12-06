/**
 * 
 */
package com.mfino.dao;

import com.mfino.dao.query.SMSCQuery;
import com.mfino.domain.SMSC;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * @author Maruthi
 *
 */
public class SMSCDAO extends BaseDAO<SMSC> {

    public List<SMSC> get(SMSCQuery query) {
        Criteria criteria = createCriteria();
        if (query.getPartnerID() != null) {
            criteria.createCriteria("SMSPartnerByPartnerID").add(Restrictions.eq(CmFinoFIX.CRSMSPartner.FieldName_RecordID, query.getPartnerID()));
        }
        if (query.getShortCode() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSMSC.FieldName_ShortCode, query.getShortCode()).ignoreCase());
        }
        if (query.getCharging() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSMSC.FieldName_Charging, query.getCharging()));
        }
        if (query.getSmartfrenSMSCID() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSMSC.FieldName_SmartfrenSMSCID, query.getSmartfrenSMSCID()).ignoreCase());
        }
        if (query.getOtherLocalOperatorSMSCID()!= null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSMSC.FieldName_OtherLocalOperatorSMSCID, query.getOtherLocalOperatorSMSCID()).ignoreCase());
        }
        
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        
        List<SMSC> results = criteria.list();
        return results;
    }

}
