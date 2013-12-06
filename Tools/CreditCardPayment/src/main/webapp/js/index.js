// Custom method to verify number is positive
$.validator.addMethod("amountValidator", function(value, element) {
//    if($("#OPERATION").val()==2)
//    {
//        if( $("#AMOUNT").val().length<1 ||parseInt($("#AMOUNT").val())!=$("#AMOUNT").val() ||$("#AMOUNT").val()<1 || $("#AMOUNT").val().length>11 ||$("#AMOUNT").val().indexOf(".")>-1)
//        {
//            return false;
//        }
//        else
//        {
//            return true;
//        }
//    } else {
        return true;
//    }
});
$(document).ready(function() {
		$(function () {
    $('#submitbutton').click(function(e) {
            e.preventDefault();
            $("#optionsForm").submit();
    });
    $('#optionsForm').validate( {
	onkeyup:false,
        rules: {
             DESCRIPTION:{
                required: true,
                maxlength:100
            },
            AMOUNT:{
                amountValidator:true
            }
        },
        messages: {
            DESCRIPTION:{
                required: "Please enter short description",
                maxlength: "Description can contain at max 100 characters"
            },
            AMOUNT:{
                amountValidator:"Please provide valid amount"
            }
        }
    });
    });
    $("#OPERATION1").click(function(){
        $("#OPERATION").val(1);
        $("#AMOUNT").attr("disabled",true)
        $("#topupamountrow").hide();
        $("#topupdataamountrow").hide();
        $("#packagerow").hide();        
    });
    $("#OPERATION2").click(function(){
        $("#OPERATION").val(2);
        $("#AMOUNT").attr("disabled",false)
        $("#packagerow").show();
        $("#topupamountrow").show();
    });
    $("#PACKAGE").change(function(){
    	if($("#PACKAGE").val()=="Reg"){
    	$("#topupamountrow").show();
        $("#topupdataamountrow").hide();
    	}else if($("#PACKAGE").val()=="Dat"){
    		$("#topupamountrow").hide();
            $("#topupdataamountrow").show();
    	}
    });
    $("#POCKETID").change(function(){
        $("#DOMICILE_ADDRESS_CITY").val(cardArray[$("#POCKETID").val()][1]);
        $("#DOMICILE_ADDRESS_REGION").val(cardArray[$("#POCKETID").val()][2]);
        $("#DOMICILE_ADDRESS_STATE").val(cardArray[$("#POCKETID").val()][3]);
        $("#DOMICILE_ADDRESS_POSTCODE").val(cardArray[$("#POCKETID").val()][4]);
        $("#DOMICILEADDRESSCITY").val(cardArray[$("#POCKETID").val()][1]);
        $("#DOMICILEADDRESSREGION").val(cardArray[$("#POCKETID").val()][2]);
        $("#DOMICILEADDRESSSTATE").val(cardArray[$("#POCKETID").val()][3]);
        $("#DOMICILEADDRESSPOSTCODE").val(cardArray[$("#POCKETID").val()][4]);
    });
});
