
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
<title>Selamat Datang di Website Pembayaran Smartfren | Lupa Password Update PIN</title>
        <script src="js/jquery-1.4.4.min.js"></script>
          <%--<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/validate/lib/jquery.delegate.js"></script>--%>
        <script type="text/javascript" src="js/jquery.validate.min.js"></script>
        <script  src="js/util.js"></script>
        <script src="js/hyperlinks.js"></script>
        <script src="js/forgotPasswordUpdation.js"></script>
        <link rel="stylesheet" href="css/util.css" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
        <link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
		<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
        </head>
    <body oncontextmenu="return false;">
<%
String mdn ="";
String resultMsg="";
String resultCode =request.getAttribute("resultCode")!=null?request.getAttribute("resultCode").toString():""; 
if("0".equals(resultCode))
{	mdn= request.getAttribute("mdn").toString(); 
}else if("1".equals(resultCode)){
	resultMsg = request.getAttribute("resultMsg").toString();
	mdn= request.getAttribute("mdn").toString();	
}else if("-1".equals(resultCode)){
	resultMsg = request.getAttribute("resultMsg").toString();
	mdn= request.getAttribute("mdn").toString();	
}
%>
<form name="forgotPasswordUpdation" id="forgotPasswordUpdation" action="ForgotPasswordUpdationServlet" onsubmit="return $('#forgotPasswordUpdation').valid();" method="POST">
   <table align="center" width="460" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(images/m06.png) bottom no-repeat;">
					<tr>
						<td colspan="2" id="title_order" align="center">		
						<h2>Ganti PIN</h2></font>
      <em><font color="#666666"><font size="-1">Change PIN</font></em></font> 
      <br/></td></tr>
	  <TR><TD></TD></TR>
                                    
  
                      <tr> 
                        
                <td align="left" colspan="2" id="title_order")>Masukkan PIN baru Anda<font color="#666666" size="-1"> 
                 <br> <em>(Please enter your new PIN)</em></font></td>
                      </tr>
                      <tr> 
                        <td colspan="2" style="font-weight: bold;"></td>
                      </tr>
                      <tr style="display: table-row;"> 
                        
                <td align="left" valign="top" class = "tbl_row2" nowrap="nowrap"> PIN Baru<font color="#FF0000">*</font><font color="#999999" size="-2"><em>/New 
                  PIN </em></font></td>
                        <td class = "tbl_row2">
                                                                    <div>
                                                                        <input id="newPassword" name="newPassword" size="30" type="password" autocomplete="off">
                                                                        <input type="hidden" name="requestType" value="forgotPasswordUpdation"/>
                                                                        <input type="hidden" name="mdn" value="<%=mdn%>" />
                                                                    </div>  </td>
                                                            </tr>
                                                            <tr style="display: table-row;"> 
                        
                <td align="left" valign="top" nowrap="nowrap" class = "tbl_row2"> Ulangi PIN 
                  Baru Anda<font color="#FF0000">* <BR></font><em><font color="#999999" size="-2">/Re-enter 
                  your new PIN</font></em></td>
                        <td class = "tbl_row2">
                                                                    <div>
                                                                        <input id="confirmPassword" name="confirmPassword" size="30" type="password" autocomplete="off">
                                                                    </div>  </td>
                                                            </tr>
                                                           
                                                           
                                                            <tr>
                                                                <td colspan="1">&nbsp;  <br><br></td>
                                                                <td colspan="1" align="right"><br>
                                                                    <input id="submitbutton" name="submitbutton" value="Save" type="SUBMIT" class = "bttn_change2">
                                                                    <input  id="cancelbutton" name="cancelbutton" value="Reset" type="reset" class = "bttn_cancel">

                                                                </td>
                                                            </tr>											
										</table>        
</form>
<%@include file="footer.jspf" %>
    </body>
</html>