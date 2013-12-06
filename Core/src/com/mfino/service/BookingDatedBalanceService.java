package com.mfino.service;

import java.util.Date;

import com.mfino.domain.BookingDatedBalance;
import com.mfino.domain.Pocket;

public interface BookingDatedBalanceService {

	public BookingDatedBalance getExactDatedEntry(Pocket pocket, Date date);
	public BookingDatedBalance getPreDatedEntry(Pocket pocket, Date date);
	public void save(BookingDatedBalance bookingDatedBalance);
	public int deleteBookingDatedEntries(Date date);
}
