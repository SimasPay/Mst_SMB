/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.ccpayment.servlet;

import java.io.IOException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.cc.message.CCInfo;
import com.mfino.cc.message.CCRegistrationInfo;
import com.mfino.ccpayment.util.RegistrationUtil;
import com.mfino.ccpayment.util.RegistrationUtil.RequestType;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.domain.CreditCardDestinations;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.uicore.service.UserService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoCreditCardUtil;
import com.mfino.util.MfinoUtil;

/**
 * This Servlet will handle all requests that are to do with editing of a user's
 * profile. The requests that will come to this servlet are
 * <ul>
 * <li>Edit Profile</li>
 * <li>Change Password</li>
 * <li>and Verification of the Security Question required for editing profile.</li>
 * </ul>
 * 
 * @author Raju
 */
public class UpdateProfileServlet extends HttpServlet {
	/**
	 * Default Serial Version ID
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(UpdateProfileServlet.class);
	private static final int MAX_WRONG_COUNT = 5;
	private static final String codeSuccess = "0";
	private static final String wrongValue = "1";
	private static final String limit = "2";
	private static final String exception = "3";

	protected static SessionFactory sessionFactory = null;

	protected static HibernateSessionHolder hibernateSessionHolder = null;

	static {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..\\spring-datasource-beans.xml");
		sessionFactory = appContext.getBean(SessionFactory.class);
		hibernateSessionHolder = appContext.getBean(HibernateSessionHolder.class);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher;
		String requestType = request.getParameter("requestType");
		dispatcher = getDispatcher(requestType);

		Session session = sessionFactory.openSession();
		hibernateSessionHolder.setSession(session);		
		DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);

		try{


			// Process request based on where the request is coming from
			if (requestType.equals("editProfile")) {
				log.info("Request Type is Edit Profile");
				try {
					CCRegistrationInfo registrationInfo = processRequestForEditProfile(request);
					log.info("Update Info  "+ registrationInfo.toString());
					RegistrationUtil registrationUtil = new RegistrationUtil();
					registrationUtil.process(registrationInfo, RequestType.Update);
					request.setAttribute("resultCode", registrationInfo.getErrorCode());
					request.setAttribute("resultMsg", registrationInfo.getErrorDescription());
				} catch (Exception e) {
					log.info("exception in update profile", e);
					request.setAttribute("resultCode", wrongValue);
					request.setAttribute("resultMsg", "Could process your request");
				}
			} else if (requestType.equals("changePassword")) {
				// TODO: Code for Change Password
				log.info("Request type is Change Password");
				String currentPasswd = request.getParameter("currentPasswd");
				String newPasswd = request.getParameter("newPasswd");
				String confirmPasswd = request.getParameter("confirmPasswd");
				if (newPasswd.equals(confirmPasswd)) {
					try {
						String username = SecurityContextHolder.getContext().getAuthentication().getName();
						UserService.changePassword(username, currentPasswd,newPasswd, Boolean.FALSE, true);
						request.setAttribute("resultCode", codeSuccess);
						request.setAttribute("resultMsg","Your pin has been successfully updated");
						log.info("pin updated successfully for user "+username);
					} catch (UsernameNotFoundException exp) {
						log.info("Old pin doesnot match", exp);
						dispatcher = getServletContext().getRequestDispatcher("/changePassword.jsp");
						request.setAttribute("resultCode", wrongValue);
						request.setAttribute("resultMsg", "Invalid old password");
					} catch (Exception e) {
						log.info("Exception", e);
						dispatcher = getServletContext().getRequestDispatcher("/changePassword.jsp");
						request.setAttribute("resultCode", wrongValue);
						request.setAttribute("resultMsg","Exception occured try after some time");
					}
				} else {
					log.info("new , confirm pins are different");
					request.setAttribute("resultCode", wrongValue);
					request.setAttribute("resultMsg","pins doesnot match. Try Again");
				}
			} else if (requestType.equals("securityCheck")) {
				log.info("Request type is security check");
				dispatcher = verifySecurityQuestion(request, response);
			}else if (requestType.equalsIgnoreCase("editEmailCodegenerate")) {
				log.info("Requesttype is generatecode for edit Email");
				dispatcher = getServletContext().getRequestDispatcher("/editEmail.jsp");
				try {
					generateCode();
					request.setAttribute("newemail", request.getParameter("newemail"));
					request.setAttribute("responsecode", codeSuccess);
					request.setAttribute("responsemsg", "");
				}catch (Exception e) {
					log.info("Exception in generatecode for edit Email",e);
					request.setAttribute("responsecode", exception);
					request.setAttribute("responsemsg", "");
				}
			} else if (requestType.equalsIgnoreCase("editEmailCodeValidation")) {
				log.info("Requesttype is code validation for edit Email");
				dispatcher = getServletContext().getRequestDispatcher("/editEmail.jsp");
				try {
					User user = UserService.getCurrentUser();
					Subscriber subscriber =  user.getSubscriberFromSubscriberUserID().iterator().next();
					SubscriberMDN subscriberMDN =  subscriber.getSubscriberMDNFromSubscriberID().iterator().next();
					String code = request.getParameter("code");
					String newemail = request.getParameter("newemail");
					request.setAttribute("newemail", newemail);
					String scrambleCode = subscriberMDN.getScrambleCode();
					if (scrambleCode.equals(code)) {
						UserDAO userDao = DAOFactory.getInstance().getUserDAO();
						user.setEmail(newemail);
						userDao.save(user);
						request.setAttribute("resultCode", limit);
						request.setAttribute("resultMsg", "");
						dispatcher = getServletContext().getRequestDispatcher("/confirmUpdation.jsp");
					} else {
						log.info("Wrong code for edit email");
						request.setAttribute("responsecode", wrongValue);
						request.setAttribute("responsemsg", "Wrong code");
					}
				}catch (Exception e) {
					log.info("Exception for EmailCode Validation",e);
					request.setAttribute("responsecode", exception);
					request.setAttribute("responsemsg", "exception");
				}
			}
		}
		finally
		{
			if(session!=null)
			{
				session.close();
			}
		}
		dispatcher.forward(request, response);
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private void generateCode() {
		User user = UserService.getCurrentUser();
		Subscriber subscriber = user.getSubscriberFromSubscriberUserID().iterator().next();
		SubscriberMDN subscriberMDN = subscriber.getSubscriberMDNFromSubscriberID().iterator().next();
		String msg = ConfigurationUtil.getCCCodeNotificationMsgForEditEmail();
		MfinoCreditCardUtil.generateScrambleCode(msg, subscriberMDN);
	}

	private RequestDispatcher verifySecurityQuestion(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/securityCheck.jsp");
		try {
			String answer = request.getParameter("Answer");
			CMJSError error = RegistrationUtil.getSecurityAnswer();
			if (error.getErrorCode().equals(CmFinoFIX.ErrorCode_NoError)) {
				if (answer.equals(error.getErrorDescription())) {
					log.info("Security Answer verification successful");
					request.getSession().setAttribute("count", null);
					if ("editProfile".equals(request.getParameter("toPage"))) {
						dispatcher = getServletContext().getRequestDispatcher("/editProfile.jsp");
						request.setAttribute("response", codeSuccess);
					} else if ("changePassword".equals(request.getParameter("toPage"))) { // redirect to new page
						dispatcher = getServletContext().getRequestDispatcher("/changePassword.jsp");
						request.setAttribute("response", codeSuccess);
					} else if ("editEmail".equals(request.getParameter("toPage"))) { // redirect to new page
						dispatcher = getServletContext().getRequestDispatcher("/editEmail.jsp");
						request.setAttribute("response", codeSuccess);
					} else {
						log.info("invalid request redirecting to login page");
						dispatcher = getServletContext().getRequestDispatcher("/login.htm");
					}
				} else {
					// invalid password entered
					log.info("Wrong Security Answer");
					Integer failCount = null;
					String failCountStr = (String) request.getSession().getAttribute("count");
					if (failCountStr != null) {
						failCount = Integer.parseInt(failCountStr);
					} else {
						failCount = 1;
					}
					if (failCount >= MAX_WRONG_COUNT) {
						log.info("User entered security answer wrongly more than"+MAX_WRONG_COUNT);
						request.getSession().setAttribute("response", limit);
						request.getSession().setAttribute("msg","Please contact adminstrator. <br> You cannot perform edit profile or change password, as you have exceeded the number of retries for security answer.");
					} else {
						failCount = failCount + 1;
						request.setAttribute("response", wrongValue);
						request.getSession().setAttribute("count",failCount.toString());
					}
				}
			} else {
				log.info("Failed to fetch security answer");
				request.setAttribute("response", exception);
			}
		} catch (Exception e) {
			log.info("exception in verify security Answer", e);
			request.setAttribute("response", exception);
		}
		return dispatcher; 
	}

	private RequestDispatcher getDispatcher(String requestType) {
		if (requestType.equals("editProfile")) {
			return getServletContext().getRequestDispatcher("/confirmUpdation.jsp");
		} else if (requestType.equals("changePassword")) {
			return getServletContext().getRequestDispatcher("/confirmPassword.jsp");
		}else if (requestType.equals("securityCheck")) {
			return getServletContext().getRequestDispatcher("/securityCheck.jsp");
		}
		return getServletContext().getRequestDispatcher("/login.jsp");
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	private CCRegistrationInfo processRequestForEditProfile(
			HttpServletRequest request) throws ServletException, IOException {
		// User
		// related
		// details
		String mdn = request.getParameter("mdn");
		String firstName = request.getParameter("firstname");
		String lastName = request.getParameter("lastname");
		String securityQuestion = request.getParameter("securityQuestion");
		if (securityQuestion.equals("ownquestion")) {
			securityQuestion = request.getParameter("ownQuestion");
		}
		String securityAnswer = request.getParameter("securityAnswer");
		String homePhone = request.getParameter("homephone");
		String workPhone = request.getParameter("workphone");
		Long userId = Long.parseLong(request.getParameter("userId"));
		Integer userVersion = Integer.parseInt(request
				.getParameter("userVersion"));
		Long subscriberID = Long
				.parseLong(request.getParameter("subscriberId"));

		CCRegistrationInfo registrationInfo = new CCRegistrationInfo();

		registrationInfo.setEmail(UserService.getCurrentUser().getEmail());
		registrationInfo.setFirstName(firstName);
		registrationInfo.setLastName(lastName);
		registrationInfo.setMdn(MfinoUtil.normalizeMDN(mdn));
		registrationInfo.setSecurityAnswer(securityAnswer);
		registrationInfo.setSecurityQuestion(securityQuestion);
		registrationInfo.setHomePhone(homePhone);
		registrationInfo.setWorkPhone(workPhone);
		registrationInfo.setSubscriberid(subscriberID);
		registrationInfo.setUserVersion(userVersion);
		registrationInfo.setUserid(userId);

		List<CCInfo> ccInfoList = new ArrayList<CCInfo>();

		Integer numOfCards = Integer.parseInt(request.getParameter("numOfCards"));
		for (int i = 1; i <= numOfCards; i++) {
			String f6 = request.getParameter("f6_" + i);
			String l4 = request.getParameter("l4_" + i);
			String bankName = request.getParameter("bankName_" + i);
			String nameOnCard = request.getParameter("nameOnCard_" + i);

			// Address related details
			String Address = request.getParameter("billingAddress_" + i);
			String line2 = request.getParameter("AddressLine2_" + i);
			String city = request.getParameter("city_" + i);
			String state = request.getParameter("state_" + i);
			String region = request.getParameter("region_" + i);
			String zipCode = request.getParameter("zipCode_" + i);
			Long cardId = null;
			if (StringUtils.isNotEmpty(request.getParameter("cardId_" + i))) {
				cardId = Long.parseLong(request.getParameter("cardId_" + i));
			}
			Integer cardVersion = null;
			if (StringUtils.isNotEmpty(request.getParameter("cardVersion_" + i))) {
				cardVersion = Integer.parseInt(request.getParameter("cardVersion_" + i));
			}
			++i;
			String billingAddress = request.getParameter("billingAddress_" + i);
			String billingline2 = request.getParameter("AddressLine2_" + i);
			String billingcity = request.getParameter("city_" + i);
			String billingstate = request.getParameter("state_" + i);
			String billingregion = request.getParameter("region_" + i);
			String billingzipCode = request.getParameter("zipCode_" + i);
			i--;

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
				ccInfo.setCardId(cardId);
				ccInfo.setCardInfoVersion(cardVersion);
				ccInfoList.add(ccInfo);
			}
		}
		registrationInfo.setCcList(ccInfoList);

		List<CreditCardDestinations> ccDestList = new ArrayList<CreditCardDestinations>();
		Integer noOfDestinations = Integer.parseInt(request.getParameter("destinations"));
		Integer noOfOldDestinations = Integer.parseInt(request.getParameter("olddestinations"));
		Integer newDestinations = noOfDestinations - noOfOldDestinations;
		registrationInfo.setOldDestinations(noOfOldDestinations);
		registrationInfo.setNewDestinations(newDestinations);

		int newdest = 0;
		for (int i = 1; i <= noOfDestinations; i++) {
			String destination = request.getParameter("destination" + i);
			if (StringUtils.isNotBlank(destination)) {
				CreditCardDestinations ccDest = new CreditCardDestinations();
				ccDest.setDestMDN(destination);
				String destinationId = request.getParameter("destination" + i+ "ID");
				String destinationVersion = request.getParameter("destination"+ i + "Version");
				if (destinationId != null) {
					Long destID = Long.parseLong(destinationId);
					Integer destVersion = Integer.parseInt(destinationVersion);
					ccDest.setID(destID);
					ccDest.setVersion(destVersion);
				} else {
					newdest++;
				}
				ccDestList.add(ccDest);
			}
		}
		registrationInfo.setCcDestinations(ccDestList);
		registrationInfo.setNewDestinations(new Integer(newdest));

		return registrationInfo;
	}
}
