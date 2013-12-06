<%-- 
    Document   : index
    Created on : Aug 6, 2010, 5:22:08 PM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.mfino.isorequests.listener.util.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>JSP Page</title>
</head>
<body>
<form name="form1" action="./RequestServlet">
<center>
<h2>Request</h2>
<table>

	<tr>
		<td>Bank</td>
		<td><select id="bankType" name="bankType" onchange="bankSelected(this)">
			<option id="bankType" value="-1" selected="selected"></option>
			<option id="bankType" value="0">Artajasa Bank</option>
			<option id="bankType" value="1">Mobile-8 Bank</option>
			<option id="bankType" value="2">Xlink Bank Gateway</option>
		</select></td>
	</tr>
	<tr>
		<td>Req Type</td>
		<td><select id="reqType" name="reqType" onchange="reqSelected(this)">
		    <option id="reqType"  value="-1" ></option>
			<option id="reqType"  value="200" selected="selected">Topup</option>
			<option id="reqType"  value="200">Payment</option>
			<option id="reqType"  value="200">Inquiry</option>
			<option id="reqType"  value="400">Payment	Reversal</option>
			<option id="reqType"  value="400">Topup Reversal</option>
		</select></td>
	</tr>

	<%
		for (int i = 0; i < Util.topupRequestParams.length; ++i) {
	%>
	<tr>
		<td><%=Util.topupRequestParams[i]%></td>
		<td><input type="text" name="<%=Util.topupRequestParams[i]%>"
			value="<%=Util.topupRequestDefaultvalues[i]%>" /></td>
		<td><%=Util.topupRequestParams[i + 1]%></td>
		<td><input type="text" name="<%=Util.topupRequestParams[i + 1]%>"
			value="<%=Util.topupRequestDefaultvalues[i + 1]%>" /></td>
	</tr>
	<%
		i++;
		}
	%>
	<tr>
		<td colspan="2" align="center"><input type="submit"
			value="Submit" /></td>
	</tr>
</table>
<br>
<br>
<br>
<br>
<h3>Help</h3>
</center>
<script type="text/javascript">

function bankSelected(elm){

	document.getElementById('reqType').value='-1';	
	var val = elm.options[elm.selectedIndex].value;
	<%for (int i = 0; i < Util.topupRequestParams.length; ++i) {%>	    		
    	var value = <%=Util.topupRequestParams[i]%>;
    	document.getElementsByName(value).item(0).disabled = false;   	
    <%}%>
    if(val == "0")
	{
    <%for (int i = 0; i < Util.topupRequestParams.length; ++i) {%>	    		
        	var value = <%=Util.topupRequestParams[i]%>;
        	if( value==40 || value==60||value==98 ||value==100){
            	document.getElementsByName(value).item(0).disabled = true;            	
        	}
	  <%}%>
    }
    else if(val == "1")
	{
    	<%for (int i = 0; i < Util.topupRequestParams.length; ++i) {%>	    		
        	var value = <%=Util.topupRequestParams[i]%>;
        	if(value==35||value==33||value==40||value==42||value==43||value==61||value==62||value==98|| value==100){
            	document.getElementsByName(value).item(0).disabled = true;            	
        	}
	  <%}%>
    }
    else if(val == "2")
	{  	<%for (int i = 0; i < Util.topupRequestParams.length; ++i) {%>	    		
        	var value = <%=Util.topupRequestParams[i]%>;
        	if(value==60|| value==100){
            	document.getElementsByName(value).item(0).disabled = true;            	
        	}
	  <%}%>
    }
}
</script>

<ul>
	<li>You can change the properties related to multix (ipaddress,
	port number, timeout) in WEB-INF in multix.properties file.<br>
	<br>
	</li>
	<li>You can view logs in <a href="./logs">log directory</a>.<br>
	<br>
	</li>
	<li>Submit the request by supplying above parameters. You would be
	redirected to plane page, because you can do back button and resubmit
	the request as many times as possible.<br>
	<br>
	</li>
	<li>It's boring to submit the request in textboxes. Don't worry,
	copy the URL as mentioned below and keep pinging URLs by changing query
	string parameters.<br>
	http://site/RequestServlet?reqType=200&2=00000&3=180000&4=5000&18=6011&32=881&37=1085B1910200&42=000000000000000&48=1101088911111111&49=360&63=110&90=6756753agh1237899<br>
	For inquiry, topup, payment reqType is 200. For reversals reqType is
	400.<br>
	<br>
	</li>
	<li><u>Note:</u> 90 would be accepted only for reversal requests,
	for rest it would be ignored.<br>
	<br>
	</li>
	<li><u>Disclaimer:</u> This tool is not extensively tested.<br>
	<br>
	</li>
	<li>One more known fact is, this help is boring :)</li>
</ul>
</form>
</body>
</html>
