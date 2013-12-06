package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.BookingDatedBalance;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.BookingDateBalanceService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ShowBalanceDetailsProcessor;

/**
 *
 * @author Srikanth
 */
@Service("ShowBalanceDetailsProcessorImpl")
public class ShowBalanceDetailsProcessorImpl extends BaseFixProcessor implements ShowBalanceDetailsProcessor{	
	
    public CFIXMsg process(CFIXMsg msg) throws Exception {

        CmFinoFIX.CMJSShowBalanceDetails realMsg = (CmFinoFIX.CMJSShowBalanceDetails) msg;        
        Long pocketID = realMsg.getPocketID();
        PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
        Pocket pocket = pocketDao.getById(pocketID);
    	if(pocket != null) {
    		Subscriber subscriber = pocket.getSubscriberMDNByMDNID().getSubscriber();
    		realMsg.setCurrency(subscriber.getCurrency());
    		BookingDateBalanceService balanceService = new BookingDateBalanceService();
    		BigDecimal actualCurrentBalance = pocketDao.getActualCurrentBalanceForPocket(pocket);
    		realMsg.setCurrentBalance(actualCurrentBalance); 
    		BookingDatedBalance bookingDatedBalance = null;
    		if(DateUtils.isSameDay(new Timestamp(), realMsg.getEndTime())){
    			realMsg.setClosingBalance(actualCurrentBalance);
    		}
    		else{
	    		bookingDatedBalance = balanceService.getBookingDatedBalances(pocket, realMsg.getEndTime());
	    		realMsg.setClosingBalance(bookingDatedBalance.getClosingBalance());
    		}
    		bookingDatedBalance = balanceService.getBookingDatedBalances(pocket, realMsg.getStartTime());
    		if(realMsg.getStartTime().equals(bookingDatedBalance.getBookingDate())) {
    			//In this case we ll get exact date entry, hence opening balance of the retrieved entry is the resultant opening balance
    			realMsg.setOpeningBalance(bookingDatedBalance.getOpeningBalance());
    		} else {
    			//In this case we ll get pre date entry, hence closing balance of the retrieved entry is the resultant opening balance
    			realMsg.setOpeningBalance(bookingDatedBalance.getClosingBalance());
    		}
    	}    	        
        realMsg.setsuccess(true);
        return realMsg;
    }
}
