<%@ page language="java" contentType="text/html; charset=ISO-8859-1" %>
<%@page import="com.mfino.ccpayment.util.RegistrationUtil"%>
<%@page import="java.util.List"%>
<%@page import="com.mfino.ccpayment.util.RegistrationUtil.RequestType"%>
<%@page import="com.mfino.cc.message.CCInfo"%>
<%@page import="com.mfino.cc.message.CCRegistrationInfo"%>
<%@page import="com.mfino.util.ConfigurationUtil"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
<title>Selamat Datang di Website Pembayaran Smartfren | Pilihan Pembayaran</title>
<link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
<link rel="stylesheet" type="text/css"	href="css/jquery-ui-1.8.2.custom.css">
<link rel="stylesheet" href="css/util.css" type="text/css" />
<script src="js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script src="js/util.js"></script>
<script src="js/editProfile.js"></script>
<script src="js/hyperlinks.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.2.custom.min.js"></script>
<link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
</head>

<%@include file="header.jspf"%>

<%
	if (request.getAttribute("response") == null || !"0".equals(request.getAttribute("response"))) {
%>
<jsp:forward page="index.jsp"></jsp:forward>
<%
	}
	Integer destinationLimit = ConfigurationUtil.getCCDestinationLimit();
	CCRegistrationInfo ccRegInfo = new CCRegistrationInfo();
	RegistrationUtil registrationUtil = new RegistrationUtil();
	String defaultDestination;
	registrationUtil.process(ccRegInfo, RequestType.Select);
	boolean hasError = false;
	String disabled = "";
	String visibility = "hidden";
	String errorMsg = "";
	if (ccRegInfo.getErrorCode() != 0) {
		hasError = true;
		visibility = "visible";
		errorMsg = ccRegInfo.getErrorDescription();
		disabled = "disabled='true'";
	}
	if(ccRegInfo.getIsConfirmationRequired()){
    	visibility = "visible";
        errorMsg = ccRegInfo.getErrorDescription();
        disabled="disabled='true'";
    	
    }
	boolean hasCard = true;
	if (null == ccRegInfo.getCcList() || ccRegInfo.getCcList().size() < 1)
		hasCard = false;
%>

<body dir="ltr" alink="#ff0000" bgcolor="#ffffff" link="#0000cc"
	text="#000000" vlink="#551a8b" onload="addrules()" oncontextmenu="return false;">
<div class="body" align="center">

