<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>subscriber cashin confirm</title>
</head>
<body>
<center>
<h1>subscriber cashin confirm</h1>
<form action="../dynamic" method="POST">
<input type="hidden" value="Wallet" name="serviceName" /> 
<input type="hidden" value="subscriberCashInConfirm" name="transaction" />
<table>
	<tr>
		<td>SourcePhoneNumber</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	<tr>
		<td>AgentCode</td>
		<td><input type="text" name="partnerCode" value="" /></td>
	</tr>
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelId" value="" /></td>
	</tr>
	
	<tr>
		
		<td><input type="hidden" name="bankID" value="" /></td>
	</tr>
	<tr>
		<td>TransferId</td>
		<td><input type="text" name="transferId" value="" /></td>
	</tr>
	<tr>
		<td>Confirmation (true/false)</td>
		<td><input type="text" name="confirmed" value="true" /></td>
	</tr>
	
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>