<%@ page import="com.mfino.service.impl.UserServiceImpl" %>
<%@ page import="com.mfino.uicore.util.CacheBuster" %>

<%@ include file="../jspf/include.jspf" %>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
   		 <!-- Mimic Internet Explorer 7 -->
   		<meta http-equiv="X-UA-Compatible" content="IE=7" >
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">        
        <title>mFino | the Mobile Financial Services Platform</title>
        <link rel="icon" type="image/vnd.microsoft.icon" href="resources/images/favicon.png">
        <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="resources/images/favicon.png">
        <%
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setHeader("Cache-Control", "no-store"); // HTTP 1.1
        response.setDateHeader("Expires", 0);
%>
    </head>

    <body>
        <div id="loading-mask"></div>
        <div id="loading">
            
            <p style="align:center; width:100%">
            <img alt="loading" src="resources/images/mfino_logo.png"/>
              <br>
                <img style="margin-top:10px;" src="<c:url value="resources/images/ajax-loader.gif"/>"/>
           </p>     
            
        </div>
        <%@include file="/WEB-INF/jspf/header.jspf" %>
        <div id="root_div">
            <div class="box_frame">
                <div class="box_middle_top">
                    &nbsp;
                </div>
                <div class="box_big2_middle">
                    <div class="white_header">
                        <div class="top_pan">                            
                            <ul>
                                <li><img class="smart_logo" alt="Zenith" src="<c:url value="/resources/images/mfino_logo.png"/>"/></li>
                                
                                <li>
                                    <a href="<c:url value="/j_spring_security_logout"/>">Logout</a>
                                </li> 
                                          
                                <li>|</li>
                                 <li>
                                    <div style="cursor:pointer" onclick="changePassword({oldPasswordRequired: true})">Change Password</div>
                                </li>
                                 <li>|</li>
                                <li><b><c:out value="${userString}"/></b></li><br/>
                                <li></li>
                                <li> IP: <c:out value="${ip}"/></li>
                                <li>|</li>
                                <li><script type="text/javascript">
                                    var d=new Date()
                                    var month=["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"]
                                    var myDays= ["Sun","Mon","Tue","Wed","Thu","Fri","Sat","Sun"]
                                    var year = d.getFullYear()
                                    document.write(myDays[d.getDay()]+ ", "+ month[d.getMonth()]+" "+ d.getDate() + ", " + year);
                                    </script>
                                </li><li>|</li><li>
                                    <script type="text/javascript">
                                        var currentTime = new Date()
                                        var hours = currentTime.getHours()
                                        var minutes = currentTime.getMinutes()
                                        if (minutes < 10){
                                            minutes = "0" + minutes
                                        }
                                        document.write("Login time : " + hours + ":" + minutes + " ")
                                    </script>
                                </li>
                                 
                            </ul>
                        </div>
                        <img class="logo" alt="mFino" src="<c:url value="/resources/images/mFino.png"/>"/>

                        <%@include file="../jspf/menu.jspf" %>

                        <div id="main" style="float:left;"></div>
                    </div>
                </div>
            </div>
            <div class="box_middle_bottom"></div>
            <div id="footer">Copyright © 2014 mFino Ltd. All rights reserved.</div>
        </div>

        <!-- Insert I18N java script link -->
        <!-- <script type="text/javascript" src="<c:url value="${i18nJSPath}"/>"></script>-->

        <%@ include file="/WEB-INF/jspf/footer.jspf" %>

        <script type="text/javascript" src="${authFileName}" >
        </script>

        <SCRIPT type="text/javascript">
            Ext.onReady(function(){            	
            	Ext.Ajax.on("beforerequest", function(con, opt){
            		opt.method = "POST";
            		if(opt.xmlData){
            			opt.params = {'data': opt.xmlData};
            		}
                    Ext.applyIf(opt.params,{'userName': mFino.auth.getUsername()});
                });
            	
                mFino.page.main({
                    width : 926,
                    height : 700,
                    mainRenderTo : "main",
                    dataUrl : "<c:url value='/fix.htm'/>"
                });

                setTimeout(function(){
                    Ext.get('loading').remove();
                    Ext.get('loading-mask').fadeOut({remove:true});
                }, 100);
                
                if('${changePassword}'=='true'){
                	changePassword({promptPin: '${promptPin}', removeCancelButton: true});
                }
            });        
            
            
            function changePassword(config){ //added for #2311
            	var win = new Ext.Window({ 
            		title: 'Change Password',
            		modal: true,
            		closable:false,
            		resizable: false,
            		id : 'changePasswordWindow',
            		items : new mFino.widget.ChangePasswordWindow(config)
            	}); 
                win.show();
            } 
        </SCRIPT>
    </body>
</html>
