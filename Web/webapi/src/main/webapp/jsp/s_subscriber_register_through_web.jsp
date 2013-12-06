<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>subscriber  registration</title>
</head>
<body>
<center>
<h1>subscriber registration</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="Account" name="service" /> 
<input type="hidden" value="SubscriberRegistrationThroughWeb"	name="txnName" /> 
<table>
	<tr>
		<td>Institution ID*</td>
		<td><input type="text" name="institutionID" value="" /></td>
	</tr>
	<tr>
		<td>Integration Authentication Key*</td>
		<td><input type="text" name="authenticationKey" value="" /></td>
	</tr>
	<tr>
		<td>SubscriberMDN*</td>
		<td><input type="text" name="subMDN" value="" /></td>
	</tr>
	<tr>
		<td>First Name*</td>
		<td><input type="text" name="subFirstName" value="" /></td>
	</tr>
	<tr>
		<td>Last Name*</td>
		<td><input type="text" name="subLastName" value="" /></td>
	</tr>
	<tr>
		<td>Date Of Birth*</td>
		<td><input type="text" name="dob" value="" /><em>ddMMyyyy</em></td>
	</tr>
	<tr>
		<td>Place Of Birth</td>
		<td><input type="text" name="birthPlace" value="" /></td>
	</tr>
	<tr>
		<td>KYC Type*</td>
		<td><input type="text" name="kycType" value="" /><em>1-unbanked, 2-semibanked, 3-fullybanked</em></td>
	</tr>
	<tr>
		<td>Plot No</td>
		<td><input type="text" name="plotNo" value="" /></td>
	</tr>
	<tr>
		<td>Street Address</td>
		<td><input type="text" name="streetAddress" value="" /></td>
	</tr>
	<tr>
		<td>Region</td>
		<td><input type="text" name="regionName" value="" /></td>
	</tr>
	<tr>
		<td>City*</td>
		<td><input type="text" name="city" value="" /></td>
	</tr>
	<tr>
		<td>Country</td>
		<td><input type="text" name="country" value="" /></td>
	</tr>
	<tr>
		<td>Address Proof</td>
		<td><input type="text" name="addressProof" value="" /></td>
	</tr>
	<tr>
		<td>Kin Name**</td>
		<td><input type="text" name="nextOfKin" value="" /><em>Mandatory if KYC Type is either 2 or 3</em></td>
	</tr>
	<tr>
		<td>Kin MDN**</td>
		<td><input type="text" name="nextOfKinNo" value="" /><em>Mandatory if KYC Type is either 2 or 3</em></td>
	</tr>
	<tr>
		<td>E-Mail</td>
		<td><input type="text" name="email" value="" /></td>
	</tr>
	<tr>
		<td>Nationality</td>
		<td><input type="text" name="nationality" value="" /></td>
	</tr>

	
	<tr>
		<td>ID Type</td>
		<td><input type="text" name="idType" value="" /></td>
	</tr>
	<tr>
		<td>ID No</td>
		<td><input type="text" name="idNumber" value="" /></td>
	</tr>
	<tr>
		<td>Date of Expiry</td>
		<td><input type="text" name="dateOfExpiry" value="" /><em>ddMMyyyy</em></td>
	</tr>

	<tr>
		<td>Company Name</td>
		<td><input type="text" name="companyName" value="" /></td>
	</tr>
	<tr>
		<td>Subscriber Mobile Company</td>
		<td><input type="text" name="subscriberMobileCompany" value="" /></td>
	</tr>
	<tr>
		<td>Cert Of Incorp</td>
		<td><input type="text" name="certOfIncorp" value="" /></td>
	</tr>
	
	<tr>
		<td>channelId*</td>
		<td><input type="text" name="channelID" value="7" /></td>
	</tr>
	<tr>
		<td>Is Approval Required*</td>
		<td><input type="text" name="approvalRequired" value="" /></td>
	</tr>
	<tr>
		<td>Bank Account Type</td>
		<td><input type="text" name="bankAccountType" value="" /></td>
	</tr>
	
	<tr>
		<td>Card Pan</td>
		<td><input type="text" name="cardPan" value="" /></td>
	</tr>
	<tr>
		<td>Reg Branch Code*</td>
		<td><input type="text" name="appId" value="" /></td>
	</tr>
	<tr>
		<td>Authorizing Person First Name</td>
		<td><input type="text" name="authorizingFirstName" value="" /></td>
	</tr>
	<tr>
		<td>Authorizing Person Last Name</td>
		<td><input type="text" name="authorizingLastName" value="" /></td>
	</tr>
	<tr>
		<td>Authorizing Person ID Number</td>
		<td><input type="text" name="authorizingIdNumber" value="" /></td>
	</tr>
	<tr>
		<td>Approver Comments</td>
		<td><input type="text" name="approvalComments" value="" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>