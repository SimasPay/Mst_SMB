/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.constants.DAOConstants;
import com.mfino.constants.GeneralConstants;
import com.mfino.dao.query.LOPQuery;
import com.mfino.domain.LOP;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author srinu
 */
public class LOPDAO extends BaseDAO<LOP> {

    public LOPDAO() {
        super();
    }

    public List<LOP> get(LOPQuery query) {

        final String MERCHANT_ASSOC_NAME = "MerchantBySubscriberID";
        final String USER_ASSOC_NAME = "User";
        final String SUBSCRIBERMDNBYSOURCEMDNID = "SubscriberMDNByMDNID";
        final String DCTID = "DistributionChainTemplateByDCTID";

        Criteria criteria = createCriteria();

        if (query.getDistributornameLike() != null || query.getUserName() != null || query.getCompany() != null) {

            final String merchantAlias = MERCHANT_ASSOC_NAME + DAOConstants.ALIAS_SUFFIX;
            criteria = criteria.createAlias(MERCHANT_ASSOC_NAME, merchantAlias);

            if (query.getDistributornameLike() != null) {
                final String merchantWithAlias = merchantAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRMerchant.FieldName_TradeName;
                addLikeStartRestriction(criteria, merchantWithAlias, query.getDistributornameLike());
                processColumn(query, CmFinoFIX.CRMerchant.FieldName_TradeName, merchantWithAlias);
            }

            final String subscriberAlias = "Subscriber" + DAOConstants.ALIAS_SUFFIX;
            criteria = criteria.createAlias(merchantAlias + GeneralConstants.DOT_STRING + "Subscriber", subscriberAlias);
            if (query.getCompany() != null) {
                criteria.add(Restrictions.eq(CmFinoFIX.CRLOP.FieldName_Company, query.getCompany()));
            }
            if (query.getUserName() != null) {
                final String userAlias = USER_ASSOC_NAME + DAOConstants.ALIAS_SUFFIX;
                criteria = criteria.createAlias(subscriberAlias + GeneralConstants.DOT_STRING + USER_ASSOC_NAME, userAlias);
                final String userWithAlias = userAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRUser.FieldName_Username;

                addLikeStartRestriction(criteria, userWithAlias, query.getUserName());
                processColumn(query, CmFinoFIX.CRUser.FieldName_Username, userWithAlias);
            }
        }
        if (query.getMdnid() != null) {
            final String mdnAlias = SUBSCRIBERMDNBYSOURCEMDNID + DAOConstants.ALIAS_SUFFIX;
            criteria.createAlias(SUBSCRIBERMDNBYSOURCEMDNID, mdnAlias);
            final String mdnWithAlias = mdnAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRSubscriberMDN.FieldName_RecordID;
            criteria.add(Restrictions.eq(mdnWithAlias, query.getMdnid()));
            processColumn(query, CmFinoFIX.CRSubscriberMDN.FieldName_RecordID, mdnWithAlias);
        }
        if (query.getDctName() != null) {
            final String dctAlias = DCTID + DAOConstants.ALIAS_SUFFIX;
            criteria = criteria.createAlias(DCTID, dctAlias);
            final String dctWithAlias = dctAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRDistributionChainTemplate.FieldName_DistributionChainName;

            addLikeStartRestriction(criteria, dctWithAlias, query.getDctName());
            processColumn(query, CmFinoFIX.CRDistributionChainTemplate.FieldName_DistributionChainName, dctWithAlias);
        }
        if(query.isCommissionChanged() != null){
            if(query.isCommissionChanged())
                criteria.add(Restrictions.eq(CmFinoFIX.CRLOP.FieldName_IsCommissionChanged, query.isCommissionChanged()));
            else
                criteria.add(Restrictions.isNull(CmFinoFIX.CRLOP.FieldName_IsCommissionChanged));// query.isCommissionChanged()));
        }
        if (query.getLopid() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRLOP.FieldName_RecordID, query.getLopid()));
        }
        if (query.getLopstatus() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRLOP.FieldName_LOPStatus, query.getLopstatus()).ignoreCase());
        }
        if (query.getStartDate() != null) {
            criteria.add(Restrictions.gt(CmFinoFIX.CRLOP.FieldName_CreateTime, query.getStartDate()));
        }
        if (query.getEndDate() != null) {
            criteria.add(Restrictions.lt(CmFinoFIX.CRLOP.FieldName_CreateTime, query.getEndDate()));
        }

        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        //applying Order
        criteria.addOrder(Order.desc(CmFinoFIX.CRLOP.FieldName_RecordID));
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<LOP> results = criteria.list();

        return results;
    }
}
