<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.mfino.util.ConfigurationUtil"%>
<%@ page pageEncoding="UTF-8" %>
<%
String mdn="";
Integer destinationLimit = ConfigurationUtil.getCCDestinationLimit();
Integer destinations =1;
if(request.getAttribute("mdn")==null){
	response.sendRedirect("login.jsp");
}else{
mdn = request.getAttribute("mdn").toString();
}
%>
<html>
<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Pragma" content="no-cache" />
<!-- Pragma content set to no-cache tells the browser not to cache the page
This may or may not work in IE -->
<meta http-equiv="Expires" content="0" />
<title>Selamat Datang di Portal Pembayaran Smartfren | Pendaftaran Akun Pembayaran Smartfren</title>
<script src="js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/util.js"></script>
<script type="text/javascript" src="js/registration.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.2.custom.min.js"></script>
<script src="js/hyperlinks.js"></script>

<link rel="stylesheet" type="text/css" href="css/jquery-ui-1.8.2.custom.css">
<link rel="stylesheet" href="css/util.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="css/ipg_payment.css">
<link rel="icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="./images/favicon.ico">
</head>
<body dir="ltr" onLoad="" alink="#ff0000" bgcolor="#ffffff"
link="#0000cc" text="#000000" vlink="#551a8b" oncontextmenu="return false;">

<div class="body" align="center">
<br><br>
<h2><font color="#666666">Pendaftaran Rekening Pembayaran Kartu Kredit Smartfren</font></h2>
<I><font size="-1">(Smartfren Credit Card Payment Registration)</font></I>
<form name="register" id="register" method="post" action="RegistrationServlet">
<!-- Hidden input field to figure out which page is the request coming from -->
<input type="hidden" name="requestType" value="registration"/>
<br>
<table width="680px" border="0" cellpadding="5" cellspacing="0" style="background:#fff url(images/m09.png) bottom no-repeat;">
	<tr>
		<td align="left" colspan="2">
			<span id="title_order">Harap lengkapi data dibawah ini untuk registrasi<br></span>
            <span class="gaia cca blue">Kolom yang diberi tanda <span class="gaia cca red">*</span> wajib dilengkapi <br>
          <font color="#666666" size="-1"><em>(Please Provide below details to 
          register, fields mark with * are mandatory)</em></font><br>
          </span>
		</td>
	</tr>
	<tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>Nama Depan<span class="gaia cca red">*<em><font color="#999999" size="-2">/First 
          Name </font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">

            <div><input name="firstname" id="firstname"
                    size="30" type="text"></div>
            </td>
    </tr>
    <tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>Nama Belakang<span class="gaia cca red"><em><font color="#999999" size="-2">/Last 
          Name </font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">

            <div><input name="lastname" id="lastname"
                    size="30" type="text"></div>
            </td>
    </tr>
     
    <tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>Nomor Smartfren<span class="gaia cca red">*<em><font color="#999999" size="-2">/Smartfren 
          Number </font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">

            <div><input name="username" id="username"
                    size="30" type="text" disabled="disabled" value=<%=mdn %>></div>
           <input name="username" id="username"
                    size="30" type="hidden" value=<%=mdn%>></input>
                    <input name="mdn" id="mdn"
                    size="30" type="hidden" value=<%=mdn%>></input>
            </td>
    </tr>
     
    <tr>
         <td align="left" class="tbl_row2" width="40%"><b>PIN<span class="gaia cca red">*</span></b></td>
         <td align="left" class="tbl_row2" width="60%"><input name="password"
                    id="password" size="30" type="password"></td>
    </tr>
    <tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>Konfirmasi PIN<span class="gaia cca red">*<em><font color="#999999" size="-2">/Confirm 
          PIN </font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">
            <div><input name="confirmPassword"
                    id="confirmPassword" size="30" type="password"></div>
            </td>
    </tr>
   <tr>
         <td align="left" class="tbl_row2" width="40%" valign="top"><b>Email<span class="gaia cca red">*</span></b></td>
         <td align="left" class="tbl_row2" width="60%">
			<div style="display: block;">
                                    <input name="email" id="email" size="30"
                                            type="text">
                                   <span class="gaia cca cmt">contoh:<i>username@email.com</i></span> </div>
    </tr>
    
    <tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>Tanggal Lahir<span class="gaia cca red">*</span></b> 
          <b><span class="gaia cca red"><em><font color="#999999" size="-2">/DOB</font></em></span></b><br />
        </td>
         <td align="left" class="tbl_row2" width="60%" valign="top">
			<input name="dob" id="dob" size="30" type="text">
			<span class="gaia cca cmt">contoh:<i>DD/MM/YYYY (25/12/2010)</i></span>
        </td>
	</tr>  
   
    
    <!--<tr>
