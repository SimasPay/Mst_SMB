$(document).ready(function() {
    $('#forgotPasswordForm').validate({
            rules: {
    	mdn : {
		required : true,
		positiveInteger : true,
		minlength : 4,
		
    	}
    	},
    	messages: {
    		mdn: {
    		required: "Please enter MDN",
    		positiveInteger: "please enter a valid Number",
    		minlength: "please enter atleast 4 digits"
    		 }
    	}
    });
    $('#SUBMIT').click(function () {
          
        if(!$('#forgotPasswordForm').valid())
        {
            return false;
        }
    });
    $('#BACK').click(function () {
        
    	 window.location.href = "login.jsp";
    });
});
        