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

<form name="loginForm" id="loginForm" action="./j_spring_security_check" onsubmit="return false;" method="post">

<table width="845" align="center" border="0" cellpadding="2" cellspacing="0">
	
	<tr>
		<td><img src="images/login-banner.png" width="310" height="41" /></td>
		<td width="8">&nbsp;</td>
		<td width="484" rowspan="5" valign="top">
		
		<table width="484" height="343" bordercolor="#CCCCCC">
			<tr>
				<td height="180" align="right" valign="top"><img src="images/online_payment2.jpg" width="484" /></td>
			</tr>
			
			<tr>
				<td>
				<table width="484">
					<tr>
						<td><img src="images/barudisini2.png" /></td>
					</tr>
					<tr>
						<td>
						<table bordercolor="#6699CC" border="-1">
							<tr>
								<td><span class="get_start">1. Registrasi</span> </br>
								Hanya butuh beberapa menit dan gratis! <span class="get_start"></br>
								</br>
								2. Login </span></br>
								Login dengan PIN yang Anda buat sendiri <span class="get_start">
								</br>
								</br>
								3. Transaksi </span> </br>
								Isi Ulang Pulsa dan Bayar Tagihan Aman dan Nyaman dengan Kartu
								Kredit Anda</td>
							</tr>
						</table>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td>
		<table background="images/m06.png" border="0" cellpadding="0"
			cellspacing="0" width="310">
			<tr>
				<td>
				<table width="310" border="0" cellpadding="5" cellspacing="0"
					align="center">
					<tr>
						<td colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td class="tbl_login" width="20%"><b>Smartfren Number</b></td>
						<td class="tbl_login" width="80%">
						<input id="j_username" name="j_username" size="30" type="text" autocomplete="off" /></td>
					</tr>
					<tr>
						<td class="tbl_login"><b>PIN</b></td>
						<td class="tbl_login">
						<input id="j_password" name="j_password" size="30" type="password" autocomplete="off"/></td>
					</tr>
					<tr>
						<td colspan="2">&nbsp;</td>
					</tr>
					<tr align="right">
						<td colspan="2">
						<input class="bttn_submit" id="submitbutton" name="submitbutton" type="button" value="Login"  /> 
						<input class="bttn_register" id="registerbutton" name="registerbutton" type="button" value="Registrasi" /></td>
					</tr>
					<tr>
						<td colspan="2"><a
							href="forgotPassword.jsp"
							target="_top" class="linkforgot"><b>Lupa PIN?</b></a></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr height="11">
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td valign="top" align="right"><img src="images/verisign.gif"
			width="66" height="27" align="top" /> <img
			src="images/bayardengan.png" width="249" height="40" /></td>
		<td rowspan="2"></td>
	</tr>
	<tr>
		<td valign="top" align="right"></br>
		</br>
		<a id="TQ" class="link2">Pertanyaan Populer</a> &nbsp; 
		<a id="HTR" class="link2">Cara Registrasi</a> &nbsp;
		 <a id="HTT" class="link2">Cara Transaksi</a></td>
	</tr>
</table>
<!--<div align="center">
<input id="Amount" name="Amount" size="30" 	value="1234567" type="hidden"></div>-->
<div class="footer">
<font size="1" color="#666666">© 2011-Copyright © 2009 PT. Smartfren Telecom |<a id="privacy" class="link"> Privacy Policy </a>&nbsp;&nbsp;<a id="TC" class="link">Syarat dan Ketentuan </a></font></div>
</form>
</body>
</html>