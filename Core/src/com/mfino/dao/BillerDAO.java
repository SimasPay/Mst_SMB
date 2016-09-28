/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.BillerQuery;
import com.mfino.domain.Biller;

/**
 *
 * @author Maruthi
 */
public class BillerDAO extends BaseDAO<Biller> {
	
	 public List<Biller> get(BillerQuery query) {

        Criteria criteria = createCriteria();

        if (query.getBankcode() != null) {
            criteria.add(Restrictions.eq(Biller.FieldName_BankCodeForRouting, query.getBankcode()));
        }
        if (query.getBillerName() != null) {
            criteria.add(Restrictions.eq(Biller.FieldName_BillerName, query.getBillerName()).ignoreCase());
        }
        if (query.getBillerCode() != null) {
            criteria.add(Restrictions.eq(Biller.FieldName_BillerCode, query.getBillerCode()));
        }
        if (query.getBillerType() != null) {
            criteria.add(Restrictions.eq(Biller.FieldName_BillerType, query.getBillerType()).ignoreCase());
        }

        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<Biller> results = criteria.list();

        return results;
    }
	
}
