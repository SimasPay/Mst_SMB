<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<html>
    <head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
<title>Selamat Datang di Website Pembayaran Smartfren | Konfirmasi ulang Pin</title>
        <script src="js/jquery-1.4.4.min.js"></script>
          <%--<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/validate/lib/jquery.delegate.js"></script>--%>
        <script type="text/javascript" src="js/jquery.validate.min.js"></script>
        <script  src="js/util.js"></script>
        <script src="js/hyperlinks.js"></script>
        <script  src="js/registrationCodeConfirmation.js"></script>
        <link rel="stylesheet" href="css/util.css" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
        <link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
    </head>
    <body oncontextmenu="return false;">
        
        <div class="body" align="center">
        <br>
            <h2>Konfirmasi Reset PIN<font color="#666666"></font></h2>
      
      <em><font color="#666666"><font size="-1">Reset PIN Confirmation</font></em></font> 
            <br/> <br/>
            <%
                String mdn = "";
                String resultMsg="";
                String restultCode= request.getAttribute("resultCode")!=null ? request.getAttribute("resultCode").toString():"";
                if("1".equals(restultCode)){
                	resultMsg = "Invalid MDN or Code";                	
                }
                if (null != request.getParameter("MDN")) {
                    mdn = request.getParameter("MDN");
                }
                String confirmationCode = "";
                if (null != request.getParameter("ForgotPasswordconfirmationCode")) {
                    confirmationCode = (String) request.getParameter("ForgotPasswordconfirmationCode");
                }                
            %>
            <br>
            <form name="forgotPasswordCodeConfirm" id="forgotPasswordCodeConfirm" action="ForgotPasswordConfirmationCodeServlet" onsubmit="return $('#forgotPasswordCodeConfirm').valid()" method="POST">
            	<!-- Hidden input field to figure out which page is the request coming from -->
				<input type="hidden" name="requestType" value="confirmCode"/>
                <table width="550" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(images/m06.png) bottom no-repeat;">
                                                                    <tbody>
        <tr> 
          <td align="left" colspan="2" id="title_order">Masukkan informasi di bawah untuk 
              memastikan identitas Anda <br><em><font color="#666666" size="-1">(Fill 
              information below to validate your identity)</font></em> </td>
                                                                        </tr>
                                                                         <tr><td colspan="2" align="center"><span class="gaia cca red"></span></td></tr>
                                                                        <tr>
                                                                        </tr>
                                                                       <tr style="display: table-row;">
		<td colspan="2" valign="top" align="center"><span
			class="gaia cca red"> <%=resultMsg%> </span></td>
	</tr>
        <tr style="display: table-row;"> 
          <td align="left" class="tbl_row2" width="30%"> <b>Nomor Smartfren<font color="#FF0000">* 
            <em><font color="#999999" size="-2">Smartfren Number</font></em> </font></b></td>
                                                                            <td class="tbl_row2">
                                                                              
                                                                                <div>
                                                                                    <input type="text" name="MDN" id="MDN" value="<%=mdn%>" size="30" autocomplete="off"/>
                                                                                </div>  </td>
                                                                        </tr>
                                                                        <tr style="display: table-row;"> 
          <td align="left" class="tbl_row2" width = "30%"> <b>Kode Konfirmasi<font color="#FF0000">* 
            <font size="-2"> <em> <font color="#999999">Confirmation Code</font></em></font></font></b></td>
                                                                            <td class ="tbl_row2">
                                                                                <div>
                                                                                    <input name="confirmationCode" id="confirmationCode" type="text" value="<%=confirmationCode%>" size="30" autocomplete="off"/>
                                                                                </div>  </td>
                                                                        </tr>


                                                                        <tr>
                                                                             <td colspan="1">&nbsp;  </td>
                                                                            <td colspan="1" align="right">
                                                                                <input id="submitbutton" name="submitbutton" value="Submit" class = "bttn_change" type="SUBMIT">  </td>
                                                                        </tr>
                                                                    </tbody></table>
                                                           
            </form>
            <%@include file="footer.jspf" %>
        </div>
    </body>
</html>