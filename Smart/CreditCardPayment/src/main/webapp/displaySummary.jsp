<%@page import="com.mfino.util.ConfigurationUtil"%>
<%@page import="com.mfino.cc.message.CCPaymentInput"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Selamat Datang di Website Pembayaran Smartfren |Rincian pembayaran</title>
        <link rel="icon" type="image/vnd.microsoft.icon" href="images/favicon.ico">
        <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="images/favicon.ico">
        <script src="js/jquery-1.4.4.min.js"></script>
        <script type="text/javascript" src="js/jquery.validate.min.js"></script>
        <link rel="stylesheet" href="css/util.css" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
        <script  src="js/util.js"></script>
        <script  src="js/displaySummary.js"></script>
        <script src="js/hyperlinks.js"></script>
    </head>
    <%
            //Get the required parameters
            CCPaymentInput ccPaymentInput = new CCPaymentInput();
            String nsiachainnum = null;
            String nsiaCurrency = null;
            String nsiapurchaseCurrency = null;
            String nsiaacquireBin = null;
            String nsiapassword = null;
            String nsiawords = null;
            String nsiaType = null;
            String nsiaBasket = null;
            String nsiaMALLID = null;
            String gateway =ccPaymentInput.getPaymentGateway();
            if("NSIAPAY".equalsIgnoreCase(gateway)){
            gateway = "NSIAPAY";
            nsiachainnum=ccPaymentInput.getNsiachainnum();
            nsiaCurrency = ccPaymentInput.getNsiaCurrency();
            nsiapurchaseCurrency = ccPaymentInput.getNsiapurchaseCurrency();
            nsiaacquireBin = ccPaymentInput.getNsiaacquireBin();
            nsiapassword = ccPaymentInput.getNsiapassword();
            nsiaType = ccPaymentInput.getNsiaType();
            nsiaMALLID = ccPaymentInput.getNsiaMALLID();
                     }
            String merchantID = ccPaymentInput.getMerchantID();
            String paymentMethod = ccPaymentInput.getPaymentMethod();
            String currencyCode = ccPaymentInput.getCurrencyCode();
            String operation = request.getParameter("OPERATION");
            String Amount = (String) request.getAttribute("AMOUNT");
            String Package = (String) request.getParameter("PACKAGE");
            String pocketId = request.getParameter("POCKETID");
            String subscriberId = (String) request.getParameter("SUBSCRIBERID");
            String mdn = request.getParameter("MDN");
            String custFirstName = request.getParameter("CUST_FIRSTNAME");
            String custLastName = request.getParameter("CUST_LASTNAME");
            String custEmail = request.getParameter("CUST_EMAIL");
            String description = request.getParameter("DESCRIPTION");
            String path = (String) request.getAttribute("PATH");
            String disabled = (String) request.getAttribute("DISABLE");
            String errorMsg = (String) request.getAttribute("ERROR_MSG");
            String billReferenceNumber = (String) request.getAttribute("BILLREFERENCENUMBER");
            String city = (String) request.getParameter("DOMICILE_ADDRESS_CITY");
            String address = (String) request.getParameter("DOMICILE_ADDRESS");
            String state = (String) request.getParameter("DOMICILE_ADDRESS_STATE");
            String postcode = (String) request.getParameter("DOMICILE_ADDRESS_POSTCODE");
            String prodDesc = (String) request.getParameter("PRODUCTDESC");
            String piCode = (String) request.getParameter("PICODE");
            String visibility = "hidden";
            String postURL = ccPaymentInput.getPostURL();
            if ("TRUE".equals(disabled.toUpperCase())) {
                visibility = "visible";
                disabled = "disabled='true'";
            } else {
                disabled = "";
            }
    %>
    <body oncontextmenu="return false;">
       <%-- <%@include file="header.jspf" --%>
       <table width = "845" align = "center">
	<Tr> 
		<Td>
		<a href="http://www.smartfren.com"><img src="./images/smartfren-logo.png" style="border-style: none"></a> 
		</Td>
	</Tr>
	</table>
        
