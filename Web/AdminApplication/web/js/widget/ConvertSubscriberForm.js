/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ConvertSubscriberForm = function (config,title){
	var mdn;
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : title,
        layout:'fit',
        floating: true,
        height : 120,
        width: 300,
        closable: true,
        resizable: false,
        plain:true
    });
    mFino.widget.ConvertSubscriberForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ConvertSubscriberForm, Ext.Window, {
    initComponent : function(){
        this.buttons = [
        {
            text : _("Submit"),
            handler : this.convertHandler.createDelegate(this)
        },
        {
            text: _('Cancel'),
            handler : this.cancelHandler.createDelegate(this)
        }
        ];

        this.form = new Ext.form.FormPanel({
            frame : true,
            items : [
            {
                xtype: 'textfield',
                fieldLabel: _('MDN'),
                allowBlank: false,
                labelSeparator :'',
                itemId : 'mdn',
                vtype: 'smarttelcophoneAdd',
                maxLength : 16,
                listeners   : {
                    render      : function (el) {
                        el.focus('', 500);
                    },
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        });
        this.items = [ this.form ];

        mFino.widget.ConvertSubscriberForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.convertHandler();
        }
    },
    
    convertHandler : function() {
        if(this.form.getForm().isValid()){
//            this.form.load({
//                url:'this',
//                waitMsg: _('Loading...')
//            });
            var mdnValue = this.form.getComponent('mdn').getValue();
            this.store.baseParams[CmFinoFIX.message.JSSubscriberMDN.ExactMDNSearch._name] = mdnValue;
            this.mdn=mdnValue;
            this.store.load();
          //  this.form.getForm().reset();
        } else {
            Ext.ux.Toast.msg(_("Error"), _("Please Enter the MDN"),5);
        }
    },
    reset: function(){
    	this.form.getForm().reset();
    	this.mdn="";
    },
    
    cancelHandler: function() {
        this.form.getForm().reset();
        this.hide();
    },
    setRecord : function(record){
        this.form.getForm().reset();
        this.record = record;
        this.form.getForm().loadRecord(record);
        this.form.getForm().clearInvalid();
    },

    setStore : function(store){
        this.store = store;
    },
    close : function(){
    	  this.form.getForm().reset();
          this.hide();
    }
});

