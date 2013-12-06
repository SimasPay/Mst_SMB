<%@ page language="java" contentType="text/html; charset=ISO-8859-1" %>

<html>
    <head>
    <meta http-equiv="Pragma" content="no-cache" />
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
        <title>Selamat Datang di Website Pembayaran Smartfren | Ganti PIN</title>
        <link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
        <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
        <script src="js/jquery-1.4.4.min.js"></script>
        <%--<script type="text/javascript" src="http://dev.jquery.com/view/trunk/plugins/validate/lib/jquery.delegate.js"></script>--%>
        <script type="text/javascript" src="js/jquery.validate.min.js"></script>
        <link rel="stylesheet" href="css/util.css" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
        <script src="js/changePassword.js"></script>
        <script src="js/hyperlinks.js"></script>
    </head>
   <body><br>
    <%@include file="header.jspf" %>
    
      
        <div class="body" oncontextmenu="return false;" align="center">
       
        <h2><font color="#666666">Ganti PIN/<font color="#999999"><em>Change PIN</em></font></font></h2><br>

            <form id="changePassword" name="changePassword" action="UpdateProfileServlet" method="post" onSubmit="">
            <!-- Hidden input field to figure out which page is the request coming from -->
				<input type="hidden" name="requestType" value="changePassword"/>
                 <table width="484" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(./images/m06.png) bottom no-repeat;">
      <tr>
		<td align="left" colspan="2" id="title_order">Masukan informasi di bawah 
          untuk merubah PIN <BR><em><font color="#999999" size="-1">(Fill information 
          below to change PIN)</font></em></td>
	</tr>
	
                                                                        <%
                                                                        if (!"1".equals(request.getAttribute("resultCode"))) {
                                                            if (request.getAttribute("response") == null || !"0".equals(request.getAttribute("response"))) {
                                                         %> 
                                                         <jsp:forward page="index.jsp"></jsp:forward> 
                                                       <%  }}
                                                    	if ("1".equals(request.getAttribute("resultCode"))) { %>
                                                        <tr><td colspan="2" align="center"><span class="gaia cca red"><b><%=request.getAttribute("resultMsg")%></b></span></td></tr>
                                                   <% 	request.setAttribute("resultMsg","");
                                                        request.setAttribute("resultCode","");
                                                        }     %>
                                                                    <tr></tr>    <tr>
         
        <td align="left" class="tbl_row2" width="37%"><b>PIN Lama<font color="#FF0000">*</font><font color="#999999" size="-2">/<em>Old 
          PIN</em></font></b></td>
         <td align="left" class="tbl_row2" width="63%">
                                                                                <div>
                                                                                    <input name="currentPasswd" id="currentPasswd" size="40" type="password" autocomplete="off">
                                                                                </div>  </td>
                                                                        </tr>
                                                                       <tr></tr> <tr>
         
        <td align="left" class="tbl_row2" width="37%"><b>PIN Baru<font color="#FF0000">*</font><font color="#999999" size="-2">/<em>New 
          PIN</em></font></b></td>
         <td align="left" class="tbl_row2" width="63%">
                                                                                <div>
                                                                                    <input name="newPasswd" id="newPasswd" size="40" type="password"  autocomplete="off">
                                                                                </div>  </td>
                                                                        </tr>
                                                                      <tr></tr>  <tr>
         
        <td align="left" valign="top" class="tbl_row2" width="37%"><p><b>Ulangi PIN Baru Anda<font color="#FF0000">*<BR></font></b><b> 
            /<font color="#999999" size="-2"><em>Confirm New PIN</em></font></b>
          </td>
         <td align="left" class="tbl_row2" width="63%">
                                                                                <div>
                                                                                    <input name="confirmPasswd" id="confirmPasswd" size="40" type="password" autocomplete="off">
                                                                                </div>  </td>
                                                                        </tr>
                                                                        <tr>
         <td align="left" width="37%">&nbsp;</td>
         <td align="left" width="63%">
			<input id="submitbutton" name="submitbutton" class="bttn_change2" value="Ganti" type="SUBMIT">
			<input id="cancelbutton" name="cancelbutton" class="bttn_reset" value="Reset" type="reset">
		</td>
	</tr></table>
                                                                  
            </form>
        </div>
        <%@include file="footer.jspf" %>
    </body></html>