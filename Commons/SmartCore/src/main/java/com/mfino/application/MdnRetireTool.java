/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.CardInfoDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CreditCardDestinationDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.CardInfo;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.CreditCardDestinations;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author Venkata Krishna Teja D
 */
public class MdnRetireTool {


	private Logger log = LoggerFactory.getLogger(this.getClass());
    public static void main(String[] args) {
        System.out.println("Start MDN retire tool");
        MdnRetireTool mdnRetireToolMain = new MdnRetireTool();
        mdnRetireToolMain.markRetiredMDNs();
    }

    private MdnRetireTool() {
    }

    /** 
     * STEP 1: Get the Retired MDNs from the subscriber_mdn table.
     * STEP 2: For each MDN create the new Retired MDN Flag.
     * STEP 3: Get All BulkUpload Entries for this MDN.
     * STEP 4: Update the MDN with the new Retired MDN Flag
     * STEP 5: Update the MDN with the Retired MDN Flag.
     */
    private void markRetiredMDNs() {

        log.info("Attempting to get all Non recycled mdns.");

        SubscriberMdnQuery subscriberMdnQuery = new SubscriberMdnQuery();
        subscriberMdnQuery.setSubscriberMDNStatusRetire(true);
        subscriberMdnQuery.setMDNNotRecycled(true);

        HibernateUtil.getCurrentSession().beginTransaction();
        SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();

        List<SubscriberMDN> retiredMDNs = subscriberMDNDAO.get(subscriberMdnQuery);
        HibernateUtil.getCurrentSession().getTransaction().commit();
        log.info("Successfully got <" + retiredMDNs.size() + "> subscriber records.");

        for (SubscriberMDN retiredMDN : retiredMDNs) {
            String mdn = retiredMDN.getMDN();

        	log.info("--------------------------------------------------------------------------------");
            log.info("Start -- processing   <" + mdn + ">");

            if (StringUtils.isBlank(mdn)) {
                continue;
            }

            String mdnRXString = getRXString(retiredMDN);
            log.info("MDNRX = <" + mdnRXString + ">");

            // Update All Commodity Transfer records.
            log.info("Start -- mark all the records related to <" + mdn + "> as retired.");
            try {
                updateAllCommodityTransferRecords(retiredMDN.getID(), mdn, mdnRXString);
            } catch (Exception ex) {
                log.error("Unable to mark records in CommodityTransfer for <" + mdn + ">.", ex);
                log.error("Rolling Back the Transaction");
                continue;
            }
            log.info("End -- marked all the records related to <" + mdn + "> as retired.");

            // Here we need to retire the CardPan for this MDN.
            log.info("Start -- mark all the CardPans related to <" + mdn + "> as retired.");
            try {
                retireAllCardPans(retiredMDN.getID());
            } catch (Exception exp) {
                log.error("Unable to mark the CardPans as retired for <" + mdn + ">.", exp);
                log.error("Rolling Back the Transaction");
                continue;
            }
            log.info("End -- marked all the CardPans related to <" + mdn + "> as retired.");

           
            try {
                retireCreditCardUser(retiredMDN,mdnRXString);
            } catch (Exception exp) {
                log.error("Unable to mark the CreditCardUser as retired for <" + mdn + ">.", exp);
                continue;
            }
            

            suffixMDNWithRX(retiredMDN, subscriberMDNDAO, mdnRXString);

            log.info("End -- renamed <" + mdn + ">  --> <" + mdnRXString + ">.");
            log.info("--------------------------------------------------------------------------------");
        }
    }

