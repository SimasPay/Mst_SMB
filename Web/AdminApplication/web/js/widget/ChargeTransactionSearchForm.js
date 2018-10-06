/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ChargeTransactionSearchForm = function (config) {
    console.log("@kris: ChargeTransactionSearchForm");
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        id : "chargetransactionsearchfrom",
        labelPad : 10,
        labelWidth : 90,
        frame:true,
        title: _('Transaction Search'),
        bodyStyle:'padding:5px 5px 0',
        buttonAlign: 'right',
        items: [ {
            layout : 'form',
            itemId : 'items',
            items : [
             {
                xtype : "enumdropdown",
                fieldLabel: _('Status'),
                labelSeparator : '',
                itemId: "chargedistributionstatus",
                anchor : '98%',
                emptyText : _('<select one..>'),
                enumId : CmFinoFIX.TagID.SCTLStatus,
                name: CmFinoFIX.message.JSServiceChargeTransactions.Status._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            {
                xtype : 'numberfield',
                allowDecimals:false,
                fieldLabel: _('Reference ID'),
                labelSeparator : '',
                itemId:"transactionid",
                maxLength : 16,
                minValue:0,
                anchor: '98%',
                name: CmFinoFIX.message.JSServiceChargeTransactions.IDSearch._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
                        
            {
                xtype:'displayfield',
                labelSeparator:''
            },
                       	    
            { xtype : "remotedropdown", 
            fieldLabel: _('Service Name'), 
            labelSeparator : '', 
            //itemId: "transactiontype", 	                
            anchor : '98%', 
            pageSize: 10,
            params: {start: 0, limit: 10},
            emptyText : _('<select one..>'),
            store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSService), 
            displayField: CmFinoFIX.message.JSService.Entries.ServiceName._name, 
            valueField : CmFinoFIX.message.JSService.Entries.ID._name,            
            name: CmFinoFIX.message.JSServiceChargeTransactions.ServiceID._name, 
            hiddenName : CmFinoFIX.message.JSServiceChargeTransactions.ServiceID._name, 
            listeners   : { 
                specialkey: this.enterKeyHandler.createDelegate(this) 
            }   
	            
        },    
	               
            { 
	                xtype:'displayfield', 
	                labelSeparator:'' 
	        }, 
	        { 
	        		xtype : "remotedropdown", 
	                fieldLabel: _('Transaction type'), 
	                labelSeparator : '', 
	                //itemId: "transactiontype", 	                
	                anchor : '98%', 
	                pageSize: 10,
	                params: {start: 0, limit: 10},
	                emptyText : _('<select  >'), 
	                store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSTransactionType), 
	                displayField: CmFinoFIX.message.JSTransactionType.Entries.TransactionName._name, 
	                valueField : CmFinoFIX.message.JSTransactionType.Entries.ID._name, 
	                hiddenName : CmFinoFIX.message.JSServiceChargeTransactions.TransactionTypeID._name, 
	                name: CmFinoFIX.message.JSServiceChargeTransactions.TransactionTypeID._name, 
	                listeners   : { 
	                    specialkey: this.enterKeyHandler.createDelegate(this) 
	                } 
 	            }, 
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            {
                xtype : 'textfield',
                allowDecimals:false,
                fieldLabel: _('BankRRN'),
                labelSeparator : '',
                itemId:"rrn",
                maxLength : 16,
                minValue:0,
                anchor: '98%',
                name: CmFinoFIX.message.JSServiceChargeTransactions.BankRetrievalReferenceNumber._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            {
                xtype : 'daterangefield',
                fieldLabel: _('Date range'),
                labelSeparator : '',
                id:"sctldaterange",
                anchor :'100%',
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            {
                xtype : 'textfield',
                vtype: 'smarttelcophone',
                fieldLabel: _('Source MDN'),
                labelSeparator : '',
                itemId:"sourcemdn",
                //maxLength : 13,
                anchor: '98%',
                name: CmFinoFIX.message.JSServiceChargeTransactions.SourceMDN._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            {
                xtype : 'textfield',
                vtype: 'smarttelcophone',
                fieldLabel: _('Destination MDN'),
                labelSeparator : '',
                itemId:"destmdn",
                //maxLength : 13,
                anchor: '98%',
                name: CmFinoFIX.message.JSServiceChargeTransactions.DestMDN._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            
            {
                xtype : 'textfield',
                fieldLabel: _('Source PartnerCode'),
                labelSeparator : '',
                itemId:"sourcepartnerid",
                maxLength : 16,
                anchor: '98%',
                name: CmFinoFIX.message.JSServiceChargeTransactions.SourcePartnerCode._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            {
                xtype : 'textfield',
                fieldLabel: _('Dest PartnerCode'),
                labelSeparator : '',
                itemId:"destpartnerid",
                maxLength : 16,
                anchor: '98%',
                name: CmFinoFIX.message.JSServiceChargeTransactions.DestPartnerCode._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            { //added for enhancement #2232
                xtype : 'textfield',
                fieldLabel: _('Biller Code'),
                labelSeparator : '',
                maxLength : 16,
                anchor: '98%',
                name: CmFinoFIX.message.JSServiceChargeTransactions.MFSBillerCode._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            {
            	xtype : "remotedropdown",
                fieldLabel: _('Channel Name'),
                labelSeparator : '',
                itemId: "sctlaccessmethod",
                anchor : '98%',                
                minChars : 2,
                emptyText : _('<select one..>'),
                store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSChannelCode),
                displayField: CmFinoFIX.message.JSChannelCode.Entries.ChannelName._name,
                valueField : CmFinoFIX.message.JSChannelCode.Entries.ChannelSourceApplication._name,
                hiddenName : CmFinoFIX.message.JSServiceChargeTransactions.SourceApplicationSearch._name,
                name: CmFinoFIX.message.JSServiceChargeTransactions.SourceApplicationSearch._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            }
            ]
        },
        {
            layout : 'column',
            items : [
            {
                columnWidth: 0.5,
                xtype:'displayfield',
                anchor:'50%',
                labelSeparator:''
            },
            {
                columnWidth: 0.5,
                xtype: 'button',
                text: _('Search'),
                style : {
                    color : 'red',
                    align : 'right'
                },
                anchor:'50%',
                handler : this.searchHandler.createDelegate(this)
            }
            ]
        },
        {
            xtype:'displayfield',
            labelSeparator:''
        },
        {
            layout : 'column',
            items : [
            {
                columnWidth: 0.5,
                xtype:'displayfield',
                anchor:'50%',
                labelSeparator:''
            },
            {
                columnWidth: 0.5,
                xtype: 'button',
                text: _('Reset'),
                anchor:'30%',
                handler : this.resetHandler.createDelegate(this)
            }
            ]
        }
        ]
    });

    mFino.widget.ChargeTransactionSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChargeTransactionSearchForm, Ext.FormPanel, {
    initComponent : function () {
        mFino.widget.ChargeTransactionSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
        this.on("render", function(){
        	this.reloadRemoteDropDown();
        });
    },

  
    searchTransactions : function(){
        console.log("@kris: searchTransactions");
        var tab_holder = Ext.get("menu1");
        tab_holder.setCurrent("transactions");
        var contentPanel = Ext.getCmp("contentPanel");
        contentPanel.layout.setActiveItem("chargeTransactions");
        contentPanel.doLayout();
        this.searchHandler();
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
        console.log("@kris: ChargeTransactionSearchForm.searchHandler");
        if(this.getForm().isValid()){
            var values = this.getForm().getValues();
           var currdatetime= new Date();
            var edate1=values.endDate;
            
            var d1=currdatetime.format("ymd");
            var d2=Ext.util.Format.substr(edate1, 2, 6);
            if(d2!="")
            	 {
           	 		if(d1 == d2)
            	 	{
           	 			values.endDate = currdatetime;
             	 	}
            	 }
            if(values.Status === "undefined"){
                values.Status =null;
            }
            if(values.TransactionTypeID === "undefined"|| values.TransactionTypeID ===""){
            	values.TransactionTypeID = null;
            }
            if(values[CmFinoFIX.message.JSServiceChargeTransactions.SourceApplicationSearch._name] === "undefined"){
                values.SourceApplicationSearch = null;
            } 
            if(values.SourceMDN === "undefined"||values.SourceMDN===""){
                values.SourceMDN =null;
            }
            if(values.DestMDN === "undefined"||values.DestMDN===""){
                values.DestMDN =null;
            }
            if(values.MFSBillerCode === "undefined"||values.MFSBillerCode===""){
                values.MFSBillerCode =null;
            }
            if(values.BankRetrievalReferenceNumber === "undefined"||values.BankRetrievalReferenceNumber===""){
                values.BankRetrievalReferenceNumber =null;
            }          
            if(values.ServiceID === "undefined"||values.ServiceID===""){
             values.ServiceID =null;
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

Ext.reg("chargetransactionsearchform", mFino.widget.ChargeTransactionSearchForm);