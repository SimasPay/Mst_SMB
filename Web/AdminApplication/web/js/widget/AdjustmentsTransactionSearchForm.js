/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.AdjustmentsTransactionSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {        
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Adjustments Search'),
        bodyStyle:'padding:5px 5px 0',
        items : [
                 {
	            	xtype : 'numberfield',
	            	allowDecimals:false,
	            	minValue:0,
		            fieldLabel: _('Sctl ID'),
		            labelSeparator : '',
		            anchor: '98%',
		            name: CmFinoFIX.message.JSAdjustments.SctlId._name,
		            listeners   : {
		                specialkey: this.enterKeyHandler.createDelegate(this)
		            }
	            },
                {
                    xtype : "enumdropdown",
                    fieldLabel: _('Adjustment Status'),
                    labelSeparator : '',
                    anchor : '98%',
                    emptyText : _('<select one..>'),
                    enumId : CmFinoFIX.TagID.AdjustmentStatus,
                    name: CmFinoFIX.message.JSAdjustments.AdjustmentStatus._name,
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

    mFino.widget.AdjustmentsTransactionSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.AdjustmentsTransactionSearchForm, Ext.FormPanel, {
	initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        
        mFino.widget.AdjustmentsTransactionSearchForm.superclass.initComponent.call(this);
        
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
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    }    
});

Ext.reg("adjustmentstransactionsearchform", mFino.widget.AdjustmentsTransactionSearchForm);