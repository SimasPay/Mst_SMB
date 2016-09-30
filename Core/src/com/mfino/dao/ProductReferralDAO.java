/**
 * 
 */
package com.mfino.dao;


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.query.ProductReferralQuery;
import com.mfino.domain.ProductReferral;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Admin
 *
 */
public class ProductReferralDAO extends BaseDAO<ProductReferral>{
	
private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public ProductReferral getProductReferralByName(String productDesired){
		log.info("ProductReferralDAO :: getProductReferralByName() "+productDesired);
		
		if((null == productDesired) || ("".equals(productDesired))) return null;
		
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(ProductReferral.FieldName_ProductDesired, productDesired).ignoreCase());
		
        List<ProductReferral> results = criteria.list();
        
        if((results != null) && (results.size() > 0)) return results.get(0);
                
        return null;
	}
		public List<ProductReferral> get(ProductReferralQuery query) {

        Criteria criteria = createCriteria();

        if (StringUtils.isNotBlank(query.getAgentMDN())) {
            criteria.add(Restrictions.eq(ProductReferral.FieldName_AgentMDN, query.getAgentMDN()).ignoreCase());
        }
        if (StringUtils.isNotBlank(query.getSubscriberMDN())) {
            criteria.add(Restrictions.eq(ProductReferral.FieldName_SubscriberMDN, query.getSubscriberMDN()).ignoreCase());
        }
        if (StringUtils.isNotBlank(query.getEmail())) {
            criteria.add(Restrictions.like(ProductReferral.FieldName_Email, query.getEmail()).ignoreCase());
        }
        
        if (StringUtils.isNotBlank(query.getFullName())) {
            criteria.add(Restrictions.eq(ProductReferral.FieldName_FullName, query.getFullName()).ignoreCase());
        }
        if (StringUtils.isNotBlank(query.getProductDesired())) {
            criteria.add(Restrictions.eq(ProductReferral.FieldName_ProductDesired, query.getProductDesired()).ignoreCase());
        }
        if (StringUtils.isNotBlank(query.getOthers())) {
            criteria.add(Restrictions.like(ProductReferral.FieldName_Others, query.getOthers()).ignoreCase());
        }
        
        if (query.getStartDate() != null) {
			criteria.add(Restrictions.ge(ProductReferral.FieldName_CreateTime, query.getStartDate()));
		}
		if (query.getEndDate() != null) {
			criteria.add(Restrictions.le(ProductReferral.FieldName_CreateTime, query.getEndDate()));
		}		
        
        processPaging(query, criteria);
        
        @SuppressWarnings("unchecked")
        List<ProductReferral> results = criteria.list();

        return results;
    }
	

}
