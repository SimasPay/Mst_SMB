/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.LOPAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle: 'padding:5px 5px 0',
        width: 400,
        frame : true
    });
    mFino.widget.LOPAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.LOPAddForm, Ext.FormPanel, {
    initComponent : function () {

        this.labelWidth = 140;
        this.labelPad = 10;
        this.defaults = {
            anchor: '85%',
            labelSeparator : ''
        };
        this.items = [
        {
            xtype: "displayfield",
            fieldLabel  : _('LOP Limit Per Transaction'),
            itemId: "maxloplimit",
            renderer : 'money'
        },
        {
            xtype: "displayfield",
            fieldLabel  : _('Weekly LOP Limit'),
            itemId: "weeklyloplimit",
            renderer : 'money'
        },
        {
            xtype: "displayfield",
            fieldLabel  : _('Accumulated LOP Amount'),
            itemId:'CurrentWeeklyPurchaseAmount',
            renderer : 'money'
        },
        {
            xtype: "displayfield",
            fieldLabel  : _('Available for LOP'),
            itemId:'AvailableforLOP',
            renderer : 'money'
        },
        {
            xtype: "displayfield",
            fieldLabel  : _('LOP Commission'),
            itemId:'LOPCommission',
            renderer : 'percentage'
        },
        {
            xtype: "displayfield",
            fieldLabel  : _('Group ID'),
            itemId:'groupId',
            anchor : '100%'
        },
        {
            xtype:'textfield',
            fieldLabel: _('Actual Value'),
            itemId:"actualvalue",
            emptyText:_('eg: 1124'),
            allowBlank: false,
            vtype:'numbercomma',
            listeners: {
                blur: function(){
                    this.findParentByType("lopaddform").evaluateActualPaidValueAndSet();                    
                }                
            }
        },
        {
            xtype:'textfield',
            fieldLabel: _('Actual Paid'),
            itemId:"actualpaid",
            emptyText:_('eg: 1124'),
            allowBlank: false,
            vtype:'numbercomma',
            listeners: {
                blur: function(){
                    this.findParentByType("lopaddform").evaluateActualValueValueAndSet();
                }
            }
        },
        {
            xtype: 'textarea',
            fieldLabel: _('Comment'),
            itemId:"lopcomment",
            maxLength : 1000,
            name: CmFinoFIX.message.JSLOP.Entries.Comment._name
        },
        {
            xtype : 'displayfield',
            fieldLabel: _("Payment Details"),
            labelSeparator : '',
            anchor : '100%'
        },
        {
            xtype:'datefield',
            fieldLabel: _('Transfer Date'),
            itemId:"transferdate",
            allowBlank: false,
            editable:false,
            emptyText: _('eg: 10/29/2009'),
            name: CmFinoFIX.message.JSLOP.Entries.TransferDate._name
        },
        {
            xtype:'textfield',
            fieldLabel: _('Giro/Ref number'),
            itemId:"number",
            maxLength : 16,
            allowBlank: false,
            vtype:'alphanum',
            name: CmFinoFIX.message.JSLOP.Entries.GiroRefID._name
        }
        ] ;

        mFino.widget.LOPAddForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        if(this.record){
            this.getForm().loadRecord(this.record);
        }
    },
    save : function(formWindow,actualPaid){
        if(this.getForm().isValid()){
            var msg= new CmFinoFIX.message.JSGenerateLOP();
            msg.m_pLOPActualAmountPaid = actualPaid;
            msg.m_pLOPGiroRefID = this.form.items.get("number").getValue();
            var lopComment = this.form.items.get("lopcomment").getValue();
            var timeentered =this.form.items.get("transferdate").getValue();
            var modifiedtime = timeentered.format(('Ymd'));
            msg.m_pLOPTransferDate = modifiedtime;
            msg.m_pPin = "";

            if(this.merchantMdn){
            	msg.m_pSourceMDN = this.merchantMdn;
            }
            if(lopComment){
            	msg.m_pComment = lopComment;
            }
            var params = mFino.util.showResponse.getDisplayParam();
            params.formWindow = formWindow;
            mFino.util.fix.send(msg, params);
        }
    },
    setLimits : function(mdn,MaxWeeklyPurchaseAmount,CurrentWeeklyPurchaseAmount,AvailableforLOP,LOPCommission,GroupId,maxAmountPerTransaction){
        this.merchantMdn = mdn;
        this.items.get("weeklyloplimit").setValue(MaxWeeklyPurchaseAmount);
        this.items.get("CurrentWeeklyPurchaseAmount").setValue(CurrentWeeklyPurchaseAmount);
        this.items.get("AvailableforLOP").setValue(AvailableforLOP);
        this.items.get("LOPCommission").setValue(LOPCommission);
        this.items.get("groupId").setValue(GroupId);
        this.items.get("maxloplimit").setValue(maxAmountPerTransaction);
    },
    evaluateActualPaidValueAndSet: function() {
        var actualValueValueEntered = this.form.items.get("actualvalue").getValue();
        actualValueValueEntered = actualValueValueEntered.replace(/\,/g,'');
        this.form.items.get("actualvalue").setValue(Ext.util.Format.number(actualValueValueEntered, '0,000'));
        var commission;
        var actualpaid;
        if(actualValueValueEntered) {
            commission = this.form.items.get("LOPCommission").getValue();
            actualpaid = actualValueValueEntered - ((actualValueValueEntered *commission)/100);
            actualpaid = Math.round(actualpaid);
            this.form.items.get("actualpaid").setValue(Ext.util.Format.number(actualpaid, '0,000'));
        } else {
            this.form.items.get("actualpaid").setValue("");
        }        
    },
    evaluateActualValueValueAndSet: function() {
        var actualPaidValueEntered = this.form.items.get("actualpaid").getValue();
        actualPaidValueEntered = actualPaidValueEntered.replace(/\,/g,'');
        this.form.items.get("actualpaid").setValue(Ext.util.Format.number(actualPaidValueEntered, '0,000'));
        var commission;
        var actualvalue;
        if(actualPaidValueEntered) {
            commission = this.form.items.get("LOPCommission").getValue() ;
            actualvalue = (actualPaidValueEntered *100)/(100- commission);
            actualvalue = Math.round(actualvalue);
            this.form.items.get("actualvalue").setValue(Ext.util.Format.number(actualvalue, '0,000'));
        } else {
            this.form.items.get("actualvalue").setValue("");
        }        
    }
});

Ext.reg("lopaddform", mFino.widget.LOPAddForm);
