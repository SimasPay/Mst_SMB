/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ProductIndicatorQuery;
import com.mfino.domain.ProductIndicator;

/**
 *
 * @author Diwakar
 */
public class ProductIndicatorDAO extends BaseDAO<ProductIndicator>{
    public ProductIndicatorDAO() {
        super();
    }

    public List<ProductIndicator> get(ProductIndicatorQuery query) {

        Criteria criteria = createCriteria();

        if (query.getTransactionType() != null) {
            criteria.add(Restrictions.eq(ProductIndicator.FieldName_TransactionUICategory, query.getTransactionType()));
        }
        if (query.getCompany() != null) {
            criteria.add(Restrictions.eq(ProductIndicator.FieldName_Company, query.getCompany()));
        }
        if (query.getProductCode() != null) {
            criteria.add(Restrictions.eq(ProductIndicator.FieldName_ProductIndicatorCode, query.getProductCode()).ignoreCase());
        }
      //  processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        //applying Order
      //  criteria.addOrder(Order.desc(ProductIndicator.FieldName_RecordID));
        //applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<ProductIndicator> results = criteria.list();

        return results;
    }
}
