<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<!-- <title>Product Referral</title> -->
</head>
<body>
<center>
<h1>Product Referral </h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="AgentServices" name="service" />
<input type="hidden" value="ProductReferral" name="txnName" /> 
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
		<td>Agent MDN</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	
	  <tr>
		<td>Agent PIN</td>
		<td><input type="text" name="sourcePIN" value="" /></td>
	</tr> 
	 
	<tr>
		<td>Full Name</td>
		<td><input type="text" name="fullName" value="" /></td>
	</tr>
	<tr>
		<td>Subscriber MDN</td>
		<td><input type="text" name="destMDN" value="" /></td>
	</tr>
	
	
	
	<tr>
		<td>Email</td>
		<td><input type="text" name="email" value="" /></td>
	</tr>
	
	<tr>
		<td>Product Desired</td>
		<td><input type="text" name="productDesired" value="" /></td>
	</tr>
			
	<tr>
		<td>Others</td>
		<td><input type="text" name="others" value="" /></td>
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
