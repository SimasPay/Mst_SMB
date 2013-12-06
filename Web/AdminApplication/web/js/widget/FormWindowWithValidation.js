/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * This widget is extended from FormWindow and its main purpose is to validate the form 
 * it is containing so the apply method is overridden to check 'this.form.validate()'
 * before actually saving the form.
 * Note: the form inside should have its validate method defined
 * 
 */

mFino.widget.FormWindowWithValidation = function (config){
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
    mFino.widget.FormWindowWithValidation.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.FormWindowWithValidation, mFino.widget.FormWindow, {
	apply : function(isCloseWindow){
    	this.getEl().unmask();
    	if(this.form.validate()){ //check if the form is valid before saving
    		
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