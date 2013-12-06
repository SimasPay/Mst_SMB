package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.util.MfinoUtil;

/**
 * Servlet implementation class ForgotPasswordConfirmationCodeServlet
 */
public class ForgotPasswordConfirmationCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ForgotPasswordConfirmationCodeServlet.class);
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
	public ForgotPasswordConfirmationCodeServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/forgotPasswordConfirmationCode.jsp");

		Session session = sessionFactory.openSession();
		hibernateSessionHolder.setSession(session);		
		DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);
		try {
			String userName = request.getParameter("MDN");
			userName = MfinoUtil.normalizeMDN(userName);
			String resetPasswordConfirmationCode = request.getParameter("confirmationCode");
			if (userName != null && resetPasswordConfirmationCode != null) {
				List<User> userResults = getUser(userName);
				if (userResults.size() == 1) {
					User user = userResults.get(0);
					if (resetPasswordConfirmationCode.equals(user.getForgotPasswordCode())) {
						request.setAttribute("resultCode", "0");
						request.setAttribute("mdn", user.getUsername());
						dispatcher = getServletContext().getRequestDispatcher("/forgotPasswordUpdation.jsp");
					} else {
						log.info("Invalid forgot password confirmation code");
						request.setAttribute("resultCode", "1");
						request.setAttribute("mdn", user.getUsername());						
						request.setAttribute("resultMsg","Invalid forgot password confirmation code");
					}
				} else {
					log.info("username(mdn) doesnot exist in DB"+ userName);
					request.setAttribute("resultCode", "1");
					request.setAttribute("resultMsg","Invalid mdn try again");
					}
			} else {
				log.info("Invalid Request either username or confirmationcode is null");
				request.setAttribute("resultCode", "-1");
				request.setAttribute("resultMsg", "Invalid Request");
			}
			
		} catch (Exception e) {
			log.info("Exception occured" + e);
			request.setAttribute("resultCode", "2");
			request.setAttribute("resultMsg", "Exception");
			
		}finally{
			if(session!=null)
			{
				session.close();
			}
			dispatcher.forward(request, response);
		}
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	private List<User> getUser(String userName) {
		UserQuery query = new UserQuery();
		query.setUserName(userName);
		query.setStatus(CmFinoFIX.UserStatus_Active);
		UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
		List<User> userResults = userDAO.get(query);
		return userResults;
	}

}
