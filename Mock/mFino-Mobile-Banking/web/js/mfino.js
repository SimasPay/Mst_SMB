/* 
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
function trim(str)
{
    return str.replace(/^\s*/, "").replace(/\s*$/, "");
}

var monthNames = {
    1: 'Jan',
    2: 'Feb',
    3: 'Mar',
    4: 'Apr',
    5: 'May',
    6: 'Jun',
    7: 'Jul',
    8: 'Aug',
    9: 'Sep',
    10: 'Oct',
    11: 'Nov',
    12: 'Dec'
};

function IsNumeric(sText)

{
    var ValidChars = "0123456789.";
    var IsNumber=true;
    var Char;

    for (i = 0; i < sText.length && IsNumber == true; i++)
    {
        Char = sText.charAt(i);
        if (ValidChars.indexOf(Char) == -1)
        {
            IsNumber = false;
        }
    }
    return IsNumber;

}

function setCookie( name, value)
{
    document.cookie = name + "=" +escape( value );
}

function getCookie( check_name ) {
    var a_all_cookies = document.cookie.split( ';' );
    var a_temp_cookie = '';
    var cookie_name = '';
    var cookie_value = '';
    var b_cookie_found = false;

    for ( i = 0; i < a_all_cookies.length; i++ )
    {
        a_temp_cookie = a_all_cookies[i].split( '=' );
        cookie_name = a_temp_cookie[0].replace(/^\s+|\s+$/g, '');
        if ( cookie_name == check_name )
        {
            b_cookie_found = true;
            if ( a_temp_cookie.length > 1 )
            {
                cookie_value = unescape( a_temp_cookie[1].replace(/^\s+|\s+$/g, '') );
            }

            return cookie_value;
            break;
        }
        a_temp_cookie = null;
        cookie_name = '';
    }
    if ( !b_cookie_found )
    {
        return null;
    }
}

function deleteCookie(name) {
    if ( getCookie( name ) )
    {
        document.cookie = name + "="
    }
}

function clearIsScheduledCookie(){
    deleteCookie("IsTransferScheduled");
}

function setIsScheduledCookie(){
    setCookie("IsTransferScheduled","yes");
}

function getScheduledCookie(){
    var isSet = getCookie("IsTransferScheduled");
    return isSet;
}

function check(){

    var isSet = getScheduledCookie();

    if(isSet == 'yes'){
        var x=document.getElementById("myForm");
        x.action="scheduletransfer.html";
        x.submit();
    }
}

function setPaymentTo(paymentto)
{
    setCookie("paymentto",paymentto);
}

function getPaymentTo()
{
    return getCookie("paymentto");
}

function setPaymentToOption()
{
    var paymentto = getPaymentTo();
    var x = document.getElementById('paymenttooption');
    x.innerHTML = paymentto;
}

function setPaymentDateOption()
{
    var curDate = new Date();
    var dtStr = curDate.getDate() + "-" + monthNames[curDate.getMonth()+1] + "-" + curDate.getFullYear();

    document.getElementById('paymentdate').value = dtStr;
}

function setTransferDateOption()
{
    var curDate = new Date();
    var dtStr = curDate.getDate() + "-" + monthNames[curDate.getMonth()+1] + "-" + curDate.getFullYear();

    document.getElementById('transferdate').value = dtStr;
}

function setPaymentFrom(paymentfrom)
{
    setCookie("paymentfrom",paymentfrom);
}

function getPaymentFrom()
{
    return getCookie("paymentfrom");
}

function setPaymentFromOption()
{
    var paymentfrom = getPaymentFrom();
    var x = document.getElementById('paymentfromoption');
    x.innerHTML = paymentfrom;
}

function clearIsScheduledPaymentCookie(){
    deleteCookie("IsPaymentScheduled");
}

function setIsScheduledPaymentCookie(){
    setCookie("IsPaymentScheduled","yes");
}

function getScheduledPaymentCookie(){
    var isSet = getCookie("IsPaymentScheduled");
    return isSet;
}

function checkPayment(){

    var isSet = getScheduledPaymentCookie();

    if(isSet == 'yes'){
        var x=document.getElementById("myForm");
        x.action="paymentdate.html";
        x.submit();
    }
}

