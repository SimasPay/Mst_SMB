/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.Transfer = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle: 'padding:5px 5px 0',
        defaultType: 'textfield',
        frame : true
    });
    mFino.widget.Transfer.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.Transfer, Ext.FormPanel, {
   
    initComponent : function () {
        this.labelWidth = 140;
        this.labelPad = 10;
        this.defaults = {
            anchor: '85%',
            labelSeparator : ''
        };
        this.store = new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSMerchant);
        this.items = [
        {
            xtype : 'panel',
            bodyStyle: 'padding:5px 5px 0',
            layout : 'column',
            items : [
            {
                columnWidth: 0.5,
                xtype : "radio",
                boxLabel : '<b>   By Name   </b>',
                name : 'groupone',
                checked : true,
                hideLabel: false,
                labelSeparator: '',
                style:{
                    display:'inline'
                },
                value : 0,
                listeners:{
                    check: function()
                    {
                        if(this.checked){
                            this.findParentByType("transfer").onnameclick();
                        }
                    }
                }
            },
            {
                columnWidth: 0.5,
                xtype : "editableremotedropdown",
                RPCObject : CmFinoFIX.message.JSMerchant,
                itemId:'byname',
                allowBlank : false,
                store : this.store,
                displayField: CmFinoFIX.message.JSMerchant.Entries.Username._name,
                valueField : CmFinoFIX.message.JSMerchant.Entries.Username._name,
                name: CmFinoFIX.message.JSMerchantTransfer.Username._name,
                listeners:
                {
                    blur: function(field) {
                        var msg = new CmFinoFIX.message.JSMerchantNameCheck();
                        msg.m_pUsername = field.getValue();
                        msg.m_pCheckIfExists = false;
                        var checkForExists=false;
                        mFino.util.fix.checkNameInDB(field, msg, checkForExists);
                    }
                }
            }
            ]
        },
        {
            xtype : 'panel',
            bodyStyle: 'padding:5px 5px 0',
            layout : 'column',
            items : [
            {
                columnWidth: 0.5,
                xtype : "radio",
                name : 'groupone',
                boxLabel : '<b>   By MDN    </b>',
                hideLabel: true,
                labelSeparator: '',
                value : 1,
                listeners:{
                    check: function()
                    {
                        if(this.checked){
                            this.findParentByType("transfer").onmdnclick();
                        }
                    }
                }
            },
            {
                columnWidth: 0.5,
                xtype : 'numberfield',
                vtype: 'smarttelcophoneAdd',
                allowBlank : false,
                itemId:'bymdn',
                disabled:true,
                maxLength : 16,
                emptyText: _('eg: 6211256874'),
                name:  CmFinoFIX.message.JSMerchantTransfer.DestMDN._name
            }
            ]
        },
        {
            xtype : "displayfield",
            fieldLabel  : _('Commodity'),
            value: 'Airtime'
        },
        {
            xtype:'textfield',
            fieldLabel: _('Amount Paid'),
            allowBlank:false,
            vtype:'numbercomma',
            emptyText: _('eg: 1125'),
            maxLength : 16,
            name: CmFinoFIX.message.JSMerchantTransfer.TransferAmount._name,
            listeners: {
                blur:  function(field){
                    field.setValue(Ext.util.Format.number(field.getValue(), '0,000'));
                }
            }
        }
        ];

        mFino.widget.Transfer.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        if(this.record){
            this.getForm().loadRecord(this.record);
        }
    },
    onnameclick: function(){
        this.form.items.get('byname').enable();
        this.form.items.get('bymdn').reset();
        this.form.items.get('bymdn').disable();
    },
    onmdnclick: function(){
        this.form.items.get('bymdn').enable();
        this.form.items.get('byname').reset();
        this.form.items.get('byname').disable();
    },
    transfer: function(formWindow) {
        if(this.getForm().isValid()){
            var msg= new CmFinoFIX.message.JSMerchantTransfer();
            var values = this.form.getValues();
            var transferAmount = values[CmFinoFIX.message.JSMerchantTransfer.TransferAmount._name];
            transferAmount =  transferAmount.replace(/\,/g,'');
            msg.m_pPin = "";
            msg.m_pTransferAmount = transferAmount;
            msg.m_pSourceMDN = this.merchantMDN;

            if(this.form.items.get('bymdn').getValue()){
            	msg.m_pDestMDN = this.form.items.get('bymdn').getValue();
            	msg.m_pUsername = "";
            }else if(this.form.items.get('byname').getValue()){
            	msg.m_pDestMDN = "";
            	msg.m_pUsername = this.form.items.get('byname').getValue();
            }

            var params = mFino.util.showResponse.getDisplayParam();
            params.formWindow = formWindow;
            mFino.util.fix.send(msg, params);
        }
    },
    setMerchantID: function(merchantID, merchantMDN){
        this.merchantID = merchantID;
        this.merchantMDN = merchantMDN;
        this.store.baseParams[CmFinoFIX.message.JSMerchant.ParentIDSearch._name] = merchantID;
    }
});

Ext.reg("transfer", mFino.widget.Transfer);

