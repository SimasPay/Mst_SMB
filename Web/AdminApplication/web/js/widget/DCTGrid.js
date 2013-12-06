/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.DCTGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSDistributionChainTemplate);
    }

    if(mFino.auth.isEnabledItem('dct.grid.edit')){
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                iconCls:'mfino-button-edit',
                itemId:'dct.grid.edit',
                tooltip: _('Edit Distribution Chain Template')
            }
            ]
        });
    } else {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[]
        });
    }

    localConfig = Ext.apply(localConfig, {
        dataUrl             : "fix.htm",
        loadMask : true,
        viewConfig: { emptyText: Config.grid_no_data },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
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
            header: _("ID"),
            dataIndex: CmFinoFIX.message.JSDistributionChainTemplate.Entries.ID._name,
			width: 50
        },
        {
            header: _("Service"),
            dataIndex: CmFinoFIX.message.JSDistributionChainTemplate.Entries.ServiceName._name,
			width: 200
        },        
        {
            header: _("Distribution Chain Template"),
            width:200,
            dataIndex: CmFinoFIX.message.JSDistributionChainTemplate.Entries.DistributionChainName._name
        },
        {
            header: _("No of Levels"),            
            dataIndex: CmFinoFIX.message.JSDistributionChainTemplate.Entries.LevelNumber._name,
			width: 50
        },
        {
            header: _("Created Date"),
            dataIndex: CmFinoFIX.message.JSDistributionChainTemplate.Entries.CreateTime._name,
            renderer:"date",
            width : 150
        },
        {
            header: _("Created By"),
            dataIndex: CmFinoFIX.message.JSDistributionChainTemplate.Entries.CreatedBy._name
        },
        {
            header: _("Modified Date"),
            dataIndex: CmFinoFIX.message.JSDistributionChainTemplate.Entries.LastUpdateTime._name,
            renderer:"date",
            width : 150
        },
        {
            header: _("Modified By"),
            dataIndex: CmFinoFIX.message.JSDistributionChainTemplate.Entries.UpdatedBy._name
        }
        ]
    });

    mFino.widget.DCTGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.DCTGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            //this.store.setDefaultSort(CmFinoFIX.message.JSDistributionChainTemplate.Entries.LevelNumber._name, 'ASC');
        }
        
        mFino.widget.DCTGrid.superclass.initComponent.call(this);
     
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("dctGrid", mFino.widget.DCTGrid);

