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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.service.UserService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author admin
 */
public class UserInputValidator extends HttpServlet {

    private Logger log = LoggerFactory.getLogger(this.getClass());
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
		
		try {
			HibernateUtil.getCurrentSession().beginTransaction();
			User user = UserService.getCurrentUser();
			SubscriberMDNDAO mdnDAO = new SubscriberMDNDAO();
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
			log.error("Error while validating transaction mdn", e);
		} finally {
			HibernateUtil.getCurrentSession().getTransaction().rollback();
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