    private void retireCreditCardUser(SubscriberMDN retiredMDN,String retireSuffix) {
    	HibernateUtil.getCurrentSession().beginTransaction();
		SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
		Subscriber subscriber = subscriberDAO.getById(retiredMDN.getSubscriber().getID());
		User ccuser = subscriber.getUserBySubscriberUserID();
		
    	if(ccuser!=null){
    		log.info("Retiring CreditCardUser for Subscriber"+retiredMDN.getMDN());
    		//SubscriberMDN subscriberMDN = (SubscriberMDN) sub.getSubscriberMDNFromSubscriberID().toArray()[0];
    		Set<CardInfo> cards = subscriber.getCardInfoFromSubscriberID();
    		CreditCardDestinationDAO creditCardDestinationDAO = DAOFactory.getInstance().getCreditCardDestinationDAO();
    		UserDAO userDao = DAOFactory.getInstance().getUserDAO();
    		CardInfoDAO cardDao = DAOFactory.getInstance().getCardInfoDAO();
    		List<CreditCardDestinations> ccDestinations = creditCardDestinationDAO.getAllDestinations(subscriber);
    		
    		log.info("Retiring all cards for user"+ccuser.getUsername());
    		for (Iterator<CardInfo> cardIterator = cards.iterator(); cardIterator.hasNext();) {
    			CardInfo card = cardIterator.next();
    			if (!card.getCardStatus().equals(CmFinoFIX.UserStatus_Expired)) {
    				card.setCardStatus(CmFinoFIX.UserStatus_Retired);
    			}
    		}
    		cardDao.save(cards);
    		
    		log.info("Retiring Destinations for user"+ccuser.getUsername());
    		for (Iterator<CreditCardDestinations> creIterator = ccDestinations.iterator(); creIterator.hasNext();) {
    			CreditCardDestinations ccdeDestination = creIterator.next();
    			if (!ccdeDestination.getCCMDNStatus().equals(CmFinoFIX.CCMDNStatus_Expired)) {
    				ccdeDestination.setCCMDNStatus(CmFinoFIX.CCMDNStatus_Retired);
    			}
    		}
    		creditCardDestinationDAO.save(ccDestinations);
    		
    		ccuser.setStatus(CmFinoFIX.UserStatus_Retired);
    		ccuser.setStatusTime(new Timestamp());
    		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		// setting user name as user name, Retired tag and time stamp
    		ccuser.setUsername(retireSuffix);
    		userDao.save(ccuser);
    		HibernateUtil.getCurrentTransaction().commit();
    		log.info("Retired CreditCardUser for Subscriber"+retiredMDN.getMDN());
    	}
    	}

	private void suffixMDNWithRX(SubscriberMDN retiredMDN, SubscriberMDNDAO subscriberMDNDAO, String markedMDNRetireString) {
        
        HibernateUtil.getCurrentSession().beginTransaction();
        // here we need to update the subscriber MDN also.
        retiredMDN.setIsMDNRecycled(Boolean.TRUE);
        retiredMDN.setMDN(markedMDNRetireString);
        subscriberMDNDAO.save(retiredMDN);        
        HibernateUtil.getCurrentSession().getTransaction().commit();
    }

    private void updateAllCommodityTransferRecords(Long mdnID, String mdn, String mdnRX) throws Exception {
    	HibernateUtil.getCurrentSession().beginTransaction();
    	CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
    	ctDao.markSourceMDNRX(mdnID, mdnRX);
    	ctDao.markDestMDNRX(mdn, mdnRX);
    	
        HibernateUtil.getCurrentSession().getTransaction().commit();
    	
    }
    
    private void updateAllCommodityTransferRecords_Old(String mdn, String markedMDNRetireString) throws Exception {
        // Now get all Commodity Transfer Records.
        HibernateUtil.getCurrentSession().beginTransaction();
        CommodityTransferQuery commodityTransferQuery = new CommodityTransferQuery();
        commodityTransferQuery.setSourceDestnMDN(mdn);

        log.info("Start -- getting the commodity transfer records for <" + mdn + ">");
        CommodityTransferDAO commodityTransferDAO = DAOFactory.getInstance().getCommodityTransferDAO();
        List<CommodityTransfer> resultantCommodityTransferRecords = null;
        try {
            resultantCommodityTransferRecords = commodityTransferDAO.get(commodityTransferQuery);
        } catch (Exception ex) {
        	log.error(ex.getMessage(), ex);
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            throw new Exception(ex);
        }
        HibernateUtil.getCurrentSession().getTransaction().commit();
        log.info("End -- Got <" + resultantCommodityTransferRecords.size() + "> records for <" + mdn + ">");

        log.info("Start -- mark all <" + resultantCommodityTransferRecords.size() + "> records" +
                "  <" + markedMDNRetireString + "> in bulks of 100 records each.");

        //  We need to take care that we save 100
        //  bulk records for CommodityTransfer update.
        List<CommodityTransfer> bulkUpdatePlaceHold = new ArrayList<CommodityTransfer>();
        int seqCounter = 0;
        for (int i = 0; i < resultantCommodityTransferRecords.size(); i++) {
            HibernateUtil.getCurrentSession().beginTransaction();

            CommodityTransfer eachRecord = resultantCommodityTransferRecords.get(i);
            String sourceMDN = eachRecord.getSourceMDN();
            String destMDN = eachRecord.getDestMDN();

            if (sourceMDN.equalsIgnoreCase(mdn)) {
                eachRecord.setSourceMDN(markedMDNRetireString);
            }

            if (destMDN.equalsIgnoreCase(mdn)) {
                eachRecord.setDestMDN(markedMDNRetireString);
            }

            bulkUpdatePlaceHold.add(eachRecord);

            seqCounter++;
            if (seqCounter == 99) {
                commodityTransferDAO.save(bulkUpdatePlaceHold);

                seqCounter = 0;
                bulkUpdatePlaceHold = new ArrayList<CommodityTransfer>();
            }
            HibernateUtil.getCurrentSession().getTransaction().commit();
        }
        if (null != bulkUpdatePlaceHold && 0 != bulkUpdatePlaceHold.size()) {
            HibernateUtil.getCurrentSession().beginTransaction();
            commodityTransferDAO.save(resultantCommodityTransferRecords);
            HibernateUtil.getCurrentSession().getTransaction().commit();
        }
    }

