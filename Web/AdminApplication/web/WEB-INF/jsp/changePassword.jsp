<%--
    Document   : changePassword
    Created on : Oct 9, 2009, 4:36:28 PM
    Author     : sunil
--%>

<%@include file="../jspf/include.jspf" %>
<%@ page import="org.springframework.security.web.authentication.*" %>
<%@ page import="com.mfino.uicore.util.CookieStore" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
    
<html>
<style>
body{
	font:normal 12px Arial, Helvetica, sans-serif;
	color:#333;
}
</style>
    <head>
   		<!-- Mimic Internet Explorer 7 -->
   		<meta http-equiv="X-UA-Compatible" content="IE=7" >
        <title>Mobile Financial Services Platform - Change Password</title>
        <link rel="icon" type="image/vnd.microsoft.icon" href="http://www.mfino.com/images/favicon.ico">
        <%
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
        response.setDateHeader("Expires", 0);
        %>
    </head>

    <body>
        <%@include file="../jspf/header.jspf" %>
        <!-- UX -->
        <script type="text/javascript" src="<c:url value="/js/ux/RowAction/js/Ext.ux.grid.RowActions.js"/>">
        </script>
        <script type="text/javascript" src="<c:url value="/js/ux/RowAction/js/Ext.ux.Toast.js"/>">
        </script>

        <script type="text/javascript" src="<c:url value="/js/widget/ChangePassword.js" />">
        </script>
        <script type="text/javascript" src="<c:url value="/js/page/changePassword.js" />">
        </script>
        <script type="text/javascript" src="<c:url value="/js/init.js"/>" ></script>
        <script type="text/javascript" >
       		 ${oldPasswordRequired}
           </script>
          <script type="text/javascript" >
       		 ${promptPin}
           </script>  
        

        <script type="text/javascript">
            Ext.onReady(function(){
                mFino.page.changePassword({
                    renderTo: "changePasswordPanel"
                });

                var warning = _("Your current browser {0} is not supported. <br/>\
                Please upgrade your browser to the latest version of Internet Explorer or Firefox.");
                var warningTitle = _("Warning");

                if(Ext.isSafari || Ext.isSafari2 || Ext.isSafari3 ) {
                    Ext.ux.Toast.msg(warningTitle, String.format(warning, "Safari"), 5);
                } else if (Ext.isChrome) {
                    Ext.ux.Toast.msg(warningTitle, String.format(warning, "Chrome"), 5);
                } else if(Ext.isOpera) {
                    Ext.ux.Toast.msg(warningTitle, String.format(warning, "Opera"), 5);
                } else if(Ext.isWebKit) {
                    Ext.ux.Toast.msg(warningTitle, String.format(warning, "Safari"), 5);
                } else if(Ext.isIE6) {
                    Ext.ux.Toast.msg(warningTitle, String.format(warning, "IE 6"), 5);
                }
            });
        </script>
        <div id="changePassword_root_div" style="width:600px;background-color:#FFF; margin:0px auto;margin-top:60px;">
            <div class="box_frame" style="width:600px;background-color:#FFF;">
                <div style="height:auto;">
                    <img style="margin-top:60px;margin-left:25px;" alt="mFino" src="<c:url value="/resources/images/mFino.gif"/>"/>
                    <br><br>
                    <h2 align="left">&nbsp; &nbsp; &nbsp; &nbsp;&nbsp;Please change your password</h2>
                    <ul>
                        <li>&nbsp; &nbsp; &nbsp; &nbsp; Keep your new password secure. If must write it down, be sure to keep it in a safe place. </li>
                        <li>&nbsp; &nbsp; &nbsp; &nbsp; Your new password must meet the following requirements :   </li>
                    </ul>
                    <br>
                    <UL>
                        <li>&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; * Must be at least 6 characters long. </li>
                        <li>&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; * Must be no more than 40 characters long. </li>
                        <li>&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; * Must contain atleast one Number. </li>
                        <li>&nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; * Must contain atleast one capital letter. </li>
                    </UL>
                    <div id="changePasswordPanel" style="margin-top:20px;margin-left:25px;">

                    </div> <div id="footer" class="footer" style="width:100%;" >
                        Copyright © 2013 mFino Ltd. All rights reserved. Build 2.6
                    </div>
                    <div style="margin-bottom:20px;"></div>

                </div>
            </div>
        </div>
    </body>
</html>
