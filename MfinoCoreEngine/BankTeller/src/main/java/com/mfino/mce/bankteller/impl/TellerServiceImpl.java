package com.mfino.mce.bankteller.impl;

import static com.mfino.mce.core.util.MCEUtil.isNullorZero;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashIn;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashInConfirm;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashOut;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashOutConfirm;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMTellerPendingCommodityTransferRequest;
import com.mfino.mce.backend.CommodityTransferService;
import com.mfino.mce.backend.impl.BackendRuntimeException;
import com.mfino.mce.backend.impl.BackendServiceDefaultImpl;
import com.mfino.mce.bankteller.TellerBackendResponse;
import com.mfino.mce.bankteller.TellerBankService;
import com.mfino.mce.bankteller.TellerPendingClearanceService;
import com.mfino.mce.bankteller.TellerService;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;

/**
 * @author Maruthi
 *
 */
public class TellerServiceImpl extends BackendServiceDefaultImpl implements TellerService {

	private TellerBankService tellerBankService;
	private TellerPendingClearanceService tellerPendingClearanceService;
	
	private CommodityTransferService commodityTransferService;

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage processMessage(MCEMessage mceMessage) throws BackendRuntimeException {
		log.info("TellerServiceImpl :: processMessage() BEGIN "+mceMessage);
		if(tellerBankService==null){
			log.error("Error in TellerServiceImpl, TellerBankService not set");
			TellerBackendResponse response = (TellerBackendResponse) createResponseObject();
			response.setInternalErrorCode(CmFinoFIX.NotificationCode_DependencyInjectionError);
			mceMessage.setResponse(response);
			return mceMessage;
		}
		try
		{
			CFIXMsg returnFix = preProcess(mceMessage);
			CMBase baseMessage =  getBaseMessage(mceMessage);
			CFIXMsg requestFix = (CFIXMsg)mceMessage.getRequest();
			CFIXMsg responseFix = (CFIXMsg)mceMessage.getResponse();

			if(isNullorZero(((BackendResponse)returnFix).getInternalErrorCode())){

				if(baseMessage instanceof CMBankTellerCashIn){
					CMBankTellerCashIn bankTellerCashIn = (CMBankTellerCashIn)baseMessage; 	
					returnFix = getTellerBankService().onTellerCashIn(bankTellerCashIn);	
				}
				else if(baseMessage instanceof CMBankTellerTransferInquiryFromBank){					
					returnFix = getTellerBankService().onTellerTransferInquiryFromBank((CMBankTellerTransferInquiryToBank)requestFix, (CMBankTellerTransferInquiryFromBank)responseFix);
				}
				else if(baseMessage instanceof CMBankTellerMoneyTransferFromBank){					
					returnFix = getTellerBankService().onTellerTransferConfirmationFromBank((CMBankTellerMoneyTransferToBank)requestFix, (CMBankTellerMoneyTransferFromBank)responseFix);
				}
				else if(baseMessage instanceof CMBankTellerCashInConfirm){
					CMBankTellerCashInConfirm bankTellerCashInConfirm = (CMBankTellerCashInConfirm)baseMessage; 					
					returnFix = getTellerBankService().onTellerCashInConfirm(bankTellerCashInConfirm);	
				}
				else if(baseMessage instanceof CMBankTellerCashOut){
					CMBankTellerCashOut bankTellerCashOut = (CMBankTellerCashOut)baseMessage; 	
					returnFix = getTellerBankService().onTellerCashOut(bankTellerCashOut);	
				}
				else if(baseMessage instanceof CMBankTellerCashOutConfirm){
					CMBankTellerCashOutConfirm bankTellerCashOutConfirm = (CMBankTellerCashOutConfirm)baseMessage; 	
					returnFix = getTellerBankService().onTellerCashOutConfirmation(bankTellerCashOutConfirm);	
				}
				else if(baseMessage instanceof CMTellerPendingCommodityTransferRequest){
					returnFix = tellerPendingClearanceService.processMessage((CMTellerPendingCommodityTransferRequest)baseMessage);	
				}
				else
				{
					log.error("got an invalid message "+requestFix.DumpFields());
					((BackendResponse)returnFix).setInternalErrorCode(NotificationCodes.InternalSystemError.getInternalErrorCode());  
				}
			}

			mceMessage = getResponse(mceMessage, returnFix);
		}
		catch(Exception e){
			log.error("Error in TellerService ", e);
		}
		
		if(mceMessage.getResponse() != null){
			log.debug("Return FIX "+mceMessage.getResponse().DumpFields());
		}
		log.info("TellerServiceImpl :: processMessage() END return messsage="+mceMessage);
		return mceMessage;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BackendResponse createResponseObject() 
	{
		return new TellerBackendResponse();
	}

	/**
	 * @param commodityTransferService the commodityTransferService to set
	 */
	public void setCommodityTransferService(CommodityTransferService commodityTransferService) {
		this.commodityTransferService = commodityTransferService;
	}

	/**
	 * @return the commodityTransferService
	 */
	public CommodityTransferService getCommodityTransferService() {
		return commodityTransferService;
	}

	public TellerBankService getTellerBankService() {
		return tellerBankService;
	}

	public void setTellerBankService(TellerBankService tellerBankService) {
		this.tellerBankService = tellerBankService;
	}

	public TellerPendingClearanceService getTellerPendingClearanceService() {
		return tellerPendingClearanceService;
	}

	public void setTellerPendingClearanceService(
			TellerPendingClearanceService tellerPendingClearanceService) {
		this.tellerPendingClearanceService = tellerPendingClearanceService;
	}
}
