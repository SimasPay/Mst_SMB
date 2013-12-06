/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DCTWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        layout:'fit',
        floating: true,                
        closable: true,
        resizable: false,
        plain:true
    });
    mFino.widget.DCTWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.DCTWindow, Ext.Window, {
    initComponent : function(){
        this.buttons = [
        /*{
        	itemId : "apply",
            text: _('Apply'),
            handler: this.apply.createDelegate(this)
        }
        ,*/{
        	itemId : "ok",
            text: _('OK'),
            handler: this.ok.createDelegate(this)
        }
        ,{
        	itemId: "cancel",
            text: _('Cancel'),
            handler: this.cancel.createDelegate(this)
        }
        ];

        this.items = [this.form];

        mFino.widget.DCTWindow.superclass.initComponent.call(this);
    },

    close: function(){ 
    this.hide();
    },
    apply : function(isCloseWindow){
    	var returnValue = this.form.validateLevels();
    	if(!returnValue){
        var detailsForm = this.form.find("itemId", "dctdetails")[0];
        if(detailsForm.getForm().isValid()){
            this.form.store.un("write", this.successNotify);
            this.form.store.un("write", this.saveLevels);
            this.form.store.on("write", this.successNotify, this,
            {
                single : true
            });
            if(this.mode === "add") {
                this.form.store.on("write", this.saveLevels, this);
            } else {
               this.form.saveLevels();
            }

            this.form.save();
            if(isCloseWindow === true){
                if(this.form.store.modified.length > 0){
                    this.form.store.on("write", function(){
                        this.hide();
                        this.form.store.reload();
                    }, this,
                    {
                        single : true
                    });                    
                }else {
                    this.hide();
                    this.form.store.reload();
                }
            }
        }
    	}
    },

    saveLevels: function(){        
        this.form.saveLevels();
    },

    setMode: function(mode){
        this.mode = mode;
		this.form.setMode(mode);
    },

    successNotify : function(){
        Ext.ux.Toast.msg(_("Message"), _("Record saved successfully"));
    },

    ok : function(){
        this.apply(true);
    },

    cancel : function(){
        if(this.form.record.phantom){
            this.form.store.remove(this.form.record);
        }else{
            this.form.record.reject();
        }
        this.hide();
    },

    setRecord : function(record){
        this.form.setRecord(record);
    },

    setStore : function(store){
        this.form.store = store;
    }    
});


