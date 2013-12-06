/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.BulkLOPQuery;
import com.mfino.domain.BulkLOP;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author admin
 */
public class BulkLOPDAO extends BaseDAO<BulkLOP> {
  public List<BulkLOP> get(BulkLOPQuery query) {

        final String SUBSCRIBERMDNBYSOURCEMDNID = "SubscriberMDNByMDNID";
        Criteria criteria = createCriteria();

        if (query.getBulkLopid() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRLOP.FieldName_RecordID, query.getBulkLopid()));
        }
        if (query.getMdnid() != null) {
            final String mdnAlias = SUBSCRIBERMDNBYSOURCEMDNID + DAOConstants.ALIAS_SUFFIX;
            criteria.createAlias(SUBSCRIBERMDNBYSOURCEMDNID, mdnAlias);
            final String mdnWithAlias = mdnAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRSubscriberMDN.FieldName_RecordID;
            criteria.add(Restrictions.eq(mdnWithAlias, query.getMdnid()));
            processColumn(query, CmFinoFIX.CRSubscriberMDN.FieldName_RecordID, mdnWithAlias);
        }
        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);criteria.addOrder(Order.desc(CmFinoFIX.CRBulkLOP.FieldName_RecordID));
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<BulkLOP> results = criteria.list();

        return results;
  }
}
