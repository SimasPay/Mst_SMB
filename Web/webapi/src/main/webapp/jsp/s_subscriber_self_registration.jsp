<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Subscriber Self Registration as Non-KYC E-Money</title>
</head>
<body>
<center>
<h1>Subscriber Self Registration as Non-KYC E-Money</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="Account" name="service" /> 
<input type="hidden" value="SubscriberRegistration" name="txnName" />
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
		<td>ChannelId</td>
		<td><input type="text" name="channelID" value="7" /></td>
	</tr>	
	<tr>
		<td>Email</td>
		<td><input type="text" name="email" value="" /></td>
	</tr>	
	<tr>
		<td>New Pin</td>
		<td><input type="text" name="activationNewPin" value="" /></td>
	</tr>
	<tr>
		<td>Confirm Pin</td>
		<td><input type="text" name="activationConfirmPin" value="" /></td>
	</tr>
	<tr>
		<td>Security Question</td>
		<td><input type="text" name="securityQuestion" value="" /></td>
	</tr>
	<tr>
		<td>Security Answer</td>
		<td><input type="text" name="securityAnswer" value="" /></td>
	</tr>
	<tr>
		<td>OneTimePin</td>
		<td><input type="text" name="otp" value="" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>