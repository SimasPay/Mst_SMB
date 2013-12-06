/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.PartnerServiceGrid = function (config) {
    renderCardPAN = function(value) {
	    if(value && value.length > 6)
	    {
	        var substring = value.substring(value.length-6,value.length);
	        var retval="",i;
	        for(i=0;i<(value.length-6);i++)
	        {
	            retval += 'X';
	        }
	        retval +=substring;
	        return retval;
	    }
	    else
	    {
	        return value;
	    }
	};

    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPartnerServices);
    }

    if(mFino.auth.isEnabledItem('sub.partnerservice.edit')){
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                iconCls:'mfino-button-View',
                tooltip: _('View Partner Service Details')
            },{
                iconCls:'mfino-button-edit',
                itemId: 'sub.partnerservice.edit',
                tooltip: _('Edit Partner Service')
            }
            ]
        });
    } else {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                iconCls:'mfino-button-View',
                tooltip: _('View Partner Service Details')
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
            header: _("Service"),
            width : 150,
            dataIndex: CmFinoFIX.message.JSPartnerServices.Entries.ServiceName._name
        },
        {
            header: _("Distribution Chain Template"),
            width : 150,            
            dataIndex: CmFinoFIX.message.JSPartnerServices.Entries.DistributionChainName._name
        },
        {
            header: _("Parent(Trade Name)"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSPartnerServices.Entries.TradeName._name
        },
        {
            header: _("Service Charge Sharing"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSPartnerServices.Entries.IsServiceChargeShareText._name
        },         
        {
            header: _("Collector Pocket"),
            width : 200,
            dataIndex: CmFinoFIX.message.JSPartnerServices.Entries.CollectorPocketDispText._name
        },
        {
            header: _("Source Pocket"),
            width : 200,
            dataIndex: CmFinoFIX.message.JSPartnerServices.Entries.SourcePocketDispText._name
        },
        {
            header: _("Status"),
            width : 70,
            dataIndex: CmFinoFIX.message.JSPartnerServices.Entries.PartnerServiceStatusText._name
        },
        {
            header: _("Created By"),
            dataIndex: CmFinoFIX.message.JSPartnerServices.Entries.CreatedBy._name
        },
        {
            header: _("Created On"),
            renderer : "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSPartnerServices.Entries.CreateTime._name
        },
        {
            header: _("Modified By"),
            dataIndex: CmFinoFIX.message.JSPartnerServices.Entries.UpdatedBy._name
        },
        {
            header: _("Modified On"),
            renderer : "date",
            width : 150,
            dataIndex: CmFinoFIX.message.JSPartnerServices.Entries.LastUpdateTime._name
        }        
        ]
    });

    mFino.widget.PartnerServiceGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PartnerServiceGrid, Ext.grid.GridPanel, {
    initComponent : function () {
	if(mFino.auth.isEnabledItem('ps.add')){
        this.tbar =  ['->',{
            iconCls: 'mfino-button-add',
            itemId: 'sub.pocket.add',
            tooltip: _('Add Partner Service'),
            handler: this.onAdd.createDelegate(this)
        }];
	}
        mFino.widget.PartnerServiceGrid.superclass.initComponent.call(this);
        this.addEvents("addclick");
    },

    onAdd : function(){
        this.fireEvent("addclick");
    }
    
});

Ext.reg("partnerservicegrid", mFino.widget.PartnerServiceGrid);