<div class="body" align="center"> <BR>
  <BR>
            
            <form name="myForm" method="POST" onsubmit="return false;"
                  action="<%=postURL%>">

                 <table width="590" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(./images/m09.png) bottom no-repeat;">
      <tbody>
	  <tr>
	  <td colspan="2" align="left">
	  <div id="title_details">Konfirmasi</div></td></tr>
	  <tr>
                                
																				
          <td colspan="2" align="left"> <span id="title_order"> Silakan periksa 
            data transaksi Anda sebelum melanjutkan pembayaran </span> </td>
        </tr>
        <tr class="tbl_row2"> 
          <td align="left" width="264"> <span id="title_order"> Data Transaksi 
            <font color="#666666"><em>/Transaction Details</em></font> </span> 
          </td>
        </tr>
        <tr > 
          <td align="left" valign="top" nowrap="nowrap" class="tbl_row2"> <b>Nomor 
            Smartfren<span class="gaia cca red"><em><font color="#999999" size="-2">/Smartfren 
            Number </font></em></span></b></td>
          <td align="left" width="306"  class="tbl_row2"> 
            <!-- class="alive" -->
            <div> 
              <label><%=mdn%></label>
            </div></td>
        </tr>
        <tr > 
          <td align="left" valign="top" nowrap="nowrap"  class="tbl_row2"> <b>Pesan<span class="gaia cca red"><em><font color="#999999" size="-2">/message</font></em></span></b></td>
          <td align="left" class="tbl_row2"> <div> 
              <label><%=description%></label>
            </div></td>
        </tr>
        <tr > 
          <td align="left" valign="top" nowrap="nowrap"  class="tbl_row2"> <b>Jumlah<span class="gaia cca red"><em><font color="#999999" size="-2">/Amount</font></em></span></b></td>
          <td align="left" class="tbl_row2"> <div> 
              <label><%=Amount%>.00</label>
            </div></td>
        </tr>
        <tr > 
          <td align="left" valign="top" nowrap="nowrap" class="tbl_row2"> <b>Paket<span class="gaia cca red"><em><font color="#999999" size="-2">/Package</font></em></span></b></td>
          <td align="left" class="tbl_row2"> <div> 
			<label><%=Package%></label>
		 
            </div></td>
        </tr>
        <tr style="visibility:<%=visibility%>" class="tbl_row2"> 
          <td nowrap="nowrap" valign="top"> <font face="Arial, sans-serif" size="-1"><b> 
            Error Message : </b></font> <b>&nbsp;</b> </td>
          <td> <div> 
              <label><%=errorMsg%></label>
            </div></td>
        </tr>
      <tr>
        <TD></TD>
        <TD></TD>
      </tr>
      <tr>
        <TD></TD>
        <TD></TD>
      </tr>
      <TR> 
        <TD align="right"><input type="button" name="CancelButton" id="CancelButton" class="bttn_cancel" onClick="confirmCancel()" value="Cancel"/></TD>
        <%
                                                                            	String msgForPopupBahasa = ConfigurationUtil.getCCPaymentPopupNotificationBahasa();
                                                                        		String msgForPopupEnglish = ConfigurationUtil.getCCPaymentPopupNotificationEnglish();
                                                                            %>
        <TD align="left"><input type="Submit" name="SubmitButton" id="SubmitButton" class="bttn_submit" onClick="confirmSubmit('<%=msgForPopupBahasa%>', '<%=msgForPopupEnglish%>')" value="Submit" <%=disabled%>></TD>
      </TR></tbody>
    </table>
                                                            </td></tr>
                                                    </tbody></table>
                                            </td>
                                        </tr>
                                    </tbody></table>
                            </td></tr>
                    </tbody></table>
         <% if(gateway.equalsIgnoreCase("infinitium")){ %>
                <input type="hidden" checked name="SUBSCRIBERID" id="SUBSCRIBERID" value="<%=subscriberId%>">
                <input type="hidden" checked name="POCKETID" id="POCKETID" value="<%=pocketId%>">
                <input type="hidden" id="MERCHANTID" name="MERCHANTID" value="<%=merchantID%>" /><br>
                <input type="hidden" id="PAYMENT_METHOD" name="PAYMENT_METHOD" value="<%=paymentMethod%>" /><br>
                <input type="hidden" id="CURRENCYCODE" name="CURRENCYCODE" value="<%=currencyCode%>" /><br>
                <input type="hidden" id="MERCHANT_TRANID" name="MERCHANT_TRANID" /><br>
                <input type="hidden" id="SIGNATURE" name="SIGNATURE" /><br>
                <input type="hidden" id="RETURN_URL" name="RETURN_URL" value="<%=path%>" />
                <input type="hidden" id="OPERATION" name="OPERATION" value="<%=operation%>" />
                <input name="MDN" id="MDN" type="hidden" value="<%=mdn%>">
                <input name="CUSTEMAIL" id="CUSTEMAIL" type="hidden" value="<%=custEmail%>">
                <input name="CUSTNAME" id="CUSTNAME" type="hidden" value="<%=custFirstName+" " + custLastName%>">
                <input name="CUSTFIRSTNAME" id="CUSTFIRSTNAME" type="hidden" value="<%=custFirstName%>">
                <input name="CUSTLASTNAME" id="CUSTLASTNAME" type="hidden" value="<%=custLastName%>">
                <input name="DESCRIPTION" id="DESCRIPTION" type="hidden" value="<%=description%>">
                <input name="AMOUNT" type="hidden" id="AMOUNT" size="50" value="<%=Amount%>" />
                <input name="BILLREFERENCENUMBER" type="hidden" id="BILLREFERENCENUMBER" value="<%=billReferenceNumber%>" />
                <input name="POSTURL" type="hidden" id="POSTURL" value="<%=postURL%>" />
                <input name="DOMICILE_ADDRESS_CITY" type="hidden" id="DOMICILE_ADDRESS_CITY" value="<%=city%>" />
                <input name="DOMICILE_ADDRESS_REGION" type="hidden" id="DOMICILE_ADDRESS" value="<%=address%>" />
                <input name="DOMICILE_ADDRESS_STATE" type="hidden" id="DOMICILE_ADDRESS_STATE" value="<%=state%>" />
                <input name="DOMICILE_ADDRESS_POSTCODE" type="hidden" id="DOMICILE_ADDRESS_POSTCODE" value="<%=postcode%>" />
                <input name="PICODE" type="hidden" id="PICODE" value="<%=piCode%>" />
              <%  } %>
              <% if(gateway.equalsIgnoreCase("NSIAPAY")){ %>
                <input type="hidden" id="TYPE" name="TYPE" value="<%=nsiaType%>" /><br>
                <input type="hidden" id="BASKET" name="BASKET"  /><br>
                <input name="MDN" id="MDN" type="hidden" value="<%=mdn%>">
                <input type="hidden" id="OPERATION" name="OPERATION" value="<%=operation%>" />
                <input name="DESCRIPTION" id="DESCRIPTION" type="hidden" value="<%=description%>">
                <input name="BILLREFERENCENUMBER" type="hidden" id="BILLREFERENCENUMBER" value="<%=billReferenceNumber%>" />
                <input type="hidden" id="MERCHANTID" name="MERCHANTID" value="<%=merchantID%>" /><br>
                <input type="hidden" id="CHAINNUM" name="CHAINNUM" value="<%=nsiachainnum%>" /><br>
                <input type="hidden" id="TRANSIDMERCHANT" name="TRANSIDMERCHANT"  /><br>
                <input type="hidden" id="AMOUNT" name="AMOUNT" size="50" value="<%=Amount%>" />
                <input type="hidden" id="CURRENCY" name="CURRENCY" value="<%=nsiaCurrency%>" /><br>
                <input type="hidden" id="PurchaseCurrency" name="PurchaseCurrency" value="<%=nsiapurchaseCurrency%>" /><br>
                <input type="hidden" id="acquirerBIN" name="acquirerBIN" value="<%=nsiaacquireBin%>" />
                <input type="hidden" id="password" name="password" value="<%=nsiapassword%>" />
                <input type="hidden" id="URL" name="URL" value="<%=path%>" />
                <input type="hidden" id="MALLID" name="MALLID" value="<%=nsiaMALLID%>"/>
                <input type="hidden" id="WORDS" name="WORDS" />
                <input name="POSTURL" type="hidden" id="POSTURL" value="<%=postURL%>" />
                <input type="hidden" id="SESSIONID" name="SESSIONID"  value="null"/>
                <input type="hidden" id="SIGNATURE" name="SIGNATURE" /><br>
                <input type="hidden" checked name="POCKETID" id="POCKETID" value="<%=pocketId%>">
                <input type="hidden" checked name="SUBSCRIBERID" id="SUBSCRIBERID" value="<%=subscriberId%>">
                <input type="hidden" checked name="gateway" id="gateway" value="<%=gateway%>">
                <input type="hidden" id="MERCHANT_TRANID" name="MERCHANT_TRANID" />
                <input type="hidden" id="PACKAGE" name="PACKAGE" value="<%=Package%>" />
                <input name="PICODE" type="hidden" id="PICODE" value="<%=piCode%>" />
                <br>
              <%  } %>

            </form>
        </div>
    </body>
</html>