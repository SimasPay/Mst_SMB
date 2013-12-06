/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.SettlementTemplateGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSSettlementTemplate);
    }

    if(mFino.auth.isEnabledItem('partner.settlement.edit')){
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
            	iconCls:'mfino-button-edit',
                itemId: 'partner.settlement.edit',
                tooltip: _('Edit Settlement Template')
            }]
        });
    } else {
       this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
//                iconCls:'mfino-button-View',
//                tooltip: _('View Settlement Template Details')
            }]
        });
    }

    localConfig = Ext.applyIf(localConfig || {}, {
        dataUrl             : "fix.htm",
        loadMask : true,
        layout : 'fit',
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
        columns: [

        this.action,
        {
            header: _("Settlement Template Name"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSSettlementTemplate.Entries.SettlementName._name
        },
        {
            header: _("Settlement Pocket"),
            width : 200,
            dataIndex: CmFinoFIX.message.JSSettlementTemplate.Entries.PocketDispText._name
        },
        {
            header: _("Schedule Template"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSSettlementTemplate.Entries.SettlementTypeText._name
        },
        
        {
            header: _("Created By"),
            dataIndex: CmFinoFIX.message.JSSettlementTemplate.Entries.CreatedBy._name
        },
        {
            header: _("Created On"),
            renderer : "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSSettlementTemplate.Entries.CreateTime._name
        },
        {
            header: _("Modified By"),
            dataIndex: CmFinoFIX.message.JSSettlementTemplate.Entries.UpdatedBy._name
        },
        {
            header: _("Modified On"),
            renderer : "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSSettlementTemplate.Entries.LastUpdateTime._name
        }
        ]
    });

    mFino.widget.SettlementTemplateGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SettlementTemplateGrid, Ext.grid.GridPanel, {
    initComponent : function () {
    	if(mFino.auth.isEnabledItem('partner.settlement.add')){ 
    		this.tbar =  ['->',{
            iconCls: 'mfino-button-add',
            itemId: 'partner.settlement.add',
            tooltip: _('Add Settlement Template'),
            handler: this.onAdd.createDelegate(this)
        }];
    	}
        mFino.widget.SettlementTemplateGrid.superclass.initComponent.call(this);
        this.addEvents("addclick");
    },
    
    onAdd : function(){
        this.fireEvent("addclick");
    }
    
});

Ext.reg("settlementgrid", mFino.widget.SettlementTemplateGrid);
