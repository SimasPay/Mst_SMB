/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.DenominationQuery;
import com.mfino.domain.Biller;
import com.mfino.domain.Denomination;

/**
 *
 * @author Srinu
 */
public class DenominationDAO extends BaseDAO<Denomination> {

    public List<Denomination> get(DenominationQuery query) {

        Criteria criteria = createCriteria();

        if(query.getBillerId() != null) {
            criteria.createCriteria("Biller").add(Restrictions.eq(Biller.FieldName_RecordID, query.getBillerId()));
        }
        
        if(query.getAmount() != null) {
            criteria.add(Restrictions.eq(Denomination.FieldName_DenominationAmount, query.getAmount()));
        }
        
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<Denomination> results = criteria.list();

        return results;
    }

}
