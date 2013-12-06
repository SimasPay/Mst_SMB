/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PocketTransactionsSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px',
//        width:800,
//        height:100,
        frame:true,
        border:true,
        items: [{
        	 layout:'column',
             border:false,
             labelWidth : 90,
             border:false,                   
                items: [
                        {
		                    columnWidth:0.5,
		                    layout: 'form',
		                    labelWidth : 90,
		                    border:false,
		                    items: [{                    
		                    	 xtype : 'numberfield',
		                         allowDecimals:false,
		                         fieldLabel: _('Reference ID'),
		                         labelSeparator : '',
		                         maxLength : 16,
		                         minValue:0,
		                         anchor: '80%',
		                         name: CmFinoFIX.message.JSCommodityTransfer.ServiceChargeTransactionLogID._name,
		                         listeners   : {
		                             specialkey: this.enterKeyHandler.createDelegate(this)
		                         	}
		                    	}]
		    			},
		    			{
                        columnWidth:0.5,
                        layout: 'form',
                        labelWidth : 90,
                        border:false,
                        items: [{
				                    xtype : 'daterangefield',
				                    fieldLabel: _('Transaction Period'),
				                    labelSeparator : '',
				                    anchor :'80%',
				//                    id: "ptdaterange",
				                    listeners : {
				                        specialkey: this.enterKeyHandler.createDelegate(this)
				                    }
				                }]
		    			}        			
		    		]
        }]
    });

    mFino.widget.PocketTransactionsSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketTransactionsSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this.buttons = [
        {
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        },
        ' ',' ', ' '
        ];
        mFino.widget.SubscriberSearchForm.superclass.initComponent.call(this);
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    searchHandler : function(){
        if(this.getForm().isValid()){
        	 var values = this.getForm().getValues();
        	 if(values.TransactionsTransferStatus === "undefined"){
                 values.TransactionsTransferStatus =null;
             }
        	  var currdatetime= new Date();
              var edate1=values.endDate;
              
              var d1=currdatetime.format("ymd");
              var d2=Ext.util.Format.substr(edate1, 2, 6);
              if(d2!="")
              	 {
             	 		if(d1 == d2)
              	 	{
             	 			values.endDate = currdatetime.format('Ymd-H:i:s:u');
               	 	}
              	 }
              this.fireEvent("search", values);
        }
        else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    }
});
