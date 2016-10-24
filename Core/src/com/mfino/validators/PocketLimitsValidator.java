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
		if (pocket.getPocketTemplateByPockettemplateid().getIscollectorpocket()!=null){
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
		
		Long tempTypeL = pocket.getPocketTemplateByPockettemplateid().getType().longValue();
		int tempTypeLI = tempTypeL.intValue();
		
		if (tempTypeLI == CmFinoFIX.PocketType_SVA) {
			if (null == pocket.getCurrentbalance()) {
				pocket.setCurrentbalance(BigDecimal.ZERO);
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
		else if (isSource && (pocket.getPocketTemplateByPockettemplateid().getMintimebetweentransactions() > 0) && 
				(pocket.getPocketTemplateByPockettemplateid().getMintimebetweentransactions()*1000 > (now.getTime() - pocket.getLasttransactiontime().getTime()))) {
			notificationCode = CmFinoFIX.NotificationCode_TransactionFailedDueToTimeLimitTransactionReached;
		}
		else if (tempTypeLI	==	CmFinoFIX.PocketType_SVA) {
			BigDecimal currBalance = pocket.getCurrentbalance();
			if (isSource) {
				
				if (((currBalance.subtract(amount).compareTo(pocket.getPocketTemplateByPockettemplateid().getMaximumstoredvalue())) == -1)) {
					notificationCode = CmFinoFIX.NotificationCode_BalanceTooLow;
				}
			}
			else if (((currBalance.add(amount).compareTo(pocket.getPocketTemplateByPockettemplateid().getMaximumstoredvalue())) == 1)) {
				notificationCode = CmFinoFIX.NotificationCode_BalanceTooHigh;
			}
		}
		
		if (amount.compareTo(pocket.getPocketTemplateByPockettemplateid().getMaxamountpertransaction()) == 1) {
			notificationCode = CmFinoFIX.NotificationCode_TransferAmountAboveMaximumAllowed;
		}
		else if (amount.compareTo(pocket.getPocketTemplateByPockettemplateid().getMinamountpertransaction()) == -1) { 
			notificationCode = CmFinoFIX.NotificationCode_TransferAmountBelowMinimumAllowed;
		}
		else if (pocket.getCurrentdailytxnscount()	>=	pocket.getPocketTemplateByPockettemplateid().getMaxtransactionsperday()) {
			notificationCode = CmFinoFIX.NotificationCode_AboveDailyTransactionsCountLimit;
		}
		else if (pocket.getCurrentweeklytxnscount()	>=	pocket.getPocketTemplateByPockettemplateid().getMaxtransactionsperweek()) {
			notificationCode = CmFinoFIX.NotificationCode_AboveWeeklyTransactionsCountLimit;
		}
		else if (pocket.getCurrentmonthlytxnscount() >=	pocket.getPocketTemplateByPockettemplateid().getMaxtransactionspermonth()) {
			notificationCode = CmFinoFIX.NotificationCode_AboveMonthlyTransactionsCountLimit;
		}
		else if (pocket.getCurrentdailyexpenditure().add(amount).compareTo(pocket.getPocketTemplateByPockettemplateid().getMaxamountperday()) == 1) {
			notificationCode = CmFinoFIX.NotificationCode_AboveDailyExpenditureLimit;
		}
		else if (pocket.getCurrentweeklyexpenditure().add(amount).compareTo(pocket.getPocketTemplateByPockettemplateid().getMaxamountperweek()) == 1) {
			notificationCode = CmFinoFIX.NotificationCode_AboveWeeklyExpenditureLimit;
		}
		else if (pocket.getCurrentmonthlyexpenditure().add(amount).compareTo(pocket.getPocketTemplateByPockettemplateid().getMaxamountpermonth()) == 1) {
			notificationCode = CmFinoFIX.NotificationCode_AboveMonthlyExpenditureLimit;
		}
	
		pocket.setLasttransactiontime(now);
		
		return notificationCode;
	}
}
