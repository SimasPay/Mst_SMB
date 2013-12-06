package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MfinoUtil;

/**
 * Servlet implementation class ValidateForgotPassword
 */
public class ValidateForgotPassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(this.getClass());   
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ValidateForgotPassword() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		log.info("Validate forgot password request");
		String mdn = request.getParameter("mdn");
	    mdn = MfinoUtil.normalizeMDN(mdn);
		String requestType = request.getParameter("requestType");
		request.setAttribute("mdn", mdn);
		RequestDispatcher dispatcher=getServletContext().getRequestDispatcher("/login.jsp");
		if(mdn!=null && "forgotpassword".equals(requestType)){
			dispatcher = getServletContext().getRequestDispatcher("/forgotPassword.jsp");
			try {
		            HibernateUtil.getCurrentSession().beginTransaction();
		            		log.info("MDN or Username is" + mdn);
                            UserDAO userDAO = new UserDAO();
                            UserQuery userQuery = new UserQuery();
                            userQuery.setUserName(mdn);
                            userQuery.setStatus(CmFinoFIX.UserStatus_Active);
                            List<User> results = userDAO.get(userQuery);
                            if (results.size() == 1) {
                                User user = results.get(0);
                                log.info("User exists with the username" + mdn);
                                String forgotPasswordCode = UUID.nameUUIDFromBytes((user.getUsername() + new Date().toString()).getBytes()).toString();
                                user.setForgotPasswordCode(forgotPasswordCode);
                                userDAO.save(user);
                                request.setAttribute("resultCode", "0");
                                request.setAttribute("resultMsg", user.getSecurityQuestion());
                                dispatcher = getServletContext().getRequestDispatcher("/forgotPasswordSecurityCheck.jsp");
                            } else {
                                log.info("User doesnot exists with the username" + mdn);
                                request.setAttribute("resultCode", "1");
                                request.setAttribute("resultMsg", "MDN not registered.");
                            }
                            
                            HibernateUtil.getCurrentSession().getTransaction().commit();
			} catch (HibernateException hiExcep) {
				log.error("Exception occured",hiExcep);
				request.setAttribute("resultCode", "2");
                request.setAttribute("resultMsg", "Exception");
				HibernateUtil.getCurrentSession().getTransaction().rollback();				
			}
			dispatcher.forward(request, response);
		} else {
			log.info("Invalid Request either username is null");
			request.setAttribute("errorCode", "-1");
			request.setAttribute("errorMsg", "Invalid Request");
			dispatcher.forward(request, response);	
		}
		
		}
	

}
