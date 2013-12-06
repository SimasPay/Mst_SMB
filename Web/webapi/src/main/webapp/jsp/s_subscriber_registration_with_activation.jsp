<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>subscriber Registration with Activation</title>
</head>
<body>
<center>
<h1>subscriber Registration with Activation</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="Account" name="service" /> 
<input type="hidden" value="RegistrationWithActivation" name="txnName" />
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
		<td>SourcePhoneNumber</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	<tr>
		<td>First Name</td>
		<td><input type="text" name="subFirstName" value="" /></td>
	</tr>
	<tr>
		<td>Last Name</td>
		<td><input type="text" name="subLastName" value="" /></td>
	</tr>
	<tr>
		<td>ID Card Type</td>
		<td><input type="text" name="idType" value="" /></td>
	</tr>
	<tr>
		<td>ID Card Number</td>
		<td><input type="text" name="idNumber" value="" /></td>
	</tr>
	<tr>
		<td>Date Of Birth(ddMMyyyy)</td>
		<td><input type="text" name="dob" value="" /></td>
	</tr>
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelID" value="" /></td>
	</tr>	
	<tr>
		<td>newPin</td>
		<td><input type="text" name="activationNewPin" value="" /></td>
	</tr>
	<tr>
		<td>confirmPin</td>
		<td><input type="text" name="activationConfirmPin" value="" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>