function doLogin()
{
    var uids = new Array();
    var pwds = new Array();

    var error = document.getElementById('error');
    var loginform = document.getElementById('loginform');

    var uid = loginform.username.value;
    var pwd = loginform.password.value;

    var loginCount = 3;
    uids[0] = 'admin';
    uids[1] = 'mfino';
    uids[2] = 'da';
    pwds[0] = 'admin123';
    pwds[1] = 'mfino123';
    pwds[2] = 'da';

    var isValid = false;
    for(i=0;i<loginCount;i++)
    {
        if((uid == uids[i]) && (pwd == pwds[i]))
        {
            isValid = true;
        }
    }

    if(!isValid){
        error.innerHTML = "Invalid Credentials. Re-login ...";
    }

    return isValid;
}

function setStyle(elmnt, classname)
{
}

function displayMenu()
{
    window.location = "clickermenuoptions.html";
}

function goToPage(page)
{
    window.location = page;
}

function formHandler(form){

    var URL = document.form.site.options[document.form.site.selectedIndex].value;
    window.location.href = URL;
}

function doSignOut()
{
    window.location.href = "exit-confirm.html";
}

function renderFooter()
{
    var x = document.getElementById("footer");
    x.style.visibility = 'hidden';

    x.style.position = 'relative';
    x.style.bottom = 0;

    x.style.visibility = 'visible';
}

function setCurrentLanguage(language)
{
    var status = document.getElementById('currentlang');
    status.innerHTML = language;

}

function setCurrentTimeZone(tz)
{
    var status = document.getElementById('currenttimezone');
    status.innerHTML = tz;
}

function sendACopy()
{
    var status = document.getElementById('success');
    status.innerHTML = "A copy of this screen is being forwarded to you."
}

function loadTransferOptions()
{
    var transferFrom = getCookie("transferfromac");
    var transferTo = getCookie("transfertoac");

    var transferFromId = document.getElementById('transferfromid');
    var transferToId = document.getElementById('transfertoid');

    transferFromId.innerHTML = transferFrom;
    transferToId.innerHTML = transferTo;
}

function clearTransferOptions()
{
    deleteCookie("transferfromac");
    deleteCookie("transfertoac");
}

function setTransferFromOption(option)
{
    var fromacid = document.getElementById('fromac');
    fromacid.innerHTML = option;

    setCookie('transferfromac',option);
}

function setTransferToOption(option)
{
    var toacid = document.getElementById('toac');
    toacid.innerHTML = option;

    setCookie('transfertoac',option);
}

function linkOnMouseDown(li)
{
    li.style.background = '-webkit-gradient(linear, left top, left bottom, from(#84aaed), to(#2d7be5), color-stop(0.5, #528de7), color-stop(0.5, #2b78e4))';
}

function linkOnMouseUp(li)
{
    li.style.background = "white url('../images/chevron.png') no-repeat right center";
}

function linkOnMouseDownPlain(li)
{
    li.style.background = '-webkit-gradient(linear, left top, left bottom, from(#84aaed), to(#2d7be5), color-stop(0.5, #528de7), color-stop(0.5, #2b78e4))';
}

function linkOnMouseUpPlain(li)
{
    li.style.background = "white";
}

function linkOnMouseUpPlainBg(li, bg)
{
    li.style.background = bg;
}

function loadDepositAccount()
{
    var x = getCookie("depositac");
    var y = getCookie("depositamt");

    var xid = document.getElementById('depositacid');
    var yid = document.getElementById('depositamtid');

    xid.innerHTML = x;
    if(yid != null)
    {
        yid.value = y;
    }
  
}

function clearDepositAccount()
{
    deleteCookie("depositac");
    deleteCookie("depositamt");
}

function setDepositAccount(ac)
{
    setCookie('depositac',ac);
}

function setDepositAmount(id)
{
    var xid = document.getElementById(id);
    setCookie('depositamt',xid.value);
}

function saveAccountActivityDetails(date, details, amount, balance, refid)
{
    setCookie("accact_date", date);
    setCookie("accact_details", details);
    setCookie("accact_amount", amount);
    setCookie("accact_balance", balance);
    setCookie("accact_refid", refid);
}

function loadAccountActivityDetails()
{
    var date = getCookie("accact_date");
    var details = getCookie("accact_details");
    var amount = getCookie("accact_amount");
    var balance = getCookie("accact_balance");
    var refid = getCookie("accact_refid");

    var dateElId = document.getElementById('dateEl');
    var detailsElId = document.getElementById('detailsEl');
    var amountElId = document.getElementById('amountEl');
    var balanceElId = document.getElementById('balanceEl');
    var refidElId = document.getElementById('refidEl');

    dateElId.innerHTML = date;
    detailsElId.innerHTML = details;
    amountElId.innerHTML = amount;
    if(balanceElId != null)
    {
        balanceElId.innerHTML = balance;
    }
    refidElId.innerHTML = refid;

    if( amount.indexOf('(') > -1)
    {
        amountElId.style.color = "red";
    }
    else
    {
        amountElId.style.color = "green";
    }

    if( balance.indexOf('(') > -1 && balanceElId != null)
    {
        balanceElId.style.color = "red";
    }

}

