/**
 * 
 */
package com.mfino.dao;

import com.mfino.dao.query.SMSCodeQuery;
import com.mfino.domain.SMSCode;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * @author Deva
 *
 */
public class SMSCodeDAO extends BaseDAO<SMSCode> {

    public List<SMSCode> get(SMSCodeQuery query) {
        Criteria criteria = createCriteria();
        if (query.getSmsCode() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSMSCode.FieldName_SMSCodeText, query.getSmsCode()).ignoreCase());
        }
        if (query.getSmsStatus() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSMSCode.FieldName_SMSCodeStatus, query.getSmsStatus()));
        }
        
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        
        List<SMSCode> results = criteria.list();
        return results;
    }

	/**
	 * @param smsCode
	 * @return
	 */
	public SMSCode getByCode(String smsCode) {
		SMSCodeQuery smsCodeQuery = new SMSCodeQuery();
		smsCodeQuery.setSmsCode(smsCode);
		List<SMSCode> smsCodeList = this.get(smsCodeQuery);
		if (smsCodeList.size() > 0) {
			return smsCodeList.get(0);
		}
		return null;
	}
}
