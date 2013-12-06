<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Get Third Party Data</title>
</head>
<body>
<center>
<h1>Get Third Party Data</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="Payment" name="service" /> 
<input type="hidden" value="GetThirdPartyData" name="txnName" />
<table>
	<tr>
		<td>Category</td>
		<td><input type="text" name="category" value="" /></td>
	</tr>
	<tr>
		<td>Version</td>
		<td><input type="text" name="version" value="" /></td>
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