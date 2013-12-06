<%--<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
        <title>Selamat Datang di Website Pembayaran Smartfren | Ubah hasil Sandi</title>
        <script src="js/jquery-1.4.4.min.js"></script>
          <%--<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/validate/lib/jquery.delegate.js"></script>--%>
        <script type="text/javascript" src="js/jquery.validate.min.js"></script>
        <link rel="stylesheet" href="css/util.css" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
        <link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
		<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
        <script  src="js/util.js"></script>
        <script src="js/hyperlinks.js"></script>
    </head>
    <body oncontextmenu="return false;">
        <div class="body" align="center">
        <br>
        <%@include file="header.jspf" %>
        <br><br>
           <table align="center" width="460" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(./images/m06.png) bottom no-repeat;">
					<tr>
						<td colspan="2" id="title_order" align="center">		
						<h2>Respon</h2></font>
      <em><font color="#666666"><font size="-1">Response</font></em></font> 
      <br/></td></tr>
  
  <tr>
					<td align="right" width="20%"><img src="./images/confirm2.png"/></td>
						<td width="80%" class="tbl_login" align="center">
                                                    <%
                                                    	String resultMsg = (String) request.getAttribute("resultMsg");
                                                    	String resultCode = request.getAttribute("resultCode").toString();
                                                    	String displayMsg = "";
                                                    	if ("0".equals(resultCode)) {
                                                    		displayMsg = "Your PIN has succesfully changed.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" ;
                                                    	} else {
                                                    		displayMsg = "There is a problem with your password updation<br> " + resultMsg +
                                            				"<p>Please contact <br>Administrator at <br>" +
                                            				"registration.service@smart-telecom.co.id <br>for further instructions.";
                                                    	}
                                                    %>
                                                <br><br><br>
                                                <div align="center"><%=displayMsg%><br>
                                                <br><br><br>
                                                </div>
                                                        </td>
                                            </tr>
                                </table>
<%@include file="footer.jspf" %>
</div>
</body>
</html>
