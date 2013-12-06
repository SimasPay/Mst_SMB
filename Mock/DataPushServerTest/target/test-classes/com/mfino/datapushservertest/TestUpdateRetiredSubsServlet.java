/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.datapushservertest;

import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.developer.WSBindingProvider;
import com.ztesoft.zsmart.bss.ws.PGWDataPushInterface;
import com.ztesoft.zsmart.bss.ws.PGWDataPushInterfaceService;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

/**
 *
 * @author admin
 */
public class TestUpdateRetiredSubsServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processGetRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestUpdateRetiredSubsServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TestUpdateRetiredSubsServlet at " + request.getContextPath () + "</h1>");
            out.println("<form id='testform' action='TestUpdateRetiredSubsServlet' method='post'>");
            out.println("<input type='text' id='subs' name='subs'>&nbsp&nbspMDN</input></br>");
            out.println("<input type='submit' id='submitBtn' value='submit'></input>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    } 
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processPostRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String val = request.getParameter("subs");
            QName qName = new QName("http://ws.bss.zsmart.ztesoft.com", "PGWDataPushInterfaceService");

            URL url = null;
            try {
                URL baseUrl;
                baseUrl = com.ztesoft.zsmart.bss.ws.PGWDataPushInterfaceService.class.getResource(".");
                url = new URL(baseUrl, "http://localhost:8084/DataPushServer/PGWDataPushInterfaceService?wsdl");
            } catch (MalformedURLException e) {
            }
            PGWDataPushInterfaceService service = new PGWDataPushInterfaceService(url, qName);
            PGWDataPushInterface port = service.getPGWDataPushInterface();

            WSBindingProvider bp = (WSBindingProvider)port ;
            bp.setOutboundHeaders(
              // simple string value as a header, like <simpleHeader>stringValue</simpleHeader>
              Headers.create(new QName("username"),"zsmart"),
              // create a header from JAXB object
              Headers.create(new QName("password"),"zsmart123")
            );
            
            out.print(port.updateRetiredSubs(val));
        }
        catch(Exception e)
        {
            out.print(e.toString());
        }finally {
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
        processGetRequest(request, response);
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
        processPostRequest(request, response);
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
