function addRows()
	{
	if($("#requestType").val()=="validatecode"){
			$("#entermdn").hide();
			$("#entercode").show();
			$("#coderow").show();
		}else{
			$("#entermdn").show();
			$("#entercode").hide();
	        $("#coderow").hide();
		}
	}
$.validator.addMethod("codeValidator", function(value, element) {
	if($("#requestType").val()=="validatecode")
	return value!="";
	
	else 
		return true;
});

$(document).ready(function() {
	
	
    $('#mdnvalidate').validate({
            rules: {
    	mdn : {
		required : true,
		number : true,
		minlength : 5,
		positiveInteger : true
    	},
    	code :{
    		codeValidator : true
    	}
    },
    	messages: {
    		mdn: {
    		required: "Please enter MDN",
    		positiveInteger: "Please enter a valid Number"
            },
            code :{
            	codeValidator : "please enter code"
    	}
    }
    });
    $('#SUBMIT').click(function () {
          
        if(!$('#mdnvalidate').valid())
        {
            return false;
        }
    });
    $('#BACK').click(function () {
        
    	 window.location.href = "login.jsp";
    });
});
        