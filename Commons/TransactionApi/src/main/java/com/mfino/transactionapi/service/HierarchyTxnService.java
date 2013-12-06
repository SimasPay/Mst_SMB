/**
 * 
 */
package com.mfino.transactionapi.service;

import java.math.BigDecimal;
import java.util.Map;

import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX.CMJSError;

/**
 * @author Shashank
 *
 */
public interface HierarchyTxnService {

	 Map<Subscriber, CMJSError> transferToChildren(Subscriber parent, Map<Subscriber, BigDecimal> childSubscriberVsAmt, String sourcePin, BigDecimal totalAmt, Long dctId);
}
