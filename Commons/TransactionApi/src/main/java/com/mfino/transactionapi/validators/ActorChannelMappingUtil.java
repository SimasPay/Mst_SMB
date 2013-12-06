package com.mfino.transactionapi.validators;

import org.apache.commons.lang.StringUtils;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.transactionapi.vo.TransactionDetails;
/**
 * 
 * @author Hemanth
 *
 */


public class ActorChannelMappingUtil {

// handle getting transactionTypeID from transactionDetails Here
	
public Long getTransactionID(TransactionDetails transactionDetails){
	
	TransactionTypeDAO tDAO=DAOFactory.getInstance().getTransactionTypeDAO();
	String transactionName = null;
	if(StringUtils.isNotBlank(transactionDetails.getTransactionName()))
	{
		// as there is only BillInquiry not Bill Confirm
		if(ServiceAndTransactionConstants.TRANSACTION_BILL_INQUIRY.equals(transactionDetails.getTransactionName()))   
		{
				transactionName=transactionDetails.getTransactionName();
		}else{
			//Checking actor channel permission for confirm requests eventhough inquiry request is received
			transactionName=transactionDetails.getTransactionName().replace("Inquiry", "");
		}
	}
	
	if(StringUtils.isNotBlank(transactionName) && null!=tDAO.getTransactionTypeByName(transactionName))
	{
	return tDAO.getTransactionTypeByName(transactionName).getID();
	}
	return null;
	
	
}
}
