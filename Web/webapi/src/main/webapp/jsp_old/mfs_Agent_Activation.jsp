<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Agent Activation</title>
</head>
<body>
<center>
<h1>Agent Activation</h1>
<form action="../dynamic" method="POST">
<input type="hidden" value="Account" name="serviceName" /> 
<input type="hidden" value="AgentActivation"	name="transaction" /> 
<table>
	<tr>
		<td>AgentPhoneNumber</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	<tr>
		<td>OneTimePin</td>
		<td><input type="text" name="otp" value="" /></td>
	</tr>
	<tr>
		<td>newPin</td>
		<td><input type="text" name="newPIN" value="" /></td>
	</tr>
	<tr>
		<td>confirmPin</td>
		<td><input type="text" name="confirmPin" value="" /></td>
	</tr>
	
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelId" value="" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>