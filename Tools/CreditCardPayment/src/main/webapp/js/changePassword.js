$.validator.addMethod("notEqual", function(value, element) {
	if (value != "") {
	if($('#currentPasswd').val()!=value)
		return true;
	
	else 
		return false;
	}
	else
		return true;
	});

$(document).ready(function() {
	$('#currentPasswd').val("");
    $('#changePassword').validate( {
        rules: {
            currentPasswd:{
                required: true,
                minlength: 6,
                maxlength:6
                
            },
            newPasswd:{
                required: true,
                minlength: 6,
                maxlength:6,
                notEqual : true
            },
            confirmPasswd:{
                required: true,
                minlength: 6,
                maxlength:6,
                equalTo: "#newPasswd"
            }
        },
        messages: {
                    
            currentPasswd: {
                required: "Please enter current pin",
                minlength: "Your pin must must be 6 characters",
                maxlength: "Your pin must must be 6 characters"
                
            },
            newPasswd: {
                required: "Please enter new pin",
                minlength: "Your pin must must be 6 characters",
                maxlength: "Your pin must must be 6 characters",
                notEqual:"old and new pin must not be same"
            },
            confirmPasswd: {
                required: "confirm the pin",
                minlength: "Your pin must must be 6 characters",
                maxlength: "Your pin must must be 6 characters",
                equalTo: "please enter same as new pin"
            }
        }
    });
});