/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.ChargeDistributionViewGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSTransactionAmountDistributionLog);
    }

    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
                iconCls:'mfino-button-View',
                tooltip: _('View BulkUpload Details')
            }
        ]
    });
    var sbun = new Ext.Toolbar.Button({
        //        pressed: true,
        enableToggle: false,
        iconCls: 'mfino-button-excel',
        tooltip : _('Export data to Excel Sheet'),
        handler : this.excelView.createDelegate(this)
    });
    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
        viewConfig: { emptyText: Config.grid_no_data },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        autoScroll : true,
        columns: [
            {
                header: _('Reference ID'),
                dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.ServiceChargeTransactionLogID._name
            },
            {
                header: _('PartnerID'),
                width :70,
                dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.PartnerID._name
            },
            {
                header: _('SubscriberID'),
                width :70,
                dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.SubscriberID._name
            },
            {
                header: _('PartnerTradeName'),
                dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.DestPartnerTradeName._name
            },
            {
                header: _('PocketID'),
                dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.PocketID._name
            },
            {
                header: _('Share Amount'),
                dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.ShareAmount._name
               
            },
            {
                header: _('Tax Amount'),
                dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.TaxAmount._name
               
            },            
            {
                header: _('Charge Type'),
                dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.ChargeTypeName._name
               
            },
            {
                header: _('Charge From Source'),
                dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.IsChargeFromCustomer._name,
				width : 40,
	            renderer: function(value) {
					if (value) {
						return "Yes";
					} else {
						return "No";
					}
				}                
            },            
            {
                header: _('Status'),
                dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.TransferStatusText._name
            },
            {
                header: _('Source MDN'),
                dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.SourceMDN._name
                
            },               
            {
                header: _('Failure Reason'),
                dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.TransferFailureReasonText._name
            },
               {
                    header: _('Processed Time'),
                    renderer:'date',
                    width:150,
                    dataIndex: CmFinoFIX.message.JSTransactionAmountDistributionLog.Entries.LastUpdateTime._name
                }
            ]
        });

        mFino.widget.ChargeDistributionViewGrid.superclass.constructor.call(this, localConfig);
        this.getBottomToolbar().add('->',sbun);
    };

    Ext.extend(mFino.widget.ChargeDistributionViewGrid, Ext.grid.GridPanel, {
        excelView: function(){
            this.fireEvent("chargedistribution");
        }
    });

    Ext.reg("chargedistributionviewgrid", mFino.widget.ChargeDistributionViewGrid);