<h2><font color="#666666">Ubah Profil</font></h2>
<h2><font color="#666666"><em><font color="#999999" size="-1">Edit Profile</font></em></font></h2>
<br/>
<%if(!ccRegInfo.getIsConfirmationRequired() && ccRegInfo.getErrorCode() == 0){ %>
<form name="editRegAccount" id="editRegAccount" method="post" action="UpdateProfileServlet">
<!-- Hidden input field to figure out which page is the request coming from -->
<input type="hidden" name="requestType" value="editProfile" />

<table align="center" width="680px" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(images/m09.png) bottom no-repeat;">
	
									<tr>
		
      <td align="left" colspan="2"> <span id="title_order" >
        </span>
            <span class="gaia cca blue">Kolom yang diberi tanda <span class="gaia cca red">*</span> wajib dilengkapi <br>
        <font color="#666666"><em>(Fields marks with * are mandatory)</em></font></span> 
      </td>
	</tr>
											<tr style="visibility:<%=visibility%>">
												<td nowrap="nowrap" valign="top"><font
													face="Arial, sans-serif" size="-1"><b> ErrorMsg	: </b></font>&nbsp;</td>
												<td>
												<div><label><%=errorMsg%></label></div>
												</td>
											</tr>
											<tr>
											<td align="left" class="tbl_row2" width="40%"><b>Nama Depan<span class="gaia cca red">*<em><font color="#999999" size="-2">/First 
        Name</font></em></span></b></td>
		<td align="left" class="tbl_row2" width="60%">

												<div><input name="firstname" id="firstname" size="30" class="update" 
													type="text"
													value="<%=hasError ? "" : ccRegInfo.getFirstName()%>" autocomplete="off"></div>
												</td>
											</tr>
											
											<tr>
		
      <td align="left" class="tbl_row2" width="40%"><b>Nama Belakang<span class="gaia cca red">*<em><font color="#999999" size="-2">/Last 
        Name </font></em></span></b></td>
		<td align="left" class="tbl_row2" width="60%">

												<div><input name="lastname" id="lastname" size="30" class="update" 
													type="text"
													value="<%=hasError ? "" : ccRegInfo.getLastName()%>"></div>
												</td>
											</tr>
											
											<tr>
		
      <td align="left" class="tbl_row2" width="40%"><b>Pertanyaan Rahasia<span class="gaia cca red">*<em><font color="#999999" size="-2">/Secret 
        Question </font></em></span></b></td>
		<td align="left" class="tbl_row2" width="60%">
												<div><input name="securityQuestion"
													id="securityQuestion" size="50" type="text" class="update" 
													value="<%=hasError ? "" : ccRegInfo.getSecurityQuestion()%>">
												</div>
												</td>
											</tr>
										<tr>
		
      <td align="left" class="tbl_row2" width="40%"><b>Jawaban Rahasia<span class="gaia cca red">*<em><font color="#999999" size="-2">/Secret 
        Answer </font></em></span></b></td>
		<td align="left" class="tbl_row2" width="60%">

												<div><input name="securityAnswer" id="securityAnswer" class="update" 
													size="50" autocomplete="off" type="text"
													value="<%=hasError ? "" : ccRegInfo.getSecurityAnswer()%>"></div>
												</td>
											</tr>
											
											<tr>
		
      <td align="left" class="tbl_row2" width="40%"><b>Telepon Rumah<span class="gaia cca red">*<em><font color="#999999" size="-2">/Home 
        Phone Number</font></em></span></b></td>
		<td align="left" class="tbl_row2" width="60%">
												<div><font face="Arial, sans-serif" size="-1">
												<i> <input name="homephone" id="homephone" size="30" class="update" 
													type="text"
													value="<%=hasError ? "" : ccRegInfo.getHomePhone()%>">
												</i> </font></div>
												</td>
											</tr>
											
											<!--- this variable would be dynamically changed based on num of cards available -->
											<tr>
												<td><input type="hidden" id="numOfCards"
													name="numOfCards" value="<%=1%>" /></td>
											</tr>
											<tr>
		
      <td align="left" class="tbl_row2" width="40%"><b>Telepon Kantor<span class="gaia cca red">*<em><font color="#999999" size="-2">/Office 
        Phone Number</font></em></span></b></td>
		<td align="left" class="tbl_row2" width="60%">
												<div><font face="Arial, sans-serif" size="-1">
												<i> <input name="workphone" id="workphone" size="30" class="update" 
													type="text"
													value="<%=hasError ? "" : ccRegInfo.getWorkPhone()%>">
												</i> </font></div>
												</td>
											</tr>

<tr>
		<td colspan="2" align="left" >&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2" id="title_order" align="left">Informasi Kartu Kredit</td>
	</tr>

											<tr>
         
      <td align="left" class="tbl_row2" width="40%"><b>6 Digit Pertama Kartu Kredit<span class="gaia cca red">*<em><font color="#999999" size="-2">/First 
        6 Digits</font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">
												<div><font face="Arial, sans-serif" size="-1">
												<i> <%--tf6_1 value is not going to backend b'coz it is hidden field so using one more field f6_1--%>
												<input name="f6_1" id="f6_1" size="30" type="text" class="update" 
													value="<%=hasCard ? ccRegInfo.getCcList().get(0).getCCNumberF6() : ""%>">
												<%--<input name="f6_1" id="f6_1" size="30" type="hidden" value="<%=hasCard? ccRegInfo.getCcList().get(0).getCCNumberF6():""%>">--%>
												</i> </font></div>
												</td>
											</tr>
											<tr>
         
      <td align="left" class="tbl_row2" width="40%"><b>4 Digit Terakhir Kartu 
        Kredit<span class="gaia cca red">*<em><font color="#999999" size="-2">/Last 
        4 Digits</font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">
												<div><font face="Arial, sans-serif" size="-1">
												<i> <%--tl3_1 value is not going to backend b'coz it is hidden field so using one more field l3_1
                <input name="tl3_1" id="tl3_1" size="30" type="text" disabled="disabled" value="<%=hasCard? ccRegInfo.getCcList().get(0).getCCNumberL4() : ""%>">--%>
												<input name="l4_1" id="l4_1" size="30" type="text" class="update" 
													value="<%=hasCard ? ccRegInfo.getCcList().get(0).getCCNumberL4() : ""%>">
												</i> </font></div>
												</td>
											</tr>
											<tr></tr>

											<tr>
												<td align="left" class="tbl_row2" width="40%"><b>Bank Penerbit Kartu<span class="gaia cca red">*<em><font color="#999999" size="-2">/Card 
        Issuer </font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">
												<div><font face="Arial, sans-serif" size="-1">
												<i> <input name="bankName_1" id="bankName_1" size="30" class="update" 
													type="text"
													value="<%=hasCard ? ccRegInfo.getCcList().get(0).getIssuerName() : ""%>">
												</i> </font></div>
												</td>
											</tr>
											<tr>
         
      <td align="left" class="tbl_row2" width="40%"><b>Nama pada Kartu<span class="gaia cca red">*<em><font color="#999999" size="-2">/Name 
        on Card</font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">
												<div><input name="nameOnCard_1" id="nameOnCard_1" class="update" 
													size="30" type="text"
													value="<%=hasCard ? ccRegInfo.getCcList().get(0).getNameOnCard() : ""%>"></div>
												</td>
											</tr>
											<tr>
		
      <td id="title_order" align="left">Nomor Tujuan<span class="gaia cca blue"><font colspan="2" id="title_order"><b><span class="gaia cca red"><em><font color="#666666" size="-1">/Destination 
        Smartfren Numbers </font></em></span></b></font></span></td>
		<td><input name="addmdn" id="addmdn" class="bttn_adddest" value="" type="button" <%=disabled%>></td>
	</tr>
											
										<tr>
		
      <td align="left" class="tbl_row2" width="40%"><b>Nomor Tujuan Default<span class="gaia cca red">*<em><font color="#999999" size="-2">/Default 
        Destination </font></em></span></b></td>
		<td align="left" class="tbl_row2" width="60%">
												<div><input name="destination0" id="destination0"
													size="30" type="text" disabled="disabled"
													value="<%=hasCard ? ccRegInfo.getMdn() : ""%>"></div>
												</td>
											</tr>
											<tr></tr>
											
											<%
												for (int i = 0; i < ccRegInfo.getCcDestinations().size(); i++) {
											%>
											<tr>
												<td align="left" class="tbl_row2" width="40%"><b>Nomor Tujuan <span class="gaia cca red"><em><font color="#999999" size="-2">/ 
        Destination <%=i+1%></font></em></span></b></td>
		<td align="left" class="tbl_row2" width="60%">
												<div><input name="destination<%=i+1%>"
													id="destination<%=i+1%>" size="30" type="text" class='dest'
													value="<%=hasCard ? ccRegInfo.getCcDestinations().get(i).getDestMDN() : ""%>"></div>
												<div><input name="destination<%=i+1%>ID"
													id="destination<%=i+1%>ID" size="30" type="hidden"
													value="<%=hasCard ? ccRegInfo.getCcDestinations().get(i).getID() : ""%>"></div>
												<div><input name="destination<%=i+1%>Version"
													id="destination<%=i+1%>Version" size="30" type="hidden"
													value="<%=hasCard ? ccRegInfo.getCcDestinations().get(i).getVersion() : ""%>"></div>

												</td>
											</tr>
											<%
												}
											%>

											<input type="hidden" name="destinationlimit" id="destinationlimit" value="<%=destinationLimit%>">
											<tr>
		<td colspan="2" id="title_order" align="left">
			<table width="99%" border="0" cellpadding="5" cellspacing="0">
            <tr>
				<td align="left" colspan="2" style="font-weight: bold;"><font colspan="2" id="title_order"> Alamat</font></td>
				<td align="left" colspan="2" style="font-weight: bold;"><font colspan="2" id="title_order">Alamat Penagihan</font></td>
			</tr>
			
			<tr>
				
            <td align="left" class="tbl_row2" width="20%"><b>Alamat<span class="gaia cca red">*<em><font color="#999999" size="-2">/</font></em></span></b><b><span class="gaia cca red"><em><font color="#999999" size="-2">Address</font></em></span></b></td>
				<td align="left" class="tbl_row2" width="30%">
				<input name="billingAddress_1" id="billingAddress_1" size="30" class="update" 
															value="<%=hasCard ? ccRegInfo.getCcList().get(0).getAddress1(): ""%>" type="text">
				</td>
            <td align="left" class="tbl_row2" width="19%"><b>Alamat Penagihan<span class="gaia cca red">*<em><font color="#999999" size="-2"> 
              /</font></em></span></b><b><span class="gaia cca red"><em><font color="#999999" size="-2">Billing 
              Address</font></em></span></b></td>
				<td align="left" class="tbl_row2" width="31%">
				<input name="billingAddress_2" id="billingAddress_2" size="30" type="text" class="update" 
															value="<%=hasCard ? ccRegInfo.getCcList().get(0).getBillingAddress(): ""%>">
				</td>
			</tr>
			<tr>
				
            <td align="left" class="tbl_row2" width="20%"><b>&nbsp;<span class="gaia cca red"><em><font color="#999999" size="-2">(Line2)</font></em></span></b></td>
				<td align="left" class="tbl_row2" width="30%">
				<input name="AddressLine2_1" id="AddressLine2_1" size="30" class="update" 
															value="<%=hasCard ? ccRegInfo.getCcList().get(0).getAddressLine2(): ""%>" type="text">
				</td>
            <td align="left" class="tbl_row2" width="19%"><b>&nbsp;<span class="gaia cca red"><em><font color="#999999" size="-2">(Line2)</font></em></span></b></td>
				<td align="left" class="tbl_row2" width="31%">
				<input name="AddressLine2_2" id="AddressLine2_2" size="30" class="update" 
															value="<%=hasCard ? ccRegInfo.getCcList().get(0).getBillingaddressLine2(): ""%>" type="text">
				</td>
			</tr>
			<tr>
				
            <td align="left" class="tbl_row2" width="20%"><b>Kota<span class="gaia cca red">*<em><font color="#999999" size="-2">/City</font></em></span></b></td>
				<td align="left" class="tbl_row2" width="30%">
				<input name="city_1" id="city_1" size="30" class="update" 
															value="<%=hasCard ? ccRegInfo.getCcList().get(0).getCity(): ""%>" type="text">
				</td>
            <td align="left" class="tbl_row2" width="19%"><b>Kota<span class="gaia cca red">*<em><font color="#999999" size="-2">/City</font></em></span></b></td>
				<td align="left" class="tbl_row2" width="31%">
				<input name="city_2" id="city_2" size="30" type="text" class="update" 
															value="<%=hasCard ? ccRegInfo.getCcList().get(0).getBillingcity(): ""%>">
				</td>
		    </tr>
			<tr>
				
            <td align="left" class="tbl_row2" width="20%"><b>Negara<span class="gaia cca red">*<em><font color="#999999" size="-2">/State</font></em></span></b></td>
				<td align="left" class="tbl_row2" width="30%">
				<input name="state_1" id="state_1" size="30" class="update" 
															value="<%=hasCard ? ccRegInfo.getCcList().get(0).getState(): ""%>" type="text">
				</td>
            <td align="left" class="tbl_row2" width="19%"><b>Negara<span class="gaia cca red">*<em><font color="#999999" size="-2">/State</font></em></span></b></td>
				<td align="left" class="tbl_row2" width="31%">
				<input name="state_2" id="state_2" size="30" type="text" class="update" 
															value="<%=hasCard ? ccRegInfo.getCcList().get(0).getBillingstate(): ""%>">
				</td>
			</tr>
			<tr>
				
            <td align="left" class="tbl_row2" width="20%"><b>Kode Pos<span class="gaia cca red">*<em><font color="#999999" size="-2">/Postal 
              Code</font></em></span></b></td>
				<td align="left" class="tbl_row2" width="30%">
				<input name="zipCode_1" id="zipCode_1" size="30" type="text" class="update" 
															value="<%=hasCard ? ccRegInfo.getCcList().get(0).getZipCode(): ""%>">
				</td>
				
            <td align="left" class="tbl_row2" width="19%"><b>Kode Pos<span class="gaia cca red">*<em><font color="#999999" size="-2">/Postal 
              Code</font></em></span></b></td>
				<td align="left" class="tbl_row2" width="31%">
				<input name="zipCode_2" id="zipCode_2" size="30" type="text" class="update" 
															value="<%=hasCard ? ccRegInfo.getCcList().get(0).getBillingzipCode(): ""%>">
				</td>
				</tr>
				</table></td>
			<tr>
		<td colspan="4" align="right">
			<input id="submitbutton" name="submitbutton" class="bttn_submit" type="SUBMIT" value="Update" <%=disabled %>> 
		</td>
    </tr>
								
											<%--hidden parameters --%>
					<tr>
					<td>
					
					<input type="hidden" name="newdestinations"	id="newdestinations" value="0">
					<input	type="hidden" name="destinations" id="destinations"	value="<%=hasCard ? ccRegInfo.getCcDestinations().size() : ""%>">
					<input type="hidden" name="olddestinations"	id="olddestinations" value="<%=hasCard ? ccRegInfo.getCcDestinations().size() : ""%>">
					<input name="cardVersion_1" id="cardVersion_1" type="hidden" value="<%=hasCard ? ccRegInfo.getCcList().get(0).getCardInfoVersion() : ""%>">
					<input name="cardId_1" id="cardId_1" type="hidden" value="<%=hasCard ? ccRegInfo.getCcList().get(0).getCardId(): ""%>">
					<input name="userVersion" id="userVersion" type="hidden" value="<%=hasError ? "" : ccRegInfo.getUserVersion()%>">
					<input name="userId" id="userId" type="hidden" value="<%=hasError ? "" : ccRegInfo.getUserid()%>">
				    <input name="subscriberId" id="subscriberId" type="hidden" value="<%=hasError ? "" : ccRegInfo.getSubscriberid()%>">
				    <input name="isUpdated" id="isUpdated" type="hidden" value="0">
					
					<%--to verify update --%>
					<%if(hasCard) {int j=0;%>
					
					<input name="firstname_org" id="firstname_org" type="hidden" value="<%=(++j)%>">
					<input name="lastname_org" id="lastname_org" type="hidden" value="<%=(++j)%>">
					<input name="securityQuestion_org" id="securityQuestion_org" type="hidden" value="<%=(++j)%>">
					<input name="securityAnswer_org" id="securityAnswer_org" type="hidden" value="<%=(++j)%>">
					<input name="homephone_org" id="homephone_org" type="hidden" value="<%=(++j)%>">
					<input name="workphone_org" id="workphone_org" type="hidden" value="<%=(++j)%>">
					<input name="f6_1_org" id="f6_1_org" type="hidden" value="<%=(++j)%>">
					<input name="l4_1_org" id="l4_1_org" type="hidden" value="<%=(++j)%>">
					<input name="bankName_1_org" id="bankName_1_org" type="hidden" value="<%=(++j)%>">
					<input name="nameOnCard_1_org" id="nameOnCard_1_org" type="hidden" value="<%=(++j)%>">
					<input name="billingAddress_1_org" id="billingAddress_1_org" type="hidden" value="<%=(++j)%>">
					<input name="AddressLine2_1_org" id="AddressLine2_1_org" type="hidden" value="<%=(++j)%>">
					<input name="city_1_org" id="city_1_org" type="hidden" value="<%=(++j)%>">
					<input name="state_1_org" id="state_1_org" type="hidden" value="<%=(++j)%>">
					<input name="zipCode_1_org" id="zipCode_1_org" type="hidden" value="<%=(++j)%>">
					<input name="billingAddress_2_org" id="billingAddress_2_org" type="hidden" value="<%=(++j)%>">
					<input name="AddressLine2_2_org" id="AddressLine2_2_org" type="hidden" value="<%=(++j)%>">
					<input name="city_2_org" id="city_2_org" type="hidden" value="<%=(++j)%>">
					<input name="state_2_org" id="state_2_org" type="hidden" value="<%=(++j)%>">
					<input name="zipCode_2_org" id="zipCode_2_org" type="hidden" value="<%=(++j)%>">
					<%for (int i = 0; i < ccRegInfo.getCcDestinations().size(); i++) {%>
					<input name="destination<%=i+1%>_org"	id="destination<%=i+1%>_org" type="hidden" value="<%=(++j)%>">
					<%}}	
					String destinationLimitAlert = "You are allowed to add up to <br><b><span class='gaia cca red'>"+destinationLimit+"</b></span> destinations only";
					String update = "No information is modified<br> ";
					%>	
					
					</td>
					</tr>
					
										
</table>
</form>
<div id="destlimit" style="display: none;" title="Destination Limit"><p align="center"><font size="3" face="Arial, sans-serif"><%=destinationLimitAlert%></font></div>
<div id="noupdate" style="display: none;" title="Update Alert"><p align="center"><font size="3" face="Arial, sans-serif"><%=update%></font></div>

 <% }else{%>
				<table bgcolor="#ffffff" >
                                                                    <tbody><tr>
                                                                            <td colspan="2" valign="top">
                                                                                <span class="gaia ops gsl">
                                                                                <br><br><br><br>
                                                                                    <%=errorMsg%>
                                                                                </span>  </td>
                                                                        </tr>
                                                                        
            </tbody></table><br><br><br><br><br><br><br><br>
            <%} %>
<%@include file="footer.jspf"%></div>
</body>
</html>