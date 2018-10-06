/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PartnerTransactionSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        id : "partnertransactionsearchfrom",
        labelPad : 10,
        labelWidth : 90,
        frame:true,
        title: _('Partner Transaction Search'),
        bodyStyle:'padding:5px 5px 0',
        buttonAlign: 'right',
        items: [ {
            layout : 'form',
            items : [
            {
                xtype : "textfield",
                fieldLabel: _('TradeName'),
                labelSeparator : '',
                anchor : '98%',
                itemId : "partnertradename",
                name: CmFinoFIX.message.JSTransactionAmountDistributionLog.TradeNameSearch._name,
                listeners : {
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
                itemId: "partnertransferstatus",
                anchor : '98%',
                emptyText : _('<select one..>'),
                enumId : CmFinoFIX.TagID.TransactionsTransferStatus,
                name: CmFinoFIX.message.JSTransactionAmountDistributionLog.TransactionsTransferStatus._name,
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
                itemId:"partnertransferid",
                maxLength : 16,
                minValue:0,
                anchor: '98%',
                name: CmFinoFIX.message.JSTransactionAmountDistributionLog.ServiceChargeTransactionLogID._name,
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
                id:"tradaterange1",
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
//                xtype : "combo",
//                fieldLabel: _('Channel Name'),
//                labelSeparator : '',
//                itemId: "partneraccessmethod",
//                anchor : '98%',
//                triggerAction: "all",
//                minChars : 2,
////                editable : true,
//                forceSelection : true,
//                pageSize : 20,
//                addEmpty : true,
//                emptyText : _('<select one..>'),
//                store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSChannelCode),
//                displayField: CmFinoFIX.message.JSChannelCode.Entries.ChannelName._name,
//                valueField : CmFinoFIX.message.JSChannelCode.Entries.ChannelSourceApplication._name,
//                hiddenName : CmFinoFIX.message.JSCommodityTransfer.SourceApplicationSearch._name,
//                name: CmFinoFIX.message.JSCommodityTransfer.SourceApplicationSearch._name,
//                listeners   : {
////                    focus: function(){
////                        this.reload();
////                    },
//                    specialkey: this.enterKeyHandler.createDelegate(this)
//                }
//            },
            {
                xtype:'displayfield',
                labelSeparator:''
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

    mFino.widget.PartnerTransactionSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PartnerTransactionSearchForm, Ext.FormPanel, {
    initComponent : function () {
        mFino.widget.PartnerTransactionSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
    },

    stateChange : function(value){
    	 var compEl = this.find('id','tradaterange1')[0];
//        if(value==CmFinoFIX.TransferState.Pending){
//            this.find('itemId','partnertransferstatus')[0].disable();
//            this.find('itemId','partnertransferstatus')[0].setValue("");
//            if(compEl){
//                compEl.triggerField.setValue(compEl.triggerField.emptyText) ;
//                 compEl.startDateField.setValue(null);
//                 compEl.endDateField.setValue(null);
//             }
//        }else if(value==CmFinoFIX.TransferState.Complete){
            this.find('itemId','partnertransferstatus')[0].enable();
            if(compEl){
                var sdate = new Date().add(Date.DAY, -1);
                var edate = new Date();
                var trigger={start:sdate,end:edate};
                compEl.triggerField.setValue(trigger) ;
                compEl.startDateField.setValue(sdate);
                compEl.endDateField.setValue(edate);
//            }
        }
    },

    searchTransactions : function(){
        var tab_holder = Ext.get("menu1");
        tab_holder.setCurrent("partnertransactions");
        var contentPanel = Ext.getCmp("contentPanel");
        contentPanel.layout.setActiveItem("partnertransactions");
        contentPanel.doLayout();
        this.searchHandler();
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    
    searchHandler : function(){
        console.log("@kris: PartnerTransactionSearchForm.searchHandler");
        if(this.getForm().isValid()){
            var values = this.getForm().getValues();
            //the date range picker seems to out put the value even if it is disabled.
            //            if(Ext.getCmp("tradaterange1").disabled){
            //                values.startDate = null;
            //                values.endDate = null;
            //            }
            
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
Ext.reg("partnertransactionsearchform", mFino.widget.PartnerTransactionSearchForm);