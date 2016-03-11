<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Airtime Purchase confirm</title>
</head>
<body>
<center>
<h1>Airtime Purchase Confirmation</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="Buy" name="service" /> 
<input type="hidden" value="AirtimePurchase" name="txnName" />
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
		<td>BillerCode</td>
		<td><input type="text" name="billerCode" value="" /></td>
	</tr>
	<tr>
		<td>RechargeMDN/Invoice</td>
		<td><input type="text" name="billNo" value="" /></td>
	</tr>
	<tr> 
		<td>PaymentMode(optional)</td> 
		<td><input type="text" name="paymentMode" value="" /></td> 
 	</tr>
	<tr>
		<td>Amount</td>
		<td><input type="text" name="amount" value="" /></td>
	</tr>
	<tr>
		<td><input type="hidden" name="bankID" value="" /></td>
	</tr>
	<tr>
		<td>TransferId</td>
		<td><input type="text" name="transferID" value="" /></td>
	</tr>
	<tr>
		<td>ParentTransferId</td>
		<td><input type="text" name="parentTxnID" value="" /></td>
	</tr>
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelID" value="" /></td>
	</tr>
	<tr>
		<td>PocketCode</td>
		<td>
			<select name="sourcePocketCode">
	  				<option value="1">E-Money</option>
	  				<option value="2">Bank</option>
	  				<option value="6">Laku Pandia</option>
			</select>
		</td>
	</tr>
	<tr>
		<td>Confirmation (true/false)</td>
		<td><input type="text" name="confirmed" value="true" /></td>
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