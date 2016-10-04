/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.AgentCommissionFee;
import com.mfino.domain.BookingDatedBalance;
import com.mfino.domain.MonthlyBalance;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.scheduler.service.AverageMonthlyBalanceService;
import com.mfino.service.AgentCommissionFeeService;
import com.mfino.service.BookingDatedBalanceService;
import com.mfino.service.MailService;
import com.mfino.service.MoneyService;
import com.mfino.service.MonthlyBalanceService;
import com.mfino.service.PartnerService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ConfigurationUtil;

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
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	private HibernateTransactionManager txManager;
	private int currentMonth;
	private int currentYear;
	private Date startDate;
	private Date endDate;
	private int startDay = 1;
	private int endDay = 31;
	private BigDecimal cFee = BigDecimal.ZERO;
	
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
			cFee = systemParametersService.getBigDecimal(SystemParameterKeys.CUSTOMER_BALANCE_FEE);
		
			List<Long> lst = pocketService.getLakuPandaiPockets();
			if (CollectionUtils.isNotEmpty(lst)) {
				File file = createFile("Interest_" + currentMonth + "_" + currentYear + ".csv");
				FileWriter writer = openFile(file);
				for (Long pocketId:lst) {
					try {
						String data = calculateAverageMonthlyBalance(pocketId);
						writer.append(data);
						writer.append('\n');
					} catch (Exception e) {
						log.error("Error while calculating the average monthly balance for pocketid: " + pocketId , e);
					}
				}
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					log.error("Error in closing the document for interest", e);
				}
				sendMail(ConfigurationUtil.getCapitalizationAuthorizedEmail(), "Interest report for " + currentMonth + "/" + currentYear, 
						"Attached Interest report for " + currentMonth + "/" + currentYear, file.toString());
			}
			// Calculates the Agent commission fee after monthly balance of the customers is calculated
			calculateAgentCommissionFee();
		} catch (Exception e) {
			log.error("Error: While Processing ledger entries " + e.getMessage(), e);
		} 
		log.info("calculateAverageMonthlyBalanceForLakupandai :: END");
	}
	
	private File createFile(String fileName) {
		String outputDir = ConfigurationUtil.getReportDir() + File.separator + "capitalization";
		if (!new File(outputDir).exists()) {
			new File(outputDir).mkdirs();
		}
		File file = new File(outputDir + File.separator + fileName);
		return file;
	}
	
	private FileWriter openFile(File file) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			log.error("Failed to create a document: " + file.getAbsolutePath(), e);
		}
		return writer;
	}

	private String calculateAverageMonthlyBalance(Long pocketId) {
		log.info("Calculating the Avergare monthly balance for pocket id: "+ pocketId);
		List<BookingDatedBalance> lstBookingDatedBalances = null;
		BigDecimal totalMonthlyBalance = BigDecimal.ZERO;
		HashMap<Integer, BigDecimal> monthlyBalances = new HashMap<Integer, BigDecimal>();
		String result = "";
		
		Pocket p = pocketService.getById(pocketId);
		MonthlyBalance mBalance = monthlyBalanceService.getByDetails(p, currentMonth+"", currentYear);
		if (mBalance == null) {
			log.info("Getting the balance detaild for pocket:"+ pocketId + " start date:" + startDate + "  end date:" + endDate);
			lstBookingDatedBalances = bookingDatedBalanceService.getDailyBalanceForPocket(pocketId, startDate, endDate);
			
			if (CollectionUtils.isNotEmpty(lstBookingDatedBalances)) {
				for (BookingDatedBalance bdb:lstBookingDatedBalances) {
					Date bookingDate = bdb.getBookingdate();
					BigDecimal bookingBalance = new BigDecimal(bdb.getClosingbalance());
					monthlyBalances.put(bookingDate.getDate(), bookingBalance);
				}
			}
			SubscriberMdn subMdn = p.getSubscriberMdn();
			int closingDay = 0;
			if ( (CmFinoFIX.SubscriberStatus_PendingRetirement.intValue() == subMdn.getStatus()) || 
					(CmFinoFIX.SubscriberStatus_Retired.intValue() == subMdn.getStatus()) ) {
				closingDay = subMdn.getStatustime().getDate();	
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
								monthlyBalances.put(i, new BigDecimal(preDayBalance.getClosingbalance()));
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
			
			mBalance = new MonthlyBalance();
			mBalance.setPocket(p);
			mBalance.setMonth(currentMonth+"");
			mBalance.setYear(currentYear);
			
			BigDecimal avgMonthlyBalance = totalMonthlyBalance.divide(new BigDecimal(endDay), 2, RoundingMode.HALF_EVEN);
			mBalance.setAveragemonthlybalance(avgMonthlyBalance);
			
			BigDecimal roi = p.getPocketTemplate().getInterestrate();
			if (roi == null) {
				roi = BigDecimal.ZERO;
			}
			mBalance.setInterestcalculated(calculateFee(avgMonthlyBalance, roi));

			mBalance.setAgentcommissioncalculated(calculateFee(avgMonthlyBalance, cFee));
			monthlyBalanceService.save(mBalance);
			
			result = subMdn.getMdn()+","+mBalance.getInterestcalculated();
		}
		else {
			log.info(String.format("Avergare monthly balance for pocket id:%S for month/year:%S/%S is already calculated", 
					pocketId.toString(),currentMonth,currentYear));			
		}
		return result;
	}
	
	private void calculateAgentCommissionFee() {
		AgentCommissionFee agentCommissionFee = null;
		log.info("Calculating the Agent Commission Fee for month : "+ currentMonth + " and year: " + currentYear);
		List<Object[]> lst = monthlyBalanceService.getCommissionFeeDetails(currentMonth+"", currentYear);
		if (CollectionUtils.isNotEmpty(lst)) {
			File file = createFile("AgentCommissionFee_"+currentMonth+"_"+currentYear+".csv");
			FileWriter writer = openFile(file);
			for (Object[] obj:lst) {
				try {
					Long agentId = (Long)obj[0];
					BigDecimal cFee = (BigDecimal)obj[1];
					agentCommissionFee = agentCommissionFeeService.getAgentCommissionFee(agentId, currentMonth+"", currentYear);
					if (agentCommissionFee == null) {
						agentCommissionFee = new AgentCommissionFee();
						agentCommissionFee.setPartnerid(BigDecimal.valueOf(agentId));
						agentCommissionFee.setMonth(currentMonth+"");
						agentCommissionFee.setYear(currentYear);
						agentCommissionFee.setCustomerbalancefee(cFee);
						agentCommissionFee.setOpenaccountfee(BigDecimal.ZERO);
					}
					else {
						agentCommissionFee.setCustomerbalancefee(cFee);
					}
					log.info("Saving the Agent commission balance for agent Id: "+ agentId);
					agentCommissionFeeService.save(agentCommissionFee);
					String agentMDN = partnerService.getMDN(agentId);
					writer.append(agentMDN+","+cFee);
					writer.append('\n');
				} catch (Exception e) {
					log.error("Error while calculating the commission fee for agent Id: "+ obj[0] , e);
				}
			}

			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				log.error("Error in closing the document for agentCommissionFee", e);
			}
			sendMail(ConfigurationUtil.getCapitalizationAuthorizedEmail(), "Agent Commission Fee report for " + currentMonth + "/" + currentYear, 
					"Attached Agent Commission Fee report for " + currentMonth + "/" + currentYear, file.toString());
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
			File file = createFile("AccountOpeningFeeToAgents_"+currentMonth+"_"+currentYear+".csv");
			FileWriter writer = openFile(file);
			for (Object[] obj:lst) {
				try {
					Long agentId = (Long)obj[0];
					Long count = (Long)obj[1];
					BigDecimal openAccFee = fee.multiply(new BigDecimal(count));
					agentCommissionFee = agentCommissionFeeService.getAgentCommissionFee(agentId, currentMonth+"", currentYear);
					if (agentCommissionFee == null) {
						agentCommissionFee = new AgentCommissionFee();
						agentCommissionFee.setPartnerid(BigDecimal.valueOf(agentId));
						agentCommissionFee.setMonth(currentMonth+"");
						agentCommissionFee.setYear(currentYear);
						agentCommissionFee.setCustomerbalancefee(BigDecimal.ZERO);
						agentCommissionFee.setOpenaccountfee(openAccFee);
					}
					else {
						agentCommissionFee.setOpenaccountfee(openAccFee);
					}
					log.info("Saving the opening account fee value to agent Id: "+ agentId);
					agentCommissionFeeService.save(agentCommissionFee);
					String agentMDN = partnerService.getMDN(agentId);
					writer.append(agentMDN+","+openAccFee);
					writer.append('\n');
				} catch (Exception e) {
					log.error("Error while calculating the LakupandaiAccountOpeningFeeToAgent:" + obj[0],e);
				}
			}
			
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				log.error("Error in closing the document for AccountOpeningFeeToAgents", e);
			}
			sendMail(ConfigurationUtil.getCapitalizationAuthorizedEmail(), "Account Opening Fee To Agents report for " + currentMonth + "/" + currentYear, 
					"Attached Account Opening Fee To Agents report for " + currentMonth + "/" + currentYear, file.toString());

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
	
	private void sendMail(String emailRecipients,String subject, String message, String attachmentFileName){
		log.info("sending mail to " + emailRecipients);
		try{
			String[] emailRecipientsList = emailRecipients.split(",");
			File attachmentFile = new File(attachmentFileName);
			for(int i=0; i< emailRecipientsList.length; i++){
				if(mailService.isValidEmailAddress(emailRecipientsList[i])){
					mailService.asyncSendEmailWithAttachment(emailRecipientsList[i].trim(), "", subject, message, attachmentFileName);
				}
			}
		}catch(Exception e){
			log.error("Error while sending the mail for: "+ attachmentFileName, e);
		}
		
	}
}
