/**
 * Ajax Related functions
 */
var W3CDOM = (document.getElementsByTagName && document.createElement);
function getXMLObject() //XML OBJECT
{
    var xmlHttp = false;
    try {
        xmlHttp = new ActiveXObject("Msxml2.XMLHTTP") // For Old Microsoft Browsers
    } catch (e) {
        try {
            xmlHttp = new ActiveXObject("Microsoft.XMLHTTP") // For Microsoft IE 6.0+
        } catch (e2) {
            xmlHttp = false // No Browser accepts the XMLHTTP Object then false
        }
    }
    if (!xmlHttp && typeof XMLHttpRequest != 'undefined') {
        xmlHttp = new XMLHttpRequest(); //For Mozilla, Opera Browsers
    }
    return xmlHttp; // Mandatory Statement returning the AJAX object created
}

// Custom method to verify number is positive
$.validator.addMethod("positiveInteger", function(value, element) {
	if(value!="")
	return Number(value) > 0;
	else 
		return true;
});

//Custom method to avoid special characters at input
$.validator.addMethod("noSpecialCharacters", function(value, element) {
	if (/^[A-Za-z0-9 ]{0,100}$/.test(value)) {
       return true
    } else {
       return false
    }
});

$.validator.addMethod("securityQuestion", function(value, element) {
	if (/^[A-Za-z0-9 ?]{0,100}$/.test(value)) {
       return true
    } else {
       return false
    }
});

//Custom method to avoid special characters at input except /,.
$.validator.addMethod("address", function(value, element) {
	if (/^[A-Za-z0-9 \/,.-]{0,100}$/.test(value)) {
       return true
    } else {
       return false
    }
});

$.validator.addMethod("email", function(value, element) {
	if (/^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/.test(value)) {
       return true
    } else {
       return false
    }
});

function writeError(obj,message) {
    validForm = false;
    if (obj.hasError) return;
    if (W3CDOM) {
        obj.onchange = removeError;
        var sp = document.createElement('span');
        sp.className = 'error';
        sp.appendChild(document.createTextNode(message));
        obj.parentNode.appendChild(sp);
        obj.hasError = sp;
    }
    else {
        errorstring += obj.name + ': ' + message + '\n';
        obj.hasError = true;
    }
    if (!firstError)
        firstError = obj;
}

function removeError()
{
    if(this.hasError)
    {
        this.parentNode.removeChild(this.hasError);
    }
    this.hasError = null;
    this.onchange = null;
}

//function to remove prefix zeros and add prefix 62
function normalizeMDN(MDN) {    
    var start = 0;
    if(MDN=="")
      return "";
    
    while(MDN.charAt(start)=='6' && MDN.charAt(start+1)=='2') {
  	  start += 2;
    }
              
    while(start < MDN.length) {
      if('0' == MDN.charAt(start))
        start ++;
      else
        break;
}
    
    return "62" + MDN.substring(start);
}

//disable keyboard navigation keys
function disablekeyboardnavigation(e)
{
	var input_key	= "";
	var ie4   = (document.all)? true: false;
	var isIE5 = (document.all && document.getElementById) ? true : false;

	
	try	{
		if(window.event) {
			input_key	= event.keyCode;	//IE
		} else {
			input_key	= e.keyCode;			//firefox
		}
		
		if (input_key == 8) //Back button
		{ 
			if (isIE5 && event.srcElement.tagName != "INPUT" && event.srcElement.tagName != "TEXTAREA"){
				return false; 
			}
			if (!isIE5 && e.target.tagName != 'INPUT' && e.target.tagName != 'TEXTAREA'){
				return false; 
			}
		}
		if((input_key == 36 || input_key == 37 || input_key == 39) && input_key == 18) //Alt + Home, Ltarrw, RtArrw
		{
			return false;
		}
		if (input_key == 27) //Esc button
		{
			return false;
		}
		if (input_key == 116 || input_key == 121 || input_key == 122)	//F5, F10, F11
		{
			if (isIE5){
				event.keyCode = 0;
			} else {
				e.preventDefault();
				e.stopPropagation();
			}
				return false;
		}
		
	} catch (e2) {
		input_key = "";
	}	
	return true;
}
document.onkeydown=disablekeyboardnavigation 

