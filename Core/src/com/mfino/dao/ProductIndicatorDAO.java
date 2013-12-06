/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import com.mfino.dao.query.ProductIndicatorQuery;
import com.mfino.domain.ProductIndicator;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

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
            criteria.add(Restrictions.eq(CmFinoFIX.CRProductIndicator.FieldName_TransactionUICategory, query.getTransactionType()));
        }
        if (query.getCompany() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRProductIndicator.FieldName_Company, query.getCompany()));
        }
        if (query.getProductCode() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRProductIndicator.FieldName_ProductIndicatorCode, query.getProductCode()).ignoreCase());
        }
      //  processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        //applying Order
      //  criteria.addOrder(Order.desc(CmFinoFIX.CRProductIndicator.FieldName_RecordID));
        //applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<ProductIndicator> results = criteria.list();

        return results;
    }
}
