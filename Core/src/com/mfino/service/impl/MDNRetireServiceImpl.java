/**
 * 
 */
package com.mfino.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.RetiredCardPANInfoDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.RetiredCardPANInfoQuery;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.Partner;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.RetiredCardPANInfo;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.MDNRetireService;
import com.mfino.service.SubscriberStatusEventService;


/**
 * @author Bala Sunku
 *
 */
@org.springframework.stereotype.Service("MDNRetireServiceImpl")
public class MDNRetireServiceImpl implements MDNRetireService{
	
	private static Logger log = LoggerFactory.getLogger(MDNRetireServiceImpl.class);
	
	/**
	 * 
	 * @param subscriberMDNId
	 * @return
	 */
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer retireMDN(Long subscriberMDNId) {
		Integer result = null;
		SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMdnQuery query = new SubscriberMdnQuery();
		query.setId(subscriberMDNId);
		List <SubscriberMdn> lst = subscriberMDNDAO.get(query);
		SubscriberMdn subscriberMDN = lst.get(0);
		boolean isPartner=false;
		Subscriber subscriber = null;
		
		if (subscriberMDN != null) {
			log.info("Retiring the MDN -->" + subscriberMDN.getMdn());
			//Check for the Pending transactions for the MDN
			subscriber = subscriberMDN.getSubscriber();
			if(CmFinoFIX.SubscriberType_Partner.equals(subscriber.getType())){
				isPartner = true;
			}
			CommodityTransferQuery ctQuery = new CommodityTransferQuery();
			ctQuery.setSourceDestnMDN(subscriberMDN.getMdn());
			
			PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
			try {
				List<PendingCommodityTransfer> lstPCT = pctDAO.get(ctQuery);
				
				if (CollectionUtils.isEmpty(lstPCT)) {
					String markedMDN = getRXString(subscriberMDN);
					
					updateAllCommodityTransferRecords(subscriberMDN.getId().longValue(), subscriberMDN.getMdn(), markedMDN);
					
					retireAllCardPans(subscriberMDN.getId().longValue());
					
					if(isPartner == true){
						retirePartner(subscriberMDN.getId().longValue());
					}
				
					suffixMDNWithRX(subscriberMDN.getId().longValue(), markedMDN);
					result = CmFinoFIX.ResolveAs_success;
				} else {
					log.info("Number of Pending transactions to be resolved are --> " + lstPCT.size());
					result = CmFinoFIX.ResolveAs_failed;
				}
			} catch (Exception e) {
				log.error("Error while getting the pending transactions for MDN --> " + subscriberMDN.getMdn(), e);
				result = CmFinoFIX.ResolveAs_failed;
			}
		}
		log.info("Retiring Status is --> " + result);
		return result;
	}

	private void retirePartner(Long subscriberId) {
		// TODO Auto-generated method stub
		PartnerQuery partnerQuery = new PartnerQuery();
		partnerQuery.setSubscriberID(subscriberId);
		String tradeNameStringToReplace = null;
		String codeStringToReplace = null;
		String userNameStringToReplace = null;


		PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
		List<Partner> partnerLst = partnerDAO.get(partnerQuery);
		Partner partner = partnerLst.get(0);
		
		tradeNameStringToReplace = getPartnerFieldStringForThisPartnerField(partner.getTradename(),false);				
		if(tradeNameStringToReplace != null)
			partner.setTradename(tradeNameStringToReplace);

		codeStringToReplace = getPartnerFieldStringForThisPartnerField(partner.getPartnercode(),true);
		if(codeStringToReplace != null)
			partner.setPartnercode(codeStringToReplace);

		UserQuery userQuery = new UserQuery();
		userQuery.setUserName(partner.getMfinoUser().getUsername());
		UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
		List <User> userLst = userDAO.get(userQuery);

		User user = null;
		if(userLst != null)
			user = userLst.get(0);
		if(user != null){
			userNameStringToReplace = getUserNameStringForUserName(user.getUsername());
			if(userNameStringToReplace != null)
				user.setUsername(userNameStringToReplace);
			user.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
			userDAO.save(user);
		}

		if(userNameStringToReplace != null)
			partner.getMfinoUser().setUsername(userNameStringToReplace);
		partnerDAO.save(partner);
	}	
	

