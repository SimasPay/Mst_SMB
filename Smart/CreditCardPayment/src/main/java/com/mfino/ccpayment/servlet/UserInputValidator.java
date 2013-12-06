/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.ccpayment.servlet;

import java.io.IOException;
import java.io.PrintWriter;

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

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.uicore.service.UserService;
import com.mfino.util.ConfigurationUtil;

/**
 *
 * @author admin
 */
public class UserInputValidator extends HttpServlet {

    private static Logger log = LoggerFactory.getLogger(UserInputValidator.class);

	protected static SessionFactory sessionFactory = null;

	protected static HibernateSessionHolder hibernateSessionHolder = null;

	static {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..\\spring-datasource-beans.xml");
		sessionFactory = appContext.getBean(SessionFactory.class);
		hibernateSessionHolder = appContext.getBean(HibernateSessionHolder.class);
	}

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();		
		String transMdn = request.getParameter("MDN");
		
		log.info("ValidateRegistrationServlet: trans MDN id is " + transMdn);
		
		boolean isValid = isTransMDNValid(transMdn);		
		response.setHeader("Content-Type", "application/json");
		out.println(String.valueOf(isValid));
	}

	private boolean isTransMDNValid(String transMdn) {
		
		if (StringUtils.isBlank(transMdn)) {
			log.info("MDN is blank");
			return false;
		}

		Session session = sessionFactory.openSession();
		hibernateSessionHolder.setSession(session);		
		DAOFactory.getInstance().setHibernateSessionHolder(hibernateSessionHolder);

		try {
			User user = UserService.getCurrentUser();
			SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			SubscriberMDN mdn = mdnDAO.getByMDN("62"+transMdn);
			
			if (null == mdn) {
				log.info("No MDN found -- 62" + transMdn);
				return false;
			}
		
			if (ConfigurationUtil.isInterCompanyCCPaymentAllowed())
				return true;
			
			if (null != user && mdn.getSubscriber().getCompany().getID().equals(user.getCompany().getID())) {
				return true;				
			}
		} catch (Exception e) {
			log.info("Error while validating transaction mdn", e);
		} finally {
			if(session!=null)
			{
				session.close();
			}
		}
		log.info("Company check failed");
		return false;
	}

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