    private String getRXString(SubscriberMDN subscriberMDN) {
        HibernateUtil.getCurrentSession().beginTransaction();
        SubscriberMdnQuery subscriberMdnQuery = new SubscriberMdnQuery();
        subscriberMdnQuery.setMdn(subscriberMDN.getMDN());

        try {
            SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
            List<SubscriberMDN> subscriberMDNLikeRecords = subscriberMDNDAO.get(subscriberMdnQuery);

            int noOfTimesRetired = 0;
            for (SubscriberMDN subscriberLikeMDN : subscriberMDNLikeRecords) {
                String mdn = subscriberLikeMDN.getMDN();
                if (null == mdn || 0 == mdn.trim().length()) {
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
                    // TODO :: Handle this.
                	log.error("getRxString", nfe);
                }

                if (timesRetired >= noOfTimesRetired) {
                    noOfTimesRetired = timesRetired + 1;
                }
            }
            if (subscriberMDN.getMDN().contains("R")) {
                // If we reach here then we already have R1.
                String mdn = subscriberMDN.getMDN();
                String mdnWithoutR = mdn.substring(0, mdn.indexOf("R"));
                return mdnWithoutR + "R" + noOfTimesRetired;
            } else {
                return subscriberMDN.getMDN() + "R" + noOfTimesRetired;
            }

        } catch (Throwable throwable) {
            log.error("Error in MDNRetireTool", throwable);
            HibernateUtil.getCurrentSession().getTransaction().rollback();
        }

        HibernateUtil.getCurrentSession().getTransaction().commit();
        return subscriberMDN.getMDN() + "R0";
    }

    private void retireAllCardPans(Long mdnId) throws Exception {
        // Here we need to get the Records from pocket table with this ID.        
        PocketQuery pocketQuery = new PocketQuery();
        pocketQuery.setMdnIDSearch(mdnId);

        HibernateUtil.getCurrentSession().beginTransaction();
        PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
        List<Pocket> resultantPockets = pocketDAO.get(pocketQuery);

        for (Pocket eachPocket : resultantPockets) {
            String cardPan = eachPocket.getCardPAN();
            if (null == cardPan || 0 == cardPan.trim().length()) {
                continue;
            }
            String cardPanStringToReplace = getCardPanRetiredStringForThisCardPan(cardPan);

            if (null == cardPanStringToReplace) {
                continue;
            }

            eachPocket.setCardPAN(cardPanStringToReplace);
            // Now set back the Data into the table.
            pocketDAO.save(eachPocket);
        }
        HibernateUtil.getCurrentSession().getTransaction().commit();
    }

    private String getCardPanRetiredStringForThisCardPan(String cardPan) {
        try {
            PocketQuery pocketQuery = new PocketQuery();
            pocketQuery.setCardPan(cardPan);
            pocketQuery.setPocketCardPaneLikeSearch(Boolean.TRUE);

            PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
            List<Pocket> results = pocketDAO.get(pocketQuery);
            int noOfTimesRetired = 0;
            for (Pocket pocket : results) {
                String pocketCardPan = pocket.getCardPAN();
                if (null == pocketCardPan || 0 == pocketCardPan.trim().length()) {
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
                    // TODO :: Handle this.
                	log.error(nfe.getMessage(), nfe);
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

        } catch (Throwable throwable) {
            log.error("Error in MDNRetireTool", throwable);
        }

        return cardPan + "R0";
    }
}
