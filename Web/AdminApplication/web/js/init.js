/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino");
Ext.QuickTips.init();

/*Global Variables*/
var SYSTEM_DEFAULT_CURRENCY = "IDR";
var SYSTEM_DEFAULT_TIMEZONE = "West_Indonesia_Time";
var SYSTEM_DEFAULT_KYC = "3";
var EMONEY_ENABLED = true;
var SYSTEM_DEFAULT_ACCOUNT_NUMBER_MANDATORY = false;
var IS_EMAIL_MANDATORY = true;

/*I18N support*/
_ = function(messageKey){
    if(mFino.msg && mFino.msg[messageKey]){
        return mFino.msg[messageKey];
    }else{
        return messageKey;
    }
};

markMandatoryFields = function(form){
    Ext.each(form.items.items, function (item) {
        if (item.allowBlank === false) {
            var color = '#8B0000';
            item.fieldLabel = '<span style="color:'+color+';">*</span>'+item.fieldLabel;
        }
    });
};
getUTCdate =  function(startDateSearch){
        var date = new Date();
        if(!startDateSearch){
            return null;
        }
        var startdate = startDateSearch.split("-")[0];
        var starttime = startDateSearch.split("-")[1];
        var difftime = starttime.split(":");
        var year = startdate.substring(0,4);
        var month =startdate.substring(4,6);
        month-=1;
        var day =startdate.substring(6,8);
        date.setFullYear(year, month, day);
//        date.setYear(year);
//        date.setMonth(month-1);
//        date.setDate(day);

        date.setHours(difftime[0], difftime[1], difftime[2], difftime[3]);
        var newdate = ""+date.getUTCFullYear()+"";
        if((date.getUTCMonth()+1)<10){
            newdate +=("0"+  (date.getUTCMonth()+1)+"");
        }else{
            newdate +=  ((date.getUTCMonth()+1)+"");
        }
        if(date.getUTCDate()<10){
            newdate +=("0"+date.getUTCDate());
        }else{
            newdate +=date.getUTCDate();
        }
        newdate += ( '-'+date.getUTCHours()+':'+date.getUTCMinutes()+':');

        if((date.getUTCSeconds()) < 9){
            newdate += ("0"+ date.getUTCSeconds());
        }else{
            newdate+=date.getUTCSeconds();
        }

        newdate +=  (':'+date.getUTCMilliseconds());
        return newdate;
    };
//Global configuration constants 
var Config = {
    grid_no_data: _("<br/><br/><center><div class='no_data'>No results found...</div></center>")
};

//force all DisplayField and default grid columns to do htmlEncode,
//to prevent scripting and html renderring problem
Ext.override(Ext.form.DisplayField, { 
    htmlEncode : true
});
Ext.override(Ext.grid.Column, {
    renderer : function(value){
        value = Ext.util.Format.htmlEncode(value);
        if(Ext.isString(value) && value.length < 1){
            return ' ';
        }
        return value;
    }
});


//FIXME: Find out the exact pattern and change this
//Ext.form.VTypes['smarttelcophoneVal'] = /^(6288|088)[\d]{1,12}(R\d*)?$/;
// This is for search MDN. we are including 'R' digit here.
Ext.form.VTypes['smarttelcophoneVal'] = /^([1-9])[\d]{3,14}(R\d*)?$/;
Ext.form.VTypes['smarttelcophoneMask'] = /[R\d]/;
Ext.form.VTypes['smarttelcophoneText'] = 'Phone number should be number only and Phone number must not start with 0';
Ext.form.VTypes['smarttelcophone'] = function(v){
    return Ext.form.VTypes['smarttelcophoneVal'].test(v);
};

// This is for adding new MDN. we are excluding 'R' digit here.
Ext.form.VTypes['smarttelcophoneAddVal'] = /^([1-9])[\d]{9,12}$/;
Ext.form.VTypes['smarttelcophoneAddMask'] = /[\d]/;
Ext.form.VTypes['smarttelcophoneAddText'] = 'Phone number must not start with 0 and should be 10 to 13 digits long';
Ext.form.VTypes['smarttelcophoneAdd'] = function(v){
    return Ext.form.VTypes['smarttelcophoneAddVal'].test(v);
};

