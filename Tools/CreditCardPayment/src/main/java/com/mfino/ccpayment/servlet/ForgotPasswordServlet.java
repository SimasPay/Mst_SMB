package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MailUtil;

/**
 * Servlet implementation class ResetPasswordServlet
 */
public class ForgotPasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int MAX_WRONG_COUNT = 5;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ForgotPasswordServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	private void sendEmailToUser(User user,String email){
		//send email to the user           
	    log.info("Sending Email to the user" + email);
            String codeBody = StringUtils.replace(ConfigurationUtil.getForgotPasswordCCSubscriberCodeBody(), "$(confirmationURL)", ConfigurationUtil.getCCPaymentDeploymentURL() + "/forgotPasswordConfirmationCode.jsp");
            codeBody = StringUtils.replace(codeBody, "$(autoConfirmationURL)", ConfigurationUtil.getCCPaymentDeploymentURL() + "/forgotPasswordConfirmationCode.jsp");
            codeBody = StringUtils.replace(codeBody, "$(ForgotPasswordconfirmationCode)", user.getForgotPasswordCode());
            codeBody = StringUtils.replace(codeBody, "$(userName)", user.getUsername());
            String emailMsg = codeBody;
            String emailSubject = ConfigurationUtil.getForgotPasswordCCSubscriberSubject();
            try {
                log.info("Info: Sending mail");
                MailUtil.sendMail(email, user.getFirstName() + " " + user.getLastName(), emailSubject,
                        emailMsg);
            } catch (Exception ee) {
                log.error("Failed to send User", ee);
            }
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		log.info("Forgot password Servlet");
		String mdn = request.getParameter("mdn");
		String secAnswer= request.getParameter("Answer");
		String requestType = request.getParameter("requestType");
		RequestDispatcher dispatcher= getServletContext().getRequestDispatcher("/forgotPassword.jsp");
		if(mdn!=null && secAnswer!=null&& "forgotPasswordSecurtiyCheck".equals(requestType)){
			try {
				HibernateUtil.getCurrentSession().beginTransaction();
				UserDAO userDAO = new UserDAO();
		    	        UserQuery userQuery = new UserQuery();
                                userQuery.setUserName(mdn);
                                userQuery.setStatus(CmFinoFIX.UserStatus_Active);
                                List<User> results = userDAO.get(userQuery);
                                User user;
		    	if(requestType.equals("forgotPasswordSecurtiyCheck")){
			    	dispatcher = getServletContext().getRequestDispatcher("/forgotPasswordSecurityCheck.jsp");
			    	}
		    	if(results.size()==0){
		    		log.info("Invalid mdn"+ mdn);
		    		request.setAttribute("resultCode", "-1");
				request.setAttribute("resultMsg", "This mdn is not registered. Please check the mdn and try again");
		    		dispatcher.forward(request, response);
		    		HibernateUtil.getCurrentTransaction().rollback();					
		    		return;
		    	}
		        user = results.get(0);
		    	if(user.getSecurityAnswer().equals(secAnswer) && (user.getRestrictions()==null || user.getRestrictions()==0))
		    	{
		    		log.info("Valid security answer, sending an email to the user");
		    		request.setAttribute("resultCode", "0");
		    		request.setAttribute("resultMsg", "Valid security answer");
				sendEmailToUser(user,user.getEmail());
				user.setFailedLoginCount(0); //update the security answer.
				userDAO.save(user);	
		    		dispatcher = getServletContext().getRequestDispatcher("/forgotPasswordConfirmation.jsp");
		    	}
		    	else{
		    		if(!(user.getRestrictions()==null || user.getRestrictions()==0)){
		    			log.info("User account is restricited"+user.getRestrictions());
		    			request.setAttribute("resultCode", "2");
		    			request.setAttribute("resultMsg", "User account locked <p>Please contact Administrator at <br>" +
                                            				"registration.service@smart-telecom.co.id <br>for further instructions.");   			
		    		}
                                else if (user.getFailedLoginCount()>MAX_WRONG_COUNT){
                                        log.info("Exceded the maximum number of failed logins" + user.getFailedLoginCount());
                                        log.info("Locking the user");
                                        request.setAttribute("resultCode", "2");
                                        request.setAttribute("resultMsg", "User account locked <p>Please contact Administrator at <br>" +
                                                "registration.service@smart-telecom.co.id <br>for further instructions.");
                                        dispatcher = getServletContext().getRequestDispatcher("/forgotPasswordSecurityCheck.jsp");
                                        user.setRestrictions(CmFinoFIX.SubscriberRestrictions_SecurityLocked);
                                        user.setFailedLoginCount(0);
                                        userDAO.save(user);
                                }
		    		else{
		    		    user.setFailedLoginCount(user.getFailedLoginCount() + 1);
                                    log.info("invalid security answer" + secAnswer);
                                    request.setAttribute("resultCode", "-1");
                                    request.setAttribute("mdn", user.getUsername());
                                    request.setAttribute("secQuestion", user.getSecurityQuestion());
                                    request.setAttribute("resultMsg", "Invalid security answer, try again");
                                    if (user.getFailedLoginCount() > MAX_WRONG_COUNT) {
                                        log.info("Exceded the maximum number of failed logins" + user.getFailedLoginCount());
                                        log.info("Locking the user");
                                        request.setAttribute("resultCode", "2");
                                        request.setAttribute("resultMsg", "User account locked <p>Please contact Administrator at <br>" +
                                                "registration.service@smart-telecom.co.id <br>for further instructions.");
                                        dispatcher = getServletContext().getRequestDispatcher("/forgotPasswordSecurityCheck.jsp");
                                        user.setRestrictions(CmFinoFIX.SubscriberRestrictions_SecurityLocked);
                                        user.setFailedLoginCount(0);
                                    }
                                    userDAO.save(user);
		    	 }
		    	}		    	
		    	HibernateUtil.getCurrentSession().getTransaction().commit();
		    	dispatcher.forward(request, response);		    	
			} catch (HibernateException hiExcep) {
				log.error("Exception",hiExcep);
				HibernateUtil.getCurrentSession().getTransaction().rollback();				
			}
		}
		else{
			log.info("mdn"+ mdn +"RequestType:"+ requestType + "secAnswer:"+ secAnswer);
			log.info("mdn, security Answer or requestType is null");
			request.setAttribute("errorCode", "-1");
			request.setAttribute("errortMsg", "Invalid Email, Please check the email and try again");
			dispatcher.forward(request, response);	
    	}
	}

}