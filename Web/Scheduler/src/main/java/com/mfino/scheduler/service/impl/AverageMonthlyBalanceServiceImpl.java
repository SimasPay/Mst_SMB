/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.AgentCommissionFee;
import com.mfino.domain.BookingDatedBalance;
import com.mfino.domain.MonthlyBalance;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.scheduler.service.AverageMonthlyBalanceService;
import com.mfino.service.AgentCommissionFeeService;
import com.mfino.service.BookingDatedBalanceService;
import com.mfino.service.MoneyService;
import com.mfino.service.MonthlyBalanceService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;

/**
 * @author Bala Sunku
 *
 */
@Service("AverageMonthlyBalanceServiceImpl")
public class AverageMonthlyBalanceServiceImpl  implements AverageMonthlyBalanceService {
	private static Logger log = LoggerFactory.getLogger(AverageMonthlyBalanceServiceImpl.class);
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("BookingDatedBalanceServiceImpl")
	private BookingDatedBalanceService bookingDatedBalanceService;
	
	@Autowired
	@Qualifier("MonthlyBalanceServiceImpl")
	private MonthlyBalanceService monthlyBalanceService;
	
	@Autowired
	@Qualifier("MoneyServiceImpl")
	private MoneyService moneyService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("AgentCommissionFeeServiceImpl")
	private AgentCommissionFeeService agentCommissionFeeService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	private HibernateTransactionManager txManager;
	private int currentMonth;
	private int currentYear;
	private Date startDate;
	private Date endDate;
	private int startDay = 1;
	private int endDay = 31;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	public void calculateAverageMonthlyBalanceForLakupandai() {
		log.info("calculateAverageMonthlyBalanceForLakupandai :: BEGIN");
		calculateStartAndEndDates();
		try {
			
			List<Long> lst = pocketService.getLakuPandaiPockets();
			if (CollectionUtils.isNotEmpty(lst)) {
				for (Long pocketId:lst) {
					try {
						calculateAverageMonthlyBalance(pocketId);
					} catch (Exception e) {
						log.error("Error while calculating the average monthly balance for pocketid: " + pocketId , e);
					}
				}
			}
			// Calculates the Agent commission fee after monthly balance of the customers is calculated
			calculateAgentCommissionFee();
		} catch (Exception e) {
			log.error("Error: While Processing ledger entries " + e.getMessage(), e);
		} 
		log.info("calculateAverageMonthlyBalanceForLakupandai :: END");
	}

	private void calculateAverageMonthlyBalance(Long pocketId) {
		log.info("Calculating the Avergare monthly balance for pocket id: "+ pocketId);
		List<BookingDatedBalance> lstBookingDatedBalances = null;
		BigDecimal totalMonthlyBalance = BigDecimal.ZERO;
		HashMap<Integer, BigDecimal> monthlyBalances = new HashMap<Integer, BigDecimal>();
		
		Pocket p = pocketService.getById(pocketId);

		log.info("Getting the balance detaild for pocket:"+ pocketId + " start date:" + startDate + "  end date:" + endDate);
		lstBookingDatedBalances = bookingDatedBalanceService.getDailyBalanceForPocket(pocketId, startDate, endDate);
		
		if (CollectionUtils.isNotEmpty(lstBookingDatedBalances)) {
			for (BookingDatedBalance bdb:lstBookingDatedBalances) {
				Date bookingDate = bdb.getBookingDate();
				BigDecimal bookingBalance = bdb.getClosingBalance();
				monthlyBalances.put(bookingDate.getDate(), bookingBalance);
			}
		}
		SubscriberMDN subMdn = p.getSubscriberMDNByMDNID();
		int closingDay = 0;
		if ( (CmFinoFIX.SubscriberStatus_PendingRetirement.intValue() == subMdn.getStatus().intValue()) || 
				(CmFinoFIX.SubscriberStatus_Retired.intValue() == subMdn.getStatus().intValue()) ) {
			closingDay = subMdn.getStatusTime().getDate();	
		}
		log.info("Mdn Close day = "+ closingDay);
		
		for (int i=startDay; i<=endDay; i++) {
			if (monthlyBalances.get(i) == null) {
				if ((closingDay > 0) && (i >= closingDay)) {
					monthlyBalances.put(i, BigDecimal.ZERO);
				}
				else {
					if (i == startDay) { 
						BookingDatedBalance preDayBalance = bookingDatedBalanceService.getPreDatedEntry(p, startDate);
						if (preDayBalance != null) 
							monthlyBalances.put(i, preDayBalance.getClosingBalance());
						else 
							monthlyBalances.put(i, BigDecimal.ZERO);
					}
					else {
						monthlyBalances.put(i, monthlyBalances.get(i-1));
					}
				}
			}
			log.info("Balance for day "+ i + " is: " + monthlyBalances.get(i));
			totalMonthlyBalance = totalMonthlyBalance.add(monthlyBalances.get(i));
		}
		
		MonthlyBalance mBalance = new MonthlyBalance();
		mBalance.setPocket(p);
		mBalance.setMonth(currentMonth+"");
		mBalance.setYear(currentYear);
		
		BigDecimal avgMonthlyBalance = totalMonthlyBalance.divide(new BigDecimal(endDay), 2, RoundingMode.HALF_EVEN);
		mBalance.setAverageMonthlyBalance(avgMonthlyBalance);
		
		BigDecimal roi = p.getPocketTemplate().getInterestRate();
		if (roi == null) {
			roi = BigDecimal.ZERO;
		}
		mBalance.setInterestCalculated(calculateFee(avgMonthlyBalance, roi));
		
		BigDecimal cFee = systemParametersService.getBigDecimal(SystemParameterKeys.CUSTOMER_BALANCE_FEE);
		if (cFee == null) {
			cFee = BigDecimal.ZERO;
		}
		mBalance.setAgentCommissionCalculated(calculateFee(avgMonthlyBalance, cFee));
		try {
			monthlyBalanceService.save(mBalance);
		} catch (DataIntegrityViolationException e) {
			log.info("Monthly balance for pocket:"+ pocketId + " is already calculated.");
		} catch (ConstraintViolationException e) {
			log.info("Monthly balance for pocket:"+ pocketId + " is already calculated.");
		}
	}
	
