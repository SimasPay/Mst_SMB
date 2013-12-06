var url="";
var appConfig={};
var isTouchDevice=false;

// Remove page from DOM when it's being replaced
$('div[data-role="page"]').live('pagehide', function(event, ui) {
	$(event.currentTarget).remove();
});


$(document).bind("pagechange", function(event, obj) {
	/* $("#baseForm input").each(function() {
		 if(this.type=='text'||this.type=='password'){
			 $(this).mask(fieldMasks[this.id],{placeholder:""});
		 }
	 });*/
	 
	 /*$("#baseForm :password").each(function() {
		 $(this).mask(fieldMasks[this.id],{placeholder:""});
	 });*/
	
});

$( document ).bind( "mobileinit", function() {
	  // Make your jQuery Mobile framework configuration changes here!

	  $.mobile.allowCrossDomainPages = true;
	});


if (!String.prototype.trim) {
	String.prototype.trim=function(){
		return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
		};
}


function setLayout(){
	var ch= $('.ui-content').height()/2+$('.ui-header').height()+$('.ui-footer').height();
    var ph= $('.ui-page').height()/2;
    $('.ui-content').css("margin-top",(ph>ch?ph-ch:3)+"px");
    $('.ui-footer').css('position','absolute');
    
}


