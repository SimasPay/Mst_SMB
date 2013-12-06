

$(document).ready(function() {
	$('#TQ').click(function () {
        window.open("http://www.smartfren.com/mcomm/faq.html");
       });
	$('#HTR').click(function () {
        window.open("http://www.smartfren.com/mcomm/cararegistrasi.html");
       });
	$('#HTT').click(function () {
        window.open("http://www.smartfren.com/mcomm/cararegistrasi.html#Transaksi");
       });
	$('#privacy').click(function () {
        window.open("http://www.smartfren.com/mcomm/privacy.html");
       });
	$('#TC').click(function () {
        window.open("http://www.smartfren.com/mcomm/syarat.html");
       });
});

if(top != self)
	top.location = self.location;