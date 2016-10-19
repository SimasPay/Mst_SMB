package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.DistributionChainTemplateDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.query.PartnerServicesQuery;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceSettlementCfg;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSPartnerServices;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.PartnerServicesProcessor;
import com.mfino.util.ConfigurationUtil;
@Service("PartnerServicesProcessorImpl")
public class PartnerServicesProcessorImpl extends BaseFixProcessor implements PartnerServicesProcessor{
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	private void updateEntity(PartnerServices ps, CMJSPartnerServices.CGEntries e) {
		DistributionChainTemplateDAO dctDAO = DAOFactory.getInstance().getDistributionChainTemplateDAO();
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		PartnerDAO pDAO = DAOFactory.getInstance().getPartnerDAO();
		ServiceDAO sDAO = DAOFactory.getInstance().getServiceDAO();

		if(e.isRemoteModifiedDistributionChainTemplateID() || e.getDistributionChainTemplateID()!=null){
			log.info("Partner Services ID: " + ps.getId() + " Distributed Chain Template ID updated to "+ e.getDistributionChainTemplateID() +" by user:"+getLoggedUserNameWithIP());
			Long oldParentId = null;
			if(ps.getPartnerByParentid()!=null){
				oldParentId = ps.getPartnerByParentid().getId().longValue();
			}
			Long oldDct = null;
			if(ps.getDistributionChainTemp()!=null){	
				oldDct = ps.getDistributionChainTemp().getId().longValue();
			}
			Long parentId = null;
			if(ps.getPartnerByPartnerid()!=null){
				parentId = ps.getPartnerByPartnerid().getId().longValue();
			}
			modifyChildsParentIdOnParentDctChange(oldDct,parentId,oldParentId,pDAO);
			if(e.getDistributionChainTemplateID()!=null){
				ps.setDistributionChainTemp(dctDAO.getById(e.getDistributionChainTemplateID()));
			} else {
				ps.setDistributionChainTemp(null);
			}			
 		}
		
		if (e.getPartnerID() != null) {
			if(ps.getPartnerByParentid()==null || !e.getPartnerID().equals(ps.getPartnerByParentid().getId())){
				log.info("Partner Services ID: " + ps.getId() + " Partner ID updated to "+ e.getPartnerID() +" by user:"+getLoggedUserNameWithIP());
        	}
			ps.setPartnerByParentid(pDAO.getById(e.getPartnerID()));
		}
		
		if(e.isRemoteModifiedParentID() || e.getParentID()!=null){
			log.info("Partner Services ID: " + ps.getId() + " Partner by Parent ID updated to "+ e.getParentID() +" by user:"+getLoggedUserNameWithIP());
			if(e.getParentID()!=null){
				ps.setPartnerByParentid(pDAO.getById(e.getParentID()));
			} else {
				ps.setPartnerByParentid(null);
			}
		}
		
		if (e.getCollectorPocket() != null) {
			if(ps.getCollectorpocket()==null || !e.getCollectorPocket().equals(ps.getCollectorpocket())){
				log.info("Partner Services ID: " + ps.getId() + " Pocket By collector pocket updated to "+ e.getCollectorPocket() +" by user:"+getLoggedUserNameWithIP());
        	}
			ps.setCollectorpocket(new BigDecimal(e.getCollectorPocket()));
		}
		 
		if (e.getSourcePocket() != null) {
			if(ps.getPocketBySourcepocket()==null || !e.getSourcePocket().equals(ps.getPocketBySourcepocket().getId())){
				log.info("Partner Services ID: " + ps.getId() + " Pocket by source pocket updated to "+ e.getSourcePocket() +" by user:"+getLoggedUserNameWithIP());
        	}
			ps.setPocketBySourcepocket(pocketDAO.getById(e.getSourcePocket()));
		}	
		if (e.getDestPocketID() != null) {
			if(ps.getPocketByDestpocketid()==null || !e.getDestPocketID().equals(ps.getPocketByDestpocketid().getId())){
				log.info("Partner Services ID: " + ps.getId() + " Pocket by destination pocket ID updated to "+ e.getDestPocketID() +" by user:"+getLoggedUserNameWithIP());
        	}
			ps.setPocketByDestpocketid(pocketDAO.getById(e.getDestPocketID()));
		}	
		
		if (e.getLevel() != null) {
			if(!e.getLevel().equals(ps.getPslevel())){
				log.info("Partner Services ID: " + ps.getId() + " Level updated to " + e.getLevel() + " by user:" + getLoggedUserNameWithIP());
        	}
			ps.setPslevel(e.getLevel());
		}
		
		
		
		if (e.getPartnerServiceStatus() != null) {
			if(!e.getPartnerServiceStatus().equals(ps.getStatus())){
				log.info("Partner Services ID: " + ps.getId() + " Partner Service Status updated to "+ e.getPartnerServiceStatus() +" by user:"+getLoggedUserNameWithIP());
        	}
			ps.setStatus(e.getPartnerServiceStatus());
		} else {
			if (ps.getStatus() == null) {
				log.info("Partner Services ID: " + ps.getId() + " Partner Service Status updated to "+ CmFinoFIX.PartnerServiceStatus_Initialized +" by user:"+getLoggedUserNameWithIP());
	        	ps.setStatus(CmFinoFIX.PartnerServiceStatus_Initialized);
			}
		}
		
		if (e.getIsServiceChargeShare() != null) {
			if(!e.getIsServiceChargeShare().equals(ps.getIsservicechargeshare())){
				log.info("Partner Services ID: " + ps.getId() + " Is Service Charge Share updated to "+ e.getIsServiceChargeShare() +" by user:"+getLoggedUserNameWithIP());
        	}
			ps.setIsservicechargeshare(e.getIsServiceChargeShare());
		}
		
		if (e.getServiceID() != null) {
			if(ps.getService()==null || !e.getServiceID().equals(ps.getService().getId())){
				log.info("Partner Services ID: " + ps.getId() + " Service ID updated to "+ e.getServiceID() +" by user:"+getLoggedUserNameWithIP());
        	}
			ps.setService(sDAO.getById(e.getServiceID()));
		}
		
		if (e.getServiceProviderID() != null) {
			if(ps.getPartnerByServiceproviderid()==null || !e.getServiceProviderID().equals(ps.getPartnerByServiceproviderid().getId())){
				log.info("Partner Services ID: " + ps.getId() + " Partner by service provider ID updated to "+ e.getServiceProviderID() +" by user:"+getLoggedUserNameWithIP());
        	}
			ps.setPartnerByServiceproviderid(pDAO.getById(e.getServiceProviderID()));
		}
	}
	
