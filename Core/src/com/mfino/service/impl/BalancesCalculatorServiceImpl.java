package com.mfino.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.domain.BookingDatedBalance;
import com.mfino.domain.LedgerBalance;
import com.mfino.domain.Pocket;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.BalancesCalculatorService;
import com.mfino.service.BookingDatedBalanceService;
import com.mfino.service.MFSLedgerService;
import com.mfino.service.PocketService;
import com.mfino.util.DateUtil;

@Service("BalancesCalculatorServiceImpl")
public class BalancesCalculatorServiceImpl implements BalancesCalculatorService{

	public Log log = LogFactory.getLog(this.getClass());
	private static final String DEBIT_LEDGER_TYPE = "Dr.";
	private static final String CREDIT_LEDGER_TYPE = "Cr.";
	
	@Autowired
	@Qualifier("MFSLedgerServiceImpl")
	private MFSLedgerService mfsLedgerService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("BookingDatedBalanceServiceImpl")
	private BookingDatedBalanceService bookingDatedBalanceService;
	
	/**
	 * Build booking dated balances for a particular day Call this from
	 * scheduler for n-1 day. Not thread safe, create with new operator. *
	 * 
	 * @param date
	 */
	public void buildBookingBalances(Date date) {
		try {
			log.info("BalancesCalculator:: Start Populating balances for date:"
					+ date.toString());
			Map<Long, BigDecimal> credit = new HashMap<Long, BigDecimal>();
			Map<Long, BigDecimal> debit = new HashMap<Long, BigDecimal>();

			String strStartDate = DateUtil.getFormattedDate(date);
			Date sDate = DateUtil.getDate(strStartDate);

			Date start = sDate;
			Date end = DateUtil.addDays(start, 1);

			List<LedgerBalance> ledgerBalances = mfsLedgerService.getConsolidateBalance(start,end);
			if (ledgerBalances != null && ledgerBalances.size() > 0) {
				calculateCreditDebit(ledgerBalances, credit, debit);
			}
			populateBookingDateBalances(start, credit, debit);
			log.info("BalancesCalculator:: end Populating balances for date:"
					+ start.toString());
		} catch (Exception e) {
			log.error("BalancesCalculator :: buildBookingBalances ", e);
			throw new RuntimeException(e);
		}
	}

	private void calculateCreditDebit(List<LedgerBalance> ledgerBalances, Map<Long, BigDecimal> credit, Map<Long, BigDecimal> debit) {
		BigDecimal creditAmount;
		BigDecimal debitAmount;
		if(ledgerBalances!=null){
		for (LedgerBalance ledgerBalance : ledgerBalances) {
				if (CREDIT_LEDGER_TYPE.equals(ledgerBalance.getBalanceType())) {
					creditAmount = ledgerBalance.getBalance();
					credit.put(ledgerBalance.getPocketId(), creditAmount);
				} else if (DEBIT_LEDGER_TYPE.equals(ledgerBalance
						.getBalanceType())) {
					debitAmount = ledgerBalance.getBalance();
					debit.put(ledgerBalance.getPocketId(), debitAmount);
				}
			}
		}

	}

	private void populateBookingDateBalances(Date date, Map<Long, BigDecimal> credit, Map<Long, BigDecimal> debit) {

		Set<Long> pocketIDS  = new HashSet<Long>();
		  if(credit.keySet() != null){
		   pocketIDS.addAll(credit.keySet());
		  }
		  if(debit.keySet() != null){
		   pocketIDS.addAll(debit.keySet());
		  }
		  BigDecimal creditAmount;
		  BigDecimal debitAmount;
		  Pocket pocket;
		for (Long pocketID : pocketIDS) {
			pocket = pocketService.getById(pocketID);
			if(pocket!=null){
			BookingDatedBalance exactDateEntry = bookingDatedBalanceService
					.getExactDatedEntry(pocket, date);
			BookingDatedBalance preDateEntry = bookingDatedBalanceService
					.getPreDatedEntry(pocket, date);

			if (exactDateEntry == null) {
				exactDateEntry = new BookingDatedBalance();
				exactDateEntry.setBookingDate(new Timestamp(date));
			}

			if (preDateEntry == null)
				exactDateEntry.setOpeningBalance(BigDecimal.ZERO);
			else
				exactDateEntry.setOpeningBalance(preDateEntry
						.getClosingBalance());

			creditAmount = credit.get(pocketID) != null ? credit.get(pocketID)
					: BigDecimal.ZERO;
			debitAmount = debit.get(pocketID) != null ? debit.get(pocketID)
					: BigDecimal.ZERO;

			exactDateEntry.setPocketID(pocket.getID());
			exactDateEntry.setTotalCredit(creditAmount);
			exactDateEntry.setTotalDebit(debitAmount);
			exactDateEntry.setNetTurnOver(creditAmount.subtract(debitAmount));
			exactDateEntry.setClosingBalance(exactDateEntry.getOpeningBalance()
					.add(exactDateEntry.getNetTurnOver()));

			try {
				bookingDatedBalanceService.save(exactDateEntry);
			} catch (Exception e) {
				log.info("BalancesCalculator :: Exception " + e);
			}
			
		}
		}		
	}
}
