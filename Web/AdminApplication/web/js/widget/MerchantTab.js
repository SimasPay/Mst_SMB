/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MerchantTab = function(config){
    mFino.widget.MerchantTab.superclass.constructor.call(this, config);
};

Ext.extend(mFino.widget.MerchantTab, Ext.TabPanel, {
		
		initComponent : function () {
		
		this.isCashFlowInitialised = true;
		this.isBulkUploadInitialised = false;
		this.isLOPInitialised = false;
		
        var config = this.initialConfig;
        this.cashFlowGrid = new mFino.widget.CashFlowGrid(Ext.apply({
            layout: "fit",
            height: 380
        }, config));
 
        this.bulkUploadGrid = new mFino.widget.BulkUploadGrid(Ext.apply({
            layout: "fit",
            height: 413
        }, config));

        this.LopGrid = new mFino.widget.LOPReportGrid(Ext.apply({
            layout: "fit",
            height: 413
        }, config));
        
        this.cashFlowTab= new Ext.FormPanel({
            bodyStyle:'padding:5px',
            width: 700,
            height:110,
            frame:true,
            border:true,
            labelWidth : 70,
            labelPad : 10,
            itemId : 'cashFlowTab',
//            listeners : {
//        		render : function(tab){
//        			alert("I am alert");
//        		}
//            },
            items: [{
                layout:'column',
                border:false,
                items:[{
                    columnWidth:0.25,
                    layout: 'form',
                    border:false,
                    items: [
                    {
                        xtype : "enumdropdown",
                        fieldLabel: _('State'),
                        labelSeparator : '',
                        anchor : '95%',
                        itemId : "cashflowtransferstate",
                        enumId : CmFinoFIX.TagID.TransferState,
                        addEmpty : false,
                        name: CmFinoFIX.message.JSCashFlow.TransferState._name,
                        value : CmFinoFIX.TransferState.Pending,
                        listeners : {
                            select : function(field){
                                this.findParentByType("merchanttab").stateChange(field.getValue());
                                
                            },
                            specialkey: this.cashFlowEnterKeyHandler.createDelegate(this)
                        }
                    },
                    {
                        xtype : "enumdropdown",
                        fieldLabel  : _('Status'),
                        labelSeparator : '',
                        mode: 'local',
                        itemId:'statusfilter',
                        anchor:'95%',
                        disabled : true,
                        triggerAction: 'all',
                        enumId : CmFinoFIX.TagID.TransactionsTransferStatus,
                        listeners : {
                            specialkey: this.cashFlowEnterKeyHandler.createDelegate(this)
                        }
                    }
                    ]
                },{
                    columnWidth:0.4,
                    layout: 'form',
                    border:false,
                    items: [
                    {
                        xtype: 'daterangefield',
                        fieldLabel: _('Date Range'),
                        itemId:'dateselected',
                        id : 'cashflowdate',
                        labelSeparator : '',
                        anchor : '90%',
                        listeners : {
                            specialkey: this.cashFlowEnterKeyHandler.createDelegate(this)
                        }
                    },
                    {
                        xtype : "enumdropdown",
                        fieldLabel : _('Transaction'),
                        labelSeparator : '',
                        itemId: 'transactionType',
                        mode: 'local',
                        triggerAction: 'all',
                        anchor:'90%',
                        emptyText : _('<Select one..>'),
                        enumId : CmFinoFIX.TagID.TransactionUICategory,
                        listeners : {
                            specialkey: this.cashFlowEnterKeyHandler.createDelegate(this)
                        }
                    },
                    {
                        xtype:'displayfield',
                        labelSeparator:''
                    }
                    ]
                },
                {
                    columnWidth:0.35,
                    labelWidth : 70,
                    layout: 'form',
                    border:false,
                    items: [
                    {
                        xtype : "enumdropdown",
                        fieldLabel  : _('Buy / Sell'),
                        labelSeparator : '',
                        mode: 'local',
                        itemId:'buySell',
                        anchor:'90%',
                        triggerAction: 'all',
                        emptyText : _('<Select one>'),
                        enumId : CmFinoFIX.TagID.TransactionType,
                        listeners : {
                            specialkey: this.cashFlowEnterKeyHandler.createDelegate(this)
                        }
                    },
                    {
                        xtype : "enumdropdown",
                        fieldLabel  : _('Subtotal By'),
                        labelSeparator : '',
                        itemId:'subtotalby',
                        mode: 'local',
                        triggerAction: 'all',
                        anchor:'90%',
                        emptyText : _('<Select one..>'),
                        enumId : CmFinoFIX.TagID.SubtotalBy,
                        listeners : {
                            specialkey: this.cashFlowEnterKeyHandler.createDelegate(this)
                        }
                    },
                    {
                        xtype:'displayfield',
                        labelSeparator:''
                    }
                    ]
                },
                {
                    columnWidth: 0.25,
                    layout : 'form',
                    labelWidth : 70,
                    items : [{
                        xtype : 'numberfield',
                        allowDecimals:false,
                        fieldLabel: _('Reference ID'),
                        labelSeparator : '',
                        itemId:"transferid",
                        maxLength:16,
                        minValue:0,
                        anchor: '95%',
                        name: CmFinoFIX.message.JSCashFlow.IDSearch._name,
                        listeners   : {
                            specialkey: this.cashFlowEnterKeyHandler.createDelegate(this)
                        }
                    }]
                },
                {
                    columnWidth: 0.55,
                    layout: 'form',
                    items : [
                    {
                        fieldLabel: _('Access '),
                        hideLabel : true,
                        layout : 'column',
                        // Distribute controls across 3 even columns, filling each column
                        // from top to bottom before starting the next column
                        labelSeparator : '',
//                        columns: 3,
//                        vertical: true,
                        items: [
//                        {
//                            columnWidth: 0.2,
//                            xtype: 'checkbox',
//                            boxLabel: _(' Phone'),
//                            itemId: 'utk',
//                            checked:false
//                        },
//                        {
//                            columnWidth: 0.2,
//                            xtype: 'checkbox',
//                            boxLabel: _(' SMS'),
//                            itemId: 'sms',
//                            checked:false
//                        },
//                        {
//                            columnWidth: 0.2,
//                            xtype: 'checkbox',
//                            boxLabel: _(' Web'),
//                            itemId: 'web',
//                            checked:false
//                        },
//                        {
//                            columnWidth: 0.2,
//                            xtype: 'checkbox',
//                            boxLabel: _(' Agent UI'),
//                            itemId: 'agentui',
//                            checked:false
//                        }
                        ]
                    }
                    ]
                },
                {
                    columnWidth: 0.2,
                    layout : 'form',
                    items : [{
                        xtype: 'button',
                        text: _('Search'),
                        anchor:'82%',
                        handler:this.onCashFlowSearch.createDelegate(this)
                    }]
                }
                ]
            }]
        });
        this.lopPanelTab = new Ext.FormPanel({
            bodyStyle:'padding:5px',
            width: 700,
            height:78,
            frame:true,
            border:true,
            itemId: 'lopPanelTab',
//            listeners : {
//        		render : function(tab){
//        			alert("I am alert");
//        		}
//            },            
            items: [{
                layout:'column',
                border:false,
                items:[{
                    columnWidth:0.45,
                    layout: 'form',
                    border:false,
                    items: [
                    {
                        xtype: 'numberfield',
                        fieldLabel: _('LOP ID'),
                        itemId : "lopid",
                        labelSeparator : '',
                        maxLength:16,
                        minValue:0,
                        width: 145,
                        listeners : {
                            specialkey: this.lopEnterKeyHandler.createDelegate(this)
                        }
                    },
                    {
                        xtype: 'daterangefield',
                        fieldLabel: _('Create Date'),
                        itemId:'lopdateselected',
                        labelSeparator : '',
                        width: 145,
                        listeners : {
                            specialkey: this.lopEnterKeyHandler.createDelegate(this)
                        }
                    }
                    ]
                },
                {
                    columnWidth:0.45,
                    layout: 'form',
                    border:false,
                    items: [
                    {
                        xtype : "enumdropdown",
                        fieldLabel  : _('LOP Status'),
                        labelSeparator : '',
                        mode: 'local',
                        itemId:'lopstatus',
                        triggerAction: 'all',
                        emptyText : _('<Select one..>'),
                        enumId : CmFinoFIX.TagID.LOPStatus,
                        listeners : {
                            specialkey: this.lopEnterKeyHandler.createDelegate(this)
                        }
                    },
                    {
                        xtype: 'button',
                        text: _('Search'),
                        anchor:'30%',
                        handler:this.onLOPSearch.createDelegate(this)
                    }
                    ]
                }
                ]
            }]
        });

        this.bulkUploadTab = new Ext.FormPanel({
            bodyStyle:'padding:5px',
            width: 700,
            height:78,
            frame:true,
            border:true,
            itemId : 'bulkUploadTab',
//            listeners : {
//        		focus : function(tab){
//        			alert("I am alert");
//        		}
//            },            
            items: [{
                layout:'column',
                border:false,
                items:[{
                    columnWidth:0.45,
                    layout: 'form',
                    border:false,
                    items: [
                    {
                        xtype: 'daterangefield',
                        fieldLabel: _('Create Date'),
                        itemId:'bulkdateselected',
                        labelSeparator : '',
                        width: 145,
                        listeners : {
                            specialkey: this.bulkUploadEnterKeyHandler.createDelegate(this)
                        }
                    },
                    {
                        xtype : "enumdropdown",
                        fieldLabel  : _('Status'),
                        labelSeparator : '',
                        mode: 'local',
                        itemId:'bulkstatus',
                        triggerAction: 'all',
                        emptyText : _('<Select one..>'),
                        enumId : CmFinoFIX.TagID.BulkUploadDeliveryStatus,
                        width: 145,
                        listeners : {
                            specialkey: this.bulkUploadEnterKeyHandler.createDelegate(this)
                        }
                    }
                    ]
                },
                {
                    columnWidth:0.45,
                    layout: 'form',
                    border:false,
                    items: [
                    {
                        xtype : "enumdropdown",
                        fieldLabel  : _('File Type'),
                        labelSeparator : '',
                        mode: 'local',
                        itemId:'bulktype',
                        triggerAction: 'all',
                        emptyText : _('<Select one..>'),
                        enumId : CmFinoFIX.TagID.BulkUploadFileType,
                        listeners : {
                            specialkey: this.bulkUploadEnterKeyHandler.createDelegate(this)
                        }
                    },
                    {
                        xtype: 'button',
                        text: _('Search'),
                        anchor:'30%',
                        handler:this.onBulkUploadSearch.createDelegate(this)
                    }
                    ]
                }
                ]
            }]
        });
        this.cashFlowPanel = new Ext.Panel({
            layout: "column",
            broder: false,
            items: [
            {
                columnWidth:1,
                items:[this.cashFlowTab]
            },
            {
                columnWidth:1,
                items:[this.cashFlowGrid]
            }
            ]
        });
        this.LOPPanel = new Ext.Panel({
            layout: "column",
            broder: false,
            items: [
            {
                columnWidth:1,
                items:[this.lopPanelTab]
            },
            {
                columnWidth:1,
                items:[this.LopGrid]
            }
            ]
        });
        this.bulkUploadPanel = new Ext.Panel({
            layout: "column",
            broder: false,
            items: [
            {
                columnWidth:1,
                items:[this.bulkUploadTab]
            },
            {
                columnWidth:1,
                items:[this.bulkUploadGrid]
            }
            ]
        });
        
        this.bulkUploadGrid.action.on({
            action:function(grid, record, action, row, col) {
                if(action === 'mfino-button-View'){
                    if(record.get(CmFinoFIX.message.JSBulkUpload.Entries.BulkUploadFileType._name) ===CmFinoFIX.BulkUploadFileType.BankAccountTransfer )
                    {
                        var msg = new CmFinoFIX.message.JSGetBulkUploadFileData();
                        msg.m_pBulkUploadID = record.get(CmFinoFIX.message.JSBulkUpload.Entries.ID._name);
                        var params = mFino.util.showResponse.getDisplayParam();
                        params.myForm = this;
                        mFino.util.fix.send(msg, params);
                        Ext.apply(params, {
                            success :  function(response){
                                Ext.Msg.show({
                                    title: _("Batch Details for Source Name ( " + record.get(CmFinoFIX.message.JSBulkUpload.Entries.UserName._name) + " , "+ record.get(CmFinoFIX.message.JSBulkUpload.Entries.MDN._name))+ " )",
                                    minProgressWidth:600,
                                    msg: response.m_pReportFileData,
                                    buttons: Ext.MessageBox.OK,
                                    multiline: false
                                });
                            }
                        });
                    }
                    else
                    {
                        var testviewform = new mFino.widget.BulkUploadViewGridWindow(Ext.apply({
                            title: _("Batch Details for Source Name ( " + record.get(CmFinoFIX.message.JSBulkUpload.Entries.UserName._name) + " , "+ record.get(CmFinoFIX.message.JSBulkUpload.Entries.MDN._name))+ " )",
                            grid : new mFino.widget.BulkUploadViewGrid(config),
                            height : 466,
                            width: 800
                        },config));
                        testviewform.grid.store.lastOptions = {
                            params : {
                                start : 0,
                                limit : CmFinoFIX.PageSize.Default
                            }
                        };
                        testviewform.grid.store.baseParams[CmFinoFIX.message.JSBulkUploadEntry.IDSearch._name] = record.get(CmFinoFIX.message.JSBulkUpload.Entries.ID._name);
                        testviewform.grid.store.load(testviewform.grid.store.lastOptions);
                        testviewform.setStore(testviewform.grid.store);
                        testviewform.show();
                    }
                }
            }
        });
        this.LopGrid.action.on({
            action:function(grid, record, action, row, col) {
                if(action === 'mfino-button-View'){
                    var lopDetailsWindow = new mFino.widget.FormWindow(Ext.apply({
                        form : new mFino.widget.LOPDetails(config),
                        height : 300,
                        width: 800,
                        mode : "close",
                        title : _("LOP Details")
                    },config));
                    lopDetailsWindow.show();
                    lopDetailsWindow.setRecord(record);
                    lopDetailsWindow.setStore(grid.store);
                }
            }
        });

        this.cashFlowGrid.on("downloadcashflow", function(isBoth) {
            var mdn=null;
            if(this.record){
                mdn = this.record.data[CmFinoFIX.message.JSMerchant.Entries.MDN._name];
            }
            var queryString;
            if(mdn){
                var transactionTypeSearch =  this.cashFlowTab.find('itemId','transactionType')[0].getValue();
                var buySellSearch=this.cashFlowTab.find('itemId','buySell')[0].getValue();
                var startDateSearch = this.cashFlowTab.find('itemId','dateselected')[0].startDateField.getValue();
                var endDateSearch = this.cashFlowTab.find('itemId','dateselected')[0].endDateField.getValue();
                var transactionsTransferStatus = this.cashFlowTab.find('itemId','statusfilter')[0].getValue();
                var subtotalby = this.cashFlowTab.find('itemId','subtotalby')[0].getValue();
//                var SourceApplicationSearch= 0;
//                if(this.cashFlowTab.form.items.get('utk').checked){
//                    SourceApplicationSearch |=CmFinoFIX.SourceApplicationSearch.Phone;
//                }
//                if(this.cashFlowTab.form.items.get('sms').checked){
//                    SourceApplicationSearch |=CmFinoFIX.SourceApplicationSearch.SMS;
//                }
//                if(this.cashFlowTab.form.items.get('web').checked){
//                    SourceApplicationSearch |= CmFinoFIX.SourceApplicationSearch.Web;
//                }
//                if(this.cashFlowTab.form.items.get('agentui').checked){
//                    SourceApplicationSearch |=  CmFinoFIX.SourceApplicationSearch.WebService;
//                }
            
                queryString = "dType=cashflow";
                queryString += "&"+CmFinoFIX.message.JSCashFlow.SourceDestnMDN._name+"="+mdn;
//                queryString += "&"+ CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name+"="+SourceApplicationSearch;
                if(startDateSearch){
                    queryString += "&"+CmFinoFIX.message.JSCashFlow.StartTime._name+"="+getUTCdate(startDateSearch);
                }
                if(endDateSearch){
                    queryString += "&"+CmFinoFIX.message.JSCashFlow.EndTime._name+"="+getUTCdate(endDateSearch);
                }
                if(transactionsTransferStatus !== null && !(transactionsTransferStatus === "undefined")){
                    queryString += "&"+CmFinoFIX.message.JSCashFlow.TransactionsTransferStatus._name+"="+transactionsTransferStatus;
                }
                if(subtotalby!== null && !(subtotalby === "undefined")){
                    queryString += "&"+CmFinoFIX.message.JSCashFlow.SubtotalBy._name+"="+subtotalby;
                }
                if(transactionTypeSearch!== null && !(transactionTypeSearch === "undefined")){
                    queryString += "&"+CmFinoFIX.message.JSCashFlow.TransactionUICategory._name+"="+transactionTypeSearch;
                }
                if(buySellSearch!== null && !(buySellSearch === "undefined")){
                    queryString += "&"+CmFinoFIX.message.JSCashFlow.TransactionType._name+"="+buySellSearch;
                }
                var transferState = this.cashFlowTab.find('itemId','cashflowtransferstate')[0].getValue();
                if(transferState!== null && !(transferState === "undefined")){
                    queryString += "&"+CmFinoFIX.message.JSCashFlow.TransferState._name+"="+transferState;
                }
                var referenceId = this.cashFlowTab.find('itemId','transferid')[0].getValue();
                if(referenceId){
                    queryString += "&"+CmFinoFIX.message.JSCashFlow.IDSearch._name+"="+referenceId;
                }
                queryString+="&isBoth="+isBoth;

                var URL = "download.htm?" + queryString;
                window.open(URL,'mywindow','width=400,height=200');
            }else{
                Ext.ux.Toast.msg(_('Error'), _("No Merchant is Selected"));
            }
        }, this);

        this.LopGrid.on("downloadlop", function() {
            var merid=null;
            if(this.record){
                merid = this.record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name];
            }
            if(merid){
                var idSearch = this.lopPanelTab.find('itemId','lopid')[0].getValue();
                var lopStatusSearch =  this.lopPanelTab.find('itemId','lopstatus')[0].getValue();
                var startDateSearch = this.lopPanelTab.find('itemId','lopdateselected')[0].startDateField.getValue();
                var endDateSearch = this.lopPanelTab.find('itemId','lopdateselected')[0].endDateField.getValue();
                var queryString = "dType=lop";
                if(startDateSearch){
                    var newstartdate = getUTCdate(startDateSearch);
                    var newenddate =  getUTCdate(endDateSearch);
                }

                queryString += "&"+CmFinoFIX.message.JSLOP.MerchantIDSearch._name+"="+merid;

                if(idSearch){
                    queryString += "&"+CmFinoFIX.message.JSLOP.IDSearch._name+"="+idSearch;
                }
                if(lopStatusSearch!== null && !(lopStatusSearch === "undefined")){
                    queryString += "&"+CmFinoFIX.message.JSLOP.LOPStatusSearch._name+"="+lopStatusSearch;
                }
                if(startDateSearch){
                    queryString += "&"+CmFinoFIX.message.JSLOP.StartDateSearch._name+"="+newstartdate;
                }
                if(endDateSearch){
                    queryString += "&"+CmFinoFIX.message.JSLOP.EndDateSearch._name+"="+newenddate;
                }

                var URL = "download.htm?" + queryString;
                window.open(URL,'mywindow','width=400,height=200');
            }else{
                Ext.ux.Toast.msg(_('Error'), _("No Merchant is Selected"));
            }
        }, this);
        this.activeTab = 0;
        this.items = [
        {
            title: _('Cash Flow'),
            layout : "fit",
            itemId: "mer.cashflow.tab",
            items:  this.cashFlowPanel
        },
        {
            title: _('LOP Report'),
            layout : "fit",
            itemId: "mer.lop.tab",
            height:502,
            items:  this.LOPPanel
        },
        {
            title: _('Bulk Upload Report'),
            layout : "fit",
            itemId: "mer.bulkupload.tab",
            items:  this.bulkUploadPanel
        }
        ];
        mFino.widget.MerchantTab.superclass.initComponent.call(this);
    },
    cashFlowEnterKeyHandler : function (f, e) {    	  	
    	if (e.getKey() === e.ENTER) {
            this.onCashFlowSearch();
        }
    },
    lopEnterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.onLOPSearch();
        }
    },
    bulkUploadEnterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.onBulkUploadSearch();
        }
    },
    stateChange : function(value){
    	var compEl = this.find('id','cashflowdate')[0];
//        var compEl = this.cashFlowTab.find('itemId','dateselected')[0];
        if(value==CmFinoFIX.TransferState.Pending){
            this.find('itemId','statusfilter')[0].disable();
            this.find('itemId','statusfilter')[0].setValue("");
            if(compEl) {
               compEl.triggerField.setValue(compEl.triggerField.emptyText) ;
                compEl.startDateField.setValue(null);
                compEl.endDateField.setValue(null);
            }
        } else if(value==CmFinoFIX.TransferState.Complete){
            this.find('itemId','statusfilter')[0].enable();
            if(compEl) {
                var sdate = new Date().add(Date.DAY, -1);
                var edate = new Date(); 
                var trigger={start:sdate,end:edate};
                compEl.triggerField.setValue(trigger) ;
                compEl.startDateField.setValue(sdate);
                compEl.endDateField.setValue(edate);
            }
        }
    },
    onCashFlowReset : function(){
        this.cashFlowTab.getForm().reset();
        this.stateChange(CmFinoFIX.TransferState.Pending);
    },
    onLOPReset : function(){
        this.lopPanelTab.getForm().reset();
    },
    onBulkUploadReset : function(){
        this.bulkUploadTab.getForm().reset();
    },
    onLOPSearch:function(){
        if(this.lopPanelTab.getForm().isValid()){
            var merid=null;
            if(this.record){
                merid = this.record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name];
            }else {
                Ext.MessageBox.alert(_("Alert"), _("No Merchant is selected!"));
            }
            if(!merid){
                return;
            }
            if(this.lopPanelTab.find('itemId','lopstatus')[0].getValue() === "undefined" ){
                this.LopGrid.store.baseParams[CmFinoFIX.message.JSLOP.LOPStatusSearch._name] =null;
            }
            this.LopGrid.store.baseParams[CmFinoFIX.message.JSLOP.MerchantIDSearch._name] = merid;
            this.LopGrid.store.baseParams[CmFinoFIX.message.JSLOP.limit._name] = CmFinoFIX.PageSize.Default;
            this.LopGrid.store.baseParams[CmFinoFIX.message.JSLOP.StartDateSearch._name] = this.lopPanelTab.find('itemId','lopdateselected')[0].startDateField.getValue();
            this.LopGrid.store.baseParams[CmFinoFIX.message.JSLOP.EndDateSearch._name] = this.lopPanelTab.find('itemId','lopdateselected')[0].endDateField.getValue();
            this.LopGrid.store.baseParams[CmFinoFIX.message.JSLOP.LOPStatusSearch._name] = this.lopPanelTab.find('itemId','lopstatus')[0].getValue();
            this.LopGrid.store.baseParams[CmFinoFIX.message.JSLOP.IDSearch._name] = this.lopPanelTab.find('itemId','lopid')[0].getValue();

            this.LopGrid.store.load();
        }
        else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
 
    onBulkUploadSearch: function(){
        var merid=null;
        if(this.record){
            merid = this.record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name];
        }else{
            Ext.MessageBox.alert(_("Alert"), _("No Merchant is selected!"));
        }
        if(!merid){
            return;
        }
        this.bulkUploadGrid.store.baseParams[CmFinoFIX.message.JSBulkUpload.MerchantIDSearch._name] = merid;
        this.bulkUploadGrid.store.baseParams[CmFinoFIX.message.JSBulkUpload.limit._name] = CmFinoFIX.PageSize.Default;
        this.bulkUploadGrid.store.baseParams[CmFinoFIX.message.JSBulkUpload.StartDateSearch._name] = this.bulkUploadTab.find('itemId','bulkdateselected')[0].startDateField.getValue();
        this.bulkUploadGrid.store.baseParams[CmFinoFIX.message.JSBulkUpload.EndDateSearch._name] = this.bulkUploadTab.find('itemId','bulkdateselected')[0].endDateField.getValue();
        this.bulkUploadGrid.store.baseParams[CmFinoFIX.message.JSBulkUpload.FileStatusSearch._name] = this.bulkUploadTab.find('itemId','bulkstatus')[0].getValue();
        this.bulkUploadGrid.store.baseParams[CmFinoFIX.message.JSBulkUpload.FileTypeSearch._name] = this.bulkUploadTab.find('itemId','bulktype')[0].getValue();

        this.bulkUploadGrid.store.load();
    },
    onCashFlowSearch:function(){
        if(this.cashFlowTab.getForm().isValid()){
            if(!this.record){
                Ext.MessageBox.alert(_("Alert"), _("No Merchant is selected!"));
                return;
            }
            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceDestnMDN._name] = this.record.data[CmFinoFIX.message.JSMerchant.Entries.MDN._name];
            if(this.cashFlowTab.find('itemId','cashflowtransferstate')[0].getValue() === "undefined" ){
                this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.TransferState._name] =null;
            }
            if(this.cashFlowTab.find('itemId','transactionType')[0].getValue() === "undefined" ){
                this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.TransactionUICategory._name] =null;
            }
            if(this.cashFlowTab.find('itemId','buySell')[0].getValue() === "undefined" ){
                this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.TransactionType._name] =null;
            }
            if(this.cashFlowTab.find('itemId','subtotalby')[0].getValue() === "undefined" ){
                this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SubtotalBy._name] =null;
            }
            if(this.cashFlowTab.find('itemId','statusfilter')[0].getValue() === "undefined" ){
                this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.TransactionsTransferStatus._name] =null;
            }
            /*if(this.cashFlowTab.find('itemId','dateselected')[0].triggerField.getValue() === "<select dates...>" || this.cashFlowTab.find('itemId','dateselected')[0].triggerField.getValue() === ""){
                this.cashFlowTab.find('itemId','dateselected')[0].startDateField.setValue(null);
                this.cashFlowTab.find('itemId','dateselected')[0].endDateField.setValue(null);
            }*/
            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.TransferState._name] = this.cashFlowTab.find('itemId','cashflowtransferstate')[0].getValue();
            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.TransactionUICategory._name] = this.cashFlowTab.find('itemId','transactionType')[0].getValue();
            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.TransactionType._name] = this.cashFlowTab.find('itemId','buySell')[0].getValue();
            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.limit._name] = CmFinoFIX.PageSize.Default;
            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.EndTime._name] =  this.cashFlowTab.find('itemId','dateselected')[0].endDateField.getValue();
            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.StartTime._name] =  this.cashFlowTab.find('itemId','dateselected')[0].startDateField.getValue();
            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SubtotalBy._name] =  this.cashFlowTab.find('itemId','subtotalby')[0].getValue();
            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.TransactionsTransferStatus._name] =  this.cashFlowTab.find('itemId','statusfilter')[0].getValue();
            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.IDSearch._name] =  this.cashFlowTab.find('itemId','transferid')[0].getValue();
            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name]=null;
            //Create a new Message
            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.CommodityProcessorType._name]= CmFinoFIX.CommodityProcessorType.CashFlowProcessor;
