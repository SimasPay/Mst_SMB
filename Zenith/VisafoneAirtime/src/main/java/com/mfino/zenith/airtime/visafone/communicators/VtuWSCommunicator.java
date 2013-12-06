//package com.mfino.zenith.airtime.visafone.communicators;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import com.mfino.dao.DAOFactory;
//import com.mfino.dao.ServiceChargeTransactionLogDAO;
//import com.mfino.domain.BillPayments;
//import com.mfino.domain.ServiceChargeTransactionLog;
//import com.mfino.fix.CmFinoFIX;
//import com.mfino.fix.CmFinoFIX.CMBase;
//import com.mfino.gt.billpayments.beans.BillPayResponse;
//import com.mfino.gt.billpayments.service.BillPaymentsService;
//import com.mfino.mce.core.MCEMessage;
//import com.mfino.mce.core.util.MCEUtil;
//import com.mfino.mce.core.ws.WSCommunicator;
//import com.mfino.zenith.airtime.visafone.VAConstants;
//import com.mfino.zenith.airtime.visafone.beans.PurchaseAirtime;
//
//public class VtuWSCommunicator extends WSCommunicator{
//	private Map<String, String> params;
//	private BillPaymentsService billPaymentsService;
//	
//	@Override
//	public List<Object> getParameterList(MCEMessage mceMessage) {
//		log.info("VtuWSCommunicator:: getParameterList mceMessage="+mceMessage);
//		openSession();
//		List<Object> parameterList = new ArrayList<Object>();
//		
//		CMBase requestFix = (CMBase)mceMessage.getRequest();
//		Long sctlId = requestFix.getServiceChargeTransactionLogID();
//		
//		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
//		ServiceChargeTransactionLog sctl = sctlDao.getById(sctlId);
//		
//		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
//		
//		PurchaseAirtime pa = new PurchaseAirtime();
//		pa.setAdditionalInfo("0");
//		pa.setAppId(params.get(VAConstants.APPID_KEY));
//		pa.setCustomerId(sctl.getSourceMDN());
//		pa.setId(sctl.getID().toString());
//		pa.setMerchandID(params.get(VAConstants.MERCHANTID_KEY));
//		pa.setProductId(billPayments.getPartnerBillerCode());
//		pa.setQuantity(billPayments.getAmount());
//		
//		log.info("VtuWSCommunicator :: PurchaseAirtime bean="+pa.toXML());
//		parameterList.add(pa.toXML());
//		
//		closeSession();
//		
//		return parameterList;
//	}
//
//	@Override
//	public MCEMessage constructReplyMessage(List<Object> wsResponse, MCEMessage requestMceMessage) {
//		Object wsResponseElement = wsResponse.get(0);
//		openSession();
//		log.info("VtuWSCommunicator :: constructReplyMessage wsResponseElement="+wsResponseElement+" requestMceMessage="+requestMceMessage);
//		MCEMessage responseMceMessage = new MCEMessage();
//		
//		CMBase requestFix = (CMBase)requestMceMessage.getRequest();
//		
//		BillPayResponse billPayResponse = new BillPayResponse();
//		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
//
//		if(!(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE))){
//			VAResponse response = VAResponseParser.parse((String)wsResponseElement);
//			
//			billPayResponse.setInResponseCode(response.retn);
//			billPayResponse.setDescription(response.desc);
//			
//			if(VAConstants.OPERATION_SUCCESS.equals(response.retn)){
//				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
//				billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
//			}
//			else{
//				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
//				billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
//			}
//		}
//		else{
//			billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
//		}
//		
//		responseMceMessage.setRequest(requestMceMessage.getRequest());
//		responseMceMessage.setResponse(billPayResponse);
//		closeSession();
//		return responseMceMessage;
//	}
//
//	@Override
//	public String getMessageName(MCEMessage mceMessage) {
//		return "SendBillPaymentAdvice";
//	}
//
//	public Map<String, String> getParams() {
//		return params;
//	}
//
//	public void setParams(Map<String, String> params) {
//		this.params = params;
//	}
//
//	public BillPaymentsService getBillPaymentsService() {
//		return billPaymentsService;
//	}
//
//	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
//		this.billPaymentsService = billPaymentsService;
//	}
//	
//	
//}
