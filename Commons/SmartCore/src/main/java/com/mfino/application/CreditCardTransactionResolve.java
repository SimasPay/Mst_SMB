package com.mfino.application;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.CreditCardTransactionQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.HibernateUtil;

public class CreditCardTransactionResolve {
	private Logger log = LoggerFactory.getLogger(this.getClass());;
	private CreditCardTransactionDAO ctDao;
	private CreditCardTransaction record;
	
public void resolve(){
	HibernateUtil.getCurrentSession().beginTransaction();
	log.info("In Credit Card Resolve Tool");
	CreditCardTransactionDAO dao = DAOFactory.getInstance().getCreditCardTransactionDAO();
	CreditCardTransactionQuery query = new CreditCardTransactionQuery();
	query.setTransStatus(CmFinoFIX.TransStatus_Pending);
	List<CreditCardTransaction> list = dao.get(query);
	HibernateUtil.getCurrentTransaction().rollback();
	if(!(list.size()>0)){
	return ;
	}
	ctDao = DAOFactory.getInstance().getCreditCardTransactionDAO();
	log.info("No of credit card Records with pending as status is "+ list.size());
	for(CreditCardTransaction card:list){	
		log.info("Processing Credit Card Transaction id " + card.getID());
		HibernateUtil.getCurrentSession().beginTransaction();
		try{
		record = ctDao.getById(card.getID());
		
         	if(record.getCommodityTransferFromCreditCardTransactionID()!=null && record.getCommodityTransferFromCreditCardTransactionID().size()==1){
         		CommodityTransfer ct =record.getCommodityTransferFromCreditCardTransactionID().iterator().next(); 
         		record.setTransactionID(ct.getID());	
         		record.setCCFailureReason(null); //updating the failure reason to null if there is CT record exists	         		
         		log.info("Updating the transactionid column to "+ ct.getID());     			
 				if(ct.getTransferStatus().equals(CmFinoFIX.TransferStatus_Completed))
         		{	
         			log.info("Updating the TransStatus to Successful for " +record.getID());
         			record.setTransStatus(CmFinoFIX.TransStatus_Successful);
         		}
 				else if(ct.getTransferStatus().equals(CmFinoFIX.TransferStatus_Failed)){
 					log.info("Updating the TransStatus to Failed for " +record.getID());
 					record.setTransStatus(CmFinoFIX.TransStatus_Failed);
 				}
 				dao.save(record);        				
 			} 
//         	else if(record.getPendingCommodityTransferFromCreditCardTransactionID()!=null && record.getPendingCommodityTransferFromCreditCardTransactionID().size()==1){
// 				PendingCommodityTransfer pct =record.getPendingCommodityTransferFromCreditCardTransactionID().iterator().next(); 
// 				 //updating the failure reason to null if there is PCT record exists
// 				if(pct.getTransferStatus().equals(CmFinoFIX.TransferStatus_Failed)){
// 					log.info("Updating the TransStatus to Failed for " +record.getID());
// 					record.setTransStatus(CmFinoFIX.TransStatus_Failed);
// 					log.info("Updating the transactionid  column to  "+ pct.getID());
// 					record.setCCFailureReason(null);
// 					record.setTransactionID(pct.getID());
// 	 				dao.save(record);
// 				}
// 			}
         	HibernateUtil.getCurrentTransaction().commit(); 
		}catch (Exception exp) {
			log.error("Exception while processing creditcardtransaction "+card.getID(),exp);
			HibernateUtil.getCurrentTransaction().rollback();
		}
	}
}
public static void main(String[] args) {
	CreditCardTransactionResolve resolve = new CreditCardTransactionResolve();
	resolve.resolve();
}
}