<td colspan="2">&nbsp;</td>
</tr>-->
   <tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>Pertanyaan Rahasia<span class="gaia cca red">*</span></b> 
          <b><span class="gaia cca red"><em><font color="#999999" size="-2">/Secret 
          Question </font></em></span></b><br />
		 <span class="gaia cca cmt">(pertanyaan keamanan untuk lupa password)</span>
		 </td>
            <td><script type="text/javascript"><!--
function openWindow(url, w, h) {
var popupWin =
window.open(url, 'windowname',
'width=' + w + ', height=' + h + ', location=no, menubar=no, status=no, toolbar=no, scrollbars=yes, resizable=yes');
}
--></script>
            <script><!--
var chooseQuestionValue = "choosequestion";
var ownQuestionValue = "ownquestion";
var ownQuestionName =  "ownquestion";
var textSize = "53";


function updateOwnQuestionBox(value) {
var ownQuestionField = document.forms['register'].elements[ownQuestionName];
if (value == ownQuestionValue) {
ownQuestionField.style.display = 'block';
ownQuestionField.focus();

} else {
ownQuestionField.style.display = 'none';
}
}
-->
</script>
            <div><select id="securityQuestion"
                    name="securityQuestion"
                    onChange="updateOwnQuestionBox(this.value)">
                    <option value="choosequestion" style="font-style: italic;">
                    Choose a question ...</option>
                    <option value="What is your favorite color? ">What
                    is your favorite color?</option>
                    <option
                            value="What is the name of your best friend from childhood? ">What
                    is the name of your best friend from chidhood?</option>
                    <option value="What is your favorite food?">What
                    is your favorite food?</option>
                    <option
                            value="Who is your favorite movie star of all time? ">Who
                    is your favorite movie star of all time?</option>
                    <option value="Where was your father born? ">Where
                    was your father born?</option>
                    <option value="Where was your mother born? ">Where
                    was your mother born?</option>
                    <option selected="selected" style="font-style: italic;"
                            value="ownquestion">Make your own question</option>
                    
<script><!--
var lastIndex = "7";
var value = "ownquestion";
var label = "Make your own question";


document.forms['register'].securityQuestion.options[lastIndex] = new Option(label, value);
document.forms['register'].securityQuestion.options[lastIndex].style.fontStyle = 'italic';



-->
</script>
            </select>
            <div style="padding-top: 5px;"><input
                    name="ownquestion" id="ownQuestion" size="50"
                    style="display: block;" type="text"></div>
            <script>
