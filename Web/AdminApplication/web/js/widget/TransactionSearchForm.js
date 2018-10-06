/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.TransactionSearchForm = function (config) {
    console.log("@kris: TransactionSearchForm");
    ChargeTransactionSearchForm
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        id : "transactionsearchfrom",
        labelPad : 10,
        labelWidth : 90,
        frame:true,
        title: _('Transaction Search'),
        bodyStyle:'padding:5px 5px 0',
        buttonAlign: 'right',
        items: [ {
            layout : 'form',
            items : [
            {
                xtype : "enumdropdown",
                fieldLabel: _('State'),
                labelSeparator : '',
                anchor : '98%',
                itemId : "transferstate",
                enumId : CmFinoFIX.TagID.TransferState,
                name: CmFinoFIX.message.JSCommodityTransfer.TransferState._name,
                addEmpty : false,
                value : CmFinoFIX.TransferState.Pending,
                listeners : {
                    select : function(field){
                        this.findParentByType("transactionsearchform").stateChange(field.getValue());
                    },
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            {
                xtype : "enumdropdown",
                fieldLabel: _('Status'),
                labelSeparator : '',
                itemId: "transferstatus",
                anchor : '98%',
                disabled : true,
                emptyText : _('<select one..>'),
                enumId : CmFinoFIX.TagID.TransactionsTransferStatus,
                name: CmFinoFIX.message.JSCommodityTransfer.TransactionsTransferStatus._name,
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
                itemId:"transferid",
                maxLength : 16,
                minValue:0,
                anchor: '98%',
                name: CmFinoFIX.message.JSCommodityTransfer.IDSearch._name,
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
                id:"tradaterange",
                anchor :'100%',
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            //            {
            //                xtype:'datefield',
            //                fieldLabel:_('Completed Date'),
            //                labelSeparator : '',
            //                anchor :'95%',
            //                name: CmFinoFIX.message.JSCommodityTransfer.CreateTimeSearch._name,
            //                listeners   : {
            //                    specialkey: this.enterKeyHandler.createDelegate(this)
            //                }
            //
            //            },
            //            {
            //                xtype:'displayfield',
            //                labelSeparator:''
            //            },
            {
                xtype : "combo",
                fieldLabel: _('Channel Name'),
                labelSeparator : '',
                itemId: "accessmethod",
                anchor : '98%',
                triggerAction: "all",
                minChars : 2,
//                editable : true,
                forceSelection : true,
                pageSize : 20,
                addEmpty : true,
                emptyText : _('<select one..>'),
                store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSChannelCode),
                displayField: CmFinoFIX.message.JSChannelCode.Entries.ChannelName._name,
                valueField : CmFinoFIX.message.JSChannelCode.Entries.ChannelSourceApplication._name,
                hiddenName : CmFinoFIX.message.JSCommodityTransfer.SourceApplicationSearch._name,
                name: CmFinoFIX.message.JSCommodityTransfer.SourceApplicationSearch._name,
                listeners   : {
//                    focus: function(){
//                        this.reload();
//                    },
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
                fieldLabel: _('SourceOrDest  MDN'),
                labelSeparator : '',
                itemId: "subscriberid",
                maxLength : 13,
                minValue:0,
                anchor: '98%',
                name: CmFinoFIX.message.JSCommodityTransfer.SourceDestnMDN._name,
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
                maxLength : 13,
                anchor: '98%',
                name: CmFinoFIX.message.JSCommodityTransfer.SourceMDN._name,
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
                maxLength : 13,
                anchor: '98%',
                name: CmFinoFIX.message.JSCommodityTransfer.DestMDN._name,
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
                fieldLabel: _('Source Ref.No'),
                labelSeparator : '',
                itemId:"sourcerefnum",
                maxLength : 16,
                minValue:0,
                anchor: '98%',
                name: CmFinoFIX.message.JSCommodityTransfer.SourceReferenceID._name,
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
                fieldLabel: _('Destination Ref.No'),
                labelSeparator : '',
                itemId:"destrefnum",
                maxLength : 16,
                minValue:0,
                anchor: '98%',
                name: CmFinoFIX.message.JSCommodityTransfer.OperatorAuthorizationCode._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            },
            {
                xtype:'displayfield',
                labelSeparator:''
            },
            {
                xtype : "enumdropdown",
                fieldLabel: _('Transaction'),
                labelSeparator : '',
                emptyText : _('<select one..>'),
                enumId : CmFinoFIX.TagID.TransactionUICategory,
                itemId:"transactionSearchFormAction",
                anchor: '98%',
                name: CmFinoFIX.message.JSCommodityTransfer.TransactionUICategory._name,
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
                    color : 'green',
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

    mFino.widget.TransactionSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.TransactionSearchForm, Ext.FormPanel, {
    initComponent : function () {
        mFino.widget.TransactionSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
    },

    stateChange : function(value){
    	 var compEl = this.find('id','tradaterange')[0];
        if(value==CmFinoFIX.TransferState.Pending){
            this.find('itemId','transferstatus')[0].disable();
            this.find('itemId','transferstatus')[0].setValue("");
            if(compEl){
                compEl.triggerField.setValue(compEl.triggerField.emptyText) ;
                 compEl.startDateField.setValue(null);
                 compEl.endDateField.setValue(null);
             }
        }else if(value==CmFinoFIX.TransferState.Complete){
            this.find('itemId','transferstatus')[0].enable();
            if(compEl){
                var sdate = new Date().add(Date.DAY, -1);
                var edate = new Date();
                var trigger={start:sdate,end:edate};
                compEl.triggerField.setValue(trigger) ;
                compEl.startDateField.setValue(sdate);
                compEl.endDateField.setValue(edate);
            }
        }
    },
    
    stateChangeOutSide : function(value){
   	 var compEl = this.find('id','tradaterange')[0];
       if(value==CmFinoFIX.TransferState.Pending){
           this.find('itemId','transferstatus')[0].disable();
           this.find('itemId','transferstatus')[0].setValue("");
       }else if(value==CmFinoFIX.TransferState.Complete){
           this.find('itemId','transferstatus')[0].enable();
       }
       if(compEl){
           compEl.triggerField.setValue(compEl.triggerField.emptyText) ;
           compEl.startDateField.setValue(null);
           compEl.endDateField.setValue(null);
       }
   },
    
    
    
    searchTransactions : function(){
        var tab_holder = Ext.get("menu1");
        tab_holder.setCurrent("transactions");
        var contentPanel = Ext.getCmp("contentPanel");
        contentPanel.layout.setActiveItem("transactions");
        contentPanel.doLayout();
        this.searchHandler();
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    
    searchHandler : function(){
        console.log("@kris: TransactionSearchForm.searchHandler");
        if(this.getForm().isValid()){
            var values = this.getForm().getValues();
            //the date range picker seems to out put the value even if it is disabled.
            //            if(Ext.getCmp("tradaterange").disabled){
            //                values.startDate = null;
            //                values.endDate = null;
            //            }
           // var iso_date = Date.parseDate(values.startDate, "Y/m/d");
            var currdatetime= new Date();
            var edate1=values.endDate;
            if(values.SourceDestnMDN==''){
            	values.SourceDestnMDN=null;
            }
            var d1=currdatetime.format("ymd");
            var d2=Ext.util.Format.substr(edate1, 2, 6);
            if(d2!="")
            	 {
           	 		if(d1 == d2)
            	 	{
           	 			values.endDate = currdatetime;
             	 	}
            	 }
            //alert(values.startDate+"    "+values.endDate);
            this.fireEvent("search", values);
            
        }
        else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
        this.stateChange(CmFinoFIX.TransferState.Pending);
    }
});
Ext.reg("transactionsearchform", mFino.widget.TransactionSearchForm);