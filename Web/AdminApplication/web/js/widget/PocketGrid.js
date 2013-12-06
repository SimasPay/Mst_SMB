/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.PocketGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPocket);
    }
    this.parenttab = config.parenttab;
    var index = 1;
    var actionItems = [{
            iconCls:'mfino-button-View',
            tooltip: _('View Pocket Details')
        }];
	    if(mFino.auth.isEnabledItem('sub.pocket.edit')){
	    	actionItems[index++]={
	                iconCls:'mfino-button-edit',
	                itemId: 'sub.pocket.edit',
	                tooltip: _('Edit Pocket')
	            };
	    }
	    if(mFino.auth.isEnabledItem('sub.pocket.transactions')){
	    	actionItems[index++]={
	                iconCls:'mfino-button-history',
	                itemId: 'sub.pocket.transactions',
	                tooltip: _('View Pocket Transactions')
	            };
	    }
	    if(mFino.auth.isEnabledItem('sub.details.checkBalance')){
	    	actionItems[index++]={
	                iconCls:'mfino-button-currency',
	                tooltip : _('Check Balance'),
	                itemId: 'sub.details.checkBalance'
	            };
	    }
	   
	    this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:actionItems
        });
	    
    localConfig = Ext.applyIf(localConfig || {}, {
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
        columns: [

        this.action,
        {
            header: _("Pocket Template Description"),
            width : 180,
            dataIndex: CmFinoFIX.message.JSPocket.Entries.PocketTemplDescription._name
        },
        {
            header: _("Pocket Type"),
            dataIndex: CmFinoFIX.message.JSPocket.Entries.PocketTypeText._name
        },
        {
            header: _("Account Number"),
            width : 120,
            dataIndex: CmFinoFIX.message.JSPocket.Entries.CardPAN._name,
            renderer: function(value)
            {
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
            }
        },
        {
            header: _("Card Alias"),
            dataIndex: CmFinoFIX.message.JSPocket.Entries.CardAlias._name
        },
        {
            header: _("Pocket Restrictions"),
            dataIndex: CmFinoFIX.message.JSPocket.Entries.PocketRestrictionsText._name
        }, {
            header: _("Is Default"),
            dataIndex: CmFinoFIX.message.JSPocket.Entries.IsDefault._name,
            renderer: function(value){
                if(value){
                    return 'Y';
                }
                else{
                    return 'N';
                }
            }
        },{
            header: _("Pocket Status"),
            dataIndex: CmFinoFIX.message.JSPocket.Entries.PocketStatusText._name
        }
        ]
    });

    mFino.widget.PocketGrid.superclass.constructor.call(this, localConfig);
    
};

Ext.extend(mFino.widget.PocketGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        this.tbar =  ['->',{
            iconCls: 'mfino-button-add',
            itemId: 'sub.pocket.add',
            tooltip: _('Add Pocket'),
            handler: this.onAdd.createDelegate(this)
        }];
        mFino.widget.PocketGrid.superclass.initComponent.call(this);
        this.addEvents("addclick");
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
            if(!mFino.auth.isEnabledItem(this.parenttab+itemID)){
                var item = tb.getComponent(itemID);
                tb.remove(item);
            }
        }
    },   
    

    onAdd : function(){
        this.fireEvent("addclick");
    }
    
});

Ext.reg("pocketgrid", mFino.widget.PocketGrid);