<!--
updateOwnQuestionBox(document.forms['register'].securityQuestion.value);
-->
</script>
            </div>
            </td>
    </tr>
  <tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>Jawaban Pertanyaan Rahasia<span class="gaia cca red">*<em><font color="#999999" size="-2">/Secret 
          Answer </font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">
			<input name="securityAnswer" id="securityAnswer" size="50" autocomplete="off" type="text">
		</td>
	</tr>
	
    <tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>Telepon Rumah<span class="gaia cca red">*<em><font color="#999999" size="-2">/Home 
          Phone Number</font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">
			<input name="homephone" id="homephone" size="30" type="text">
		</td>
	</tr>
	<tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>Telepon Kantor<span class="gaia cca red">*<em><font color="#999999" size="-2">/Office 
          Phone Number</font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">
			<input name="workphone" id="workphone" size="30" type="text">
		</td>
	</tr>
    <!--- this variable would be dynamically changed based on num of cards available -->
    <tr>
		<td colspan="2" id="title_order" align="left">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2" id="title_order" align="left">Informasi Kartu Kredit <b><span class="gaia cca red"><em><font color="#666666" size="-1">/Credit 
          Card Details</font></em></span></b></td>
	</tr>
	<tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>6 Digit Pertama Kartu 
          Kredit<span class="gaia cca red">*<em><font color="#999999" size="-2">/First 
          6 Digit</font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">
			<input name="f6" id="f6" size="30" type="text">
		</td>
	</tr>	
	<tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>4 Digit Terakhir Kartu 
          Kredit<span class="gaia cca red">*<em><font color="#999999" size="-2">/Last 
          4 Digit</font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">
			<input name="l4" id="l4" size="30" type="text">
		</td>
	</tr>	
	<tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>Bank Penerbit Kartu<span class="gaia cca red">*<em><font color="#999999" size="-2">/Card 
          Issuer </font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%">
			<input name="bankName_1" id="bankName_1" size="30" type="text">
		</td>
	</tr>	
	<tr>
         
        <td align="left" class="tbl_row2" width="40%"><b>Nama pada Kartu<span class="gaia cca red">*<em><font color="#999999" size="-2">/Name 
          on Card</font></em></span></b></td>
         <td align="left" class="tbl_row2" width="60%"><input name="nameOnCard" id="nameOnCard" size="30" type="text"></td>
	</tr>	
	<tr>
		<td colspan="2" id="title_order" align="left">&nbsp;</font></span></td>
	</tr>  
    <tr>
		<td align="left" style="font-weight: bold;"><span class="gaia cca blue"><font colspan="2" id="title_order">Nomor 
          Tujuan<b><span class="gaia cca red"><em><font color="#666666" size="-1">/Destination 
          Smartfren Number </font></em></span></b></font></span></td>
		<td><input name="addmdn" id="addmdn" class="bttn_adddest" value="" type="button"></td>
	</tr>
	<tr>
		<td align="left" class="tbl_row2" width="40%"><b>Nomor Tujuan Default<span class="gaia cca red">*<em><font color="#999999" size="-2">/Default 
          Destination </font></em></span></b></td>
		<td align="left" class="tbl_row2" width="60%">
            <div><input name="destination1" id="destination1"
                    size="30" type="text" disabled="disabled" value=<%=mdn %>></div>
            </td>
    </tr>
