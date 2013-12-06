<%--<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
<script src="js/jquery-1.4.4.min.js"></script>
  <%--<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/validate/lib/jquery.delegate.js"></script>--%>
  <script type="text/javascript" src="js/jquery.validate.min.js"></script>
<link rel="stylesheet" href="css/util.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
<script src="js/util.js"></script>
<script src="js/hyperlinks.js"></script>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Selamat Datang di Website Pembayaran Smartfren | Edit Informasi</title>
 <link rel="icon" type="image/vnd.microsoft.icon" href="images/favicon.ico">
        <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="images/favicon.ico">
</head>
<body oncontextmenu="return false;" >

<div class="body" align="center">
<br><br><%@include file="header.jspf"%>
<br><br><br><br>
<table align="center" width="460" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(images/m06.png) bottom no-repeat;">
					<tr>
						<td colspan="2" id="title_order" align="center">		
						<h2>Ubah Profil</h2></font>
      <em><font color="#666666"><font size="-1">Edit Profile</font></em></font> 
      <br/></td></tr>
<tr><td>&nbsp;</td></tr>
	<tr>
		
				<td class="tbl_row2" align="center">
					<a href="securityCheck.jsp?toPage=editEmail"> Ubah Email </a>
				</td></tr>
			<tr>
				<td class="tbl_row2" align="center">
					<a href="securityCheck.jsp?toPage=editProfile">Ubah Profil</a>
				</td>
			</tr>
			<tr>
	<td>&nbsp;</td></tr>
			</table>
		
<br>
<br>


<%@include file="footer.jspf"%></div>
</body>
</html>