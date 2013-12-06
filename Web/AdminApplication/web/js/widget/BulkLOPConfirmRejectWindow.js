/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BulkLOPConfirmRejectWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Do You Want to Reject?"),
        layout:'fit',
        floating: true,
        width:360,
        height:300,
        plain:true,
        closable: true,
        resizable: false
    });
    mFino.widget.BulkLOPConfirmRejectWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkLOPConfirmRejectWindow, Ext.Window, {
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
                name: CmFinoFIX.message.JSBulkLOP.Entries.GiroRefID._name
            },
            {
                xtype : 'displayfield',
                fieldLabel : _('User Name'),
                labelSeparator :'',
                name: CmFinoFIX.message.JSBulkLOP.Entries.Username._name
            },
            {
                xtype : 'displayfield',
                fieldLabel : _('Paid Amount'),
                labelSeparator :'',
                renderer : 'money',
                name: CmFinoFIX.message.JSBulkLOP.Entries.ActualAmountPaid._name
            },
            {
                xtype : 'displayfield',
                fieldLabel : _('Value Amount'),
                labelSeparator :'',
                renderer : 'money',
                name: CmFinoFIX.message.JSBulkLOP.Entries.AmountDistributed._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _("LOP ID"),
                name: CmFinoFIX.message.JSBulkLOP.Entries.ID._name,
                anchor : '100%'
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel :_('LOP Status'),
                name : CmFinoFIX.message.JSBulkLOP.Entries.Status._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Transfer Date'),
                name : CmFinoFIX.message.JSBulkLOP.Entries.TransferDate._name,
                renderer : function(value){
                    if(value){
                        return value.split(' ')[0];
                    }
                    return "";
                }
            },
            {
              xtype:'bulklopdetailsgrid',
              itemId:'bulklopgrid'
            }
            ]
        });
        this.items = [
        this.form
        ];
        mFino.widget.BulkLOPConfirmRejectWindow.superclass.initComponent.call(this);
    },

    cancel : function(){
        this.hide();
    },

    ok : function(){
      var record = this.record;
        record.beginEdit();
        record.set(CmFinoFIX.message.JSBulkLOP.Entries.Status._name, CmFinoFIX.LOPStatus.Rejected);
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
        bulklopgrid = this.items.get("bulklopgrid");
        bulklopgrid.store.baseParams[CmFinoFIX.message.JSBulkLOPDetails.IDSearch._name] = record.data[CmFinoFIX.message.JSBulkLOP.Entries.ID._name];
        bulklopgrid.store.load();
        this.form.getForm().clearInvalid();
    },

    setStore : function(store){
        this.store = store;
    }
});

