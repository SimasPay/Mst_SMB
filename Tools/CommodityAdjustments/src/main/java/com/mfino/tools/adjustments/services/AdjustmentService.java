package com.mfino.tools.adjustments.services;

import static com.mfino.fix.CmFinoFIX.ServletPath_BankAccount;
import static com.mfino.fix.CmFinoFIX.ServletPath_Subscribers;
import static com.mfino.fix.CmFinoFIX.ServletPath_WebAppFEForSubscribers;
import static com.mfino.mce.core.util.MCEUtil.isNullOrEmpty;
import static com.mfino.mce.core.util.MCEUtil.isNullorZero;

import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.mce.backend.impl.BackendRuntimeException;
import com.mfino.mce.backend.impl.BackendServiceDefaultImpl;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.MessageTypes;
import com.mfino.mce.core.util.NotificationCodes;

public class AdjustmentService extends BackendServiceDefaultImpl {

	private static Logger	log	= LoggerFactory.getLogger(AdjustmentService.class);

	@Handler
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage processMessage(MCEMessage mceMessage) throws BackendRuntimeException {

		log.info("AdjustmentService processMessage {}", mceMessage);
		
		CFIXMsg returnFix=null;
		try {
			CMBase baseMessage = getBaseMessage(mceMessage);
			CFIXMsg requestFix = (CFIXMsg) mceMessage.getRequest();
			CFIXMsg responseFix = (CFIXMsg) mceMessage.getResponse();

			log.info("AdjustmentService :: processMessage baseMessage.DumpFields(): {}", baseMessage.DumpFields());

			returnFix = preProcess(mceMessage);

			if (isNullorZero(((BackendResponse) returnFix).getInternalErrorCode())) {

				if (baseMessage instanceof CMBankAccountToBankAccount) {
					returnFix = bankService.onTransferInquiryToBank((CMBankAccountToBankAccount) baseMessage);
				}
				else if (baseMessage instanceof CMTransferInquiryFromBank) {
					returnFix = bankService.onTransferInquiryFromBank((CMTransferInquiryToBank) requestFix, (CMTransferInquiryFromBank) responseFix);
				}
				else if (baseMessage instanceof CMBankAccountToBankAccountConfirmation) {
					returnFix = bankService.onTransferConfirmationToBank((CMBankAccountToBankAccountConfirmation) baseMessage);
				}
				else if (baseMessage instanceof NoISOResponseMsg) {
					if (requestFix instanceof CMMoneyTransferToBank)
						returnFix = bankService.onTransferReversalToBank((CMMoneyTransferToBank) requestFix, (NoISOResponseMsg) responseFix);
				}
				else if (baseMessage instanceof CMMoneyTransferReversalFromBank) {
					returnFix = bankService.onTransferReversalFromBank((CMMoneyTransferToBank) requestFix,
					        (CMMoneyTransferReversalFromBank) responseFix);
				}
				else if (baseMessage instanceof CMMoneyTransferFromBank) {
					returnFix = bankService.onTransferConfirmationFromBank((CMMoneyTransferToBank) requestFix, (CMMoneyTransferFromBank) responseFix);
				}
				else {
					log.error("got an invalid message {}", requestFix.DumpFields());
					((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.InternalSystemError.getInternalErrorCode());
				}
			}

			
		}
		catch(Exception e){
			if(returnFix instanceof BackendResponse){
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
			log.error("Error in AdjustmentService ", e);
		}
		mceMessage = getResponse(mceMessage, returnFix);

		log.info("completed AdjustmentService {}", mceMessage.getResponse());

		if (mceMessage.getResponse() != null) {
			log.info("Return FIX " + mceMessage.getResponse().DumpFields());
		}

		return mceMessage;

	}

	@Override
	public BackendResponse preProcess(MCEMessage mceMessage) throws BackendRuntimeException {
		log.info("BackendServiceDefaultImpl :: preProcess() BEGIN");
		BackendResponse backendResponse = createResponseObject();
		CMBase baseMessage = getBaseMessage(mceMessage);

		backendResponse = validationService.validateFixMessage(MessageTypes.getMessageCode(baseMessage), baseMessage);

		if (isNullorZero(((BackendResponse) backendResponse).getInternalErrorCode())) {

			if (!baseMessage.checkRequiredFields()) {
				log.warn("required feilds are missing in fix message, possibly a bug in code, stopping processing for this request baseMessage="
				        + baseMessage);
				backendResponse.setInternalErrorCode(NotificationCodes.RequiredParametersMissing.getInternalErrorCode());
			}
		}

		if (!isNullOrEmpty(baseMessage.getServletPath())) {
			if ((baseMessage.getServletPath().equals(ServletPath_Subscribers)) || (baseMessage.getServletPath().equals(ServletPath_BankAccount))
			        || (baseMessage.getServletPath().equals(ServletPath_WebAppFEForSubscribers))) {

				baseMessage.setMessageType(MessageTypes.getMessageCode(baseMessage));
			}
		}

		log.info("BackendServiceDefaultImpl :: preProcess() END");
		return backendResponse;
	}

}