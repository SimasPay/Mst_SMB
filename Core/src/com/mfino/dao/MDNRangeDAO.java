/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import com.mfino.constants.DAOConstants;
import com.mfino.domain.MDNRange;
import com.mfino.dao.query.MDNRangeQuery;
import com.mfino.fix.CmFinoFIX;
import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADMIN
 */
public class MDNRangeDAO extends BaseDAO<MDNRange>{


    public List<MDNRange> get(MDNRangeQuery query) {

        final String MERCHANT_ASSOC_NAME = "Merchant";
        Criteria criteria = createCriteria();
        
        if (query.getStartPrefix() != null) {
            criteria.add(Restrictions.like(CmFinoFIX.CRMDNRange.FieldName_StartPrefix, query.getStartPrefix()).ignoreCase());
        }
        if (query.getEndPrefix() != null) {
            criteria.add(Restrictions.like(CmFinoFIX.CRMDNRange.FieldName_EndPrefix, query.getEndPrefix()).ignoreCase());
        }
        if (query.getMerchantId() != null) {
            final String merchantAlias = MERCHANT_ASSOC_NAME + DAOConstants.ALIAS_SUFFIX;
            criteria.createAlias(MERCHANT_ASSOC_NAME, merchantAlias);
            final String merchantWithAlias = merchantAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRMerchant.FieldName_RecordID;
            criteria.add(Restrictions.eq(merchantWithAlias, query.getMerchantId()));
            processColumn(query, CmFinoFIX.CRMerchant.FieldName_RecordID, merchantWithAlias);
        }
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<MDNRange> results = criteria.list();

        return results;
    }

    @Override
    public void save(MDNRange mr) {
        super.save(mr);
    }
}