//This is for adding new MDN - with few more validations. we are excluding 'R' digit here.
Ext.form.VTypes['smarttelcophoneAddMoreVal'] = /^([1-9])[\d]{9,12}$/;
Ext.form.VTypes['smarttelcophoneAddMoreMask'] = /[\d]/;
Ext.form.VTypes['smarttelcophoneAddMore'] = function(v, field){
	if(!Ext.form.VTypes['smarttelcophoneAddMoreVal'].test(v)) {
		this.smarttelcophoneAddMoreText = "Phone number must not start with 0 and should be 10 to 13 digits long";
		return false;
	}
	var valmdn=/^[2]{1}[3]{1}[4]{1}[0-9]{10}$/;
	var valmdn1=/^[2]{1}[3]{1}[4]{1}[0-9]{7}$/;
	var mdn = field.getValue();
	if(mdn.length==13)
	{
		if(!valmdn.test(mdn))
		{
			this.smarttelcophoneAddMoreText = "MDN start with 234";
			return false;
		}
	}else if(mdn.length>10){
		this.smarttelcophoneAddMoreText = "MDN starting with 234 should be 13 digits or 10 digits";
		return false;
	}
	else{
		if(valmdn1.test(mdn))
		{
			this.smarttelcophoneAddMoreText = "MDN should be 13 digits";
			return false;
		}
	}
	return true;    
};

