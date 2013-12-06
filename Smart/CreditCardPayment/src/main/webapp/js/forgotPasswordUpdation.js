$(document).ready(function() {
	$('#newPassword').val("");
    $('#forgotPasswordUpdation').validate({
            rules: {
            	newPassword:{
                    required: true,
                    minlength: 6,
                    maxlength:6
                },
                confirmPassword:{
                    required: true,
                    equalTo: "#newPassword"
                }
            },
            messages: {                       
                newPassword: {
                    required: "Please enter new pin",
                    minlength: "Your pin must must be 6 characters",
                    maxlength: "Your pin must must be 6 characters"
                },
                confirmPassword: {
                    required: "confirm the pin",
                    equalTo: "please enter same as new pin"
                }
            }
    });
});