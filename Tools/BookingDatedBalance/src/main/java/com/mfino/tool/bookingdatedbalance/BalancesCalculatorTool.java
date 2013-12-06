package com.mfino.tool.bookingdatedbalance;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.mfino.dao.BookingDatedBalanceDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.service.impl.BalancesCalculatorServiceImpl;
import com.mfino.util.DateUtil;

public class BalancesCalculatorTool {
	
	public Log log = LogFactory.getLog(this.getClass());
	public static final String dateFormat = "dd/MM/yyyy";
	
	SessionIntializationService sessionIntializer;
	
	public BalancesCalculatorTool(){
		sessionIntializer = new SessionIntializationService();
		sessionIntializer.initialize();
		sessionIntializer.initializeDependencies();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("Booking Dated Balance Calculator Tool");
		if(args.length < 2){
			System.out.println("Please provide a date or daterange (dd/MM/yyyy) and path for Spring context file");
			return;
		}
		Date startDate = null;
		try{
			startDate = DateUtil.getDate(args[0], dateFormat);
			
			System.out.println("Creating Spring Application Context");
			ApplicationContext context = new FileSystemXmlApplicationContext(args[1]);
			System.out.println("Spring Application Context created successfully");
			
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		BalancesCalculatorTool balancesCalculatorTool = new BalancesCalculatorTool();
		balancesCalculatorTool.calculate(startDate);
		System.out.println("Booking Dated Balance Calculation Completed");
	}

	private void calculate(Date startDate) {
		Date currentDate = new Date(System.currentTimeMillis());
		Date endDate = DateUtil.addDays(currentDate, -1);
		
		log.info("BalancesCalculatorTool startDate="+DateUtil.getFormattedDate(startDate, dateFormat)+", endDate="+endDate+", currentDate="+currentDate);
		
		BalancesCalculatorServiceImpl bookingBalanceBuilder = new BalancesCalculatorServiceImpl();
		
		try{
			
			log.info("Deleting all Booing Dated Value entries on or before "+DateUtil.getFormattedDate(startDate));
			BookingDatedBalanceDAO bdvDao = DAOFactory.getInstance().getBookingDatedBalanceDao();
			int count = bdvDao.deleteBookingDatedEntries(startDate);
			
			log.info("BalancesCalculatorTool :: calculate BDV delete count="+count);
			
			do{
				bookingBalanceBuilder.buildBookingBalances(startDate);
				startDate = DateUtil.addDays(startDate, 1);
			}
			while(startDate.before(endDate));
		}catch (Exception e) {
			log.info("BalancesCalculatorTool :: calculate ", e);
			e.printStackTrace();
		}
	}
}

