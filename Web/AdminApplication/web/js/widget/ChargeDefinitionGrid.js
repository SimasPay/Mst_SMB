/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.ChargeDefinitionGrid = function (config) {
    
    var localConfig = Ext.apply({}, config);

    var store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSChargeDefinition);

    if(mFino.auth.isEnabledItem('chargedefinition.grid.edit')){
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            {
                iconCls:'mfino-button-View',
                tooltip: _('View Charge Definition Details'),
                align:'right'
            },
            {
                iconCls:'mfino-button-edit',
                itemId:'chargetype.grid.edit',
                tooltip: _('Edit Charge Definition')
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
                tooltip: _('View Charge Definition Details'),
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
            header: _("Name"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSChargeDefinition.Entries.Name._name
        },
        {
            header: _("Description"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSChargeDefinition.Entries.Description._name
        },        
        {
            header: _("Charge Type"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSChargeDefinition.Entries.ChargeTypeName._name
        },
        {
            header: _("Is Charge From Customer"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSChargeDefinition.Entries.IsChargeFromCustomer._name,
            renderer: function(value) {
				if (value) {
					return "Yes";
				} else {
					return "No";
				}
			}
        },        
        {
            header: _("Dependant Charge Type"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSChargeDefinition.Entries.DependantChargeTypeName._name
        },
        
        {
            header: _("Is Charge Taxable"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSChargeDefinition.Entries.IsTaxable._name,
            renderer: function(value) {
				if (value) {
					return "Yes";
				} else {
					return "No";
				}
			}
        },        
        {
            header: _("Created By"),
            dataIndex: CmFinoFIX.message.JSChargeDefinition.Entries.CreatedBy._name
        },
        {
            header: _("Created On"),
            renderer : "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSChargeDefinition.Entries.CreateTime._name
        },
        {
            header: _("Modified By"),
            dataIndex: CmFinoFIX.message.JSChargeDefinition.Entries.UpdatedBy._name
        },
        {
            header: _("Modified On"),
            renderer : "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSChargeDefinition.Entries.LastUpdateTime._name
        }        
        ]
    });

    mFino.widget.ChargeDefinitionGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChargeDefinitionGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.ChargeDefinitionGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});





