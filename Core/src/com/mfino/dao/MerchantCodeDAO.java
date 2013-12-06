/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import com.mfino.dao.query.MerchantCodeQuery;
import com.mfino.domain.MerchantCode;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADMIN
 */
public class MerchantCodeDAO extends BaseDAO<MerchantCode>{

public List<MerchantCode> get(MerchantCodeQuery query) {
        Criteria criteria = createCriteria();
        if (query.getCompany() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRMerchantCode.FieldName_Company, query.getCompany()));
        }
        if (query.getMerchantCode() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRMerchantCode.FieldName_MerchantCode, query.getMerchantCode()).ignoreCase());
        }
        if (query.getMdn() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRMerchantCode.FieldName_MDN, query.getMdn()));
        }
        
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        
        List<MerchantCode> results = criteria.list();
        return results;
    }
}
