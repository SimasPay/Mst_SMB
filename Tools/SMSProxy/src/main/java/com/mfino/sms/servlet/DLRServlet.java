package com.mfino.sms.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class DLRServlet
 */
public class DLRServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	private Logger log = LoggerFactory.getLogger(getClass());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DLRServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("Got DLR Request ");
		Map parameterMap = request.getParameterMap();
		for (Entry entry : (Set<Entry>)parameterMap.entrySet()) {
			log.info(entry.getKey() + "   " + request.getParameter((String)entry.getKey()));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
