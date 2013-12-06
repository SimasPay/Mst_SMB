<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Mobile Shopping</title>
</head>
<body>
<center>
<h1>Mobile Shopping Payment</h1>
<form action="../dynamic" method="POST">
<input type="hidden" value="5" name="mode" /> 
<input type="hidden" value="PaymentConfirm"	name="serviceName" />
<table>
<tr>
		<td>MDN</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	<tr>
		<td>Source Pocket</td>
		<td><input type="text" name="sourcePocketCode" value="" /></td>
	</tr>	
	<tr>
		<td>BillerCode</td>
		<td><input type="text" name="mfsBillerCode" value="" /></td>
	</tr>
	<tr>
		<td>Parent Txn Id</td>
		<td><input type="text" name="parentTxnID" value="" /></td>
	</tr>
	<tr>
		<td>confirmed</td>
		<td><input type="text" name="confirmed" value="true" /></td>
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