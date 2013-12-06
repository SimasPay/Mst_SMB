/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.AgentCashinTransactionLogDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.query.AgentCashInTransactionQuery;
import com.mfino.domain.AgentCashInTransactions;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAgentCashIn;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.service.AuthorizationService;
import com.mfino.service.EnumTextService;
import com.mfino.service.SMSService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.transactionapi.service.AgentCashInService;
import com.mfino.uicore.fix.processor.AgentCashInProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.validators.PartnerValidator;


/**
 * 
 * @author Raju
 */

@Service("AgentCashInProcessorImpl")
public class AgentCashInProcessorImpl extends BaseFixProcessor implements AgentCashInProcessor{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("AgentCashInServiceImpl")
	private AgentCashInService agentCashInService ;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

	//@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		AgentCashinTransactionLogDAO actlDao = DAOFactory.getInstance().getAgentCashinTransactionLogDAO();
		CMAgentCashIn realMsg = (CMAgentCashIn) msg;
		
		
		log.info("AgentCashIn real.getAction():"+realMsg.getaction());
		log.info("Realmsg.getAdminAction(): "+realMsg.getAdminAction());
		
		if(CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())){
			AgentCashInTransactions actl=new AgentCashInTransactions();
		    if (!(authorizationService.isAuthorized(CmFinoFIX.Permission_ServicePartner_Distribute) || authorizationService.isAuthorized(CmFinoFIX.Permission_Partner_Funding))) {
		        log.error("You are not authorized to perform this operation");
		        errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
		        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		        return errorMsg;
		    }
			
			log.info("Procesing the cashin Request for " + realMsg.getPartnerID());
			if (realMsg.getPartnerID() == null ) {
				log.error("AgentID is null");
				errorMsg.setErrorDescription(MessageText._("Invalid Agent ID"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			if (realMsg.getAmount() == null ) {
				log.error("Amount is null");
				errorMsg.setErrorDescription(MessageText._("Invalid Amount"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			
			log.info("Validating cashIn request");
			errorMsg = validateAndCreateAgentCashIn(realMsg,errorMsg,actl);
			if(CmFinoFIX.ErrorCode_Generic.equals(errorMsg.getErrorCode())){
				return errorMsg;
			}
				
			//checking if funding amount needs an approval.
			BigDecimal maxFundingAmount = systemParametersService.getBigDecimal(SystemParameterKeys.MAX_AMT_FOR_AGENT_FUNDING);
			if(realMsg.getAmount().compareTo(maxFundingAmount)!=1){
				
				errorMsg = agentCashInService.processAgentCashIn(actl);
			}
			else{
				log.info("Requested transfer amount is greater than the max funding amount limit.Transaction needs approval");
				errorMsg.setErrorDescription(MessageText._("Requested transfer amount is greater than the max funding amount limit.Transaction will be processed after " +
																									"Approval"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			}
			
			actlDao.save(actl);
			return errorMsg;

		}
		else if(CmFinoFIX.JSaction_Select.equals(realMsg.getaction())){
			AgentCashInTransactionQuery query = new AgentCashInTransactionQuery();
			
			if(realMsg.getstart() != null){
				query.setStart(realMsg.getstart());
			}
			if(realMsg.getlimit() != null)
			{
				query.setLimit(realMsg.getlimit());
			}
			List<AgentCashInTransactions> results = actlDao.get(query);
			realMsg.allocateEntries(results.size());
			for (int i = 0; i < results.size(); i++) {
				AgentCashInTransactions agentCashInTransactions = results.get(i);
				CMAgentCashIn.CGEntries entry = new CMAgentCashIn.CGEntries();
				updateMessage(agentCashInTransactions, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			return realMsg;
		}
		else if(CmFinoFIX.JSaction_Update.equals(realMsg.getaction())){
		    if (!authorizationService.isAuthorized(CmFinoFIX.Permission_FundingForAgent_Approve)) {
		        log.error("You are not authorized to perform this operation");
		        errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
		        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		        return errorMsg;
		    }
			if(realMsg.getTransactionID() != null)
			{
				AgentCashInTransactions agentCashInTransactions = actlDao.getById(realMsg.getTransactionID());
				if(CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())){
					agentCashInTransactions.setAgentCashInTrxnStatusReason(realMsg.getAdminComment());
					errorMsg = agentCashInService.processAgentCashIn(agentCashInTransactions);
					if(CmFinoFIX.ErrorCode_NoError.equals(errorMsg.getErrorCode())){
						errorMsg.setErrorDescription(MessageText._("Transaction successfully approved!"));
					}
				}
				else if(CmFinoFIX.AdminAction_Reject.equals(realMsg.getAdminAction())){
					agentCashInTransactions.setAgentCashInTrxnStatus(CmFinoFIX.AgentCashInTrxnStatus_Failed);
					agentCashInTransactions.setAgentCashInTrxnStatusReason(realMsg.getAdminComment());
					errorMsg.setErrorDescription(MessageText._("Transaction successfully rejected!"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
					sendSms(agentCashInTransactions,"sdf");
					
				}
				actlDao.save(agentCashInTransactions);
			}
			return errorMsg;	
		}
		else if(CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())){
			
		}
		return realMsg;
	}
	
	private void sendSms(AgentCashInTransactions agentCashInTransactions,
			String string) {
		smsService.setDestinationMDN(agentCashInTransactions.getDestMDN());
		smsService.setMessage("Dear agent " + agentCashInTransactions.getDestMDN() + " your cashIn request with AgentCashIn transaction ID: " +
				agentCashInTransactions.getID()+ " has been rejected " );
		smsService.send();	
	}

	private void updateMessage(AgentCashInTransactions agentCashInTransactions,
			CMAgentCashIn.CGEntries e) {
		e.setID(agentCashInTransactions.getID());
		e.setSourceMDN(agentCashInTransactions.getSourceMDN());
		e.setDestMDN(agentCashInTransactions.getDestMDN());
		e.setDestPartnerID(agentCashInTransactions.getDestPartnerID());
		e.setSourcePocketID(agentCashInTransactions.getSourcePocketID());
		e.setDestPocketID(agentCashInTransactions.getDestPocketID());
		e.setAmount(agentCashInTransactions.getAmount());
		e.setAgentCashInTrxnStatus(agentCashInTransactions.getAgentCashInTrxnStatus());
		e.setAgentCashInTrxnStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_AgentCashInTrxnStatus, null, agentCashInTransactions.getAgentCashInTrxnStatus()));
		e.setAgentCashInTrxnStatusReason(agentCashInTransactions.getAgentCashInTrxnStatusReason());
		e.setSctlId(agentCashInTransactions.getSctlId());
		e.setCreatedBy(agentCashInTransactions.getCreatedBy());
		e.setCreateTime(agentCashInTransactions.getCreateTime());
		e.setLastUpdateTime(agentCashInTransactions.getLastUpdateTime());
		e.setUpdatedBy(agentCashInTransactions.getUpdatedBy());		
	}

	private CMJSError validateAndCreateAgentCashIn(CMAgentCashIn realMsg, CMJSError errorMsg, AgentCashInTransactions actl) {
		//Getting Destination
		Partner agent = partnerDAO.getById(realMsg.getPartnerID()); 
		if(agent==null){
			log.error("Destination agent is null");
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			errorMsg.setErrorDescription(MessageText._("Destination agent is null"));
			return errorMsg;
		}
		PartnerValidator partnerValidator = new PartnerValidator();
		//partnerValidator.setIsAgent(true);
		partnerValidator.setPartner(agent);
		//validating destination agent
		log.info("validating destination agent");
		Integer validationResult= partnerValidator.validate();
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Agent/Partner is not active"); //set the error desc as below as that will be the only chance for validation failure
			errorMsg.setErrorDescription(MessageText._("Agent/Partner is not active"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		SubscriberMDN agentmdn=agent.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
		

		PartnerServices agentService = null;
		Pocket agentPocket =null;
		try {
			agentService = transactionChargingService.getPartnerService(agent.getID(), transactionChargingService.getServiceProviderId(null), transactionChargingService.getServiceId(ServiceAndTransactionConstants.SERVICE_AGENT));
			if(agentService==null){
				log.error("Valid agent service not found. Cash-in to agent is not possible");
				errorMsg.setErrorDescription(MessageText._("Valid agent service not found. Cash-in to agent is not possible"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			//getting destination pocket
			agentPocket = agentService.getPocketBySourcePocket();
			log.info("validating destination agent pocket ");
			if(agentPocket==null){
				log.error("Valid agent emoney pocket not found");
				errorMsg.setErrorDescription(MessageText._("Valid agent emoney pocket not found"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
		} catch (InvalidServiceException e) {
			log.error("Service Not Avialable",e);
			errorMsg.setErrorDescription(MessageText._("Service Not Avialable"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		
		actl.setDestPartnerID(agent.getID());
		actl.setDestMDN(agentmdn.getMDN());
		actl.setAmount(realMsg.getAmount());
		actl.setAgentCashInTrxnStatus(CmFinoFIX.AgentCashInTrxnStatus_Initialized);
		return errorMsg;
	}
	
	

}
