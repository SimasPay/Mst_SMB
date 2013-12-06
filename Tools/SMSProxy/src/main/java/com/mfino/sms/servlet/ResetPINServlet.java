package com.mfino.sms.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;

import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.SubscriberMDN;
import com.mfino.util.HibernateUtil;

/**
 * Servlet implementation class ResetPINServlet
 */
public class ResetPINServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResetPINServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String mdn = request.getParameter("mdn");
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("<h1>");
		if(mdn == null) {
			out.println("Please enter a valid pin and try again");
		}
		HibernateUtil.getCurrentTransaction().begin();
		SubscriberMDNDAO subscriberMDNDAO = new SubscriberMDNDAO();
		SubscriberMDN subscriberMDN = subscriberMDNDAO.getByMDN(mdn);
		if (subscriberMDN == null) {
			out.println("Subscriber MDN is not available in DB");
		}
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update("123456".getBytes());
			md.update(subscriberMDN.getMDN().getBytes()); 
			byte[] bytes = md.digest();
			char[] encodeHex = Hex.encodeHex(bytes);
			String calcPIN = new String(encodeHex);
			calcPIN = calcPIN.toUpperCase();
			subscriberMDN.setDigestedPIN(calcPIN);
			subscriberMDN.setMerchantDigestedPIN(calcPIN);
			subscriberMDN.setWrongPINCount(0);
			subscriberMDNDAO.save(subscriberMDN);
		} catch (Exception e) {
			HibernateUtil.getCurrentTransaction().rollback();
			out.println(e.getMessage());
		}
		out.println("Successfully updated the PIN");
		HibernateUtil.getCurrentTransaction().commit();
		out.println("</h1>");
		out.println("</body>");
		out.println("</html>");
	}

}
