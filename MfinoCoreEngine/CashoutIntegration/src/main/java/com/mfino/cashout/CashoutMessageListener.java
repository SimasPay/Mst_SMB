package com.mfino.cashout;

/**
 * 
 */

import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.BillPaymentsDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX.CMThirdPartyCashOut;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.result.XMLResult;
import com.mfino.service.MfinoService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PartnerService;
import com.mfino.service.SMSService;
import com.mfino.service.impl.SystemParametersServiceImpl;
import com.mfino.transactionapi.handlers.wallet.ReversalFromATMHandler;
import com.mfino.transactionapi.handlers.wallet.WithdrawFromATMHandler;

/**
 * @author Bala Sunku
 * 
 */
public class CashoutMessageListener implements Processor {

	private Logger	log	= LoggerFactory.getLogger(CashoutMessageListener.class);
	private static final String WITHDRAWAL_REQUEST_MSG_TYPE = "0200";
	private static final String REVERSAL_REQUEST_MSG_TYPE = "0420";
	private static final String REVERSAL_ADDLN_REQUEST_MSG_TYPE = "0421";
	
	private SMSService smsService;
	private MfinoService mfinoService;
	private PartnerService partnerService;
	private NotificationService notificationService;
	private ReversalFromATMHandler reversalFromATMHandler;
	private WithdrawFromATMHandler withdrawFromATMHandler;
	private NotificationMessageParserService notificationMessageParserService;




	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void process(Exchange exchange) throws Exception {

		CMThirdPartyCashOut thirdPartyCashOut = exchange.getIn().getBody(CMThirdPartyCashOut.class);

		String syncId = exchange.getIn().getHeader("synchronous_request_id").toString();
		String cashinChannel = exchange.getIn().getHeader("FrontendID").toString();

		// Save the Request Data sent from Interswitch
		BillPaymentsDAO bpDAO = DAOFactory.getInstance().getBillPaymentDAO();
		BillPayments bp = new BillPayments();
		bp.setSourcemdn(thirdPartyCashOut.getSourceMDN());
		bp.setAmount(thirdPartyCashOut.getAmount());
		bp.setIntxnid(thirdPartyCashOut.getTxnReferenceId());
		bp.setOriginalintxnid(thirdPartyCashOut.getOriginalTxnReferenceId());
		bp.setInfo1(thirdPartyCashOut.getOneTimePassCode());
		bp.setInfo2(thirdPartyCashOut.getCATerminalId());
		bpDAO.save(bp);
		thirdPartyCashOut.setAccountNumber(getATMAccountNumber(thirdPartyCashOut.getCATerminalId()));
		thirdPartyCashOut.setTransactionIdentifier((String)exchange.getIn().getHeader(MCEUtil.BREADCRUMB_ID));
		
		XMLResult result = null;
		if (WITHDRAWAL_REQUEST_MSG_TYPE.equals(thirdPartyCashOut.getMessageTypeIndicator())) {
			result = (XMLResult) withdrawFromATMHandler.handle(thirdPartyCashOut);
		}
		else if (REVERSAL_REQUEST_MSG_TYPE.equals(thirdPartyCashOut.getMessageTypeIndicator()) ||
				REVERSAL_ADDLN_REQUEST_MSG_TYPE.equals(thirdPartyCashOut.getMessageTypeIndicator())) {
			// Getting the Source MDN and FAC details based on the origina; Txn Ref Id
			BillPaymentsQuery bpQuery = new BillPaymentsQuery();
			bpQuery.setIntegrationTxnRefId(thirdPartyCashOut.getOriginalTxnReferenceId());
			List<BillPayments> billPayments = bpDAO.get(bpQuery);
			if (CollectionUtils.isNotEmpty(billPayments)) {
				bp = billPayments.get(0);
				thirdPartyCashOut.setSourceMDN(bp.getSourcemdn());
				thirdPartyCashOut.setOneTimePassCode(bp.getInfo1());
			}
			thirdPartyCashOut.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_REVERSE_FROM_ATM);
			result = (XMLResult) reversalFromATMHandler.handle(thirdPartyCashOut);
			
		}
		
