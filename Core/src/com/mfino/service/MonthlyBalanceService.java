package com.mfino.service;

import java.util.List;

import com.mfino.domain.MonthlyBalance;
import com.mfino.domain.Pocket;

public interface MonthlyBalanceService {

	public void save(MonthlyBalance monthlyBalance);
	
	public List<Object[]> getCommissionFeeDetails(String month, int year);
	
	public MonthlyBalance getByDetails(Pocket pocket, String month, int year);
}
