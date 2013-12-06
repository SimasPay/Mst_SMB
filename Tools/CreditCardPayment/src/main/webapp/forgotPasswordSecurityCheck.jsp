<%@page import="com.mfino.ccpayment.util.RegistrationUtil"%>
<%@page import="com.mfino.fix.CmFinoFIX.CMJSError"%>
<%@page import="javax.servlet.RequestDispatcher"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" %>
<html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
<title>Selamat Datang di Website Pembayaran Smartfren | Cek keamanan</title>
        <script src="js/jquery-1.4.4.min.js"></script>
          <%--<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/validate/lib/jquery.delegate.js"></script>--%>
        <script type="text/javascript" src="js/jquery.validate.min.js"></script>
        <link rel="stylesheet" href="css/util.css" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
        <link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
        <script  src="js/util.js"></script>
        <script src="js/hyperlinks.js"></script>
        <script src="js/securityCheck.js"></script>
       
    </head>
    <body oncontextmenu="return false;">
        <div class="body" align="center">
            
            <%
            String secQuestion ="";
            String errorMsg = "";
            String answer ="" ;
            String submitbuttonstyle = "";
            String mdn="";
            if("0".equals(request.getAttribute("resultCode")))
            {
             secQuestion = request.getAttribute("resultMsg").toString();
             mdn = request.getAttribute("mdn").toString();
            }
            else if("-1".equals(request.getAttribute("resultCode"))){
            	errorMsg =request.getAttribute("resultMsg").toString();
            	secQuestion = request.getAttribute("secQuestion").toString();
            	mdn = request.getAttribute("mdn").toString();
            }else if("2".equals(request.getAttribute("resultCode")))
            {   //2 means max number of wrong passwords are tried.
            	errorMsg =request.getAttribute("resultMsg").toString();
            	answer = "disabled='true'";
            }            
            %>
            <br>
            <h2><font color="#666666">Pertanyaan rahasia untuk Ganti PIN</h2>
<h4><em>(Security Question for Change PIN)</em></font></font></h4>
<br>
            <form id="securityCheckChangePassword" name="securityCheckChangePassword" action="ForgotPasswordServlet" method="post" onSubmit="return $('#forgotPasswordSecurityCheck').valid()">
           
				 <table align="center" width="445" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(images/m06.png) bottom no-repeat;">
            <tbody>
                                                               <tr>
					<td id="title_order" colspan="2" valign="top" align="left">
					&nbsp;Lengkapi informasi dibawah ini. 	<BR>
      <span class="gaia cca al"><b><font color="#999999" size="-2"><em><font color="#666666" size="2">&nbsp;(Please provide the details)</font></em></font></b></span></td>
			   </tr>
			   
			   <tr style="display: table-row;">
		<td colspan="2" valign="top" align="center"><span
			class="gaia cca red"> <%=errorMsg%> </span></td>
	</tr>
                                                                       <tr style="display: table-row;">		
					<td width="45%" valign="top" nowrap="nowrap" class="tbl_login"> <span class="gaia cca al">&nbsp;Pertanyaan Rahasia<b><font color="#999999" size="-2"><em><font color="#FF0000">&nbsp;</font>/Security Question</em></font></b>&nbsp;</span> 
					</td>
                    <td width="55%">
                                                                                <div>
                                                                                    <input name="SecurityQuestion" id="SecurityQuestion" disabled="disabled" size="53" type="text" value="<%=secQuestion%>">
                                                                                     <!-- Hidden input field to figure out which page is the request coming from -->
                                                                                    <input type="hidden" name="requestType" value="forgotPasswordSecurtiyCheck"/>
                                                                                    <input type="hidden" name="mdn" value="<%=mdn%>" />      
                                                                                </div>  </td>
                                                                        </tr>
                                                                      <tr style="display: table-row;"> 
					<td valign="top" nowrap="nowrap" class="tbl_login"><span class="gaia cca al">&nbsp;Jawaban Rahasia<font color="#999999" size="-2"><em><font color="#FF0000">*</font>/Secret Answer</em></font></span>&nbsp;</font>
					</td>
                                                                            <td >
                                                                                <div>
                                                                                    <input <%=answer%> name="Answer" id="Answer" size="53" autocomplete="off" type="text">
                                                                                </div>
                                                                            </td>
                                                                        </tr>
                                                                        <br>
                                                                        <tr>
                                                                            <td colspan="1">&nbsp;  </td>
                                                                            <td colspan="1" align="right">
                                                                                <input  <%=answer%> id="submitbutton" name="submitbutton" value="Submit" class = "bttn_change" type="SUBMIT" <%=submitbuttonstyle%>>
                                                                                <input  id="back" name="back" value="Cancel" class = "bttn_cancel" type="button">
                                                                            </td>
                                                                        </tr>
                                                                    </tbody></table>
                                                           
            </form>
            <%@include file="footer.jspf" %>
        </div>
    </body></html>