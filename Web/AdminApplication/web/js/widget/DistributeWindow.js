/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DistributeWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("LOP Distribute"),
        layout:'fit',
        floating: true,
        width:360,
        height:250,
        plain:true,
        closable: true,
        resizable: false
    });
    mFino.widget.DistributeWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.DistributeWindow, Ext.Window, {
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
                itemId :'dmdn',
                fieldLabel : _('Destination MDN'),
                labelSeparator :'',                
                name: CmFinoFIX.message.JSLOP.Entries.MDN._name
            },
            {
                xtype : 'displayfield',
                itemId :'distributeAmount',
                fieldLabel : _('Distribute Amount'),
                labelSeparator :'',
                renderer : 'money',
                name: CmFinoFIX.message.JSLOP.Entries.AmountDistributed._name
            },{
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
            }//            ,
//            {
//                xtype : 'textfield',
//                itemId :'smdn',
//                fieldLabel : _('Source MDN'),
//                labelSeparator :'',
//                width : 180
//            },
//            {
//                xtype : 'textfield',
//                itemId :'pin',
//                fieldLabel : _('PIN'),
//                labelSeparator :'',
//                inputType : 'password',
//                width : 180
//            }
            ]
        });
        this.items = [
        this.form
        ];
        mFino.widget.DistributeWindow.superclass.initComponent.call(this);
    },

    cancel : function(){
        this.hide();
    },

    ok : function(){
        var msg= new CmFinoFIX.message.JSLOPDistribute();
        msg.m_pLOPID = this.record.get(CmFinoFIX.message.JSLOP.Entries.ID._name);
        msg.m_pDestMDN = this.record.get(CmFinoFIX.message.JSLOP.Entries.MDN._name);
        msg.m_pAmount = this.record.get(CmFinoFIX.message.JSLOP.Entries.AmountDistributed._name);
        msg.m_pPin = "";       
        var params = mFino.util.showResponse.getDisplayParam();
        mFino.util.fix.send(msg, params);
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

