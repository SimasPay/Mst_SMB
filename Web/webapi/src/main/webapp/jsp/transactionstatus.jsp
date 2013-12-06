<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Transaction Status</title>
</head>
<body>
<center>
<h1>Transaction Status</h1>
<form action="../dynamic" method="POST">
<input type="hidden" value="Account" name="service" /> 
<input type="hidden" value="TransactionStatus" name="txnName" />
<table>
	<tr>
		<td>SourceMDN</td>
		<td><input type="text" name="sourceMDN"/></td>
	</tr>
	<tr>
		<td>SourcePIN</td>
		<td><input type="text" name="sourcePIN"/></td>
	</tr>
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelID"/></td>
	</tr>
	<tr>
		<td>transferId</td>
		<td><input type="text" name="transferID" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>