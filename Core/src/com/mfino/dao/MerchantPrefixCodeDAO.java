/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import com.mfino.dao.query.MerchantPrefixCodeQuery;
import com.mfino.domain.MerchantPrefixCode;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADMIN
 */
public class MerchantPrefixCodeDAO extends BaseDAO<MerchantPrefixCode>{

    public List<MerchantPrefixCode> get(MerchantPrefixCodeQuery query) {
        Criteria criteria = createCriteria();
//        if (query.getCompany() != null) {
//            criteria.add(Restrictions.eq(CmFinoFIX.CRMerchantPrefixCode.FieldName_Company, query.getCompany()));
//        }
        if (query.getMerchantPrefixCode() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRMerchantPrefixCode.FieldName_MerchantPrefixCode, query.getMerchantPrefixCode()));
        }
        if (query.getBillerName() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRMerchantPrefixCode.FieldName_BillerName, query.getBillerName()).ignoreCase());
        }

        processPaging(query, criteria);
        @SuppressWarnings("unchecked")

        List<MerchantPrefixCode> results = criteria.list();
        return results;
    }

}
