/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MerchantDAO;
import com.mfino.domain.Merchant;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCheckBalance;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.service.SubscriberService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.CheckBalanceProcessor;

/**
 *
 * @author sunil
 */
@Service("CheckBalanceProcessorImpl")
public class CheckBalanceProcessorImpl extends BaseFixProcessor implements CheckBalanceProcessor{

	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
   
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {

       CMJSCheckBalance realMsg = (CMJSCheckBalance) msg; 
                 
       MerchantDAO merDao = DAOFactory.getInstance().getMerchantDAO();
       Merchant mer = merDao.getById(realMsg.getMerchantID());    
       Set<SubscriberMdn> mdnSet = mer.getSubscriber().getSubscriberMdns();
       SubscriberMdn subsMDN = (SubscriberMdn) mdnSet.toArray()[0];
       
        
        Pocket p = subscriberService.getDefaultPocket(subsMDN.getId().longValue(), CmFinoFIX.PocketType_SVA, 
        		CmFinoFIX.Commodity_Airtime);
  	    //Before Correcting errors reported by Findbugs:
        /*if(p == null){
            updateToDefault(realMsg);
        } else {
          updateMessage(p, realMsg);
        }
        
        log.info(getLoggedUserName() + " has successfully checked balance for Merchant " + subsMDN.getID() + " for pocket " + p.getID());
        */       
  	
  	    //After Correcting the errors reported by Findbugs  :     
        if(p == null){
            log.error("default pocket for " + subsMDN.getId()+ "for commodity type "+ CmFinoFIX.PocketType_SVA +" is null");
               updateToDefault(realMsg);
           } else {
             updateMessage(p, realMsg);
             log.info(getLoggedUserNameWithIP() + " has successfully checked balance for Merchant " + subsMDN.getId() + " for pocket " + p.getId());
           }    
        
        realMsg.setsuccess(CmFinoFIX.Boolean_True);
        
        realMsg.settotal(1);
        return realMsg;
    }

    private void updateMessage(Pocket pocket, CMJSCheckBalance entry) {
        if (pocket.getCurrentbalance() != null) {
            entry.setBalance(new BigDecimal(pocket.getCurrentbalance()));
        } else {
        	entry.setBalance(ZERO);
        }
        if (null != pocket.getPocketTemplate()) {
            entry.setCommodity(((Long)pocket.getPocketTemplate().getCommodity()).intValue());
            String PocketTypeText = enumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketType, null, pocket.getPocketTemplate().getType());
            Integer commodityType = ((Long)pocket.getPocketTemplate().getCommodity()).intValue();
            String commodityText = enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, commodityType);
            entry.setPocketTypeText(String.format("%s  %s", commodityText,PocketTypeText));
        }

        if (null != pocket.getLastupdatetime()) {
            entry.setLastUpdateTime(pocket.getLastupdatetime());
        }
    }

    private void updateToDefault(CMJSCheckBalance entry) {
        entry.setBalance(ZERO);
        entry.setCommodity(CmFinoFIX.Commodity_Airtime);
        entry.setPocketTypeText(MessageText._("You don't have a default SVA Airtime Pocket"));
    }
}