//            if(this.cashFlowTab.form.items.get('utk').checked){
//                this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] = this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] | CmFinoFIX.SourceApplicationSearch.Phone;
//            } if(this.cashFlowTab.form.items.get('sms').checked){
//                this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] = this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] | CmFinoFIX.SourceApplicationSearch.SMS;
//            }
//            if(this.cashFlowTab.form.items.get('web').checked){
//                this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] = this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] | CmFinoFIX.SourceApplicationSearch.Web;
//            }
//            if(this.cashFlowTab.form.items.get('agentui').checked){
//                this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] = this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] | CmFinoFIX.SourceApplicationSearch.WebService;
//            }
            this.cashFlowGrid.store.load();
        }
        else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    setRecord : function(record){
        if(!record){
            delete this.record;
            if(this.cashFlowGrid.store){
                this.cashFlowGrid.store.removeAll();
                this.cashFlowGrid.store.removed = [];
                this.cashFlowGrid.getBottomToolbar().hide();
            }
            if(this.bulkUploadGrid.store){
                this.bulkUploadGrid.store.removeAll();
                this.bulkUploadGrid.store.removed = [];
                this.bulkUploadGrid.getBottomToolbar().hide();
            }
            if(this.LopGrid.store){
                this.LopGrid.store.removeAll();
                this.LopGrid.store.removed = [];
                this.LopGrid.getBottomToolbar().hide();
            }
            return;
        }

        this.record = record;

        this.cashFlowGrid.getBottomToolbar().show();
        this.LopGrid.getBottomToolbar().show();
        this.bulkUploadGrid.getBottomToolbar().show();

        if(this.cashFlowGrid.store){
            this.onCashFlowSearch();
//            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.TransferState._name] = this.cashFlowTab.find('itemId','cashflowtransferstate')[0].getValue();
//            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.TransactionUICategory._name] = this.cashFlowTab.find('itemId','transactionType')[0].getValue();
//            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.TransactionType._name] = this.cashFlowTab.find('itemId','buySell')[0].getValue();
//            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.limit._name] = CmFinoFIX.PageSize.Default;
//            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceDestnMDN._name] = record.data[CmFinoFIX.message.JSMerchant.Entries.MDN._name];
//            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.CommodityProcessorType._name]= CmFinoFIX.CommodityProcessorType.CashFlowProcessor;
//            if(this.cashFlowTab.form.items.get('sms').checked){
//                this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] = this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] | CmFinoFIX.SourceApplicationSearch.Phone;
//            }
//            if(this.cashFlowTab.form.items.get('web').checked){
//                this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] = this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] | CmFinoFIX.SourceApplicationSearch.Web;
//            }
//            if(this.cashFlowTab.form.items.get('agentui').checked){
//                this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] = this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.SourceApplicationSearch._name] | CmFinoFIX.SourceApplicationSearch.WebService;
//            }
//            this.cashFlowGrid.store.baseParams[CmFinoFIX.message.JSCashFlow.CommodityProcessorType._name]= CmFinoFIX.CommodityProcessorType.CashFlowProcessor;
//            this.cashFlowGrid.store.load();
        }
        
//        if(this.bulkUploadGrid.store){
//            this.bulkUploadGrid.store.baseParams[CmFinoFIX.message.JSBulkUpload.limit._name] = CmFinoFIX.PageSize.Default;
//            this.bulkUploadGrid.store.baseParams[CmFinoFIX.message.JSBulkUpload.MerchantIDSearch._name] = record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name];
//            this.bulkUploadGrid.store.load();
//        }
//
//        if(this.LopGrid.store){
//            this.LopGrid.store.baseParams[CmFinoFIX.message.JSLOP.limit._name] = CmFinoFIX.PageSize.Default;
//            this.LopGrid.store.baseParams[CmFinoFIX.message.JSLOP.MerchantIDSearch._name] = record.data[CmFinoFIX.message.JSMerchant.Entries.ID._name];
//            this.LopGrid.store.load();
//        }
    },
    checkEnabledItems : function(){
        var tabNames = [];
        for(var i = 0; i < this.items.length; i++){
            tabNames.push(this.items.get(i).itemId);
        }

        for(i = 0; i < tabNames.length; i++){
            var tabName = tabNames[i];
            if(!mFino.auth.isEnabledItem(tabName)){
                var tab = this.getComponent(tabName);
                this.remove(tab);
            }
        }
    },
	listeners : {
		tabchange : function(tabpanel,tab){
    		if(this.record) {
    			if(tab.itemId === 'mer.lop.tab' && !tabpanel.isLOPInitialised){
    				tabpanel.onLOPSearch();
    				tabpanel.isLOPInitialised=true;
    			}
    			if(tab.itemId === 'mer.bulkupload.tab' && !tabpanel.isBulkUploadInitialised){
    				tabpanel.onBulkUploadSearch();
    				tabpanel.isBulkUploadInitialised=true;
    			}
    		}
		}
	}
});
Ext.reg("merchanttab", mFino.widget.MerchantTab);