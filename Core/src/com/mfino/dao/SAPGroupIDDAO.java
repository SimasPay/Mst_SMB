/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;

import com.mfino.dao.query.SAPGroupIDQuery;
import com.mfino.domain.SapGroupid;

/**
 *
 * @author xchen
 */
public class SAPGroupIDDAO extends BaseDAO<SapGroupid> {

    public List<SapGroupid> get(SAPGroupIDQuery query){
                Criteria criteria = createCriteria();
        if (query.getGroupID() != null) {
            addLikeStartRestriction(criteria, SapGroupid.FieldName_GroupID, query.getGroupID());
        }
        if (query.getGroupIDName() != null) {
            addLikeStartRestriction(criteria, SapGroupid.FieldName_GroupIDName, query.getGroupIDName());
        }

        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);
        //applying Order
        applyOrder(query, criteria);

        @SuppressWarnings("unchecked")
        List<SapGroupid>results = criteria.list();

        return results;
    }
}
