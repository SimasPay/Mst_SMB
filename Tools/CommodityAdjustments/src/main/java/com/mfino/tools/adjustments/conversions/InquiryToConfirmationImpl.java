package com.mfino.tools.adjustments.conversions;

import java.util.Date;

import org.apache.camel.Handler;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class InquiryToConfirmationImpl implements InquiryToConfirmation {

	private String	               channel;

	@Handler
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage convert(MCEMessage msg) {

		ChannelCodeDAO ccdao = DAOFactory.getInstance().getChannelCodeDao();
		ChannelCode cc = ccdao.getByChannelCode(channel);

		BackendResponse response = (BackendResponse) msg.getResponse();

		CMBankAccountToBankAccountConfirmation confirm = new CMBankAccountToBankAccountConfirmation();
		CMBase baseMsg = (CMBase)msg.getRequest();
		
		confirm.m_pHeader.setSendingTime(DateTimeUtil.getLocalTime());
		confirm.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());

		confirm.setSourceMDN(baseMsg.getSourceMDN());
		confirm.setDestMDN(response.getReceiverMDN());
		confirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		confirm.setTransferID(response.getTransferID());
		confirm.setConfirmed(true);
		confirm.setSourceApplication((int)cc.getChannelsourceapplication());
		confirm.setChannelCode(cc.getChannelcode());
		confirm.setParentTransactionID(response.getParentTransactionID());
		confirm.setIsSystemIntiatedTransaction(true);
		confirm.setServiceChargeTransactionLogID(response.getServiceChargeTransactionLogID());
		confirm.setSourcePocketID(response.getSourcePocketId());
		confirm.setDestPocketID(response.getDestPocketId());
		confirm.setServiceChargeTransactionLogID(response.getServiceChargeTransactionLogID());
		
		TransactionLog tlog = saveTransactionsLog(CmFinoFIX.MessageType_BankAccountToBankAccountConfirmation, confirm.DumpFields(),
		        confirm.getParentTransactionID());
		confirm.setTransactionID(tlog.getId().longValue());
		confirm.setMSPID(tlog.getMfinoServiceProvider().getId().longValue());

		MCEMessage mce = new MCEMessage();
		mce.setRequest(confirm);
		
		return mce;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	protected TransactionLog saveTransactionsLog(Integer messageCode, String data, Long parentTxnID) {
		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionLog transactionsLog = new TransactionLog();
		transactionsLog.setMessagecode(messageCode);
		transactionsLog.setMessagedata(data);
		MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
		MfinoServiceProvider msp = mspDao.getById(1);
		transactionsLog.setMfinoServiceProvider(msp);
		transactionsLog.setTransactiontime(new Timestamp(new Date()));
		transactionsLog.setParenttransactionid(parentTxnID);
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;
	}

}
