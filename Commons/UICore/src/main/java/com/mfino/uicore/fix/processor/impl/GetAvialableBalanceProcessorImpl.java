package com.mfino.uicore.fix.processor.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountBalanceInquiry;
import com.mfino.fix.CmFinoFIX.CMJSGetAvailableBalance;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.service.SubscriberService;
import com.mfino.uicore.fix.processor.GetAvialableBalanceProcessor;

/**
 * 
 * @author Bala Sunku
 */
@Service("GetAvialableBalanceProcessorImpl")
public class GetAvialableBalanceProcessorImpl extends FIXMessageHandler implements GetAvialableBalanceProcessor{
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {
	
		CMJSGetAvailableBalance realMsg = (CMJSGetAvailableBalance) msg;
	    Logger log = LoggerFactory.getLogger(this.getClass());
	    ChannelCodeDAO channelCodeDAO = DAOFactory.getInstance().getChannelCodeDao();  
	    String balance = "Not Avialable";
	    
	    if (StringUtils.isNotBlank(realMsg.getSourceMDN()) && StringUtils.isNotBlank(realMsg.getPin()) && realMsg.getSourcePocketID() != null) {
	    	log.info("Getting the pocket balance for the pocket id --> " + realMsg.getSourcePocketID());
	    	CMBankAccountBalanceInquiry	balanceInquiry = new CMBankAccountBalanceInquiry();
	    	ChannelCode cc = channelCodeDAO.getByChannelSourceApplication(CmFinoFIX.SourceApplication_Web);
	    	
	    	balanceInquiry.setSourceMDN(subscriberService.normalizeMDN(realMsg.getSourceMDN()));
	    	balanceInquiry.setPin(realMsg.getPin());
	    	balanceInquiry.setServletPath(CmFinoFIX.ServletPath_BankAccount);
	    	balanceInquiry.setSourceApplication((cc.getChannelsourceapplication()).intValue());
	    	balanceInquiry.setChannelCode(cc.getChannelcode());
	    	balanceInquiry.setPocketID(realMsg.getSourcePocketID());
	    	
	    	CFIXMsg response = super.process(balanceInquiry);
	    	TransactionResponse transactionResponse = checkBackEndResponse(response);
			if (transactionResponse.isResult()) {
				realMsg.setsuccess(CmFinoFIX.Boolean_True);
				String message = transactionResponse.getMessage();
				balance = message.substring(message.lastIndexOf(")")+1);
			}
			
	    	realMsg.setAvialableBalance(balance);
	    }
	    log.info("Avialable balance for the pocket id :  " + realMsg.getSourcePocketID() + " is --> " + realMsg.getAvialableBalance());
	    return realMsg;
	  }
}
