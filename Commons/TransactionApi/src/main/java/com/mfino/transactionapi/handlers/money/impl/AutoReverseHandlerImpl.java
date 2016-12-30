/**
 * 
 */
package com.mfino.transactionapi.handlers.money.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.domain.AutoReversals;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAutoReversal;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.SCTLService;
import com.mfino.transactionapi.handlers.money.AutoReverseHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Bala Sunku
 *
 */
@Service("AutoReverseHandlerImpl")
public class AutoReverseHandlerImpl extends FIXMessageHandler implements AutoReverseHandler{
	private Logger log = LoggerFactory.getLogger(AutoReverseHandlerImpl.class);
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;

	public Result handle(TransactionDetails transactionDetails) {
		
		boolean isChargeReverseAlso = transactionDetails.isChargeReverseAlso();
		Long chargeRevFundPocket	= transactionDetails.getChargeRevFundPocket();

		CMAutoReversal autoReversal = new CMAutoReversal();
		
		ServiceChargeTxnLog sctl =sctlService.getBySCTLID(transactionDetails.getSctlId());
		CommodityTransfer ct = sctlService.getCTfromSCTL(sctl);
		
 		log.info("Reversing the Transaction SCTLId=" + sctl.getId() + " CTId="+ ct.getId() + " And CT Status=" + ct.getTransferstatus());

		XMLResult result = new TransferInquiryXMLResult();
		result.setTransactionTime(new Timestamp());
		
		if (CmFinoFIX.TransactionsTransferStatus_Completed.equals(ct.getTransferstatus())) {
			autoReversal = new CMAutoReversal();
			autoReversal.setSourcePocketID(ct.getPocket().getId());
			autoReversal.setDestPocketID(ct.getDestpocketid());
			autoReversal.setServiceChargeTransactionLogID(sctl.getId());
			autoReversal.setAmount(ct.getAmount());
			
			if (isChargeReverseAlso && (ct.getCharges().compareTo(ZERO) > 0) ) {
				autoReversal.setCharges(ct.getCharges());
			}
			else {
				autoReversal.setCharges(ZERO);
			}
			if ((chargeRevFundPocket != null) && (chargeRevFundPocket != -1)) {
				autoReversal.setChargesPocketID(chargeRevFundPocket);
			}
			result.setSourceMessage(autoReversal);
			CFIXMsg backEndResponse = super.process(autoReversal);
			TransactionResponse transactionResponse = checkBackEndResponse(backEndResponse);
			
			if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {

				AutoReversals ar = sctlService.getAutoReversalsFromSCTL(sctl);
				if (ar != null &&
					(CmFinoFIX.AutoRevStatus_TRANSIT_SRC_COMPLETED.equals(ar.getAutorevstatus()) || CmFinoFIX.AutoRevStatus_COMPLETED.equals(ar.getAutorevstatus()) ||
					 CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_INQ_FAILED.equals(ar.getAutorevstatus()) || CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_FAILED.equals(ar.getAutorevstatus()) ||
					 CmFinoFIX.AutoRevStatus_DEST_TRANSIT_INQ_FAILED.equals(ar.getAutorevstatus()) || CmFinoFIX.AutoRevStatus_DEST_TRANSIT_FAILED.equals(ar.getAutorevstatus()) ||
					 CmFinoFIX.AutoRevStatus_TRANSIT_SRC_INQ_FAILED.equals(ar.getAutorevstatus()) || CmFinoFIX.AutoRevStatus_TRANSIT_SRC_FAILED.equals(ar.getAutorevstatus())) ) {
					log.info("Auto Reverse of Transaction: " + sctl.getId() + " for amount " + sctl.getTransactionamount() + 
							" Is Initiated and Transfer Id is : " + transactionResponse.getTransferId());
					commodityTransferService.addCommodityTransferToResult(result, transactionResponse.getTransferId());
					result.setNotificationCode(CmFinoFIX.NotificationCode_AutoReverseSuccess);
				}
				else {
					log.info("Auto Reverse of Transaction: " + sctl.getId() + " for amount " + sctl.getTransactionamount() + " Is failed");
					result.setNotificationCode(CmFinoFIX.NotificationCode_AutoReverseFailed);
				}
			} 
			else {
				// Transfer in Pending state
				log.info("Auto Reverse of Transaction: " + sctl.getId() + " for amount " + sctl.getTransactionamount() + " Is Pending");
			}
		}
		else {
			//TODO for Wrong Auto Reverse Request
		}
		return result;
	}
}
