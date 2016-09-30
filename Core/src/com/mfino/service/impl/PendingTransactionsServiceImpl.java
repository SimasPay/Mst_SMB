package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingTransactionsFileDAO;
import com.mfino.domain.PendingTxnsFile;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.service.PendingTransactionsService;

@Service("PendingTransactionsServiceImpl")
public class PendingTransactionsServiceImpl implements PendingTransactionsService{
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void savePendingTransactions(PendingTxnsFile fileToSave) throws MfinoRuntimeException{
        PendingTransactionsFileDAO  dao = DAOFactory.getInstance().getPendingTransactionsFileDAO();
        try{
        	dao.save(fileToSave);
        }catch(Exception e){
        	throw new MfinoRuntimeException(e);
        }
	}
}
