package com.mfino.scheduler.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.scheduler.service.BookingDatedValueService;
import com.mfino.service.BalancesCalculatorService;
import com.mfino.service.BookingDatedBalanceService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.DateUtil;

/**
 * @author Sasi
 */
@Service("BookingDatedValueServiceImpl")
public class BookingDatedValueServiceImpl  implements BookingDatedValueService{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParameterService;
	
	@Autowired
	@Qualifier("BalancesCalculatorServiceImpl")
	private BalancesCalculatorService balanceCalculatorService;
	
	@Autowired
	@Qualifier("BookingDatedBalanceServiceImpl")
	private BookingDatedBalanceService bookingDatedBalanceService;
	
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	public void constructBookingDatedBalance(){
		log.info("BookingDatedValueServiceImpl :: constructBookingDatedBalance");
		
			
			Date currentDate = new Date(System.currentTimeMillis());
			Date previousDate = DateUtil.addDays(currentDate, -1);
	
			Date lastBdvDate = null;
			String strLastBdvDate = null;
			strLastBdvDate=systemParameterService.getString(SystemParameterKeys.LAST_BDV_DATE);
			
			try{
				lastBdvDate = DateUtil.getDate(strLastBdvDate);
			}
			catch(Exception e){
				log.error("BookingDatedValueServiceImpl constructBookingDatedBalance() Eror getting BDV from System_Parameters", e);
			}
			
			if(null == lastBdvDate){
				lastBdvDate = previousDate;
			}
			else{
				lastBdvDate = DateUtil.addDays(lastBdvDate, 1);
			}
			
			int count = bookingDatedBalanceService.deleteBookingDatedEntries(lastBdvDate);
			
			log.info("BalancesCalculatorTool :: calculate BDV delete count="+count);
			log.info("BookingDatedServiceImpl :: constructBookingDatedBalance lastBdvDate="+DateUtil.getFormattedDate(lastBdvDate)+", currentDate="+DateUtil.getFormattedDate(currentDate)+", previousDate="+DateUtil.getFormattedDate(previousDate));
			
			while(lastBdvDate.before(previousDate)){
				log.info("BookingDatedServiceImpl :: constructBookingDatedBalance lastBdvDate="+DateUtil.getFormattedDate(lastBdvDate)+", currentDate="+DateUtil.getFormattedDate(currentDate));
				try{
					calculateBookingDatedBalance(lastBdvDate);
					systemParameterService.setBookDatedValue(lastBdvDate);
				}catch(Exception e){
					log.error("BookingDatedValueServiceImpl :: error calculating BDV ", e);
				}
				
				lastBdvDate = DateUtil.addDays(lastBdvDate, 1);
			}			
	}
	
	public void calculateBookingDatedBalance(Date date){
		log.info("BookingDatedValueServiceImpl :: constructBookingDatedBalance date="+DateUtil.getFormattedDate(date));
		try{
			balanceCalculatorService.buildBookingBalances(date);
		}
		catch(Exception e){
			log.error("BookingDatedValueServiceImpl :: calculateBookingDatedBalance ERROR",e);
		}
		
		log.info("BookingDatedValueServiceImpl :: constructBookingDatedBalance end");
	}
}
