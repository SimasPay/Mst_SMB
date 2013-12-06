<%-- 
    Document   : index
    Created on : Aug 6, 2010, 5:22:08 PM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.mfino.isorequests.listener.util.*" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
            <form action="./RequestServlet">
        <center>
            <h2>Request</h2>
                <select  id="reqType" name="reqType">
                    <option id="reqType" name="reqType" value="200" selected="selected">Topup</option>
                    <option id="reqType" name="reqType" value="200">Payment</option>
                    <option id="reqType" name="reqType" value="200">Inquiry</option>
                    <option id="reqType" name="reqType" value="400">Payment Reversal</option>
                    <option id="reqType" name="reqType" value="400">Topup Reversal</option>
                </select>
                <table>
                    <% for (int i=0; i< Util.topupRequestParams.length;++i)
                        {
                    %>
                    <tr>
                        <td> <%= Util.topupRequestParams[i] %></td>
                        <td>
                            <input type="text" name="<%= Util.topupRequestParams[i] %>" value="<%= Util.topupRequestDefaultvalues[i] %>"/>
                        </td>
                    </tr>
                    <% }
                    %>
                    <tr>
                        <td colspan="2" align="center">
                            <input type="submit" value="Submit" />
                        </td>
                    </tr>
                </table>
        <br><br><br><br>
        <h3>Help</h3>
        </center>

            <ul>
                <li> You can change the properties related to multix (ipaddress, port number, timeout) in WEB-INF in multix.properties file.<br><br></li>
            <li>
                You can view logs in <a href="./logs">log directory</a>.<br><br>
                </li><li>
                Submit the request by supplying above parameters. You would be redirected to plane page, because you can do back button and resubmit the request as many times as possible.<br><br>
                </li><li>
                It's boring to submit the request in textboxes. Don't worry, copy the URL as mentioned below and keep pinging URLs by changing query string parameters.<br>
                http://site/RequestServlet?reqType=200&2=00000&3=180000&4=5000&18=6011&32=881&37=1085B1910200&42=000000000000000&48=1101088911111111&49=360&63=110&90=6756753agh1237899<br>
                For inquiry, topup, payment reqType is 200. For reversals reqType is 400.<br><br>
                </li><li>
                    <u>Note:</u> 90 would be accepted only for reversal requests, for rest it would be ignored.<br><br>
                </li><li>
                    <u>Disclaimer:</u> This tool is not extensively tested.<br><br>
                </li><li>One more known fact is, this help is boring :)</li>
            </ul>
            </form>
    </body>
</html>
