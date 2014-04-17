<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>NFC Transaction History</title>
</head>
<body>
<center>
<h1>NFC Trasaction History</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value=NFCService name="service" /> 
<input type="hidden" value="History" name="txnName" />
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
		<td>Source MDN</td>
		<td><input type="text" name="sourceMDN"/></td>
	</tr>
	<tr>
		<td>PIN</td>
		<td><input type="text" name="sourcePIN"/></td>
	</tr>
	<tr>
		<td>Enter either of the below fields</td>
	</tr>
	<tr>
		<td>Card PAN</td>
		<td><input type="text" name="cardPan" value="" /></td>
	
		<td>Card Alias </td>
		<td><input type="text" name="cardAlias" value="" /></td>
	</tr>
	<tr>
		<td>PageNumber</td>
		<td><input type="text" name="pageNumber" /></td>
	</tr>
	
	<tr>
		<td>No. of Records</td>
		<td><input type="text" name="numRecords" /></td>
	</tr>
	<tr>
		<td>Sctl ID</td>
		<td><input type="text" name="sctlId" /></td>
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