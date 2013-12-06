<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.mfino.cc.message.CCRegistrationInfo"%>
<%@page import="com.mfino.ccpayment.util.RegistrationUtil"%>
<%@page import="com.mfino.util.ConfigurationUtil"%>


<%@page import="com.mfino.fix.CmFinoFIX"%>
<%@page import="com.mfino.domain.Company"%>
<%@page import="com.mfino.cc.message.CCProductInfo"%>
<%@page import="com.mfino.ccpayment.util.CCProductUtil"%>
<%@page import="java.util.Map"%><html>
<head>
<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<!-- <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" /> -->
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
<title>Welcome to Smart Telecom Payments | Payment Options</title>

<link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
<link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
<script src="js/jquery-1.4.4.min.js"></script>
<script src="js/hyperlinks.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<link rel="stylesheet" href="css/util.css" type="text/css" />
<script src="js/util.js"></script>

<%
	Company company;
	CCProductInfo ccpInfo = new CCProductInfo();
	CCProductUtil ccpUtil = new CCProductUtil();
	//ccpUtil.process(ccpInfo, CCProductUtil.RequestType.Select); 
	CCRegistrationInfo ccRegInfo = new CCRegistrationInfo();
	RegistrationUtil regUtil = new RegistrationUtil();
	regUtil.process(ccRegInfo, RegistrationUtil.RequestType.Select);
	boolean hasError = false;
	String topupDenominations[] = ConfigurationUtil
			.getCCTopUpDenominations().split(",");
	String topupDataDenominations[] = ConfigurationUtil
			.getCCTopUpDataDenominations().split(",");
	String disabled = "";
	String visibility = "hidden";
	String errorMsg = "";
	if (request.getSession().getAttribute("response") != null
			&& "2".equals(request.getSession().getAttribute("response"))) {
		visibility = "visible";
		disabled = "disabled='true'";
	}
	if (ccRegInfo.getErrorCode() != 0) {
		hasError = true;
		visibility = "visible";
		errorMsg = ccRegInfo.getErrorDescription();
		disabled = "disabled='true'";
	} else if (ccRegInfo.getIsConfirmationRequired()) {
		visibility = "visible";
		errorMsg = ccRegInfo.getErrorDescription();
		disabled = "disabled='true'";

	}
	/*CreditCardProductDAO ccpDAO = new CreditCardProductDAO();
	CreditCardProductQuery ccpQuery = new CreditCardProductQuery();
	ccpQuery.setProductIndicatorCode("5111");
	List<CreditCardProduct> results = ccpDAO.get(ccpQuery);*/
%>
<script>
            var cardArray = new Array();
            <%for (int i = 0; ccRegInfo.getCcList() != null
					&& i < ccRegInfo.getCcList().size(); i++) {
				String F6 = ccRegInfo.getCcList().get(i).getCCNumberF6();
				String L3 = ccRegInfo.getCcList().get(i).getCCNumberL4();
				Long pocketId = ccRegInfo.getCcList().get(i).getPocketId();
				String city = ccRegInfo.getCcList().get(i).getCity();
				String region = ccRegInfo.getCcList().get(i).getRegion();
				String state = ccRegInfo.getCcList().get(i).getState();
				String zipCode = ccRegInfo.getCcList().get(i).getZipCode();%>
                  cardArray["<%=pocketId%>"] = new Array("<%=pocketId%>",'<%=city%>','<%=region%>','<%=state%>','<%=zipCode%>');
            <%}%>
        </script>
<script src="js/index.js"></script>
</head>
<body>
<br>
<%@include file="header.jspf"%>

