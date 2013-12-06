
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" %>

<html>
    <head>
        <title>Welcome to Smart Telecom Payments | Update Password</title>
        <script src="js/jquery-1.4.4.min.js"></script>
         <%--<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/validate/lib/jquery.delegate.js"></script>--%>
        <script type="text/javascript" src="js/jquery.validate.min.js"></script>
        <link rel="stylesheet" href="css/util.css" type="text/css"/>
        <script src="js/changePassword.js"></script>
    </head>
    <body oncontextmenu="return false;">
        <%@include file="header.jspf" %>
        <div class="body" align="center">
            <h3> Update Password</h3>

            <form id="changePassword" name="changePassword" action="UpdateProfileServlet" method="post" onSubmit="">
            <!-- Hidden input field to figure out which page is the request coming from -->
				<input type="hidden" name="requestType" value="changePassword"/>
                <table bgcolor="#003d79" border="0" cellpadding="2" cellspacing="0" width="1%">
                    <tbody><tr><td>
                                <table bgcolor="#ffffff" border="0" cellpadding="2" cellspacing="20" width="1%">
                                    <tbody><tr><td>
                                                <table bgcolor="#eeeeee" border="0" cellpadding="2" cellspacing="0" width="100%">
                                                    <tbody><tr><td align="center" bgcolor="#ffffff" valign="top">

                                                                <table bgcolor="#ffffff" border="0" cellpadding="5" cellspacing="0" width="100%">
                                                                    <tbody><tr>
                                                                            <td colspan="2" valign="top">
                                                                                <span class="gaia ops gsl">
                                                                                    Please provide details to change pin
                                                                                </span>  </td>
                                                                        </tr>
                                                                        <%
                                                                        if (!"1".equals(request.getAttribute("resultCode"))) {
                                                            if (request.getAttribute("response") == null || !"0".equals(request.getAttribute("response"))) {
                                                         %> 
                                                         <jsp:forward page="index.jsp"></jsp:forward> 
                                                       <%  }}
                                                    	if ("1".equals(request.getAttribute("resultCode"))) { %>
                                                        <tr><b><%=request.getAttribute("resultMsg")%></b></tr>
                                                   <% 	request.setAttribute("resultMsg","");
                                                        request.setAttribute("resultCode","");
                                                        }     %>
                                                                        <tr>
                                                                            <td nowrap="nowrap" valign="top">
                                                                                <font face="Arial, sans-serif" size="-1"><b>
                                                                                        Current Pin:
                                                                                    </b>  </font>  </td>
                                                                            <td>
                                                                                <div>
                                                                                    <input name="currentPasswd" id="currentPasswd" size="53" type="password">
                                                                                </div>  </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td nowrap="nowrap" valign="top">
                                                                                <font face="Arial, sans-serif" size="-1"><b>
                                                                                        New Pin:
                                                                                    </b>  </font>  </td>
                                                                            <td>
                                                                                <div>
                                                                                    <input name="newPasswd" id="newPasswd" size="53" type="password">
                                                                                </div>  </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td nowrap="nowrap" valign="top">
                                                                                <font face="Arial, sans-serif" size="-1"><b>
                                                                                        Confirm Pin:
                                                                                    </b>  </font>  </td>
                                                                            <td>
                                                                                <div>
                                                                                    <input name="confirmPasswd" id="confirmPasswd" size="53" type="password">
                                                                                </div>  </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td colspan="1">&nbsp;  </td>
                                                                            <td colspan="1" align="left">
                                                                                <input style="width: 6em;" id="submitbutton" name="submitbutton" value="Submit" type="SUBMIT">
                                                                                <input style="width: 6em;" id="cancelbutton" name="cancelbutton" value="Reset" type="reset">
                                                                            </td>
                                                                        </tr>
                                                                    </tbody></table>
                                                            </td></tr>
                                                    </tbody></table>
                                            </td>
                                        </tr>
                                    </tbody></table>
                    </tbody></table>
            </form>
        </div>
        <%@include file="footer.jspf" %>
    </body></html>