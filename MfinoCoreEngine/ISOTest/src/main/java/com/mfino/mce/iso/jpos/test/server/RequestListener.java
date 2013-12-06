package com.mfino.mce.iso.jpos.test.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;

public class RequestListener implements ISORequestListener 
{	

	ExecutorService service;
	AccountDetails ad = Configuration.getAccountDetailsObject();
	public RequestListener()
	{	
		service = Executors.newFixedThreadPool(10);
	}
	
	@Override
	public boolean process(ISOSource source, ISOMsg m) 
	{	
		service.execute(new ISORunnable(m, source));
		return false;
	}

	private class ISORunnable implements Runnable {

		private ISOMsg		msg;
		private ISOSource	source;
		 
		
		
		public ISORunnable(ISOMsg msg, ISOSource source) {
			this.msg = msg;
			this.source = source;
		}
		/**
		 * @param type
		 * @throws ISOException
		 */
		
		//this method returns a trxnDetails object with details about the transaction filled in.it takes a int argument which is 
		//type=0 for balance type or history transaction,1 for debitting ,anything for creditting
		public TrxnDetails getTxnDetails(int type) throws ISOException{
			TrxnDetails trxn = new TrxnDetails();
			if(type==1 || type==0){
				trxn.setId((String) msg.getValue("102"));//id for debit from field 102
			}else{
				trxn.setId((String) msg.getValue("103"));//id for credit from field 103
			}
				trxn.setDate(TransactionDate.getTransactionDate());//date at which the transaction was recorde by our system
			
			if(type == 0){
				trxn.setCredit(Constants.DEFAULT_AMOUNT);//credit
				trxn.setDebit(Constants.DEFAULT_AMOUNT);//debit
			}else if(type == 1 ){
				trxn.setCredit(Constants.DEFAULT_AMOUNT);//credit
				trxn.setDebit((String)msg.getValue("4"));//debit
			}else{
				trxn.setCredit((String)msg.getValue("4"));//credit
				trxn.setDebit(Constants.DEFAULT_AMOUNT);//debit				
			}
			
			trxn.setInitBalance(Configuration.getConfigObject().getInitialBalance());//initbalance
			trxn.setStan((String) msg.getValue("11"));//stan
			trxn.setTrxnType(msg.getValue(3).toString().substring(0, 2));//trxntype
			trxn.setTrxnNo(Configuration.getConfigObject().getTrxnNo());//no of transactions to be stored in file
			return trxn;
		}

		@Override
		public void run() {
			try {
				if (msg.getMTI().equals("0200")) {
					msg.setResponseMTI();
					if (msg.getValue(3).toString().substring(0, 2).equals("31")) {
						msg.set(39, "00");
						if(Configuration.getConfigObject().isMockModeOn()){
						TrxnDetails trxn = getTxnDetails(0);//0 for check balance
						ad.createFile(trxn);//creating file if it does not exist
						String currentBalance = ad.getBalance(trxn.getId());//getting the balance after the last transaction
						while(currentBalance.length()<12){
							currentBalance = "0" + currentBalance;
						}
						msg.set(54,"2001566C0001255713702002566C"+currentBalance);
						}else{
							msg.set(54,"2001566C0001255713702002566C000025508870");
						}
						source.send(msg);
					}
					else if(msg.getValue(3).toString().substring(0, 2).equals("30")) 
					{
						msg.set(38,"123456");
						msg.set(39, "00");
					}
					else if(msg.getValue(3).toString().substring(0, 2).equals("50")) 
					{
						//sleep for 40 seconds so that reversal code is trigerred
						//Thread.sleep(40000);
						if(Configuration.getConfigObject().isMockModeOn()){
							TrxnDetails trxn = getTxnDetails(2);//2 for creating the credit file first
							trxn.setTrxnType("31");//since 31 is a code of checking balance.31 is used so tht no file write occurs if file is already present
							//creating the credit and debit files with default values if they are non-existent
							ad.createFile(trxn);
							trxn = getTxnDetails(0);//0 here for cheking debit > or < balance
							trxn.setTrxnType("31");
							ad.createFile(trxn);						
							String currentBalance = ad.getBalance(trxn.getId());
							while(currentBalance.length()<12){
								currentBalance = "0" + currentBalance;
							}
							//parsing field 4 to do an integer check
							int  debitAmt = Integer.parseInt((String)msg.getValue("4"));
							
							//checking if debit amt is greater than balance
							//if it is true return error else note the transaction into file
							if(debitAmt>=Integer.parseInt(currentBalance)){
								msg.set(39, "06");
								msg.set(38,"123456");
							}
						//--------------------------------------------------------------------------------
							else{
								trxn = getTxnDetails(1);//debit file
								ad.createFile(trxn);
								trxn = getTxnDetails(2);//credit file
								ad.createFile(trxn);
								msg.set(39, "00");
								msg.set(38,"123456");
							}
						}
						else
						{
							msg.set(39, "00");
							msg.set(38,"123456");
						}
						
						
					}
					else if(msg.getValue(3).toString().substring(0, 2).equals("38")) 
					{
						
						if(Configuration.getConfigObject().isMockModeOn()){
							TrxnDetails trxn = getTxnDetails(0);//0 for transaction history since no credit or debit to be made
							ad.createFile(trxn);
							//ad.getHistory returns the transaction data in the required format
							String miniStatement = "ACC_ID1|DATE_TIME|TRAN_AMOUNT|SEQ_NR~" + ad.getHistory(trxn.getId());
							msg.set(39, "00");
							msg.set(48,miniStatement);
						}else{
							msg.set(39, "00");
							msg.set(48,"TERM_ID|DATE_TIME|ACC_ID1|TRAN_TYPE|TRAN_AMOUNT|FROM_ACC|CURR_CODE~" +
									"00000000|20110413000000|0000000000000000000000000072|91|4000000||566~" +
									"00000000|20110330000000|0000000000000000000000000072|91|800000||566~");
						}
					}					
					else
					{
						msg.set(39, "00");
						msg.set(38,"123456");
					}
				}
				else if (msg.getMTI().equals("0202")) {
					msg.setResponseMTI();
					msg.set(39, "00");
				}
				//reversal request
				else if(msg.getMTI().equals("0420"))
				{
					msg.setResponseMTI();
					msg.set(39, "00");
				}
				else if(msg.getMTI().equals("0800"))
				{
					msg.setResponseMTI();
					msg.set(39, "00");
				}
						
				source.send(msg);

			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}