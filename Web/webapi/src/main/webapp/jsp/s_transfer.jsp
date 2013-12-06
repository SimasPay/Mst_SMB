<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Transfer confirmation</title>
</head>
<body>
<center>
<h1>Transfer Confirmation</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="Wallet" name="service" /> 
<input type="hidden" value="Transfer" name="txnName" />
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
		<td>SourcePhoneNumber</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	<tr>
		<td>Enter either of the below fields</td>
	</tr>
	<tr>
		<td>Destination MDN</td>
		<td><input type="text" name="destMDN" value="" /></td>
	
		<td>Destination BankAccount No </td>
		<td><input type="text" name="destBankAccount" value="" /></td>
	</tr>
	<tr>
		<td>TransferId</td>
		<td><input type="text" name="transferID" value="" /></td>
	</tr>

	<tr>
		
		<td><input type="hidden" name="bankID" value="" /></td>
	</tr>
	<tr>
		<td>Parent TxnID</td>
		<td><input type="text" name="parentTxnID" value="" /></td>
	</tr>
		<tr>
		<td>Confirmation (true/false)</td>
		<td><input type="text" name="confirmed" value="true" /></td>
	</tr>
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelID" value="" /></td>
	</tr>
	<tr>
		<td>SourcePocketCode</td>
		<td><input type="hidden" name="sourcePocketCode" value="1"/>1</td>
	</tr>
	<tr>
		<td>DestinationPocketCode</td>
		<td><input type="text" name="destPocketCode" />(Emoney-1 ,Bank-2)</td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>