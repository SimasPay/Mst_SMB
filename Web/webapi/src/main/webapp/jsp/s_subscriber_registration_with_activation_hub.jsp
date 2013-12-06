<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Subscriber Registration with Activation For Hub</title>
</head>
<body>
<center>
<h1>Subscriber Registration with Activation For Hub</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="Account" name="service" /> 
<input type="hidden" value="RegistrationWithActivationForHub" name="txnName" />
<table>
	<tr>
		<td>Source Phone No</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	<tr>
		<td>Nickname</td>
		<td><input type="text" name="nickname" value="" /></td>
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
		<td>Other Phone No</td>
		<td><input type="text" name="otherMDN" value="" /></td>
	</tr>
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelID" value="" /></td>
	</tr>	
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>