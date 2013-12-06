<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
<title>Selamat Datang di Website Pembayaran Smartfren | Halaman Login</title>

<script src="js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script src="js/util.js"></script>
<script>window.history.forward(0); </script>
<script src="js/login.js"></script>
<script src="js/hyperlinks.js"></script>


<link rel="stylesheet" type="text/css" href="css/ipg_payment.css" />
<link rel="stylesheet" type="text/css" href="css/util.css"  />
<link rel="icon" type="image/vnd.microsoft.icon" href="images/favicon.ico" />
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="images/favicon.ico" />

</head>

<body oncontextmenu="return false;">
<table width = "845" align = "center">
	<Tr> 
		<Td>
		<a href="http://www.smartfren.com"><img src="./images/smartfren-logo.png" style="border-style: none"></a> 
		</Td>
	</Tr>
</table>
<form name="loginForm" id="loginForm" action="./j_spring_security_check" onsubmit="return false;" method="post">

  <table width="740" align="center" border="0" cellpadding="2" cellspacing="0">
    <tr> 
      <td colspan="2"> <img src="images/Gunakan-isi-Ulang.png" /> </td>
    </tr>
    <tr> 
      <td width="319">&nbsp;</td>
      <td width="413" rowspan="5" align ="right" valign="top"><img src="images/CC2.jpg"/> 
      </td>
    </tr>
    <tr> 
      <td> <table background="images/m06.png" border="0" cellpadding="0"
			cellspacing="0" width="310">
          <tr> 
            <td> <table width="309" border="0" cellpadding="5" cellspacing="0"
					align="center">
                <tr> 
                  <td class="tbl_login" width="21%"><strong>Smartfren Number</strong></td>
                  <td width="79%" align="center" class="tbl_login"> 
                    <input id="j_username" name="j_username" size="30" type="text" />
                  </td>
                </tr>
                <tr> 
                  <td class="tbl_login"><b>Login PIN</b></td>
                  <td align="center" class="tbl_login"> 
                    <input id="j_password" name="j_password" size="30" type="password" />
                  </td>
                </tr>
                <tr align="right"> 
                  <td colspan="2"><a
							href="forgotPassword.jsp"
							target="_top" class="linkforgot"><b> Lupa Login PIN?</b></a></td>
                </tr>
                <tr align="right"> 
                  <td colspan="2"> <input class="bttn_submit" id="submitbutton" name="submitbutton" type="button" value="Login"  /> 
                    <input class="bttn_register" id="registerbutton" name="registerbutton" type="button" value="Registrasi" /></td>
                </tr>
                <tr> 
                  <td colspan="2">&nbsp;</td>
                </tr>
              </table></td>
          </tr>
        </table></td>
    </tr>
    <tr height="11"> 
      <td></td>
    </tr>
    <tr> 
      <td valign="top" align="right"> <img
			src="images/bayardengan.png" width="249" height="40" /></td>
    </tr>
    <tr> 
		<td valign="top" align="left"> 
			<span class="tbl_row2"><a id="TQ" class="link2">Pertanyaan Populer</a></span> &nbsp; 
			<span class="tbl_row2"><a id="HTR" class="link2">Cara Registrasi</a></span> &nbsp; 
			<span class="tbl_row2"><a id="HTT" class="link2">Cara Transaksi</a></span>
		</td>
    </tr>
  </table>
<!--<div align="center">
<input id="Amount" name="Amount" size="30" 	value="1234567" type="hidden"></div>-->
  <div align="center"><img src="images/logo_verisign.gif" align = "center" /> </div>
  <div class="footer">
  
<font size="1" color="#666666">2011 - Copyright PT. Smartfren Telecom | <a id="privacy" class="link"> Privacy Policy </a>&nbsp;&nbsp;<a id="TC" class="link">Syarat dan Ketentuan </a> 
<br>
This website is best viewed with IE (version 7 or above), Mozilla Firefox (version 3 or above), or Google Chrome
</font></div>
</form>
</body>
</html>