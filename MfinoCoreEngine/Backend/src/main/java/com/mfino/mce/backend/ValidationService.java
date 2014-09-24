package com.mfino.mce.backend;

import java.math.BigDecimal;

import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.mce.core.util.BackendResponse;

/**
 * @author sasidhar
 *
 */
public interface ValidationService {
	
	public BackendResponse validateFixMessage(Integer messageCode, CFIXMsg requestFix);
	
	public BackendResponse validateBankAccountSubscriber(Subscriber subscriber, SubscriberMDN subscriberMdn, Pocket pocket, String rPin, boolean isSource, boolean isMerchant, boolean isValidateBobPocket, boolean allowInitialized);
	
	public BackendResponse validateBankAccountSubscriber(Subscriber subscriber, SubscriberMDN subscriberMdn, Pocket pocket, String rPin, boolean isSource, boolean isMerchant, boolean isValidateBobPocket, boolean allowInitialized, boolean isSystemInitiatedTransaction);
	
	public BackendResponse validateSubscriberPin(Subscriber subscriber, SubscriberMDN subscriberMdn, String rPin, boolean isSource, boolean isSystemInitiatedTransaction);
	
	public BackendResponse validateSubscriber(Subscriber subscriber, SubscriberMDN subscriberMdn, boolean isSource, boolean isMerchant, boolean isValidateBobPocket, boolean isAllowInitialized);
	
	public BackendResponse validateSubscriber(Subscriber subscriber, SubscriberMDN subscriberMdn, boolean isSource, boolean isMerchant, boolean isValidateBobPocket, boolean isAllowInitialized, boolean isSystemInitiatedTransaction);
	
	public BackendResponse validateRisksAndLimits(Pocket sourcePocket, Pocket destinationPocket, BigDecimal debitAmount, BigDecimal creditAmount, SubscriberMDN srcSubscriberMdn, SubscriberMDN destSubscriberMdn);
	
	public BackendResponse validatePct(PendingCommodityTransfer pct,Pocket sourcePocket, Pocket destinationPocket, SubscriberMDN sourceMdn, SubscriberMDN destMdn);
	
	public BackendResponse validatePctForSourcePocketBalance(PendingCommodityTransfer pct,Pocket sourcePocket);
	
	public BackendResponse validatePocketsForBulkTransfer(Pocket srcPocket, Pocket destPocket, BigDecimal amount);
	
	public BackendResponse validatePocketsForChargeDistribution(Pocket sourcePocket, BigDecimal amount, boolean isSource);

}