function clearLoanCalculator()
{
    var calPanel = document.getElementById('calculationsPanel');

    var nopaymentsEl = document.getElementById('noofpayments');
    var monthlypaymentsEl = document.getElementById('monthlypayment');

    nopaymentsEl.innerHTML = "";
    monthlypaymentsEl.innerHTML = "";

    calPanel.style.visibility = "hidden" ;
}

function calculateLoanPayment()
{
    var loanAmt = document.getElementById('loanamount').value;
    var loanInt = document.getElementById('loaninterest').value;
    var loanTerm = document.getElementById('loanterm').value;

    if( loanAmt.length == 0 || IsNumeric(loanAmt) == false || loanAmt <= 0)
    {
        alert("Enter a valid Loan Amount.");
        return;
    }
    if( loanInt.length == 0 || IsNumeric(loanInt) == false || loanAmt <= 0)
    {
        alert("Enter a valid Interest rate.");
        return;
    }
    if( loanTerm.length == 0 || IsNumeric(loanTerm) == false || loanAmt <= 0)
    {
        alert("Enter a valid Loan Term.");
        return;
    }

    var nopaymentsEl = document.getElementById('noofpayments');
    var monthlypaymentsEl = document.getElementById('monthlypayment');

    var calPanel = document.getElementById('calculationsPanel');

    DownPayment= "0";
    AnnualInterestRate = loanInt/100;
    Years= loanTerm;
    MonthRate=AnnualInterestRate/12
    NumPayments=Years*12
    Prin=loanAmt-DownPayment

    MonthPayment=Math.floor((Prin*MonthRate)/(1-Math.pow((1+MonthRate),(-1*NumPayments)))*100)/100;
    nopaymentsEl.innerHTML = NumPayments;
    monthlypaymentsEl.innerHTML=MonthPayment;

    calPanel.style.visibility = "visible" ;
}


function effects()
{
    var bodyEl = document.getElementById('cmbody');
    bodyEl.className = 'faded' === this.className ? '' : 'faded';
}


var datePickerEl = null;

function setDatePickerDone()
{
    var results = SpinningWheel.getSelectedValues();
    document.getElementById(datePickerEl).value =  results.values.join('-') ;
}

function setDatePickerCancel()
{
//document.getElementById(datePickerEl).value =  '' ;
}

function openDatePicker(id) {

    datePickerEl = id;

    var now = new Date();

    var nowday = now.getDate();
    var nowmonth = now.getMonth();
    var nowyear = now.getFullYear();

    var nowdayindex = 0, nowyearindex = 0;

    var days = { };
    var years = { };
    var months = {
        1: 'Jan',
        2: 'Feb',
        3: 'Mar',
        4: 'Apr',
        5: 'May',
        6: 'Jun',
        7: 'Jul',
        8: 'Aug',
        9: 'Sep',
        10: 'Oct',
        11: 'Nov',
        12: 'Dec'
    };

    for( var i = 1; i < 32; i += 1 ) {
        days[i] = i;
        if( i == nowday)
        {
            nowdayindex = i;
        }
    }

    for( i = now.getFullYear(); i < now.getFullYear()+10; i += 1 ) {
        years[i] = i;
        if(i == nowyear)
        {
            nowyearindex = i;
        }
    }

    SpinningWheel.addSlot(days, 'center', days[nowdayindex]);
    SpinningWheel.addSlot(months, 'center', nowmonth + 1);
    SpinningWheel.addSlot(years, 'center', years[nowyearindex]);

    SpinningWheel.setCancelAction(setDatePickerCancel);
    SpinningWheel.setDoneAction(setDatePickerDone);

    SpinningWheel.open();
}

var pickerEl = null;

function setPickerDone()
{
    var results = SpinningWheel.getSelectedValues();
    document.getElementById(pickerEl).innerHTML =  results.values.join('') ;
    pickerEl = null;
}

function setPickerDoneIpField()
{
    var results = SpinningWheel.getSelectedValues();
    document.getElementById(pickerEl).value =  unescape(results.values.join('')) ;
    pickerEl = null;
}

