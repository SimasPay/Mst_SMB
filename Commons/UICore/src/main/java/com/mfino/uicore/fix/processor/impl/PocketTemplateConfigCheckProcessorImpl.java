package com.mfino.uicore.fix.processor.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketTemplateConfigDAO;
import com.mfino.dao.query.PocketTemplateConfigQuery;
import com.mfino.domain.PocketTemplateConfig;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSPocketTemplateConfigCheck;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.PocketTemplateConfigCheckProcessor;

/**
 *
 * @author Satya
 */
@Service("PocketTemplateConfigCheckProcessorImpl")
public class PocketTemplateConfigCheckProcessorImpl extends BaseFixProcessor implements PocketTemplateConfigCheckProcessor{
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg)
	{
		CMJSError err = (CMJSError)isDuplicateConfig(msg);
		if(!err.getErrorCode().equals(CmFinoFIX.ErrorCode_NoError))
			return err;
		return checkDefaultPTCwithSimillarConfigExist(msg);	
		
	}

    public CFIXMsg isDuplicateConfig(CFIXMsg msg) {

        CMJSPocketTemplateConfigCheck realMsg = (CMJSPocketTemplateConfigCheck) msg;
        PocketTemplateConfigDAO dao = DAOFactory.getInstance().getPocketTemplateConfigDao();
        PocketTemplateConfigQuery query = new PocketTemplateConfigQuery();
       
        
        if (realMsg.getSubscriberType() != null) {
			query.set_subscriberType(realMsg.getSubscriberType());
		}
        if(realMsg.getBusinessPartnerType() != null){
        	query.set_businessPartnerType(realMsg.getBusinessPartnerType());
        }
        
        if(realMsg.getCommodity() != null){
        	query.set_commodity(realMsg.getCommodity());
        }        
        
        if(realMsg.getKYCLevel() != null){
        	query.set_KYCLevel(realMsg.getKYCLevel());     	
        }       
        
        if(realMsg.getPocketType() != null){
        	query.set_pocketType(realMsg.getPocketType());
        }
        if(realMsg.getIsCollectorPocket() != null){
        	query.set_isCollectorPocket(realMsg.getIsCollectorPocket());
        }
        if(realMsg.getIsSuspencePocket() != null){
        	query.set_isSuspensePocket(realMsg.getIsSuspencePocket());
        }
        if(realMsg.getPocketTemplateID() != null){
        	query.set_pocketTemplateID(realMsg.getPocketTemplateID());
        }
        if(realMsg.getGroupID() !=null){
        	query.set_GroupID(Long.parseLong(realMsg.getGroupID()));
        }       
        
        
        List<PocketTemplateConfig> results = dao.get(query);

        CMJSError err=new CMJSError();
        if(results.size() > 0) {
        	boolean isEditingSamePTC = false;
        	for(PocketTemplateConfig ptc : results) {
        		if(ptc.getID().equals(realMsg.getID())) {
        			isEditingSamePTC = true;
        			break;
        		}
        	}
        	if(!isEditingSamePTC) {
        		err.setErrorCode(CmFinoFIX.ErrorCode_DuplicatePocketTemplateConfig);
            	err.setErrorDescription(MessageText._("PTC with similar configuration exists"));
            	return err;
        	}
        }
        err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
    	err.setErrorDescription(MessageText._("Template Name Available"));

        return err;
    }
    
    public CFIXMsg checkDefaultPTCwithSimillarConfigExist(CFIXMsg msg) {

        CMJSPocketTemplateConfigCheck realMsg = (CMJSPocketTemplateConfigCheck) msg;
        PocketTemplateConfigDAO dao = DAOFactory.getInstance().getPocketTemplateConfigDao();
        PocketTemplateConfigQuery query = new PocketTemplateConfigQuery();
       
        
        if (realMsg.getSubscriberType() != null) {
			query.set_subscriberType(realMsg.getSubscriberType());
		}
        if(realMsg.getBusinessPartnerType() != null){
        	query.set_businessPartnerType(realMsg.getBusinessPartnerType());
        }
        
        if(realMsg.getCommodity() != null){
        	query.set_commodity(realMsg.getCommodity());
        }        
        
        if(realMsg.getKYCLevel() != null){
        	query.set_KYCLevel(realMsg.getKYCLevel());     	
        }       
        
        if(realMsg.getPocketType() != null){
        	query.set_pocketType(realMsg.getPocketType());
        }
        if(realMsg.getIsCollectorPocket() != null){
        	query.set_isCollectorPocket(realMsg.getIsCollectorPocket());
        }
        if(realMsg.getIsSuspencePocket() != null){
        	query.set_isSuspensePocket(realMsg.getIsSuspencePocket());
        }
        if(realMsg.getGroupID() !=null){
        	query.set_GroupID(Long.parseLong(realMsg.getGroupID()));
        }
        
        List<PocketTemplateConfig> results = dao.get(query);

        CMJSError err=new CMJSError();
        
        if(results.size() > 0) {        	
        	Iterator<PocketTemplateConfig> iterator = results.iterator();
        	PocketTemplateConfig defaultPTCwithSimilarConfig = null;
        	while(iterator.hasNext())
        	{
        		defaultPTCwithSimilarConfig = iterator.next();
        		if(defaultPTCwithSimilarConfig.getIsDefault())
        		{        			
        			break;
        		}       		
        	}
        	if(defaultPTCwithSimilarConfig.getID().equals(realMsg.getID())) {        		
        		if(realMsg.getIsDefault() != null && defaultPTCwithSimilarConfig.getIsDefault() == true && realMsg.getIsDefault() == false) {
        			err.setErrorCode(CmFinoFIX.ErrorCode_LastDefaultPocketTemplateConfig);
        			err.setErrorDescription(MessageText._("Atleast one configuration need to be default"));
        		} else {
        			err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
                	err.setErrorDescription(MessageText._("Editing the existing PTC"));
        		}
        	} else {
        		err.setErrorCode(CmFinoFIX.ErrorCode_DefaultPTCwithSimillarConfigExists);
            	err.setErrorDescription(MessageText._("Pocket Template Config with similar configuration exists"));
        	}        	
        } else {
        	err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
        	err.setErrorDescription(MessageText._("No PTC with similar configurations exist"));
        }        
        return err;
    }
}