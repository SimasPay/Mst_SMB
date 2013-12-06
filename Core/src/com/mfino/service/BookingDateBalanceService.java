package com.mfino.service;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.BookingDatedBalanceDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.BookingDatedBalance;
import com.mfino.domain.Pocket;
import com.mfino.hibernate.Timestamp;
import com.mfino.util.DateTimeUtil;

/**
 *
 * @author Maruthi
 */
public class BookingDateBalanceService extends BaseService<BookingDatedBalance> {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	BookingDatedBalanceDAO bookingDatedBalanceDAO = DAOFactory.getInstance().getBookingDatedBalanceDao();
	
	public BookingDateBalanceService() {
		super();
	}
	
	
	public BookingDatedBalance getBookingDatedBalances(Pocket pocket,Date date){
		BookingDatedBalance bookingDatedBalance = new BookingDatedBalance();
		if(pocket != null && date != null) {
			log.debug("Getting the booking dated balance of pocket with id "+ pocket.getID() + " for the date :" + date);
			date = DateTimeUtil.getStartOfDay(date);
			log.debug("Modified date is : " + date);
			BookingDatedBalance exactDateEntry  = bookingDatedBalanceDAO.getExactDatedEntry(pocket, date);
			if(exactDateEntry !=null) {
				log.debug("Got exact date entry");
				exactDateEntry.setBookingDate(new Timestamp(date));
				return exactDateEntry;
			}
			
			bookingDatedBalance.setTotalCredit(BigDecimal.ZERO);
			bookingDatedBalance.setTotalDebit(BigDecimal.ZERO);
			bookingDatedBalance.setNetTurnOver(BigDecimal.ZERO);
			bookingDatedBalance.setPocketID(pocket.getID());
				
			BookingDatedBalance preDateEntry  = bookingDatedBalanceDAO.getPreDatedEntry(pocket, date);
			
			if(preDateEntry != null){
				log.debug("Got preDate entry");
				bookingDatedBalance.setOpeningBalance(preDateEntry.getClosingBalance());
				bookingDatedBalance.setClosingBalance(preDateEntry.getClosingBalance());
			}else{
				log.debug("Setting Open & close balance to Zero");
				bookingDatedBalance.setOpeningBalance(BigDecimal.ZERO);
				bookingDatedBalance.setClosingBalance(BigDecimal.ZERO);
			}
		}				
		return bookingDatedBalance;		
	}		
}
