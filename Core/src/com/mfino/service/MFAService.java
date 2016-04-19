package com.mfino.service;

public interface MFAService {

	void handleMFATransaction(Long sctlID, String sourceMDN);

	boolean isValidOTP(String transactionOtp, Long sctlID, String sourceMDN);

	boolean isMFATransaction(String serviceName, String transactionName, Long channelCodeId);
	
	public void resendHandleMFATransaction(Long sctlID, String sourceMDN, int retryAttempt);
}
