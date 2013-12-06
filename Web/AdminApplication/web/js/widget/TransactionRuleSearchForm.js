/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.TransactionRuleSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Transaction Rule Search'),
        bodyStyle:'padding:5px 5px 0',
        items : [
              {
                  xtype : 'textfield',
                  fieldLabel: _('Name'),
                  labelSeparator : '',
                  anchor:'98%',
                  maxLength:255,
                  name: CmFinoFIX.message.JSTransactionRule.NameSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },
              {
                  xtype: "remotedropdown",
                  fieldLabel: _('Service Type'),
				    emptyText : _('<select one..>'),
                  labelSeparator : '',
                  anchor:'98%',
                  RPCObject : CmFinoFIX.message.JSService,
                  displayField: CmFinoFIX.message.JSService.Entries.ServiceName._name,
                  valueField : CmFinoFIX.message.JSService.Entries.ID._name,
                  hiddenName : CmFinoFIX.message.JSTransactionRule.ServiceTypeNameSearch._name,
                  name: CmFinoFIX.message.JSTransactionRule.ServiceTypeNameSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this),
                      
                      select: function(field) {
              			this.findParentByType('transactionrulesearchform').getTransactions(field.getValue());
                      }
                  }
              },
              {
                  xtype: "remotedropdown",
                  labelWidth : 100,
                  fieldLabel: _('Transaction Type'),
                  labelSeparator : '',
				  emptyText : _('<select one..>'),
                  anchor:'98%',
                  itemId : 'transactionrule.search.transactiontype',                  
                  lastQuery: '',
                  store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSTransactionsForService),
                  displayField: CmFinoFIX.message.JSTransactionsForService.Entries.TransactionName._name,
                  valueField : CmFinoFIX.message.JSTransactionsForService.Entries.TransactionTypeID._name,
                  hiddenName : CmFinoFIX.message.JSTransactionRule.TransactionTypeNameSearch._name,
                  name: CmFinoFIX.message.JSTransactionRule.TransactionTypeNameSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },
              {
                  xtype: 'remotedropdown',
                  labelWidth : 100,
                  fieldLabel: _('Channel'),
				    emptyText : _('<select one..>'),
                  labelSeparator : '',
                  anchor:'98%',
                  RPCObject : CmFinoFIX.message.JSChannelCode,
                  displayField: CmFinoFIX.message.JSChannelCode.Entries.ChannelName._name,
                  valueField : CmFinoFIX.message.JSChannelCode.Entries.ID._name,
                  hiddenName : CmFinoFIX.message.JSTransactionRule.AccessChannelSearch._name,
                  name: CmFinoFIX.message.JSTransactionRule.AccessChannelSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },    
              {
                  xtype: 'enumdropdown',                   
                  fieldLabel: _('Charge Mode'),
                  labelSeparator:':',
				    emptyText : _('<select one..>'),
                  anchor:'98%',
                  enumId : CmFinoFIX.TagID.ChargeMode,
                  name : CmFinoFIX.message.JSTransactionRule.ChargeModeSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }                  
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

    mFino.widget.TransactionRuleSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.TransactionRuleSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        
        mFino.widget.TransactionRuleSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
        
        this.on("render", function(){
            this.reloadRemoteDropDown();
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
    },
    
    reloadRemoteDropDown : function(){
    	this.getForm().items.each(function(item) {
            if(item.getXType() == 'remotedropdown') {
                item.reload();
            }
        });
    },
    
    getTransactions : function(field) {
    	var tr_combo = this.find('itemId','transactionrule.search.transactiontype')[0];
    	tr_combo.clearValue();
    	tr_combo.store.reload({
    		params: {ServiceID : field}
    	});
    }    
});

Ext.reg("transactionrulesearchform", mFino.widget.TransactionRuleSearchForm);