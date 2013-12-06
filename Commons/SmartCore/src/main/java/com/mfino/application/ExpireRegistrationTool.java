/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.application;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.CardInfoDAO;
import com.mfino.dao.CreditCardDestinationDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;

/**
 * 
 * @author admin
 */
public class ExpireRegistrationTool {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) {
		System.out.println("Start expire registration tool");
		ExpireRegistrationTool expireRegistrationTool = new ExpireRegistrationTool();
		expireRegistrationTool.expireRegistrations();
	}

	public void expireRegistrations() {
		log.info("Attempting to expire registrations.");
		HibernateUtil.getCurrentSession().beginTransaction();
		Integer hrs = ConfigurationUtil.getCreditcardRegistrationExpirationTimeInHrs();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -hrs);
		UserDAO userDao = DAOFactory.getInstance().getUserDAO();
		CardInfoDAO cardInfoDao = DAOFactory.getInstance().getCardInfoDAO();
		CreditCardDestinationDAO creditCardDestinationDAO = DAOFactory.getInstance().getCreditCardDestinationDAO();
				
		int updatedRows = userDao.expireRegistrations(cal.getTime());
		log.info("number of user expired : " + updatedRows);
		
		updatedRows = creditCardDestinationDAO.expireRegistrations(cal.getTime());
		log.info("number of Destination MDNS expired : " + updatedRows);
		
		updatedRows = cardInfoDao.expireRegistrations(cal.getTime());
		log.info("number of cards expired : " + updatedRows);

		HibernateUtil.getCurrentTransaction().commit();
	}
	
}
