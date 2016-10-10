/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SMSCodeQuery;
import com.mfino.domain.SmsCode;

/**
 * @author Deva
 *
 */
public class SMSCodeDAO extends BaseDAO<SmsCode> {

    public List<SmsCode> get(SMSCodeQuery query) {
        Criteria criteria = createCriteria();
        if (query.getSmsCode() != null) {
            criteria.add(Restrictions.eq(SmsCode.FieldName_SMSCodeText, query.getSmsCode()).ignoreCase());
        }
        if (query.getSmsStatus() != null) {
            criteria.add(Restrictions.eq(SmsCode.FieldName_SMSCodeStatus, query.getSmsStatus()));
        }
        
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        
        List<SmsCode> results = criteria.list();
        return results;
    }

	/**
	 * @param smsCode
	 * @return
	 */
	public SmsCode getByCode(String smsCode) {
		SMSCodeQuery smsCodeQuery = new SMSCodeQuery();
		smsCodeQuery.setSmsCode(smsCode);
		List<SmsCode> smsCodeList = this.get(smsCodeQuery);
		if (smsCodeList.size() > 0) {
			return smsCodeList.get(0);
		}
		return null;
	}
}
