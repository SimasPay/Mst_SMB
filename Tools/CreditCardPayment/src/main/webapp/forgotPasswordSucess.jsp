
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<html>
    <head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
<title>Selamat Datang di Website Pembayaran Smartfren | Ulang hasil pin</title>
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
    <br><br>
        <div class="body" align="center">
        <br><br>
           <table align="center" width="460" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(images/m06.png) bottom no-repeat;">
					<tr>
						<td colspan="2" id="title_order" align="center">		
						<h2>Respon</h2></font>
      <em><font color="#666666"><font size="-1">Response</font></em></font> 
      <br/></td></tr>
					
					<tr>
						
						<td class="tbl_login" align="center">
                                                    <%
                                                    	String resultMsg = (String) request.getAttribute("resultMsg");
                                                    	String resultCode = request.getAttribute("resultCode").toString();
                                                    	String displayMsg = "";
                                                    	String login = "";
                                                    	displayMsg = "<br><br><br>"+resultMsg; 
                                                    	if ("0".equals(resultCode)) {
                                                    		login = "window.location.href='./login.htm'";
                                                    		displayMsg = "<br><br><br>Your pin has been Reset successfully";
                                                    	} else {
                                                    		displayMsg = "<br><br><br>There is a problem with your request:<br> " + resultMsg +
                                            				"<p>Please contact Administrator at <br>" +
                                            				"registration.service@smart-telecom.co.id <br>for further instructions.";
                                                    		login = "window.location.href='./login.htm'";
                                                    	}
                                                    %>
                                               <%=displayMsg%><br>
                                                <br><br><br></td></tr>
                                                <tr><td align="right">
                                                <input type="button" class="bttn_reset" value="Back" onclick="<%=login%>"></input>
                                              
                                                        </td>
                                            </tr>
                               </table>
<%@include file="footer.jspf" %>
</div>
</body>
</html>