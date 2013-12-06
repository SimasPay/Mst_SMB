/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.isorequests.servlet;

import com.mfino.isorequests.listener.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import test.Main;
import test.IsoArtaClient;
import com.mfino.util.logging.ILogger;
import com.mfino.util.logging.LogFactory;

/**
 *
 * @author admin
 */
public class RequestServlet extends HttpServlet {
    private Logger log = LoggerFactory.getLogger(this.getClass());tClass());
   
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
                       
            Main main = new Main();
            StringWriter sw = new StringWriter();
            String action = request.getParameter("reqType");
            int reqTyep = Integer.parseInt(action);
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(sw);
            writer.writeStartDocument();
            writer.writeStartElement("iso8583");

            for (int i =0 ; i < Util.topupRequestParams.length; ++i) {
                if((!action.equalsIgnoreCase("400"))&&(request.getParameter(Util.topupRequestParams[i]).equalsIgnoreCase("90")))
                {
                }
                else
                {
                    writer.writeStartElement("bit");
                    writer.writeAttribute("pos", Util.topupRequestParams[i]);
                    writer.writeCharacters(request.getParameter(Util.topupRequestParams[i]));
                    writer.writeEndElement();
                }
            }

            writer.writeEndElement();
            writer.writeEndDocument();
            System.out.println(sw.toString().substring(21));
            main.executeFile(Util.client, sw.toString(), reqTyep);
            out.print("Your Request Submitted");
        }catch (Exception e){
            e.printStackTrace();
            log.error("error in servlet", e);
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
        return "Short description";
    }// </editor-fold>

}
