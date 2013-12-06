/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.PartnerDefaultServicesQuery;
import com.mfino.domain.PartnerDefaultServices;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author Maruthi
 */
public class PartnerDefaultServicesDAO extends BaseDAO<PartnerDefaultServices> {
	
	@SuppressWarnings("unchecked")
	public List<PartnerDefaultServices> get(PartnerDefaultServicesQuery query) {

        Criteria criteria = createCriteria();

        if (query.getBusinessPartnerType() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRPartnerDefaultServices.FieldName_BusinessPartnerType, query.getBusinessPartnerType()));
        }
         processBaseQuery(query, criteria);
         List<PartnerDefaultServices> results = criteria.list();
         return results;
	}
	
}
