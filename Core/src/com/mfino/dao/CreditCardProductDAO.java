package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.CreditCardProductQuery;
import com.mfino.domain.Company;
import com.mfino.domain.CreditCardProduct;

public class CreditCardProductDAO extends BaseDAO<CreditCardProduct>{

    public List<CreditCardProduct> get(CreditCardProductQuery query) {

        Criteria criteria = createCriteria();
        
        if (query.getProductIndicatorCode()!= null) {
            criteria.add(Restrictions.eq(CreditCardProduct.FieldName_ProductIndicatorCode, query.getProductIndicatorCode()).ignoreCase());
        }
        if (query.getCompanyID() != null) {
            criteria.add(Restrictions.eq(CreditCardProduct.FieldName_CompanyID, query.getCompanyID()));
        }
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<CreditCardProduct> results = criteria.list();

        return results;
    }
    
    public List<CreditCardProduct> getProducts(Company _company){
        CreditCardProductQuery query = new CreditCardProductQuery();
        CreditCardProductDAO dao = new CreditCardProductDAO();
        query.setCompany(_company);
        return dao.get(query);
}
}
