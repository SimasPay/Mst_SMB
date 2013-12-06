package com.mfino.ccpayment.servlet;

import java.io.IOException;
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
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.session.HibernateSessionHolder;

/**
 * Servlet implementation class ForgotPasswordUpdationServlet
 */
public class ForgotPasswordUpdationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ForgotPasswordUpdationServlet.class);    
   
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
    public ForgotPasswordUpdationServlet() {
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
		String requestType = request.getParameter("requestType");
		String newPassword = request.getParameter("newPassword");
    	String confirmPassword = request.getParameter("confirmPassword");
    	RequestDispatcher dispatcher=getServletContext().getRequestDispatcher("/login.htm");
		if (StringUtils.isNotBlank(requestType) && StringUtils.isNotBlank(newPassword) && StringUtils.isNotBlank(confirmPassword)) {
			if ("forgotPasswordUpdation".equals(requestType)) {
				dispatcher = getServletContext().getRequestDispatcher("/login.htm");

				Session session = sessionFactory.openSession();
				hibernateSessionHolder.setSession(session);		
				DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);

				try {
					if (!newPassword.equals(confirmPassword)) {
						log.info("New password and confirm password doesnot match");
						request.setAttribute("resultCode", "1");
						request.setAttribute("mdn", request.getParameter("mdn"));						
						request.setAttribute("resultMsg","New password and confirm password doesnot match.Try again");
						dispatcher = getServletContext().getRequestDispatcher("/forgotPasswordUpdation.jsp");
					} else {
						dispatcher = processRequest(request, newPassword);
					}
					dispatcher.forward(request, response);
				}finally{
					if(session!=null)
					{
						session.close();
					}
				}
			}

		}
		else{
			dispatcher = getServletContext().getRequestDispatcher("/forgotPasswordUpdation.jsp");
			log.info("Invalid Request because any one of these is/are requestType, newPassword or confirmPassword is null");
			request.setAttribute("resultCode", "-1");
			request.setAttribute("resultMsg", "Invalid Request");
			request.setAttribute("mdn", request.getParameter("mdn"));
			dispatcher.forward(request, response);
		}
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	private RequestDispatcher processRequest(HttpServletRequest request,
			String newPassword) {
		RequestDispatcher dispatcher;
		UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
		UserQuery userQuery = new UserQuery();
		String username = request.getParameter("mdn");
		userQuery.setUserName(username);
		userQuery.setStatus(CmFinoFIX.UserStatus_Active);
		List<User> results = userDAO.get(userQuery);
		if (results.size() == 1) {
			User user = results.get(0);
			PasswordEncoder encoder = new ShaPasswordEncoder(1);
			String encPassword = encoder.encodePassword(newPassword, user.getUsername());
			user.setPassword(encPassword);
		    user.setForgotPasswordCode(null);
			userDAO.save(user);
			request.setAttribute("resultCode", "0");
			request.setAttribute("resultMsg","Congratulations! You have been successfully updated the password");
			dispatcher = getServletContext().getRequestDispatcher("/forgotPasswordSucess.jsp");
		} else {
			log.info("Invalid username" + username);
			request.setAttribute("resultCode", "1");
			request.setAttribute("resultMsg","Invalid Email, Try again");
			request.setAttribute("mdn", request.getParameter("mdn"));							
			dispatcher = getServletContext().getRequestDispatcher("/forgotPassword.jsp");
		}
		return dispatcher;
	}

}
