package com.mfino.mce.backend;

import java.math.BigDecimal;

import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX.CMBase;

/**
 * @author sasidhar
 *
 */
public interface CommodityTransferService {
	
	public PendingCommodityTransfer createPCT(CMBase requestFix,Subscriber objSourceSubscriber, Subscriber objDestSubscriber, Pocket objSourcePocket, Pocket objDestPocket, SubscriberMDN objSourceSubMdn, SubscriberMDN objDestSubMdn, String sourceMessage, BigDecimal amount, BigDecimal charges, String bucketType, Integer billingType, Integer initialTransferStatus);
	
	public PendingCommodityTransfer createPCT(CMBase requestFix,Subscriber objSourceSubscriber, Subscriber objDestSubscriber, Pocket objSourcePocket, Pocket objDestPocket, SubscriberMDN objSourceSubMdn, SubscriberMDN objDestSubMdn, String sourceMessage, BigDecimal amount, BigDecimal charges, BigDecimal taxAmount, String bucketType, Integer billingType, Integer initialTransferStatus);
	
	public PendingCommodityTransfer createPCT(CMBase requestFix,Subscriber objSourceSubscriber, Subscriber objDestSubscriber, Pocket objSourcePocket, Pocket objDestPocket, SubscriberMDN objSourceSubMdn, SubscriberMDN objDestSubMdn, String sourceMessage, BigDecimal amount, BigDecimal charges, BigDecimal taxAmount, String bucketType, Integer billingType, Integer initialTransferStatus, String destinationBankAccountNo);
	
	public PendingCommodityTransfer createPCT(CMBase requestFix,Subscriber objSourceSubscriber, Subscriber objDestSubscriber, Pocket objSourcePocket, Pocket objDestPocket, 
			SubscriberMDN objSourceSubMdn, SubscriberMDN objDestSubMdn, String sourceMessage, BigDecimal amount, BigDecimal charges, BigDecimal taxAmount, String bucketType, 
			Integer billingType, Integer initialTransferStatus, String destinationBankAccountNo, String sourceBankAccountNo);	
	
	public CommodityTransfer movePctToCt(PendingCommodityTransfer pct);
	
	public PendingCommodityTransfer getPendingCT(Long sctlId);
}