Ext.form.VTypes["numbercharVal"] = /^[0-9a-zA-Z' ']{0,255}$/;
Ext.form.VTypes["numbercharText"]="should not exceed 255 characters";
Ext.form.VTypes["numbercharMask"]=/[0-9a-zA-Z' ']/;
Ext.form.VTypes["numberchar"]=function(v){
    return Ext.form.VTypes["numbercharVal"].test(v);
};
Ext.form.VTypes["numbercharothersVal"] = /^[\-\.\/\\a-zA-Z0-9' '\#]{0,255}$/;
Ext.form.VTypes["numbercharothersText"]="should not exceed 255 characters";
Ext.form.VTypes["numbercharothersMask"]=/[\-\.\/\\a-zA-Z0-9' '\#]/;
Ext.form.VTypes["numbercharothers"]=function(v){
    return Ext.form.VTypes["numbercharothersVal"].test(v);
};
Ext.form.VTypes["nameVal"] = /^[0-9a-zA-Z' ']{0,255}$/;
Ext.form.VTypes["nameText"]="should not exceed 255 characters";
Ext.form.VTypes["nameMask"]=/[0-9a-zA-Z' ']/;
Ext.form.VTypes["name"]=function(v){
    return Ext.form.VTypes["nameVal"].test(v);
};
Ext.form.VTypes['namesVal'] = /^.{1,255}$/;
Ext.form.VTypes['namesMask'] = /./;
Ext.form.VTypes["namesText"]="should not exceed 255 characters";
Ext.form.VTypes["names"]=function(v){
    return Ext.form.VTypes["namesVal"].test(v);
};
Ext.form.VTypes["REVal"] = /^[0-9\$\^\-,\[\]\{\}]{0,255}$/;
Ext.form.VTypes["REText"]="doesnot allow alphabets";
Ext.form.VTypes["REMask"]=/[\d\$\^\-,\[\]\{\}]/;
Ext.form.VTypes["RE"]=function(v){
    return Ext.form.VTypes["REVal"].test(v);
};
Ext.form.VTypes["number19Val"] = /^[0-9]{1,19}$/;
Ext.form.VTypes["number19Text"]="should be numeric and not exceed 19 characters";
Ext.form.VTypes["number19Mask"]=/[\d]/;
Ext.form.VTypes["number19"]=function(v){
    return Ext.form.VTypes["number19Val"].test(v);
};

Ext.form.VTypes["number8Val"] = /^[0-9]{0,8}$/;
Ext.form.VTypes["number8Text"]="should be numeric and not exceed 8 characters";
Ext.form.VTypes["number8Mask"]=/[\d]/;
Ext.form.VTypes["number8"]=function(v){
    return Ext.form.VTypes["number8Val"].test(v);
};
Ext.form.VTypes["pinVal"] = /^[0-9]{4}$/;
Ext.form.VTypes["pinText"]="should be numeric and 4 digits long";
Ext.form.VTypes["pinMask"]=/[\d]/;
Ext.form.VTypes["pin"]=function(v){
    return Ext.form.VTypes["pinVal"].test(v);
};
Ext.form.VTypes["number16Val"] = /^[0-9]{0,16}$/;
Ext.form.VTypes["number16Text"]="should be positive numeric and not exceed 16 characters";
Ext.form.VTypes["number16Mask"]=/[\d]/;
Ext.form.VTypes["number16"]=function(v){
    return Ext.form.VTypes["number16Val"].test(v);
};
Ext.form.VTypes["number10Val"] = /^[0-9]{0,10}$/;
Ext.form.VTypes["number10Text"]="should be numeric and not exceed 10 characters";
Ext.form.VTypes["number10Mask"]=/[\d]/;
Ext.form.VTypes["number10"]=function(v){
    return Ext.form.VTypes["number10Val"].test(v);
};
Ext.form.VTypes["number3Val"] = /^[0-9]{3}$/;
Ext.form.VTypes["number3Text"]="should be numeric and exactly 3 characters";
Ext.form.VTypes["number3Mask"]=/[\d]/;
Ext.form.VTypes["number3"]=function(v){
    return Ext.form.VTypes["number3Val"].test(v);
};
Ext.form.VTypes["tendigitnumberVal"] = /^[0-9]{10}$/;
Ext.form.VTypes["tendigitnumberText"]="should be numeric and exactly 10 characters";
Ext.form.VTypes["tendigitnumberMask"]=/[\d]/;
Ext.form.VTypes["tendigitnumber"]=function(v){
    return Ext.form.VTypes["tendigitnumberVal"].test(v);
};
Ext.form.VTypes["signedNumber16Val"] = /^[\-+0-9]{0,16}$/;
Ext.form.VTypes["signedNumber16Text"]="should be numeric and not exceed 16 characters";
Ext.form.VTypes["signedNumber16Mask"]=/[\d\-+]/;
Ext.form.VTypes["signedNumber16"]=function(v){
    return Ext.form.VTypes["signedNumber16Val"].test(v);
};


Ext.form.VTypes["number20Val"] = /^[0-9]{0,20}$/;
Ext.form.VTypes["number20Text"]="should be numeric and not exceed 20 characters";
Ext.form.VTypes["number20Mask"]=/[\d]/;
Ext.form.VTypes["number20"]=function(v){
    return Ext.form.VTypes["number20Val"].test(v);
};
Ext.form.VTypes["number11Val"] = /^[0-9]{0,11}$/;
Ext.form.VTypes["number11Text"]="should be numeric and not exceed 11 characters";
Ext.form.VTypes["number11Mask"]=/[\d]/;
Ext.form.VTypes["number11"]=function(v){
    return Ext.form.VTypes["number11Val"].test(v);
};
Ext.form.VTypes["numberVal"] = /^[0-9]{0,255}$/;
Ext.form.VTypes["numberText"]="should be positive number and not exceed 255 characters";
Ext.form.VTypes["numberMask"]=/[\d]/;
Ext.form.VTypes["number"]=function(v){
    return Ext.form.VTypes["numberVal"].test(v);
};

Ext.form.VTypes["usernameVal"] = /^[a-zA-Z][\-_.a-zA-Z0-9]{4,30}$/;
Ext.form.VTypes["usernameText"]="Should start with a character, cannot contain any special characters other than -_. and not exceed 30 characters ";
Ext.form.VTypes["usernameMask"]=/[\d]/;
Ext.form.VTypes["usernamechk"]=function(v){
    return Ext.form.VTypes["usernameVal"].test(v);
};
Ext.form.VTypes["numbercommaVal"] = /^[\,0-9]{0,24}$/;
Ext.form.VTypes["numbercommaText"]="should be number and not more than 18";
Ext.form.VTypes["numbercommaMask"]=/[\d]/;
Ext.form.VTypes["numbercomma"]=function(v){
    return Ext.form.VTypes["numbercommaVal"].test(v);
};
Ext.form.VTypes["smsnumbercommaVal"] = /^[\,0-9]{0,50}$/;
Ext.form.VTypes["smsnumbercommaText"]="should be number and value should be separated by , and not more than 50 chars";
Ext.form.VTypes["smsnumbercommaMask"]=/[\,\d]/;
Ext.form.VTypes["smsnumbercomma"]=function(v){
    return Ext.form.VTypes["smsnumbercommaVal"].test(v);
};


Ext.form.VTypes["ipAddressVal"]= /^([1-9][0-9]{0,1}|1[013-9][0-9]|12[0-689]|2[01][0-9]|22[0-3])([.]([1-9]{0,1}[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])){2}[.]([1-9][0-9]{0,1}|1[0-9]{2}|2[0-4][0-9]|25[0-4])$/;
Ext.form.VTypes["ipAddressText"]="should be number and value should be separated by .";
Ext.form.VTypes["ipAddressMask"]=/[\.\d]/;
Ext.form.VTypes["ipAddress"]=function(v){
    return Ext.form.VTypes["ipAddressVal"].test(v);
};


Ext.form.VTypes["IntegrationIPAddressVal"]= /^(([1-9][0-9]{0,1}|1[013-9][0-9]|12[0-689]|2[01][0-9]|22[0-3])([.]([1-9]{0,1}[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])){2}[.]([1-9]{0,1}[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-4]))$|^(0.0.0.0)$/;
Ext.form.VTypes["IntegrationIPAddressText"]="should be number and value should be separated by .";
Ext.form.VTypes["IntegrationIPAddressMask"]=/[\.\d]/;
Ext.form.VTypes["IntegrationIPAddress"]=function(v){
    return Ext.form.VTypes["IntegrationIPAddressVal"].test(v);
};


Ext.apply(Ext.form.VTypes, {
    validatePassword: function(value, field)
    {
        if (field.initialPasswordField)
        {
            var pwd = Ext.getCmp(field.initialPasswordField);
            this.validatePasswordText = 'Confirmation does not match your new password .';
            return (value === pwd.getValue());
        }
        
        var hasLength = ((value.length >= 6) && (value.length <=40));
        if(!hasLength){
            if(value.length < 6){
                this.validatePasswordText='Password should have Minimum 6 characters';
            }else if(value.length > 40){
                this.validatePasswordText='Password cannot have more than 40 characters';
            }
            return false;
        }
        //var hasSpecial = value.match(/[!@#\$%\^&\*\(\)\-_=\+]+/i);
      
        var hasnumber  = value.match(/[0-9]+/i);
        if(!hasnumber){
            this.validatePasswordText='Password should have atleast one Number';
            return false;
        }
        
        var hasCaps  = value.match(/[A-Z]+/);
        if(!hasCaps){
            this.validatePasswordText='Password should have atleast one Capital letter';
            return false;
        }
       
        if (field.notEqualField)
        {
            var pwd = Ext.getCmp(field.notEqualField);
            this.validatePasswordText = 'New password must not match oldpassword.';
            return (pwd?value !== pwd.getValue():true);
        }

        return true;
    }//,
//passwordText: 'Passwords must be at least 5 characters, containing either a number, or a valid special character (!@#$%^&*()-_=+)'
});

Ext.apply(Ext.form.VTypes, {
    yearCheck: function(value)
    {
        if(value < 0){
            this.yearCheckText='Year cannot be negative.';
            return false;
        }
        return true;
    }
});

Ext.form.VTypes["luhn_checkText"]=_("invalid bank card number");
Ext.form.VTypes["luhn_checkMask"]=/[\d]/;
Ext.form.VTypes["luhn_check"]=function(number) {
    //<![CDATA[
    // Strip any non-digits (useful for credit card numbers with spaces and hyphens)
    number=number.replace(/\D/g, '');

    // Set the string length and parity
    var number_length=number.length;
    var parity=number_length % 2;

    // Loop through each digit and do the maths
    var total=0;
    for (i=0; i < number_length; i++) {
        var digit=number.charAt(i);
        // Multiply alternate digits by two
        if (i % 2 == parity) {
            digit=digit * 2;
            // If the sum is two digits, add them together (in effect)
            if (digit > 9) {
                digit=digit - 9;
            }
        }
        // Total up the digits
        total = total + parseInt(digit, 10);
    }

    // If the total mod 10 equals 0, the number is valid
    if (total % 10 === 0) {
        return true;
    } else {
        return false;
    }

//]]>
};

/* session timeout handling */
Ext.Ajax.on("requestcomplete", function(conn, response, options){
    //We can do nicer things here, like showing a login window and let
    //the user login on the same screen. But now we simply redirects the
    //user to the login page.
	if(response.responseText.indexOf("<html>") > 0){
        window.location = mFino.BASE_URL + "login.htm?sessionExpired=true";
        return;
    }
	if(response.responseText.indexOf("Session Expired") > 0){ //added for #2081
		window.location = mFino.BASE_URL + "login.htm?sessionExpired=true";
		return;
	}
});

//I18N money and time format handling
Ext.apply(Ext.util.Format, {
    dateFormat: 'm/d/Y h:i:s A',
    moneyFormat: '0,000.00',
    addPercentage : '0.00%',
    date: function(v, format){
        if(!v){
            return '';
        }
        if(!Ext.isDate(v)){
            v = new Date(Date.parse(v));
        }
        return v.dateFormat((typeof format == 'string') ? format : Ext.util.Format.dateFormat);
    },
    money: function(v){
        return Ext.util.Format.number(v, Ext.util.Format.moneyFormat);
    },
    percentage: function(v){
        return Ext.util.Format.number(v, Ext.util.Format.addPercentage);
    },
    stripTags: function(v){
        return !v ? v : String(v).replace(Ext.util.Format.stripTagsRE, '');
    },
    stripScripts: function(v){
        return !v ? v : String(v).replace(Ext.util.Format.stripScriptsRe, '');
    }
});

Ext.override(Ext.form.DisplayField, {
    getValue : function(){
        return this.value;
    },
    setValue : function(v){
        this.value = v;
        this.setRawValue(this.formatValue(v));
        return this;
    },
    formatValue : function(v){
        var renderer = this.renderer;
        if(!renderer){
            return v;
        }
        if(typeof renderer == 'string'){
            renderer = Ext.util.Format[renderer];
        }
        return renderer(v, this.format);
    }
});

//enable combobox to show blank field
Ext.override(Ext.form.ComboBox, {
    initList: (function(){
        if(!this.tpl) {
            this.tpl = new Ext.XTemplate('<tpl for="."><div class="x-combo-list-item">{', this.displayField , ':this.blank}</div></tpl>', {
                blank: function(value){
                    return value==='' ? '&nbsp' : value;
                }
            });
        }
    }).createSequence(Ext.form.ComboBox.prototype.initList)
});

//disable the sort options in grid's header
Ext.override(Ext.grid.GridView, {
    handleHdDown : function(e, t){
        if(Ext.fly(t).hasClass('x-grid3-hd-btn')){
            e.stopEvent();
            var hd = this.findHeaderCell(t);
            Ext.fly(hd).addClass('x-grid3-hd-menu-open');
            var index = this.getCellIndex(hd);
            this.hdCtxIndex = index;
            var ms = this.hmenu.items, cm = this.cm;
            ms.get("asc").setVisible(cm.isSortable(index));
            ms.get("desc").setVisible(cm.isSortable(index));
            this.hmenu.on("hide", function(){
                Ext.fly(hd).removeClass('x-grid3-hd-menu-open');
            }, this, {
                single:true
            });
            this.hmenu.show(t, "tl-bl?");
        }
    }
});

//label config  
var firstname="First Name";
var lastname="Last Name";
var mobile="MDN";
var language="Language";
var currency="Currency";
var status="Status";
var timezone="Time Zone";
var email="Email";
var kyc="KYC";
var dateofbirth="Date of Birth";
var city="City";
var streetname="Street Name";
var streetaddress="Street Address";
var idtype="ID Type";
var idnumber="ID Number";
var expirationtime="Date Of Expiry";
var proofofaddress="Proof of Address";
var creditcheck="Credit Check";
var typeofbankaccount="Type of Bank Account";
var bankaccid="PAN Card/Bank Acc ID";
var subsrefaccount="Reference Account";
var kinname="Kin Name";
var sms="SMS";
var email="Email";
var selfsuspended="Self Suspended";
var suspended="Suspended";
var securitylocked="Security Locked";
var absolutelocked="Absolute Locked";
var secretquestion="Secret Question";
var secretanswer="Secret Answer";
var subscriberid="User ID";
var subscribertype="Subscriber Type";
var birthplace="Place of Birth";
var plotno="Plot Number";
var regionname="Region";
var nationality="Nationality";
var country="Country";
var companyname="Company Name";
var subscribermobilecompany="MDN Money Co. Name";
var coi="Cert. of Incorp.";
var idnumber="ID Number";
var description="Description";
var dhtreeData = "";
var distributionTree = [];


function loadXMLDoc(filename)
{
var xmlhttp2;
var txt,x,xx,i;


if (window.XMLHttpRequest)
  {// code for IE7+, Firefox, Chrome, Opera, Safari
  xmlhttp2=new XMLHttpRequest();
  }
else
  {// code for IE6, IE5
  xmlhttp2=new ActiveXObject("Microsoft.XMLHTTP");
  }
xmlhttp2.onreadystatechange=function()
  {
	if (xmlhttp2.overrideMimeType)
		xmlhttp2.overrideMimeType('text/xml')

  if (xmlhttp2.readyState==4 && xmlhttp2.status==200)
    {
    
	 // alert("++++"+xmlhttp2.readyState+"  xmlhttp.status=  "+xmlhttp2.status);
	  
  var x=xmlhttp2.responseXML.documentElement.getElementsByTagName("Label");
    //alert(xmlhttp2.responseText);
    
    var xmldata=xmlhttp2.responseXML; //retrieve result as an XML object
    var fn=xmldata.getElementsByTagName("FirstName");
    firstname=fn[0].firstChild.nodeValue;
 //    alert(firstname);
    fn=xmldata.getElementsByTagName("LastName");
    lastname=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("MDN");
    mobile=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("Language");
    language=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("Currency");
    currency=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("TimeZone");
    timezone=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("Status");
    status=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("Email");
    email=fn[0].firstChild.nodeValue;    
    fn=xmldata.getElementsByTagName("KYC");
    kyc=fn[0].firstChild.nodeValue; 
    fn=xmldata.getElementsByTagName("DateOfBirth");
    dateofbirth=fn[0].firstChild.nodeValue; 
    fn=xmldata.getElementsByTagName("City");
    city=fn[0].firstChild.nodeValue; 
    fn=xmldata.getElementsByTagName("StreetName");
    streetname=fn[0].firstChild.nodeValue; 
    fn=xmldata.getElementsByTagName("StreetAddress");
    streetaddress=fn[0].firstChild.nodeValue; 
    fn=xmldata.getElementsByTagName("IDType");
    idtype=fn[0].firstChild.nodeValue; 
    fn=xmldata.getElementsByTagName("IDNumber");
    idnumber=fn[0].firstChild.nodeValue; 
    fn=xmldata.getElementsByTagName("DateOfExpiry");
    expirationtime=fn[0].firstChild.nodeValue; 
    fn=xmldata.getElementsByTagName("ProoOfAddress");
    proofofaddress=fn[0].firstChild.nodeValue;
    //alert(proofofaddress);
    fn=xmldata.getElementsByTagName("CreditCheck");
    creditcheck=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("TypeOfBankAccount");
    typeofbankaccount=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("PANBankAccID");
    bankaccid=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("ReferenceAccount");
    subsrefaccount=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("KinName");
    kinname=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("SMS");
    sms=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("Email");
    email=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("SelfSuspended");
    selfsuspended=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("Suspended");
    suspended=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("SecurityLocked");
    securitylocked=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("AbsoluteLocked");
    absolutelocked=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("SecretQuestion");
    secretquestion=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("SecretAnswer");
    secretanswer=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("SubscriberID");
    subscriberid=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("SubscriberType");
    subscribertype=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("PlaceOfBirth");
    birthplace=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("PlotNumber");
    plotno=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("RegionName");
    regionname=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("Nationality");
    nationality=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("Country");
    country=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("CompanyName");
    companyname=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("SubscriberMobileCompany");
    subscribermobilecompany=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("CertOfIncorp");
    coi=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("IDNumber");
    idnumber=fn[0].firstChild.nodeValue;
    fn=xmldata.getElementsByTagName("Description");
    description=fn[0].firstChild.nodeValue;
    
    }
  }
xmlhttp2.open("GET",filename,true);
xmlhttp2.send();
}

//alert("var firstname  "+firstname);
setTimeout(function(){
	loadXMLDoc('resources/lb_catalog.xml');
},5);

var browserHeight = Ext.lib.Dom.getViewHeight();
var browserWidth = Ext.lib.Dom.getViewWidth();

//alert(firstname);