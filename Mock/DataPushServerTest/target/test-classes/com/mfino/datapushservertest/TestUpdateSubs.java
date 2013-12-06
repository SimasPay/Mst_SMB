/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.datapushservertest;

import com.ztesoft.zsmart.bss.ws.PGWDataPushInterface;
import com.ztesoft.zsmart.bss.ws.PGWDataPushInterfaceService;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.developer.WSBindingProvider;

/**
 *
 * @author admin
 */
public class TestUpdateSubs extends HttpServlet {

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
            out.println("<title>Servlet TestUpdateSubs</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TestUpdateSubs at " + request.getContextPath () + "</h1>");
            out.println("<form id='testform' action='TestUpdateSubs' method='post'>");
            out.println("<input type='text' id='subs' name='subs'>&nbsp&nbspMDN</input></br>");
            out.println("<input type='text' id='IMSI' name='IMSI'>&nbsp&nbspIMSI</input></br>");
            out.println("<input type='text' id='MarketingCatg' name='MarketingCatg'>&nbsp&nbspMarketingCatg</input></br>");
            out.println("<input type='text' id='Product' name='Product'>&nbsp&nbspProduct</input></br>");
            out.println("<input type='text' id='FirstName' name='FirstName'>&nbsp&nbspFirst Name</input></br>");
            out.println("<input type='text' id='LastName' name='LastName'>&nbsp&nbspLast Name</input></br>");
            out.println("<input type='text' id='Email' name='Email'>&nbsp&nbspEmail</input></br>");
            out.println("<input type='text' id='Language' name='Language'>&nbsp&nbspLanguage</input></br>");
            out.println("<input type='text' id='Currency' name='Currency'>&nbsp&nbspCurrency</input></br>");
            out.println("<input type='text' id='PaidFlag' name='PaidFlag'>&nbsp&nbspPaid Flag</input></br>");
            out.println("<input type='text' id='IDType' name='IDType'>&nbsp&nbspID Type</input></br>");
            out.println("<input type='text' id='IDNumber' name='IDNumber'>&nbsp&nbspID Number</input></br>");
            out.println("<input type='text' id='Gender' name='Gender'>&nbsp&nbspGender</input></br>");
            out.println("<input type='text' id='Address' name='Address'>&nbsp&nbspAddress</input></br>");
            out.println("<input type='text' id='City' name='City'>&nbsp&nbspCity</input></br>");
            out.println("<input type='text' id='BirthPlace' name='BirthPlace'>&nbsp&nbspBirth Place</input></br>");
            out.println("<input type='text' id='BirthDate' name='BirthDate'>&nbsp&nbspBirth Date</input></br>");
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
            String IMSI = request.getParameter("IMSI");
            String MarketingCatg = request.getParameter("MarketingCatg");
            String Product = request.getParameter("Product");
            String FirstName = request.getParameter("FirstName");
            String LastName = request.getParameter("LastName");
            String Email = request.getParameter("Email");
            String Language = request.getParameter("Language");
            String Currency = request.getParameter("Currency");
            String PaidFlag = request.getParameter("PaidFlag");
            String IDType = request.getParameter("IDType");
            String IDNumber = request.getParameter("IDNumber");
            String Gender = request.getParameter("Gender");
            String Address = request.getParameter("Address");
            String City = request.getParameter("City");
            String BirthPlace = request.getParameter("BirthPlace");
            String BirthDate = request.getParameter("BirthDate");
            XMLGregorianCalendar bDate = null;
            if(BirthDate != null && BirthDate.length()>0)
            {
                try
                {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                Date birthDate = df.parse(BirthDate);
                bDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(birthDate.getYear(), birthDate.getMonth(), birthDate.getDay(), 5);
                }
                catch(Exception e)
                {}
            
            }
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
            
            out.print(port.updateSubs(val,IMSI,MarketingCatg,Product,FirstName,
                    LastName, Email, Language, Currency, PaidFlag, IDType,
                    IDNumber,Gender, Address, City, BirthPlace, bDate));
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
