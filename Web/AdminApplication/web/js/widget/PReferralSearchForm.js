/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PReferralSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Product Referral Search'),
        bodyStyle:'padding:5px 5px 0',
        items : [
		
        {
              xtype:'enumdropdown',
              fieldLabel:'ProductDesired',              
              triggerAction: 'all',
              emptyText : _('<Select one..>'),
              enumId : CmFinoFIX.TagID.ProductDesired,
              name: CmFinoFIX.message.JSProductReferral.ProductDesiredSearch._name,
              anchor:'90%',
              listeners   : {
                  specialkey: this.enterKeyHandler.createDelegate(this)}
              
		} ,  
             
		{  	  xtype : 'textfield',
              allowDecimals:false,
              fieldLabel: _("AgentMDN"),
              labelSeparator : '',
              maxLength:50000,
              minValue:0,
              name: CmFinoFIX.message.JSProductReferral.AgentMDNSearch._name,
              anchor : '98%',
              listeners   : {
                  specialkey: this.enterKeyHandler.createDelegate(this)
              }
		},
		
		{  	 xtype : 'textfield',
            allowDecimals:false,
            fieldLabel: _("SubscriberMDN"),
            labelSeparator : '',
            maxLength:50000,
            minValue:0,
            name: CmFinoFIX.message.JSProductReferral.SubscriberMDNSearch._name,
            anchor : '98%',
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
		},
		
		
		{  	 xtype : 'daterangefield',
            fieldLabel: _('DateRange'),
            labelSeparator : '',
            anchor :'98%',
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }		
		}
		
		]             
        
    });

    mFino.widget.PReferralSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PReferralSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        
        mFino.widget.PReferralSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
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

Ext.reg("PReferralSearchForm", mFino.widget.PReferralSearchForm);