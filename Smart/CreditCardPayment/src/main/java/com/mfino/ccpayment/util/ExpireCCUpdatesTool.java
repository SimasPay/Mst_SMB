/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.ccpayment.util;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.AddressDAO;
import com.mfino.dao.CardInfoDAO;
import com.mfino.dao.CreditCardDestinationDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.UserDAO;
import com.mfino.domain.Address;
import com.mfino.domain.CardInfo;
import com.mfino.domain.CreditCardDestinations;
import com.mfino.domain.Subscriber;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;

/**
 * 
 * @author admin
 */
public class ExpireCCUpdatesTool {
	UserDAO userDao;
	CardInfoDAO cardInfoDao;
	CreditCardDestinationDAO creditCardDestinationDAO;

	public ExpireCCUpdatesTool() {
		userDao = DAOFactory.getInstance().getUserDAO();
		cardInfoDao = DAOFactory.getInstance().getCardInfoDAO();
		creditCardDestinationDAO = DAOFactory.getInstance().getCreditCardDestinationDAO();
	}

	private static Logger log = LoggerFactory.getLogger(ExpireCCUpdatesTool.class);

	public void expireUpdates() {
/*		log.info("Attempting to expire Profile Updates.");

		HibernateUtil.getCurrentSession().beginTransaction();

		Integer hrs = ConfigurationUtil.getCreditcardUpdateExpirationTimeInHrs();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -hrs);
		CardInfoQuery query = new CardInfoQuery();
		query.setCardStatus(CmFinoFIX.UserStatus_Active);
		query.setLastUpdateTimeLT(cal.getTime());
		query.setIsConfirmationRequired(true);
		List<CardInfo> cards = cardInfoDao.get(query);
		HibernateUtil.getCurrentTransaction().rollback();
		int i = 0;

		for (Iterator<CardInfo> cardIterator = cards.iterator(); cardIterator.hasNext();) {
			CardInfo card = cardIterator.next();
			HibernateUtil.getCurrentSession().beginTransaction();
			if(revertChanges(card))
				i++;
		}
		log.info("profile changes reverted for " + i + " users");
*/	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public Boolean revertChanges(CardInfo card) {
		User user = null;
		SubscriberDAO subDao = DAOFactory.getInstance().getSubscriberDAO();
		Subscriber subscriber = subDao.getById(card.getSubscriber().getID());
		user = subscriber.getUserBySubscriberUserID();
		log.info("Reverting Profile change of " + user.getUsername());
		revertCardInfoChanges(card);
		List<CreditCardDestinations> ccDestinations = creditCardDestinationDAO.getAllDestinations(subscriber);
		for (Iterator<CreditCardDestinations> ccDestinationIterator = ccDestinations.iterator(); ccDestinationIterator.hasNext();) {
			CreditCardDestinations creditCardDestination = ccDestinationIterator.next();
			revertDestinationChanges(creditCardDestination);
		}
		revertUserChanges(user);
		log.info("Reverted Profile changes of " + user.getUsername());
		return true;
	}

	private void revertUserChanges(User user) {
		user.setFirstName(user.getOldFirstName());
		user.setLastName(user.getOldLastName());
		user.setHomePhone(user.getOldHomePhone());
		user.setWorkPhone(user.getOldWorkPhone());
		user.setSecurityQuestion(user.getOldSecurityQuestion());
		user.setSecurityAnswer(user.getOldSecurityAnswer());
		userDao.save(user);
	}

	private void revertDestinationChanges(CreditCardDestinations creditCardDestination) {
		if (creditCardDestination.getCCMDNStatus().equals(CmFinoFIX.CCMDNStatus_New)) {
			creditCardDestinationDAO.delete(creditCardDestination);
		} else if (creditCardDestination.getCCMDNStatus().equals(CmFinoFIX.CCMDNStatus_Updated)) {
			creditCardDestination.setCCMDNStatus(CmFinoFIX.CCMDNStatus_Active);
			creditCardDestination.setDestMDN(creditCardDestination.getOldDestMDN());
			creditCardDestinationDAO.save(creditCardDestination);
		}

	}

	private void revertCardInfoChanges(CardInfo card) {
		card.setCardF6(card.getOldCardF6());
		card.setCardL4(card.getOldCardL4());
		card.setIssuerName(card.getOldIssuerName());
		card.setNameOnCard(card.getOldNameOnCard());
		card.setisConformationRequired(false);
		cardInfoDao.save(card);
		AddressDAO addressDAO = new AddressDAO();
		Address address = card.getAddress();
		Address billingaddress = card.getAddressByBillingAddressID();
		Address oldAddress = card.getAddressByOldAddressID();
		Address oldBillingAddress = card.getAddressByOldBillingAddressID();

		address.setCity(oldAddress.getCity());
		address.setLine1(oldAddress.getLine1());
		address.setLine2(oldAddress.getLine2());
		address.setRegionName(oldAddress.getRegionName());
		address.setState(oldAddress.getState());
		address.setZipCode(oldAddress.getZipCode());

		billingaddress.setCity(oldBillingAddress.getCity());
		billingaddress.setLine1(oldBillingAddress.getLine1());
		billingaddress.setLine2(oldBillingAddress.getLine2());
		billingaddress.setRegionName(oldBillingAddress.getRegionName());
		billingaddress.setState(oldBillingAddress.getState());
		billingaddress.setZipCode(oldBillingAddress.getZipCode());

		addressDAO.save(address);
		addressDAO.save(billingaddress);

	}

	//	public static void main(String[] args) {
	//		System.out.println("Start expire registration tool");
	//		ExpireCCUpdatesTool expireUpdatesTool = new ExpireCCUpdatesTool();
	//		expireUpdatesTool.expireUpdates();
	//	}
}
