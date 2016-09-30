/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.Person2PersonQuery;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.Person2Person;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author sunil
 */
public class Person2PersonDAO extends BaseDAO<Person2Person> {

    private static final String subscriberTableName = "Subscriber";
    private static final String subcriberIDColumnName = "ID";

    public Person2PersonDAO() {
        super();
    }

    @Override
    public void save(Person2Person s) {
        if (s.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            s.setMfinoServiceProvider(msp);
        }

        super.save(s);
    }

    public List<Person2Person> get(Person2PersonQuery query) {

        Criteria criteria = createCriteria();

        if (query.getMdn() != null && query.getMdn().length() > 0) {
            SimpleExpression expression = getLikeAnywhereRestriction(Person2Person.FieldName_MDN, query.getMdn());
            criteria.add(expression);
            processColumn(criteria, query, expression, Person2Person.FieldName_MDN);
        }

        if (query.getPeerName() != null) {
            criteria.add(Restrictions.gt(Person2Person.FieldName_PeerName, query.getPeerName()));
        }
        if (query.getSubscriberId() != null) {
            criteria.createAlias(subscriberTableName, subscriberTableName + DAOConstants.ALIAS_SUFFIX);
            criteria.add(Restrictions.eq(subscriberTableName + DAOConstants.ALIAS_SUFFIX + DAOConstants.ALIAS_COLNAME_SEPARATOR + subcriberIDColumnName, query.getSubscriberId()));
        }

        // Paging
        processPaging(query, criteria);

        //applying Order for columns which are not part of criteria
        applyOrder(query, criteria);
        
        @SuppressWarnings("unchecked")
        List<Person2Person> results = criteria.list();

        return results;
    }

    private void processColumn(Criteria criteria, Person2PersonQuery query, SimpleExpression expression, String colName) {

        String order = getOrderForColumn(query, colName);
        if (order != null) {
            addOrder(order, colName, criteria);
            query.removeMappingFromOrderMap(colName);
        }
    }
}

