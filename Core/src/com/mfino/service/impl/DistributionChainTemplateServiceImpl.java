/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.DistributionChainTemplateDAO;
import com.mfino.dao.query.DistributionChainTemplateQuery;
import com.mfino.domain.DistributionChainTemp;
import com.mfino.service.DistributionChainTemplateService;

/**
 *
 * @author sandeepjs
 */
@Service("DistributionChainTemplateServiceImpl")
public class DistributionChainTemplateServiceImpl implements DistributionChainTemplateService{
	
	private static Logger log = LoggerFactory.getLogger(DistributionChainTemplateServiceImpl.class);
    
    /**
     * Returns list of distribution chain template objects for a particular user.
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<DistributionChainTemp> getDistributionChainTemplates(DistributionChainTemplateQuery query){
    	log.info("DistributionChainTemplateService :: getDistributionChainTemplates");
    	List<DistributionChainTemp> distributionChainTemplates = new ArrayList<DistributionChainTemp>();
    	
    	DistributionChainTemplateDAO dctDao = DAOFactory.getInstance().getDistributionChainTemplateDAO();
    	distributionChainTemplates = dctDao.get(query);
    	
    	log.info("DistributionChainTemplateService :: getDistributionChainTemplates END distributionChainTemplates="+distributionChainTemplates);
    	return distributionChainTemplates;
    }
   
    /**
     * 
     * @param dctId
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public DistributionChainTemp getDistributionChainTemplateById(Long dctId){
    	log.info("DistributionChainTemplateService :: getDistributionChainTemplateById() BEGIN");
    	DistributionChainTemplateDAO dao = DAOFactory.getInstance().getDistributionChainTemplateDAO();
    	DistributionChainTemp distributionChainTemplate = null;
    	if(dctId!=null){
    		log.info("getting dcyTemplate with id: "+dctId);
    		distributionChainTemplate = dao.getById(dctId);
    	}
    	
    	log.info("DistributionChainTemplateService :: getDistributionChainTemplateById() END distributionChainTemplate="+distributionChainTemplate);
    	return distributionChainTemplate;
    }
}
