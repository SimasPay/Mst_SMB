package com.mfino.ccpayment.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.uicore.fix.processor.RegistrationCodeConfirmationProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MfinoCreditCardUtil;
import com.mfino.util.MfinoUtil;

/**
 * This servlet receives requests from mdnvalidation.jsp to validate that mdn is
 * not already registered and is valid. generates and validates scramble code
 * and redirects to registration page
 */
public class ScrambleCodeValidateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ScrambleCodeValidateServlet.class);
	private static final String codeGenerated = "0";
	private static final String invalidCode = "1";
	private static final String registeredUser = "2";
	private static final String activeUser = "3";
	private static final String invalidMDN = "4";

	protected static SessionFactory sessionFactory = null;

	protected static HibernateSessionHolder hibernateSessionHolder = null;

	static {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..\\spring-datasource-beans.xml");
		sessionFactory = appContext.getBean(SessionFactory.class);
		hibernateSessionHolder = appContext.getBean(HibernateSessionHolder.class);
	}
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ScrambleCodeValidateServlet() {
		super();
		}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// redirect to login page
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/login.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Subscriber subscriber;
		SubscriberMDN subscriberMDN;
		
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/mdnvalidation.jsp");
		Session session = sessionFactory.openSession();
		hibernateSessionHolder.setSession(session);		
		DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);

		try {
			HibernateUtil.getCurrentSession().beginTransaction();
			String requestType = request.getParameter("requestType");
			String mdn = request.getParameter("mdn");
			request.setAttribute("mdn", mdn);
			if (StringUtils.isNotBlank(requestType)) {
				subscriberMDN = getSubscriberMDN(mdn);
				if (subscriberMDN != null && !subscriberMDN.getStatus().equals(CmFinoFIX.MDNStatus_Retired)) {
					subscriber = subscriberMDN.getSubscriber();
					User user = subscriber.getUserBySubscriberUserID();

					if (requestType.equalsIgnoreCase("generatecode")) {
						if (user == null || user.getStatus().equals(CmFinoFIX.UserStatus_Expired)) {
							generateScrambleCode(subscriberMDN, user, false);
							request.setAttribute("responsecode", codeGenerated);
							request.setAttribute("responsemsg", StringUtils.EMPTY);
							log.info("generatecode: code sent to mdn " + mdn);
						} else if (user.getStatus().equals(CmFinoFIX.UserStatus_Registered)) {
							if ((System.currentTimeMillis()- user.getCreateTime().getTime() 
									> ConfigurationUtil.getCreditcardRegistrationExpirationTimeInHrs() * 60 * 60 * 1000)) {
								log.info("User previous Registration expired");
								generateScrambleCode(subscriberMDN, user, true);
								request.setAttribute("responsecode", codeGenerated);
								request.setAttribute("responsemsg", StringUtils.EMPTY);
								log.info("generatecode: code sent to mdn "+ mdn);
							} else {
								dispatcher = getServletContext().getRequestDispatcher("/confirmRegistration.jsp");
								request.setAttribute("resultCode", registeredUser);
								request.setAttribute("resultMsg", "");
								log.info("generatecode: User already Registered with  mdn "+ mdn);
							}
						} else if (user.getStatus().equals(CmFinoFIX.UserStatus_Active)) {
							dispatcher = getServletContext().getRequestDispatcher("/confirmRegistration.jsp");
							request.setAttribute("resultCode", activeUser);
							request.setAttribute("resultMsg", "");
							log.info("generatecode: User already Active with  mdn "+ mdn);
						}
					} else if (requestType.equalsIgnoreCase("validatecode")) {
						String code = request.getParameter("code");
						String scrambleCode = subscriberMDN.getScrambleCode();
						if (scrambleCode.equals(code)) {
							request.setAttribute("mdn", MfinoUtil.normalizeMDN(mdn));
							dispatcher = getServletContext().getRequestDispatcher("/registration.jsp");
							log.info("validatecode: valid code redirected to registration page "+ mdn);
						} else {
							request.setAttribute("responsecode", invalidCode);
							request.setAttribute("responsemsg", StringUtils.EMPTY);
							log.info("validatecode: invalid code for  " + mdn);
						}
					} else {
						dispatcher = getServletContext().getRequestDispatcher("/login.jsp");
					}
				} else {
					request.setAttribute("responsecode", invalidMDN);
					request.setAttribute("responsemsg", "Invalid MDN");
					log.info("generatecode: Invalid or Retired MDN " + mdn);
				}
			} else {
				dispatcher = getServletContext().getRequestDispatcher("/login.jsp");
				log.info("Invalid RequestType");
			}

		} catch (Exception e) {
			log.info("ScrambledCodeValidation servlet error",e);
			request.setAttribute("responsecode", null);

		} finally {
			if(session!=null)
			{
				session.close();
			}
			dispatcher.forward(request, response);
		}

	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private void generateScrambleCode(SubscriberMDN subscriberMDN, User user, boolean expireUser) {
		if(expireUser)
		{
			RegistrationCodeConfirmationProcessor registrationCodeConfirmationProcessor = new RegistrationCodeConfirmationProcessor();
			registrationCodeConfirmationProcessor.expireUser(user);
		}
		String msg = ConfigurationUtil.getCCCodeNotificationMsg();
		MfinoCreditCardUtil.generateScrambleCode(msg,subscriberMDN);
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	private SubscriberMDN getSubscriberMDN(String mdn) {
		SubscriberMDN subscriberMDN;
		SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		subscriberMDN = subscriberMDNDAO.getByMDN(MfinoUtil.normalizeMDN(mdn));
		return subscriberMDN;
	}

	
}