package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

import com.mfino.cc.message.CCInfo;
import com.mfino.cc.message.CCRegistrationInfo;
import com.mfino.ccpayment.util.RegistrationUtil;
import com.mfino.ccpayment.util.RegistrationUtil.RequestType;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.domain.CreditCardDestinations;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX.CMJSConfirmationCode;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.uicore.fix.processor.RegistrationCodeConfirmationProcessor;
import com.mfino.util.MfinoUtil;

/**
 * Servlet for posting Registration Data to the Database. After data has been
 * posted successfully, an email is sent to the subscriber.
 */
/**
 * @author maruthi
 *
 */
public class RegistrationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(RegistrationServlet.class);

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
	public RegistrationServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processCodeConfirmation(request, response, "get");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String requestType = request.getParameter("requestType");
		if (requestType.equals("registration")) {
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/confirmRegistration.jsp");
			log.info("Request type is registration");
			try {
				CCRegistrationInfo registrationInfo = processRequestForRegistration(request);
				log.info("RegistrationInfo posted "+ registrationInfo.toString());
				RegistrationUtil registrationUtil = new RegistrationUtil();
				registrationUtil.process(registrationInfo, RequestType.Register);
				request.setAttribute("resultCode", registrationInfo.getErrorCode());
				request.setAttribute("resultMsg", registrationInfo.getErrorDescription());
			} catch (Exception e) {
				log.info("Exception during registration", e);
				request.setAttribute("resultCode", 1);
				request.setAttribute("resultMsg", "Could not register");
			} finally {
				dispatcher.forward(request, response);
			}
		} else if (requestType.equals("confirmCode")) {
			log.info("Request type is confirmCode");
			processCodeConfirmation(request, response, "post");
		}

	}

	private CCRegistrationInfo processRequestForRegistration(
			HttpServletRequest request) {
		// User related details
		String mdn = request.getParameter("mdn");
		String username = request.getParameter("username");
		String dob = request.getParameter("dob");
		String firstName = request.getParameter("firstname");
		String lastName = request.getParameter("lastname");
		String emailID = request.getParameter("email");
		String password = request.getParameter("password");
		String securityQuestion = request.getParameter("securityQuestion");
		if (securityQuestion.equals("ownquestion")) {
			securityQuestion = request.getParameter("ownquestion");
		}
		String securityAnswer = request.getParameter("securityAnswer");
		String homePhone = request.getParameter("homephone");
		String workPhone = request.getParameter("workphone");

		CCRegistrationInfo registrationInfo = new CCRegistrationInfo();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			registrationInfo.setDateOfBirth(dateFormat.parse(dob));
		} catch (ParseException e) {
			log.error("Error parsing date of birth ", e);
		}
		registrationInfo.setUsername(username);
		registrationInfo.setEmail(emailID);
		registrationInfo.setFirstName(firstName);
		registrationInfo.setLastName(lastName);
		registrationInfo.setMdn(MfinoUtil.normalizeMDN(mdn));
		registrationInfo.setPassword(password);
		registrationInfo.setSecurityAnswer(securityAnswer);
		registrationInfo.setSecurityQuestion(securityQuestion);
		registrationInfo.setHomePhone(homePhone);
		registrationInfo.setWorkPhone(workPhone);
		// registrationInfo.setH
		List<CCInfo> ccInfoList = new ArrayList<CCInfo>();

		Integer numOfCards = Integer.parseInt(request.getParameter("numOfCards"));
		for (int i = 1; i <= numOfCards; i++) {
			String f6 = request.getParameter("f6");
			String l4 = request.getParameter("l4");
			String bankName = request.getParameter("bankName_" + i);
			String nameOnCard = request.getParameter("nameOnCard");

			// Address related details
			String Address = request.getParameter("billingAddress_" + i);
			String line2 = request.getParameter("AddressLine2_" + i);
			String city = request.getParameter("city_" + i);
			String state = request.getParameter("state_" + i);
			String region = request.getParameter("region_" + i);
			String zipCode = request.getParameter("zipCode_" + i);
			++i;
			String billingAddress = request.getParameter("billingAddress_" + i);
			String billingline2 = request.getParameter("AddressLine2_" + i);
			String billingcity = request.getParameter("city_" + i);
			String billingstate = request.getParameter("state_" + i);
			String billingregion = request.getParameter("region_" + i);
			String billingzipCode = request.getParameter("zipCode_" + i);

			if (StringUtils.isNotEmpty(Address) && StringUtils.isNotEmpty(f6)
					&& StringUtils.isNotEmpty(l4)
					&& StringUtils.isNotEmpty(city)
					&& StringUtils.isNotEmpty(bankName)
					&& StringUtils.isNotEmpty(nameOnCard)
					&& StringUtils.isNotEmpty(state)
					&& StringUtils.isNotEmpty(zipCode)
					&& StringUtils.isNotEmpty(billingcity)
					&& StringUtils.isNotEmpty(billingAddress)
					&& StringUtils.isNotEmpty(billingstate)
					&& StringUtils.isNotEmpty(billingzipCode)) {
				CCInfo ccInfo = new CCInfo();
				ccInfo.setAddress1(Address);
				ccInfo.setAddressLine2(line2);
				ccInfo.setCCNumberF6(f6);
				ccInfo.setCCNumberL4(l4);
				ccInfo.setCity(city);
				ccInfo.setIssuerName(bankName);
				ccInfo.setNameOnCard(nameOnCard);
				ccInfo.setRegion(region);
				ccInfo.setState(state);
				ccInfo.setZipCode(zipCode);
				ccInfo.setBillingAddress(billingAddress);
				ccInfo.setBillingaddressLine2(billingline2);
				ccInfo.setBillingcity(billingcity);
				ccInfo.setBillingregion(billingregion);
				ccInfo.setBillingstate(billingstate);
				ccInfo.setBillingzipCode(billingzipCode);
				ccInfoList.add(ccInfo);
			} else {
				log.info("Invalid CCinfo");
			}
		}
		registrationInfo.setCcList(ccInfoList);
		List<CreditCardDestinations> ccDestList = new ArrayList<CreditCardDestinations>();
		Integer noOfDestinations = Integer.parseInt(request.getParameter("destinations"));
		for (int i = 2; i <= noOfDestinations; i++) {
			String destination = request.getParameter("destination" + i);
			if (StringUtils.isNotBlank(destination)) {
				CreditCardDestinations ccDest = new CreditCardDestinations();
				destination = destination.trim();
				ccDest.setDestMDN(destination);
				ccDestList.add(ccDest);
			}
		}
		registrationInfo.setCcDestinations(ccDestList);

		return registrationInfo;
	}

	private void processCodeConfirmation(HttpServletRequest request,
			HttpServletResponse response, String requestType)
					throws ServletException, IOException {

		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/codeConfirmationResult.jsp");
		String username = request.getParameter("username");
		String code = request.getParameter("confirmationCode");
		request.setAttribute("requestType", requestType);
		if (username != null && code != null) {
			Session session = sessionFactory.openSession();
			hibernateSessionHolder.setSession(session);		
			DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);
			try {
				
				username = MfinoUtil.normalizeMDN(username);
				User user = getUser(username);
				if (user != null && user.getConfirmationCode().equals(code)) {
					log.info("valid username and code send to RegistrationCodeConfirmationProcessor");
					CMJSConfirmationCode msg = new CMJSConfirmationCode();
					msg.setUsername(username);
					msg.setConfirmationCode(code);
					RegistrationCodeConfirmationProcessor regCodeConfirmProcessor = new RegistrationCodeConfirmationProcessor();
					try{
						CMJSError errorMsg = (CMJSError) regCodeConfirmProcessor.process(msg);
						request.setAttribute("resultCode", errorMsg.getErrorCode());
						request.setAttribute("resultMsg", errorMsg.getErrorDescription());
						log.info("resultCode" + errorMsg.getErrorCode()+ " resultMsg" + errorMsg.getErrorDescription());
					}catch (Exception e) {
						log.error("failed to active user");
						request.setAttribute("resultCode", "6");
					}					
				} else {
					request.setAttribute("resultCode", "1");
					request.setAttribute("resultMsg","Invalid UserName or Code");
				}

			} catch (Exception e) {
				request.setAttribute("resultCode", -1);
				request.setAttribute("resultMsg", "Could not confirm the code");
				log.info("exception in code confirmation ", e);
			}
			finally
			{
				if(session!=null)
				{
					session.close();
				}
			}
		} else {
			dispatcher = getServletContext().getRequestDispatcher("/login.htm");
		}
		dispatcher.forward(request, response);
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	private User getUser(String username) {
		UserDAO userDao = DAOFactory.getInstance().getUserDAO();
		User user = userDao.getByUserName(username);
		return user;
	}
}
