/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.AdjustmentsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSAdjustments);
    }    
    
    var index = 0;
    var actionItems = [{}];
    actionItems[index++]={
    		iconCls:'mfino-button-history',
            tooltip: _('View Sctl, Ledger details')
    };
    if(mFino.auth.isEnabledItem('adjustments.approve')){
    	actionItems[index++]={
    			iconCls : "mfino-button-resolve",
                tooltip : _('Approve/Reject Adjustment'),
            	itemId : 'adjustments.approve'
            };
    }
    
    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:actionItems
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
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        autoScroll : true,
        plugins:[this.action],
        columns: [
        this.action,
        {
            header: _("Adjustment ID"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSAdjustments.Entries.ID._name
        },
        {
            header: _("Sctl ID"),
            width : 100,
            dataIndex: CmFinoFIX.message.JSAdjustments.Entries.SctlId._name
        },
        {
            header: _("Adjustment Status"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSAdjustments.Entries.AdjustmentStatusText._name
        },
        {
            header: _("Source Pocket"),
            width : 200,
            dataIndex: CmFinoFIX.message.JSAdjustments.Entries.SourcePocketTemplateDescription._name
        },
        {
        	header: _("Destination Pocket"),
        	width : 200,
        	dataIndex: CmFinoFIX.message.JSAdjustments.Entries.DestPocketTemplateDescription._name
		},        
        {
            header: _("Amount"),
            renderer : "money",
            dataIndex: CmFinoFIX.message.JSAdjustments.Entries.Amount._name
        },
        {
        	header: _("Type"),
        	width : 150,
        	dataIndex: CmFinoFIX.message.JSAdjustments.Entries.AdjustmentType._name
		},
		{
        	header: _("Description"),
        	width : 150,
        	dataIndex: CmFinoFIX.message.JSAdjustments.Entries.Description._name
		},
        {
        	header: _("Applied By"),
        	width : 150,
        	dataIndex: CmFinoFIX.message.JSAdjustments.Entries.AppliedBy._name
		},
		{
        	header: _("Applied Time"),
        	width : 150,
        	dataIndex: CmFinoFIX.message.JSAdjustments.Entries.AppliedTime._name
		},
		{
        	header: _("Approved/Rejected By"),
        	width : 150,
        	dataIndex: CmFinoFIX.message.JSAdjustments.Entries.ApprovedOrRejectedBy._name
		},
		{
        	header: _("Approved/Rejected Time"),
        	width : 150,
        	dataIndex: CmFinoFIX.message.JSAdjustments.Entries.ApproveOrRejectTime._name
		},
		{
        	header: _("Approved/Rejected Comment"),
        	width : 150,
        	dataIndex: CmFinoFIX.message.JSAdjustments.Entries.ApproveOrRejectComment._name
		}
        ]
    });

    mFino.widget.AdjustmentsGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.AdjustmentsGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.AdjustmentsGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("adjustmentsgrid", mFino.widget.AdjustmentsGrid);
