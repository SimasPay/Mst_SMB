/**
 * 
 */
package com.mfino.transactionapi.handlers.subscriber.impl;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.KtpDetailsDAO;
import com.mfino.domain.KtpDetails;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRKtpDetails;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.Result;
import com.mfino.service.NotificationService;
import com.mfino.service.PartnerService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.SubscriberKtpValidationHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberKTPValidationXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sunil
 *
 */
@Service("SubscriberKtpValidationHandlerImpl")
public class SubscriberKtpValidationHandlerImpl  extends FIXMessageHandler implements SubscriberKtpValidationHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	
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
	
	/* (non-Javadoc)
	 * @see com.mfino.transactionapi.handlers.subscriber.SubscriberKtpValidation#handle(com.mfino.transactionapi.vo.TransactionDetails)
	 */
	@Override
	public Result handle(TransactionDetails transactionDetails) {
		
		log.info("Handling subscriber services Registration webapi request");
		SubscriberKTPValidationXMLResult result = new SubscriberKTPValidationXMLResult();
		
		CRKtpDetails ktpDetails = new CRKtpDetails();
		ktpDetails.setAgentMDN(transactionDetails.getSourceMDN());
		ktpDetails.setMDN(transactionDetails.getDestMDN());
		ktpDetails.setDateOfBirth(new Timestamp(transactionDetails.getDateOfBirth()));
		ktpDetails.setFullName(transactionDetails.getFirstName());
		ktpDetails.setKTPID(transactionDetails.getKtpId());

		transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_KtpDetails,ktpDetails.DumpFields());
		
		result.setActivityStatus(false);
		result.setTransactionID(Long.parseLong("-1"));
		
		SubscriberMDN agentMDN = subscriberMdnService.getByMDN(ktpDetails.getAgentMDN());
		
		result.setCompany(agentMDN.getSubscriber().getCompany());
		result.setLanguage(CmFinoFIX.Language_Bahasa);
		
		Integer validationResult = transactionApiValidationService.validateAgentMDN(agentMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		
		validationResult = transactionApiValidationService.validatePin(agentMDN, transactionDetails.getSourcePIN());
		
		validationResult = CmFinoFIX.ResponseCode_Success;
		
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			result.setNumberOfTriesLeft(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT)-agentMDN.getWrongPINCount());
			return result;
		}
		
		SubscriberMDN subMDN = subscriberMdnService.getByMDN(ktpDetails.getMDN());
		if (subMDN != null) {
			
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNAlreadyRegistered_Source);
			return result;
		}
		
		KtpDetails ktpDetail = new KtpDetails();
		ktpDetail.setAgentMDN(transactionDetails.getSourceMDN());
		ktpDetail.setMDN(transactionDetails.getDestMDN());
		ktpDetail.setDateOfBirth(new Timestamp(transactionDetails.getDateOfBirth()));
		ktpDetail.setFullName(transactionDetails.getFirstName());
		ktpDetail.setKTPID(transactionDetails.getKtpId());
		
		
		/**
		 * Mapping of Address in DB as below:
		 * 
		 *  AddressLine -> Address.Line1
		 *  RT -> Address.RT
		 *  RW -> Address.RW
		 *  District -> Address.STATE
		 *  SubDistrict -> Address.SUBSTATE
		 *  Province -> Address.REGIONNAME
		 *  PostalCode -> Address.ZipCode
		 */
		
		result.setName(transactionDetails.getFirstName());
		result.setDob(getDob(transactionDetails.getDateOfBirth()));
		result.setMothersMaidenName("mothersMaidenName");
		result.setAddressLine("addressLine");
		result.setRt("rt");
		result.setRw("rw");
		result.setSubDistrict("subDistrict");
		result.setDistrict("district");
		result.setProvince("province");
		result.setPostalCode("postalCode");
		
		KtpDetailsDAO ktpDetailsDAO = DAOFactory.getInstance().getKtpDetailsDAO();
		ktpDetailsDAO.save(ktpDetail);
		
		result.setTransactionID(ktpDetail.getID());
		result.setCode(String.valueOf(CmFinoFIX.NotificationCode_SubscriberKtpValdiationSuccess));
		result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberKtpValdiationSuccess);
		result.setMessage("Subscriber KTP validation successfull");
		result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
		
		return result;
	}
	
	private String getDob(Date dobDate) {
		
		String dob = null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dobDate);
		
		String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH));
		String month = String.valueOf(cal.get(Calendar.MONTH) < 10 ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH)+1));
		String year = String.valueOf(cal.get(Calendar.YEAR));
		
		dob = day + month + year;
		
		return dob;
	}
}
