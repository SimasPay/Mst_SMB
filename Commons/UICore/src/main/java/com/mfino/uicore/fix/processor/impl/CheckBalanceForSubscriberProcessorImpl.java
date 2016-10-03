package com.mfino.uicore.fix.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.i18n.MessageText;
import com.mfino.service.AuthorizationService;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.CheckBalanceForSubscriberProcessor;

/**
 *
 * @author ADMIN
 */
@Service("CheckBalanceForSubscriberProcessorImpl")
public class CheckBalanceForSubscriberProcessorImpl extends BaseFixProcessor implements CheckBalanceForSubscriberProcessor{
	PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();

	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {

        CmFinoFIX.CMJSCheckBalanceForSubscriber realMsg = (CmFinoFIX.CMJSCheckBalanceForSubscriber) msg;

        boolean isAuth = authorizationService.isAuthorized(CmFinoFIX.Permission_Subscriber_Balance_View);
        if (!isAuth) {
        	log.warn("User: " + getLoggedUserNameWithIP() + " is not authorized to view the subscriber balance for pocket:" + realMsg.getPocketID() );
            return getErrorMessage(MessageText._("Not authorized to view subscriber balance"),
                    CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSCheckBalanceForSubscriber.FieldName_Balance,
                    MessageText._("Not allowed"));
        }

        log.info("User: " + getLoggedUserNameWithIP() + " attempted to check balance");
        Pocket p = pocketDao.getById(realMsg.getPocketID());//SubscriberService.getDefaultPocket(realMsg.getSubscriberMDNID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
        if (p == null) {
            updateToDefault(realMsg);           
        }else if(CmFinoFIX.PocketType_BankAccount.equals(p.getPocketTemplate().getType())){
        	return getErrorMessage(MessageText._("Balance not Available"),
                    CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSCheckBalanceForSubscriber.FieldName_Balance,
                    MessageText._("Balance not Available"));
        }else {
            updateMessage(p, realMsg);
            log.info("User: " + getLoggedUserNameWithIP() + " successfully checked balance for pocket ID: " + p.getId());
        }
        
        realMsg.setsuccess(true);
        return realMsg;
    }

    private void updateMessage(Pocket pocket, CmFinoFIX.CMJSCheckBalanceForSubscriber entry) {
    	entry.setBalance(pocketDao.getActualCurrentBalanceForPocket(pocket));
    	entry.setCurrency(pocket.getSubscriberMdn().getSubscriber().getCurrency());
        if (null != pocket.getPocketTemplate()) {
            entry.setCommodity(((Long)pocket.getPocketTemplate().getCommodity()).intValue());
            String pocketTypeText = enumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketType, null,
                    pocket.getPocketTemplate().getType());
            Integer commodityType = ((Long)pocket.getPocketTemplate().getCommodity()).intValue();
            String commodityText = enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, commodityType);
            entry.setPocketTypeText(String.format("%s  %s", commodityText, pocketTypeText));
        }

        if (null != pocket.getLastupdatetime()) {
            entry.setLastUpdateTime(pocket.getLastupdatetime());
        }
    }

    private void updateToDefault(CmFinoFIX.CMJSCheckBalanceForSubscriber entry) {
        entry.setBalance(ZERO);
        entry.setCommodity(CmFinoFIX.Commodity_Money);
        entry.setLastUpdateTime(null);
        entry.setPocketTypeText(MessageText._("You don't have a default SVA Money Pocket"));
    }
}
