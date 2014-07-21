<%@include file="../jspf/include.jspf" %>
<%@ page import="org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter" %>
<%@ page import="org.springframework.security.core.AuthenticationException" %>


<html>
    <head>
    	<!-- Mimic Internet Explorer 7 -->
   		<meta http-equiv="X-UA-Compatible" content="IE=7" >
        <title>mFino | the Mobile Financial Services Platform - Login</title>
        <link rel="icon" type="image/vnd.microsoft.icon" href="resources/images/favicon.png">
        <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="resources/images/favicon.png">
        <%
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
            response.setHeader("Cache-Control", "no-store"); // HTTP 1.1
            response.setDateHeader("Expires", 0);
        %>
    </head>

    <body>
        <%@include file="../jspf/header.jspf" %>
        <script type="text/javascript" src="<c:url value="/js/widget/MerchantRegistration.js"/>">
        </script>
         <%--<script type="text/javascript" src="<c:url value="/js/widget/MerchantRegistrationForm.js"/>">
        </script>
        <script type="text/javascript" src="<c:url value="/js/widget/MerchantRegistrationPasswordForm.js"/>">
        </script> --%>
        <script type="text/javascript" src="<c:url value="/js/widget/ForgotPasswordForm.js"/>">
		</script>

        <!-- UX -->
        <script type="text/javascript" src="<c:url value="/js/ux/RowAction/js/Ext.ux.grid.RowActions.js"/>">
        </script>
        <script type="text/javascript" src="<c:url value="/js/ux/RowAction/js/Ext.ux.Toast.js"/>">
        </script>

        <script type="text/javascript" src="<c:url value="/js/widget/Login.js" />">
        </script>
        <script type="text/javascript" src="<c:url value="/js/page/login.js" />">
        </script>
        <script type="text/javascript" src="<c:url value="/js/init.js"/>" ></script>

        <script type="text/javascript">
        var win = new Ext.Window({
      	  title: 'Warning',
      	  height: 150,
          width: 410,
          preventBodyReset: true,
      	  html: 'Your current browser IE 6# is not supported.<br/>\
      		  mFino application screens may not display or function properly.<br />\
      		  Please upgrade your browser to the latest version of Internet Explorer 8 or Firefox.'
      	});    
        Ext.onReady(function(){

                var warning = _("Your current browser ")+ "{0}" + _(" is not supported. <br/>\
                 mFino application screens may not display or function properly. <br>\
                 Please upgrade your browser to the latest version of Internet Explorer 8 or Firefox.");
                var warningTitle = _("Warning");
                Ext.isIE6 = Ext.isIE && (/msie 6/).test(navigator.userAgent.toLowerCase());
                
                if(Ext.isSafari) {
                    if(Ext.isSafari2){
                       // Ext.ux.Toast.msg(warningTitle, String.format(warning, "Safari 2#"), 5);
                    	win.show();
                    }
                    else if(Ext.isSafari3){
                        //Ext.ux.Tast.msg(warningTitle, String.format(warning, "Safari 3#"), 5);
                    	win.show();
                    }else if(Ext.isSafari4){
                        //Ext.ux.Toast.msg(warningTitle, String.format(warning, "Safari 4#"), 5);
                    	win.show();
                    }else {
                        //Ext.ux.Toast.msg(warningTitle, String.format(warning, "Safari"), 5);
                    	win.show();
                    }
                } else if (Ext.isChrome) {
//                    Ext.ux.Toast.msg(warningTitle, String.format(warning, "Chrome"), 5);
                //	win.show();
                	mFino.page.login({
                        renderTo: "loginPanel"
                    });
                } else if(Ext.isOpera) {
                    //Ext.ux.Toast.msg(warningTitle, String.format(warning, "Opera"), 5);
                	win.show();
                } else if(Ext.isWebKit) {
                    //Ext.ux.Toast.msg(warningTitle, String.format(warning, "Safari"), 5);
                	win.show();
                } else if(Ext.isIE6) {
                   // Ext.ux.Toast.msg(warningTitle, String.format(warning, "IE 6#"), 5);
                    win.show();                    
                } /* else if(Ext.isIE7) {
                    Ext.ux.Toast.msg(warningTitle, String.format(warning, "IE 7#"), 5);
                } else if(Ext.isIE8) {
                    Ext.ux.Toast.msg(warningTitle, String.format(warning, "IE 7#"), 5);
                } */else{                        	
                	mFino.page.login({
                        renderTo: "loginPanel"
                    });
                }
                
                if('<%=request.getParameter("sessionExpired")%>'=='true'){ //added for #2081
                	alert("The session is expired due to timeout or because the same user logged in on another system");
                }
                
            });
        </script>
        <div id="root_div">
            <div class="box_frame_login">
                <div class="box_middle_top_login"  >
                  <div class="company_logo" style="float:left"></div>
                  <div class="mfino_logo"  style="float:right"></div>
                </div>
                <div class="box_big2_middle_login">
                    <div class="white_header">
                        <div class="top_pan">
                        </div>
        <div id="login_root_div">

                            <div style="height:auto;"><img style="float:right; margin-right:30px;margin-top:0px;height:291px; width:584px;"alt="image" src="<c:url value="/resources/images/login-image.png"/>"/>
                                <div id="loginPanel" style="margin-top:60px;margin-left:30px;">
                                
         <!--   <div class="box_frame" style="width:600px;background-color:#FFF;">
                <div style="height:auto;">
                    <img style="margin-top:60px;margin-left:25px;" alt="mFino" src="<c:url value="/resources/images/mFino.gif"/>"/>
                    <div id="loginPanel" style="margin-top:20px;margin-left:25px;"> -->
                    </div>
                   <!-- <img style="float:right; margin-right:20px;margin-top:-225px;"alt="smart" src="<c:url value="/resources/images/mfino_lady.gif"/>"/>
                    <div style="margin-bottom:20px;"></div>
                    <div id="footer" class="footer" style="width:100%;" >
                        Copyright © 2010 mFino, Inc. All rights reserved. Build 2.0
                    </div> -->
                    <div id="main" style="float:left;"></div>
                </div>
            </div>
        </div>
        </div>
                <div class="box_middle_bottom_login"></div>
                <%--<img style="float:right; margin-right:20px;margin-top:-225px;"alt="smart" src="<c:url value="/resources/images/background-5.jpg"/>"/>--%>
                <div style="margin-bottom:20px;"></div>
                <div id="footer_login" ><br>Copyright © 2014 mFino Ltd. All rights reserved.</div>
            </div>
        </div>
    </body>
</html>
