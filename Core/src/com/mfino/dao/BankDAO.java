/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.BankQuery;
import com.mfino.domain.Bank;

/**
 *
 * @author sandeepjs
 */
public class BankDAO extends BaseDAO<Bank> {
	
	@SuppressWarnings("unchecked")
	public List<Bank> get(BankQuery query) {

        Criteria criteria = createCriteria();

        if (query.getBankcode() != null) {
            criteria.add(Restrictions.eq(Bank.FieldName_BankCodeForRouting, query.getBankcode()));
        }
         processBaseQuery(query, criteria);
         List<Bank> results = criteria.list();
         return results;
	}
	
}
