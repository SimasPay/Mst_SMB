package com.mfino.zenith.billpay.mfinows.communicators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.core.ws.WSCommunicator;
import com.mfino.zenith.billpay.mfinows.MfinoWSConstants;
//import com.mfino.zenith.airtime.visafone.beans.PurchaseAirtime;
import com.mfino.zenith.billpay.mfinows.communicators.MfinoWSResponse;
import com.mfino.zenith.billpay.mfinows.communicators.MfinoWSResponseParser;
/**
 * @author Satya
 * 
 */

public class ProcessRequestCommunicator extends WSCommunicator{
	private Map<String, String>	params;
	private BillPaymentsService	billPaymentsService;

	@Override
 	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<Object> getParameterList(MCEMessage mceMessage) {
		log.info("ProcessRequestCommunicator:: getParameterList mceMessage=" + mceMessage);

		CMBase requestFix = (CMBase) mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();

		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDao.getById(sctlId);

		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);

		BigDecimal bd = billPayments.getAmount();
		BigDecimal amount = bd;
		
		String custID = billPayments.getInvoiceNumber();
		String txnRef = "" + sctl.getID();
		String merchantCode = billPayments.getBillerCode();//params.get(MfinoWSConstants.MERCHANTID_KEY);
				
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(txnRef);
		paramList.add(merchantCode);
		paramList.add(amount);
		paramList.add(custID);

		return paramList;
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> wsResponse, MCEMessage requestMceMessage) {
		// Object wsResponseElement = wsResponse.get(0);
		log.info("ProcessRequestCommunicator :: constructReplyMessage wsResponseElement=" + wsResponse + " requestMceMessage=" + requestMceMessage);
		MCEMessage responseMceMessage = new MCEMessage();

		CMBase requestFix = (CMBase) requestMceMessage.getRequest();

		BillPayResponse billPayResponse = new BillPayResponse();
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());

		if (wsResponse == null) {
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			log.info("ProcessRequestCommunicator :: Service Unavailable");
		}
		else if (wsResponse instanceof List && wsResponse.get(0).equals(MCEUtil.SERVICE_UNAVAILABLE)) {
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			log.info("ProcessRequestCommunicator :: Service Unavailable");
		}
		else {
			MfinoWSResponse response;
			try {
				response = MfinoWSResponseParser.parse(wsResponse);
				billPayResponse.setInResponseCode(response.retn);
				billPayResponse.setDescription(response.desc);

				if (MfinoWSConstants.OPERATION_SUCCESS.equals(response.retn)) {
					billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
					billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
				}
				else {
					billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
					billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
				}
			}
			catch (Exception ex) {
				log.error("received unparseable xml from mfino webservice");
				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
				billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
				billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			}
		}

		responseMceMessage.setRequest(requestMceMessage.getRequest());
		responseMceMessage.setResponse(billPayResponse);
		return responseMceMessage;
	}

	@Override
	public String getMessageName(MCEMessage msg) {
		return "ProcessRequest";
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}

	public String deNormalizeMDN(String MDN) {

		int start = 0;
		if (StringUtils.isBlank(MDN))
			return StringUtils.EMPTY;

		MDN = MDN.trim();

		while (start < MDN.length()) {
			if ('0' == MDN.charAt(start))
				start++;
			else
				break;
		}

		if (MDN.startsWith("234", start)) {
			start += "234".length();
		}

		return "0" + MDN.substring(start);
	}
}
