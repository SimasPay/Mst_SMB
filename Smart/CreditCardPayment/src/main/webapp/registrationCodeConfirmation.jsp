<%--<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<html>
    <head><meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<!-- <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" /> -->
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
<title>Selamat Datang di Website Pembayaran Smartfren | Konfirmasi kode</title>
        <script src="js/jquery-1.4.4.min.js"></script>
         <%--<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/validate/lib/jquery.delegate.js"></script>--%>
        <script type="text/javascript" src="js/jquery.validate.min.js"></script>
        <script  src="js/util.js"></script>
        <script  src="js/registrationCodeConfirmation.js"></script>
        <script src="js/hyperlinks.js"></script>
        <link rel="stylesheet" href="css/util.css" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
        <link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
		<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
        </head>
    <body oncontextmenu="return false;">
       
        <div class="body" align="center">
          <h2><font color="#666666">Kode Konfirmasi</h2>
<em><font color="#666666"> <font size="-1">Confirmation Code</font></em></font> 

            <%
                String username = "";
                if (null != request.getParameter("username")) {
                    username = request.getParameter("username");
                }
                String confirmationCode = "";
                if (null != request.getParameter("confirmationCode")) {
                    confirmationCode = (String) request.getParameter("confirmationCode");
                }
            %>
            <form name="codeConfrim" id="codeConfrim" action="RegistrationServlet" onsubmit="return $('#codeConfrim').valid()" method="POST">
            	<!-- Hidden input field to figure out which page is the request coming from -->
				<input type="hidden" name="requestType" value="confirmCode"/>
               <table align="center" width="445" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(./images/m06.png) bottom no-repeat;">
  <tr>
                              <td id="title_order" colspan="2" valign="top" align="left">
					&nbsp;Lengkapi informasi dibawah ini. 	<BR>
      <span class="gaia cca al"><b><font color="#999999" size="-2"><em><font color="#666666" size="2">&nbsp;(Please provide the details)</font></em></font></b></span></td>
			   </tr>
       
			
			   <tr style="display: table-row;">		
					<td width="45%" valign="top" nowrap="nowrap" class="tbl_row2"> <span class="gaia cca al">&nbsp;Nomor Smartfren<b><font color="#999999" size="-2"><em><font color="#FF0000">*</font>/Smartfren Number</em></font></b>&nbsp;</span> 
					</td>
                    <td width="55%" class="tbl_row2">
                         
                                                                                <div>
                                                                                    <input type="text" name="username" id="username" value="<%=username%>"/>
                                                                                </div>  </td>
                                                                        </tr>
                                                                        <tr style="display: table-row;"> 
					<td valign="top" nowrap="nowrap" class="tbl_row2"><span class="gaia cca al">&nbsp;Masukan kode<font color="#999999" size="-2"><em><font color="#FF0000">*</font>/Enter Code</em></font></span>&nbsp;
					</td>
					<td class="tbl_row2"><div>
                                                                                <div>
                                                                                    <input name="confirmationCode" id="confirmationCode" type="text" autocomplete="off" value="<%=confirmationCode%>"/>
                                                                                </div>  </td>
                                                                        </tr>
<br><br>

                                                                        <tr>
                                                                        
                                                                            <td colspan="1">&nbsp;  </td>
                                                                            <td colspan="1" align="right">
                                                                                <input id="submitbutton"   class="bttn_submit" name="submitbutton" value="Submit" type="SUBMIT">  </td>
                                                                        </tr>
                                                                   </table>
            </form>
            <%@include file="footer.jspf" %>
        </div>
    </body>
</html>