/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.domain.PocketTemplate;
import com.mfino.service.PocketTemplateService;

/**
 *
 * @author Raju
 */
@Service("PocketTemplateServiceImpl")
public class PocketTemplateServiceImpl implements PocketTemplateService {
	
	private static Logger log = LoggerFactory.getLogger(PocketTemplateServiceImpl.class);

	// *FindbugsChange*
	// Previous -- public static PocketTemplateDAO templateDAO = DAOFactory.getInstance().getPocketTemplateDao();
	// Changed to private
	private PocketTemplateDAO templateDAO = DAOFactory.getInstance().getPocketTemplateDao();

	/**
	 * Returns the pocketType of the pocket Template got by the pocket template id
	 * @param pocketTemplateID
	 * @return
	 */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public Integer getPocketType(Long pocketTemplateID){
    	PocketTemplate template = null;
    	if(pocketTemplateID!=null){
    		log.info("getting pocketTemplate by id: "+pocketTemplateID);
    		template = templateDAO.getById(pocketTemplateID);
    	}
        return ((template != null) ? template.getType() : null).intValue();
    }

    public boolean areCompatible(PocketTemplate template1, PocketTemplate template2) {
        if(template1 == null || template2 == null){
            return false;
        }

        //template type and commodity are mandatory.
        Long tempTemplate2L = template2.getType();
        Long tempTemplate1L = template1.getType();
        
        if(!tempTemplate1L.equals(tempTemplate2L)){
            return false;
        }
        
        Long tempCommodity2L = template2.getCommodity();
        Long tempCommodity1L = template1.getCommodity();

        if(!tempCommodity1L.equals(template2.getCommodity())){
            return false;
        }

        //Billing type, operator code, bank code, bank account card type are optional.
        //They are not present for all pocket types.

     // We are not checking BillingType field as per the smart ticket #441.
//        if(!equals(template1.getBillingType(), template2.getBillingType())){
//            return false;
//        }
        if(!equals(template1.getOperatorcode().intValue(), template2.getOperatorcode().intValue())){
            return false;
        }
        if(!equals(template1.getBankcode().intValue(), template2.getBankcode().intValue())){
            return false;
        }
        if(!equals(template1.getBankaccountcardtype().intValue(), template2.getBankaccountcardtype().intValue())){
            return false;
        }

        return true;
    }

    public boolean equals(Integer intA, Integer intB){
        if(intA == null && intB == null){
            return true;
        }
        if(intA != null && intA.equals(intB)) {
            return true;
        }
        return false;
    }
    	
    /**
     * Gets the pocketTemplate by the given pocketTemplateID
     * @param pocketTemplateId
     * @return
     */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public PocketTemplate getById(Long pocketTemplateId) {
    	PocketTemplate template = null;
    	if(pocketTemplateId!=null){
    		log.info("getting pocketTemplate by id: "+pocketTemplateId);
    		template = templateDAO.getById(pocketTemplateId);
    	}
        return template;
    }

}
