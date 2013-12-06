<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.mfino.util.ConfigurationUtil"%>
<%@ page pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Selamat Datang Pembayaran Smart Telecom | Pendaftaran</title>

<script src="js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script src="js/util.js"></script>
<script src="js/hyperlinks.js"></script>
<script type="text/javascript" src="js/mdnvalidation.js"></script>

<link rel="stylesheet" href="css/util.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
<link rel="icon" type="image/vnd.microsoft.icon" href="images/favicon.ico">
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="images/favicon.ico">
</head>

<body dir="ltr" alink="#ff0000" bgcolor="#ffffff" link="#0000cc"
	text="#000000" vlink="#551a8b" onload="addRows()" oncontextmenu="return false;">

<table width = "845" align = "center">
	<Tr> 
		<Td>
		<a href="http://www.smartfren.com"><img src="./images/smartfren-logo.png" style="border-style: none"></a> 
		</Td>
	</Tr>
</table>

<div class="body" align="center"><br>
<br>

<!--h2><font color="#666666">Registrasi Pembayaran Smartfren</h2-->
<!--h4><em>(Register your Smartfren Payment)</em></font></font></h4-->
<br>
<form name="mdnvalidate" id="mdnvalidate" method="post"	action="CodeValidation">
<!-- Hidden input field to figure out which page is the request coming from -->

<%
	//Object responseCodeObj = request.getAttribute("responsecode");
	String responseCode = null;
	String responseMsg = "";
	String disabled = "";
	String mdn = "";
	String requestType = "generatecode";

	if (request.getAttribute("responsecode") != null
			&& request.getAttribute("mdn") != null) {
		responseCode = request.getAttribute("responsecode").toString();
		mdn = request.getAttribute("mdn").toString();
	}
%>

<table align="center" width="421" border="0" cellpadding="5"
	cellspacing="0"	style="background: #fff url(images/m06.png) bottom no-repeat;">


	<%
		if (responseCode != null
				&& (responseCode.equals("0") || responseCode.equals("1"))) {
			disabled = "disabled='true' value='" + mdn + "'";
			requestType = "validatecode";
			if (responseCode.equals("1"))
				responseMsg = "Wrong Code";
	%><%--to replace disabled mdn parameter in request  --%>
	<tr>
		<td><input type="hidden" name="mdn" value="<%=mdn%>" /></td>
	</tr>
	<%
		} else if (responseCode != null && responseCode.equals("4")) {
			disabled = "value='" + mdn + "'";
			responseMsg = "Invalid Smartfren number";
		}
	%>
	<input type="hidden" id="requestType" name="requestType" value="<%=requestType%>" />
	<tr id="entermdn" style="display: none">
		<td id="title_order" colspan="2" valign="top" align="left">
		Masukan Nomor Smartfren Anda untuk registrasi. <BR>
		<span class="gaia cca al"><b><font color="#999999"
			size="-2"><em><font color="#666666" size="2">(Enter
		your Smartfren Number to register)</font></em></font></b></span></td>
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
			class="gaia cca red"> <%=responseMsg%> </span></td>
	</tr>

	<tr style="display: table-row;">
		<td width="45%" valign="top" nowrap="nowrap" class="tbl_login"><span
			class="gaia cca al">Nomor Smartfren<b><font
			color="#999999" size="-2"><em><font color="#FF0000">*</font>/Smartfren
		Number</em></font></b>&nbsp;</span></td>
		<td width="55%">
		<div><input name="mdn" id="mdn" size="30" type="text" <%=disabled%>></div>
		</td>
	</tr>

	<tr></tr>
	<tr></tr>
	<tr></tr>
	<tr id="coderow" style="display: none;">
		<td valign="top" nowrap="nowrap"><font size="2"
			face="Arial, sans-serif">Masukan kode<span class="gaia cca al"><font
			color="#999999"><em><font color="#FF0000">*</font>/Code</em></font></span>:</font></td>
		<td width="55%">
		<div><input name="code" id="code" size="30" autocomplete="off" type="text">
		 <span class="gaia cca cmt"> <i><br>
		kode case sensitif<br>
		(code is case sensitive)</i> </span></div>

		</td>
	</tr>

	
	<tr>
		<td colspan="2" align="right"><BR>
		<br>
          <input class="bttn_submit" id="SUBMIT" name="SUBMIT" value="Next"	type="SUBMIT">
		 <input class="bttn_reset" id="BACK" name="BACK" value="Kembali" type="button"></td>
	</tr>
</table>
</form>
<%@include file="footer.jspf"%></div>

</body>
</html>