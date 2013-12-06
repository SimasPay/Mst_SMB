package com.mfino.ccpayment.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.processor.RegistrationCodeConfirmationProcessor;
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
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final String codeGenerated = "0";
	private static final String invalidCode = "1";
	private static final String registeredUser = "2";
	private static final String activeUser = "3";
	private static final String invalidMDN = "4";

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
		try {
			HibernateUtil.getCurrentSession().beginTransaction();
			String requestType = request.getParameter("requestType");
			String mdn = request.getParameter("mdn");
			request.setAttribute("mdn", mdn);
			if (StringUtils.isNotBlank(requestType)) {
				SubscriberMDNDAO subscriberMDNDAO = new SubscriberMDNDAO();
				subscriberMDN = subscriberMDNDAO.getByMDN(MfinoUtil.normalizeMDN(mdn));
				if (subscriberMDN != null && !subscriberMDN.getStatus().equals(CmFinoFIX.MDNStatus_Retired)) {
					subscriber = subscriberMDN.getSubscriber();
					User user = subscriber.getUserBySubscriberUserID();

					if (requestType.equalsIgnoreCase("generatecode")) {
						if (user == null || user.getStatus().equals(CmFinoFIX.UserStatus_Expired)) {
							request.setAttribute("responsecode", codeGenerated);
							request.setAttribute("responsemsg", StringUtils.EMPTY);
							String msg = ConfigurationUtil.getCCCodeNotificationMsg();
							MfinoCreditCardUtil.generateScrambleCode(msg,subscriberMDN);
							HibernateUtil.getCurrentTransaction().commit();
							log.info("generatecode: code sent to mdn " + mdn);
						} else if (user.getStatus().equals(CmFinoFIX.UserStatus_Registered)) {
							if ((System.currentTimeMillis()- user.getCreateTime().getTime() 
									> ConfigurationUtil.getCreditcardRegistrationExpirationTimeInHrs() * 60 * 60 * 1000)) {
								log.info("User previous Registration expired");
								RegistrationCodeConfirmationProcessor registrationCodeConfirmationProcessor = new RegistrationCodeConfirmationProcessor();
								registrationCodeConfirmationProcessor.expireUser(user);
								request.setAttribute("responsecode", codeGenerated);
								request.setAttribute("responsemsg", StringUtils.EMPTY);
								String msg = ConfigurationUtil.getCCCodeNotificationMsg();
								MfinoCreditCardUtil.generateScrambleCode(msg,subscriberMDN);
								HibernateUtil.getCurrentTransaction().commit();
								log.info("generatecode: code sent to mdn "+ mdn);
							} else {
								dispatcher = getServletContext().getRequestDispatcher("/confirmRegistration.jsp");
								request.setAttribute("resultCode", registeredUser);
								request.setAttribute("resultMsg", "");
								log.info("generatecode: User already Registered with  mdn "+ mdn);
								HibernateUtil.getCurrentTransaction().rollback();
							}
						} else if (user.getStatus().equals(CmFinoFIX.UserStatus_Active)) {
							dispatcher = getServletContext().getRequestDispatcher("/confirmRegistration.jsp");
							request.setAttribute("resultCode", activeUser);
							request.setAttribute("resultMsg", "");
							log.info("generatecode: User already Active with  mdn "+ mdn);
							HibernateUtil.getCurrentTransaction().rollback();
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
						HibernateUtil.getCurrentTransaction().rollback();
					} else {
						HibernateUtil.getCurrentTransaction().rollback();
						dispatcher = getServletContext().getRequestDispatcher("/login.jsp");
					}
				} else {
					request.setAttribute("responsecode", invalidMDN);
					request.setAttribute("responsemsg", "Invalid MDN");
					HibernateUtil.getCurrentTransaction().rollback();
					log.info("generatecode: Invalid or Retired MDN " + mdn);
				}
			} else {
				HibernateUtil.getCurrentTransaction().rollback();
				dispatcher = getServletContext().getRequestDispatcher("/login.jsp");
				log.info("Invalid RequestType");
			}

		} catch (Exception error) {
			log.error("ScrambledCodeValidation servlet error",error);
			HibernateUtil.getCurrentTransaction().rollback();
			request.setAttribute("responsecode", null);

		} finally {
			dispatcher.forward(request, response);
		}

	}

	
}