<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Subscriber Registration by Agent</title>
</head>
<body>
<center>
<h1>Subscriber Registration by Agent</h1>
<form action="../sdynamic" method="POST">
<input type="hidden" value="AgentServices" name="service" /> 
<input type="hidden" value="SubscriberRegistration"	name="txnName" /> 
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
		<td>Agent MDN</td>
		<td><input type="text" name="sourceMDN" value="" /></td>
	</tr>
	<tr>
		<td>Agent Pin</td>
		<td><input type="text" name="sourcePIN" value="" /></td>
	</tr>
	<tr>
		<td>SubscriberMDN</td>
		<td><input type="text" name="destMDN" value="" /></td>
	</tr>
	<tr>
		<td>Name</td>
		<td><input type="text" name="subFirstName" value="" /></td>
	</tr>
	<tr>
		<td>KTP ID</td>
		<td><input type="text" name="ktpId" value="" /></td>
	</tr>
	<tr>
		<td>KTP Valid Until</td>
		<td><input type="text" name="ktpValidUntil" value="" /><em>ddmmyyyy</em></td>
	</tr>
	<tr>
		<td>KTP Valid Until Lifetime</td>
		<td><input type="text" name="ktpLifetime" value="true" /><em>(true / false)</em></td>
	</tr>
	<tr>
		<td>KTP Address</td>
		<td></td>
	</tr>
	<tr>
		<td>Line1</td>
		<td><input type="text" name="ktpLine1" value="" /></td>
	</tr>
	<tr>
		<td>Line2</td>
		<td><input type="text" name="ktpLine2" value="" /></td>
	</tr>
	<tr>
		<td>City</td>
		<td><input type="text" name="ktpCity" value="" /></td>
	</tr>
	<tr>
		<td>State</td>
		<td><input type="text" name="ktpState" value="" /></td>
	</tr>
	<tr>
		<td>Country</td>
		<td><input type="text" name="ktpCountry" value="" /></td>
	</tr>
	<tr>
		<td>Zip Code</td>
		<td><input type="text" name="ktpZipCode" value="" /></td>
	</tr>
	<tr>
		<td>Region Name</td>
		<td><input type="text" name="ktpRegionName" value="" /></td>
	</tr>
	<tr>
		<td>Domestic Address</td>
		<td></td>
	</tr>
	<tr>
		<td>Domestic Identity</td>
		<td><input type="text" name="domesticIdentity" value="1" /><em>1 - In accordance identity, 2 - In contrast to the Identity</em></td>
	</tr>
	<tr>
		<td>Line1</td>
		<td><input type="text" name="line1" value="" /></td>
	</tr>
	<tr>
		<td>Line2</td>
		<td><input type="text" name="line2" value="" /></td>
	</tr>
	<tr>
		<td>City</td>
		<td><input type="text" name="city" value="" /></td>
	</tr>
	<tr>
		<td>State</td>
		<td><input type="text" name="state" value="" /></td>
	</tr>
	<tr>
		<td>Country</td>
		<td><input type="text" name="country" value="" /></td>
	</tr>
	<tr>
		<td>Zip Code</td>
		<td><input type="text" name="zipCode" value="" /></td>
	</tr>
	<tr>
		<td>Region Name</td>
		<td><input type="text" name="regionName" value="" /></td>
	</tr>
	<tr>
		<td>Mother's Maiden Name</td>
		<td><input type="text" name="subMothersMaidenName" value="" /></td>
	</tr>
	<tr>
		<td>Date Of Birth</td>
		<td><input type="text" name="dob" value="" /><em>ddmmyyyy</em></td>
	</tr>
	<tr>
		<td>Work</td>
		<td><input type="text" name="work" value="" /></td>
	</tr>
	<tr>
		<td>Income</td>
		<td><input type="text" name="income" value="" /></td>
	</tr>
	<tr>
		<td>Goal Of Opening Account</td>
		<td><input type="text" name="goalOfOpeningAccount" value="" /></td>
	</tr>
	<tr>
		<td>Source of Funds</td>
		<td><input type="text" name="sourceOfFunds" value="" /></td>
	</tr>
	<tr>
		<td>KTP Document</td>
		<td><input type="text" name="ktpDocument" value="" /></td>
	</tr>
	<tr>
		<td>Subscriber Form Document</td>
		<td><input type="text" name="subscriberFormDocument" value="" /></td>
	</tr>
	<tr>
		<td>Supporting Document</td>
		<td><input type="text" name="supportingDocument" value="" /></td>
	</tr>
	<tr>
		<td>E-mail</td>
		<td><input type="text" name="email" value="" /></td>
	</tr>
	<tr>
		<td>Transaction Id</td>
		<td><input type="text" name="transactionId" value="" /></td>
	</tr>
	<tr>
		<td>channelId</td>
		<td><input type="text" name="channelID" value="7" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" value="submit" /></td>
	</tr>
</table>
</form>
</center>
</body>
</html>