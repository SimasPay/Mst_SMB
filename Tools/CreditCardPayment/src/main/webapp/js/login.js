// JQuery


$(document).ready(function() {
    $('#loginForm').validate( {
            rules: {
                j_username:{
                    required: true,
                    positiveInteger: true,
                    minlength: 4
                },
                j_password:{
                    required: true,
                    minlength: 6
                }
            },
            messages: {
                    j_username: {
                            required: "Please enter your MDN",
                            positiveInteger : "please enter a valid Number",
                            minlength: "please enter atleast 4 digits "
                    },
                    j_password: {
                            required: "Please enter pin",
                            minlength: "please enter atleast 6 characters"
                    }
            }
    });

    //if register button is clicked
    $('#registerbutton').click(function () {
     window.location.href = "mdnvalidation.jsp";
    });
    //if submit button is clicked
    $('#submitbutton').click(function () {
        //Get the data from all the fields
        var name = $('input[name=j_username]');
        var password = $('input[name=j_password]');
    
        if(!$('#loginForm').valid())
        {
            return false;
        }
        //organize the data properly
        var data = 'j_username=' + normalizeMDN(name.val()) + '&j_password=' + password.val();
        //disabled all the text fields
        $('.text').attr('disabled','true');

        //show the loading sign
        $('.loading').show();

        //start the ajax
        $.ajax({

            url: "j_spring_security_check",

            //POST method is used
            type: "POST",

            //pass the data
            data: data,
            //Do not cache the page
            cache: false,

            //success
            success: function (result) {
                var action;
                eval("action = " + result);
                window.location.href = action["url"];
            },
            error:function(xhr,status,error){
                if(xhr.status == 401)
                {
//                    alert(xhr.statusText);
                	var responseText = xhr.responseText;
                	var statusText = xhr.statusText;
                	var re = new RegExp("Authentication Failed.*(?=</h1>)");
					var resultToDisplay = re.exec(responseText) || statusText;
					alert(resultToDisplay);
                    $('#loginForm')[0].reset();
                }
                else
                {
                    alert("error during ajax call");
                    $('#loginForm')[0].reset();
                }
            }
        });

        //cancel the submit button default behaviours
        return false;
    });
});
