/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.FormWindowForPocketTemplateConfig = function (config){
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
    mFino.widget.FormWindowForPocketTemplateConfig.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.FormWindowForPocketTemplateConfig, mFino.widget.FormWindow, {
	//overridden the apply function to handle checkDefaultAndSave and then call 'save' as callback instead of calling sequentially
	//as in other formwindows to stop hiding window when a check fails.
	apply : function(isCloseWindow){
    	this.getEl().unmask();
    	if(this.form.getForm().isValid()){    		
    		this.form.store.un("write", this.successNotify);
            this.form.store.on("write", this.successNotify, this,
            {
                single : true
            });            
            this.form.checkDefaultAndSave(this);
        }
	}
});