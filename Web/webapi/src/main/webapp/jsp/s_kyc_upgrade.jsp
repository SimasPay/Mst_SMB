<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>KYC Upgrade</title>
</head>
<body>
<center>
<h1>KYC Upgrade</h1>
<form action="../sdynamic" method="POST">
<input type="hidden"value="Account" name="service" /> 
<input type="hidden" value="KYCUpgrade" name="txnName" />
<table>
	<tr>
		<td>Institution ID*</td>
		<td><input type="text" name="institutionID" value="" /><em>Required if the request is from an Integration</em></td>
	</tr>
	<tr>
		<td>Integration Authentication Key*</td>
		<td><input type="text" name="authenticationKey" value="" /></td>
	</tr>
	<tr>
		<td>MDN*</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	<tr>
		<td>KYC Type*</td>
		<td><input type="text" name="kycType" value="" /><em>1-unbanked, 2-semibanked, 3-fullybanked</em></td>
	</tr>
	<tr>
		<td>channelId*</td>
		<td><input type="text" name="channelID" value="" /></td>
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
		<td>City*</td>
		<td><input type="text" name="city" value="" /></td>
	</tr>
	<tr>
		<td>Trans ID*</td>
		<td><input type="text" name="transID" value="" /></td>
	</tr>
	
	<tr>
		<td></td><td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>
