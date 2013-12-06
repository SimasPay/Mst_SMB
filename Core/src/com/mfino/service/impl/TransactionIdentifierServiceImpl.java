package com.mfino.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionIdentifierDAO;
import com.mfino.domain.TransactionIdentifier;
import com.mfino.service.TransactionIdentifierService;

@Service("TransactionIdentifierServiceImpl")
public class TransactionIdentifierServiceImpl implements TransactionIdentifierService {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Creates the transactionIdentifer using the sourceMDN,timestamp and a random number generator and returns the same.
	 * @param uniqueIdMDN
	 * @return
	 */
	public String generateTransactionIdentifier(String uniqueIdMDN){
		Date date = new Date();
		Random randomNumberGen = new Random();
		SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMddHHmmssS");

		//creating the transactionIdentifier
		String transactionIdentifier=uniqueIdMDN+"-"+formatter.format(date.getTime())+"-"+randomNumberGen.nextInt(1000000);
		return transactionIdentifier;
	}
	/**
	 * This method saves the transactionIdentifier and sctlID in the database.This method is called for only those transactions that have a transactionIdentifier. 
	 * @param transactionIdentifier
	 * @param sctlID
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void createTrxnIdentifierDbEntry(String transactionIdentifier,Long sctlID){
		//saving into the db table
		TransactionIdentifierDAO trxnIdentifierDAO = DAOFactory.getInstance().getTransactionIdentifierDAO();
		TransactionIdentifier trxnIdentifier = new TransactionIdentifier();
		trxnIdentifier.setTransactionIdentifier(transactionIdentifier);
		trxnIdentifier.setServiceChargeTransactionLogID(sctlID);
		trxnIdentifierDAO.save(trxnIdentifier);
		log.info("Added the sctl ID:" +sctlID+ "to the Transaction Identifier table with transaction identifier ="+transactionIdentifier);
	}
}
