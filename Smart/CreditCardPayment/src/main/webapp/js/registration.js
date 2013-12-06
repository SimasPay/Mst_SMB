//add destinations dynamically

$(document).ready(function() {
	$('#MoveBack').click(function () {
        
   	 window.location.href = "login.jsp";
   });
					$('#addmdn').click(function() {
										var destinations = Number(document.register.destinations.value);
										var destinationlimit = Number(document.register.destinationlimit.value);
										if (destinations < destinationlimit) {
											++destinations;
											$("<tr><td align='left' class='tbl_row2' width='40%'><b>&nbsp;Nomor Tujuan "+destinations
													+"<em><font color='#999999' size='-2'>/DestinationMDN"
													+ destinations
													+ ": </font></em></b></td><td align='left' class='tbl_row2' width='60%'><div><input type='text' class='dest' name='destination"
													+ destinations
													+ "' size='30' id='destination"
													+ destinations
													+ "'></td></tr>").insertBefore("#destinations");
																															
//											$("input.dest").each(function(){
								                $('#destination'+destinations).rules("add", {
								                	number : true,
													minlength : 5,
													positiveInteger : true,
													isDuplicate : true,
													remote : {url : "ValidateRegistrationServlet",type : "POST"},
								                        messages: {
														positiveInteger : "please enter a valid Number",
														isDuplicate : "This is a duplicate Destnation",
														remote : "This MDN is not valid"
								                        }
								                } );
								                document.register.destinations.value = destinations;
//											} );
										}
										else{
											$('#destlimit').dialog(
													{
														autoOpen : false,
														width : 300,
														modal : true,
														resizable : false,
														buttons : {
															"OK" : function() {
																$(this).dialog("close");
																
															}											
														}
													});
											$('#destlimit').dialog('open');
											$('#addmdn').attr("disabled",true);
										}
							});
					});

$.validator.addMethod("dateFormat", function(value, element) {
	// this accepts the format dd/mm/yyyy
		return value.match(/^\d\d?\/\d\d?\/\d\d\d\d$/);
	});

$.validator.addMethod('validDate', function(value, element) {
	return isDate(value);
});

// Custom method to ensure user selects an option
$.validator.addMethod("selectNone", function(value, element) {
	if (element.value === chooseQuestionValue) {
		$("#ownQuestion").rules("remove");
				return false;
	} else if (element.value === ownQuestionValue) {
		$("#ownQuestion").rules("add", {
			required : true,
			minlength : 10,
			securityQuestion : true,
			messages : {
				required : " Please enter a question or select from drop down",
				securityQuestion : "Special Characters are not allowed"			
			}
		});
		return true;
	} else {
		$("#ownQuestion").rules("remove");
		return true;
	}
});

// Custom methods for second f6 validation
$.validator.addMethod("f6Max", function(value, element) {
	if (value !== "") {
		if (value.length > 6)
			return false;
	}
	return true;
});

//$.validator.addMethod("filldestination1", function(value, element) {
//	document.register.destination1.value = value;
//	document.register.addmdn.disabled=false;
//	return true;
//});


$.validator.addMethod("isDuplicate", function(value, element) {
	if (value != "") {
		var destinations=document.register.destinations.value;
		for(var i=1;i<=destinations;i++){
			if(element.id=="destination"+i)
				continue;
			var dest = document.getElementById('destination'+i).value;
		if(dest!="" && normalizeMDN(dest)==normalizeMDN(value)){
		return false;
		}
		}
		return true;
	}else
		return true;
	});
