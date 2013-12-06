<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.util.Date"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.mfino.util.DateUtil"%><html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Scheduler</title>
</head>
<body>
<%
Date end=new Date();
Date start =DateUtil.addDays(end,-1);
DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
%>
<center>
<br>
<br>
<h1>ReportScheduler Module</h1>

	<%-- <h3>Report Tool.</h3>
	<br><br>
	<form  name="reportForm" action="./Report" method="post">
	<table >
	<tr>
	<td align="left">Select Report Type</td>
	<td>
	<select name="reportType" >
					<option selected value="1">Consolidated Sales report</option>
					<option value="2">Customer Registration</option>
					<option value="3">Money Available</option>
					<option value="4">In Clearing</option>
					<option value="5">Agent Sales Commision</option>
					<option value="6">Service Charge and VAT</option>
					<option value="7">Transaction Report</option>
					<option value="8">FundMovementReport</option>
					<option value="11">CBNSummaryReport</option>	
					<option value="12">SubscriberClasificationReport</option>
					<option value="13">PartnerTransactionReport</option>
					<option value="9">MobileMoneyFialedReport</option>	
					<option value="10">UserRolesAndRight</option>
					<option value="14">AggregatevalueAndTransactionsperSubscriber</option>	
					<option value="15">ResolvedTransactionReport</option>
					<option value="16">MobileMoneyDuplicateTransactionReport</option>		
					<option value="17">RepeatedTransactionsPerTransactionTypeReport</option>		
					<option value="18">OverLimitTransactionReport</option>	
					<option value="19">ZMMAccountDeactiveReport</option>
					<option value="20">AccountProfileChangeReport</option>
					<option value="21">UpdatedAccountsReport</option>		
					<option value="22">DailyLimitUtilizationReport</option>	
					<option value="23">KinInformationMissingAccountReport</option>
					<option value="24">B2E2BTransactionReport</option>		
					<option value="25">CashFlow</option>
					<option value="26">MFSIncome</option>	
					<option value="27">MFSBalanceSheet</option>
					<option value="28">EndofDayProcess</option>		
												
				</select>
	
	
	</td>	
	</tr>
	<tr></tr>
	<tr></tr>

	<tr>
	<td align="left">From Date</td><td><input type="text" name="start" value=<%=df.format(start)%>>(dd/MM/yyyy)</td>
	</tr>
	<tr></tr>
	<tr></tr>
	<tr>
	<td align="left">To Date</td><td><input type="text" name="end" value=<%=df.format(end)%>>(dd/MM/yyyy)</td>
	</tr>
	
	<tr></tr>
	<tr></tr>
	<tr><td colspan="2" align="center"><input type="submit"  value="Generate"></td></tr>
	</table>
	</form> --%>
	</center>
	
</body>
</html>