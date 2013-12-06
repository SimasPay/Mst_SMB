<%@page import="com.mfino.ccpayment.util.RegistrationUtil"%>
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"--%>
<%@page import="com.mfino.fix.CmFinoFIX.CMJSError"%>
<%@page import="javax.servlet.RequestDispatcher"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" %>
<html>
    <head>
        <script src="js/jquery-1.4.4.min.js"></script>
         <%--<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/validate/lib/jquery.delegate.js"></script>--%>
        <script type="text/javascript" src="js/jquery.validate.min.js"></script>
        <link rel="stylesheet" href="css/util.css" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
        <script  src="js/util.js"></script>
        <script src="js/hyperlinks.js"></script>
        <script src="js/securityCheck.js"></script>
        <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<!-- <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" /> -->
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
<title>Selamat Datang di Website Pembayaran Smartfren | Cek keamanan</title>
        <link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
        <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
    </head>
    <body oncontextmenu="return false;">
     <br/><br/>
        <div class="body" align="center">
            <%@include file="header.jspf" %>
            <%
                      CMJSError secQustionMsg = RegistrationUtil.getSecurityQuestion();
                      String secQuestion = "";
                      String errorMsg = "";
                      String submitbuttonstyle = "";
                      String disabled="";
                      if (secQustionMsg.getErrorCode() == 0) {
                          submitbuttonstyle = "";
                          secQuestion = secQustionMsg.getErrorDescription();
                      } else {
                          submitbuttonstyle = "disabled='true'";
                          errorMsg = secQustionMsg.getErrorDescription();
                          disabled="disabled='true'";
                      }
                      String answer = "";
                      String postPage = (String) request.getParameter("toPage");
                      if("editEmail".equals(postPage)) { %>
                            <h2><font color="#666666">Pertanyaan rahasia untuk Ganti Email</h2>
<h4><em>(Security Question for Edit Email)</em></font></font></h4>
                   <%   }else if("changePassword".equals(postPage)) {   %>
                        <h2><font color="#666666">Pertanyaan rahasia untuk Ganti PIN</h2>
<h4><em>(Security Question for Change PIN)</em></font></font></h4>
           <%} else if("editProfile".equals(postPage)) { %>
            <h2><font color="#666666">Pertanyaan rahasia untuk Ganti Profile</h2>
<h4><em>(Security Question for Edit Profile)</em></font></font></h4>
           <%}%>
           <%if(secQustionMsg.getErrorCode() == 0){ %>
           <br>
            <form id="securityCheckChangePassword" name="securityCheckChangePassword" action="UpdateProfileServlet" method="post" onSubmit="return $('#securityCheckChangePassword').valid()">
            <!-- Hidden input field to figure out which page is the request coming from -->
				<input type="hidden" name="requestType" value="securityCheck"/>
                <table align="center" width="445" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(./images/m06.png) bottom no-repeat;">
  <tr><td id="title_order" colspan="2" valign="top" align="left">
					&nbsp;Lengkapi informasi dibawah ini. 	<BR>
      <span class="gaia cca al"><b><font color="#999999" size="-2"><em><font color="#666666" size="2">&nbsp;(Please provide the details)</font></em></font></b></span></td>
			   </tr>
       			                                                                           <%         
                      if("1".equals(request.getAttribute("response")))
                      {
                          %><tr><b><font color="red">"Wrong answer"</font></b></tr><%
                    	  request.setAttribute("response","0");
                      }else if("3".equals(request.getAttribute("response")))
                      {
                          %><tr><b><font color="red">"Could not process your request please retry after some time"</font></b></tr><%
                    	  request.setAttribute("response","0");
                      }
                      if("2".equals(request.getSession().getAttribute("response")))
                      {
                          answer = "disabled='true'";
                    	  %><tr><b><%=request.getSession().getAttribute("msg")%></b></tr><%
                          RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
                          request.setAttribute("response","2");
                          dispatcher.forward(request, response);
                      }
            %>
                                                                      <tr style="display: table-row;">		
					<td width="45%" valign="top" nowrap="nowrap" class="tbl_login"> <span class="gaia cca al">&nbsp;Pertanyaan Rahasia<b><font color="#999999" size="-2"><em><font color="#FF0000">&nbsp;</font>/Security Question</em></font></b>&nbsp;</span> 
					</td>
                    <td width="55%">
                                                                                <div>
                                                                                     <input name="toPage" id="toPage" type="hidden" value="<%=postPage%>">
                                                                                    <input name="SecurityQuestion" id="SecurityQuestion" disabled="disabled" size="53" type="text" value="<%=secQuestion%>">
                                                                                </div>  </td>
                                                                        </tr>
                                                                        <tr style="display: table-row;"> 
					<td valign="top" nowrap="nowrap" class="tbl_login"><span class="gaia cca al">&nbsp;Jawaban Rahasia<font color="#999999" size="-2"><em><font color="#FF0000">*</font>/Secret Answer</em></font></span>&nbsp;</font>
					</td>
					<td><div>
                                                                                    <input <%=answer%> name="Answer" id="Answer" <%=disabled%> size="53" autocomplete="off" type="text">
                                                                                </div>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td colspan="1">&nbsp;  </td>
                                                                            <td colspan="2" align="right"><br>
                                                                                <input  <%=answer%> id="submitbutton" class="bttn_submit" name="submitbutton" value="Submit" type="SUBMIT" <%=submitbuttonstyle%>>
                                                                                <input  id="cancelbutton" name="cancelbutton" class="bttn_reset" value="Reset" type="reset" <%=disabled%>>
                                                                            </td>
                                                                        </tr>
                                                                    </tbody></table>
                                                            </td></tr>
                                                    </tbody></table>
                                            </td>
                                        </tr>
                                    </tbody></table>
                    </tbody></table>
            </form><%}else{ %>
            <table bgcolor="#ffffff" >
                                                                    <tbody><tr>
                                                                            <td colspan="2" valign="top">
                                                                                <span class="gaia ops gsl">
                                                                                <br><br><br><br>
                                                                                    <%=errorMsg%>
                                                                                </span>  </td>
                                                                        </tr>
            </tbody></table><br><br><br><br><br><br><br><br>
            <%} %>
            <%@include file="footer.jspf" %>
        </div>
    </body></html>