	private String getRXString(SubscriberMdn subscriberMDN) {
        SubscriberMdnQuery subscriberMdnQuery = new SubscriberMdnQuery();
        subscriberMdnQuery.setMdn(subscriberMDN.getMdn());

        SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
        List<SubscriberMdn> subscriberMDNLikeRecords = subscriberMDNDAO.get(subscriberMdnQuery);

        int noOfTimesRetired = 0;
        for (SubscriberMdn subscriberLikeMDN : subscriberMDNLikeRecords) {
            String mdn = subscriberLikeMDN.getMdn();
            if (StringUtils.isBlank(mdn)) {
                continue;
            }

            // here check if the MDN has R and Number after it.
            String[] splitComponents = mdn.split("R");
            if (splitComponents.length != 2) {
                continue;
            }

            String timesRetiredIndicator = splitComponents[1];
            int timesRetired = 0;
            try {
                timesRetired = Integer.parseInt(timesRetiredIndicator);
            } catch (NumberFormatException nfe) {
            	log.error("getRxString", nfe);
            }

            if (timesRetired >= noOfTimesRetired) {
                noOfTimesRetired = timesRetired + 1;
            }
        }
        if (subscriberMDN.getMdn().contains("R")) {
            // If we reach here then we already have R1.
            String mdn = subscriberMDN.getMdn();
            String mdnWithoutR = mdn.substring(0, mdn.indexOf("R"));
            return mdnWithoutR + "R" + noOfTimesRetired;
        } else {
            return subscriberMDN.getMdn() + "R" + noOfTimesRetired;
        }
    }
    
	
    private void updateAllCommodityTransferRecords(Long mdnID, String mdn, String mdnRX) {
		CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
    	ctDao.markSourceMDNRX(mdnID, mdnRX);
    	ctDao.markDestMDNRX(mdn, mdnRX);

    }
    
    private void retireAllCardPans(Long mdnId) {
        // Here we need to get the Records from pocket table for MdnId.        
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();			
        PocketQuery pocketQuery = new PocketQuery();
        pocketQuery.setMdnIDSearch(mdnId);
        
        List<Pocket> resultantPockets = pocketDAO.get(pocketQuery);

        for (Pocket eachPocket : resultantPockets) {
        	String cardPanStringToReplace = null;
            String cardPan = eachPocket.getCardpan();
            int timesRetired = 0;            
            if (StringUtils.isNotBlank(cardPan)) {
            	//cardPanStringToReplace = getCardPanRetiredStringForThisCardPan(cardPan);
            	timesRetired = getTimesRetiredForThisCardPan(cardPan);
            	cardPanStringToReplace = cardPan + "R" + timesRetired;
            }

            if (StringUtils.isBlank(cardPanStringToReplace)) {
            	cardPanStringToReplace = cardPan;
            }

            eachPocket.setCardpan(cardPanStringToReplace);
            eachPocket.setStatus(CmFinoFIX.PocketStatus_Retired);
            eachPocket.setIsdefault(false);
            // Now set back the Data into the table.
            try{
            	pocketDAO.save(eachPocket);
            }catch(ConstraintViolationException e){
            	//Handles already existing duplicate card pans insertion, Scheduler picks it in next cycle
            	log.info("Handling Constraint violation Exception Occured: " + e );
            	if (StringUtils.isNotBlank(cardPan)) {
            		timesRetired=timesRetired+1;
                	updateCardPANInfo(cardPan, timesRetired);
                	throw e;
                }            	
            }
            if (StringUtils.isNotBlank(cardPan)) {
            	updateCardPANInfo(cardPan, timesRetired+1);
            }
        }        
        }
    
    
    
    private String getPartnerFieldStringForThisPartnerField(String field, boolean isPartnerCode){
    	PartnerQuery partnerQuery = new PartnerQuery();
    	
    	if(isPartnerCode){
    		partnerQuery.setPartnerCode(field);
    	}
    	else {
    		partnerQuery.setTradeName(field);
    	}
    	
    	partnerQuery.setPartnerCodeLike(true);
    	
    	PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
    	List <Partner> results = partnerDAO.get(partnerQuery);
    	int noOfTimesRetired=0;
    	for( Partner partner : results ){
    		String partnerField = null;
    		if(isPartnerCode){
    			partnerField = partner.getPartnercode();
    		}
    		else {
    			partnerField = partner.getTradename();
    		}
    		
    		if(StringUtils.isBlank(partnerField)){
    			continue;
    		}
    		
    		String[] splitComponents = partnerField.split("R");
            if (splitComponents.length != 2) {
                continue;
            }
    		
            String timesRetiredIndicator = splitComponents[1];
            int timesRetired = 0;
            try {
                timesRetired = Integer.parseInt(timesRetiredIndicator);
            } catch (NumberFormatException nfe) {
            	log.error("Getting partner field count" + nfe.getMessage(), nfe);
            }

            if (timesRetired >= noOfTimesRetired) {
                noOfTimesRetired = timesRetired + 1;
            }            
    	}
    	
    	if (field.contains("R")) {
            // If we reach here then we already have R0.
            String str = field;
            String strWithoutR = str.substring(0, str.indexOf("R"));
            return strWithoutR + "R" + noOfTimesRetired;
        } else {
            return field + "R" + noOfTimesRetired;
        }    	
    }
    
