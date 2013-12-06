<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Download NFC Txn History</title>
</head>
<body>
<center>
<h1>Download NFC Txn History</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="NFCService" name="service" /> 
<input type="hidden" value="DownloadHistoryAsPDF" name="txnName" />
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
		<td>SourceMDN</td>
		<td><input type="text" name="sourceMDN"/></td>
	</tr>
	<tr>
		<td>SourcePIN</td>
		<td><input type="text" name="sourcePIN"/></td>
	</tr>
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelID"/></td>
	</tr>
	<tr>
		<td>Card PAN</td>
		<td><input type="text" name="cardPan" value="" /></td>
	</tr>
	<tr>
		<td>From Date</td>
		<td><input type="text" name="fromDate"/><em>ddMMyyyy</em></td>
	</tr>
	<tr>
		<td>To Date</td>
		<td><input type="text" name="toDate"/><em>ddMMyyyy</em></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>