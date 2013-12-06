package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BulkBankAccountDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.BulkBankAccount;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.service.BulkBankAccountService;

@Service("BulkBankAccountServiceImpl")
public class BulkBankAccountServiceImpl implements BulkBankAccountService{
	/**
	 * Saves the BulkBankAccount record
	 * @param bba
	 * @throws MfinoRuntimeException
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void save(BulkBankAccount bba) throws MfinoRuntimeException{
		BulkBankAccountDAO bulkDAO = DAOFactory.getInstance().getBulkBankAccountDAO();
		try
		{
			bulkDAO.save(bba);
		}
		catch (Exception e) {
			throw new MfinoRuntimeException(e);
		}
	}
}
