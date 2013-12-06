<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Bill Pay confirm</title>
</head>
<body>
<center>
<h1>Bill Pay confirm</h1>
<form action="../dynamic" method="POST">
<input type="hidden" value="Payment" name="service" /> 
<input type="hidden" value="BillPay" name="txnName" />
<table>
	<tr>
		<td>SourcePhoneNumber</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	<tr>
		<td>BillerCode</td>
		<td><input type="text" name="billerCode" value="" /></td>
	</tr>
	<tr>
	<td>InvoiceNumber</td>
		<td><input type="text" name="billNo" value="" /></td>
	</tr>	
	<tr>
		<td>ParentTransferId</td>
		<td><input type="text" name="parentTxnID" value="" /></td>
	</tr>
	<tr>
		<td>TransferId</td>
		<td><input type="text" name="transferID" value="" /></td>
	</tr>
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelID" value="" /></td>
	</tr>
	
	<tr>
		<td>Confirmation (true/false)</td>
		<td><input type="text" name="confirmed" value="true" /></td>
	</tr>
	<tr>
		<td>OneTimePin</td>
		<td><input type="text" name="otp" value="" /><em>Required if the transaction is in mfa mode</em></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>