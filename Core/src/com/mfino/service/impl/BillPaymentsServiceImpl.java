package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BillPaymentsDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.domain.BillPayments;
import com.mfino.service.BillPaymentsService;

@Service("BillPaymentsServiceImpl")
public class BillPaymentsServiceImpl implements BillPaymentsService {
	/**
	 * Gets the list of all billPayment records matching the query
	 * @param bpq
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<BillPayments> get(BillPaymentsQuery bpq) {
		BillPaymentsDAO bpDAO = DAOFactory.getInstance().getBillPaymentDAO();
		return bpDAO.get(bpq);
	}
	
	/**
	 * Saves the BillPayments record to database
	 * @param bp
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void save(BillPayments bp){
		BillPaymentsDAO bpDAO = DAOFactory.getInstance().getBillPaymentDAO();
		bpDAO.save(bp);
	}
}
