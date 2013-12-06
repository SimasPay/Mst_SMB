<%@page import="com.mfino.util.ConfigurationUtil"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
        <title>Selamat Datang di Website Pembayaran Smartfren | Lupa PIN</title>
        <script src="js/jquery-1.4.4.min.js"></script>
        <script type="text/javascript" src="js/jquery.validate.min.js"></script>
        <script  src="js/util.js"></script>
        <script src="js/hyperlinks.js"></script>
        <script  src="js/forgotpassword.js"></script>       
        <link rel="stylesheet" href="css/util.css" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="css/ipg_payment.css"/>
        <link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
    </head>
    <body oncontextmenu="return false;">

<%

String resultCode = request.getAttribute("resultCode")!=null?request.getAttribute("resultCode").toString():"";
String displayMsg=request.getAttribute("resultMsg")!=null?request.getAttribute("resultMsg").toString():"";
String mdn=request.getAttribute("mdn")!=null?request.getAttribute("mdn").toString():"";
if(resultCode.equals("1")){
	displayMsg="Invalid MDN ";
}else if(resultCode.equals("2")){
	displayMsg="Could not process your request please try again";
}
%>
<%--if (resultCode!=null && "-1".equals(resultCode)) {
	 request.setAttribute("resultCode","");
         request.setAttribute("resultMsg","");
}--%>
<br><br>
<form name="forgotPasswordForm" id="forgotPasswordForm" action="ValidateForgotPassword" method="POST">
   <table align="center" width="460" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(images/m06.png) bottom no-repeat;">
					<tr>
						<td colspan="2" id="title_order" align="center">		
						<h2>Reset PIN</h2>
         <br/></td></tr>
              <tr>
						<td id="title_order" colspan="2" align="left">Lupa 
                    PIN Anda? <font color="#999999" size="-1"><em>(Forgot 
                    Your PIN?)</em></font>
                
                  </td>
					</tr>
					<tr style="display: table-row;">
		<td colspan="2" valign="top" align="center"><span
			class="gaia cca red"> <%=displayMsg%> </span></td>
	</tr>
					<tr style="display: table-row;">
						
                <td width="45%" valign="top" nowrap="nowrap" class="tbl_login"> <span class="gaia cca al">Nomor 
                  Smartfren<b><font color="#999999" size="-2"><em><font color="#FF0000">*</font>/Smartfren 
                  Number</em></font></b>&nbsp;</span> </td>
                        <td width="55%"><div>
                                   <input id="mdn" name="mdn" size="30" type="text">
                               </div>  
                                   <input name="requestType" value="forgotpassword" type="hidden">                                                                    
                       </td>
                    </tr>                
					<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
					<br/>
					<tr><td colspan="2" align="right">
					    <input class="bttn_submit"  id="SUBMIT" name="SUBMIT" value="Submit" type="SUBMIT">
						<input class="bttn_reset" id="BACK" name="BACK" value="Kembali" type="button"></td>

					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					
</table>
</form>
<%@include file="footer.jspf" %>
      </body>
</html>