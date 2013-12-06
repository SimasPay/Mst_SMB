/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ConfirmApproveWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Do You Want to Approve?"),
        layout:'fit',
        floating: true,
        width:360,
        height:300,
        plain:true,
        closable: true,
        resizable: false
    });
    mFino.widget.ConfirmApproveWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ConfirmApproveWindow, Ext.Window, {
    initComponent : function(){
        this.buttons = [
        {
            text: _('Ok'),
            handler: this.ok.createDelegate(this)
        },
        {
            text: _('Cancel'),
            handler: this.cancel.createDelegate(this)
        }
        ];

        this.form = new Ext.form.FormPanel({
            frame : true,
            labelWidth : 120,
            labelPad : 20,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel : _('Giro Ref ID'),
                labelSeparator :'',
                name: CmFinoFIX.message.JSLOP.Entries.GiroRefID._name
            },
            {
                xtype : 'displayfield',
                fieldLabel : _('User Name'),
                labelSeparator :'',
                name: CmFinoFIX.message.JSLOP.Entries.Username._name
            },
            {
                xtype : 'displayfield',
                fieldLabel : _('Paid Amount'),
                labelSeparator :'',
                renderer : 'money',
                name: CmFinoFIX.message.JSLOP.Entries.ActualAmountPaid._name
            },
            {
                xtype : 'displayfield',
                fieldLabel : _('Value Amount'),
                labelSeparator :'',
                renderer : 'money',
                name: CmFinoFIX.message.JSLOP.Entries.AmountDistributed._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("LOP ID"),
                name: CmFinoFIX.message.JSLOP.Entries.ID._name,
                anchor : '100%'
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('LOP Status'),
                name : CmFinoFIX.message.JSLOP.Entries.Status._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Transfer Date'),
                name : CmFinoFIX.message.JSLOP.Entries.TransferDate._name,
                renderer : function(value){
                    if(value){
                        return value.split(' ')[0];
                    }
                    return "";
                }
            }
            ]
        });
        this.items = [
        this.form
        ];
        mFino.widget.ConfirmApproveWindow.superclass.initComponent.call(this);
    },

    cancel : function(){
        this.hide();
    },

    ok : function(){
        var record = this.record;
        record.beginEdit();        
        record.set(CmFinoFIX.message.JSLOP.Entries.Status._name, CmFinoFIX.LOPStatus.Approved);
        record.endEdit();
        if(record.store){
            if(record.phantom){
                record.store.insert(0, record);
            }
            record.store.save();
        }
    
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
    }
});

