/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BrandDetailsGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl,CmFinoFIX.message.JSBrand);
    }

    var gridAddForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.BrandAddForm(config),
        height : 200,
        width : 350
    },config));

    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 80,
        height:518,
        frame:true,
        bodyStyle:'padding:5px 5px 0',
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        tbar:  [{
            iconCls: 'mfino-button-add',
            itemId : 'brand.grid.add',
            text:'New',
            tooltip: _('New MNO Parameters'),
            handler: function(){
                var record = new localConfig.store.recordType();
                gridAddForm.setTitle(_("Add New MNO Parameters"));
                gridAddForm.setMode("add");
                gridAddForm.show();
                gridAddForm.setRecord(record);
                gridAddForm.setStore(localConfig.store);
            }
        }],
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        {
            header:_("Company Name"),
            width:225,
            dataIndex: CmFinoFIX.message.JSBrand.Entries.CompanyName._name
        },
        {
            header: _("International Country Code"),
            width:225,
            dataIndex: CmFinoFIX.message.JSBrand.Entries.InternationalCountryCode._name
        },
        {
            header: _("Prefix Code" ),
            width:225,
            dataIndex: CmFinoFIX.message.JSBrand.Entries.PrefixCode._name
        },
        {
            header: _("Name" ),
            width:225,
            dataIndex: CmFinoFIX.message.JSBrand.Entries.BrandName._name
        }
        ]
    });

    mFino.widget.BrandDetailsGrid.superclass.constructor.call(this, localConfig);

};
Ext.extend(mFino.widget.BrandDetailsGrid, Ext.grid.GridPanel, {

    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.BrandDetailsGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});