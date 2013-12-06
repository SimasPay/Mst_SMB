package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;

import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.validators.BrandValidator;

/**
 * This servlet receives AJAX requests to validate mdn request to
 */
public class ValidateRegistrationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ValidateRegistrationServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/login.jsp");
		dispatcher.forward(request, response);
		}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		/*
		 * To process all AJAX validation requests from registration.jsp.
		 * Requests are: Check if username is already registered, check if mdn
		 * is valid, check if scramble code is valid
		 */
			String isValid = "true";
		 String registermdn = request.getParameter("mdn");
		if(registermdn != null){
			isValid = String.valueOf(isMDNValid(MfinoUtil.normalizeMDN(registermdn)));
		}else{
		Enumeration<String> e = request.getParameterNames();
		String mdn = request.getParameter(e.nextElement());
		if (mdn != null) {
			isValid = String.valueOf(isValidBrandMDN(mdn));
		} 
		}
		// }
		response.setHeader("Content-Type", "application/json");
		PrintWriter writer = response.getWriter();
		writer.println(isValid);
		}
	

	private boolean isValidBrandMDN(String mdn) {
		BrandValidator brandValidator = new BrandValidator(mdn);
		Integer validationResult = brandValidator.validate();
		if(validationResult.equals(CmFinoFIX.ResponseCode_Success))
		return true;
		
		return false;
	}

	// }

	private boolean isMDNValid(String mdn) {

		boolean isValid = false;
		if (mdn != null) {
			try {
				HibernateUtil.getCurrentSession().beginTransaction();
				// Call the method in SubscriberService to see if this
				// subscriber
				// exists. If yes, return true.
				SubscriberMDNDAO mdnDAO = new SubscriberMDNDAO();
				SubscriberMDN subscriberMDN = mdnDAO.getByMDN(mdn);
				if (subscriberMDN != null&& !subscriberMDN.getStatus().equals(CmFinoFIX.SubscriberStatus_Retired)) {
					isValid = true;
//					if (!ConfigurationUtil.isInterCompanyCCPaymentAllowed()
//							&& subscriberMDN.getSubscriber().getCompany().getID() != 1L)
//						isValid = false;
				}
			} catch (HibernateException hiExcep) {
				isValid = false;
				log.error("ValidateRegistrationServlet: MDN  is " + mdn, hiExcep);
				throw new DataRetrievalFailureException(
						"Exception validating MDN ", hiExcep);
			} finally {
				HibernateUtil.getCurrentSession().getTransaction().rollback();
			}
		}
		return isValid;
	}

	/**
	 * Return <code>true</code> if email is already registered
	 */
	/*
	 * private boolean isUserIDRegistered(String email) { boolean
	 * userIDRegistered = false; try {
	 * HibernateUtil.getCurrentSession().beginTransaction(); UserDAO userDAO =
	 * new UserDAO(); UserQuery userQuery = new UserQuery();
	 * userQuery.setUserName(email); List<User> results = (List<User>)
	 * userDAO.get(userQuery); if(results.size() > 0) userIDRegistered = true;
	 * HibernateUtil.getCurrentSession().getTransaction().commit(); } catch
	 * (HibernateException hiExcep) { userIDRegistered = false;
	 * HibernateUtil.getCurrentSession().getTransaction().rollback(); throw new
	 * DataRetrievalFailureException( "Exception validating Email registered ",
	 * hiExcep); } return userIDRegistered; }
	 */
}