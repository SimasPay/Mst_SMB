<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Bill Payment </title>
</head>
<body>
<center>
<h1>Bill Payment topup Inquiry</h1>
<form action="../dynamic" method="POST">
<input type="hidden" value="4" name="mode" /> 
<input type="hidden" value="billPaymentTopupInquiry"	name="serviceName" />
<table>
<tr>
		<td>MDN</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	<tr>
		<td>Customer ID</td>
		<td><input type="text" name="customerID" /></td>
	</tr>
	<tr>
		<td>Amount</td>
		<td><input type="text" name="amount" value="" /></td>
	</tr>
	<tr>
		<td>BillerName</td>
		<td><input type="text" name="billerName" value="" /></td>
	</tr>
	<tr>
		<td>BankCode</td>
		
		<td><input type="text" name="bankID" value="" /></td>
	</tr>
	<tr>
		<td>ChannelID</td>
		
		<td><input type="text" name="channelId" value="" /></td>
	</tr>
	
        <tr>
		<td>SourcePIN</td>
		<td><input type="text" name="sourcePIN" value="" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit"  value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>