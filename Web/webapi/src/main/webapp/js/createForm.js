
var parseInquiryResponseXML = function(data){
/*data = '<?xml version="1.0"?><response><message code="676">You requested to transfer NGN 50.00 to 2349876543210 and Charges NGN 0.  ParentTransactionID 12756 REF: 388</message><transactionTime>20/08/13 11:09</transactionTime><debitamt>50.00</debitamt><creditamt>50.00</creditamt><charges>0</charges><destinationMDN>2349876543210</destinationMDN><transferID>278</transferID><parentTxnID>12756</parentTxnID><sctlID>388</sctlID></response>'; */
	var x2js = new X2JS();
	var jsonObj = x2js.xml_str2json(data);
	var transferID="";
	var parentTxnID="";
	try{
		transferID = jsonObj["response"]["transferID"];
		parentTxnID = jsonObj["response"]["parentTxnID"];
		$("#confirmForm").find('input[name="transferID"]').val(transferID);
		$("#confirmForm").find('input[name="parentTxnID"]').val(parentTxnID);
	}catch(e){
	}
	var resp = convertJsonObjectToHtml(jsonObj);
	$("#inquiryResponse").html("<table border='1'>"+resp+"</table>");
};

var parseConfirmResponseXML = function(data){
	var x2js = new X2JS();
	var jsonObj = x2js.xml_str2json(data);
	var resp = convertJsonObjectToHtml(jsonObj);
	$("#confirmResponse").html("<table border='1'>"+resp+"</table>");
}

var convertJsonObjectToHtml = function (jsonObj){
	var resp = "";
	$.each(jsonObj, function(key, value) {
		if(value.constructor == Object){
			resp = resp+convertJsonObjectToHtml(value)
		}else if(key != "toString"){
			resp = resp+"<tr><td>"+key+"</td><td>"+value+"</td></tr>";
		}
	});
	return resp;
}
var processInquiry = function(){
	var nameValuePairs = $("#inquiryForm").serialize();
	$.ajax({
	  url: 'sdynamic',
	  data: nameValuePairs,
	  type: 'POST',
	   beforeSend: function () {
			$("#inquiryResponse").html("<img src='images/loading.gif' />");
	   },
	  success: function(data){
		parseInquiryResponseXML(data);
	  }
	});
	return false;
};

var processConfirm = function(){
	var nameValuePairs = $("#confirmForm").serialize();
	$.ajax({
	  url: 'sdynamic',
	  data: nameValuePairs,
	  type: 'POST',
	   beforeSend: function () {
			$("#confirmResponse").html("<img src='images/loading.gif' />");
	   },
	  success: function(data){
		parseConfirmResponseXML(data);
	  }
	});
	return false;
};

var createForm = function(formDiv,selectedService,selectedTxnType){
	$(formDiv).html("");
	var formData = formTypes[selectedService+"_"+selectedTxnType];
	var htmlContent = "";
	var htmlContent = htmlContent+"<table>";
	for(var i=0;i<formData.length;i++){
		var labelElement = formData[i]['caption'];
		var inputElement = "<input type="+formData[i]['type']+" name="+formData[i]['name']+" value="+formData[i]['value']+"\>";
		var rowContent = "<tr><td>"+labelElement+"</td><td>"+inputElement+"</td></tr>";
		htmlContent = htmlContent+rowContent;
	}
	var submitButton = '<tr><td></td><td><input type="submit" value="Submit"></td>';
	htmlContent = htmlContent+submitButton;
	htmlContent = htmlContent+"</table>";
	$(formDiv).html(htmlContent);
	if(formDiv == "#inquiryForm"){
		$(formDiv).find('input[type="submit"]').click(processInquiry);
	}else if(formDiv == "#confirmForm"){
		$(formDiv).find('input[type="submit"]').click(processConfirm);
	}
};

var setConfirmTxnType = function(selectedService,selectedTxnType){
	selectedTxnType = selectedTxnType.replace("Inquiry","");
	$("#confirmTransactionType").val(selectedTxnType);
	createForm("#confirmForm",selectedService,selectedTxnType);
	$("#confirmResponse").html("");
}
var setConfirmService = function(selectedService){
	$("#confirmService").val(selectedService);
	getConfirmTxnTypes(selectedService);
}

var getInquiryTxnTypes = function(selectedService){
	$('#inquiryTransactionType').empty(); 
	$(inquiryTxnTypes[selectedService]).each(function(iIndex, sElement) {
		$('#inquiryTransactionType').append('<option>' + sElement + '</option>');
	});
	$('#inquiryTransactionType').change(function(){
		var selectedService = $('#inquiryService').val();
		var selectedTxnType = this.options[this.selectedIndex].value;
		createForm("#inquiryForm",selectedService,selectedTxnType);
		setConfirmTxnType(selectedService,selectedTxnType);
		$("#inquiryResponse").html("");
	});
};

var inquirySelect = function(){
	$('#inquiryService').empty(); 
	$(services).each(function(iIndex, sElement) {
		$('#inquiryService').append('<option>' + sElement + '</option>');
	});
	$('#inquiryService').change(function(){
		var selectedService = this.options[this.selectedIndex].value;
		getInquiryTxnTypes(selectedService);
		$("#inquiryResponse").html("");
		setConfirmService(selectedService);
	});
}

var getConfirmTxnTypes = function(selectedService){
	$('#confirmTransactionType').empty(); 
	$(confirmTxnTypes[selectedService]).each(function(iIndex, sElement) {
		$('#confirmTransactionType').append('<option>' + sElement + '</option>');
	});
	$('#confirmTransactionType').change(function(){
		var selectedService = $('#confirmService').val();
		var selectedTxnType = this.options[this.selectedIndex].value;
		createForm("#confirmForm",selectedService,selectedTxnType);
		$("#confirmResponse").html("");
	});
};
 
var confirmSelect = function(){	
	$('#confirmService').empty(); 
	$(services).each(function(iIndex, sElement) {
		$('#confirmService').append('<option>' + sElement + '</option>');
	});
	$('#confirmService').change(function(){
		var selectedService = this.options[this.selectedIndex].value;
		getConfirmTxnTypes(selectedService);
		$("#confirmResponse").html("");
	});
}

jQuery(document).ready(function(){
	inquirySelect();
	confirmSelect();
	parseInquiryResponseXML();
});