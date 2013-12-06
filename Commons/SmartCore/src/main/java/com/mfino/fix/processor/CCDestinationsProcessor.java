/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.CreditCardDestinationDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.CreditCardDestinationQuery;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.CreditCardDestinations;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCreditCardDestination;
import com.mfino.fix.CmFinoFIX.CMJSCreditCardDestination.CGEntries;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

/**
 * 
 * @author ADMIN
 */
public class CCDestinationsProcessor extends BaseFixProcessor {

	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	public CFIXMsg process(CFIXMsg msg) throws Exception {

		CMJSCreditCardDestination realMsg = (CMJSCreditCardDestination) msg;

		CreditCardDestinationDAO ccdestDao = DAOFactory.getInstance().getCreditCardDestinationDAO();

		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			CMJSCreditCardDestination.CGEntries[] entries = realMsg.getEntries();
			CreditCardDestinations ccDestinations = null;
			for (CMJSCreditCardDestination.CGEntries entry : entries) {
				if(entry!=null && entry.getDestMDN() != null){
					ccDestinations= ccdestDao.getById(entry.getID());
				if (ccDestinations != null) {
					// no need of this check
					if (!entry.getRecordVersion().equals(ccDestinations.getVersion())) {
						handleStaleDataException();
					}
					UpdateEntity(ccDestinations, entry, true);
					ccdestDao.save(ccDestinations);
					updateMessage(ccDestinations, entry); // to show the udpated details to the user.
				} else {
					CMJSError err = new CMJSError();
					err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					err.setErrorDescription(MessageText._("Invalid details"));
					return err;
				}
			}
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			CreditCardDestinationQuery query = new CreditCardDestinationQuery();
			if (null != realMsg.getSubscriberUserIDSearch()) {
                UserDAO dao = DAOFactory.getInstance().getUserDAO();
				User user = dao.getById(realMsg.getSubscriberUserIDSearch()); 
				// SubscriberUserID is same as userid
                Set<Subscriber> sub = user.getSubscriberFromSubscriberUserID();
                if (!sub.isEmpty()) {
                    for (Subscriber record : sub) {
                        query.setSubscriber(record);
                    }
                }else{
                	SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
                	SubscriberMdnQuery subquery = new SubscriberMdnQuery();
                	int index=-1;
                	if(user.getStatus().equals(CmFinoFIX.UserStatus_Expired)){
                		index=user.getUsername().indexOf(UserDAO.EXPIRY_TAG);
                	}           		
            		if(index!=-1){    
            		String	usermdn=user.getUsername().substring(0,index);
            		subquery.setMdn(usermdn);
            		subquery.setCreateTimeLT(realMsg.getCreationDateStartTime());
            		subquery.setLastUpdateTimeGE(realMsg.getCreationDateEndTime());
            		List<SubscriberMDN> subscriberMDNs = subscriberMDNDAO.get(subquery);
            		if (!subscriberMDNs.isEmpty()) {
                        for (SubscriberMDN record : subscriberMDNs) {
                        	if( record !=null && record.getSubscriber()!=null && isSameMDN(record.getMDN(),usermdn)){
                            query.setSubscriber(record.getSubscriber());
                            break;
                        	}     	
                        }
                      }else {
                  		return realMsg;
          			}
            		}else {
                		return realMsg;
        			}            		
                }
            }
			
			query.setCreateTimeGE(realMsg.getCreationDateStartTime());
			query.setCreateTimeLT(realMsg.getCreationDateEndTime());
			query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            
			List<CreditCardDestinations> results = ccdestDao.get(query);

			realMsg.allocateEntries(results.size());

			for (int i = 0; i < results.size(); i++) {
				CreditCardDestinations ccDestinations = results.get(i);
				CMJSCreditCardDestination.CGEntries entry = new CMJSCreditCardDestination.CGEntries();

				updateMessage(ccDestinations, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());

		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
			CMJSCreditCardDestination.CGEntries[] entries = realMsg.getEntries();
			CreditCardDestinations ccdest;
			for (CMJSCreditCardDestination.CGEntries e : entries) {				
				if(e!=null && e.getDestMDN() != null && e.getSubscriberID() != null)
				{
				ccdest = new CreditCardDestinations();
				UpdateEntity(ccdest, e, false);
				ccdestDao.save(ccdest);

				updateMessage(ccdest, e);
			}
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		}
		return realMsg;
	}

	private boolean isSameMDN(String mdn, String usermdn) {
		if(mdn.equals(usermdn)){
			return true;
		}else if(mdn.contains("R")&&usermdn.equals(mdn.substring(0, mdn.indexOf("R")))){
			return true;			
		}
		return false;
	}

	private void UpdateEntity(CreditCardDestinations ccdest, CGEntries e,boolean isUpdate) {
		SubscriberDAO subdao = DAOFactory.getInstance().getSubscriberDAO();
	    if(e.getSubscriberID()!=null){
	    	ccdest.setSubscriber(subdao.getById(e.getSubscriberID()));
	    }
	    if(e.getDestMDN()!=null){
	    	if(isUpdate){
	    		if(ccdest.getDestMDN()!=null){
	    			ccdest.setOldDestMDN(ccdest.getDestMDN());
	    		}
	    	}
	    	else{
	    		ccdest.setOldDestMDN(e.getDestMDN());
	    	}
	    ccdest.setDestMDN(e.getDestMDN());
	    }
	    if(e.getLastUpdateTime()!=null)
	    {
	    	ccdest.setLastUpdateTime(e.getLastUpdateTime());
	    }
	    if(e.getCreatedBy()!=null){
	    	ccdest.setCreatedBy(e.getCreatedBy());
	    }
	    if(e.getCreateTime()!=null){
	    	ccdest.setCreateTime(e.getCreateTime());
	    }
	    if(e.getUpdatedBy()!=null){
	    	ccdest.setUpdatedBy(e.getUpdatedBy());
	    }
	    if(e.getCCMDNStatus()!=null){
	    	ccdest.setCCMDNStatus(e.getCCMDNStatus());
	    }
		
		
	}

	public void updateMessage(CreditCardDestinations ccDestinations, CGEntries entry) {
		entry.setID(ccDestinations.getID());
		entry.setSubscriberID(ccDestinations.getSubscriber().getID());
		if (ccDestinations.getDestMDN() != null) {
			entry.setDestMDN(ccDestinations.getDestMDN());
		}
		
		entry.setRecordVersion(ccDestinations.getVersion());
		entry.setLastUpdateTime(ccDestinations.getLastUpdateTime());
		entry.setUpdatedBy(ccDestinations.getUpdatedBy());
		entry.setCreatedBy(ccDestinations.getCreatedBy());
		entry.setCreateTime(ccDestinations.getCreateTime());
		entry.setCCMDNStatus(ccDestinations.getCCMDNStatus());
		entry.setCCMDNStatusText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_CCMDNStatus, CmFinoFIX.Language_English, ccDestinations.getCCMDNStatus()));
		
	}
	
}
