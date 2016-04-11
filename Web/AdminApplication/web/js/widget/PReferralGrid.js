/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.PReferralGrid = function (config) {
    
    var localConfig = Ext.apply({}, config);

    var store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSProductReferral);

    if(mFino.auth.isEnabledItem('ProductReferrals.view')){
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            {
                iconCls:'mfino-button-View',
                tooltip: _('View ProductReferral Details'),
                align:'right'
            }
			/*,
            {
                iconCls:'mfino-button-edit',
                itemId:'chargetype.grid.edit',
                tooltip: _('Edit Charge Type')
            }*/
            ]
        });
    } /*else{
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[
            {
                iconCls:'mfino-button-View',
                tooltip: _('View Charge Type Details'),
                align:'right'
            }
            ]
        });
    }*/

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
            header:  _("Agent MDN"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSProductReferral.Entries.AgentMDN._name
         },
		 {
           header:  _("Subscriber MDN"),
           width : 120,
           dataIndex: CmFinoFIX.message.JSProductReferral.Entries.SubscriberMDN._name
        },        
        {
            header:  _("Product Desired"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSProductReferral.Entries.ProductDesired._name
         },                
        
        {
           header:  _("FullName"),
		   width : 180,
           dataIndex: CmFinoFIX.message.JSProductReferral.Entries.FullName._name
        },
        {
            header:  _("Email"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSProductReferral.Entries.Email._name
         },
		 {
            header:  _("Others"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSProductReferral.Entries.Others._name	
         }
    
        ]
    });

    mFino.widget.PReferralGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PReferralGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.PReferralGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});





