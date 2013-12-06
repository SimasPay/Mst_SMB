<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Inter Emoney Transfer Inquiry</title>
</head>
<body>
<center>
<h1>Inter Emoney Transfer Inquiry</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="Wallet" name="service" /> 
<input type="hidden" value="InterEmoneyTransferInquiry" name="txnName" />
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
		<td>SourcePIN</td>
		<td><input type="text" name="sourcePIN" value="" /></td>
	</tr>
	<tr>
		<td>Amount</td>
		<td><input type="text" name="amount" value="" /></td>
	</tr>
	<tr>
		<td>Beneficiary OP Code</td>
		<td><input type="text" name="benOpCode" value="" /></td>
	</tr>
	<tr>
		<td>Beneficiary Phone number</td>
		<td><input type="text" name="destMDN" value="" /></td>
	</tr>
	<tr>
		<td>Narration</td>
		<td><input type="text" name="narration" value="" /></td>
	</tr>	
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelID" value="" /></td>
	</tr>
	<tr>
		<td>PocketCode</td>
		<td>1 (Emoney)</td>
	</tr>
	
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>