    private String getUserNameStringForUserName(String userName) {
        UserQuery userQuery = new UserQuery();
        userQuery.setUserNameLike(userName);
        
        UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
        List<User> results = userDAO.get(userQuery);
        
        int noOfTimesRetired = 0;
        for (User user : results) {
            String name = user.getUsername();
            if (StringUtils.isBlank(name)) {
                continue;
            }

            // here check if the MDN has R and Number after it.
            String[] splitComponents = name.split("R");
            if (splitComponents.length != 2) {
                continue;
            }

            String timesRetiredIndicator = splitComponents[1];
            int timesRetired = 0;
            try {
                timesRetired = Integer.parseInt(timesRetiredIndicator);
            } catch (NumberFormatException nfe) {
            	log.error("Getting userName count" + nfe.getMessage(), nfe);
            }

            if (timesRetired >= noOfTimesRetired) {
                noOfTimesRetired = timesRetired + 1;
            }
        }
        if (userName.contains("R")) {
            // If we reach here then we already have R0.
            String field = userName;
            String mdnWithoutR = field.substring(0, field.indexOf("R"));
            return mdnWithoutR + "R" + noOfTimesRetired;
        } else {
            return userName + "R" + noOfTimesRetired;
        }
    }
    
/*    private String getCardPanRetiredStringForThisCardPan(String cardPan) {
        PocketQuery pocketQuery = new PocketQuery();
        pocketQuery.setCardPan(cardPan);
        pocketQuery.setPocketCardPaneLikeSearch(Boolean.TRUE);

        PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
        List<Pocket> results = pocketDAO.get(pocketQuery);
        int noOfTimesRetired = 0;
        for (Pocket pocket : results) {
            String pocketCardPan = pocket.getCardPAN();
            if (StringUtils.isBlank(pocketCardPan)) {
                continue;
            }

            // here check if the MDN has R and Number after it.
            String[] splitComponents = pocketCardPan.split("R");
            if (splitComponents.length != 2) {
                continue;
            }

            String timesRetiredIndicator = splitComponents[1];
            int timesRetired = 0;
            try {
                timesRetired = Integer.parseInt(timesRetiredIndicator);
            } catch (NumberFormatException nfe) {
            	log.error("Getting card pan count" + nfe.getMessage(), nfe);
            }

            if (timesRetired >= noOfTimesRetired) {
                noOfTimesRetired = timesRetired + 1;
            }
        }
        if (cardPan.contains("R")) {
            // If we reach here then we already have R0.
            String mdn = cardPan;
            String mdnWithoutR = mdn.substring(0, mdn.indexOf("R"));
            return mdnWithoutR + "R" + noOfTimesRetired;
        } else {
            return cardPan + "R" + noOfTimesRetired;
        }
    }
*/
    /**
	 * Gets the number of times a cardpan is retired.
	 * @param cardPan
	 * @return
	 */
    private int getTimesRetiredForThisCardPan(String cardPan) {
        RetiredCardPANInfoQuery query = new RetiredCardPANInfoQuery();
    	query.setCardPan(cardPan);
    	
    	RetiredCardPANInfoDAO dao = DAOFactory.getInstance().getRetiredCardPANInfoDAO();
    	List<RetiredCardPANInfo> results = dao.get(query);
    	int timesRetired = 0;
    	if(results.size() > 0){
	    	RetiredCardPANInfo retiredCardPANInfo = results.get(0);
	    	if(retiredCardPANInfo != null){
	    		timesRetired = (int) retiredCardPANInfo.getRetirecount();    		
	    	}
    	}
    	
    	return timesRetired;    	
    }
    /**
	 * Updates the retired_cardpan_info with the number of times a cardpan is retired.
	 * @param cardPan
	 * @param timesRetired
	 * @return
	 */
    private void updateCardPANInfo(String cardPan, int timesRetired) {
        RetiredCardPANInfoQuery query = new RetiredCardPANInfoQuery();
    	query.setCardPan(cardPan);
    	
    	RetiredCardPANInfoDAO dao = DAOFactory.getInstance().getRetiredCardPANInfoDAO();
    	List<RetiredCardPANInfo> results = dao.get(query);
    	
    	if(results.size() > 0){
    		RetiredCardPANInfo retiredCardPANInfo = results.get(0);
    		if(retiredCardPANInfo != null){
    			retiredCardPANInfo.setRetirecount(timesRetired);
    			dao.save(retiredCardPANInfo);
    		}    	    	
    	}
    	else{
    		RetiredCardPANInfo retiredCardPANInfo = new RetiredCardPANInfo();
    		retiredCardPANInfo.setCardpan(cardPan);
    		retiredCardPANInfo.setRetirecount(timesRetired);
    		dao.save(retiredCardPANInfo);
    	}
    }
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	private void suffixMDNWithRX(Long subscriberMDNId, String markedMDNRetireString) {
        // here we need to update the subscriber MDN also.
		Timestamp now = new Timestamp();
		SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
		
		SubscriberMdnQuery query = new SubscriberMdnQuery();
		query.setId(subscriberMDNId);
		List <SubscriberMdn> lst = subscriberMDNDAO.get(query);
		SubscriberMdn smdn = lst.get(0);
		
		if(smdn != null){
			smdn.setIsmdnrecycled(Boolean.TRUE);
			smdn.setMdn(markedMDNRetireString);
			smdn.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
			smdn.setStatus(CmFinoFIX.SubscriberStatus_Retired);
			smdn.setStatustime(now);
		}
		
		Subscriber subscriber = smdn.getSubscriber();
		subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
		subscriber.setStatus(CmFinoFIX.SubscriberStatus_Retired);
		subscriber.setStatustime(now);
		subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
		subscriberMDNDAO.save(smdn);
		subscriberDAO.save(subscriber);
		
    }
	
}
