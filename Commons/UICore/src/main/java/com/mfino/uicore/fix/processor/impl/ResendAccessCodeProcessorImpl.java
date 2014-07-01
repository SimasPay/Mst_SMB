/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionType;
import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSResendAccessCode;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.FundStorageService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.UnRegisteredTxnInfoService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ResendAccessCodeProcessor;
import com.mfino.util.MfinoUtil;

/**
 * @author Amar
 */
@Service("ResendAccessCodeProcessorImpl")
public class ResendAccessCodeProcessorImpl extends BaseFixProcessor implements ResendAccessCodeProcessor{

	@Autowired
	@Qualifier("UnRegisteredTxnInfoServiceImpl")
	private UnRegisteredTxnInfoService unRegisteredTxnInfoService;
	
	@Autowired
	@Qualifier("FundStorageServiceImpl")
	private FundStorageService fundStorageService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {

		CMJSResendAccessCode realMsg = (CMJSResendAccessCode) msg;
		CMJSError error = new CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		
		UnRegisteredTxnInfoQuery  query = new UnRegisteredTxnInfoQuery();
		if(realMsg.getSctlId() != null)
		{
			query.setTransferSctlId(realMsg.getSctlId());
			UnRegisteredTxnInfoDAO unRegisteredTxnInfoDAO =  DAOFactory.getInstance().getUnRegisteredTxnInfoDAO();
			List<UnRegisteredTxnInfo> list = unRegisteredTxnInfoDAO.get(query);
			if(list != null && list.size()>0)
			{
				UnRegisteredTxnInfo unRegisteredTxnInfo = list.get(0);
				
				ServiceChargeTransactionLog sctl = DAOFactory.getInstance().getServiceChargeTransactionLogDAO().getById(realMsg.getSctlId());
				CommodityTransfer ct = DAOFactory.getInstance().getCommodityTransferDAO().getById(sctl.getCommodityTransferID());
				TransactionType trxnType = DAOFactory.getInstance().getTransactionTypeDAO().getById(sctl.getTransactionTypeID());

				if(unRegisteredTxnInfo.getCashoutCTId() != null)
				{
					error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
					error.setErrorDescription(MessageText._("Transaction Cashed out. New Fund Access Code can't be generated"));
					return error;
				}
				//resend Fac for fund Allocation type transaction only if there is fund and it is not expired
				if((ServiceAndTransactionConstants.TRANSACTION_FUND_ALLOCATION.equals(trxnType.getTransactionName())) && 
						!( (unRegisteredTxnInfo.getUnRegisteredTxnStatus().equals(CmFinoFIX.UnRegisteredTxnStatus_FUND_PARTIALLY_WITHDRAWN)) ||
						(unRegisteredTxnInfo.getUnRegisteredTxnStatus().equals(CmFinoFIX.UnRegisteredTxnStatus_FUNDALLOCATION_COMPLETE)) )
				){
					error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
					error.setErrorDescription(MessageText._("Your Fund is completely withdrawn or has expired. New Fund Access Code can't be generated"));
					return error;
				}

				String code;
				String receiverMDN;
				if(ServiceAndTransactionConstants.TRANSACTION_FUND_ALLOCATION.equals(trxnType.getTransactionName())){
					code = fundStorageService.generateFundAccessCode(unRegisteredTxnInfo.getFundDefinition());
					receiverMDN = unRegisteredTxnInfo.getWithdrawalMDN();
				}else{
					code = unRegisteredTxnInfoService.generateFundAccessCode();
					receiverMDN = unRegisteredTxnInfo.getSubscriberMDNByMDNID().getMDN();
				}
					
				String senderMDN = realMsg.getSenderMDN();

				String digestedCode = MfinoUtil.calculateDigestPin(receiverMDN, code);
				unRegisteredTxnInfo.setDigestedPIN(digestedCode);
				unRegisteredTxnInfoDAO.save(unRegisteredTxnInfo);
				Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
				NotificationWrapper wrapper = new NotificationWrapper();
				SubscriberMDN smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(senderMDN);
				if(smdn != null)
				{
					language = smdn.getSubscriber().getLanguage();
					wrapper.setFirstName(smdn.getSubscriber().getFirstName());
	            	wrapper.setLastName(smdn.getSubscriber().getLastName());
				}
				wrapper.setOneTimePin(code);
				wrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_Web);
				if(ServiceAndTransactionConstants.TRANSACTION_FUND_ALLOCATION.equals(trxnType.getTransactionName())){
					wrapper.setCode(CmFinoFIX.NotificationCode_ResendAccessCodeForFund);
					wrapper.setDestMDN(receiverMDN);
				}
				else{
					wrapper.setCode(CmFinoFIX.NotificationCode_ResendAccessCodeNotificationToSenderOfUnregisteredTransfer);
					wrapper.setDestMDN(senderMDN);
				}
				wrapper.setLanguage(language);
				wrapper.setSctlID(realMsg.getSctlId());
				wrapper.setServiceChargeTransactionLog(sctl);
				wrapper.setCommodityTransfer(ct);

				String message = notificationMessageParserService.buildMessage(wrapper,true);
				if(ServiceAndTransactionConstants.TRANSACTION_FUND_ALLOCATION.equals(trxnType.getTransactionName())){
					smsService.setDestinationMDN(receiverMDN);
				}
				else{
					smsService.setDestinationMDN(senderMDN);
				}
				smsService.setMessage(message);
				smsService.setNotificationCode(wrapper.getCode());
				smsService.asyncSendSMS();

			}
			error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
			error.setErrorDescription(MessageText._("New Access Code sent successfully"));
		}

		return error;


	}

}
