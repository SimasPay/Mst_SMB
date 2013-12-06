function confirmSubmit(messageBahasa, messageEnglish) {
	var answer = confirm(messageBahasa + '\n\n' + messageEnglish)
	if(answer) {
		$('#SubmitButton').attr("disabled",true);
		$('#CancelButton').attr("disabled",true);
		ajaxFunction();
	}
	else
		return false;
}

function confirmCancel() {
	var answer = confirm("Are you sure you want to cancel the transaction?")
	if(answer){
		$('#SubmitButton').attr("disabled",true);
		window.location.href="index.jsp";
	}
		else
		return false;
}

var xmlhttp = new getXMLObject(); //xmlhttp to hold the AJAX object
var gateway;
function ajaxFunction() {
    if (xmlhttp) {
        var amount = document.getElementById("AMOUNT");
        var mdn = document.getElementById("MDN");
        var description = document.getElementById("DESCRIPTION");
        var operation = document.getElementById("OPERATION");
        var billReferenceNumber = document.getElementById("BILLREFERENCENUMBER");
        var subscriberID= document.getElementById("SUBSCRIBERID");
        var pack= document.getElementById("PACKAGE");
        var piCode= document.getElementById("PICODE");
        
	var pocketID = document.getElementById("POCKETID");
        gateway = document.getElementById("gateway").value;
        if(gateway === "NSIAPAY"){
            xmlhttp.open("POST", "NSIACCPaymentServlet", true);
        }else if(gateway === "infinitium"){
            xmlhttp.open("POST", "CCPaymentServlet", true);
        }
        xmlhttp.onreadystatechange = handleServerResponse;
        xmlhttp.setRequestHeader('Content-Type',
            'application/x-www-form-urlencoded');
        xmlhttp.send("AMOUNT=" + amount.value+
            "&MDN=" + mdn.value+
            "&POCKETID=" + pocketID.value +
            "&SUBSCRIBERID=" + subscriberID.value +
            "&DESCRIPTION=" + description.value +
            "&OPERATION=" + operation.value +
            "&BILLREFERENCENUMBER=" + billReferenceNumber.value+
            "&PACKAGE="+pack.value+
            "&PICODE="+piCode.value); //Post amount to Servlet
    }
}

function handleServerResponse() {
    if (xmlhttp.readyState == 4) {
        if (xmlhttp.status == 200) {
            var responseText = xmlhttp.responseText;
            responseText = responseText
            .substring(0, responseText.indexOf("\r"));
            var signature = responseText.substring(0, responseText.indexOf("***"));
            var merchantTxnID = responseText.substring(responseText.lastIndexOf("*") + 1,responseText.indexOf("sessionid"));
            var amount =  document.myForm.AMOUNT.value;
            var sessionid =responseText.substring(responseText.indexOf("sessionid") + 9);
            //document.myForm.OPERATION.disabled=true;
            document.myForm.AMOUNT.value = document.myForm.AMOUNT.value + ".00";
            document.myForm.SIGNATURE.value = signature;
            document.myForm.WORDS.value = signature;
            document.myForm.MERCHANT_TRANID.value = merchantTxnID;
            document.myForm.TRANSIDMERCHANT.value = merchantTxnID;
            document.myForm.SESSIONID.value = sessionid;
            document.myForm.action = document.myForm.POSTURL.value;        
            if(gateway === "NSIAPAY"){
            document.myForm.BASKET.value = ( document.getElementById("OPERATION").value == 1?"Post-Paid":"Pre-Paid") + ","+amount +","+ 1 +"," + amount;
         }
                       document.myForm.submit();
        } else {
            alert("Error during AJAX call. Please try again");
            $('#SubmitButton').attr("disabled",false);
    		$('#CancelButton').attr("disabled",false);
        }
    }
}