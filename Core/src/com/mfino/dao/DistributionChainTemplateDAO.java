/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.DistributionChainTemplateQuery;
import com.mfino.domain.Base;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Service;
import com.mfino.domain.User;

/**
 *
 * @author xchen
 */
public class DistributionChainTemplateDAO extends BaseDAO<DistributionChainTemplate> {

    public List<DistributionChainTemplate> get(DistributionChainTemplateQuery query) {
        Criteria criteria = createCriteria();

        if (query.getId() != null) {
            criteria.add(Restrictions.eq(Base.FieldName_RecordID, query.getId()));
        }

        // Have search the tradename of the corresponding merchant.
        if (query.getDistributionChainTemplateName() != null) {
           addLikeStartRestriction(criteria,
                   DistributionChainTemplate.FieldName_DistributionChainName,
                   query.getDistributionChainTemplateName());
        }

        if (query.getExactdistributionChainTemplateName() != null) {
           criteria.add(Restrictions.eq(
                   DistributionChainTemplate.FieldName_DistributionChainName,
                   query.getExactdistributionChainTemplateName()).ignoreCase());
        }

        if(query.getCreatedBy() != null){
            addLikeStartRestriction(criteria,
                    DistributionChainTemplate.FieldName_CreatedBy,
                    query.getCreatedBy());
        }
        
        if(null != query.getServiceIdSearch()){
        	criteria.add(Restrictions.like(Service.FieldName_RecordID, query.getServiceIdSearch()));
        }
        
        if(null != query.getUserIdSearch()){
        	Criteria partnerServiceCriteria = criteria.createCriteria(DistributionChainTemplate.FieldName_PartnerServicesFromDistributionChainTemplateID);
        	Criteria partnerCriteria = partnerServiceCriteria.createCriteria(PartnerServices.FieldName_Partner);
        	Criteria userCriteria = partnerCriteria.createCriteria(Partner.FieldName_User);
        	
        	userCriteria.add(Restrictions.eq(User.FieldName_RecordID, query.getUserIdSearch()));
        }
        
        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);
        //applying Order
        applyOrder(query, criteria);

        @SuppressWarnings("unchecked")
        List<DistributionChainTemplate> results= criteria.list();
        
        return results;
    }

    @Override
    public void save(DistributionChainTemplate template){
        if(template.getMfinoServiceProvider() == null){
            MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDAO.getById(1l);
            template.setMfinoServiceProvider(msp);
        }
        super.save(template);
    }
}
