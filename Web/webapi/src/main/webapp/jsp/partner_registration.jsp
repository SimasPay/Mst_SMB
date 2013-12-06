<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Partner  registration</title>
</head>
<body>
<center>
<h1>Partner registration</h1>
<form action="../dynamic" method="POST">
<input type="hidden" value="Account" name="service" /> 
<input type="hidden" value="PartnerRegistrationThroughAPI"	name="txnName" />

<table>

	<tr>
		<td>Institution ID*</td>
		<td><input type="text" name="institutionID" value="" />&nbsp;&nbsp;&nbsp;</td>
	
		<td>Integration Authentication Key*</td>
		<td><input type="text" name="authenticationKey" value="" /></td>
	</tr>
	<tr>
		<td>MDN*</td>
		<td><input type="text" name="subMDN" value="" /></td>
	
		<td>PartnerType*</td>
		<td><input type="text" name="partnerType" value="" /><em>7-Merchant, 8-Biller</em></td>
	</tr>
	<tr>
		<td>TradeName*</td>
		<td><input type="text" name="tradeName" value="" /></td>
	
		<td>PartnerCode*</td>
		<td><input type="text" name="partnerCode" value="" /></td>
	</tr>
	<tr>
		<td>UserName</td>
		<td><input type="text" name="userName" value="" /></td>
	</tr>
	<tr></tr>
	<tr><td colspan="4" align="center">Contact details</td></tr>
	<tr>
		<td>Plot No*</td>
		<td><input type="text" name="plotNo" value="" /></td>
	
		<td>Street Address</td>
		<td><input type="text" name="streetAddress" value="" /></td>
	</tr>
	<tr>
		<td>Region*</td>
		<td><input type="text" name="regionName" value="" /></td>
	
		<td>City*</td>
		<td><input type="text" name="city" value="" /></td>
	</tr>
	<tr>
		<td>Country*</td>
		<td><input type="text" name="country" value="" /></td>
	
		<td>PostalCode*</td>
		<td><input type="text" name="postalCode" value="" /></td>
	</tr>
	<tr></tr>
	<tr><td colspan="4" align="center"> OutletDetails</td></tr>
	<tr>
		<td>OutletClasification</td>
		<td><input type="text" name="outletClasification" value="" /></td>
	
	    <td>FranchisePhoneNumber*</td>
		<td><input type="text" name="franchisePhoneNumber" value="" /></td>
	</tr>
	<tr>
		<td>FaxNumber</td>
		<td><input type="text" name="faxNumber" value="" /></td>
	
		<td>TypeOfOrganization</td>
		<td><input type="text" name="typeOfOrganization" value="" /></td>
	</tr>
	<tr>
		<td>WebSiteUrl</td>
		<td><input type="text" name="webSite" value="" /></td>
	
		<td>Line Of Businesses / Industries*</td>
		<td><input type="text" name="industryClassification" value="" /></td>
	</tr>
	<tr>
		<td>NumberOf Outlets</td>
		<td><input type="text" name="numberOfOutlets" value="" /></td>
	
		<td>yearEstablished*</td>
		<td><input type="text" name="yearEstablished" value="" /></td>
	</tr>
	<tr></tr>
	<tr><td colspan="4" align="center">OutletAddress</td></tr>
	<tr>
		<td>OutletAddressLine1</td>
		<td><input type="text" name="outletAddressLine1" value="" /></td>
	
		<td>OutletAddressLine2</td>
		<td><input type="text" name="outletAddressLine2" value="" /></td>
	</tr>
	<tr>
		<td>outletAddressCity</td>
		<td><input type="text" name="outletAddressCity" value="" /></td>
	
		<td>OutletAddressState</td>
		<td><input type="text" name="outletAddressState" value="" /></td>
	</tr>
	<tr>
		<td>OutletAddressZipcode</td>
		<td><input type="text" name="outletAddressZipcode" value="" /></td>
	
		<td>OutletAddressCountry</td>
		<td><input type="text" name="outletAddressCountry" value="" /></td>
	</tr>
	
	<tr></tr>
	<tr><td colspan="4" align="center">OtherDetails</td></tr>
	<tr>
		<td>AuthorizedRepresentative</td>
		<td><input type="text" name="authorizedRepresentative" value="" /></td>
	
		<td>RepresentativeName*</td>
		<td><input type="text" name="representativeName" value="" /></td>
	</tr>
	<tr>
		<td>Designation</td>
		<td><input type="text" name="designation" value="" /></td>
	
		<td>AuthorizedFaxNumber</td>
		<td><input type="text" name="authorizedFaxNumber" value="" /></td>
	</tr>
	<tr>
		<td>E-Mail*</td>
		<td><input type="text" name="email" value="" /></td>
	
		<td>CardPan*</td>
		<td><input type="text" name="cardPan" value="" />for existing subscribers with bank ac not required</td>
	</tr>
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelID" value="" /></td>
	
		<td>Approver Comments</td>
		<td><input type="text" name="approvalComments" value="" /></td>
	</tr>
	<tr>
		<td>Is Approval Required</td>
		<td><input type="text" name="approvalRequired" value="" /></td>
	</tr>
	<tr>
		<td colspan="4"><input type="submit" value="submit" /></td>
	</tr>
	
</table>
</form>
</center>
</body>
</html>