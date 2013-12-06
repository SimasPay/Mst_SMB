/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.DenominationGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSDenomination);
    }

    if(mFino.auth.isEnabledItem('denomination.edit') && mFino.auth.isEnabledItem('denomination.delete')){
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            //        {
            //            iconCls:'mfino-button-View',
            //            tooltip: _('View Denomination Details')
            //        },
            {
                iconCls:'mfino-button-edit',
                itemId: 'denomination.edit',
                tooltip: _('Edit Denomination')
            },
            {
                iconCls: 'mfino-button-remove',
                itemId : 'denomination.delete',
                tooltip: _('Delete Denomination')
            }
            ]
        });
    } else {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                iconCls:'mfino-button-View',
                tooltip: _('View SMSC Details')
            }]
        });
    }

    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl             : "fix.htm",
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
            header: _("Denomination Amount"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSDenomination.Entries.DenominationAmount._name
        },
        {
            header: _("Biller ID"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSDenomination.Entries.BillerID._name
        },
        {
            header: _("Create Time"),
            renderer : 'date',
            width : 180,
            dataIndex: CmFinoFIX.message.JSDenomination.Entries.CreateTime._name
        },
        {
            header: _("Created By"),
            dataIndex: CmFinoFIX.message.JSDenomination.Entries.CreatedBy._name
        }
        ]
    });

    mFino.widget.DenominationGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.DenominationGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        this.tbar =  ['->',{
            iconCls: 'mfino-button-add',
            itemId: 'denomination.add',
            tooltip: _('Add Denomination'),
            handler: this.onAdd.createDelegate(this)
        }];
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.DenominationGrid.superclass.initComponent.call(this);
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
        if(this.billerType === "topup_denomination") {
            this.fireEvent("addDenomination");
        } else {
            Ext.ux.Toast.msg(_("Message"), _("Denomination is available only for topup"));
        }
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
        var billerID = record.data[CmFinoFIX.message.JSBiller.Entries.ID._name];
        this.billerType = record.data[CmFinoFIX.message.JSBiller.Entries.BillerType._name];
        if(billerID && this.store) {
            this.store.baseParams[CmFinoFIX.message.JSDenomination.BillerIDSearch._name] = billerID;
            this.store.baseParams[CmFinoFIX.message.JSDenomination.limit._name]=CmFinoFIX.PageSize.Default;
            this.store.load();
        }
    }
});

Ext.reg("DenominationGrid", mFino.widget.DenominationGrid);