// Validate function
$(document).ready(function() {
				$("#register").validate(
									{
										onkeyup : false,
										invalidHandler : function(form,validator) {
											// alert('Validation failed!');
										},
										submitHandler : function() {
											$('#dialog').dialog(
															{
																autoOpen : true,
																width : 600,
																modal : true,
																resizable : false,
																buttons : {
																	"I Agree" : function() {
																		$(this).dialog("close");
																		document.register.submit();
																		$('#MoveBack').attr("disabled",true);
																		$('#registerbutton').attr("disabled",true);
																		$('#resetbutton').attr("disabled",true);
																		},
																	"I Disagree" : function() {
																		$(this).dialog("close");
																	}
																}
															});
											
											$('form#register').submit(function() {
														if ($("#register").valid()) {
																$('#dialog').dialog('open');
																	return false;
																	}
																});
										},
										rules : {
											firstname : {
												required : true,
												minlength : 2,
												noSpecialCharacters : true
											},
											lastname :{
												noSpecialCharacters : true
											},
											email :{
												required : true,
												email : true
											},
//											userid : {
//												required : true,
//												minlength : 5,
//												remote : {url : "ValidateRegistrationServlet",type : "POST"}
//											},
											password : {
												required : true,
												minlength : 6,
												maxlength : 6
												
											},
											confirmPassword : {
												required : true,
												equalTo : "#password"
											},
											dob : {
												required : true,
												dateFormat : true,
												validDate : true
											},
//											mdn : {
//												required : true,
//												number : true,
//												minlength : 5,
//												positiveInteger : true,
//												filldestination1 : true,
//												remote : {url : "ValidateRegistrationServlet",type : "POST"}
//												
//											},
											homephone : {
												required : true,
												number : true,
												minlength : 5,
												positiveInteger : true
											},
											workphone : {
												required : true,
												number : true,
												minlength : 5,
												positiveInteger : true
											},
											securityQuestion : {
												selectNone : true
											},
											securityAnswer : {
												required : true,
												noSpecialCharacters : true
											},
											f6 : {
												required : true,
												number : true,
												minlength : 6,
												maxlength : 6,
												positiveInteger : true
											},
											l4 : {
												required : true,
												number : true,
												minlength : 4,
												maxlength : 4,
												positiveInteger : true
											},
											bankName_1 : {
												required : true,
												noSpecialCharacters : true
											},
											nameOnCard : {
												required : true,
												noSpecialCharacters : true
											},
											billingAddress_1 : {
												required : true,
												address : true
											},
											billingAddress_2 : {
												required: true,
												address : true
											},
											AddressLine2_1 :{
												address : true
											},
											AddressLine2_2 :{
												address : true
											},
											city_1 : {
												required : true,
												noSpecialCharacters : true
											},
											city_2 : {
												required: true,
												noSpecialCharacters : true
											},
											state_1 : {
												required : true,
												noSpecialCharacters : true
											},
											state_2 : {
												required: true,
												noSpecialCharacters : true
											},
											region_1 : {
												required : true,
												noSpecialCharacters : true
											},
											region_2 : {
												required: true,
												noSpecialCharacters : true
											},
											zipCode_1 : {
												required : true,
												noSpecialCharacters : true
											},
											zipCode_2 : {
												required: true,
												noSpecialCharacters : true
											}
										}, // Rules
										messages : {
											firstname : {
											required : "please enter firstname",
											minlength : "please enter atleast 2 letters",
											noSpecialCharacters : "Special characters are not allowed"
										},
										lastname :{
											noSpecialCharacters : "Special characters are not allowed"
										},
											password :{
											maxlength : "Please enter only 6 digits"
										},
											email : {
												email : "Please enter a valid email id"
											},
											confirmPassword : {
												equalTo : "The values do not match. Please enter same pin"
											},
											dob : {
												dateFormat : "The date should be in DD/MM/YYYY format",
												validDate : "Please enter a valid date"
											},
											mdn : {
												positiveInteger : "Your MDN should be a positive number",
												remote : "This MDN is not valid"
												},
											homephone : {
												positiveInteger : "Your MDN should be a positive number",
												noSpecialCharacters : "Special Characters are not allowed"
//												remote : "This MDN is not valid"
											},
											workphone: {
												positiveInteger : "Your MDN should be a positive number",
												noSpecialCharacters : "Special Characters are not allowed"
//												remote : "This MDN is not valid"
											},
											securityQuestion : {
												selectNone : "Please select a security question"
											},
											securityAnswer : {
												required : "Please enter answer for the security question",
												noSpecialCharacters : "Special Characters are not allowed"
											},
											bankName_1: {
												required: "Please enter bank name",
												noSpecialCharacters : "Special Characters are not allowed"
											},
											nameOnCard : {
												required : "Please enter  name as on your credit card",
												noSpecialCharacters : "Special Characters are not allowed"
											},
											billingAddress_1 : {
												required : "Please enter address",
												address : "Special Characters are not allowed"
											},
											billingAddress_2 : {
												required : "Please enter the Credit Card billing address",
												address : "Special Characters are not allowed"
											},
											AddressLine2_1 :{
												address : "Special Characters are not allowed"
											},
											AddressLine2_2 :{
												address : "Special Characters are not allowed"
											},
											city_1 : {
												required : "Please enter City",
												noSpecialCharacters : "Special Characters are not allowed"
											},
											city_2 : {
												required : "Please enter city",
												noSpecialCharacters : "Special Characters are not allowed"
											},
											state_1 : {
												required : "Please enter State",
												noSpecialCharacters : "Special Characters are not allowed"
											},
											state_2 : {
												required : "Please enter State",
												noSpecialCharacters : "Special Characters are not allowed"
											},
//											region_1 : {
//												required : "Please enter Region",
//												noSpecialCharacters : "Special Characters are not allowed"
//											},
//											region_2 : {
//												required : "Please enter Region",
//												noSpecialCharacters : "Special Characters are not allowed"
//											},
											country_1 : {
												required: "Please enter Country",
												noSpecialCharacters : "Special Characters are not allowed"
											},
											country_2 : {
												required : "Please enter Country",
												noSpecialCharacters : "Special Characters are not allowed"
											},
											zipCode_1 : {
												required : "Please enter Zip Code",
												noSpecialCharacters : "Special Characters are not allowed"
											},
											zipCode_2 : {
												required: "Please enter zipCode",
												noSpecialCharacters : "Special Characters are not allowed"
											},
											f6 : {
												maxlength : "Please enter only 6 digits",
												positiveInteger : "Your credit card number should be positive"
											},
											l4 : {
												maxlength : "Please enter only 4 digits",
												positiveInteger : "Your credit card number should be positive"
											}
									// messages
										}
									});

				});

