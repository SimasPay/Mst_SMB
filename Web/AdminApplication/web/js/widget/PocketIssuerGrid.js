/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.PocketIssuerGrid = function (config) {
    
    var localConfig = Ext.apply({}, config);

    var store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPocketTemplate);

    if(mFino.auth.isEnabledItem('pockettemplate.grid.edit')){
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            {
                iconCls:'mfino-button-View',
                tooltip: _('View Pocket Template Details'),
                align:'right'
            },
            {
                iconCls:'mfino-button-edit',
                itemId:'pockettemplate.grid.edit',
                tooltip: _('Edit Pocket Template')
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
                tooltip: _('View Pocket Template Details'),
                align:'right'
            }
            ]
        });
    }

    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl             : "fix.htm",
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
        {
            header: _("Pocket Template Description"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSPocketTemplate.Entries.Description._name
        },
        {
            header: _("Pocket Type"),
            dataIndex: CmFinoFIX.message.JSPocketTemplate.Entries.PocketTypeText._name
        },
        {
            header: _("Pocket Code"),
            dataIndex: CmFinoFIX.message.JSPocketTemplate.Entries.PocketCode._name
        },
        {
            header: _("Max Pocket Balance"),
            renderer : "money",
            dataIndex: CmFinoFIX.message.JSPocketTemplate.Entries.MaximumStoredValue._name
        },
        {
            header: _("Min Pocket Balance"),
            renderer : "money",
            dataIndex: CmFinoFIX.message.JSPocketTemplate.Entries.MinimumStoredValue._name
        },
        
        {
            header: _("Created By"),
            dataIndex: CmFinoFIX.message.JSPocketTemplate.Entries.CreatedBy._name
        },
        {
            header: _("Created On"),
            renderer : "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSPocketTemplate.Entries.CreateTime._name
        },
        {
            header: _("Modified By"),
            dataIndex: CmFinoFIX.message.JSPocketTemplate.Entries.UpdatedBy._name
        },
        {
            header: _("Modified On"),
            renderer : "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSPocketTemplate.Entries.LastUpdateTime._name
        }
        ]
    });

    mFino.widget.PocketIssuerGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketIssuerGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.PocketIssuerGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("pocketissuergrid", mFino.widget.PocketIssuerGrid);