<!--<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%">-->
<div class="body" align="center" oncontextmenu="return false;">
<h2><font color="#666666">Selamat Datang di Layanan Kartu
Kredit Smartfren</font></h2>
<em><font color="#666666" size="-1">(Welcome to Smartfren
Credit Card Service)</font></em> <%
 	if (!ccRegInfo.getIsConfirmationRequired()
 			&& ccRegInfo.getErrorCode() == 0) {
 %>
<%
	if ("2".equals(request.getSession().getAttribute("response"))) {
			String msg = "Please contact adminstrator. <br>You cannot perform any activity, as you have exceeded <br>the number of retries for security answer";
%><tr>
	<td align="center"><br>
	<font color="red"><b><%=msg%></b></font></td>
</tr>
<br>
<%
	}
%>
<form id="optionsForm" name="optionsForm"
	action="./BillingDetailsServlet" method="post">
<table width="590" border="0" cellpadding="5" cellspacing="0"
	style="background: #fff url(./images/m09.png) bottom no-repeat;">
	<tbody>
		<tbody>

			<tr>

				<td colspan="2" valign="top"><span class="gaia ops gsl">
				</span></td>
			</tr>



			<tr style="display: table-row;">
				<td valign="top" nowrap="nowrap" align="left" class="tbl_row2"
					width="35%"><b>Pilih Layanan<font color="#FF0000">*</font><span
					class="gaia cca red"><em><font color="#999999"
					size="-2">/Select Service</font></em></span></b></td>
				<td nowrap="nowrap" align="left" class="tbl_row2" width="65%">
				<div><label> <input type="hidden" checked
					name="SUBSCRIBERID" id="SUBSCRIBERID"
					value="<%=ccRegInfo.getSubscriberid()%>"> <input
					type="hidden" checked name="OPERATION" id="OPERATION" value="1">
				<input type="radio" <%=disabled%> checked name="OPERATION_RADIO"
					id="OPERATION1" value="1"> </label>Bayar Tagihan Smartfren <input
					name="OPERATION_RADIO" id="OPERATION2" value="2" type="radio">Top
				Up Pulsa Smartfren
				<input
					name="OPERATION_RADIO" id="OPERATION3" value="3" type="radio">
              		Top Up Data Smartfren</div>
				</td>
			</tr>

			<tr style="display: table-row;">
				<td valign="top" nowrap="nowrap" align="left" class="tbl_row2"><b>Nomor
				Smartfren<font color="#FF0000">*</font><span class="gaia cca red"><em><font
					color="#999999" size="-2">/Smartfren Number </font></em></span></b></td>
				<td align="left" class="tbl_row2">
				<div><select name="MDN" id="MDN" <%=disabled%>>
					<option selected value="<%=ccRegInfo.getMdn()%>"><%=ccRegInfo.getMdn()%></option>

					<%
						for (int i = 0; ccRegInfo.getCcDestinations() != null
									&& i < ccRegInfo.getCcDestinations().size(); i++) {
								String destination = ccRegInfo.getCcDestinations().get(i).getDestMDN();
								//Long compID = ccRegInfo.getCcDestCompIDs().get(destination);
								String selected = "";
					%>
					<option <%=selected%> value="<%=destination%>"><%=destination%></option>
					<%
						}
					%>
				</select></div>
				</td>
			</tr>

			<tr "display:table-row;">
				<td valign="top" nowrap="nowrap" align="left" class="tbl_row2"><b>Kartu
				Kredit<span class="gaia cca red"><em><font
					color="#999999" size="-2">/Credit Card </font></em></span></b></td>
				<td>
				<%
					String F6 = ccRegInfo.getCcList().get(0).getCCNumberF6();
						String L3 = ccRegInfo.getCcList().get(0).getCCNumberL4();
						Long pocketId = ccRegInfo.getCcList().get(0).getPocketId();
				%><input
					type="text" disabled="disabled" size="30" value=<%=F6 + "XXXXXX" + L3%>></input>
				<input type="hidden" id="POCKETID" name="POCKETID"
					value="<%=pocketId%>"></td>
        </tr>
          <input type="hidden" id="PACKAGE" name="PACKAGE"
					value="">
		<input type="hidden" id="PRODUCTDESC" name="PRODUCTDESC"
					value="">

			<tr id="topupamountrow" style="display: none;">
				<td valign="top" nowrap="nowrap" align="left" class="tbl_row2"><b>Jumlah<font
					color="#FF0000">*</font><span class="gaia cca red"><em><font
					color="#999999" size="-2">/Amount</font></em></span></b></td>
				<td align="left" class="tbl_row2">
				<div><select name="AMOUNT" id="AMOUNT" <%=disabled%>>

					<%
						for (int i = 0; i < topupDenominations.length; i++) {
								String denomination = topupDenominations[i];
								String selected = "";
								if (i == 0)
									selected = "selected";
					%>
					<option <%=selected%> value="<%=denomination%>"><%=denomination%></option>
					<%
						}
					%>
				</select></div>
				</td>
			</tr>	
                 
        <tr id="ccproductrow1" style="display: none;"> 
          <td valign="top" nowrap="nowrap" align="left" class="tbl_row2"><b>CC Data Paket<font
					color="#FF0000">*</font><span class="gaia cca red"><em><font
					color="#999999" size="-2">/CC Data Package</font></em></span></b></td>
          <td align="left" class="tbl_row2"> <div>
              <select name="PICODE" id="PICODE" <%=disabled%>>
                <%                 
                ccpInfo.setCompanyID(1L);                
                ccpUtil.getInfo(ccpInfo);
                for (int i = 0; ccpInfo.getCCPList() != null
				&& i < ccpInfo.getCCPList().size(); i++) {
            String piCode = ccpInfo.getCCPList().get(i).getProductIndicatorCode();
			String description = ccpInfo.getCCPList().get(i).getProductDescription();
			String amount = ccpInfo.getCCPList().get(i).getAmount().toString();
			String selected = "";
			if (i == 0)
				selected = "selected";
					%>
                <option <%=selected%> value="<%=piCode%>"> <%=description%> &nbsp;&nbsp;&nbsp;Rp.&nbsp;<%=amount%></option>
                <%
						}
					%>
              </select>
            </div></td>
        </tr>
        
        <tr id="ccproductrow2" style="display: none;"> 
          <td valign="top" nowrap="nowrap" align="left" class="tbl_row2"><b>CC Data Paket<font
					color="#FF0000">*</font><span class="gaia cca red"><em><font
					color="#999999" size="-2">/CC Data Package</font></em></span></b></td>
          <td align="left" class="tbl_row2"> <div>
              <select name="PICODE" id="PICODE" <%=disabled%>>
                <%                 
                ccpInfo.setCompanyID(2L);                
                ccpUtil.getInfo(ccpInfo);
                for (int i = 0; ccpInfo.getCCPList() != null
				&& i < ccpInfo.getCCPList().size(); i++) {
            String piCode = ccpInfo.getCCPList().get(i).getProductIndicatorCode();
			String description = ccpInfo.getCCPList().get(i).getProductDescription();
			String amount = ccpInfo.getCCPList().get(i).getAmount().toString();
			String selected = "";
			if (i == 0)
				selected = "selected";
					%>
                <option <%=selected%> value="<%=piCode%>"> <%=description%> &nbsp;&nbsp;&nbsp;Rp.&nbsp;<%=amount%></option>
                <%
						}
					%>
              </select>
            </div></td>
        </tr>
        
        <tr> 
          <td valign="top" nowrap="nowrap" align="left" class="tbl_row2"><b>Pesan<font
					color="#FF0000">*</font><span class="gaia cca red"><em><font
					color="#999999" size="-2">/message</font></em></span></b></td>
				<td align="left" class="tbl_row2">
				<div><label> <input type="text" name="DESCRIPTION"
					size="30" <%=disabled%> id="DESCRIPTION"></input> </label></div>
				</td>
			</tr>
			<tr>
				<td valign="top" nowrap="nowrap" align="left" class="tbl_row2"><b>Nama
				Depan<span class="gaia cca red"><em><font
					color="#999999" size="-2">/First Name </font></em></span></b></td>
				<td align="left" class="tbl_row2">
				<div><input name="CUST_FIRSTNAME" id="CUST_FIRSTNAME"
					size="30" type="hidden"
					value="<%=hasError ? "" : ccRegInfo.getFirstName()%>"> <input
					name="CUSTFIRSTNAME" id="CUSTFIRSTNAME" disabled="disabled"
					size="30" type="text"
					value="<%=hasError ? "" : ccRegInfo.getFirstName()%>"></div>
				</td>
			</tr>
			<tr>
				<td valign="top" nowrap="nowrap" align="left" class="tbl_row2"><b>Nama
				Belakang<span class="gaia cca red"><em><font
					color="#999999" size="-2">/Last Name </font></em></span></b></td>
				<td align="left" class="tbl_row2">
				<div><input name="CUST_LASTNAME" id="CUST_LASTNAME" size="30"
					type="hidden" value="<%=hasError ? "" : ccRegInfo.getLastName()%>">
				<input name="CUSTLASTNAME" id="CUSTLASTNAME" disabled="disabled"
					size="30" type="text"
					value="<%=hasError ? "" : ccRegInfo.getLastName()%>"></div>
				</td>
			</tr>
			<tr>
				<td valign="top" width="35%" nowrap="nowrap" align="left"
					class="tbl_row2"><b>Email</b></td>
				<td align="left" class="tbl_row2"><input name="CUST_EMAIL"
					id="CUST_EMAIL" size="30" type="hidden"
					value="<%=hasError ? "" : ccRegInfo.getEmail()%>"> <input
					name="CUSTEMAIL" id="CUSTEMAIL" disabled="disabled" size="30"
					type="text" value="<%=hasError ? "" : ccRegInfo.getEmail()%>"></td>
        </tr>
        <tr> 
          <td valign="top" width="35%" nowrap="nowrap" align="left"
					class="tbl_row2"><b>Kota<span class="gaia cca red"><em><font
					color="#999999" size="-2">/City</font></em></span></b></td>
				<td valign="top" align="left" class="tbl_row2"><input
					name="DOMICILE_ADDRESS_CITY" id="DOMICILE_ADDRESS_CITY" size="30"
					type="hidden"
					value="<%=hasError ? "" : ccRegInfo.getCcList().get(0)
						.getCity()%>">
				<input name="DOMICILEADDRESSCITY" id="DOMICILEADDRESSCITY"
					disabled="disabled" size="30" type="text"
					value="<%=hasError ? "" : ccRegInfo.getCcList().get(0)
						.getCity()%>"></td>
			</tr>

			<tr>
				<td valign="top" width="35%" nowrap="nowrap" align="left"
					class="tbl_row2"><b>Provinsi<span class="gaia cca red"><em><font
					color="#999999" size="-2">/State</font></em></span></b></td>
				<td valign="top" align="left" class="tbl_row2"><input
					name="DOMICILE_ADDRESS_STATE" id="DOMICILE_ADDRESS_STATE" size="30"
					type="hidden"
					value="<%=hasError ? "" : ccRegInfo.getCcList().get(0)
						.getState()%>">
				<input name="DOMICILEADDRESSSTATE" id="DOMICILEADDRESSSTATE"
					disabled="disabled" size="30" type="text"
					value="<%=hasError ? "" : ccRegInfo.getCcList().get(0)
						.getState()%>"></td>
			</tr>

			<tr>
				<td valign="top" width="35%" nowrap="nowrap" align="left"
					class="tbl_row2"><b>Kode Pos<span class="gaia cca red"><em><font
					color="#999999" size="-2">/Postal Code </font></em></span></b></td>
				<td valign="top" align="left" class="tbl_row2"><input
					name="DOMICILE_ADDRESS_POSTCODE" id="DOMICILE_ADDRESS_POSTCODE"
					size="30" type="hidden"
					value="<%=hasError ? "" : ccRegInfo.getCcList().get(0)
						.getZipCode()%>">
				<input name="DOMICILEADDRESSPOSTCODE" id="DOMICILEADDRESSPOSTCODE"
					disabled="disabled" size="30" type="text"
					value="<%=hasError ? "" : ccRegInfo.getCcList().get(0)
						.getZipCode()%>"></td>
			</tr>
			<tr>
				<td align="left" width="35%">&nbsp;</td>
				<td align="right" width="65%"><input id="submitbutton"
					name="submitbutton" class="bttn_submit" <%=disabled%>
					value="Submit" type="SUBMIT"></td>
        </tr>
        <input name="<%=ccRegInfo.getMdn()%>"	id="<%=ccRegInfo.getMdn()%>" type="hidden" value="<%=ccRegInfo.getCcDestCompIDs().get(ccRegInfo.getMdn())%>">
        <%for (int i = 0; i < ccRegInfo.getCcDestinations().size(); i++) {%>
          <input name="<%=ccRegInfo.getCcDestinations().get(i).getDestMDN()%>"	id="<%=ccRegInfo.getCcDestinations().get(i).getDestMDN()%>" type="hidden" value="<%=ccRegInfo.getCcDestCompIDs().get(ccRegInfo.getCcDestinations().get(i).getDestMDN())%>"> 
          <%}%>
      </tbody>
    </table>

</form>
<%
	} else {
%>
<table bgcolor="#ffffff">
	<tbody>
		<tr>
			<td colspan="2" valign="top"><span class="gaia ops gsl">
			<br>
			<br>
			<br>
			<br>
			<%=errorMsg%> </span></td>
		</tr>

	</tbody>
</table>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<%
	}
%>
</div>
<%@include file="footer.jspf"%>
</body>
</html>