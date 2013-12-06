/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.RegionDetailsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    var gridAddForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.RegionDetailsForm(config),
        height : 180,
        width : 300
    },config));
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSRegion);
    }
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 80,
        height:539,
        frame:true,
        clicksToEdit:1,
        loadMask : true,
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        tbar:  [{
            iconCls: 'mfino-button-add',
            itemId : 'region.grid.add',
            text:'New',
            tooltip: _('New Region'),
            handler: function(){            
                var record = new localConfig.store.recordType();
                gridAddForm.setTitle(_("Add New Region"));
                gridAddForm.setMode("add");
                gridAddForm.show();
                gridAddForm.setRecord(record);
                gridAddForm.setStore(localConfig.store);
            }
        }],
        columns: [
        {
            header: _("Company Name"),
            width:300,
            dataIndex: CmFinoFIX.message.JSRegion.Entries.CompanyName._name
        },
        {
            header: _("Region Code"),
            width:300,
            dataIndex: CmFinoFIX.message.JSRegion.Entries.RegionCode._name
        },
        {
            header: _("Region Name"),
            width:310,
            dataIndex: CmFinoFIX.message.JSRegion.Entries.RegionName._name
        }
        ]
    });

    mFino.widget.RegionDetailsGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.RegionDetailsGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.RegionDetailsGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});
