/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.MFSBillerList = function (config) {
    var localConfig = Ext.apply({}, config);

    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSMFSBiller);
    }

    localConfig = Ext.applyIf(localConfig, {
        dataUrl : "fix.htm",
        frame: true,
        loadMask : true,
        height : 375,
        title: _("Biller Search Results "),
        emptyText : _("No Results"),
        deferEmptyText : false,
        viewConfig: { 
            emptyText: Config.grid_no_data
        },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: false,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        autoExpandColumn : CmFinoFIX.message.JSMFSBiller.Entries.MFSBillerName._name,
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        {
            id : CmFinoFIX.message.JSMFSBiller.Entries.MFSBillerName._name,
            resizable : false,
            menuDisabled : true,
            dataIndex: CmFinoFIX.message.JSMFSBiller.Entries.MFSBillerName._name,
            renderer : function (value,p,r){
                var m = CmFinoFIX.message.JSMFSBiller.Entries;
                var image = '<img src="resources/images/customer_green.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                return String.format("{0} <b>{1}</b> <br/>{2}", image ,r.data[m.MFSBillerCode._name], value || _("No Biller name"));
            }
        }
        ]
    });

    mFino.widget.MFSBillerList.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MFSBillerList, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
            this.store.on("add", function(item, records, index){
                this.addIndex = index;
            }.createDelegate(this));
        }
        mFino.widget.MFSBillerList.superclass.initComponent.call(this);
    },

    onStoreChange : function(){
        if(this.store.getAt(0) && (this.addIndex >= 0 || !(this.getSelectionModel().getSelected()))){
            if(this.addIndex >= 0){
                this.getSelectionModel().selectRow(this.addIndex);
                delete this.addIndex;
            }
            else{
                this.getSelectionModel().selectFirstRow();
            }
        }
    }
});
