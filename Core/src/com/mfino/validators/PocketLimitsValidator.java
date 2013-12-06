package com.mfino.validators;

import java.math.BigDecimal;
import java.util.Calendar;

import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;

/**
 * @author Maruthi
 *
 */

public class PocketLimitsValidator implements IValidator {
	private BigDecimal amount;
	private Pocket pocket;
	private boolean isSource = false;

	public PocketLimitsValidator(BigDecimal amount,Pocket pocket, boolean isSource){
		this.amount=amount;
		this.pocket= pocket;
		this.isSource = isSource;
				
	}
	
	/**
	 *  @author Bala sunku
	 *  Validates the Pocket velocity limits based on the pocket template
	 */
	@Override
	public Integer validate() {
		Timestamp now = new Timestamp();
		
		if (pocket.getPocketTemplate().getIsCollectorPocket()!=null && pocket.getPocketTemplate().getIsCollectorPocket()){
			pocket.setLastTransactionTime(now);
			return CmFinoFIX.ResponseCode_Success;
		}

		if (pocket.getLastTransactionTime() == null) {
			pocket.setLastTransactionTime(now);
			pocket.setCurrentDailyExpenditure(BigDecimal.ZERO);
			pocket.setCurrentDailyTxnsCount(0);
			pocket.setCurrentMonthlyExpenditure(BigDecimal.ZERO);
			pocket.setCurrentMonthlyTxnsCount(0);
			pocket.setCurrentWeeklyExpenditure(BigDecimal.ZERO);
			pocket.setCurrentWeeklyTxnsCount(0);
		} 
		else {
			// Reset the pocket counters based on the last transaction date 
			Calendar calendarNow = Calendar.getInstance();
			calendarNow.setTimeInMillis(now.getTime());
			Calendar lastTransationTime = Calendar.getInstance();
			lastTransationTime.setTimeInMillis(pocket.getLastTransactionTime().getTime()); 
			if (calendarNow.get(Calendar.DATE) != lastTransationTime.get(Calendar.DATE)) {
				pocket.setCurrentDailyTxnsCount(0);
				pocket.setCurrentDailyExpenditure(BigDecimal.ZERO);
			} 
			
			if (lastTransationTime.get(Calendar.DAY_OF_WEEK)>calendarNow.get(Calendar.DAY_OF_WEEK) || 
					(calendarNow.getTimeInMillis() - lastTransationTime.getTimeInMillis()) > 7  * 24 * 60 * 60 * 1000) {
				pocket.setCurrentWeeklyTxnsCount(0);
				pocket.setCurrentWeeklyExpenditure(BigDecimal.ZERO);
			} 
			
			if (calendarNow.get(Calendar.MONTH) != lastTransationTime.get(Calendar.MONTH)) {
				pocket.setCurrentMonthlyTxnsCount(0);
				pocket.setCurrentMonthlyExpenditure(BigDecimal.ZERO);
			} 
		}
		
		if (pocket.getPocketTemplate().getType().intValue() == CmFinoFIX.PocketType_SVA) {
			if (null == pocket.getCurrentBalance()) {
				pocket.setCurrentBalance(BigDecimal.ZERO);
			}
		}

		Integer notificationCode = CmFinoFIX.ResponseCode_Success;
		
		if (amount.compareTo(BigDecimal.ZERO) != 1) {
			notificationCode = CmFinoFIX.NotificationCode_TransactionFailedDueToInvalidAmount;
		}
		else if (pocket.getRestrictions() != 0) {
			if(isSource){
				notificationCode = CmFinoFIX.NotificationCode_SenderSVAPocketRestricted;
			}else{
				notificationCode = CmFinoFIX.NotificationCode_ReceiverSVAPocketRestricted;
			}
		}
		else if (isSource && (pocket.getPocketTemplate().getMinTimeBetweenTransactions() > 0) && 
				(pocket.getPocketTemplate().getMinTimeBetweenTransactions()*1000 > (now.getTime() - pocket.getLastTransactionTime().getTime()))) {
			notificationCode = CmFinoFIX.NotificationCode_TransactionFailedDueToTimeLimitTransactionReached;
		}
		else if (pocket.getPocketTemplate().getType().intValue()	==	CmFinoFIX.PocketType_SVA) {
			if (isSource) {
				if (((pocket.getCurrentBalance().subtract(amount).compareTo(pocket.getPocketTemplate().getMinimumStoredValue())) == -1)) {
					notificationCode = CmFinoFIX.NotificationCode_BalanceTooLow;
				}
			}
			else if (((pocket.getCurrentBalance().add(amount).compareTo(pocket.getPocketTemplate().getMaximumStoredValue())) == 1)) {
				notificationCode = CmFinoFIX.NotificationCode_BalanceTooHigh;
			}
		}
		
		if (amount.compareTo(pocket.getPocketTemplate().getMaxAmountPerTransaction()) == 1) {
			notificationCode = CmFinoFIX.NotificationCode_TransferAmountAboveMaximumAllowed;
		}
		else if (amount.compareTo(pocket.getPocketTemplate().getMinAmountPerTransaction()) == -1) { 
			notificationCode = CmFinoFIX.NotificationCode_TransferAmountBelowMinimumAllowed;
		}
		else if (pocket.getCurrentDailyTxnsCount()	>=	pocket.getPocketTemplate().getMaxTransactionsPerDay()) {
			notificationCode = CmFinoFIX.NotificationCode_AboveDailyTransactionsCountLimit;
		}
		else if (pocket.getCurrentWeeklyTxnsCount()	>=	pocket.getPocketTemplate().getMaxTransactionsPerWeek()) {
			notificationCode = CmFinoFIX.NotificationCode_AboveWeeklyTransactionsCountLimit;
		}
		else if (pocket.getCurrentMonthlyTxnsCount() >=	pocket.getPocketTemplate().getMaxTransactionsPerMonth()) {
			notificationCode = CmFinoFIX.NotificationCode_AboveMonthlyTransactionsCountLimit;
		}
		else if (pocket.getCurrentDailyExpenditure().add(amount).compareTo(pocket.getPocketTemplate().getMaxAmountPerDay()) == 1) {
			notificationCode = CmFinoFIX.NotificationCode_AboveDailyExpenditureLimit;
		}
		else if (pocket.getCurrentWeeklyExpenditure().add(amount).compareTo(pocket.getPocketTemplate().getMaxAmountPerWeek()) == 1) {
			notificationCode = CmFinoFIX.NotificationCode_AboveWeeklyExpenditureLimit;
		}
		else if (pocket.getCurrentMonthlyExpenditure().add(amount).compareTo(pocket.getPocketTemplate().getMaxAmountPerMonth()) == 1) {
			notificationCode = CmFinoFIX.NotificationCode_AboveMonthlyExpenditureLimit;
		}
	
		pocket.setLastTransactionTime(now);
		
		return notificationCode;
	}
}
