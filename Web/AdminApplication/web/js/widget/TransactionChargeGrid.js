/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.TransactionChargeGrid = function (config) {
    
    var localConfig = Ext.apply({}, config);
    
    var isActiveColumn = new Ext.ux.grid.CheckColumn(
			{
				header : _('Is Active'),
				dataIndex :CmFinoFIX.message.JSTransactionCharge.Entries.IsActive._name,
				width : 55
				});
     var store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSTransactionCharge);

    if(mFino.auth.isEnabledItem('transactioncharge.grid.edit')){
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            {
                iconCls:'mfino-button-View',
                tooltip: _('View Transaction Charge Details'),
                align:'right'
            },
            {
                iconCls:'mfino-button-edit',
                itemId:'chargetype.grid.edit',
                tooltip: _('Edit Transaction Charge')
            }
            ]
        });
    } else{
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            {
                iconCls:'mfino-button-View',
                tooltip: _('View Transaction Charge Details'),
                align:'right'
            }
            ]
        });
    }

    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl: "fix.htm",
        store: store,
        frame: true,
        width: 300,
        loadMask : true,
        viewConfig: { emptyText: Config.grid_no_data },
        bbar: new Ext.PagingToolbar({
            store: store,
            displayInfo: true,
            displayMsg: _('Displaying topics') + ('{0} - {1} of {2}'),
            emptyMsg: _("No Records to display"),
            pageSize: CmFinoFIX.PageSize.Default
            
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        plugins:[this.action],
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        this.action,
        isActiveColumn,
        {
            header: _("Transaction Rule"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSTransactionCharge.Entries.TransactionRuleName._name
        },
        {
            header: _("Charge Type"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSTransactionCharge.Entries.ChargeTypeName._name
        },
        {
            header: _("Charge Definition"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSTransactionCharge.Entries.ChargeDefinitionName._name
        },        
        {
            header: _("Created By"),
            dataIndex: CmFinoFIX.message.JSTransactionCharge.Entries.CreatedBy._name
        },
        {
            header: _("Created On"),
            renderer : "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSTransactionCharge.Entries.CreateTime._name
        },
        {
            header: _("Modified By"),
            dataIndex: CmFinoFIX.message.JSTransactionCharge.Entries.UpdatedBy._name
        },
        {
            header: _("Modified On"),
            renderer : "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSTransactionCharge.Entries.LastUpdateTime._name
        }        
        ]
    });

    mFino.widget.TransactionChargeGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.TransactionChargeGrid, Ext.grid.GridPanel, {
    initComponent : function () {
    	
    	this.tbar =  [{
            text: 'EnableAll',
            handler: this.onEnableAll,
            scope: this
    		}, '-', {
    		text: 'DisableAll',
    		handler: this.onDisableAll,
    		scope: this
    		}, '-'];
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.TransactionChargeGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    },
    
    onEnableAll : function(){
        for(var i=0;i<this.store.getCount();i++){
        	if(!this.store.getAt(i).data.IsActive){
        	this.store.getAt(i).beginEdit();
        	this.store.getAt(i).set(CmFinoFIX.message.JSTransactionCharge.Entries.IsActive._name,1);
        	this.store.getAt(i).endEdit();
        	}
        }
        this.store.save();
//        this.store.reload();
    },
    onDisableAll : function(){
        for(var i=0;i<this.store.getCount();i++){
        	if(this.store.getAt(i).data.IsActive){
        	this.store.getAt(i).beginEdit();
        	this.store.getAt(i).set(CmFinoFIX.message.JSTransactionCharge.Entries.IsActive._name,0);
        	this.store.getAt(i).endEdit();
        	}
        }
        this.store.save();
//        this.store.reload();
    }
});





