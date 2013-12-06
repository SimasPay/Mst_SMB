/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.Recharge = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        id: "Recharge",
        bodyStyle: 'padding:5px 5px 0',
        defaultType: 'textfield',                
        frame : true
    });
    mFino.widget.Recharge.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.Recharge, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 140;
        this.labelPad = 10;
        this.defaults = {
            anchor: '85%',
            labelSeparator : ''
        };
        this.items = [
        {
            // Use the default, automatic layout to distribute the controls evenly
            // across a single row        
            fieldLabel: _('Phone Number'),
            allowBlank: false,
            vtype: 'smarttelcophoneAdd',
            maxLength : 16,
            emptyText:'eg: 6211256874',
            name:  CmFinoFIX.message.JSMerchantRecharge.DestMDN._name
        },
        {
            xtype : "displayfield",
            fieldLabel :_('Commodity'),
            value: "Airtime"
        },
        {
            xtype : "enumdropdown",
            fieldLabel :_('Bucket Type'),
            allowBlank: false,
            blankText : _('Bucket Type is required'),
            enumId : CmFinoFIX.TagID.BucketType_Recharge,
            emptyText: _('<Select One>'),
            name : CmFinoFIX.message.JSMerchantRecharge.BucketType_Recharge._name,
            value: CmFinoFIX.BucketType_Recharge.Regular
        },
        {
            xtype: "textfield",
            fieldLabel: _('Recharge Amount'),
            allowBlank: false,
            emptyText: _('eg: 1125'),
            vtype:'numbercomma',
            maxLength : 16,
            name : CmFinoFIX.message.JSMerchantRecharge.RechargeAmount._name,
            listeners: {
                blur:  function(field){
                    field.setValue(Ext.util.Format.number(field.getValue(), '0,000'));
                }
            }
        }
        //        ,{
        //            fieldLabel: _('Merchant PIN'),
        //            inputType: 'password',
        //            allowBlank: false,
        //            vtype:'pin',
        //            name: CmFinoFIX.message.JSMerchantRecharge.Pin._name
        //        }
        ] ;

        mFino.widget.Recharge.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        if(this.record){
            this.getForm().loadRecord(this.record);
        }
    },
    setMDN : function(mdn){
      this.merchantMdn = mdn;
    },
    recharge: function(formWindow) {
        if(this.getForm().isValid()){
            var msg= new CmFinoFIX.message.JSMerchantRecharge();
            var values = this.form.getValues();
            var rechargeAmount = values[CmFinoFIX.message.JSMerchantRecharge.RechargeAmount._name];
            rechargeAmount =  rechargeAmount.replace(/\,/g,'');
            msg.m_pBucketType_Recharge = values[CmFinoFIX.message.JSMerchantRecharge.BucketType_Recharge._name];
            msg.m_pDestMDN = values[CmFinoFIX.message.JSMerchantRecharge.DestMDN._name];
            msg.m_pPin = "";
            msg.m_pRechargeAmount = rechargeAmount;
            
            //FIXME: Isn't there a better way to do this?
            msg.m_pSourceMDN = this.merchantMdn;
            var params = mFino.util.showResponse.getDisplayParam();
            params.formWindow = formWindow;
            mFino.util.fix.send(msg, params);           
        }
    }
});

