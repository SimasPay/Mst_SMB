
// JQuery
$(document).ready(function() {
    $('#codeConfrim').validate( {
            rules: {
                confirmationCode:{
                    required: true,
                    minlength: 4
                },
                username:{
                    required: true,
                    positiveInteger: true,
                    minlength: 5
                }
            },
            messages: {
                    username: {
                            required: "Please enter MDN",
                            positiveInteger : "please enter digits only",
                            minlength: "Your MDN must consist of at least 5 digits"
                    },
                    confirmationCode: {
                            required: "Please enter code",
                            minlength: "Your code must consist of at least 4 characters"
                    }
            }
    });
    $('#forgotPasswordCodeConfirm').validate({
        rules: {
            confirmationCode:{
                required: true,
                minlength: 4
            },
            MDN:{
                required: true,
                positiveInteger: true,
                minlength: 5
            }
        },
        messages: {
                MDN: {
                        required: "Please enter valid MDN",
                        positiveInteger : "please enter digits only",
                        minlength: "Your MDN must consist of at least 5 digits"
                },
                confirmationCode: {
                        required: "Please enter valid code",
                        minlength: "Your code must consist of at least 4 characters"
                }
        }
});

});