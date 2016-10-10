/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.MDNRangeQuery;
import com.mfino.domain.MdnRange;
import com.mfino.domain.Merchant;

/**
 *
 * @author ADMIN
 */
public class MDNRangeDAO extends BaseDAO<MdnRange>{


    public List<MdnRange> get(MDNRangeQuery query) {

        final String MERCHANT_ASSOC_NAME = "Merchant";
        Criteria criteria = createCriteria();
        
        if (query.getStartPrefix() != null) {
            criteria.add(Restrictions.like(MdnRange.FieldName_StartPrefix, query.getStartPrefix()).ignoreCase());
        }
        if (query.getEndPrefix() != null) {
            criteria.add(Restrictions.like(MdnRange.FieldName_EndPrefix, query.getEndPrefix()).ignoreCase());
        }
        if (query.getMerchantId() != null) {
            final String merchantAlias = MERCHANT_ASSOC_NAME + DAOConstants.ALIAS_SUFFIX;
            criteria.createAlias(MERCHANT_ASSOC_NAME, merchantAlias);
            final String merchantWithAlias = merchantAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + Merchant.FieldName_RecordID;
            criteria.add(Restrictions.eq(merchantWithAlias, query.getMerchantId()));
            processColumn(query, Merchant.FieldName_RecordID, merchantWithAlias);
        }
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<MdnRange> results = criteria.list();

        return results;
    }

    @Override
    public void save(MdnRange mr) {
        super.save(mr);
    }
}
