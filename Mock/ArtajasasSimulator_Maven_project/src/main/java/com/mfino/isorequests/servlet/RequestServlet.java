/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.isorequests.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.Socket;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.isorequests.listener.util.Util;

/**
 * 
 * @author admin
 */
public class RequestServlet extends HttpServlet {
	private Logger log = LoggerFactory.getLogger(this.getClass());

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
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			// Main main = new Main();
			// StringWriter sw = new StringWriter();
			String bankType = request.getParameter("bankType");
			String action = request.getParameter("reqType");
			if (bankType.equals("-1") || action.equals("-1")) {
				return;
			}

			int reqType = Integer.parseInt(action);
			int bankTypeInt = Integer.parseInt(bankType);
			IsoMessage topupRequest = Util.mfact.newMessage(0x200);
			Socket socket = null;

			if (bankTypeInt == 0) {
				ArtajasaBankChannelProcessor artajasa = new ArtajasaBankChannelProcessor(
						null, Util.socket, Util.mfact, topupRequest);
				if (reqType == 200) {
					// socket = new Socket(InetAddress.getLocalHost(), 9999);
					BigDecimal amount = new BigDecimal(request
							.getParameter("4"));
					topupRequest.setValue(2, request.getParameter("2"),
							IsoType.LLVAR, 19);
					topupRequest.setValue(3, request.getParameter("3"),
							IsoType.NUMERIC, 6); // Deafult is Topup.
					topupRequest.setValue(4, amount, IsoType.AMOUNT, 12);
					topupRequest.setValue(7, request.getParameter("10"),
							IsoType.DATE10, 10);
					topupRequest.setValue(11, request.getParameter("11"),
							IsoType.NUMERIC, 6);
					topupRequest.setValue(12, request.getParameter("12"),
							IsoType.TIME, 6);
					topupRequest.setValue(13, request.getParameter("13"),
							IsoType.DATE4, 4);
					topupRequest.setValue(15, request.getParameter("15"),
							IsoType.DATE4, 4);
					topupRequest.setValue(18, request.getParameter("18"),
							IsoType.NUMERIC, 4);
					topupRequest.setValue(32, request.getParameter("32"),
							IsoType.LLVAR, 11);
					topupRequest.setValue(37, request.getParameter("37"),
							IsoType.ALPHA, 12);
					topupRequest.setValue(42, request.getParameter("42"),
							IsoType.NUMERIC, 15);
					topupRequest.setValue(48, request.getParameter("48"),
							IsoType.LLLVAR, 120);
					topupRequest.setValue(49, request.getParameter("49"),
							IsoType.NUMERIC, 3);
					topupRequest.setValue(63, request.getParameter("63"),
							IsoType.LLLVAR, 3);
					artajasa.sendTopupPaymentRequest(Util.socket, topupRequest);
				} else {
					artajasa.sendTopupPaymentReversalRequest();
				}

			} else if (bankTypeInt == 1) {
				Mobile8BankChannelProcessor mobile8 = new Mobile8BankChannelProcessor(
						null, Util.mobile8Socket, Util.mfact, topupRequest);
				if (reqType == 200) {
					socket = new Socket(InetAddress.getLocalHost(), 9993);
					// topupRequest.setValue(3, request.getParameter("3"),
					// IsoType.NUMERIC, 6); // Deafult is Topup.
					// topupRequest.setValue(4, request.getParameter("4"),
					// IsoType.AMOUNT, 12);
					// topupRequest.setValue(11, request.getParameter("11"),
					// IsoType.NUMERIC, 6);
					// topupRequest.setValue(37, request.getParameter("37"),
					// IsoType.ALPHA, 12);
					// topupRequest.setValue(61, request.getParameter("61"),
					// IsoType.LLLVAR, 16);
					// topupRequest.setValue(98, request.getParameter("98"),
					// IsoType.ALPHA, 25); // Default is Topup
					BigDecimal amount = new BigDecimal(request
							.getParameter("4"));
					topupRequest.setValue(2, request.getParameter("2"),
							IsoType.LLVAR, 16);
					topupRequest.setValue(3, request.getParameter("3"),
							IsoType.NUMERIC, 6);
					topupRequest.setValue(4, amount, IsoType.NUMERIC, 12);
					topupRequest.setValue(7, request.getParameter("7"),
							IsoType.DATE10, 10);
					topupRequest.setValue(11, request.getParameter("11"),
							IsoType.NUMERIC, 6);
					topupRequest.setValue(12, request.getParameter("12"),
							IsoType.TIME, 6);
					topupRequest.setValue(13, request.getParameter("13"),
							IsoType.DATE4, 4);
					topupRequest.setValue(15, request.getParameter("15"),
							IsoType.DATE4, 4);
					topupRequest.setValue(18, request.getParameter("18"),
							IsoType.NUMERIC, 4);
					topupRequest.setValue(32, request.getParameter("32"),
							IsoType.LLVAR, 11);
					topupRequest.setValue(37, request.getParameter("37"),
							IsoType.ALPHA, 12);
					topupRequest.setValue(41, request.getParameter("41"),
							IsoType.ALPHA, 16);
					topupRequest.setValue(48, request.getParameter("48"),
							IsoType.LLLVAR, 120);
					topupRequest.setValue(49, request.getParameter("49"),
							IsoType.NUMERIC, 3);
					topupRequest.setValue(63, request.getParameter("63"),
							IsoType.LLLVAR, 3);
					mobile8.sendTopUpPaymentRequest(Util.mobile8Socket,
							topupRequest);
				} else {
					mobile8.sendTopUpPaymentReversalRequest();
				}

			} else if (bankTypeInt == 2) {
				XLinkBankChannelProcessor xlink = new XLinkBankChannelProcessor(
						null, Util.xlinkSocket, Util.mfact, topupRequest);
				if (reqType == 200) {
					socket = new Socket(InetAddress.getLocalHost(), 9990);
					// topupRequest.setValue(3, request.getParameter("3"),
					// IsoType.NUMERIC, 6); // Deafult is Topup.
					// topupRequest.setValue(4, request.getParameter("4"),
					// IsoType.AMOUNT, 12);
					// topupRequest.setValue(11, request.getParameter("11"),
					// IsoType.NUMERIC, 6);
					// topupRequest.setValue(37, request.getParameter("37"),
					// IsoType.ALPHA, 12);
					// topupRequest.setValue(61, request.getParameter("61"),
					// IsoType.LLLVAR, 16);
					// topupRequest.setValue(98, request.getParameter("98"),
					// IsoType.ALPHA, 25); // Default is Topup
					String MDN = request.getParameter("61");
					BigDecimal amount = new BigDecimal(request
							.getParameter("4"));
					topupRequest.setValue(3, request.getParameter("3"),
							IsoType.NUMERIC, 6); // Deafult is Topup.
					topupRequest.setValue(4, amount, IsoType.AMOUNT, 12);
					topupRequest.setValue(11, request.getParameter("11"),
							IsoType.NUMERIC, 6);
					topupRequest.setValue(37, request.getParameter("37"),
							IsoType.ALPHA, 12);
					topupRequest.setValue(61, StringUtils.rightPad(MDN, 16),
							IsoType.LLLVAR, 16);
					topupRequest.setValue(98, StringUtils
							.rightPad("019003", 25), IsoType.ALPHA, 25); // Default Topup
					topupRequest.setValue(40, request.getParameter("40"),
							IsoType.ALPHA, 3);
					xlink.sendTopupPaymentRequest(Util.xlinkSocket,
							topupRequest);
				} else {
					xlink.sendTopupPaymentReversalRequest();
				}

			}