/**
 * DHTML date validation script for dd/mm/yyyy. Courtesy of SmartWebby.com
 * (http://www.smartwebby.com/dhtml/)
 */
// Declaring valid date character, minimum year and maximum year
var dtCh = "/";
var minYear = 1900;
var maxYear = 2100;

function isInteger(s) {
	var i;
	for (i = 0; i < s.length; i++) {
		// Check that current character is number.
		var c = s.charAt(i);
		if (((c < "0") || (c > "9")))
			return false;
	}
	// All characters are numbers.
	return true;
}

function stripCharsInBag(s, bag) {
	var i;
	var returnString = "";
	// Search through string's characters one by one.
	// If character is not in bag, append to returnString.
	for (i = 0; i < s.length; i++) {
		var c = s.charAt(i);
		if (bag.indexOf(c) == -1)
			returnString += c;
	}
	return returnString;
}

function daysInFebruary(year) {
	// February has 29 days in any year evenly divisible by four,
	// EXCEPT for centurial years which are not also divisible by 400.
	return (((year % 4 == 0) && ((!(year % 100 == 0)) || (year % 400 == 0))) ? 29
			: 28);
}
function DaysArray(n) {
	for ( var i = 1; i <= n; i++) {
		this[i] = 31
		if (i == 4 || i == 6 || i == 9 || i == 11) {
			this[i] = 30
		}
		if (i == 2) {
			this[i] = 29
		}
	}
	return this
}

function isDate(dtStr) {
	var daysInMonth = DaysArray(12)
	var pos1 = dtStr.indexOf(dtCh)
	var pos2 = dtStr.indexOf(dtCh, pos1 + 1)
	var strDay = dtStr.substring(0, pos1)
	var strMonth = dtStr.substring(pos1 + 1, pos2)
	var strYear = dtStr.substring(pos2 + 1)
	strYr = strYear
	if (strDay.charAt(0) == "0" && strDay.length > 1)
		strDay = strDay.substring(1)
	if (strMonth.charAt(0) == "0" && strMonth.length > 1)
		strMonth = strMonth.substring(1)
	for ( var i = 1; i <= 3; i++) {
		if (strYr.charAt(0) == "0" && strYr.length > 1)
			strYr = strYr.substring(1)
	}
	month = parseInt(strMonth)
	day = parseInt(strDay)
	year = parseInt(strYr)
	if (pos1 == -1 || pos2 == -1) {
		// alert("The date format should be : dd/mm/yyyy")
		return false
	}
	if (strMonth.length < 1 || month < 1 || month > 12) {
		// alert("Please enter a valid month")
		return false
	}
	if (strDay.length < 1 || day < 1 || day > 31
			|| (month == 2 && day > daysInFebruary(year))
			|| day > daysInMonth[month]) {
		// alert("Please enter a valid day")
		return false
	}
	if (strYear.length != 4 || year == 0 || year < minYear || year > maxYear) {
		// alert("Please enter a valid 4 digit year between "+minYear+" and
		// "+maxYear)
		return false
	}
	if (dtStr.indexOf(dtCh, pos2 + 1) != -1
			|| isInteger(stripCharsInBag(dtStr, dtCh)) == false) {
		// alert("Please enter a valid date")
		return false
	}
	return true
}