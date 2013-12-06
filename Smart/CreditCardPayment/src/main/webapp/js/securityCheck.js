$(document).ready(function() {
	 $('#back').click(function () {
	     window.location.href = "login.jsp";
	    });
	$('#securityCheckChangePassword').validate( {
            rules: {
                Answer:{
                    required: true,
                  
                }             
            },
            messages: {
                    Answer: {
                            required: "Please enter your Secret Answer",
                            minlength: "Your answer must consist of at least 4 characters"
                    }                   
            }
    });
});
