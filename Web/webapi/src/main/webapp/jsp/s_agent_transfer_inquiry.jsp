<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Agent Transfer Inquiry</title>
</head>
<body>
<center>
<h1>Agent Transfer Inquiry</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="AgentServices" name="service" /> 
<input type="hidden" value="TransferInquiry" name="txnName" />
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
		<td>Enter either of the below fields</td>
	</tr>
	<tr>
		<td>Destination MDN</td>
		<td><input type="text" name="destMDN" value="" /></td>
	
		<td>Destination BankAccount No </td>
		<td><input type="text" name="destBankAccount" value="" /></td>
	</tr>
	<tr>
		<td>Amount</td>
		<td><input type="text" name="amount" value="" /></td>
	</tr>
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelID" value="7" /></td>
	</tr>
	<tr>
		
		<td><input type="hidden" name="bankID" value="" /></td>
	</tr>
	<tr>
		<td>SourcePocketCode</td>
		<td><select name="sourcePocketCode">
  				<option value="1">1-E-Money</option>
  				<option value="2">2-Bank</option>
  				<option value="6">6-Laku Pandia</option>
			</select>
		</td>
	</tr>
	<tr>
		<td>DestinationPocketCode</td>
		<td>
		<select name="destPocketCode">
  				<option value="1">1-E-Money</option>
  				<option value="2">2-Bank</option>
  				<option value="6">6-Laku Pandia</option>
		</select></td>
	</tr>
	<tr>
		<td>Description</td>
		<td><input type="text" name="description" /></td>
	</tr>	
	<tr>
		<td>Destination First Name</td>
		<td><input type="text" name="subFirstName" value="" /></td>
	</tr>
	<tr>
		<td>Destination Last Name</td>
		<td><input type="text" name="subLastName" value="" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>