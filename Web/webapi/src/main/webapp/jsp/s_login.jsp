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
<h1>Login</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="Account" name="service" /> 
<input type="hidden" value="Login"	name="txnName" /> 
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
		<td>SourceMDN</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	<tr>
		<td>Pin</td>
		<td><input type="text" name="authenticationString" value="" /></td>
	</tr>
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelID" value="" /></td>
	</tr>
	<tr>
		<td>Is Simaspay Activity</td>
		<td><input type="text" name="isSimaspayActivity" value="" /><em>true or false</em></td>
	</tr>
	<tr>
		<td>AppOS</td>
		<td><input type="text" name="appos" value="" /></td>
	</tr>
	<tr>
		<td>AppType</td>
		<td><input type="text" name="apptype" value="" /></td>
	</tr>
	<tr>
		<td>appVersion</td>
		<td><input type="text" name="appversion" value="" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>