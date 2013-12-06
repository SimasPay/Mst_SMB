<%-- 
    Document   : index
    Created on : May 11, 2011, 11:26:50 AM
    Author     : Srinu
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <center>
            <h1>SMS Alert Test</h1>
            <form action="dynamic/test" method="POST">
                <table>
                    <tr>
                        <td>Short code</td>
                        <td><input type="text" name="shortcode" value="" /></td>
                    </tr>
                    <tr>
                        <td>Dest MDN</td>
                        <td><input type="text" name="to" value="" /></td>
                    </tr>
                    <tr>
                        <td>Message</td>
                        <td><input type="text" name="message" value="" /></td>
                    </tr>
                    <tr>
                        <td>Partner ID</td>
                        <td><input type="text" name="partnerID" value="" /></td>
                    </tr>
                    <tr>
                        <td>API token</td>
                        <td><input type="text" name="apiToken" value="" /></td>
                    </tr>
                    <tr>
                        <td colspan="2"><input type="submit"  value="submit" /></td>
                    </tr>
                </table>
            </form>
        </center>
    </body>
</html>
