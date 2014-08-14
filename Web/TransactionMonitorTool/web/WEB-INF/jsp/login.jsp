<%@ page
	import="org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter"%>
<%@ page
	import="org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter"%>
<%@ page
	import="org.springframework.security.core.AuthenticationException"%>


<html>
<head>
<!-- Mimic Internet Explorer 7 -->
<meta http-equiv="X-UA-Compatible" content="IE=7">
<title>mFino | Transactions Monitor Tool - Login</title>
<link rel="icon" type="image/vnd.microsoft.icon"
	href="resources/images/favicon.png">
<link rel="shortcut icon" type="image/vnd.microsoft.icon"
	href="resources/images/favicon.png">

<%
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
            response.setHeader("Cache-Control", "no-store"); // HTTP 1.1
            response.setDateHeader("Expires", 0);
        %>
</head>

<body onload="handleLoadTasks()">
	<%@include file="../jspf/header.jspf"%>
	<script>
		function handleLoadTasks() {
			//register keypress events for input fields
			document.getElementById("j_username").addEventListener("keypress", handleKeyPress, false);
			document.getElementById("j_password").addEventListener("keypress", handleKeyPress, false);
			
			//check for unauthorized param to show popup
			if('${model.Error}' == 'Unauthorized access'){
				alert("Unauthorized access");
			}
			//check for sessionExpired param to show popup
			if('<%=request.getParameter("sessionExpired")%>'=='true'){ 
            	alert("The session is expired due to timeout or because the same user logged in on another system");
            }
		}	
	
		function handleKeyPress(e) {
			if (e.keyCode == 13) {
				validateLogin();
	        }
		}
		function validateLogin() {
			var xmlhttp;
			if (window.XMLHttpRequest) { // code for IE7+, Firefox, Chrome, Opera, Safari
			  xmlhttp=new XMLHttpRequest();
			} else {// code for IE6, IE5
			  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			}
			xmlhttp.onreadystatechange = function() {
			  if (xmlhttp.readyState==4 && xmlhttp.status==200) {			    
			    var response  = xmlhttp.responseText;
			    if(response.indexOf("index.htm")!= -1){
			    	window.location = "index.htm";
			    } 
			  } else if (xmlhttp.readyState==4 && xmlhttp.status==401) {
				  var response  = xmlhttp.responseText;
				  if(response.indexOf("User account is locked")!= -1){
					  alert("User account is locked"); 
				  } else {
					  alert("Authentication Failed: Invalid username or password"); 
				  }				  
			  } else if (xmlhttp.readyState==4 && xmlhttp.status==403) {
				  var response  = xmlhttp.responseText;
				  if(response.indexOf("User account is locked")!= -1){
					  alert("User account is locked"); 
				  } else {
					  alert("Authentication Failed: Invalid username or password"); 
				  }				  
			  }
			}
			xmlhttp.open("POST","j_spring_security_check",true);
			xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
			xmlhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
			var username = document.getElementById("j_username").value;
			var password = document.getElementById("j_password").value;
			xmlhttp.send("j_username="+username+"&j_password="+password);
		}
	</script>
	<div id="root_div">
		<div class="box_frame_login">
			<div class="box_middle_top_login">
				<div class="company_logo" style="float: left"></div>
				<div class="mfino_logo" style="float: right"></div>
			</div>
			<div class="box_big2_middle_login">
				<div class="white_header">
					<div class="top_pan"></div>
					<div id="login_root_div">
						<div style="height: auto;">
							<img
								style="float: right; margin-right: 30px; margin-top: 0px; height: 291px; width: 584px;"
								alt="image"
								src="<c:url value="/resources/images/login-image.png"/>" />
							<div id="loginPanel" style="margin-top: 60px; margin-left: 30px;">
							<table>
                               		<tr><td>Username</td><td><input type="text" id="j_username" name="j_username"/></td></tr>
                               		<tr><td>Password</td><td><input type="password" id="j_password" name="j_password"/></td></tr>
                               		<tr><td colspan="2" align="right"><input type="submit" value="Login" onclick="validateLogin()"/></td></tr>
                                </table>                            
							</div>
							<div id="main" style="float: left;"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="box_middle_bottom_login"></div>
			<%--<img style="float:right; margin-right:20px;margin-top:-225px;"alt="smart" src="<c:url value="/resources/images/background-5.jpg"/>"/>--%>
			<div style="margin-bottom: 20px;"></div>
			<div id="footer_login">
				<br>Copyright © 2014 mFino Ltd. All rights reserved.
			</div>
		</div>
	</div>
</body>
</html>
