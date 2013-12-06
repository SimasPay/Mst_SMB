/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.mb.web;

import com.mfino.mb.Stocks;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Mfino
 */
public class StocksProcessor extends HttpServlet {

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
        try {
            String symbol = request.getParameter("symbol");
            String qtype = request.getParameter("queryType");

            log("Stock Symbol - " + symbol + " - " + qtype);
            String quoteContent = "";

            if (qtype != null) {
                if (qtype.equalsIgnoreCase("value")) {
                    log("Quote Value");
                    quoteContent = Stocks.getQuoteValue(symbol);
                } else {
                    quoteContent = Stocks.getQuoteDetails(symbol, qtype);
                }
            } else {
                log("Quote Summary");
                quoteContent = Stocks.getQuote(symbol);
            }

            log("Quote Data - " + quoteContent);
            out.println(quoteContent);
        } finally {
            out.close();
        }
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
        return "mFino Stocks Processor Web Interface";
    }// </editor-fold>
}
