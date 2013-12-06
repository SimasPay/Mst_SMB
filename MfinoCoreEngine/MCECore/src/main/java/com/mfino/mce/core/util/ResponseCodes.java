package com.mfino.mce.core.util;

import java.util.HashSet;
import java.util.Set;

/**
 *  Mapping for external response code, messageType and internal error code.
 * @author sasidhar
 *
 */
public enum ResponseCodes {
	
	ISO_ResponseCode_Success(1,"00",0),
//	bank_InvalidKCVSpecified(1,"M1",11001), 
//	bank_InvalidWorkingKeyLength(1,"M2",11002),
//	bank_InvalidMasterKey(1,"M3",11003),
//	bank_DoNotHonor(1,"05",11005),
//	bank_XLinkGeneralFailure(1,"06",11006),
//	bank_RequestInProgress(1,"09",11009),
//	bank_InvalidTransactionRequest(1,"12",11012),
//	bank_InvalidTransactionAmount(1,"13",11013),
//	bank_InvalidCardNumber(1,"14",11014),
//	bank_SuspectedMalfunction(1,"22",11022),
//	bank_RecordNotFound(1,"25",11025),
//	bank_DuplicateRecord(1,"26",11026),
////	bank_InvalidPINBlock(1,"30",10030),
//	bank_RequestedFunctionNotSuppported(1,"40",11040), 
//	bank_InsufficientFunds(1,"51",11051),
//	bank_NoCheckingAccount(1,"52",11052),
//	bank_NoSavingAccount(1,"53",11053),
//	bank_CardHasExpired(1,"54",11054),
//	bank_Incorrect_PIN(1,"55",11055),
//	bank_TransactionNotPermitted(1,"57",11057),
//	bank_SuspectedFraud(1,"59",11059),
//	bank_ExceedsWithdrawlLimit(1,"61",11061),
//	bank_RestrictedCard(1,"62",11062),
//	bank_SecurityViolation(1,"63",11063),
//	bank_ExceedsWithdrawlFrequency(1,"65",11065),
////	bank_ResponseToLate(1,"68",11068),
//	bank_MaxWrongPINRetries(1,"75",11075),
////	bank_NoPaymentDue(1,"88",11088),
////	bank_DestinationProcessorNotAvailable(1,"91",11091),
//	bank_RoutingError(1,"92",11092),
//	bank_ViolationOfLaw(1,"93",11093),
//	bank_DuplicateTransaction(1,"94",11094),
//	bank_ReconcileError(1,"95",11095),
//	bank_SystemError(1,"96" ,11096),
//	bank_ExceedsCashLimit(1,"98",11098),
	bank_Failure(1,"39" ,10089);
	
	ResponseCodes(Integer messageType, String externalResponseCode, Integer internalErrorCode){
		this.messageType = messageType;
		this.externalResponseCode = externalResponseCode;
		this.internalErrorCode = internalErrorCode;
	}
	
	private static Set<ResponseCodes> responseCodes = null;
	
	private Integer messageType;
	private String externalResponseCode;
	private Integer internalErrorCode;
	
	public Integer getMessageType() {
		return messageType;
	}
	
	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}
	
	public String getExternalResponseCode() {
		return externalResponseCode;
	}
	
	public void setExternalResponseCode(String externalResponseCode) {
		this.externalResponseCode = externalResponseCode;
	}
	
	public Integer getInternalErrorCode() {
		return internalErrorCode;
	}
	
	public void setInternalErrorCode(Integer internalErrorCode) {
		this.internalErrorCode = internalErrorCode;
	}
	/*
	 * Code before running findbug tool 
	 * public static ResponseCodes getResponseCodes(Integer messageType, String externalResponseCode){
		
		//FIXME: default failure message need to add failure messages from bank to here
		// this should be ISO specific implementation
		ResponseCodes responseCode = bank_Failure;
		
		if(responseCodes == null){
			responseCodes = new HashSet<ResponseCodes>();
			
			ResponseCodes[] arResponseCodes = ResponseCodes.values();
			
			for (int i = 0; i < arResponseCodes.length; i++) {
				responseCodes.add(arResponseCodes[i]);
			}
		}
		
		for(ResponseCodes resCode : responseCodes){
			if((resCode.getMessageType().equals(messageType)) && (resCode.getExternalResponseCode().equals(externalResponseCode))){
				responseCode = resCode;
				break;
			}
		}
		
		return responseCode;
	}
  }*/

	static{
		responseCodes = new HashSet<ResponseCodes>();
		
		ResponseCodes[] arResponseCodes = ResponseCodes.values();
		
		for (int i = 0; i < arResponseCodes.length; i++) {
			responseCodes.add(arResponseCodes[i]);
		}
	}
	
	
	public  static  ResponseCodes getResponseCodes(Integer messageType, String externalResponseCode){
		
		//FIXME: default failure message need to add failure messages from bank to here
		// this should be ISO specific implementation
		ResponseCodes responseCode = bank_Failure;
		
		/*if(responseCodes == null){
			responseCodes = new HashSet<ResponseCodes>();
			
			ResponseCodes[] arResponseCodes = ResponseCodes.values();
			
			for (int i = 0; i < arResponseCodes.length; i++) {
				responseCodes.add(arResponseCodes[i]);
			}
		}*/
		
		for(ResponseCodes resCode : responseCodes){
			if((resCode.getMessageType().equals(messageType)) && (resCode.getExternalResponseCode().equals(externalResponseCode))){
				responseCode = resCode;
				break;
			}
		}
		
		return responseCode;
	}
}