<input type="hidden" name="destinationlimit" id="destinationlimit" value="<%=destinationLimit%>">
<input type="hidden" name="destinations" id="destinations" value="1">
<input type="hidden" name="numOfCards" id="numOfCards" value="1">
<tr>
		<td colspan="2" id="title_order" align="left">&nbsp;</font></span></td>
	</tr>  
	<tr>
		<td colspan="2" id="title_order" align="left">
			<table width="99%" border="0" cellpadding="5" cellspacing="0">
            <tr>
				
              <td align="left" colspan="2" style="font-weight: bold;"><font colspan="2" id="title_order"> 
                Alamat<span class="gaia cca blue"><font colspan="2" id="title_order"><b><span class="gaia cca red"><em><font color="#666666" size="-1">/Address</font></em></span></b></font></span></font></td>
              <td align="left" colspan="2" style="font-weight: bold;"><font colspan="2" id="title_order">Alamat 
                Penagihan<span class="gaia cca blue"><font colspan="2" id="title_order"><b><span class="gaia cca red"><em><font color="#666666" size="-1">/Billing 
                Address</font></em></span></b></font></span></font></td>
			</tr>
			<tr>
				
              <td width="17%" height="39" align="left" class="tbl_row2"><p><b>Alamat<span class="gaia cca red">* 
                  <em><font color="#999999" size="-2">/</font></em></span></b><span class="gaia cca red"><em><font color="#999999" size="-2">Address</font></em></span></p>
                </td>
				<td align="left" class="tbl_row2" width="30%">
				<input name="billingAddress_1" id="billingAddress_1" size="30" type="text">
				</td>
              <td align="left" class="tbl_row2" width="17%"><p><b>Alamat Penagihan<span class="gaia cca red">* 
                  </span></b><span class="gaia cca red"><em><font color="#999999" size="-2"> 
                  /Billing Address</font></em></span></p>
                </td>
				<td align="left" class="tbl_row2" width="36%">
				<input name="billingAddress_2" id="billingAddress_2" size="30" type="text">
				</td>
			</tr>
		    <tr> 
              <td align="left" class="tbl_row2" width="17%"><span class="gaia cca red"><em><font color="#999999" size="-2">(Line2)</font></em></span></td>
				<td align="left" class="tbl_row2" width="30%">
				<input name="AddressLine2_1" id="AddressLine2_1" size="30" type="text">
				</td>
              <td align="left" class="tbl_row2" width="17%"><span class="gaia cca red"><em><font color="#999999" size="-2">(Line2)</font></em></span></td>
				<td align="left" class="tbl_row2" width="36%">
				<input name="AddressLine2_2" id="AddressLine2_2" size="30" type="text">
				</td>
		    </tr>
			<tr>
				
              <td align="left" class="tbl_row2" width="17%"><b>Kota</b><span class="gaia cca red">* 
                <em><font color="#999999" size="-2"> /City</font></em></span></td>
				<td align="left" class="tbl_row2" width="30%">
				<input name="city_1" id="city_1" size="30" type="text">
				</td>
              <td align="left" class="tbl_row2" width="17%"><b>Kota</b><span class="gaia cca red">* 
                <em><font color="#999999" size="-2"> /City</font></em></span></td>
				<td align="left" class="tbl_row2" width="36%">
				<input name="city_2" id="city_2" size="30" type="text">
				</td>
		    </tr>
			<tr>
				
              <td align="left" class="tbl_row2" width="17%"><b>Wilayah</b><span class="gaia cca red">* 
                <em><font color="#999999" size="-2"> /State</font></em></span></td>
				<td align="left" class="tbl_row2" width="30%">
				<input name="state_1" id="state_1" size="30" type="text">
				</td>
              <td align="left" class="tbl_row2" width="17%"><b>Wilayah</b><span class="gaia cca red">* 
                <em><font color="#999999" size="-2"> /State</font></em></span></td>
				<td align="left" class="tbl_row2" width="36%">
				<input name="state_2" id="state_2" size="30" type="text">
				</td>
			</tr>
			<tr>
				
              <td align="left" class="tbl_row2" width="17%"><b>Kode Pos</b><span class="gaia cca red">* 
                <em><font color="#999999" size="-2"> /Postal Code</font></em></span></td>
				<td align="left" class="tbl_row2" width="30%">
				<input name="zipCode_1" id="zipCode_1" size="30" type="text">
				</td>
				
              <td align="left" class="tbl_row2" width="17%"><b>Kode Pos</b><span class="gaia cca red">*<em><font color="#999999" size="-2">/Postal 
                Code</font></em></span></td>
				<td align="left" class="tbl_row2" width="36%">
				<input name="zipCode_2" id="zipCode_2" size="30" type="text">
				</td>
		    </tr>
			</table>
		</td>
	</tr>
		<tr>
		<td colspan="2" align="right">
			<input id="registerbutton" name="registerbutton" class="bttn_change" value="Submit" type="SUBMIT"> 
			<!--<input id="resetbutton" name="resetbutton" class="bttn_reset" value="Reset" type="reset">-->
			<input id="MoveBack" name="MoveBack" class="bttn_cancel" value="Batal" type="reset">
		</td>
    </tr>
</table>
</form>
<%@include file="footer.jspf"%>
</div>
<%
	String msgForDisclaimer = ConfigurationUtil.getRegisterCCSubscriberDisclaimer();
	Integer expirationTime = ConfigurationUtil.getCreditcardRegistrationExpirationTimeInHrs();
	msgForDisclaimer = StringUtils.replace(msgForDisclaimer, "$(expirationTime)", expirationTime.toString());
	String destinationLimitAlert = "You are allowed to add up to <br>"+destinationLimit+" destinations only";
	
%>
<div id="dialog" style="display: none;" title="Disclaimer"><p><font size="-2"><i><%=msgForDisclaimer%></i></font></div>
<div id="destlimit" style="display: none;" title="Destination Limit"><p><font size="2"><%=destinationLimitAlert%></font></div>

</body>
</html>