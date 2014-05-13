<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Inter Bank Transfer confirmation</title>
</head>
<body>
<center>
<h1>Inter Bank Transfer Confirmation</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="InterBankTransfer" name="txnName" />
<table>
	<tr>
		<td>Service*</td>
		<td>
			<select name="service" id="service" > 
				<option value="">Choose Service</option> 
				<option value="Wallet">Wallet</option> 
				<option value="Bank">Bank</option>
			</select>s 
		</td>
	</tr>
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
		<td>TransferId</td>
		<td><input type="text" name="transferID" value="" /></td>
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
		<td><input type="text" name="sourcePocketCode" value=""/><em>1-emoney, 2-bank</em></td>
	</tr>
	<tr>
		<td>MFAOneTimePin</td>
		<td><input type="text" name="mfaOtp" value="" /><em>Required if the transaction is in mfa mode</em></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>