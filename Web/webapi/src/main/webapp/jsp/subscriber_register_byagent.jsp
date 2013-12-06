<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>subscriber  registration</title>
</head>
<body>
<center>
<h1>subscriber registration</h1>
<form action="../dynamic" method="POST">
<input type="hidden" value="AgentServices" name="service" /> 
<input type="hidden" value="SubscriberRegistration"	name="txnName" /> 
<table>
	<tr>
		<td>Agent MDN</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	<tr>
		<td>SubscriberMDN</td>
		<td><input type="text" name="subMDN" value="" /></td>
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
		<td>Date Of Birth</td>
		<td><input type="text" name="dob" value="" /><em>ddmmyyyy</em></td>
	</tr>
	<tr>
		<td>AccountType</td>
		<td><input type="text" name="accountType" value="" /></td>
	</tr>
	
	<!-- <tr>
		<td>Amount</td>
		<td><input type="text" name="amount" value="" /></td>
	</tr>  -->
	<tr>
		<td>ApplicationId</td>
		<td><input type="text" name="appId" value="" /></td>
	</tr>
	<tr>
		<td>Pin</td>
		<td><input type="text" name="sourcePIN" value="" /></td>
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