package com.mfino.zenith.airtime.visafone.impl;

import java.text.SimpleDateFormat;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.VisafoneTxnGeneratorDao;
import com.mfino.domain.VisafoneTxnGenerator;
import com.mfino.hibernate.Timestamp;

/**
 * @author Sasi
 * Transaction id generator for 
 */
public class VisafoneAirtimeTransactionIdGenerator {
	
	public String getTransactionId(){
		String transactionId = "";
		VisafoneTxnGenerator generator = getVisafoneTxnGenerator();
		
		Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
		Timestamp dbTimeStamp = generator.getTxnTimestamp();
		
		if(dbTimeStamp.equals(currentTimeStamp)){
			Integer txnCount = generator.getTxnCount();
			generator.setTxnCount(txnCount + 1);
			
			if(generator.getTxnCount() > 99){
				try {
					Thread.sleep(1000);
					return getTransactionId();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		else{
			generator.setTxnTimestamp(currentTimeStamp);
			generator.setTxnCount(0);
		}
		
		saveVisafoneTxnGenerator(generator);
		
		String strTimestamp = getTimeStampAsString(currentTimeStamp);
		String strTxnCount = ("" + generator.getTxnCount()).length() == 1 ? "0" + generator.getTxnCount() : ""+generator.getTxnCount();
		
		transactionId = strTimestamp + strTxnCount;
		
		return transactionId;
	}
	
	protected VisafoneTxnGenerator getVisafoneTxnGenerator()
	{
		VisafoneTxnGeneratorDao visafoneTxnGeneratorDao = DAOFactory.getInstance().getVisafoneTxnGeneratorDao();
		VisafoneTxnGenerator vTxnGen = visafoneTxnGeneratorDao.getVisafoneTxnGenerator();
		
		if(vTxnGen == null){
			vTxnGen = new VisafoneTxnGenerator();
			vTxnGen.setTxnTimestamp(new Timestamp(System.currentTimeMillis()));
			vTxnGen.setTxnCount(0);
			visafoneTxnGeneratorDao.save(vTxnGen);
		}
		
		return vTxnGen;
	}
	
	protected VisafoneTxnGenerator saveVisafoneTxnGenerator(VisafoneTxnGenerator vTxnGenerator)
	{
		VisafoneTxnGeneratorDao visafoneTxnGeneratorDao = DAOFactory.getInstance().getVisafoneTxnGeneratorDao();
		
		visafoneTxnGeneratorDao.save(vTxnGenerator);
		
		return vTxnGenerator;
	}
	
	private String getTimeStampAsString(Timestamp ts){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(ts);
	}
}
