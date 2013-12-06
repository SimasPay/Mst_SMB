package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MfinoUtil;

/**
 * Servlet implementation class ForgotPasswordConfirmationCodeServlet
 */
public class ForgotPasswordConfirmationCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(this.getClass());
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
		try {
			HibernateUtil.getCurrentSession().beginTransaction();
			String userName = request.getParameter("MDN");
			userName = MfinoUtil.normalizeMDN(userName);
			String resetPasswordConfirmationCode = request.getParameter("confirmationCode");
			if (userName != null && resetPasswordConfirmationCode != null) {
				UserQuery query = new UserQuery();
				query.setUserName(userName);
				query.setStatus(CmFinoFIX.UserStatus_Active);
				UserDAO userDAO = new UserDAO();
				List<User> userResults = userDAO.get(query);
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
			
		} catch (Exception error) {
			log.error("Exception occured", error);
			request.setAttribute("resultCode", "2");
			request.setAttribute("resultMsg", "Exception");
			
		}finally{
			HibernateUtil.getCurrentTransaction().rollback();
			dispatcher.forward(request, response);
		}
	}

}
