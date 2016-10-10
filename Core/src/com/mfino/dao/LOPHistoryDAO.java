/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.LOPHistoryQuery;
import com.mfino.domain.LopHistory;

/**
 *
 * @author ADMIN
 */
public class LOPHistoryDAO extends BaseDAO<LopHistory> {

    public LOPHistoryDAO() {
        super();
    }

    public List<LopHistory> get(LOPHistoryQuery query) {

        final String LOP_ASSOC_NAME = "LOPHistory";
        Criteria criteria = createCriteria();

       if (query.getLopid() != null) {
            final String lopAlias = LOP_ASSOC_NAME + DAOConstants.ALIAS_SUFFIX;
            criteria.createAlias(LOP_ASSOC_NAME, lopAlias);
            final String lopWithAlias = lopAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + LopHistory.FieldName_RecordID;
            criteria.add(Restrictions.eq(lopWithAlias, query.getLopid()));
            processColumn(query, LopHistory.FieldName_RecordID, lopWithAlias);
        }

        processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        //applying Order
        criteria.addOrder(Order.asc(LopHistory.FieldName_RecordID));
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<LopHistory> results = criteria.list();

        return results;

    }
}
