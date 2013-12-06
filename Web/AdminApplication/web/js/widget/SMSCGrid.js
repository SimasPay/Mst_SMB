/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.SMSCGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSSMSC);
    }

    if(mFino.auth.isEnabledItem('smsc.edit')){
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                iconCls:'mfino-button-View',
                tooltip: _('View SMSC Details')
            },{
                iconCls:'mfino-button-edit',
                itemId: 'smsc.edit',
                tooltip: _('Edit SMSC')
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
            header: _("Short Code"),
            dataIndex: CmFinoFIX.message.JSSMSC.Entries.ShortCode._name
        },
        {
            header: _("Long Number"),
            dataIndex: CmFinoFIX.message.JSSMSC.Entries.LongNumber._name
        },
        {
            header: _("Smartfren SMSCID"),
            dataIndex: CmFinoFIX.message.JSSMSC.Entries.SmartfrenSMSCID._name
        },
        {
            header: _("OtherLocalOperator SMSCID"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSSMSC.Entries.OtherLocalOperatorSMSCID._name
        },
        {
            header: _("Charging"),
            dataIndex: CmFinoFIX.message.JSSMSC.Entries.Charging._name
            
        },
        {
            header: _("Header"),
            dataIndex: CmFinoFIX.message.JSSMSC.Entries.Header._name
        },
        {
            header: _("Footer"),
            dataIndex: CmFinoFIX.message.JSSMSC.Entries.Footer._name
        }
        ]
    });

    mFino.widget.SMSCGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SMSCGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        this.tbar =  ['->',{
            iconCls: 'mfino-button-add',
            itemId: 'smsc.add',
            tooltip: _('Add SMSC'),
            handler: this.onAdd.createDelegate(this)
        }];
        //        if(this.store){
        //            this.store.on("load", this.onStoreChange.createDelegate(this));
        //            this.store.on("update", this.onStoreChange.createDelegate(this));
        //        }
        mFino.widget.SMSCodesGrid.superclass.initComponent.call(this);
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
        this.fireEvent("addsmsc");
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    },
    setPartnerRecord : function(record) {
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
        var partnerID = record.data[CmFinoFIX.message.JSSMSPartner.Entries.ID._name];
        if(partnerID && this.store) {
            this.store.baseParams[CmFinoFIX.message.JSSMSC.PartnerIDSearch._name] = partnerID;
            this.store.baseParams[CmFinoFIX.message.JSSMSC.limit._name]=CmFinoFIX.PageSize.Default;
            this.store.load();
        }
    }
});

Ext.reg("SMSCGrid", mFino.widget.SMSCGrid);
