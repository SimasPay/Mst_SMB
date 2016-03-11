/**
 * 
 */
package com.mfino.transactionapi.handlers.agent.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSAgentClosing;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.MFAService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.agent.AgentClosingHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberAccountClosingXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ValidationUtil;

/**
 * @author Sunil
 *
 */
@Service("AgentClosingHandlerImpl")
public class AgentClosingHandlerImpl  extends FIXMessageHandler implements AgentClosingHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	/* (non-Javadoc)
	 * @see com.mfino.transactionapi.handlers.subscriber.SubscriberKtpValidation#handle(com.mfino.transactionapi.vo.TransactionDetails)
	 */
	@Override
	public Result handle(TransactionDetails transactionDetails) {
		
		log.info("Handling subscriber services Registration webapi request");
		SubscriberAccountClosingXMLResult result = new SubscriberAccountClosingXMLResult();
		
		CMJSAgentClosing agentClosing = new CMJSAgentClosing();
		agentClosing.setDestMDN(transactionDetails.getDestMDN());
		
		transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_JSAgentClosing,agentClosing.DumpFields());
		
		SubscriberMDN subMDN = subscriberMdnService.getByMDN(agentClosing.getDestMDN());
		
		result.setLanguage(CmFinoFIX.Language_Bahasa);
		
		Integer validationResult = transactionApiValidationService.validateSubscriberAsDestination(subMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			return result;
		}
		
		if (subMDN != null) {
			
			boolean isHttps = transactionDetails.isHttps();
			boolean isHashedPin = transactionDetails.isHashedPin();
			String oneTimeOTP = transactionDetails.getActivationOTP();
			
			if(CmFinoFIX.NotificationCode_OTPValidationSuccessful.equals(ValidationUtil.validateOTP(subMDN, isHttps, isHashedPin, oneTimeOTP))) {
			
				try {
				
					retireAllCardPans(subMDN.getID());
					retirePartner(subMDN.getID());
					
					Subscriber subscriber = subMDN.getSubscriber();
					
					subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
					
					SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
					subscriberDAO.save(subscriber);
					
					result.setCode(String.valueOf(CmFinoFIX.NotificationCode_AgentClosingSuccess));
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
					result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingSuccess);
					
					log.debug("Agent state modifeid to retired....");
					
				} catch (Exception ex) {
					
					result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
					result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingFailed);
					
					log.debug("Agent state is not modified to retired due to some error....");
				}
			} else {
				
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
				result.setNotificationCode(CmFinoFIX.NotificationCode_AgentClosingFailed);
				
				log.debug("Agent state is not modified to retired due to otp validation failure....");
			}
			
		} else {
		
			result.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
			
			log.debug("Agent not found....");
		}		
		
		return result;
	}
	
	private void retireAllCardPans(Long mdnId) {
        
		// Here we need to get the Records from pocket table for MdnId.        
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();			
        PocketQuery pocketQuery = new PocketQuery();
        pocketQuery.setMdnIDSearch(mdnId);
        
        List<Pocket> resultantPockets = pocketDAO.get(pocketQuery);

        for (Pocket eachPocket : resultantPockets) {
        	
        	if(eachPocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)) {
        		
        		continue;
        	}
        	
        	String cardPanStringToReplace = null;
            String cardPan = eachPocket.getCardPAN();
            int timesRetired = 0;       
            
            if (StringUtils.isNotBlank(cardPan)) {
            	
            	cardPanStringToReplace = cardPan + "R";
            }

            if (StringUtils.isBlank(cardPanStringToReplace)) {
            	
            	cardPanStringToReplace = cardPan;
            }

            eachPocket.setCardPAN(cardPanStringToReplace);
            eachPocket.setStatus(CmFinoFIX.PocketStatus_Retired);
            eachPocket.setIsDefault(false);
            // Now set back the Data into the table.
            try{
            	
            	pocketDAO.save(eachPocket);
            	
            }catch(ConstraintViolationException e){
            	
            	log.info("Handling Constraint violation Exception Occured: " + e );
            	if (StringUtils.isNotBlank(cardPan)) {
            		timesRetired=timesRetired+1;
                	throw e;
                }            	
            }
        }        
	}
	
	private void retirePartner(Long subscriberId) {
		
		// TODO Auto-generated method stub
		PartnerQuery partnerQuery = new PartnerQuery();
		partnerQuery.setSubscriberID(subscriberId);
		String tradeNameStringToReplace = null;
		String codeStringToReplace = null;
		String userNameStringToReplace = null;

		PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
		List<Partner> partnerLst = partnerDAO.get(partnerQuery);
		Partner partner = partnerLst.get(0);
		
		tradeNameStringToReplace = getPartnerFieldStringForThisPartnerField(partner.getTradeName(),false);
		
		if(tradeNameStringToReplace != null) {
			
			partner.setTradeName(tradeNameStringToReplace);
		}

		codeStringToReplace = getPartnerFieldStringForThisPartnerField(partner.getPartnerCode(),true);
		
		if(codeStringToReplace != null) {
			
			partner.setPartnerCode(codeStringToReplace);
		}

		UserQuery userQuery = new UserQuery();
		userQuery.setUserName(partner.getUser().getUsername());
		UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
		List <User> userLst = userDAO.get(userQuery);

		User user = null;
		
		if(userLst != null) {
			user = userLst.get(0);
		}
		
		if(user != null){
			
			userNameStringToReplace = getUserNameStringForUserName(user.getUsername());
			
			if(userNameStringToReplace != null)
				user.setUsername(userNameStringToReplace);
			
			user.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
			userDAO.save(user);
		}

		if(userNameStringToReplace != null) {
			
			partner.getUser().setUsername(userNameStringToReplace);
		}
		
		partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_Retired);
		
		partnerDAO.save(partner);
	}
	
	private String getPartnerFieldStringForThisPartnerField(String field, boolean isPartnerCode){
		
    	PartnerQuery partnerQuery = new PartnerQuery();
    	
    	if(isPartnerCode){
    		partnerQuery.setPartnerCode(field);
    		
    	} else {
    		
    		partnerQuery.setTradeName(field);
    	}
    	
    	partnerQuery.setPartnerCodeLike(true);
    	
    	PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
    	List <Partner> results = partnerDAO.get(partnerQuery);
    	int noOfTimesRetired=0;
    	
    	for( Partner partner : results ){
    		
    		String partnerField = null;
    		if(isPartnerCode){
    			
    			partnerField = partner.getPartnerCode();
    			
    		} else {
    			
    			partnerField = partner.getTradeName();
    		}
    		
    		if(StringUtils.isBlank(partnerField)){
    			
    			continue;
    		}
    		
    		String[] splitComponents = partnerField.split("R");
            if (splitComponents.length != 2) {
                
            	continue;
            }
    		
            String timesRetiredIndicator = splitComponents[1];
            int timesRetired = 0;
            
            try {
                
            	timesRetired = Integer.parseInt(timesRetiredIndicator);
            	
            } catch (NumberFormatException nfe) {
            	
            	log.error("Getting partner field count" + nfe.getMessage(), nfe);
            }

            if (timesRetired >= noOfTimesRetired) {
                
            	noOfTimesRetired = timesRetired + 1;
            }            
    	}
    	
    	if (field.contains("R")) {
            // If we reach here then we already have R0.
            String str = field;
            String strWithoutR = str.substring(0, str.indexOf("R"));
            
            return strWithoutR + "R" + noOfTimesRetired;
            
        } else {
            
        	return field + "R" + noOfTimesRetired;
        }    	
    }
	
	private String getUserNameStringForUserName(String userName) {
        
		UserQuery userQuery = new UserQuery();
        userQuery.setUserNameLike(userName);
        
        UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
        List<User> results = userDAO.get(userQuery);
        
        int noOfTimesRetired = 0;
        for (User user : results) {
            
        	String name = user.getUsername();
            
        	if (StringUtils.isBlank(name)) {
                continue;
            }

            // here check if the MDN has R and Number after it.
            
        	String[] splitComponents = name.split("R");
            if (splitComponents.length != 2) {
                continue;
            }

            String timesRetiredIndicator = splitComponents[1];
            int timesRetired = 0;
            try {
                
            	timesRetired = Integer.parseInt(timesRetiredIndicator);
            } catch (NumberFormatException nfe) {
            	log.error("Getting userName count" + nfe.getMessage(), nfe);
            }

            if (timesRetired >= noOfTimesRetired) {
                noOfTimesRetired = timesRetired + 1;
            }
        }
        
        if (userName.contains("R")) {
            // If we reach here then we already have R0.
            String field = userName;
            String mdnWithoutR = field.substring(0, field.indexOf("R"));
            
            return mdnWithoutR + "R" + noOfTimesRetired;
            
        } else {
            
        	return userName + "R" + noOfTimesRetired;
        }
    }
}