		String message = null;
		try {
			result.setNotificationMessageParserService(notificationMessageParserService);
			result.setMfinoService(mfinoService);
			result.setPartnerService(partnerService);
			result.setNotificationService(notificationService);

			result.buildMessage();
			StringBuilder sb = new StringBuilder("(");
			sb.append(result.getXMlelements().get("code"));
			sb.append(")");
			sb.append(result.getXMlelements().get("message"));
			message = sb.toString();
		}
		catch (XMLStreamException e) {
			log.error("Error While parsing the Result...", e);
		}

		log.info("SMS : " + thirdPartyCashOut.getSourceMDN() + "-->" + message);

		smsService.setDestinationMDN(thirdPartyCashOut.getSourceMDN());
		smsService.setMessage(message);
		smsService.setNotificationCode(result.getNotificationCode());
		smsService.setSctlId(result.getSctlID());
		if(StringUtils.isNotBlank(thirdPartyCashOut.getTransactionIdentifier())){
			smsService.setTransactionIdentifier(thirdPartyCashOut.getTransactionIdentifier());
		}
		smsService.asyncSendSMS();

		Map<String, Object> headers = exchange.getIn().getHeaders();
		headers.clear();

		headers.put("synchronous_request_id", syncId);
		headers.put("FrontendID", cashinChannel);
		
		MCEUtil.setBreadCrumbId(headers, thirdPartyCashOut.getTransactionIdentifier());		

		exchange.getIn().setBody(result.getNotificationCode().toString());

	}
	
	private String getATMAccountNumber(String terminalId) {
		String result = null;
		SystemParametersServiceImpl systemParametersServiceImpl = new SystemParametersServiceImpl();
		String terminalPrefix = systemParametersServiceImpl.getString(SystemParameterKeys.ATM_TERMINAL_PREFIX_CODE);
		String defaultAcctNum = systemParametersServiceImpl.getString(SystemParameterKeys.ATM_TERMINAL_DEFAULT_ACCOUNT_NUMBER);
		
		if ( StringUtils.isNotBlank(terminalId) && (StringUtils.isNotBlank(terminalPrefix)) && (terminalId.startsWith(terminalPrefix) )) {
			result = defaultAcctNum.substring(0, 7) + terminalId.substring(4, 7) + defaultAcctNum.substring(10, 17) + terminalId.substring(7, 8); 
		}
		
		return result;
	}
	public NotificationMessageParserService getNotificationMessageParserService() {
		return notificationMessageParserService;
	}
	
	public void setNotificationMessageParserService(
			NotificationMessageParserService notificationMessageParserService) {
		this.notificationMessageParserService = notificationMessageParserService;
	}
	
	public MfinoService getMfinoService() {
		return mfinoService;
	}
	
	public void setMfinoService(MfinoService mfinoService) {
		this.mfinoService = mfinoService;
	}
	
	public PartnerService getPartnerService() {
		return partnerService;
	}
	
	public void setPartnerService(PartnerService partnerService) {
		this.partnerService = partnerService;
	}
	
	
	
	public NotificationService getNotificationService() {
		return notificationService;
	}
	
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
	public SMSService getSmsService() {
		return smsService;
	}
	
	public void setSmsService(SMSService smsService) {
		this.smsService = smsService;
	}
	
	public ReversalFromATMHandler getReversalFromATMHandler() {
		return reversalFromATMHandler;
	}
	
	public void setReversalFromATMHandler(
			ReversalFromATMHandler reversalFromATMHandler) {
		this.reversalFromATMHandler = reversalFromATMHandler;
	}
	
	public WithdrawFromATMHandler getWithdrawFromATMHandler() {
		return withdrawFromATMHandler;
	}
	
	public void setWithdrawFromATMHandler(
			WithdrawFromATMHandler withdrawFromATMHandler) {
		this.withdrawFromATMHandler = withdrawFromATMHandler;
	}
}
