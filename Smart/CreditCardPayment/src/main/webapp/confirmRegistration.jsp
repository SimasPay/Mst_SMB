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
        <title>Selamat Datang di Website Pembayaran Smartfren  | Hasil Pendaftaran</title>
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
	
	<table width = "845" align = "center">
	<Tr> 
		<Td>
		<a href="http://www.smartfren.com"><img src="./images/smartfren-logo.png" style="border-style: none"></a> 
		</Td>
	</Tr>
	</table>
	
        <div class="body" align="center">  <br><br><br>
<br><br>
           <table align="center" width="460" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(./images/m06.png) bottom no-repeat;">
					<tr>
						<td colspan="2" id="title_order" align="center">		
						<h2>Respon</h2>
      </font>
      <em><font color="#666666"><font size="-1">Response</font></em></font> 
      <br/></td></tr>
<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
					
    
						<td colspan="2" width="85%" class="tbl_login" align="center">
                                                    
                                                    <%String resultCode=null;
                                                    if( request.getAttribute("resultCode")!=null){
                                                    	resultCode = request.getAttribute("resultCode").toString();
                                                    }
                                                    	String displayMsg = "";
                                                    	String backButton = "";
                                                    	if ("0".equals(resultCode)) {
                                                    		displayMsg = "Anda telah teregistrasi dalam layanan pembayaran Kartu Kredit Smartfren." +
                                            				"<p>Cek email anda dan ikuti langkah selanjutnya untuk mengaktifkan Account Anda." +
                                            				"<p>Anda hanya dapat menggunakan layanan ini setelah Account Anda diaktifkan." +
															"<p><p>Please click on the activation link sent to your email address" +
															"<p>to activate your Smartfren Credit Card Service.";
                                                       	}else if ("2".equals(resultCode)) {
                                                        	displayMsg = "<p>Nomor Smartfren Anda telah teregistrasi sebelumnya." +
                                                			"<p>Masukkan Nomor Smartfren lainnya.";
                                                        } else if ("3".equals(resultCode)) {
															displayMsg = "<p>Nomor Smartfren Anda telah teregistrasi sebelumnya." +
                                                			"<p>Masukkan Nomor Smartfren lainnya.";
                                                       	} else {
                                                    		displayMsg = "Registrasi Anda gagal:<br>" +
                                            				"<p>Hubungi Customer Care kami di 888.";
                                                    	}
                                                    	backButton = "window.location.href='./login.htm'";
                                                    %>
                                              
                                                <div align="center" ><%=displayMsg%><br></div>
                                                <br></td>
                                                </tr><tr><td colspan="2" align="right">
                                                <input type="button" value="Kembali" class="bttn_reset" onclick="<%=backButton%>"></input>
                                                
                                                        </td>
                                            </tr>
                                </table>
<%@include file="footer.jspf" %>
</div>
</body>
</html>