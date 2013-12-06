package com.mfino.smart.gateway.impl;

import static com.mfino.mce.core.util.MCEUtil.SERVICE_TIME_OUT;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.impl.BillPayBillerIntegrationServiceImpl;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.mce.core.MCEMessage;
import com.mfino.smart.gateway.service.definition.GatewayIntegrationService;

public class GatewayIntegrationServiecImpl extends BillPayBillerIntegrationServiceImpl implements GatewayIntegrationService{

	private static Logger log = LoggerFactory.getLogger(GatewayIntegrationServiecImpl.class);
	
	@Override
    public MCEMessage handleBillPayAdviceResponse(MCEMessage mceMessage) {
		log.info(""+mceMessage);

		BillPayResponse response = (BillPayResponse)mceMessage.getResponse();
		
		Long sctlId = response.getServiceChargeTransactionLogID();
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		if(SERVICE_TIME_OUT.equals(response.getInResponseCode())){
			billPayments.setINResponseCode(SERVICE_TIME_OUT);
			billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_ADVICE_PENDING);
		}
		else
		{
			if(CmFinoFIX.ResponseCode_Success.equals(response.getResponse())){
				billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_ADVICE_COMPLETED);
			}
			else{
				billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_ADVICE_FAILED);
			}
			billPayments.setINTxnId(response.getInTxnId());
			billPayments.setINResponseCode(response.getInResponseCode());
			
			billPayments.setResponseCode(response.getResponse());
		}
		
		billPaymentsService.saveBillPayment(billPayments);
		
		mceMessage.setResponse(response);
		
		return mceMessage;

    }

	@Override
    public MCEMessage preBillerAdvice(@Header("mceMessage") MCEMessage mceMessage) {
		log.info(mceMessage+"");

		CMBase response = (CMBase)mceMessage.getResponse();
		
		Long sctlId = response.getServiceChargeTransactionLogID();
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_ADVICE_REQUESTED);
		billPaymentsService.saveBillPayment(billPayments);
		
		return mceMessage;

	}

	@Override
    public MCEMessage handleBillPayReversalResponse(MCEMessage mceMessage) {
		log.info(""+mceMessage);

		BillPayResponse response = (BillPayResponse)mceMessage.getResponse();
		
		Long sctlId = response.getServiceChargeTransactionLogID();
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		
		if(SERVICE_TIME_OUT.equals(response.getInResponseCode())){
			billPayments.setINResponseCode(SERVICE_TIME_OUT);
			billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_REVERSAL_PENDING);
		}
		else
		{
			if(CmFinoFIX.ResponseCode_Success.equals(response.getResponse())){
				billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_REVERSAL_COMPLETED);
			}
			else{
				billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_REVERSAL_FAILED);
			}
			billPayments.setINTxnId(response.getInTxnId());
			billPayments.setINResponseCode(response.getInResponseCode());
			
			billPayments.setResponseCode(response.getResponse());
		}
		
		billPaymentsService.saveBillPayment(billPayments);
		
		mceMessage.setResponse(response);
		
		return mceMessage;
    }

	@Override
    public MCEMessage preBillerReversal(MCEMessage mceMessage) {
		log.info(""+mceMessage);

		CMBase response = (CMBase)mceMessage.getResponse();
		
		Long sctlId = response.getServiceChargeTransactionLogID();
		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
		billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_BILLER_REVERSAL_REQUESTED);
		billPaymentsService.saveBillPayment(billPayments);
		
		return mceMessage;
	}

	@Override
    public MCEMessage handleNoResponse(@Body Object body, @Header("mceMessage") Object mceMessage) {

		MCEMessage mceMsg = (MCEMessage)mceMessage;
		log.info("Response was not received for confirmation.Failing the request");
		
		CMBase base = (CMBase) mceMsg.getRequest();

		BillPayResponse billpayResponse = new BillPayResponse();
		billpayResponse.setServiceChargeTransactionLogID(base.getServiceChargeTransactionLogID());
		billpayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
		billpayResponse.setResult(CmFinoFIX.ResponseCode_Failure);

		mceMsg.setResponse(billpayResponse);
		
		return mceMsg;
	
	}

}
