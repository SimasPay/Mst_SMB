<%--<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<html>
    <head>
        <title>Selamat Datang di Website Pembayaran Smartfren |hasil Konfirmasi</title>
        <script src="js/jquery-1.4.4.min.js"></script>
        <%--<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/validate/lib/jquery.delegate.js"></script> --%>
        <script type="text/javascript" src="js/jquery.validate.min.js"></script>
        <link rel="stylesheet" href="css/util.css" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
        <link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
		<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
        <script  src="js/util.js"></script>
        <script src="js/hyperlinks.js"></script>
    </head>
    <body oncontextmenu="return false;">
       
        <div class="body" align="center"><br><br>
           <table align="center" width="465" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(./images/m06.png) bottom no-repeat;">
 <tr>
						<td colspan="2" id="title_order" align="center">		
						<h2>Respon </h2></font>
      <em><font color="#666666"><font size="-1"> Response</font></em></font> <br/></td></tr>
					<tr>
						
    
	<td colspan="2" align="center"><font color="#666666">
                                                    <%
                                                        String resultMsg = (String) request.getAttribute("resultMsg");
                                                        String resultCode = request.getAttribute("resultCode").toString();
                                                        String requestType = request.getAttribute("requestType").toString();
                                                        String backButton = "";
                                                        if ("0".equals(resultCode)) {
                                                            backButton = "window.location='./login.htm'";
                                                            resultMsg = "Your registration has been Activated<br>"+
                                                            "You can use user MDN and pin to Login";
                                                        }else if ("1".equals(resultCode)) {
                                                            backButton = "window.location='./registrationCodeConfirmation.jsp'";
                                                            resultMsg = "Invalid username or code";
                                                        } else if ("2".equals(resultCode)) {
                                                            backButton = "window.location='./login.htm'";
                                                            resultMsg = "Your registration request status has been expired<br>"+
                                                            "please register again";
                                                        }else if ("4".equals(resultCode)) {
                                                            backButton = "window.location='./login.htm'";
                                                            resultMsg = "Your profile changes has been confirmed";
                                                        } else if ("5".equals(resultCode)) {
                                                            backButton = "window.location='./login.htm'";
                                                            resultMsg = "Your profile changes has been Expired";
                                                        } else if ("6".equals(resultCode)) { //6 is used when any exception occurs in all the servlets
                                                            backButton = "window.location='./login.htm'";
                                                            resultMsg = "There is some problem with your registration confirmation<br> please try again "
                                                            			+"<p>Please contact Administrator at <br>"
                                                            			+"registration.service@smart-telecom.co.id <br>for further instructions.";
                                                        }else {
                                                            backButton = "window.location='./registrationCodeConfirmation.jsp'";
                                                        }
                                                        if(requestType.equalsIgnoreCase("get")){
                                                       	 backButton = "window.location='./login.htm'";
                                                       	 if(!"0".equals(resultCode) && !"4".equals(resultCode) && !"2".equals(resultCode))
                                                       		resultMsg = "Your Confirmation Link has been Expired";
                                                       }
                                                    %>
                                                <br>
                                                <%=resultMsg%><br>
                                                <br><br><br>
                                                <input type="button" value="Back" class="bttn_reset"onclick="<%=backButton%>"></input>
                                                
                                                        </td>
                                            </tr>
                               </table>
<%@include file="footer.jspf" %>
</div>
</body>
</html>