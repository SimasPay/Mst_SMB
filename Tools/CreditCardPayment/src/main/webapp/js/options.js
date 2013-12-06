
function validateForm()
{
    var mdn = document.myForm.MDN;
    if(mdn.value < 0 || mdn.value.length < 8 || mdn.value.length > 13)
    {
        alert("Please enter valid Mobile Number");
        mdn.focus();
        return false;
    }
    else if(isNaN(mdn.value)){
        alert("Mobile Number must contain only digits");
        mdn.focus();
        return false;
    }
    var name = document.myForm.CUSTNAME;
    if(name.value.length <= 0 || name.value.length > 120)
    {
        alert("Please enter valid Name");
        name.focus();
        return false;
    }
    var email = document.myForm.CUSTEMAIL;
    if(email.value.length <= 0 || email.value.length > 100)
    {
        alert("Please enter valid Email");
        email.focus();
        return false;
    }else {
        var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
        if(reg.test(email.value) == false) {
            alert('Invalid Email Address');
            email.focus();
            return false;
        }
    }
    var desc = document.myForm.DESCRIPTION;
    if(desc.value.length <= 0 || desc.value.length > 100)
    {
        alert("Please enter valid Description");
        desc.focus();
        return false;
    }
    if(document.getElementById('OPERATION').value == '2') {
        var amount = document.getElementById('AMOUNT');
        if(amount.value > 0 && amount.value.length > 0 && amount.value.length <= 11) {
            if(isNaN(amount.value) || parseInt(amount.value)!=amount.value || amount.value<0){
                alert("Amount must contain only positive integers");
                amount.focus();
                return false;
            }
        } else {
            alert("Invalid Amount");
            amount.focus();
            return false;
        }
    }
    return true;
}
function clearForm()
{
    document.myForm.MDN.value = "";
    document.myForm.CUSTNAME.value = "";
    document.myForm.CUSTEMAIL.value = "";
    document.myForm.DESCRIPTION.value = "";
    document.myForm.AMOUNT.value = "";
}
function RadioGroup1_toggle(c) {
    if (c.value == '2'){
        document.getElementById('hideme').style.visibility = 'visible';
    }
    else {
        document.getElementById('hideme').style.visibility = 'hidden';
    }
    document.getElementById('OPERATION').value = c.value;  // Using operation value at the time of validation
    document.myForm.AMOUNT.value = "";
}