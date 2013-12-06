/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.FormWindowForIntegrations = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        layout:'fit',
        floating: true,
        width: 800,
        height:400,
        closable:true,
        resizable: false,
        plain:true
    });
    mFino.widget.FormWindowForIntegrations.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.FormWindowForIntegrations, mFino.widget.FormWindow, {
	apply : function(isCloseWindow){
    	this.getEl().unmask();
    	if(this.form.validate()){

			var validationResult = this.form.ipMappingGrid.validateIPMappingGrid();
        	if (validationResult == -1) {
        		return;
        	}    		
    		this.form.store.un("write", this.successNotify);
            this.form.store.on("write", this.successNotify, this,
            {
                single : true
            });
            
            this.form.save();
            
            if(isCloseWindow === true){
            	this.getEl().unmask();
            	if(this.form.store.modified.length > 0){
            		 this.form.store.on("write", function(){
                		this.hide();
                    }, this,
                    {
                        single : true
                    });
                }else{
                	
                	this.hide();
                }
            }
        }
	}
});