	private void updateMessage(PartnerServices ps, CMJSPartnerServices.CGEntries e) {
		e.setID(ps.getId().longValue());
		e.setMSPID(ps.getMfinoServiceProvider().getId().longValue());
		if (ps.getDistributionChainTemp() != null) {
			e.setDistributionChainTemplateID(ps.getDistributionChainTemp().getId().longValue());
			e.setDistributionChainName(ps.getDistributionChainTemp().getName());
		}
		
		if (ps.getCollectorpocket() != null) {
			PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
			Pocket pocket = pocketDAO.getById(ps.getCollectorpocket().longValue());
			e.setCollectorPocket(ps.getCollectorpocket().longValue());
			e.setCollectorCardPAN(pocket.getCardpan());
			if (StringUtils.isNotBlank(pocket.getCardpan()) && 
					pocket.getPocketTemplateByPockettemplateid() != null) {
	        	String cPan = pocket.getCardpan();
	        	if (cPan.length() > 6) {
	        		cPan = cPan.substring(cPan.length()-6);
	        	}
	        	e.setCollectorPocketDispText(pocket.getPocketTemplateByPockettemplateid().getDescription() + " - " + cPan);
			} else if (pocket.getPocketTemplateByPockettemplateid() != null) {
				e.setCollectorPocketDispText(pocket.getPocketTemplateByPockettemplateid().getDescription());
			}
		}
		
		if (ps.getPocketBySourcepocket() != null) {
			e.setSourcePocket(ps.getPocketBySourcepocket().getId().longValue());
			e.setSourceCardPAN(ps.getPocketBySourcepocket().getCardpan());
			if (StringUtils.isNotBlank(ps.getPocketBySourcepocket().getCardpan()) && 
					ps.getPocketBySourcepocket().getPocketTemplateByPockettemplateid() != null) {
	        	String cPan = ps.getPocketBySourcepocket().getCardpan();
	        	if (cPan.length() > 6) {
	        		cPan = cPan.substring(cPan.length()-6);
	        	}
	        	e.setSourcePocketDispText(ps.getPocketBySourcepocket().getPocketTemplateByPockettemplateid().getDescription() + " - " + cPan);
			} else if (ps.getPocketBySourcepocket().getPocketTemplateByPockettemplateid() != null) {
				e.setSourcePocketDispText(ps.getPocketBySourcepocket().getPocketTemplateByPockettemplateid().getDescription());
			}
		}
		
		if (ps.getPocketByDestpocketid()!= null) {
			e.setDestPocketID(ps.getPocketByDestpocketid().getId().longValue());
			e.setDestCardPAN(ps.getPocketByDestpocketid().getCardpan());
			if (StringUtils.isNotBlank(ps.getPocketByDestpocketid().getCardpan()) && 
					ps.getPocketByDestpocketid().getPocketTemplateByPockettemplateid() != null) {
	        	String cPan = ps.getPocketByDestpocketid().getCardpan();
	        	if (cPan.length() > 6) {
	        		cPan = cPan.substring(cPan.length()-6);
	        	}
	        	e.setDestPocketDispText(ps.getPocketByDestpocketid().getPocketTemplateByPockettemplateid().getDescription() + " - " + cPan);
			} else if (ps.getPocketByDestpocketid().getPocketTemplateByPockettemplateid() != null) {
				e.setDestPocketDispText(ps.getPocketByDestpocketid().getPocketTemplateByPockettemplateid().getDescription());
			}
		}
		
		if (ps.getPartnerByParentid() != null) {
			e.setPartnerID(ps.getPartnerByParentid().getId().longValue());
		}
		
		if (ps.getPartnerByParentid() != null) {
			e.setParentID(ps.getPartnerByParentid().getId().longValue());
			e.setTradeName(ps.getPartnerByParentid().getTradename());
		}
		e.setLevel(ps.getPslevel().intValue());
		
//		if (ps.getServiceProviderServices() != null) {
//			e.setServiceProviderID(ps.getServiceProviderServices().getPartnerByServiceProviderID().getID());
//			e.setServiceProviderName(ps.getServiceProviderServices().getPartnerByServiceProviderID().getTradeName());
//			e.setServiceID(ps.getServiceProviderServices().getService().getID());
//			e.setServiceName(ps.getServiceProviderServices().getService().getServiceName());
//		}
		if (ps.getService() != null) {
			e.setServiceID(ps.getService().getId().longValue());
			e.setServiceName(ps.getService().getDisplayname());
		}
		
		if (ps.getPartnerByServiceproviderid() != null) {
			e.setServiceProviderID(ps.getPartnerByServiceproviderid().getId().longValue());
			e.setServiceProviderName(ps.getPartnerByServiceproviderid().getTradename());
		}
		
		if (ps.getStatus() != null) {
			e.setPartnerServiceStatus(ps.getStatus());
			e.setPartnerServiceStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_PartnerServiceStatus, null, ps.getStatus()));
		}
		
		if (ps.getIsservicechargeshare() != null) {
			e.setIsServiceChargeShare(ps.getIsservicechargeshare().intValue());
			e.setIsServiceChargeShareText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_IsServiceChargeShare, null, e.getIsServiceChargeShare()));
		}
		
		e.setRecordVersion(((Long)ps.getVersion()).intValue());
		e.setCreatedBy(ps.getCreatedby());
		e.setCreateTime(ps.getCreatetime());
		e.setUpdatedBy(ps.getUpdatedby());
		e.setLastUpdateTime(ps.getLastupdatetime());
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		
/*		PartnerSettlementService pss = new PartnerSettlementService();
		PartnerServicesDAO partnerServicesDao = new PartnerServicesDAO();
		List<PartnerServices> partnerServices = partnerServicesDao.getPartnerServices(9L, 1L, 1L);
		log.info("Partner Services Object "+partnerServices);
		
//		pss.doSettlement(partnerServices.get(0));
		
		PartnerSettlementScheduler pScheduler = new PartnerSettlementScheduler();
		pScheduler.scheduleSettlement(partnerServices.get(0));*/
		
		
		CMJSPartnerServices realMsg = (CMJSPartnerServices) msg;
		PartnerServicesDAO dao = DAOFactory.getInstance().getPartnerServicesDAO();
		PartnerServicesQuery query = new PartnerServicesQuery();
		if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			int i = 0;
			if(realMsg.getIDSearch()!=null){
				query.setId(realMsg.getIDSearch());
			}
			if (realMsg.getPartnerID() != null) {
				query.setPartnerId(realMsg.getPartnerID());
			}
			if (realMsg.getServiceID() != null) {
				query.setServiceId(realMsg.getServiceID());
			}
			
			query.setStart(realMsg.getstart());
			query.setLimit(realMsg.getlimit());
			
			List<PartnerServices> results = dao.get(query);
			
			if (CollectionUtils.isNotEmpty(results)) {
				realMsg.allocateEntries(results.size());
				for (PartnerServices ps: results) {
					CMJSPartnerServices.CGEntries e = new CMJSPartnerServices.CGEntries();
					updateMessage(ps, e);
					realMsg.getEntries()[i] = e;
					i++;
					log.info("Partner Services ID:" + ps.getId() + " details viewing completed by user:"+getLoggedUserNameWithIP());
				}
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			
		}else if(CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())){
			
			
			
			CMJSPartnerServices.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSPartnerServices.CGEntries e: entries) {
				PartnerServices ps = dao.getById(e.getID());
				log.info("Partner Services: "+ps.getId()+" details edit requested by user:"+getLoggedUserNameWithIP());
				
				if (!(((Long)ps.getVersion()).equals(e.getRecordVersion()))) {
					log.warn("Partner Services: "+ps.getId()+" stale data exception for user:"+getLoggedUserNameWithIP());
					handleStaleDataException();					
				}
				
				if (e.getServiceProviderID() == null) {
					e.setServiceProviderID(ps.getPartnerByServiceproviderid().getId().longValue());
				}

				if (e.getServiceID() == null) {
					e.setServiceID(ps.getService().getId().longValue());
				}
				
				if (e.getPartnerID() == null) {
					e.setPartnerID(ps.getPartnerByParentid().getId().longValue());
				}
				
				if (e.getPartnerServiceStatus() == CmFinoFIX.PartnerServiceStatus_Active) {
					if(!CmFinoFIX.SubscriberStatus_Active.equals(ps.getPartnerByParentid().getPartnerstatus())){
						return generateError(3, null);
					}
					if (!validatePockets(ps)) {
						return generateError(2, null);
					}
					
					List<PartnerServices> lstPS = dao.getPartnerServices(e.getPartnerID(), e.getServiceProviderID(), e.getServiceID());
					if (CollectionUtils.isNotEmpty(lstPS) && lstPS.size() > 0) {
		                return generateError(1, null);					
					}						
				}
				
				updateEntity(ps, e);
                try {
                	dao.save(ps);
                	log.info("Partner Services: " + ps.getId() + " details edit completed by user:" + getLoggedUserNameWithIP());
                } catch (ConstraintViolationException ex) {
                	return generateError(1, ex);	
                }
				updateMessage(ps, e);
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		}else if(CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())){
			CMJSPartnerServices.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSPartnerServices.CGEntries e: entries) {
				PartnerServices ps = new PartnerServices();
				List<Integer> psstatus = new ArrayList<Integer>();
				psstatus.add(CmFinoFIX.PartnerServiceStatus_Active);
				psstatus.add(CmFinoFIX.PartnerServiceStatus_Initialized);
				
				List<PartnerServices> lstPS = dao.getPartnerServices(e.getPartnerID(), e.getServiceProviderID(), e.getServiceID(),psstatus);
				log.info("PartnerServicesProcessor :: "+lstPS.size());
//				e.getPartnerID(), e.getServiceProviderID(), e.getServiceID()
				
				log.info("e.getPartnerID()="+e.getPartnerID()+", e.getServiceProviderID()="+e.getServiceProviderID() + " e.getServiceID()="+e.getServiceID());
				
				if (CollectionUtils.isNotEmpty(lstPS) && lstPS.size() > 0) {
	                return generateError(1, null);					
				}
								
				updateEntity(ps, e);
                try {
                	dao.save(ps);
                	log.info("Partner Service: " + ps.getId() + " created by user:"+getLoggedUserNameWithIP());
                } catch (ConstraintViolationException ex) {
                	return generateError(1, ex);	
                }
                updateMessage(ps, e);
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		}
		return msg;
	}

	/**
	 * Validates whether all the pockets of the given Partner Service are Active or not.
	 * @param ps
	 * @return
	 */
	private boolean validatePockets(PartnerServices ps) {
		boolean result = true;
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		Pocket pocket = pocketDAO.getById(ps.getCollectorpocket().longValue());
		if (checkPocketStatus(pocket) && (ps.getPocketBySourcepocket()==null || 
				(ps.getPocketBySourcepocket()!=null && checkPocketStatus(ps.getPocketBySourcepocket())))) {
			Set<ServiceSettlementCfg> setSSC = ps.getServiceSettlementCfgs();

			if((CollectionUtils.isNotEmpty(setSSC)) && (ConfigurationUtil.getIsEMoneyPocketRequired())){
				for (ServiceSettlementCfg ssc: setSSC) {
					if (ssc.getSettlementTemplate() != null && checkPocketStatus(ssc.getSettlementTemplate().getPocket())) {
						result = true;
					} else {
						result = false;
					}
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("***************** pockets staus ::" + result);
		}
		return result;
	}

	/**
	 * Check whether the pocket status is Active or not.
	 * @param pocket
	 * @return
	 */
	private boolean checkPocketStatus(Pocket pocket) {
		boolean result = true;
		if((pocket != null) && (!(pocket.getStatus()).equals(CmFinoFIX.PocketStatus_Active))) {
			result = false;
		}
		return result;
	}
	
	/**
	 * @return
	 */
	private CFIXMsg generateError(int errorNum, ConstraintViolationException error) {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
		newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
		String message = "";
		if (errorNum == 1) {
			message = MessageText._("Service already added to the Partner.");
			errorMsg.setErrorDescription(message);
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			newEntries[0].setErrorName(CmFinoFIX.CMJSPartnerServices.FieldName_PartnerID);
			newEntries[0].setErrorDescription(message);
		} else if (errorNum == 2) {
			message = MessageText._("Please Activate all the pockets added to the Service.");
			errorMsg.setErrorDescription(message);
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			newEntries[0].setErrorName(CmFinoFIX.CMJSPartnerServices.FieldName_PartnerID);
			newEntries[0].setErrorDescription(MessageText._("Please Activate all the pockets added to the Service."));
		}else if (errorNum == 3) {
			message = MessageText._("Please Activate Partner.");
			errorMsg.setErrorDescription(message);
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			newEntries[0].setErrorName(CmFinoFIX.CMJSPartnerServices.FieldName_PartnerID);
			newEntries[0].setErrorDescription(MessageText._("Please Activate Partner."));
		}
		
		if(error==null){
			log.warn(message);
		}else{
			log.warn(message, error);
		}
		return errorMsg;
	}
	
	private void modifyChildsParentIdOnParentDctChange(Long dctId, Long partnerId,Long oldParentId, PartnerDAO pDAO){
		PartnerServicesDAO psDAO = DAOFactory.getInstance().getPartnerServicesDAO();

		List<PartnerServices> lstPartnerServices = psDAO.getPartnerServicesByDCT(dctId,partnerId);
    	if (CollectionUtils.isNotEmpty(lstPartnerServices)) {
    		for (PartnerServices partnerService:lstPartnerServices) {
    			if(oldParentId!=null){
    				partnerService.setPartnerByParentid(pDAO.getById(oldParentId));
    			}
    			else{
    				partnerService.setPartnerByParentid(null);
    			}
    			psDAO.save(partnerService);
    		}
    	}
	}

}
