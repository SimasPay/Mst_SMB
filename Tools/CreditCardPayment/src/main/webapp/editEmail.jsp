<%@page import="com.mfino.ccpayment.util.RegistrationUtil"%>
<%@page import="com.mfino.fix.CmFinoFIX.CMJSError"%>
<%@page import="javax.servlet.RequestDispatcher"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"%>
<%@page import="com.mfino.service.UserService"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Selamat Datang di Website Pembayaran Smartfren | Ubah email</title>

<script src="js/jquery-1.4.4.min.js"></script>
<%--<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/validate/lib/jquery.delegate.js"></script>--%>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script src="js/util.js"></script>
<script src="js/hyperlinks.js"></script>
<script src="js/editEmail.js"></script>

<link rel="stylesheet" href="css/util.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="css/ipg_payment.css" />
<link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
</head>

<body onload="addCodeRules()" oncontextmenu="return false;">
<div class="body" align="center"><br>
<%@include file="header.jspf"%> 
<%
 	CMJSError oldemail = RegistrationUtil.getEmail();
 	String email = "";
 	String requestType = "editEmailCodegenerate";
 	String errorMsg = "";
 	String newmail = "";
 	String submitbut = "";
 	String disabled = "";
 	String responseCode = "";
 	if (oldemail.getErrorCode() == 0) {
 		email = oldemail.getErrorDescription();
 	} else {
 		errorMsg = oldemail.getErrorDescription();
 		disabled = "disabled='true'";
 		submitbut = "disabled='true'";
 	}
 	if (request.getAttribute("responsecode") != null) {
 		responseCode = request.getAttribute("responsecode").toString();
 		requestType = "editEmailCodeValidation";
 		errorMsg = request.getAttribute("responsemsg").toString();
 		newmail = request.getAttribute("newemail").toString();
 		disabled = "disabled='true'";
 		if (responseCode.equals("3")) {
 			disabled = "disabled='true'";
 			submitbut = "disabled='true'";
 			errorMsg = "Could not process your request please try again";
 		}
 	}
 %>
 <br>
<h2><font color="#666666">Ubah Anda Email Id</h2>
<h4><em>(Edit your Email Id)</em></font></font></h4>
<br>
<form id="editEmail" name="editEmail" action="UpdateProfileServlet"	method="post" onSubmit="return $('#editEmail').valid()"><!-- Hidden input field to figure out which page is the request coming from -->
<table align="center" width="460" border="0" cellpadding="5"
	cellspacing="0"
	style="background: #fff url(images/m06.png) bottom no-repeat;">
	<input type="hidden" name="requestType" id="requestType" value="<%=requestType%>">
	<tr id="enteremail" style="display: none">
		<td id="title_order" colspan="2" valign="top" align="left">
		Masukan Anda baru Email Id. <BR>
		<span class="gaia cca al"><b><font color="#999999"
			size="-2"><em><font color="#666666" size="2">(Enter
		your new Email Id)</font></em></font></b></span></td>
	</tr>


	<tr id="entercode" style="display: none">
		<td id="title_order" colspan="2" valign="top" align="left">
		Masukan kode rahasia Anda<BR>
		<span class="gaia cca al"><b><font color="#999999"
			size="-2"><em><font color="#666666" size="2">(Please
		enter the code you received)</font></em></font></b></span></td>
	</tr>
	
	<tr style="display: table-row;">
		<td colspan="2" valign="top" align="center"><span
			class="gaia cca red"> <%=errorMsg%> </span></td>
	</tr>


	<tr style="display: table-row;">

		<td width="45%" valign="top" nowrap="nowrap" class="tbl_login"><span
			class="gaia cca al">lama id email <b><font color="#999999"
			size="-2"><em><font color="#FF0000">*</font>/Old Email
		ID:</em></font></b>&nbsp;</span></td>
		<td width="55%">
		<div><input name="oldemail" id="oldemail" disabled="disabled"
			size="30" type="text" value="<%=email%>"></div>
		</td>
	</tr>
	<tr style="display: table-row;">

		<td width="45%" valign="top" nowrap="nowrap" class="tbl_login"><span
			class="gaia cca al">email baru id <b><font color="#999999"
			size="-2"><em><font color="#FF0000">*</font>/New Email
		ID:</em></font></b>&nbsp;</span></td>
		<td width="55%">
		<div><input type="text" name="newemail" id="newemail" size="30"
			autocomplete="off" type="text" value="<%=newmail%>" <%=disabled%> autocomplete="off"></div>
		<%
			if (!responseCode.equals("")) {
		%> <input type="hidden" name="newemail"
			id="newemail" type="text" value="<%=newmail%>"> <%
 	}
 %>
		</td>
	</tr>
	
	<tr id="coderow" style="display: none">

		<td width="45%" valign="top" nowrap="nowrap" class="tbl_login"><span
			class="gaia cca al">Masukan kode <b><font color="#999999"
			size="-2"><em><font color="#FF0000">*</font>/Code:</em></font></b>&nbsp;</span>
		</td>
		<td width="55%">
		<div><input name="code" id="code" size="30" autocomplete="off"
			type="text"> <span class="gaia cca cmt"> <i><br>
		kode case sensitif<br>
		(code is case sensitive)</i> </span></div>

		</td>
	</tr>
	
	<tr>
		<td colspan="2" align="right"><BR>
		<br>
		<input class="bttn_submit" id="submitbutton" name="submitbutton"
			value="Submit" type="SUBMIT" <%=submitbut%>> <input
			class="bttn_reset" id="cancelbutton" name="cancelbutton"
			value="Kembali" type="reset"></td>
	</tr>

</table>
</form>
<%@include file="footer.jspf"%></div>
</body>
</html>