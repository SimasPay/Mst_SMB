/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.DistributionChainTemplateQuery;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author xchen
 */
public class DistributionChainTemplateDAO extends BaseDAO<DistributionChainTemplate> {

    public List<DistributionChainTemplate> get(DistributionChainTemplateQuery query) {
        Criteria criteria = createCriteria();

        if (query.getId() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBase.FieldName_RecordID, query.getId()));
        }

        // Have search the tradename of the corresponding merchant.
        if (query.getDistributionChainTemplateName() != null) {
           addLikeStartRestriction(criteria,
                   CmFinoFIX.CRDistributionChainTemplate.FieldName_DistributionChainName,
                   query.getDistributionChainTemplateName());
        }

        if (query.getExactdistributionChainTemplateName() != null) {
           criteria.add(Restrictions.eq(
                   CmFinoFIX.CRDistributionChainTemplate.FieldName_DistributionChainName,
                   query.getExactdistributionChainTemplateName()).ignoreCase());
        }

        if(query.getCreatedBy() != null){
            addLikeStartRestriction(criteria,
                    CmFinoFIX.CRDistributionChainTemplate.FieldName_CreatedBy,
                    query.getCreatedBy());
        }
        
        if(null != query.getServiceIdSearch()){
        	criteria.createAlias(CmFinoFIX.CRDistributionChainTemplate.FieldName_Service, "service");
        	criteria.add(Restrictions.like("service."+CmFinoFIX.CRService.FieldName_RecordID, query.getServiceIdSearch()));
        }
        
        if(null != query.getUserIdSearch()){
        	Criteria partnerServiceCriteria = criteria.createCriteria(CmFinoFIX.CRDistributionChainTemplate.FieldName_PartnerServicesFromDistributionChainTemplateID);
        	Criteria partnerCriteria = partnerServiceCriteria.createCriteria(CmFinoFIX.CRPartnerServices.FieldName_Partner);
        	Criteria userCriteria = partnerCriteria.createCriteria(CmFinoFIX.CRPartner.FieldName_User);
        	
        	userCriteria.add(Restrictions.eq(CmFinoFIX.CRUser.FieldName_RecordID, query.getUserIdSearch()));
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
        if(template.getmFinoServiceProviderByMSPID() == null){
            MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDAO.getById(1l);
            template.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(template);
    }
}
