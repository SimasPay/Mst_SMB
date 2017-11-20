package com.mfino.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFAAuthenticationDAO;
import com.mfino.dao.MFATransactionInfoDAO;
import com.mfino.dao.query.MFAAuthenticationQuery;
import com.mfino.dao.query.MFATransactionInfoQuery;
import com.mfino.domain.MFAAuthentication;
import com.mfino.domain.MFATransactionInfo;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.MFAService;
import com.mfino.service.SCTLService;
import com.mfino.service.SMSService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionTypeService;
import com.mfino.util.MfinoUtil;

@org.springframework.stereotype.Service("MFAServiceImpl")
public class MFAServiceImpl implements MFAService{

	private static Logger log = LoggerFactory.getLogger(MFAServiceImpl.class);
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;

	@Autowired
	@Qualifier("TransactionTypeServiceImpl")
	private TransactionTypeService transactionTypeService;
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean isMFATransaction(String serviceName, String transactionName, Long channelCodeId){
		log.info("MFAService::isMFATransaction Begin");
		MFATransactionInfoDAO dao = DAOFactory.getInstance().getMfaTransactionInfoDAO();
		MFATransactionInfoQuery mfaQuery = new MFATransactionInfoQuery();
		Service service = DAOFactory.getInstance().getServiceDAO().getServiceByName(serviceName);
		Long serviceId = service.getID();
		TransactionType transactionType = DAOFactory.getInstance().getTransactionTypeDAO().getTransactionTypeByName(transactionName);
		Long transactionTypeId = transactionType.getID();
		mfaQuery.setTransactionTypeId(transactionTypeId);
		mfaQuery.setServiceId(serviceId);
		mfaQuery.setChannelCodeId(channelCodeId);
		mfaQuery.setMfaMode(CmFinoFIX.MFAMode_OTP);
		List <MFATransactionInfo> transactionList = dao.get(mfaQuery);
		if(transactionList.size() > 0){
			log.info("MFAService::isMFATransaction = true");
			log.info("MFAService::isMFATransaction End");
			return true;
		}
		log.info("MFAService::isMFATransaction End");
		return false;
	}
	
	/**
	 * Since the message for Bahasa and English is same, we dont need to get user's language.
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void handleMFATransaction(Long sctlID, String sourceMDN){
		log.info("MFAService::handleMFATransaction Begin");
		Integer OTPLength = systemParametersService.getOTPLength();
		String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(sourceMDN, oneTimePin);
		MFAAuthentication mfaAuth = new MFAAuthentication();
		mfaAuth.setSctlId(sctlID);
		mfaAuth.setMFAMode(CmFinoFIX.MFAMode_OTP);
		mfaAuth.setMFAValue(digestPin1);
		
		MFAAuthenticationDAO authDAO = DAOFactory.getInstance().getMfaAuthenticationDAO();
		authDAO.save(mfaAuth);

		String additionalTrxInfo = getAdditionalTnxInfo(sctlID);
		String message = "Your Simobi Code is "
				+ oneTimePin+ "(ref no: "+ sctlID
				+ ")"+additionalTrxInfo+" WASPADAI PENIPUAN! JANGAN berikan kode ini kepada siapapun! Bank Sinarmas CARE 1500153";
		
		smsService.setDestinationMDN(sourceMDN);
		smsService.setMessage(message);
		smsService.setNotificationCode(CmFinoFIX.NotificationCode_New_OTP_Success);
		smsService.asyncSendSMS();
		log.info("MFAService::handleMFATransaction End");
	}

	private String getAdditionalTnxInfo(Long sctlID) {
		String additionalTrxInfo = "";
		ServiceChargeTransactionLog serviceChargeTransactionLog = sctlService.getBySCTLID(sctlID);
		if(serviceChargeTransactionLog != null){
			TransactionType transactionType = transactionTypeService.getTransactionTypeById(serviceChargeTransactionLog.getTransactionTypeID());
			
			if(transactionType != null){
				if(StringUtils.equals(transactionType.getTransactionName(), ServiceAndTransactionConstants.TRANSACTION_TRANSFER)
						|| StringUtils.equals(transactionType.getTransactionName(), ServiceAndTransactionConstants.TRANSACTION_INTERBANK_TRANSFER)
						|| StringUtils.equals(transactionType.getTransactionName(), ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_UANGKU)
						){
					additionalTrxInfo = "For Transfer";
					String destinationAccountNo = serviceChargeTransactionLog.getOnBeHalfOfMDN();
					if(StringUtils.isNotBlank(destinationAccountNo))
						additionalTrxInfo = additionalTrxInfo+" to "+destinationAccountNo;
					
				} else if(StringUtils.equals(transactionType.getTransactionName(), ServiceAndTransactionConstants.TRANSACTION_ACTIVATION)
						|| StringUtils.equals(transactionType.getTransactionName(), ServiceAndTransactionConstants.TRANSACTION_CHANGEPIN)
						|| StringUtils.equals(transactionType.getTransactionName(), ServiceAndTransactionConstants.TRANSACTION_REACTIVATION)
						){
					additionalTrxInfo = "For "+transactionType.getDisplayName();
					
				}else{
					String invoiceNo = serviceChargeTransactionLog.getInvoiceNo();
					if(StringUtils.isNotBlank(invoiceNo))
						additionalTrxInfo = "For IDPEL "+invoiceNo;
				}
			}
		}
		return additionalTrxInfo;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean isValidOTP(String otp, Long sctlID, String sourceMdn){
		log.info("MFAService::handleMFATransaction Begin");
		String oneTimePin = otp;
		String digestPin1 = MfinoUtil.calculateDigestPin(sourceMdn, oneTimePin);
		MFAAuthenticationDAO authDAO = DAOFactory.getInstance().getMfaAuthenticationDAO();
		MFAAuthenticationQuery query = new MFAAuthenticationQuery();
		query.setSctlId(sctlID);
		query.setMfaMode(CmFinoFIX.MFAMode_OTP);
		query.setMfaValue(digestPin1);
		List <MFAAuthentication> mfaLst = authDAO.get(query);
		if(mfaLst.size() > 0){
			log.info("MFAService::isValidOTP = true");
			log.info("MFAService::isValidOTP End");
			return true;
		}
		log.info("MFAService::isValidOTP End");
		return false;			
	}
}