	private void calculateAgentCommissionFee() {
		AgentCommissionFee agentCommissionFee = null;
		log.info("Calculating the Agent Commission Fee for month : "+ currentMonth + " and year: " + currentYear);
		List<Object[]> lst = monthlyBalanceService.getCommissionFeeDetails(currentMonth+"", currentYear);
		if (CollectionUtils.isNotEmpty(lst)) {
			for (Object[] obj:lst) {
				try {
					Long agentId = (Long)obj[0];
					BigDecimal cFee = (BigDecimal)obj[1];
					agentCommissionFee = agentCommissionFeeService.getAgentCommissionFee(agentId, currentMonth+"", currentYear);
					if (agentCommissionFee == null) {
						agentCommissionFee = new AgentCommissionFee();
						agentCommissionFee.setPartnerID(agentId);
						agentCommissionFee.setMonth(currentMonth+"");
						agentCommissionFee.setYear(currentYear);
						agentCommissionFee.setCustomerBalanceFee(cFee);
						agentCommissionFee.setOpenAccountFee(BigDecimal.ZERO);
					}
					else {
						agentCommissionFee.setCustomerBalanceFee(cFee);
					}
					log.info("Saving the Agent commission balance for agent Id: "+ agentId);
					agentCommissionFeeService.save(agentCommissionFee);
				} catch (Exception e) {
					log.error("Error while calculating the commission fee for agent Id: "+ obj[0] , e);
				}
			}
		}
	}
	
	public void calculateLakupandaiAccountOpeningFeeToAgents() {
		calculateStartAndEndDates();
		log.info("Calculating the LakupandaiAccountOpeningFeeToAgents :: BEGIN for month : "+ currentMonth + " and year: " + currentYear);
		AgentCommissionFee agentCommissionFee = null;
		BigDecimal fee = systemParametersService.getBigDecimal(SystemParameterKeys.OPEN_ACCOUNT_FEE_TO_AGENT);
		if (fee == null) {
			fee = BigDecimal.ZERO;
		}
		List<Object[]> lst = subscriberService.getNewSubscribersCount(startDate, endDate);
		if (CollectionUtils.isNotEmpty(lst)) {
			for (Object[] obj:lst) {
				try {
					Long agentId = (Long)obj[0];
					Long count = (Long)obj[1];
					BigDecimal openAccFee = fee.multiply(new BigDecimal(count));
					agentCommissionFee = agentCommissionFeeService.getAgentCommissionFee(agentId, currentMonth+"", currentYear);
					if (agentCommissionFee == null) {
						agentCommissionFee = new AgentCommissionFee();
						agentCommissionFee.setPartnerID(agentId);
						agentCommissionFee.setMonth(currentMonth+"");
						agentCommissionFee.setYear(currentYear);
						agentCommissionFee.setCustomerBalanceFee(BigDecimal.ZERO);
						agentCommissionFee.setOpenAccountFee(openAccFee);
					}
					else {
						agentCommissionFee.setOpenAccountFee(openAccFee);
					}
					log.info("Saving the opening account fee value to agent Id: "+ agentId);
					agentCommissionFeeService.save(agentCommissionFee);
				} catch (Exception e) {
					log.error("Error while calculating the LakupandaiAccountOpeningFeeToAgent:" + obj[0],e);
				}
			}
		}
		log.info("Calculating the LakupandaiAccountOpeningFeeToAgents :: END");
	}
	
	private void calculateStartAndEndDates() {
		Calendar cal = Calendar.getInstance();
		currentMonth = cal.get(Calendar.MONTH) + 1;
		currentYear = cal.get(Calendar.YEAR);
		
		if (currentMonth == 1) {
			currentMonth = 12;
			currentYear = currentYear - 1;
		} else {
			currentMonth = currentMonth - 1;
		}
		
		switch (currentMonth) {
		case 1: case 3: case 5: case 7: case 8: case 10: case 12: {
			endDay = 31;
			break;
		}
		case 4: case 6: case 9: case 11: {
			endDay = 30;
			break;
		}
		default:
			if (currentYear % 4 == 0) 
				endDay = 29;
			else
				endDay = 28;
			break;
		}
		
		cal.clear();
		cal.set(currentYear, currentMonth-1, startDay, 0, 0, 0);
		startDate = cal.getTime();
		
		cal.clear();
		cal.set(currentYear, currentMonth-1, endDay, 23, 59, 59);
		endDate = cal.getTime();
	}
	
	private BigDecimal calculateFee(BigDecimal avgBalance, BigDecimal roi) {
		return moneyService.round(avgBalance.multiply(roi).divide(new BigDecimal(1200), 2, RoundingMode.HALF_EVEN));
	}
}
