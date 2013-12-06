package com.mfino.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BookingDatedBalanceDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.BookingDatedBalance;
import com.mfino.domain.Pocket;
import com.mfino.service.BookingDatedBalanceService;

@Service("BookingDatedBalanceServiceImpl")
public class BookingDatedBalanceServiceImpl implements
		BookingDatedBalanceService {

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public BookingDatedBalance getExactDatedEntry(Pocket pocket, Date date) {
		BookingDatedBalanceDAO bookingDatedBalanceDAO = DAOFactory
				.getInstance().getBookingDatedBalanceDao();
		return bookingDatedBalanceDAO.getExactDatedEntry(pocket, date);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public BookingDatedBalance getPreDatedEntry(Pocket pocket, Date date) {
		BookingDatedBalanceDAO bookingDatedBalanceDAO = DAOFactory
				.getInstance().getBookingDatedBalanceDao();
		return bookingDatedBalanceDAO.getPreDatedEntry(pocket, date);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(BookingDatedBalance bookingDatedBalance) {
		BookingDatedBalanceDAO bookingDatedBalanceDAO = DAOFactory
				.getInstance().getBookingDatedBalanceDao();
		bookingDatedBalanceDAO.save(bookingDatedBalance);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public int deleteBookingDatedEntries(Date date){
		BookingDatedBalanceDAO bookingDatedBalanceDAO = DAOFactory
				.getInstance().getBookingDatedBalanceDao();
		return bookingDatedBalanceDAO.deleteBookingDatedEntries(date);
	}

}
