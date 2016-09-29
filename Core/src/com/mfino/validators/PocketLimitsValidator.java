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
		
//		if (pocket.getPocketTemplate().getIscollectorpocket()!=null && pocket.getPocketTemplate().getIscollectorpocket()){
		if (pocket.getPocketTemplate().getIscollectorpocket()!=null){
			pocket.setLasttransactiontime(now);
			return CmFinoFIX.ResponseCode_Success;
		}
		
		if (pocket.getLasttransactiontime() == null) {
			pocket.setLasttransactiontime(now);
			pocket.setCurrentdailyexpenditure(BigDecimal.ZERO);
			pocket.setCurrentdailytxnscount(0);
			pocket.setCurrentmonthlyexpenditure(BigDecimal.ZERO);
			pocket.setCurrentmonthlytxnscount(0);
			pocket.setCurrentweeklyexpenditure(BigDecimal.ZERO);
			pocket.setCurrentweeklytxnscount(0);
		} 
		else {
			// Reset the pocket counters based on the last transaction date 
			Calendar calendarNow = Calendar.getInstance();
			calendarNow.setTimeInMillis(now.getTime());
			Calendar lastTransationTime = Calendar.getInstance();
			lastTransationTime.setTimeInMillis(pocket.getLasttransactiontime().getTime()); 
			if (calendarNow.get(Calendar.DATE) != lastTransationTime.get(Calendar.DATE)) {
				pocket.setCurrentdailytxnscount(0);
				pocket.setCurrentdailyexpenditure(BigDecimal.ZERO);
			} 
			
			if (lastTransationTime.get(Calendar.DAY_OF_WEEK)>calendarNow.get(Calendar.DAY_OF_WEEK) || 
					(calendarNow.getTimeInMillis() - lastTransationTime.getTimeInMillis()) > 7  * 24 * 60 * 60 * 1000) {
				pocket.setCurrentweeklytxnscount(0);
				pocket.setCurrentweeklyexpenditure(BigDecimal.ZERO);
			} 
			
			if (calendarNow.get(Calendar.MONTH) != lastTransationTime.get(Calendar.MONTH)) {
				pocket.setCurrentmonthlytxnscount(0);
				pocket.setCurrentmonthlyexpenditure(BigDecimal.ZERO);
			} 
		}
		
		Long tempTypeL = pocket.getPocketTemplate().getType();
		int tempTypeLI = tempTypeL.intValue();
		
		if (tempTypeLI == CmFinoFIX.PocketType_SVA) {
			if (null == pocket.getCurrentbalance()) {
				pocket.setCurrentbalance(BigDecimal.ZERO.toString());
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
		else if (isSource && (pocket.getPocketTemplate().getMintimebetweentransactions() > 0) && 
				(pocket.getPocketTemplate().getMintimebetweentransactions()*1000 > (now.getTime() - pocket.getLasttransactiontime().getTime()))) {
			notificationCode = CmFinoFIX.NotificationCode_TransactionFailedDueToTimeLimitTransactionReached;
		}
		else if (tempTypeLI	==	CmFinoFIX.PocketType_SVA) {
			BigDecimal currBalance = new BigDecimal(pocket.getCurrentbalance());
			if (isSource) {
				
				if (((currBalance.subtract(amount).compareTo(pocket.getPocketTemplate().getMaximumstoredvalue())) == -1)) {
					notificationCode = CmFinoFIX.NotificationCode_BalanceTooLow;
				}
			}
			else if (((currBalance.add(amount).compareTo(pocket.getPocketTemplate().getMaximumstoredvalue())) == 1)) {
				notificationCode = CmFinoFIX.NotificationCode_BalanceTooHigh;
			}
		}
		
		if (amount.compareTo(pocket.getPocketTemplate().getMaxamountpertransaction()) == 1) {
			notificationCode = CmFinoFIX.NotificationCode_TransferAmountAboveMaximumAllowed;
		}
		else if (amount.compareTo(pocket.getPocketTemplate().getMinamountpertransaction()) == -1) { 
			notificationCode = CmFinoFIX.NotificationCode_TransferAmountBelowMinimumAllowed;
		}
		else if (pocket.getCurrentdailytxnscount()	>=	pocket.getPocketTemplate().getMaxtransactionsperday()) {
			notificationCode = CmFinoFIX.NotificationCode_AboveDailyTransactionsCountLimit;
		}
		else if (pocket.getCurrentweeklytxnscount()	>=	pocket.getPocketTemplate().getMaxtransactionsperweek()) {
			notificationCode = CmFinoFIX.NotificationCode_AboveWeeklyTransactionsCountLimit;
		}
		else if (pocket.getCurrentmonthlytxnscount() >=	pocket.getPocketTemplate().getMaxtransactionspermonth()) {
			notificationCode = CmFinoFIX.NotificationCode_AboveMonthlyTransactionsCountLimit;
		}
		else if (pocket.getCurrentdailyexpenditure().add(amount).compareTo(pocket.getPocketTemplate().getMaxamountperday()) == 1) {
			notificationCode = CmFinoFIX.NotificationCode_AboveDailyExpenditureLimit;
		}
		else if (pocket.getCurrentweeklyexpenditure().add(amount).compareTo(pocket.getPocketTemplate().getMaxamountperweek()) == 1) {
			notificationCode = CmFinoFIX.NotificationCode_AboveWeeklyExpenditureLimit;
		}
		else if (pocket.getCurrentmonthlyexpenditure().add(amount).compareTo(pocket.getPocketTemplate().getMaxamountpermonth()) == 1) {
			notificationCode = CmFinoFIX.NotificationCode_AboveMonthlyExpenditureLimit;
		}
	
		pocket.setLasttransactiontime(now);
		
		return notificationCode;
	}
}
