/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.OLAP = function(config){
	    
	    
	   
	   
	    
	    
	    
	    var panel = new Ext.Panel({
	         broder: false,
	        width : 1020,
	         items:[      
					 {
		                xtype:'button',
		                fieldLabel: '',
		                text:'GetReport',
		                anchor:'60%',
		                handler : function(){
                       			Ext.Ajax.request({
								   url: 'olap.htm',
								   success: function(response){
								   var res = response.responseText.substring(1,response.responseText.length-1);
								   var splitRes = res.split(",");
								   var url = splitRes[1].substring(7,splitRes[1].length-1);
								   window.open(url);
								   },
								   failure: function(response){
								   },
								   params: { userName: mFino.auth.getUsername() }
								});             
                	}
		            }]
	         
	    });
	    
	   
	    
	    return panel;
};

