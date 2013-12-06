/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MerchantCodesGrid = function (config) {
    var localConfig = Ext.apply({}, config);

    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore("fix.htm", CmFinoFIX.message.JSMerchantCode);
    }
    if(mFino.auth.isEnabledItem('merchantCodes.grid.edit') && mFino.auth.isEnabledItem('merchantCodes.grid.delete')) {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                iconCls:'mfino-button-edit',
                itemId : 'merchantCodes.grid.edit',
                tooltip: _('Edit Merchant Code')
            },
            {
                iconCls: 'mfino-button-remove',
                itemId : 'merchantCodes.grid.delete',
                tooltip: _('Delete Merchant Code')
            }
            ]
        });
    } else if(mFino.auth.isEnabledItem('merchantCodes.grid.edit')) {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                iconCls:'mfino-button-edit',
                itemId : 'merchantCodes.grid.edit',
                tooltip: _('Edit Merchant Code')
            }]
        });
    } else if(mFino.auth.isEnabledItem('merchantCodes.grid.delete')) {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                iconCls: 'mfino-button-remove',
                itemId : 'merchantCodes.grid.delete',
                tooltip: _('Delete Merchant Code')
            }]
        });
    } else {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[]
        });
    }
    var gridAddForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.MerchantCodeAddForm(config),
        height : 150,
        width : 350
    },config));
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 80,
        height: 505,
        frame:true,
        bodyStyle:'padding:5px 5px 0',
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        tbar:  [{
            iconCls: 'mfino-button-add',
            itemId : 'merchantCodes.grid.add',
            text:'New',
            tooltip: _('New Merchant Code'),
            handler: function(){
                var record = new localConfig.store.recordType();
                gridAddForm.setTitle(_("Add New Merchant Code"));
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
        plugins:[
        this.action],
        columns: [
        this.action,
        {
            header: _("Merchant Code"),
            width:215,
            dataIndex: CmFinoFIX.message.JSMerchantCode.Entries.MerchantCode._name
        },
        {
            header: _("MDN"),
            width:215,
            dataIndex: CmFinoFIX.message.JSMerchantCode.Entries.MDN._name
        },
        {
            header: _("Create Time"),
            width:215,
            renderer : 'date',
            dataIndex: CmFinoFIX.message.JSMerchantCode.Entries.CreateTime._name
        },
        {
            header: _("Created By"),
            width:215,
            dataIndex: CmFinoFIX.message.JSMerchantCode.Entries.CreatedBy._name
        }
        ]
    });

    mFino.widget.MerchantCodesGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantCodesGrid, Ext.grid.GridPanel, {

    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.MerchantCodesGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});
