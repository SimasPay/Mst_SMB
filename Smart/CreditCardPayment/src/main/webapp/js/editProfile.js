//normalize MDN 

//calculate maskvalue 
function maskValue(id){
		var mask=1;
	for(var j=1;j<id;j++)
		mask=mask*2;
	return mask;		
}

//check if any new destination is added
function isNewDestinationsAdded(){
	if(document.editRegAccount.newdestinations.value=="0")
		return false;
	else{
		var destinations=Number(document.editRegAccount.newdestinations.value);
		var olddest=Number(document.editRegAccount.olddestinations.value);
		for(var i=olddest+1;i<=destinations+olddest;i++){
			if(document.getElementById('destination'+i).value!="")
				return true;
		}
		return false;
	}
}

//add new destination 
$(document).ready(function() {
					$('#addmdn').click(function() {
										var destinations = Number(document.editRegAccount.newdestinations.value)+Number(document.editRegAccount.olddestinations.value);
										var destinationlimit = Number(document.editRegAccount.destinationlimit.value);
										++destinations;
										if (destinations < destinationlimit) {
											
											$("<tr><td align='left' class='tbl_row2' width='40%'><b>&nbsp;Nomor Tujuan "+destinations
													+"<em><font color='#999999' size='-2'>/DestinationMDN"
													+ destinations
													+ ": </font></em></b></td><td align='left' class='tbl_row2' width='60%'><div><input type='text' class='dest' name='destination"
													+ destinations
													+ "' size='30' id='destination"
													+ destinations
													+ "'></td></tr>").insertBefore("#destinationlimit");
										
												//add validation rules for new destination																		
								                $('#destination'+destinations).rules("add", {
								                	number : true,
													minlength : 5,
													positiveInteger : true,
													isDuplicate : true,
													remote : {url : "ValidateRegistrationServlet",type : "POST"},
								                        messages: {
														positiveInteger : "Please enter a valid Number",
														isDuplicate : "This is a duplicate Destnation",
														remote : "Invalid MDN"
								                        }
								                } );
								                document.editRegAccount.destinations.value = destinations;
								                document.editRegAccount.newdestinations.value = destinations-Number(document.editRegAccount.olddestinations.value);
										}
										else{
											$('#addmdn').attr('disabled','true');
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
										}
							});		
					
					$("input.update").each(function(){
						$(this).change(function(){
							var id=Number(document.getElementById(this.id+'_org').value);
							var mask=maskValue(id);
							var updatevalue=Number(document.editRegAccount.isUpdated.value);
							if($(this).val()!=this.defaultValue)
								document.editRegAccount.isUpdated.value=updatevalue|mask;
							else
								document.editRegAccount.isUpdated.value=updatevalue-(updatevalue&mask);
						});
						});
					});       
//Validate function
function addrules(){
	$("input.dest").each(function(){
        $(this).rules("add", {
        	required : true,
        	number : true,
			minlength : 5,
			positiveInteger : true,
			isDuplicate : true,
			remote : {url : "ValidateRegistrationServlet",type : "POST"},
                messages: {
				positiveInteger : "Please enter a valid Number",
				isDuplicate : "This is a duplicate Destnation",
				remote : "Invalid MDN"
                }
        } );
        $(this).change(function(){
			var id=Number(document.getElementById(this.id+'_org').value);
			var mask=maskValue(id);
			var updatevalue=Number(document.editRegAccount.isUpdated.value);
			if(normalizeMDN($(this).val())!=normalizeMDN(this.defaultValue))
				document.editRegAccount.isUpdated.value=updatevalue|mask;
			else
				document.editRegAccount.isUpdated.value=updatevalue-(updatevalue&mask);
		});
	});
		
}
$.validator.addMethod("isDuplicate", function(value, element) {
	if (value != "") {
		
		var destinations=document.editRegAccount.destinations.value;
		for(var i=0;i<=destinations;i++){
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

$.validator.addMethod("positiveInteger", function(value, element) {
	if(value!="")
	return Number(value) > 0;
	else 
		return true;
});
$(document).ready(function() {
		$(function () {
			$('#submitbutton').click(function(e) {
				e.preventDefault();
				if(isNewDestinationsAdded() || document.editRegAccount.isUpdated.value!="0"){
					if($('#editRegAccount').valid()){
				$("#editRegAccount").submit();
				$('#submitbutton').attr("disabled",true);
				}
				}
				else{
					$('#noupdate').dialog(
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
					$('#noupdate').dialog('open');
				}
			});
			$("#editRegAccount").validate({
				rules : {
					firstname : {
						required : true,
						minlength : 2,
						noSpecialCharacters : true
					},
					lastname :{
						noSpecialCharacters : true
					},
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
						required : true,
                        minlength : 10,
                        securityQuestion : true
					},
					securityAnswer : {
						required : true,
						noSpecialCharacters : true
					},
					bankName_1: {
						required: true,
						noSpecialCharacters : true
					},
					nameOnCard_1 : {
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
					},
					f6_1 : {
						required : true,
						number : true,
						minlength : 6,
						maxlength : 6,
						positiveInteger : true
					},
					l4_1 : {
						required : true,
						number : true,
						minlength : 4,
						maxlength : 4,
						positiveInteger : true
					}
				}, // Rules
				messages : {
					firstname : {
						required : "Please enter your first name",
						noSpecialCharacters : "Special Characters are not allowed"
					},
					lastname : {
						noSpecialCharacters : "Special Characters are not allowed"
					},
					securityQuestion : {
						required : "Please enter your security question",
						securityQuestion : "Special Characters are not allowed"
					},
					securityAnswer : {
						required : "Please enter answer for the security question",
						noSpecialCharacters : "Special Characters are not allowed"
					},
					homephone : {
						positiveInteger : "Your MDN should be a positive number",
						noSpecialCharacters : "Special Characters are not allowed"
//						remote : "This MDN is not valid"
					},
					workphone: {
						positiveInteger : "Your MDN should be a positive number",
						noSpecialCharacters : "Special Characters are not allowed"
//						remote : "This MDN is not valid"
					},
					bankName_1: {
						required: "Please enter bank name",
						noSpecialCharacters : "Special Characters are not allowed"
					},
					nameOnCard_1 : {
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
//					region_1 : {
//						required : "Please enter Region",
//						noSpecialCharacters : "Special Characters are not allowed"
//					},
//					region_2 : {
//						required : "Please enter Region",
//						noSpecialCharacters : "Special Characters are not allowed"
//					},
					zipCode_1 : {
						required : "Please enter Zip Code",
						noSpecialCharacters : "Special Characters are not allowed"
					},
					zipCode_2 : {
						required: "Please enter zipCode",
						noSpecialCharacters : "Special Characters are not allowed"
					},
					f6_1 : {
						maxlength : "Please enter only 6 digits",
						positiveInteger : "Your credit card number should be a Number"
					},			
					l4_1 : {
						maxlength : "Please enter only 4 digits",
						positiveInteger : "Your credit card number should be a Number"
					}
				}//messages
			}); // validate
			return false;
		});
	});