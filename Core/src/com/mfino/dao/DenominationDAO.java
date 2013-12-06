/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import com.mfino.dao.query.DenominationQuery;
import com.mfino.domain.Denomination;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Srinu
 */
public class DenominationDAO extends BaseDAO<Denomination> {

    public List<Denomination> get(DenominationQuery query) {

        Criteria criteria = createCriteria();

        if(query.getBillerId() != null) {
            criteria.createCriteria("Biller").add(Restrictions.eq(CmFinoFIX.CRBiller.FieldName_RecordID, query.getBillerId()));
        }
        
        if(query.getAmount() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRDenomination.FieldName_DenominationAmount, query.getAmount()));
        }
        
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<Denomination> results = criteria.list();

        return results;
    }

}
