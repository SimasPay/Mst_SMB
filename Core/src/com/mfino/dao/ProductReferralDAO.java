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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.query.ProductReferralQuery;
import com.mfino.dao.query.SystemParametersQuery;
import com.mfino.domain.ProductReferral;
import com.mfino.domain.SystemParameters;
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
		criteria.add(Restrictions.eq(CmFinoFIX.CRProductReferral.FieldName_ProductDesired, productDesired).ignoreCase());
		
        List<ProductReferral> results = criteria.list();
        
        if((results != null) && (results.size() > 0)) return results.get(0);
                
        return null;
	}
	
	//@Transactional(readOnly=false, propagation = Propagation.REQUIRED, rollbackFor=Throwable.class)
	public List<ProductReferral> get(ProductReferralQuery query) {

        Criteria criteria = createCriteria();

        if (StringUtils.isNotBlank(query.getAgentMDN())) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRProductReferral.FieldName_AgentMDN, query.getAgentMDN()).ignoreCase());
        }
        if (StringUtils.isNotBlank(query.getSubscriberMDN())) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRProductReferral.FieldName_SubscriberMDN, query.getSubscriberMDN()).ignoreCase());
        }
        if (StringUtils.isNotBlank(query.getEmail())) {
            criteria.add(Restrictions.like(CmFinoFIX.CRProductReferral.FieldName_Email, query.getEmail()).ignoreCase());
        }
        
        if (StringUtils.isNotBlank(query.getFullName())) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRProductReferral.FieldName_FullName, query.getFullName()).ignoreCase());
        }
        if (StringUtils.isNotBlank(query.getProductDesired())) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRProductReferral.FieldName_ProductDesired, query.getProductDesired()).ignoreCase());
        }
        if (StringUtils.isNotBlank(query.getOthers())) {
            criteria.add(Restrictions.like(CmFinoFIX.CRProductReferral.FieldName_Others, query.getOthers()).ignoreCase());
        }
        
        
        processPaging(query, criteria);
        
        @SuppressWarnings("unchecked")
        List<ProductReferral> results = criteria.list();

        return results;
    }
	
	
	
	
	
	

}
