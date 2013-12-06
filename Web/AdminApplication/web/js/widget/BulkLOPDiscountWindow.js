/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BulkLOPDiscountWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Change LOP Discount"),
        layout:'fit',
        floating: true,
        width:360,
        height:450,
        plain:true,
        closable: true,
        resizable: false
    });
    this.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSLOPHistory);
    mFino.widget.BulkLOPDiscountWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkLOPDiscountWindow, Ext.Window, {
    initComponent : function(){
        this.buttons = [
        {
            text: _('Save'),
            handler: this.save.createDelegate(this)
        },
        {
            text: _('Close'),
            handler: this.close.createDelegate(this)
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
                fieldLabel: _("LOP ID"),
                name: CmFinoFIX.message.JSLOP.Entries.ID._name
            },
            {
                xtype : "displayfield",
                fieldLabel :_('LOP Status'),
                name : CmFinoFIX.message.JSLOP.Entries.Status._name
            },
            {
                xtype : 'displayfield',
                fieldLabel : _('Transfer Date'),
                name : CmFinoFIX.message.JSLOP.Entries.TransferDate._name,
                renderer : function(value){
                    if(value){
                        return value.split(' ')[0];
                    }
                    return "";
                }
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Paid Amount'),
                renderer : "money",
                name: CmFinoFIX.message.JSLOP.Entries.ActualAmountPaid._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Commission'),
                renderer : "percentage",
                name: CmFinoFIX.message.JSLOP.Entries.Commission._name
            },
            {
                xtype : 'displayfield',
                itemId :'distributeAmount',
                fieldLabel : _('Value Amount'),
                labelSeparator :'',
                renderer : 'money',
                name: CmFinoFIX.message.JSLOP.Entries.AmountDistributed._name
            },
            {
                xtype : "numberfield",
                anchor : '90%',
                allowBlank: false,
                allowNegative:false,
                allowDecimals:true,
                decimalPrecision : 2,
                maxValue:100,
                fieldLabel :_('New Discount'),
                itemId : 'addDiscount'
            },
            {
                xtype : "textarea",
                anchor : '90%',
                maxLength : 255,
                itemId : 'comment',
                fieldLabel :_('Comments')
            }
            ]
        });
        this.items = [
        this.form
        ];
        mFino.widget.BulkLOPDiscountWindow.superclass.initComponent.call(this);
    },

    close : function(){
        this.hide();
    },

    save : function(){
        if(this.form.getForm().isValid()){

            this.store.un("write", this.successNotify);
            this.store.on("write", this.successNotify, this,
            {
            });
            if(!this.discountRecord) {
                this.discountRecord = new this.store.recordType();
            }
            this.discountRecord.beginEdit();
            this.discountRecord.set(CmFinoFIX.message.JSLOPHistory.Entries.LOPID._name, this.record.get(CmFinoFIX.message.JSLOP.Entries.ID._name));
            this.discountRecord.set(CmFinoFIX.message.JSLOPHistory.Entries.OldDiscount._name, this.record.get(CmFinoFIX.message.JSLOP.Entries.Commission._name));
            this.discountRecord.set(CmFinoFIX.message.JSLOPHistory.Entries.NewDiscount._name, this.form.items.get('addDiscount').getValue());
            this.discountRecord.set(CmFinoFIX.message.JSLOPHistory.Entries.Comments._name, this.form.items.get('comment').getValue());
            this.discountRecord.endEdit();

            if(this.store){
                if(this.discountRecord.phantom && !(this.discountRecord.store)){
                    this.store.insert(0, this.discountRecord);
                }
                this.store.save();
            }
        }
    },
    successNotify: function(){
        Ext.ux.Toast.msg(_("Message"), _("Record saved successfully"));
        this.discountRecord = null;
        this.hide();
        this.record.store.load();
    },
    setRecord : function(record){
        this.form.getForm().reset();
        this.record = record;
        this.form.getForm().loadRecord(record);
        this.form.getForm().clearInvalid();
    }
});

