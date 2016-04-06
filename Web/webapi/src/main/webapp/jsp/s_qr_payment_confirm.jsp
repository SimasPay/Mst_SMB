<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>QR Payment confirm</title>
</head>
<body>
<center>
<h1>QR Payment confirm</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="Payment" name="service" /> 
<input type="hidden" value="QRPayment" name="txnName" />
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
	<td>InvoiceNumber</td>
		<td><input type="text" name="billNo" value="" /></td>
	</tr>
	<tr>
		<td>PaymentMode</td>
		<td><input type="text" name="paymentMode" value="" /></td>
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
		<td>MerchantData</td>
		<td><input type="text" name="merchantData" value="" /></td>
	</tr>
	<tr>
		<td>UserAPIKey</td>
		<td><input type="text" name="userAPIKey" value="" /></td>
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
		<td>DiscountAmount</td>
		<td><input type="text" name="discountAmount" />
	</tr>
	<tr>
		<td>LoyalityName</td>
		<td><input type="text" name="loyalityName" />
	</tr>
	<tr>
		<td>DiscountType</td>
		<td><input type="text" name="discountType" />
	</tr>
	<tr>
		<td>NumberOfCoupons</td>
		<td><input type="text" name="numberOfCoupons" />
	</tr>
	<tr>
		<td>Points Redeemed</td>
		<td><input type="text" name="pointsRedeemed" />
	</tr>
	<tr>
		<td>Amount Redeemed</td>
		<td><input type="text" name="amountRedeemed" />
	</tr>
	<tr>
		<td>Tipping Amount</td>
		<td><input type="text" name="tippingAmount" />
	</tr>	
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>