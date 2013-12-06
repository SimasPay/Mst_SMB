function addCodeRules()
	{
	if($("#requestType").val()=="editEmailCodeValidation"){
					$('#code').rules("add", {
							 	required : true,
							 	minlength: 4,
							 	messages: {
								required: "Please enter Code"
					       }
			  } );
		}
	if($("#requestType").val()=="editEmailCodeValidation"){
		$("#enteremail").hide();
		$("#entercode").show();
		$("#coderow").show();
	}else{
		$("#enteremail").show();
		$("#entercode").hide();
        $("#coderow").hide();
	}
	}

$.validator.addMethod("newemail", function(value, element) {
	if(value==document.editEmail.oldemail.value){
		return false;
	}
	return true;
	});
$(document).ready(function() {
$('#editEmail').validate( {
        rules: {
    	newemail:{
                required: true,
                email : true,
                newemail : true
            }
        },
        messages: {
        	newemail: {
                        required: "Please enter an email id",
                        email: "please enter a valid email id",
                        newemail : "enter a new mail id"
                }
}
    });
});
