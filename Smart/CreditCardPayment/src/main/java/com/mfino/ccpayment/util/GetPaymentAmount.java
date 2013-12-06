package com.mfino.ccpayment.util;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankChannelQueryRequest;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.fix.processor.BankChannelQueryProcessor;
import com.mfino.util.ConfigurationUtil;

public class GetPaymentAmount {
	private static Logger log = LoggerFactory.getLogger(GetPaymentAmount.class);

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public CMJSError processAmount(String mdn) {
		CMBankChannelQueryRequest request = new CMBankChannelQueryRequest();
		CMJSError returnMsg = null;
		request.setSourceMDN(ConfigurationUtil.getPostpaidSourceMDN());
		request.setDestMDN(mdn);
		log.info("Source MDN = " + ConfigurationUtil.getPostpaidSourceMDN() + ",Destination MDN = " + mdn);
		request.setSourceApplication(CmFinoFIX.SourceApplication_Web);
		request.setISO8583_PrimaryAccountNumber("xxxxxx");
		request.setISO8583_ProcessingCode("380000");
		Long transID = 0l;

		TransactionsLog transLog = new TransactionsLog();
		TransactionsLogDAO tDao = DAOFactory.getInstance().getTransactionsLogDAO();
		transLog.setTransactionTime(new Timestamp());
		transLog.setMessageCode(request.header().getMsgType());
		transLog.setMessageData("dummy");
		MfinoServiceProviderDAO mDao = new MfinoServiceProviderDAO();
		transLog.setmFinoServiceProviderByMSPID(mDao.getById(1l));

		tDao.save(transLog);
		transID = transLog.getID();
		log.info("Transaction Log Id = " + transID);
		transID = transID % 1000000;
		String stan = String.format("%06d", transID);

		request.setISO8583_RetrievalReferenceNum(String.format("%012d", transID));
		Calendar dt = Calendar.getInstance();
		String Hhmmss = String.format("%02d%02d%02d", dt.get(Calendar.HOUR_OF_DAY),
				dt.get(Calendar.MINUTE),
				dt.get(Calendar.SECOND));
		request.setISO8583_LocalTxnTimeHhmmss(Hhmmss);
		request.setISO8583_MerchantType("1002");
		request.setISO8583_SystemTraceAuditNumber(stan);
		request.setISO8583_AcquiringInstIdCode(881);
		request.setISO8583_Variant("artajasa");
		request.setISO8583_CardAcceptorIdCode("123456789012345");
		request.setISO8583_MessageData("xxxxxx");
		BankChannelQueryProcessor amountProcessor = new BankChannelQueryProcessor();
		CFIXMsg msg = amountProcessor.process(request);
		returnMsg = (CMJSError) msg;
		return returnMsg;
	}
}
