/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.BrandQuery;
import com.mfino.domain.Brand;

/**
 *
 * @author admin
 */
public class BrandDAO extends BaseDAO<Brand> {

    public List<Brand> get(BrandQuery query) {
        final String COMPANY_ASSOC_NAME = "Company";
        Criteria criteria = createCriteria();

        if (query.getPrefixCode() != null) {
            criteria.add(Restrictions.eq(Brand.FieldName_PrefixCode, query.getPrefixCode()).ignoreCase());
        }
        if (query.getPrefixCodeLike() != null) {
            addLikeStartRestriction(criteria, Brand.FieldName_PrefixCode, query.getPrefixCodeLike());
        }
        if (query.getBrandName() != null) {
            criteria.add(Restrictions.eq(Brand.FieldName_BrandName, query.getBrandName()).ignoreCase());
        }
        if (query.getCompany() != null) {
//         final String companyAlias = COMPANY_ASSOC_NAME + DAOConstants.ALIAS_SUFFIX;
//            criteria.createAlias(COMPANY_ASSOC_NAME, companyAlias);
//            final String companyWithAlias = companyAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + Company.FieldName_Company;
//            criteria.add(Restrictions.eq(companyWithAlias, query.getCompanyName()));
//            processColumn(query, Company.FieldName_CompanyName, companyWithAlias);
            criteria.add(Restrictions.eq(Brand.FieldName_Company, query.getCompany()));
        }
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<Brand> results = criteria.list();

        return results;
    }

	@SuppressWarnings("unchecked")
	public Brand getBrandbyPrefix(String brandPrefix) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(Brand.FieldName_PrefixCode, brandPrefix));
		List<Brand> results = criteria.list();
		if(results.size()>0){
		return results.get(0);
		}
		return null;
	}
}
