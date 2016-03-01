package com.mfino.service;

import java.util.List;

import com.mfino.domain.MonthlyBalance;

public interface MonthlyBalanceService {

	public void save(MonthlyBalance monthlyBalance);
	
	public List<Object[]> getCommissionFeeDetails(String month, int year);
}
