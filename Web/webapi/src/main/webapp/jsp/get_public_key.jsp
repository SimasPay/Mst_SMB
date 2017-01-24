<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Get Public Key</title>
</head>
<body>
<center>
<h1>Get Public Key</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="Account" name="service" /> 
<input type="hidden" value="GetPublicKey" name="txnName" />
<table>
	<tr>
		<td>channelId</td>
		<td><input type="hidden" name="channelID" value="7" />7</td>
	</tr>
	<tr>
		<td>AppOS</td>
		<td>
			<select name="appos">
	  				<option value="1">iOs</option>
	  				<option value="2">Android</option>
			</select>
		</td>
	</tr>
	<tr>
		<td>appVersion</td>
		<td><input type="text" name="appversion" value="" /></td>
	</tr>
	<tr>
		<td>Is Simaspay Activity</td>
		<td><input type="text" name="isSimaspayActivity" value="" /><em>true or false</em></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>