/**
 * 
 */
package com.mfino.transactionapi.handlers.subscriber.impl;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
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
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.wsclient.RSClientPostHttps;

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
		
		KtpDetailsDAO ktpDetailsDAO = DAOFactory.getInstance().getKtpDetailsDAO();
		ktpDetailsDAO.save(ktpDetail);
		
		RSClientPostHttps wsCall = new RSClientPostHttps();
		
		try {
			
			JSONObject request = new JSONObject();
			
			request.put("nik",transactionDetails.getKtpId());
			request.put("namalengkap",transactionDetails.getFirstName());
			request.put("tanggallahir",getKtpDob(transactionDetails.getDateOfBirth()));
			request.put("reffno",StringUtils.leftPad(String.valueOf(ktpDetail.getID()),12,"0"));
			request.put("action","inquiryEKTPPersonal");
			
			JSONObject response = wsCall.callHttpsPostService(request.toString(), ConfigurationUtil.getKTPServerURL(), ConfigurationUtil.getKTPServerTimeout(), "KTP Server Validation");
		
			if(null != response) {// && response.get("status").toString().equals("Success")) {
			
				
				ktpDetail.setBankResponse(response.toString());
				ktpDetail.setBankResponseStatus(response.get("responsecode").toString());
				ktpDetailsDAO.save(ktpDetail);
			
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
				result.setAddressLine(response.get("alamat").toString());
				result.setCity(response.get("kota").toString());
				result.setRt(response.get("rt").toString());
				result.setRw(response.get("rw").toString());
				result.setSubDistrict(response.get("kelurahan").toString());
				result.setDistrict(response.get("kecamatan").toString());
				result.setProvince(response.get("provinsi").toString());
				result.setPostalCode(response.get("kodepos").toString());
				result.setTransactionID(ktpDetail.getID());
				result.setCode(String.valueOf(CmFinoFIX.NotificationCode_SubscriberKtpValdiationSuccess));
				result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberKtpValdiationSuccess);
				result.setMessage("Subscriber KTP validation successfull");
				result.setResponseStatus(GeneralConstants.RESPONSE_CODE_SUCCESS);
				
			} else {
				
				result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberKtpValidationFailed);
			}
		} catch(Exception ex) {
			
			log.error("Error in parsing the response from server..." + ex);
		}
		
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
	
	private String getKtpDob(Date dobdate) {
		
		String dob = null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dobdate);
		
		String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH));
		String month = String.valueOf(cal.get(Calendar.MONTH) < 10 ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH)+1));
		String year = String.valueOf(cal.get(Calendar.YEAR));
		
		dob = year + "-" + month + "-" + day;
		
		return dob;
	}
}