function setPickerCancel()
{
    document.getElementById(pickerEl).innerHTML =  '' ;
    pickerEl = null;
}

function setPickerCancelIpField()
{
    document.getElementById(pickerEl).value =  '' ;
    pickerEl = null;
}

function openLanguagePicker(id) {

    if(pickerEl!=null)
    {
        SpinningWheel.destroy();
    }

    pickerEl = id;

    var languages = {
        1: 'English',
        2: 'Japanese',
        3: 'Chinese',
        4: 'Spanish'
    };

    SpinningWheel.addSlot(languages, 'center', 'English');

    SpinningWheel.setCancelAction(setPickerCancel);
    SpinningWheel.setDoneAction(setPickerDone);

    SpinningWheel.open();
}

function openTimeZonePicker(id) {

    if(pickerEl!=null)
    {
        SpinningWheel.destroy();
    }

    pickerEl = id;
    
    var timezones = {
        1: 'Pacific',
        2: 'Hawaii',
        3: 'Alaska',
        4: 'Arizona',
        5: 'Mountain'
    };

    SpinningWheel.addSlot(timezones, 'center', 'Pacific');

    SpinningWheel.setCancelAction(setPickerCancel);
    SpinningWheel.setDoneAction(setPickerDone);

    SpinningWheel.open();
}

function openTradeAccountPicker(id)
{
    if(pickerEl!=null)
    {
        SpinningWheel.destroy();
    }

    pickerEl = id;

    var accounts = {
        1: "David's Investments"
    };

    SpinningWheel.addSlot(accounts, 'center', "David's  Investments");

    SpinningWheel.setCancelAction(setPickerCancelIpField);
    SpinningWheel.setDoneAction(setPickerDoneIpField);

    SpinningWheel.open();
}

function openTradeActionPicker(id)
{
    if(pickerEl!=null)
    {
        SpinningWheel.destroy();
    }

    pickerEl = id;

    var actions = {
        1: 'Buy',
        2: 'Sell',
        3: 'Sell Short'
    };

    SpinningWheel.addSlot(actions, 'center', 'Buy');

    SpinningWheel.setCancelAction(setPickerCancelIpField);
    SpinningWheel.setDoneAction(setPickerDoneIpField);

    SpinningWheel.open();
}

function openTradeTimingPicker(id)
{
    if(pickerEl!=null)
    {
        SpinningWheel.destroy();
    }

    pickerEl = id;

    var timings = {
        1: 'Day Only',
        2: 'Open Until Filled'
    };

    SpinningWheel.addSlot(timings, 'center', 'Day Only');

    SpinningWheel.setCancelAction(setPickerCancelIpField);
    SpinningWheel.setDoneAction(setPickerDoneIpField);

    SpinningWheel.open();
}

function reviewOrder()
{
    var account = document.getElementById('placeatradeaccount').value;
    var symbol = document.getElementById('placeatradesymbol').value;
    var action = document.getElementById('placeatradeaction').value;
    var quantity = document.getElementById('placeatradequantity').value;

    var marketorder = document.getElementById('marketorder').checked;
    var limit = document.getElementById('limit').checked;
    var stop = document.getElementById('stop').checked;
    var stoplimit = document.getElementById('stoplimit').checked;

    var marketorderamt = document.getElementById('marketorderamount').value;
    var limitamt = document.getElementById('limitamount').value;
    var stopamt = document.getElementById('stopamount').value;
    var stoplimitstopamt = document.getElementById('stoplimitstopamount').value;
    var stoplimitlimitamt = document.getElementById('stoplimitlimitamount').value;

    var timing = document.getElementById('placeatradetiming').value;
    var reinvestdividends = document.getElementById('reinvestdividends').checked;

    setCookie('account', account);
    setCookie('symbol', symbol);
    setCookie('action', action);
    setCookie('quantity', quantity);

    if(marketorder == true)
    {
        setCookie('ordertype','Market Order');
        setCookie('ordertypeamt',marketorderamt);
    }
    else if(stop == true)
    {
        setCookie('ordertype','Stop');
        setCookie('ordertypeamt',stopamt);
    }
    else if(limit == true)
    {
        setCookie('ordertype','Limit');
        setCookie('ordertypeamt',limitamt);
    }
    else if(stoplimit == true)
    {
        setCookie('ordertype','Stop & Limit');
        setCookie('ordertypeamt',stoplimitstopamt);
        setCookie('ordertypeamt1',stoplimitlimitamt);
    }

    setCookie('timing', timing);
    
    setCookie('reinvestdividends', reinvestdividends);
}

