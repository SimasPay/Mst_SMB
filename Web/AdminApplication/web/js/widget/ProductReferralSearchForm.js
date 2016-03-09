/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ProductReferralSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
   
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 60,
        layout:'column',
        labelWidth : 160,
        frame:true,
        title: _('Product Referral'),
        bodyStyle:'padding:17px 12px 0',
        items: [
		{
		  columnWidth:0.25,
          layout:'form',
          labelWidth:80,
          items:[
          {
             
        	  
        	  xtype:'enumdropdown',
              fieldLabel:'ProductDesired',
              //mode: 'local',
              //itemId:'filetype',
              triggerAction: 'all',
              emptyText : _('<Select one..>'),
              enumId : CmFinoFIX.TagID.ProductDesired,
              name: CmFinoFIX.message.JSProductReferral.ProductDesiredSearch._name,
              anchor:'90%',
              listeners   : {
                  specialkey: this.enterKeyHandler.createDelegate(this)
              }
        	  
        	  
          }
          ]
      },
      
        {
    	  columnWidth:0.18,
          layout:'form',
          labelWidth:60,
          items:[
          {
              xtype : 'textfield',
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
          }
          ]
      },
		
      
      {
    	  columnWidth:0.23,
          layout:'form',
          labelWidth:60,
          items:[
          {
        	     xtype : 'daterangefield',
                  fieldLabel: _('DateRange'),
                  labelSeparator : '',
                  anchor :'98%',
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              } 
          
          ]
      },
	
      {
    	  columnWidth:0.19,
          layout:'form',
          labelWidth:70,
          items:[
          {
              xtype : 'textfield',
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
          }
          ]
      },        
        {  columnWidth:0.05,
            layout:'form',
            items:[
            {
                xtype:'displayfield',
                anchor:'50%'
            }
            ]
        },
		{
            columnWidth:0.05,
            layout:'form',
            items:[
            {
                xtype:'button',
                text:'Search',
                anchor:'50%',
                handler : this.searchHandler.createDelegate(this)
            }            
            ]
        },
		{
            columnWidth:0.05,
            layout:'form',
            items:[
            {
                xtype: 'button',
                text: _('Reset'),
                anchor:'50%',
                handler : this.resetHandler.createDelegate(this)
            }            
            ]
        }
        
        ]
    });

    mFino.widget.ProductReferralSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ProductReferralSearchForm, Ext.FormPanel, {

    initComponent : function () {
        mFino.widget.ProductReferralSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
        //this.form.items.get("productDesired").getStore().reload();
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
        }else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    }
});

