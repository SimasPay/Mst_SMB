/**
 * 
 */
package com.mfino.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.domain.TransactionType;
import com.mfino.service.TransactionTypeService;

/**
 * Service class for datatbase access related to TransactionType table
 * @author Sreenath
 *
 */
@Service("TransactionTypeServiceImpl")
public class TransactionTypeServiceImpl implements TransactionTypeService{

	private static Logger log = LoggerFactory.getLogger(TransactionTypeServiceImpl.class);

	/**
	 * Returns the TransactionType by the TransactionTypeID
	 * @param TransactionTypeId
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public TransactionType getTransactionTypeById(Long transactionTypeId){
		TransactionType transactionType = null;
		if(transactionTypeId!=null){
			log.info("Getting the TransactionType for id: "+transactionTypeId);
			TransactionTypeDAO transactionTypeDao = DAOFactory.getInstance().getTransactionTypeDAO();
			transactionType = transactionTypeDao.getById(transactionTypeId);
		}
		return transactionType;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public TransactionType getTransactionTypeByName(String transactionName){
		TransactionType transactionType = null;

		if(transactionName!=null){
			log.info("Getting the TransactionType for id: "+transactionName);
			TransactionTypeDAO transactionTypeDao = DAOFactory.getInstance().getTransactionTypeDAO();
			transactionType = transactionTypeDao.getTransactionTypeByName(transactionName);
		}
		return transactionType;
	}
}
