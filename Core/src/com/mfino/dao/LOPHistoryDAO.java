/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.LOPHistoryQuery;
import com.mfino.domain.LOPHistory;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADMIN
 */
public class LOPHistoryDAO extends BaseDAO<LOPHistory> {

    public LOPHistoryDAO() {
        super();
    }

    public List<LOPHistory> get(LOPHistoryQuery query) {

        final String LOP_ASSOC_NAME = "LOP";
        Criteria criteria = createCriteria();

       if (query.getLopid() != null) {
            final String lopAlias = LOP_ASSOC_NAME + DAOConstants.ALIAS_SUFFIX;
            criteria.createAlias(LOP_ASSOC_NAME, lopAlias);
            final String lopWithAlias = lopAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRLOP.FieldName_RecordID;
            criteria.add(Restrictions.eq(lopWithAlias, query.getLopid()));
            processColumn(query, CmFinoFIX.CRLOP.FieldName_RecordID, lopWithAlias);
        }

        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        //applying Order
        criteria.addOrder(Order.asc(CmFinoFIX.CRLOPHistory.FieldName_RecordID));
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<LOPHistory> results = criteria.list();

        return results;

    }
}
