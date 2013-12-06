/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.CashFlowGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSCashFlow);
    }
    var sbun = new Ext.Toolbar.Button({
        //        pressed: true,
        enableToggle: false,
        iconCls: 'mfino-button-excel',
        text: _('Download Grid Data'),
        tooltip : _('Export data to Excel Sheet'),
        handler : this.excelViewCashFlow.createDelegate(this)
    });
    var sbun1 = new Ext.Toolbar.Button({
        //        pressed: true,
        enableToggle: false,
        text: _('Download All'),
        iconCls: 'mfino-button-excel',
        tooltip : _('Export Pending and Complete data to Excel Sheet'),
        handler : this.excelViewCashFlowBoth.createDelegate(this)
    });

    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl             : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
        viewConfig: { 
            emptyText: Config.grid_no_data
        },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default,
            items: [sbun, sbun1]
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        autoScroll : true,
        columns: [
        {
            header: _("Reference ID"),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.ID._name,
            renderer: function(value){
                if(value < 0){
                    return "--";
                }else{
                    return value;
                }
            }
        },
        {
            header: _('Date & Time'),
            renderer: "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.StartTime._name
        },
        {
            header: _('Transaction Type'),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.SourceDestnPocketID._name,
            renderer: function(value, a, b){
                if(b.data[CmFinoFIX.message.JSCashFlow.Entries.TransactionUICategoryText._name]!== null ){
                    return b.data[CmFinoFIX.message.JSCashFlow.Entries.TransactionUICategoryText._name];
                }else{
                    return "--";
                }
            }
        },
        {
            header: _('Buy / Sell'),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.SourceDestnPocketID._name,
            renderer: function(value, a, b){
                if(b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceDestnPocketID._name]===b.data[CmFinoFIX.message.JSCashFlow.Entries.SourcePocketID._name]){
                    return CmFinoFIX.TransactionType.Sell;
                }else if(b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceDestnPocketID._name]===b.data[CmFinoFIX.message.JSCashFlow.Entries.DestPocketID._name]){
                    return CmFinoFIX.TransactionType.Buy;
                }else{
                    return "--";
                }
            }
        },
        {
            header: _('Commodity Type'),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.CommodityText._name,
            renderer:function (value){
                if(value ===null){
                    return "--";
                }else{
                    return value;
                }
            }
        },
        {
            header: _('Value'),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.Amount._name,
            renderer:function (value){
                if(value ===null){
                    return "--";
                }else{
                    return Ext.util.Format.money(value);
                }
            }
        },
        {
            header: _("Paid"),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.PaidAmount._name,
            renderer:function (value){
                if(value ===null){
                    return "--";
                }else{
                    return Ext.util.Format.money(value);
                }
            }
        },
        {
            header: _("Margin"),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.Amount._name,
            renderer: function(value, a,b){
                if(b.data[CmFinoFIX.message.JSCashFlow.Entries.PaidAmount._name]!==null){
                    var v = b.data[CmFinoFIX.message.JSCashFlow.Entries.PaidAmount._name] - b.data[CmFinoFIX.message.JSCashFlow.Entries.Amount._name];
                    return Ext.util.Format.money(v);
                }else{
                    return "--";
                }
            }
        },
        {
            header: _("To/From"),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.SourceDestnPocketID._name,
            renderer: function(value, a, b){
                if(b.data[CmFinoFIX.message.JSCashFlow.Entries.TransactionUICategory._name]=== CmFinoFIX.TransactionUICategory.MA_Topup ||
                    b.data[CmFinoFIX.message.JSCashFlow.Entries.TransactionUICategory._name]=== CmFinoFIX.TransactionUICategory.BulkTopup ||
                    b.data[CmFinoFIX.message.JSCashFlow.Entries.TransactionUICategory._name]=== CmFinoFIX.TransactionUICategory.Dompet_Self_Topup ||
                    b.data[CmFinoFIX.message.JSCashFlow.Entries.TransactionUICategory._name]=== CmFinoFIX.TransactionUICategory.Dompet_Topup_another){
                    if(b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceDestnPocketID._name]===b.data[CmFinoFIX.message.JSCashFlow.Entries.SourcePocketID._name]){
                        if(b.data[CmFinoFIX.message.JSCashFlow.Entries.DestMDN._name] !== null) {
                            return b.data[CmFinoFIX.message.JSCashFlow.Entries.DestMDN._name];
                        } else {
                            return"--";
                        }
                    }else{
                        if(b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceMDN._name] !== null) {
                            return b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceMDN._name];
                        } else {
                            return"--";
                        }
                    }
                }else if(b.data[CmFinoFIX.message.JSCashFlow.Entries.TransactionUICategory._name]=== CmFinoFIX.TransactionUICategory.MA_Transfer ||
                    b.data[CmFinoFIX.message.JSCashFlow.Entries.TransactionUICategory._name]=== CmFinoFIX.TransactionUICategory.BulkTransfer){
                    //                            b.data[CmFinoFIX.message.JSCashFlow.Entries.TransactionUICategory._name]=== CmFinoFIX.TransactionUICategory.Dompet_Money_Transfer)
                    if(b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceDestnPocketID._name]===b.data[CmFinoFIX.message.JSCashFlow.Entries.SourcePocketID._name]){
                        if(b.data[CmFinoFIX.message.JSCashFlow.Entries.DestMDN._name] !== null) {
                            return b.data[CmFinoFIX.message.JSCashFlow.Entries.DestnUserName._name];
                        } else {
                            return"--";
                        }
                    }else{
                        if(b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceMDN._name] !== null) {
                            return b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceUserName._name];
                        } else {
                            return"--";
                        }
                    }
                }
                //                else if (b.data[CmFinoFIX.message.JSCashFlow.Entries.TransactionUICategory._name]=== CmFinoFIX.TransactionUICategory.BulkTransfer){
                //                    return b.data[CmFinoFIX.message.JSCashFlow.Entries.BulkUploadToFrom._name];
                //                }
                if( b.data[CmFinoFIX.message.JSCashFlow.Entries.DestSubscriberName._name]!==null || b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceSubscriberName._name]!==null ){
                    if(b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceDestnPocketID._name]===b.data[CmFinoFIX.message.JSCashFlow.Entries.SourcePocketID._name]){
                        if(b.data[CmFinoFIX.message.JSCashFlow.Entries.DestSubscriberName._name]!== null){
                            return b.data[CmFinoFIX.message.JSCashFlow.Entries.DestSubscriberName._name];
                        }else{
                            return "--";
                        }
                    }
                    else{
                        if(b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceSubscriberName._name]!== null){
                            return b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceSubscriberName._name];
                        }else{
                            return "--";
                        }
                    }
                } else{
                    return "--";
                }
            }
        },
        {
            header: _('State'),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.TransferStateText._name
        },
        {
            header: _('Status'),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.TransferStatusText._name,
            renderer:function (value){
                if(value ===null){
                    return "--";
                }else{
                    return value;
                }
            }
        },
        {
            header: _("Ending Balance"),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.DestPocketBalance._name,
            renderer: function(value, a, b){
                var v = 0;
                if(b.data[CmFinoFIX.message.JSCashFlow.Entries.DestPocketBalance._name]!==null || b.data[CmFinoFIX.message.JSCashFlow.Entries.SourcePocketBalance._name]!==null){
                    if(b.data[CmFinoFIX.message.JSCashFlow.Entries.SourceDestnPocketID._name]===b.data[CmFinoFIX.message.JSCashFlow.Entries.DestPocketID._name]){
                        if(b.data[CmFinoFIX.message.JSCashFlow.Entries.TransferStatus._name]=== CmFinoFIX.TransferStatus.Failed &&
                            b.data[CmFinoFIX.message.JSCashFlow.Entries.TransferStateText._name]===CmFinoFIX.TransferStateValue.Complete){
                            v = (b.data[CmFinoFIX.message.JSCashFlow.Entries.DestPocketBalance._name]);
                        }else{
                            v = (b.data[CmFinoFIX.message.JSCashFlow.Entries.DestPocketBalance._name] + b.data[CmFinoFIX.message.JSCashFlow.Entries.Amount._name]);
                        }
                    }
                    else{
                        if(b.data[CmFinoFIX.message.JSCashFlow.Entries.TransferStatus._name]=== CmFinoFIX.TransferStatus.Failed &&
                            b.data[CmFinoFIX.message.JSCashFlow.Entries.TransferStateText._name]===CmFinoFIX.TransferStateValue.Complete &&
                            b.data[CmFinoFIX.message.JSCashFlow.Entries.CSRAction._name] === null){
                            v = (b.data[CmFinoFIX.message.JSCashFlow.Entries.SourcePocketBalance._name]);
                        }else{
                            v = (b.data[CmFinoFIX.message.JSCashFlow.Entries.SourcePocketBalance._name] - b.data[CmFinoFIX.message.JSCashFlow.Entries.Amount._name]);
                        }
                    }
                    return Ext.util.Format.money(v);
                } else{
                    return "--";
                }
            }
        },
        {
            header: _("Channel Name"),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.AccessMethodText._name,
            renderer:function (value){
                if(value ===null){
                    return "--";
                }else{
                    return value;
                }
            }
        },
        {
            header: _('Source Terminal ID'),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.SourceTerminalID._name,
            renderer:function (value){
                if(value ===null){
                    return "--";
                }else{
                    return value;
                }
            }
        },
        {
            header: _('Merchant Ref No'),
            dataIndex: CmFinoFIX.message.JSCashFlow.Entries.SourceReferenceID._name,
            renderer:function (value){
                if(value ===null){
                    return "--";
                }else{
                    return value;
                }
            }
        }
        ]
    });

    mFino.widget.CashFlowGrid.superclass.constructor.call(this, localConfig);
//    this.getBottomToolbar().add('->',sbun);
//    this.getBottomToolbar().add('->',sbun1);
};

Ext.extend(mFino.widget.CashFlowGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        mFino.widget.CashFlowGrid.superclass.initComponent.call(this);
    },
    searchHandler : function(){
        var values = this.getForm().getValues();
        this.fireEvent("search", values);
    },
    excelViewCashFlow: function(){
        var isBoth=false;
        this.fireEvent("downloadcashflow",isBoth);
    },
    excelViewCashFlowBoth: function(){
        var isBoth=true;
        this.fireEvent("downloadcashflow",isBoth);
    }
});

Ext.reg("cashflowgrid", mFino.widget.CashFlowGrid);
