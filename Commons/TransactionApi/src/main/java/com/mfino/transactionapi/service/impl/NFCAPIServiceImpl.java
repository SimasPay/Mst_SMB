package com.mfino.transactionapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.result.XMLResult;
import com.mfino.transactionapi.handlers.nfc.ModifyNFCCardAliasHandler;
import com.mfino.transactionapi.handlers.nfc.NFCCardLinkHandler;
import com.mfino.transactionapi.handlers.nfc.NFCCardTopupHandler;
import com.mfino.transactionapi.handlers.nfc.NFCCardTopupReversalHandler;
import com.mfino.transactionapi.handlers.nfc.NFCCardUnlinkHandler;
import com.mfino.transactionapi.handlers.nfc.impl.NFCPocketTopupHandlerImpl;
import com.mfino.transactionapi.handlers.nfc.impl.NFCPocketTopupInquiryHandlerImpl;
import com.mfino.transactionapi.handlers.nfc.impl.NFCTransactionsHistoryHandlerImpl;
import com.mfino.transactionapi.handlers.wallet.CheckBalanceHandler;
import com.mfino.transactionapi.service.BaseAPIService;
import com.mfino.transactionapi.service.NFCAPIService;
import com.mfino.transactionapi.service.TransactionRequestValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Bala Sunku
 *
 */
@Service("NFCAPIServiceImpl")
public class NFCAPIServiceImpl extends BaseAPIService implements NFCAPIService{


	@Autowired
	@Qualifier("TransactionRequestValidationServiceImpl")
	private TransactionRequestValidationService transactionRequestValidationService;
	
	@Autowired
	@Qualifier("CheckBalanceHandlerImpl")
	private CheckBalanceHandler checkBalanceHandler;
	
	@Autowired
	@Qualifier("NFCCardUnlinkHandlerImpl")
	private NFCCardUnlinkHandler nfcCardUnlinkHandler;
	
	@Autowired
	@Qualifier("NFCCardTopupHandlerImpl")
	private NFCCardTopupHandler nFCCardTopupHandler;
	
	@Autowired
	@Qualifier("NFCCardTopupReversalHandlerImpl")
	private NFCCardTopupReversalHandler nFCCardTopupReversalHandler;
	
	@Autowired
	@Qualifier("NFCCardLinkHandlerImpl")
	private NFCCardLinkHandler nfcCardLinkHandler;
	
	@Autowired
	@Qualifier("ModifyNFCCardAliasHandlerImpl")
	private ModifyNFCCardAliasHandler modifyNFCCardAliasHandlerImpl;
	
	@Autowired
	@Qualifier("NFCTransactionsHistoryHandlerImpl")
	private NFCTransactionsHistoryHandlerImpl nfcTransactionsHistoryHandlerImpl;
	
	@Autowired
	@Qualifier("NFCPocketTopupInquiryHandlerImpl")
	private NFCPocketTopupInquiryHandlerImpl nFCPocketTopupInquiryHandlerImpl;
	
	@Autowired
	@Qualifier("NFCPocketTopupHandlerImpl")
	private NFCPocketTopupHandlerImpl nFCPocketTopupHandlerImpl;
		
	@Override
	public XMLResult handleRequest(TransactionDetails transactionDetails) throws InvalidDataException {

		String transactionName = transactionDetails.getTransactionName();
		XMLResult xmlResult = null;

		if (ServiceAndTransactionConstants.TRANSACTION_NFC_POCKET_BALANCE.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateNFCPocketBalanceDetails(transactionDetails);
			
			transactionDetails.setSourcePIN("dummy");
			transactionDetails.setSourcePocketCode(CmFinoFIX.PocketType_NFC.toString());


			transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_NFC);

			xmlResult = (XMLResult) checkBalanceHandler.handle(transactionDetails);
		} else if(ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_UNLINK.equalsIgnoreCase(transactionName)) {
			if(CmFinoFIX.SourceApplication_CMS.toString().equals(transactionDetails.getChannelCode()))
			{
			transactionDetails.setSourcePIN("dummy");
		}
			transactionRequestValidationService.validateNFCCardUnlinkDetails(transactionDetails);
			xmlResult = (XMLResult) nfcCardUnlinkHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_TOPUP.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateNFCCardTopup(transactionDetails);	
			transactionDetails.setSourcePIN("dummy");
			transactionDetails.setSourcePocketCode(CmFinoFIX.PocketType_NFC.toString());
			transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_NFC);	
			xmlResult = (XMLResult) nFCCardTopupHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_TOPUP_REVERSAL.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateNFCCardTopupReversal(transactionDetails);		
			transactionDetails.setSourcePIN("dummy");
			transactionDetails.setSourcePocketCode(CmFinoFIX.PocketType_NFC.toString());
			transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_NFC);
			
			xmlResult = (XMLResult) nFCCardTopupReversalHandler.handle(transactionDetails);
		}else if (ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_LINK.equalsIgnoreCase(transactionName)) {
			if(CmFinoFIX.SourceApplication_CMS.toString().equals(transactionDetails.getChannelCode()))
			{
			transactionDetails.setSourcePIN("dummy");
		}
			transactionRequestValidationService.validateNFCCardLinkDetails(transactionDetails);
			xmlResult = (XMLResult) nfcCardLinkHandler.handle(transactionDetails);
			}
		else if (ServiceAndTransactionConstants.TRANSACTION_HISTORY.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateNFCTransactionHistoryDetails(transactionDetails);
			transactionDetails.setSourcePocketCode(CmFinoFIX.PocketType_NFC.toString());			
			xmlResult = (XMLResult) nfcTransactionsHistoryHandlerImpl.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_HISTORY_DETAILED_STATEMENT.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateNFCTxnHistoryDetailedStmtDetails(transactionDetails);
			transactionDetails.setSourcePocketCode(CmFinoFIX.PocketType_NFC.toString());			
			xmlResult = (XMLResult) nfcTransactionsHistoryHandlerImpl.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_EMAIL_HISTORY_AS_PDF.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateEmailNFCTxnHistoryAsPDFDetails(transactionDetails);
			transactionDetails.setSourcePocketCode(CmFinoFIX.PocketType_NFC.toString());			
			xmlResult = (XMLResult) nfcTransactionsHistoryHandlerImpl.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_DOWNLOAD_HISTORY_AS_PDF.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateDownloadNFCTxnHistoryAsPDFDetails(transactionDetails);
			transactionDetails.setSourcePocketCode(CmFinoFIX.PocketType_NFC.toString());			
			xmlResult = (XMLResult) nfcTransactionsHistoryHandlerImpl.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_NFC_POCKET_TOPUP_INQUIRY.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateNFCPocketTopupInquiry(transactionDetails);	
			xmlResult = (XMLResult) nFCPocketTopupInquiryHandlerImpl.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_NFC_POCKET_TOPUP.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateNFCPocketTopup(transactionDetails);		
			xmlResult = (XMLResult) nFCPocketTopupHandlerImpl.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_NFC_CARD_BALANCE.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateNFCCardBalanceDetails(transactionDetails);
			transactionDetails.setSourcePocketCode(CmFinoFIX.PocketType_NFC.toString());			
			xmlResult = (XMLResult) checkBalanceHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_MODIFY_NFC_CARD_ALIAS.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateModifyNFCCardAliasDetails(transactionDetails);
			transactionDetails.setSourcePocketCode(CmFinoFIX.PocketType_NFC.toString());			
			xmlResult = (XMLResult) modifyNFCCardAliasHandlerImpl.handle(transactionDetails);
		}

		return xmlResult;
	}
}
