package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MonthlyBalanceDAO;
import com.mfino.domain.MonthlyBalance;
import com.mfino.service.MonthlyBalanceService;

@Service("MonthlyBalanceServiceImpl")
public class MonthlyBalanceServiceImpl implements MonthlyBalanceService {
	private MonthlyBalanceDAO monthlyBalanceDAO = DAOFactory.getInstance().getMonthlyBalanceDAO();

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(MonthlyBalance monthlyBalance) {
		monthlyBalanceDAO.save(monthlyBalance);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<Object[]> getCommissionFeeDetails(String month, int year) {
		return monthlyBalanceDAO.getCommissionFeeDetails(month, year);
	}
}
