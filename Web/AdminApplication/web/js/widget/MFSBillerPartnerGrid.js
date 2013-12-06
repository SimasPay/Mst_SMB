/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.MFSBillerPartnerGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSMFSBillerPartner);
    }
    var index = 0;
    var actionItems = [{ }];
    if(mFino.auth.isEnabledItem('mfsb.edit')){
    	actionItems[index++]={
    			iconCls:'mfino-button-edit',
                itemId: 'mfsb.edit',
                tooltip: _('Edit Partner')
            };
    }
    if(mFino.auth.isEnabledItem('mfsb.delete')){
    	actionItems[index++]={
    			 iconCls: 'mfino-button-remove',
                 itemId : 'mfsb.delete',
                 tooltip: _('Delete Partner')
            };
    }
   this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:actionItems
    });
    



    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl : "fix.htm",
        loadMask : true,
        viewConfig: {
            emptyText: Config.grid_no_data
        },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        plugins:[this.action],
        columns: [
        this.action,
        {
            header: _("Partner Name"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSMFSBillerPartner.Entries.PartnerName._name
        },
        {
            header: _("Partner Biller Code"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSMFSBillerPartner.Entries.PartnerBillerCode._name
        },
        {
            header: _("Create Time"),
            renderer : 'date',
            width : 180,
            dataIndex: CmFinoFIX.message.JSMFSBillerPartner.Entries.CreateTime._name
        },
        {
            header: _("Created By"),
            dataIndex: CmFinoFIX.message.JSMFSBillerPartner.Entries.CreatedBy._name
        },
        {
            header: _("Last Update Time"),
            renderer : 'date',
            width : 180,
            dataIndex: CmFinoFIX.message.JSMFSBillerPartner.Entries.LastUpdateTime._name
        },
        {
            header: _("Updated By"),
            dataIndex: CmFinoFIX.message.JSMFSBillerPartner.Entries.UpdatedBy._name
        }
        ]
    });

    mFino.widget.MFSBillerPartnerGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MFSBillerPartnerGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        this.tbar =  ['->',{
            iconCls: 'mfino-button-add',
            itemId: 'denomination.add',
            tooltip: _('Add Partner'),
            handler: this.onAdd.createDelegate(this)
        }];
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.MFSBillerPartnerGrid.superclass.initComponent.call(this);
        this.on('render', this.removeDisabled, this);
    },
    removeDisabled: function(){
        var tb = this.getTopToolbar();
        var itemIDs = [];
        for(var i = 0; i < tb.items.length; i++){
            itemIDs.push(tb.items.get(i).itemId);
        }
        for(i = 0; i < itemIDs.length; i++){
            var itemID = itemIDs[i];
            if(!mFino.auth.isEnabledItem(itemID)){
                var item = tb.getComponent(itemID);
                tb.remove(item);
            }
        }
    },
    onAdd : function(){
      this.fireEvent("addMFSBPartner");
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    },
    setBillerRecord : function(record) {
        if(!record){
            delete this.record;
            if(this.store){
                this.store.removeAll();
                this.store.removed = [];
                this.getBottomToolbar().hide();
            }
            return;
        }
        this.getBottomToolbar().show();
        var billerID = record.data[CmFinoFIX.message.JSMFSBiller.Entries.ID._name];
        if(billerID && this.store) {
            this.store.baseParams[CmFinoFIX.message.JSMFSBillerPartner.MFSBillerIdSearch._name] = billerID;
            this.store.baseParams[CmFinoFIX.message.JSMFSBillerPartner.limit._name]=CmFinoFIX.PageSize.Default;
            this.store.load();
        }
    }
});

Ext.reg("mfsbillerpartnergrid", mFino.widget.MFSBillerPartnerGrid);