function clearOrderFields()
{
    document.getElementById('placeatradeaccount').value = '';
    document.getElementById('placeatradesymbol').value = '';
    document.getElementById('symboltradevalue').innerHTML = '';
    document.getElementById('symboltradevalue').style.visibility = 'hidden';

    deleteCookie('GetQuoteSymbolOption');

    document.getElementById('placeatradeaction').value = '';
    document.getElementById('placeatradequantity').value = '';

    document.getElementById('marketorder').checked = true;
    document.getElementById('limit').checked = false;
    document.getElementById('stop').checked = false;
    document.getElementById('stoplimit').checked = false;

    document.getElementById('marketorderamount').value = '';
    document.getElementById('limitamount').value  = '';
    document.getElementById('stopamount').value = '';
    document.getElementById('stoplimitstopamount').value = '';
    document.getElementById('stoplimitlimitamount').value = '';

    document.getElementById('timing').value = '';
    document.getElementById('reinvestdividends').checked = false;
}

function clearOrderCookies()
{
    deleteCookie('account');
    deleteCookie('symbol');
    deleteCookie('action');
    deleteCookie('quantity');
    deleteCookie('ordertype');
    deleteCookie('ordertypeamt');
    deleteCookie('ordertypeamt1');
    deleteCookie('timing');
    deleteCookie('reinvestdividends');
}

var reqForReviewTrade = false;

function processReqChangeForReviewTrade() {
    // only if req shows "loaded"
    if (reqForReviewTrade.readyState == 4) {
        // only if "OK"
        if (reqForReviewTrade.status == 200) {
        } else {
            document.getElementById('placeatradeestimatedvalue').innerHTML = "N/A";
        }
    }
}

