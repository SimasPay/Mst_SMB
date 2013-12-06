

<%@include file="../jspf/include.jspf"%>
<%@ page import="org.springframework.security.web.authentication.*"%>

<html>


<head>
<!-- Mimic Internet Explorer 7 -->
   		<meta http-equiv="X-UA-Compatible" content="IE=7" >
<title>Mobile Financial Services Platform - Result</title>
 <link rel="icon" type="image/vnd.microsoft.icon" href="resources/images/favicon.png">
        <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="resources/images/favicon.png">
       
<%
	response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	response.setHeader("Cache-Control", "no-store"); // HTTP 1.1
	response.setDateHeader("Expires", 0);
%>

        
</head>

<body>
	<link type="text/css" href="<c:url value="/extjs/resources/css/ext-all.css"/>" rel="stylesheet" />
<link type="text/css" href="<c:url value="/resources/css/app.css"/>" rel="stylesheet" />
<link type="text/css" href="<c:url value="/resources/css/mfinostyles.css"/>" rel="stylesheet" />

<!-- Base -->
<script type="text/javascript" src="<c:url value="/extjs/adapter/ext/ext-base.js"/>" ></script>
<script type="text/javascript" src="<c:url value="/extjs/ext-all.js"/>" ></script>

	<div id="root_div">
		<div  class="box_frame_login">
			<div class="box_middle_top_login">
			  <div class="company_logo" style="float:left"></div>
              <div class="mfino_logo"  style="float:right"></div>
              </div>
			<div class="box_big2_middle_login">
				<div class="white_header">
					<div class="top_pan"></div>

				</div>
				<div style="height: 350px; ">
					<p style="padding-top:95px;" class="errormsg" align="center"><c:out value="${Error}" /></p>
					<p style="margin-top:15px;" align="center">	
						 <a href="<c:url value="/login.htm"/>">
						 <input style="width:80px;"  type="button"	value="Ok">
						</a>
					</p>
				</div>
				<!--<div class="box_middle_bottom_login"></div>
				--><!-- <div style="margin-bottom:20px;"></div> -->
				<div id="footer_login">
					<br>Copyright © 2013 mFino Ltd. All rights reserved. Build	2.6
				</div>

			</div>
		</div>
</body>
</html>