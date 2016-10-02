package com.mfino.mce.backend.util;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMChargeDistribution;
import com.mfino.fix.CmFinoFIX.CMDSTVPaymentInquiry;
import com.mfino.fix.CmFinoFIX.CMSettlementOfCharge;
import com.mfino.hibernate.Timestamp;

/**
 * @author sasidhar
 *
 */
public class BackendUtil {
	
	private static Logger log = LoggerFactory.getLogger(BackendUtil.class);

	public static Integer getUiCategory(CMBase requestFix, Pocket sourcePocket, Pocket destinationPocket)
	{	
		// FindbugsChange
    	
		if((null != requestFix.getUICategory()) ){
			return requestFix.getUICategory();
		}
		
		Integer messageType = requestFix.getMessageType();
		Integer uiCategory = -1;
		
		if(messageType != null){
			if(requestFix instanceof CMDSTVPaymentInquiry)
			{
				if(sourcePocket.getPocketTemplate().getType() == CmFinoFIX.PocketType_SVA.intValue())
				{
					uiCategory = CmFinoFIX.TransactionUICategory_Bill_Payment_Emoney;
				}
				else //only two modes of payment are allowed SVA or Bank, if others come need to update this
				{
					uiCategory = CmFinoFIX.TransactionUICategory_Bill_Payment_Bank;
				}
			}
			else if(requestFix instanceof CMBankAccountToBankAccount)
			{
				String sourceMsg = ((CMBankAccountToBankAccount) requestFix).getSourceMessage();
				if (ServiceAndTransactionConstants.MESSAGE_BULK_TRANSFER.equals(sourceMsg)) {
					uiCategory = CmFinoFIX.TransactionUICategory_Bulk_Transfer;
				} 
				else if (ServiceAndTransactionConstants.MESSAGE_SUB_BULK_TRANSFER.equals(sourceMsg)) {
					uiCategory = CmFinoFIX.TransactionUICategory_Sub_Bulk_Transfer;
				}
				else if (ServiceAndTransactionConstants.MESSAGE_SETTLE_BULK_TRANSFER.equals(sourceMsg)) {
					uiCategory = CmFinoFIX.TransactionUICategory_Settle_Bulk_Transfer;
				}
				else if(sourcePocket.getPocketTemplate().getType()== CmFinoFIX.PocketType_SVA.intValue() && 
						destinationPocket.getPocketTemplate().getType() == CmFinoFIX.PocketType_BankAccount.intValue()){
					if(((destinationPocket.getPocketTemplate().getAllowance() & CmFinoFIX.PocketAllowance_MerchantDompet) == 1) ||
							CmFinoFIX.MessageType_PurchaseInquiry == messageType.intValue() || 
							CmFinoFIX.MessageType_Purchase == messageType.intValue()) 
					{
						uiCategory = CmFinoFIX.TransactionUICategory_EMoney_Purchase;
					} 
					else if(((destinationPocket.getPocketTemplate().getAllowance() & CmFinoFIX.PocketAllowance_CashOutDompet) == 1) ||
							CmFinoFIX.MessageType_CashOutInquiry == messageType.intValue() ||
							CmFinoFIX.MessageType_CashOut == messageType.intValue()) 
					{
						uiCategory = CmFinoFIX.TransactionUICategory_EMoney_CashOut;
					} 
					else 
					{
						uiCategory = CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf;
					}
				} 
				else if((sourcePocket.getPocketTemplate().getType() == CmFinoFIX.PocketType_SVA.intValue()) && 
						(destinationPocket.getPocketTemplate().getType() == CmFinoFIX.PocketType_SVA.intValue())) 
				{
					if (CmFinoFIX.MessageType_CashInInquiry == messageType.intValue() ||
							CmFinoFIX.MessageType_CashIn == messageType.intValue()) 
					{
						uiCategory = CmFinoFIX.TransactionUICategory_EMoney_CashIn;
					}
					else if (CmFinoFIX.MessageType_CashOutInquiry == messageType.intValue() ||
							CmFinoFIX.MessageType_CashOut == messageType.intValue()) 
					{
						uiCategory = CmFinoFIX.TransactionUICategory_EMoney_CashOut;
					} 
					else 
					{
						uiCategory = CmFinoFIX.TransactionUICategory_EMoney_EMoney_Trf;
					}
				} 
				else if((sourcePocket.getPocketTemplate().getType() == CmFinoFIX.PocketType_BankAccount.intValue()) && 
						(destinationPocket.getPocketTemplate().getType()== CmFinoFIX.PocketType_SVA.intValue())) 
				{
					if(((sourcePocket.getPocketTemplate().getAllowance() & CmFinoFIX.PocketAllowance_CashInDompet) == 1) ||
							CmFinoFIX.MessageType_CashInInquiry == messageType.intValue() ||
							CmFinoFIX.MessageType_CashIn == messageType.intValue()) 
					{
						uiCategory = CmFinoFIX.TransactionUICategory_EMoney_CashIn;
					} 
					else 
					{
						uiCategory = CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf;
					}
				} 
				else 
				{
					uiCategory = CmFinoFIX.TransactionUICategory_Dompet_Money_Transfer;
				}
			} 
			else if (requestFix instanceof CMChargeDistribution) 
			{
				uiCategory = CmFinoFIX.TransactionUICategory_Charge_Distribution;
			} 
			else if (requestFix instanceof CMSettlementOfCharge) 
			{
				uiCategory = CmFinoFIX.TransactionUICategory_Settlement_Of_Charge;
			}
		}
		
		return uiCategory;
	}
	
