/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SMSPartnerQuery;
import com.mfino.domain.SmsPartner;

/**
 *
 * @author Srinu
 */
public class SMSPartnerDAO extends BaseDAO<SmsPartner>{

    public List<SmsPartner> get(SMSPartnerQuery query) {
        Criteria criteria = createCriteria();

        if (query.getPartnerName() != null) {
            criteria.add(Restrictions.eq(SmsPartner.FieldName_PartnerName, query.getPartnerName()).ignoreCase());
        }
       if (query.getStartDate() != null) {
            criteria.add(Restrictions.gt(SmsPartner.FieldName_CreateTime, query.getStartDate()));
        }
        if (query.getEndDate() != null) {
            criteria.add(Restrictions.lt(SmsPartner.FieldName_CreateTime, query.getEndDate()));
        }

        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<SmsPartner> results = criteria.list();

        return results;
    }

}