function loadOrderDetails()
{
    var account = document.getElementById('placeatradeaccount');
    var symbol = document.getElementById('placeatradesymbol');
    var action = document.getElementById('placeatradeaction');
    var quantity = document.getElementById('placeatradequantity');

    var ordertype = document.getElementById('placeatradeordertype');

    var timing = document.getElementById('placeatradetiming');
    var reinvestdividends = document.getElementById('placeatradereinvestdividends');

    var accountVal = getCookie('account');
    var symbolVal = getCookie('symbol');
    var actionVal = getCookie('action');
    var quantityVal = getCookie('quantity');
    var ordertypeVal = getCookie('ordertype');
    var timingVal = getCookie('timing');
    var reinvestdividendsVal = getCookie('reinvestdividends');

    account.innerHTML = accountVal;
    symbol.innerHTML = symbolVal;
    action.innerHTML = actionVal;
    quantity.innerHTML = quantityVal;
    ordertype.innerHTML = ordertypeVal;
    timing.innerHTML = timingVal;
    if(reinvestdividendsVal != null)
    {
        if(reinvestdividendsVal == true)
        {
            reinvestdividends.innerHTML = 'Yes';
        }
        else
        {
            reinvestdividends.innerHTML = 'No';
        }
    }

    if(symbolVal != null)
    {
        var url = '/mfinomb/StocksProcessor?symbol='+symbolVal+'&queryType=value';

        if (window.XMLHttpRequest) {
            try
            {
                reqForReviewTrade = new XMLHttpRequest();
            }
            catch (e)
            {
                reqForReviewTrade = false;
            }
        }

        if (reqForReviewTrade)
        {
            reqForReviewTrade.open('GET', url, false);
            reqForReviewTrade.send(null);
            var response = reqForReviewTrade.responseText;
            response = response.replace( /"/g, ' ' );

            var data = trim(response);

            if(!isNaN(data) && !isNaN(trim(getCookie('quantity'))))
            {
                var qtAmt = parseFloat(data);
                var qtQuantity = parseFloat(trim(getCookie('quantity')));
                var estVal = qtAmt * qtQuantity;

                if(!isNaN(estVal))
                {
                    document.getElementById('placeatradeestimatedvalue').innerHTML = ('<span style=\'height:30px;\'>$ '+estVal+'</span>');
                }
                else
                {
                    document.getElementById('placeatradeestimatedvalue').innerHTML = ('<span style=\'height:30px;\'>&nbsp;</span>');
                }
            }
            else
            {
                document.getElementById('placeatradeestimatedvalue').innerHTML = "N/A";
            }
        }
        else
        {
            document.getElementById('placeatradeestimatedvalue').innerHTML = "N/A";
        }
    }
    else
    {
        document.getElementById('placeatradeestimatedvalue').innerHTML = "N/A";
    }
}

function storeTradeDetails()
{
    var symbol = document.getElementById('placeatradesymbol').innerHTML;
    var action = document.getElementById('placeatradeaction').innerHTML;
    var quantity = document.getElementById('placeatradequantity').innerHTML;

    if( action == 'Buy')
    {
        setCookie('stockquote-'+symbol, quantity);
    }
    else
    {
        deleteCookie('stockquote-'+symbol);
    }
}

function storeWatchList()
{
    var idx = 1;
    for(idx=1;idx<9;idx++)
    {
        var line = document.getElementById('line'+idx+'1').value;
        if(line != null && trim(line))
        {
            setCookie('watchlist'+idx, line);
        }
        else
        {
            setCookie('watchlist'+idx, ' ');
        }
    }
}

function storeNewsList(){

    var l1 = document.getElementById('line1').value;
    if(l1 != null && trim(l1))
    {
        setCookie('news1', l1);
    }
    else
    {
        setCookie('news1', '');
    }
        
        
    var l2 = document.getElementById('line2').value;
    if(l2 != null && trim(l2))
    {
        setCookie('news2', l2);
    }
    else
    {
        setCookie('news2', '');
    }
}

function loadNewsList(){

    var url1 = getCookie('news1');
    feeds[1].url = url1;
        
    var url2 = getCookie('news2');
    feeds[2].url = url2;
}

function loadNewsListToEdit(){
    var url1 = getCookie('news1');

    if(url1 != null && trim(url1))
    {
        document.getElementById('line1').value = url1;
    }else{
        document.getElementById('line1').value = '';
    }

    var url2 = getCookie('news2');

    if(url2 != null && trim(url2))
    {
        document.getElementById('line2').value = url2;
    }else{
        document.getElementById('line2').value = '';
    }
}
function loadWatchList()
{
    var idx = 1;
    for(idx=1;idx<9;idx++)
    {
        var line = getCookie('watchlist'+idx);
        if(line != null && trim(line))
        {
            document.getElementById('line'+idx+'1').innerHTML = line;
            document.getElementById('line'+idx+'2').innerHTML = '<span style=\'height:30px;\'><img src="../images/loading-green.gif" style="width:43px;height:11px;vertical-align:middle;margin:1px;padding:1px;padding-right:10px;"></span>';
            document.getElementById('line'+idx+'3').innerHTML = '<span style=\'height:30px;\'><img src="../images/loading-green.gif" style="width:43px;height:11px;vertical-align:middle;margin:1px;padding:1px;padding-right:10px;"></span>';
        }
    }
}

function loadWatchListToEdit()
{
    var idx = 1;
    for(idx=1;idx<9;idx++)
    {
        var line = getCookie('watchlist'+idx);

        if(line != null && trim(line))
        {
            document.getElementById('line'+idx+'1').value = line;
        }
    }
}

function saveToWatchListToEdit()
{
    var symoption = document.getElementById('ipsymbol').value;

    var idx = 1;
    for(idx=1;idx<9;idx++)
    {
        var line = getCookie('watchlist'+idx);
        if((line != null) && trim(line) == trim(symoption))
        {
            break;
        }

        if(line == null || (trim(line)).length <= 0)
        {
            setCookie('watchlist'+idx, symoption);
            break;
        }
    }
}

var reqStockPos = false;

function loadTradeDetails()
{
    var a_all_cookies = document.cookie.split( ';' );
    var a_temp_cookie = '';
    var cookie_name = '';
    var cookie_value = '';

    var tickerEl = document.getElementById('stockpositiontable');
    var tradeEntryFound = 0;

    if ( tickerEl.hasChildNodes() )
    {
        while ( tickerEl.childNodes.length >= 1 )
        {
            tickerEl.removeChild( tickerEl.firstChild );
        }
    }

    var tmpIdx = -1;
    for ( i = 0; i < a_all_cookies.length; i++ )
    {
        a_temp_cookie = a_all_cookies[i].split( '=' );
        cookie_name = a_temp_cookie[0].replace(/^\s+|\s+$/g, '');
        tmpIdx = cookie_name.indexOf('stockquote-');
        if ( tmpIdx > -1 )
        {
            cookie_name = cookie_name.substring('stockquote-'.length);
            
            if ( a_temp_cookie.length > 1 )
            {
                cookie_value = unescape( a_temp_cookie[1].replace(/^\s+|\s+$/g, '') );
                if(cookie_value.length == 0)
                {
                    continue;
                }
            }

            var row = document.createElement('tr');

            var col1 = document.createElement('td');
            var col2 = document.createElement('td');
            var col3 = document.createElement('td');
            var col4 = document.createElement('td');

            var url = '/mfinomb/StocksProcessor?symbol='+cookie_name+'&queryType=value';

            if (window.XMLHttpRequest) {
                try
                {
                    reqStockPos = new XMLHttpRequest();
                }
                catch (e)
                {
                    reqStockPos = false;
                }
            }

            if (reqStockPos)
            {
                reqStockPos.open('GET', url, false);
                reqStockPos.send(null);
                var response = reqStockPos.responseText;
                response = response.replace( /"/g, ' ' );
                var data = response.split(',');

                col2.innerHTML = '<span style=\'height:30px;\'>' + data + '</span>';
                var tmpCol2 = parseFloat(data);
                var tmpCol3 = parseFloat(cookie_value);
                var tmpCol4 = tmpCol2 * tmpCol3;
                col4.innerHTML = ('<span style=\'height:30px;\'>$ '+tmpCol4+'</span>');
            }
            else
            {
                col2.innerHTML = "N/A";
                col4.innerHTML = "N/A";
            }

            col1.innerHTML = '<span style=\'height:30px;\'>'+cookie_name+'</span>';
            col3.innerHTML = '<span style=\'height:30px;\'>'+cookie_value+'</span>';

            var style = '';
            if( tradeEntryFound%2 == 0)
            {
                style = 'background:LightSteelBlue;height:30px;text-align:center;vertical-align:middle;';
            }
            else{
                style = 'background:white;height:30px;text-align:center;vertical-align:middle;';
            }

            row.setAttribute('style', style);

            col1.setAttribute('style', style);
            col2.setAttribute('style', style);
            col3.setAttribute('style', style);
            col4.setAttribute('style', style);

            row.appendChild(col1);
            row.appendChild(col2);
            row.appendChild(col3);
            row.appendChild(col4);

            tickerEl.appendChild(row);
            tradeEntryFound++;
        }
        
        a_temp_cookie = null;
        tmpIdx = -1;
        cookie_name = '';
    }

    if(tradeEntryFound == 0)
    {
        var emptyrow = document.createElement('tr');

        var emptycol = document.createElement('td');
        emptycol.setAttribute('colspan', '4')

        emptyrow.setAttribute('style', 'background:LightSteelBlue;height:30px;text-align:center;vertical-align:middle;')

        emptycol.innerHTML = '<span style=\'height:30px;\'>No Stock trade data available</span>';
        emptycol.setAttribute('style', 'background:LightSteelBlue;height:30px;text-align:center;vertical-align:middle;');
        emptyrow.setAttribute('style', 'background:LightSteelBlue;height:30px;text-align:center;vertical-align:middle;');

        emptyrow.appendChild(emptycol);
        tickerEl.appendChild(emptyrow);

        tradeEntryFound++
    }

    while(true)
    {
        if(tradeEntryFound < 10)
        {
            var emprow = document.createElement('tr');

            var empcol1 = document.createElement('td');
            var empcol2 = document.createElement('td');
            var empcol3 = document.createElement('td');
            var empcol4 = document.createElement('td');

            empcol1.innerHTML = '<span style=\'height:30px;\'>&nbsp;</span>';
            empcol2.innerHTML = '<span style=\'height:30px;\'>&nbsp;</span>';
            empcol3.innerHTML = '<span style=\'height:30px;\'>&nbsp;</span>';
            empcol4.innerHTML = '<span style=\'height:30px;\'>&nbsp;</span>';

            emprow.appendChild(empcol1);
            emprow.appendChild(empcol2);
            emprow.appendChild(empcol3);
            emprow.appendChild(empcol4);

            var rwstyle = '';
            if( tradeEntryFound % 2 == 0)
            {
                rwstyle = 'background:LightSteelBlue;height:30px;text-align:center;vertical-align:middle;';
            }
            else{
                rwstyle = 'background:white;height:30px;text-align:center;vertical-align:middle;';
            }

            empcol1.setAttribute('style', rwstyle);
            empcol2.setAttribute('style', rwstyle);
            empcol3.setAttribute('style', rwstyle);
            empcol4.setAttribute('style', rwstyle);

            emprow.setAttribute('style', rwstyle);

            tickerEl.appendChild(emprow);
            tradeEntryFound++;
        }
        else{
            break;
        }
    }
}

var reqWatchListDetails = false;

function loadWatchListDetails()
{
    var fmt = 'l1c1';
    
    for(i=1;i<9;i++)
    {
        var symbol = trim(document.getElementById('line'+i+'1').innerHTML);
        if(symbol == null || symbol.length == 0)
        {
            continue;
        }

        var url = '/mfinomb/StocksProcessor?symbol='+symbol+'&queryType=' + fmt;

        if (window.XMLHttpRequest) {
            try
            {
                reqWatchListDetails = new XMLHttpRequest();
            }
            catch (e)
            {
                reqWatchListDetails = false;
            }
        }

        if (reqWatchListDetails)
        {
            reqWatchListDetails.open('GET', url, false);
            reqWatchListDetails.send(null);
            var response = reqWatchListDetails.responseText;
            response = response.replace( /"/g, ' ' );
            var linesdata = response.split(',');

            document.getElementById('line'+ i +'2').innerHTML = linesdata[0];
            document.getElementById('line'+ i + '3').innerHTML = linesdata[1];

            if(linesdata[1].indexOf('-') > -1)
            {
                document.getElementById('line'+ i + '3').style.color = 'red';
            }
            else
            {
                document.getElementById('line'+ i + '3').style.color = 'green';
            }
        }
        else
        {
            document.getElementById('line'+i+'2').innerHTML = '<span style=\'height:30px;\'>N/A- '+reqWatchListDetails.status + '</span>';
            document.getElementById('line'+i+'3').innerHTML = '<span style=\'height:30px;\'>N/A- '+reqWatchListDetails.status + '</span>';
        }
    }
}

function getGetQuoteOption()
{
    return getCookie('GetQuoteOption');
}

function setGetQuoteOption(opt)
{
    setCookie('GetQuoteOption',opt);
}

function saveSymbol()
{
    var symoption = document.getElementById('ipsymbol').value;

    if(symoption != null && trim(symoption).length > 0)
    {
        setCookie('GetQuoteSymbolOption',symoption);
    }
    else
    {
        setCookie('GetQuoteSymbolOption','');
    }
}

function loadSymbol()
{
    var symoption = getCookie('GetQuoteSymbolOption');

    document.getElementById('placeatradesymbol').value = symoption;
    document.getElementById('placeatradesymbol').onchange();

    if(symoption != null)
    {
        document.getElementById('symboltradevalue').style.visibility = 'visible';
    }
    else
    {
        document.getElementById('symboltradevalue').style.visibility = 'hidden';
    }
}

var reqLSVWatchListDetails;

function processLSVReqChange() {
    // only if req shows "loaded"
    if (reqLSVWatchListDetails.readyState == 4) {
        // only if "OK"
        var symtradeel = document.getElementById('symboltradevalue');
        if (reqLSVWatchListDetails.status == 200) {
            var response = reqLSVWatchListDetails.responseText;
            response = response.replace( /"/g, ' ' );
            var dataVals = response.split(',');
            var fmtTxt = 'Last: '+dataVals[0]+', '+dataVals[1]+' - '+ dataVals[2];

            symtradeel.innerHTML = '<span style=\'font-weight:normal;color:blue;float:right;\'>'+fmtTxt+'</span><br>';

        }
        else
        {
            symtradeel.innerHTML = '<span style=\'font-weight:normal;color:red;float:right;\'>Last : N/A</span><br>';
        }
    }
}

function loadSymbolValue()
{
    var fmt = 'd1t1l1';
    var symel = document.getElementById('placeatradesymbol').value;

    if(symel == null || trim(symel).length <= 0)
    {
        document.getElementById('symboltradevalue').innerHTML = '';
        document.getElementById('symboltradevalue').style.visibility = 'hidden';
        return;
    }

    var symtradeel = document.getElementById('symboltradevalue');
    document.getElementById('symboltradevalue').style.visibility = 'visible';

    var url = '/mfinomb/StocksProcessor?symbol='+symel+'&queryType=' + fmt;

    if (window.XMLHttpRequest) {
        try
        {
            reqLSVWatchListDetails = new XMLHttpRequest();
        }
        catch (e)
        {
            reqLSVWatchListDetails = false;
        }
    }

    if (reqLSVWatchListDetails)
    {
        reqLSVWatchListDetails.onreadystatechange = processLSVReqChange;
        reqLSVWatchListDetails.open('GET', url, true);
        reqLSVWatchListDetails.send(null);
    }
    else
    {
        symtradeel.innerHTML = '<span style=\'font-weight:normal;color:red;float:right;\'>Last : N/A</span><br>';
    }
}