	public static void setPocketLimits(Pocket pocket, BigDecimal transactionAmount){
		if(!((pocket.getPocketTemplate().getIscollectorpocket()) || (pocket.getPocketTemplate().getIssuspencepocket()) || (pocket.getPocketTemplate().getIssystempocket()) )){
			pocket.setCurrentdailyexpenditure(pocket.getCurrentdailyexpenditure().add(transactionAmount));
			pocket.setCurrentdailytxnscount(pocket.getCurrentdailytxnscount() + 1);
			pocket.setCurrentmonthlyexpenditure(pocket.getCurrentmonthlyexpenditure().add(transactionAmount));
			pocket.setCurrentmonthlytxnscount(pocket.getCurrentmonthlytxnscount() + 1);
			pocket.setCurrentweeklyexpenditure(pocket.getCurrentweeklyexpenditure().add(transactionAmount));
			pocket.setCurrentweeklytxnscount(pocket.getCurrentweeklytxnscount() + 1);
		}
	}
	
	public static void revertPocketLimits(Pocket pocket, BigDecimal transactionAmount, PendingCommodityTransfer pct){
		if(pct==null){
			log.error("Aborting revert of pocket limits, pct is null");
			return;
		}
		if(pocket==null){
			log.error("Aborting revert of pocket limits, pct is null");
			return;
		}
		Timestamp createTime = pct.getCreatetime();
		long createMilliSecs = createTime.getTime();
		
		Timestamp now = new Timestamp();
		long currentMilliSecs = now.getTime();
		
		long diffMilliSecs = currentMilliSecs - createMilliSecs;
		
		long milliSecsForDay = 24*60*60*1000; //24hrs*60mins*60secs*1000
		
		long mod = diffMilliSecs / milliSecsForDay;

		if(!((pocket.getPocketTemplate().getIscollectorpocket()) || (pocket.getPocketTemplate().getIssuspencepocket()) || (pocket.getPocketTemplate().getIssystempocket()) )){
			if(mod <= 30){
				//Within a month
				pocket.setCurrentmonthlyexpenditure(pocket.getCurrentmonthlyexpenditure().subtract(transactionAmount));
				pocket.setCurrentmonthlytxnscount(pocket.getCurrentmonthlytxnscount() - 1);
			}		
			if(mod <= 7){
				//Within a week
				pocket.setCurrentweeklyexpenditure(pocket.getCurrentweeklyexpenditure().subtract(transactionAmount));
				pocket.setCurrentweeklytxnscount(pocket.getCurrentweeklytxnscount() - 1);
			}
			if(mod < 1){
				// With in Day
				pocket.setCurrentdailyexpenditure(pocket.getCurrentdailyexpenditure().subtract(transactionAmount));
				pocket.setCurrentdailytxnscount(pocket.getCurrentdailytxnscount() - 1);
			}
		}
	}

}
