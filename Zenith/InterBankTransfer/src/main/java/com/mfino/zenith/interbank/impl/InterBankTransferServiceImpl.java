package com.mfino.zenith.interbank.impl;

import static com.mfino.mce.core.util.MCEUtil.isNullorZero;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransfer;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferInquiry;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferStatus;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankPendingCommodityTransferRequest;
import com.mfino.mce.backend.impl.BackendServiceDefaultImpl;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.zenith.interbank.IBTBankService;
import com.mfino.zenith.interbank.InterBankPendingClearanceService;
import com.mfino.zenith.interbank.InterBankTransferService;

/**
 * @author Sasi
 *
 */
public class InterBankTransferServiceImpl extends BackendServiceDefaultImpl implements InterBankTransferService {

	private IBTBankService ibtBankService;
	
	private InterBankPendingClearanceService interBankPendingClearanceService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage processMessage(MCEMessage mceMessage) {
		log.info("InterBankTransferServiceImpl :: processMessage() BEGIN");
		
		if(mceMessage == null) return null;
		
		try
		{
			CFIXMsg returnFix = preProcess(mceMessage);
			CMBase baseMessage =  getBaseMessage(mceMessage);
			CFIXMsg requestFix = (CFIXMsg)mceMessage.getRequest();
			CFIXMsg responseFix = (CFIXMsg)mceMessage.getResponse();

			if(isNullorZero(((BackendResponse)returnFix).getInternalErrorCode())){

				if(baseMessage instanceof CMInterBankFundsTransferInquiry){
					log.debug("baseMessage instanceof CMInterBankFundsTransferInquiry");
					returnFix = ibtBankService.onTransferInquiry((CMInterBankFundsTransferInquiry)requestFix);
				}
				else if(baseMessage instanceof CMInterBankFundsTransfer){
					log.debug("baseMessage instanceof CMInterBankFundsTransfer");
					returnFix = ibtBankService.onTransferConfirmation((CMInterBankFundsTransfer)requestFix);
				}
/*				TODO delete
 * 					else if(baseMessage instanceof CMInterBankFundsTransferStatus){
					log.debug("baseMessage instanceof CMInterBankFundsTransferStatus");
					returnFix = ibtBankService.onInterBankFundsTransferStatus((CMInterBankFundsTransferStatus)requestFix);
				}*/
				else if(baseMessage instanceof CMInterBankMoneyTransferFromBank){
					log.debug("baseMessage instanceof CMInterBankMoneyTransferFromBank");
					/*
					 * Got reply form web service, now need to do pending stuff for confirmationFromBank.
					 */
					returnFix = ibtBankService.onResponseFromInterBankService((CMInterBankMoneyTransferToBank)requestFix, (CMInterBankMoneyTransferFromBank)baseMessage);
				}
				else if(baseMessage instanceof CMInterBankPendingCommodityTransferRequest){
					log.debug("baseMessage instanceof CMInterBankPendingCommodityTransferRequest");
					returnFix = interBankPendingClearanceService.processMessage((CMInterBankPendingCommodityTransferRequest)baseMessage);
				}
				else if(baseMessage instanceof IBTBackendResponse){
					log.debug("baseMessage instanceof IBTBackendResponse");
					returnFix = ibtBankService.onGetTxnStatusFromIBTService((CMInterBankFundsTransferStatus)requestFix, (IBTBackendResponse)baseMessage);
				}
				else if(baseMessage instanceof CMInterBankFundsTransferStatus){
					log.debug("baseMessage instanceof CMInterBankFundsTransferStatus");
					returnFix = baseMessage;
				}
			}
			
			mceMessage = getResponse(mceMessage, returnFix);
		}
		catch(Exception e){
			log.error("Error in InterBankTransferServiceImpl ", e);
		}
		
		if(mceMessage.getResponse() != null){
			log.debug("InterBankTransferServiceImpl Return FIX "+mceMessage.getResponse().DumpFields());
		}
		
		log.info("InterBankTransferServiceImpl :: processMessage() END");
		
		return mceMessage;
	}

	public IBTBankService getIbtBankService() {
		return ibtBankService;
	}

	public void setIbtBankService(IBTBankService ibtBankService) {
		this.ibtBankService = ibtBankService;
	}

	public InterBankPendingClearanceService getInterBankPendingClearanceService() {
		return interBankPendingClearanceService;
	}

	public void setInterBankPendingClearanceService(InterBankPendingClearanceService interBankPendingClearanceService) {
		this.interBankPendingClearanceService = interBankPendingClearanceService;
	}
}
