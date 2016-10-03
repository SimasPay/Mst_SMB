package com.mfino.tools.adjustments;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.QueryConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.LedgerDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.dao.query.LedgerQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Ledger;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMTransactionAdjustments;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.tools.adjustments.services.TxnAdjustmentService;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class InquiryCreatorImpl implements InquiryCreator {

	private static Logger	       log	= LoggerFactory.getLogger(InquiryCreatorImpl.class);
	private String	               channel;

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void process(Exchange exchange) throws Exception {

		BackendResponse response = new BackendResponse();
		MCEMessage mce = new MCEMessage();

		String str = exchange.getIn().getBody(String.class).trim();

		log.info("creating CMBankAccountToBankAccount from {}", str);


		String[] split = str.split(" ");
		if(split.length<4 || split.length>5)
		{
			String error = "Manual Adjustments: Invalid number of arguments. Expected Format is: SourcePocket DestPocket Amount RefID Type";
			log.error(error);
			response.setDescription(error);
			mce.setRequest(response);
			mce.setResponse(response);
			exchange.getIn().setBody(mce);
			return;
		}

		Long srcPocketID = Long.parseLong(split[0]);
		Long destPocketID = Long.parseLong(split[1]);
		BigDecimal amount = new BigDecimal(split[2]);
		Long refId = Long.parseLong(split[3]);
		
		int type = 1;
		//Type 1 means pocket needs to be adjusted without any update in source pocket balance
		//Type 2 means the existing ledger needs to be corrected and then do the money movement
		//
		if(split.length==5)
		{
			type = Integer.parseInt(split[4]);
		}
		
		log.info("Setting the adjustment paramters from file to message");
		CMTransactionAdjustments trxnAdjustmentDetails = new CMTransactionAdjustments();
		trxnAdjustmentDetails.setSourcePocketID(srcPocketID);
		trxnAdjustmentDetails.setDestPocketID(destPocketID);
		trxnAdjustmentDetails.setAmount(amount);
		trxnAdjustmentDetails.setSctlId(refId);
		trxnAdjustmentDetails.setAdjustmentType(type);		
		mce.setRequest(trxnAdjustmentDetails);
		mce = processMessage(mce);
		exchange.getIn().setBody(mce);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	@Override
	public MCEMessage processMessage(MCEMessage mce) throws Exception {
		BackendResponse response = new BackendResponse();
		CMTransactionAdjustments trxnAdjustmentDetails = (CMTransactionAdjustments)mce.getRequest();
		
		log.info("Getting the adjustment parameters");
		Long srcPocketID = trxnAdjustmentDetails.getSourcePocketID();
		Long destPocketID = trxnAdjustmentDetails.getDestPocketID();
		BigDecimal amount = trxnAdjustmentDetails.getAmount();
		Long refId = trxnAdjustmentDetails.getSctlId();
		Integer type = 1;
		if(trxnAdjustmentDetails.getAdjustmentType() != null){
			type = trxnAdjustmentDetails.getAdjustmentType();
		}
		
		ChannelCodeDAO ccdao = DAOFactory.getInstance().getChannelCodeDao();
		ChannelCode cc = ccdao.getByChannelCode(channel);


		if(srcPocketID==null || destPocketID==null || amount==null || refId==null)
		{
			String error = "Manual Adjustments: Invalid arguments";
			log.error(error);
			response.setDescription(error);
			mce.setRequest(response);
			mce.setResponse(response);
			return mce;
		}

		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDAO.getById(refId);
		if(sctl==null)
		{
			String error = "Cannot find Transaction with ref id:"+refId;
			log.error(error);
			response.setDescription(error);
			mce.setRequest(response);
			mce.setResponse(response);			
			return mce;
		}
		
		if(CmFinoFIX.SCTLStatus_Pending.equals(sctl.getStatus()))
		{
			String error = "Cannot find Transaction with ref id:"+refId;
			log.error(error);
			response.setDescription(error);
			mce.setRequest(response);
			mce.setResponse(response);				
			return mce;
		}
		TxnAdjustmentService ledgerService = new TxnAdjustmentService();


		if(type==2 || type==3)
		{
			log.info("Processing type: "+type+" for refid:"+refId);
			List<Ledger> ledgers = ledgerService.getStaleLedgerEntries(sctl);
			//Correct the pocket balance and ledgers
			boolean isSuspense = ledgerService.isSuspensePocket(srcPocketID);
			if(isSuspense)
			{
				//Update pocket balance only for suspense pockets
				if(ledgers.isEmpty())
				{
					String error = "Cannot update pocket balance as there is no stale ledger found:"+refId;
					log.error(error);
					response.setDescription(error);
					mce.setRequest(response);
					mce.setResponse(response);						
					return mce;
				}

				Ledger staleLedger = null;
				for(Ledger ledger: ledgers)
				{
					if(ledger.getDestpocketid().equals(srcPocketID))
					{
						staleLedger = ledger;
						break;
					}
				}

				if(staleLedger!=null)
				{
					log.info("StaleLedger found is Src: "+staleLedger.getSourcepocketid()+" Dest:"+staleLedger.getDestpocketid()+
							" Amount:"+staleLedger.getAmount());
					boolean isUpdateSuccess = updateLedgerOpeningBalance(staleLedger, srcPocketID);
					if(isUpdateSuccess)
					{
						try
						{
							updatePocketBalance(srcPocketID, amount);

							LedgerDAO ledgerDAO = DAOFactory.getInstance().getLedgerDAO();
							ledgerDAO.save(staleLedger);

							log.info("Completed pocket balance update of pocket:"+srcPocketID+" refId:"+refId);
						}
						catch (HibernateException hibernateExp)
						{
							String error = "Pocket Update failed for pocket ID: "+srcPocketID +" for refId:"+refId;
							log.error(error, hibernateExp);
							response.setDescription(error);
							mce.setRequest(response);
							mce.setResponse(response);								
							return mce;
						}
						catch (Exception exp)
						{
							String error = "Pocket Update failed for pocket ID: "+srcPocketID +" for refId:"+refId;
							log.error(error, exp);
							response.setDescription(error);
							mce.setRequest(response);
							mce.setResponse(response);
							
							return mce;
						}
						boolean success = updateLedgers(staleLedger, srcPocketID, amount);
						if(!success)
						{
							String error = "Manual Adjustments: Update of Ledgers failed for refId:"+refId;
							log.error(error);
							response.setDescription(error);
							mce.setRequest(response);
							mce.setResponse(response);								
							return mce;
						}
					}
					else
					{
						String error = "Manual Adjustments: Update of Ledger Balance failed for refID:"+refId;
						log.error(error);
						response.setDescription(error);
						mce.setRequest(response);
						mce.setResponse(response);							
						return mce;
					}
				}
				else
				{
					String error = "Manual Adjustments: Stale Ledger is null for refId:"+refId;
					log.error(error);
					response.setDescription(error);
					mce.setRequest(response);
					mce.setResponse(response);							
					return mce;
				}

			}
			else
			{
				String error = "Manual Adjustments: Cannot apply type 2 for non suspense source pockets: "+refId;
				log.error(error);
				response.setDescription(error);
				mce.setRequest(response);
				mce.setResponse(response);					
				return mce;
			}
		}
		else if (type==11 || type==12)
		{
			List<Ledger> ledgers = ledgerService.getAllLedgerEntries(sctl);
			//Update pocket balance
			if(ledgers.isEmpty())
			{
				String error = "Cannot update pocket balance as there is no stale ledger found:"+refId;
				log.error(error);
				response.setDescription(error);
				mce.setRequest(response);
				mce.setResponse(response);					
				return mce;
			}

			Ledger staleLedger = null;
			for(Ledger ledger: ledgers)
			{
				if(ledger.getDestpocketid().equals(destPocketID) && ledger.getSourcepocketid().equals(srcPocketID))
				{
					staleLedger = ledger;
					break;
				}
			}
			if(staleLedger==null)
			{
				String error = "Cannot update pocket balance as there is no stale ledger found:"+refId;
				log.error(error);
				response.setDescription(error);
				mce.setRequest(response);
				mce.setResponse(response);					
				return mce;
			}
			Long pocketID = srcPocketID;
			try
			{
				
				if(type==11)
				{
					updatePocketBalance(pocketID, amount);
					BigDecimal updatedBalance = new BigDecimal(staleLedger.getSourcepocketbalance()).add(amount);
					staleLedger.setSourcepocketbalance(String.valueOf(updatedBalance));
				}
				else if(type==12)
				{
					pocketID = destPocketID;
					updatePocketBalance(destPocketID, amount);
					/*BigDecimal updatedBalance = staleLedger.getDestPocketBalance().add(amount);
					staleLedger.setDestPocketBalance(updatedBalance);*/
				}
				LedgerDAO ledgerDAO = DAOFactory.getInstance().getLedgerDAO();
				ledgerDAO.save(staleLedger);

			}
			catch (HibernateException hibernateExp)
			{
				String error = "Pocket Update failed for pocket ID: "+srcPocketID;
				log.error(error, hibernateExp);
				response.setDescription(error);
				mce.setRequest(response);
				mce.setResponse(response);
				
				return mce;
			}
			catch (Exception exp)
			{
				String error = "Pocket Update failed for pocket ID: "+srcPocketID;
				log.error(error, exp);
				response.setDescription(error);
				mce.setRequest(response);
				mce.setResponse(response);					
				return mce;
			}
			
			boolean success = updateLedgers(staleLedger, pocketID, amount);
			if(!success)
			{
				String error = "Manual Adjustments: Update of Ledgers failed";
				log.error(error);
				response.setDescription(error);
				mce.setRequest(response);
				mce.setResponse(response);					
				return mce;
			}
		}
		if(type==3 || type==11 || type==12)
		{
			String error = "Manual Adjustments: Money movement not allowed for type: "+type;
			log.warn(error);
			response.setDescription(error);
			mce.setRequest(response);
			mce.setResponse(response);				
			return mce;
		}
		
		CommodityTransfer ct=null;
		if(sctl.getCommoditytransferid()!=null){
			ct = DAOFactory.getInstance().getCommodityTransferDAO().getById(sctl.getCommoditytransferid().longValue());
		}
		
		if(ct!=null){
			BigDecimal txnAmount = ct.getAmount().add(ct.getCharges());
			if(amount.compareTo(txnAmount)> 0)
			{
				String error = "Amount cannot be greater than Transction amount:"+txnAmount+" for refid:"+refId;
				log.error(error);
				response.setDescription(error);
				mce.setRequest(response);
				mce.setResponse(response);		
				return mce;
			}
		}
		else{
			String error = "ct record not found for refid:"+refId;
			log.error(error);
			response.setDescription(error);
			mce.setRequest(response);
			mce.setResponse(response);		
			return mce;
		}
		CMBankAccountToBankAccount msg = new CMBankAccountToBankAccount();
		msg.m_pHeader.setSendingTime(DateTimeUtil.getLocalTime());
		msg.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
		msg.setSourceApplication((int)cc.getChannelsourceapplication());
		msg.setChannelCode(cc.getChannelcode());
		msg.setIsSystemIntiatedTransaction(true);
		msg.setPin("1234");
		msg.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		msg.setAmount(amount);
		msg.setSourceMessage(ServiceAndTransactionConstants.TRANSACTION_ADJUSTMENTS);
		msg.setSourcePocketID(srcPocketID);
		msg.setDestPocketID(destPocketID);

		//ServiceChargeTransactionLog sctl = createSctl(srcPocketID, destPocketID, cc, amount);

		TransactionLog tlog = saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccount, msg.DumpFields());
		msg.setTransactionID(tlog.getId().longValue());
		/*sctl.setTransactionID(tlog.getID());*/

		//Long sctlID = tcs.saveServiceTransactionLog(sctl);

		msg.setServiceChargeTransactionLogID(refId);
		String sourceMdn = DAOFactory.getInstance().getPocketDAO().getById(srcPocketID).getSubscriberMdn().getMdn();
		String destMdn = DAOFactory.getInstance().getPocketDAO().getById(destPocketID).getSubscriberMdn().getMdn();
		msg.setDestMDN(destMdn);
		msg.setSourceMDN(sourceMdn);

		log.info("constructed the request -->{}", msg.DumpFields());

		mce.setRequest(msg);
		return mce;

	}

	private void updatePocketBalance(Long srcPocketID, BigDecimal amount) {
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		Pocket srcPocket = pocketDAO.getById(srcPocketID);

		BigDecimal currBalance = new BigDecimal(srcPocket.getCurrentbalance());
		BigDecimal updatedBalance = currBalance.add(amount);

		srcPocket.setCurrentbalance(updatedBalance.toPlainString());
		pocketDAO.save(srcPocket);
	}
	
	private TransactionLog saveTransactionsLog(Integer messageCode, String data) {
		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionLog transactionsLog = new TransactionLog();
		transactionsLog.setMessagecode(messageCode);
		transactionsLog.setMessagedata(data);
		MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
		MfinoServiceProvider msp = mspDao.getById(1);
		transactionsLog.setMfinoServiceProvider(msp);
		transactionsLog.setTransactiontime(new Timestamp(new Date()));
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;
	}


	private boolean updateLedgers(Ledger staleLedger, Long pocketId, BigDecimal amount)
	{
		LedgerQuery query = new LedgerQuery();
		query.setCreateTimeGE(staleLedger.getCreatetime());
		query.setSourceDestnPocketID(pocketId);
		query.setSortString(CmFinoFIX.CRLedger.FieldName_RecordID+QueryConstants.COLUMN_ORDER_DELIMITER+QueryConstants.ASC_STRING);
		
		LedgerDAO ledgerDAO = DAOFactory.getInstance().getLedgerDAO(); 
		List<Ledger> pocketLedgers = ledgerDAO.get(query);

		for(Ledger ledger: pocketLedgers)
		{
			if((ledger.getId().compareTo(staleLedger.getId())==0) ||
					ledger.getCreatetime().after(staleLedger.getLastupdatetime()))
			{
				continue;
			}
			if(ledger.getSourcepocketid().equals(pocketId))
			{
				ledger.setSourcepocketbalance(String.valueOf(new BigDecimal(ledger.getSourcepocketbalance()).add(amount)));
			}
			else if (ledger.getDestpocketid().equals(pocketId))
			{
				ledger.setDestpocketbalance(String.valueOf(new BigDecimal(ledger.getDestpocketbalance()).add(amount)));
			}
		}
		try
		{
			ledgerDAO.save(pocketLedgers);
		}
		catch(HibernateException exp)
		{
			log.error("Ledger update failed", exp);
			return false;
		}
		catch(Exception exp)
		{
			log.error("Ledger update failed", exp);
			return false;
		}
		return true;
	}

	private boolean updateLedgerOpeningBalance(Ledger staleLedger, Long pocketId)
	{
		LedgerQuery query = new LedgerQuery();
		query.setCreateTimeGE(staleLedger.getCreatetime());
		query.setLimit(5);
		query.setStart(1);
		query.setSortString(CmFinoFIX.CRLedger.FieldName_RecordID+QueryConstants.COLUMN_ORDER_DELIMITER+QueryConstants.ASC_STRING);

		LedgerDAO ledgerDAO = DAOFactory.getInstance().getLedgerDAO(); 
		List<Ledger> afterLedgers = ledgerDAO.get(query);

		LedgerQuery beforeQuery = new LedgerQuery();
		beforeQuery.setCreateTimeLT(staleLedger.getCreatetime());
		beforeQuery.setLimit(5);
		beforeQuery.setStart(0);
		beforeQuery.setSortString(CmFinoFIX.CRLedger.FieldName_RecordID+QueryConstants.COLUMN_ORDER_DELIMITER+QueryConstants.DESC_STRING);
		List<Ledger> beforeLedgers = ledgerDAO.get(beforeQuery);

		Ledger ascending = getProperLedger(afterLedgers, true, pocketId);
		Ledger descending = getProperLedger(beforeLedgers, false, pocketId);

		if(ascending==null || descending==null)
		{
			log.error("Cannot process updation of ledger balance as unable to find sequential ledgers");
			return false;
		}

		log.info("Ascending Ledger: "+ascending.getSourcepocketid()+" "+ascending.getSourcepocketbalance()+
				" "+ascending.getDestpocketid()+ " "+ascending.getDestpocketbalance()+" "+ascending.getAmount());
		log.info("Descending Ledger: "+descending.getSourcepocketid()+" "+descending.getSourcepocketbalance()+
				" "+descending.getDestpocketid()+ " "+descending.getDestpocketbalance()+" "+descending.getAmount());
		
		BigDecimal ascendingBalance = BigDecimal.ZERO;
		BigDecimal descendingBalance = BigDecimal.ZERO;

		if(ascending.getSourcepocketid().equals(pocketId))
		{
			ascendingBalance = new BigDecimal(ascending.getSourcepocketbalance());
		}
		else
		{
			ascendingBalance = new BigDecimal(ascending.getDestpocketbalance());
		}

		if(descending.getSourcepocketid().equals(pocketId))
		{
			descendingBalance =new BigDecimal( descending.getSourcepocketbalance());
			descendingBalance = descendingBalance.subtract(descending.getAmount());
		}
		else
		{
			descendingBalance = new BigDecimal(descending.getDestpocketbalance());
			descendingBalance = descendingBalance.add(descending.getAmount());
		}

		if(ascendingBalance.compareTo(descendingBalance)==0)
		{
			staleLedger.setDestpocketbalance(String.valueOf(ascendingBalance));
			return true;
		}
		return false;
	}

	/*
	 * Picks ledger whose closing balance matches with the opening balance of next ledger in ascending order.
	 * 
	 * @param ledgers
	 * @param isAscending
	 * @return
	 */
	private Ledger getProperLedger(List<Ledger> ledgers, boolean isAscending, Long pocketID)
	{
		for(int index=0; index<ledgers.size()-1; index++)
		{
			Ledger ledger = ledgers.get(index);

			Ledger ledgerToCompare = ledgers.get(index+1);

			BigDecimal ledgerBalance = BigDecimal.ZERO;
			BigDecimal compareBalance = BigDecimal.ZERO;

			if(isAscending)
			{
				//closing balance of ledger should be compared to opening balance of ledgerToCompare
				if(ledger.getSourcepocketid().equals(pocketID))
				{
					ledgerBalance = new BigDecimal(ledger.getSourcepocketbalance());
					ledgerBalance = ledgerBalance.subtract(ledger.getAmount());
				}
				else
				{
					ledgerBalance =new BigDecimal( ledger.getDestpocketbalance());
					ledgerBalance = ledgerBalance.add(ledger.getAmount());
				}

				if(ledgerToCompare.getSourcepocketid().equals(pocketID))
				{
					compareBalance =new BigDecimal( ledgerToCompare.getSourcepocketbalance());
				}
				else
				{
					compareBalance = new BigDecimal(ledgerToCompare.getDestpocketbalance());
				}
			}
			else
			{
				//opening balance of ledger should be compared to closing balance of ledgerToCompare
				if(ledger.getSourcepocketid().equals(pocketID))
				{
					ledgerBalance = new BigDecimal(ledger.getSourcepocketbalance());
				}
				else
				{
					ledgerBalance = new BigDecimal(ledger.getDestpocketbalance());
				}

				if(ledgerToCompare.getSourcepocketid().equals(pocketID))
				{
					compareBalance = new BigDecimal(ledgerToCompare.getSourcepocketbalance());
					compareBalance  = compareBalance.subtract(ledgerToCompare.getAmount());
				}
				else
				{
					compareBalance = new BigDecimal(ledgerToCompare.getDestpocketbalance());
					compareBalance = compareBalance.add(ledgerToCompare.getAmount());
				}
			}
			if(ledgerBalance.compareTo(compareBalance)==0)
			{
				return ledger;
			}
		}
		return null;
	}
	
	
	

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
}