			// XMLOutputFactory factory = XMLOutputFactory.newInstance();
			// XMLStreamWriter writer = factory.createXMLStreamWriter(sw);
			// writer.writeStartDocument();
			// if (bankType.equals("0")) {
			// writer.writeStartElement("Artajasa");
			// } else if (bankType.equals("1")) {
			// writer.writeStartElement("Xlink");
			// } else {
			// writer.writeStartElement("Mobile-8");
			// }
			// for (int i = 0; i < Util.topupRequestParams.length; ++i) {
			// if (request.getParameter(Util.topupRequestParams[i]) == null)
			// continue;
			// if ((!action.equalsIgnoreCase("400"))
			// && (request.getParameter(Util.topupRequestParams[i])
			// .equalsIgnoreCase("90"))
			// && (request.getParameter(Util.topupRequestParams[i])
			// .equalsIgnoreCase("39"))) {
			// } else {
			// if (request.getParameter(Util.topupRequestParams[i])
			// .equals(Util.topupRequestParams[i]))
			// continue;
			// writer.writeStartElement("bit");
			// writer.writeAttribute("pos", Util.topupRequestParams[i]);
			// writer.writeCharacters(request
			// .getParameter(Util.topupRequestParams[i]));
			// writer.writeEndElement();
			// }
			// }
			//
			// writer.writeEndElement();
			// writer.writeEndDocument();
			// System.out.println(sw.toString());

			out.print("Your Request Submitted");
		} catch (Exception e) {			
			log.error("error in servlet", e);
		} finally {
			out.close();
		}
	}

	// <editor-fold defaultstate="collapsed"
	// desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
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
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
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
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 * 
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
