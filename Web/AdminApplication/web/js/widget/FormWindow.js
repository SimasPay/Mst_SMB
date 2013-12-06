/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.FormWindow = function (config){
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
    mFino.widget.FormWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.FormWindow, Ext.Window, {
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
        },{
            itemId: "cancel",
            text: _('Cancel'),
            handler: this.cancel.createDelegate(this)
        }
        ,{
            itemId: "okView",
            text: _('OK'),
            handler: this.cancel.createDelegate(this)
        }
        ,{
            itemId: "close",
            text: _('Close'),
            handler: this.close.createDelegate(this)
        }
        ];

        this.items = [this.form];

        mFino.widget.FormWindow.superclass.initComponent.call(this);

        this.on("render", function(){
            this.setEditable(this.initialConfig.isEditable);
            if(this.initialConfig.mode){
                this.setMode(this.initialConfig.mode);
            }
        });

        this.on("show", function(){
            this.reloadRemoteDropDown();
        });
    },

    setMode : function(mode){
        Ext.each(this.buttons, function(item){
            item.hide();
            if((mode === "edit" || mode === "add")
                && (item.itemId === "ok" || item.itemId === "cancel")){
                item.show();
            }
            if(mode === "edit" && (item.itemId === "apply")){
                item.show();
            }
            if(mode === "view" && (item.itemId === "okView")){
                item.show();
            }
            if(mode === "close" && (item.itemId === "close")){
                item.show();
            }
        });
    },

    apply : function(isCloseWindow){
    	this.getEl().unmask();
    	if(this.form.getForm().isValid()){
    		
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
    	else{
    		var errfield;
    		var tb1=this.form.findByType('textfield', false);
    		var chkfld;
    		var prefix = "";    		
    		for ( var k = 0; k < tb1.length; k++) {    			
    			var fldlabel = this.find('itemId',tb1[k].itemId)[0].fieldLabel;
    			chkfld=tb1[k].name;
    			prefix = "";
    			if(chkfld.match("OutletAddress"))
    			{
    				prefix="Outlet Details ";
    			}
    			if(chkfld.match("Merchant"))
    			{
    				prefix="Contact Details ";
    			}
    			
    			if(this.find('itemId',tb1[k].itemId)[0].allowBlank == false)
    			{
    				if(this.find('itemId',tb1[k].itemId)[0].getValue()== '' && !tb1[k].disabled)
    				{
    					fldlabel=fldlabel.replace(/<span style="color:#8B0000;">\*<\/span>/,"");    //by itself always elim ; at the end if any
    					errfield=prefix+fldlabel;
    					break;
    				}
    			}
    			if(this.find('itemId',tb1[k].itemId)[0].isValid() == false)
    			{
    				fldlabel=fldlabel.replace(/<span style="color:#8B0000;">\*<\/span>/,"");    //by itself always elim ; at the end if any
    				errfield=prefix+fldlabel;
     				break;
    			}
    		}
    		Ext.ux.Toast.msg(_("Error"), _(errfield+" has invalid information. <br/> Please fix the errors before submit"),5);
    		this.getEl().unmask();
    	}
    },

    successNotify : function(){
        Ext.ux.Toast.msg(_("Message"), _("Record saved successfully"));
        this.getEl().unmask();
    },

    ok : function(){
    	this.getEl().mask();
        this.apply(true);
    },

    cancel : function(){
    	this.getEl().unmask();
        if(this.form.record.phantom){
            this.form.store.remove(this.form.record);
        }else{
            this.form.record.reject();
        }
        this.hide();
    },

    close : function(){
        this.cancel();
    },
    
    setRecord : function(record){
        this.form.setRecord(record);
    },
    setStore : function(store){
        this.form.store = store;
    },

    // This is for set the first tab as ActiveTab always.
    reset : function(values){
        this.form.getForm().reset();
        this.form.find("itemId", values)[0].setActiveTab(0);
    },

    //  This is for clearing second and third tab panel contents.
    resetAll : function(value){
        for(var i=1;i<=2;i++){
            var tb = this.form.find('itemId',value)[0].getItem(i).findByType('textfield', false);

            for(var j = 0; j < tb.length; j++){
                this.form.find("itemId", tb[j].itemId)[0].setValue(null);
            }
        }
    },
  
    setEditable : function(isEditable){
        if(isEditable === undefined || isEditable){
            this.form.getForm().items.each(function(item) {
                if(item.getXType() == 'textfield' || item.getXType() == 'combo' ||
                    item.getXType() == 'textarea' || item.getXType() == 'numberfield' ||
                    item.getXType() == 'datefield' ||item.getXType() == 'checkboxgroup' ||
                    item.getXType() == 'enumdropdown'||item.getXType() == 'remotedropdown') {
                    //enable the item
                    item.enable();
                }
            });
        }else{
            this.form.getForm().items.each(function(item) {
                if(item.getXType() == 'textfield' || item.getXType() == 'combo' ||
                    item.getXType() == 'textarea' || item.getXType() == 'numberfield' ||
                    item.getXType() == 'datefield' ||item.getXType() == 'checkboxgroup' ||
                    item.getXType() == 'enumdropdown'||item.getXType() == 'remotedropdown') {
                    //Disable the item
                    item.disable();
                }
            });
        }
    },

    reloadRemoteDropDown : function(){
        this.form.getForm().items.each(function(item) {
            if(item.getXType() == 'remotedropdown') {
                item.reload();
            }
        });
    }
});