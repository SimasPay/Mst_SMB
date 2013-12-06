/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.TransactionChargeSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Transaction Charge Search'),
        bodyStyle:'padding:5px 5px 0',
        items : [
              {
                  xtype : "remotedropdown",
                  anchor : '98%',
                  fieldLabel :_("Transaction Rule"),
                  labelSeparator : '',
				  emptyText : _('<select one..>'),
                  itemId : 'transactioncharge.form.transactionrulesearch',
                  RPCObject : CmFinoFIX.message.JSTransactionRule,
                  displayField: CmFinoFIX.message.JSTransactionRule.Entries.Name._name,
                  valueField : CmFinoFIX.message.JSTransactionRule.Entries.ID._name,
                  hiddenName : CmFinoFIX.message.JSTransactionCharge.TransactionRuleSearch._name,
                  name: CmFinoFIX.message.JSTransactionCharge.TransactionRuleSearch._name,
                  pageSize : 10,
                  params: {start: 0, limit: 10}
              },              
              {
                  xtype : "remotedropdown",
                  anchor : '98%',
                  fieldLabel :_("Charge Type"),
                  labelSeparator : '',
				  emptyText : _('<select one..>'),
                  itemId : 'transactioncharge.form.chargetypesearch',
                  RPCObject : CmFinoFIX.message.JSChargeType,
                  displayField: CmFinoFIX.message.JSChargeType.Entries.Name._name,
                  valueField : CmFinoFIX.message.JSChargeType.Entries.ID._name,
                  hiddenName : CmFinoFIX.message.JSTransactionCharge.ChargeTypeSearch._name,
                  name: CmFinoFIX.message.JSTransactionCharge.ChargeTypeSearch._name,
                  pageSize : 10,
                  params: {start: 0, limit: 10}
              },              
              {
                  xtype : "remotedropdown",
                  anchor : '98%',
                  fieldLabel :_("Charge Definition"),
                  labelSeparator : '',
				  emptyText : _('<select one..>'),
                  itemId : 'transactioncharge.form.chargedefinitionsearch',
                  RPCObject : CmFinoFIX.message.JSChargeDefinition,
                  displayField: CmFinoFIX.message.JSChargeDefinition.Entries.Name._name,
                  valueField : CmFinoFIX.message.JSChargeDefinition.Entries.ID._name,
                  hiddenName : CmFinoFIX.message.JSTransactionCharge.ChargeDefinitionSearch._name,
                  name: CmFinoFIX.message.JSTransactionCharge.ChargeDefinitionSearch._name,
                  pageSize : 10
              },              
              {
                  xtype : 'daterangefield',
                  fieldLabel: _('Date range'),
                  labelSeparator : '',
                  anchor :'98%',
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              }
              ]
        
    });

    mFino.widget.TransactionChargeSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.TransactionChargeSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        
        mFino.widget.TransactionChargeSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
        
        this.on("render", function(){
            this.reloadRemoteDropDown();
        });        
    },
    
    reloadRemoteDropDown : function(){
    	this.getForm().items.each(function(item) {
            if(item.getXType() == 'remotedropdown') {
                item.reload();
            }
        });
    },    

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },

    searchHandler : function(){
        if(this.getForm().isValid()){
            var values = this.getForm().getValues();
            this.fireEvent("search", values);
        } else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    }
});

Ext.reg("transactionchargesearchform", mFino.widget.TransactionChargeSearchForm);