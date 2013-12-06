package com.mfino.web.admin.Listeners;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.User;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.uicore.security.AuthProcessingFilter;
import com.mfino.util.ConfigurationUtil;

public class MFinoHTTPSessionListener implements HttpSessionListener,
		HttpSessionBindingListener {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public void valueBound(HttpSessionBindingEvent event) {
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
	}

	public MFinoHTTPSessionListener() {
	}

	/**
	 * Called by the HttpSessionListener when a new session is created by the
	 * server container. Warning: This session may not be logged into the
	 * application yet.
	 * 
	 * @param httpSessionEvent
	 *            An HttpSessionEvent
	 */
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {

	}

	/**
	 * Called by the HttpSessionListener when a session is invalidated by the
	 * server container.
	 * 
	 * @param httpSessionEvent
	 *            An HttpSessionEvent
	 */
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
//		log.info(ConfigurationUtil.getConcurrentLoginsAllowed());
		/*
		 * Below logic is no longer useful as multiple login check is handled using spring-security
		 * 
		 */
		if (!ConfigurationUtil.getConcurrentLoginsAllowed()) {
			try {
				String loggedInUser = (String) ((HttpSession) (httpSessionEvent.getSession())).getAttribute("_LoggedUser");
				// log.info("In sessionDestroyed loggedInUser "+loggedInUser);
				// log.info("In sessionDestroyed loggedInHash "+AuthProcessingFilter.loggedInHash.size());
				if(loggedInUser != null) {
					if (AuthProcessingFilter.loggedInHash != null
							&& AuthProcessingFilter.loggedInHash.containsKey(loggedInUser)) {
						/*
						 * HibernateService hibernateService =
						 * CoreServiceFactory.getInstance().getHibernateService();
						 * Session session =
						 * hibernateService.getSessionFactory().openSession();
						 * HibernateSessionHolder sessionHolder =
						 * hibernateService.getHibernateSessionHolder();
						 * sessionHolder.setSession(session);
						 */
						UserDAO userDao = DAOFactory.getInstance().getUserDAO();
						User userObj = null;
						UserQuery query = new UserQuery();
						query.setUserName(loggedInUser);
						List<User> results = (List<User>) userDao.get(query);

						if (results.size() > 0) {
							userObj = results.get(0);
							userObj.setIsLoggedIn(false);
							userDao.save(userObj);
							log.info("sessionDestroyed for user " + loggedInUser);
						}
						// AuthProcessingFilter.loggedInHash.remove(loggedInUser);
					}
				}				
			} catch (Exception ex) {
				throw new MfinoRuntimeException(ex);
			}
		}
	}
}
