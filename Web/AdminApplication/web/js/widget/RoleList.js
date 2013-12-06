/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.RoleList = function (config) {
    var localConfig = Ext.apply({}, config); 
    
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSRole);
    }

    localConfig = Ext.applyIf(localConfig, {
        dataUrl : "fix.htm",
        frame: true,
        loadMask : true,
        //id:"rolelist",
        title: _(" Role Search Results "),
        emptyText : _("No Results"),
        deferEmptyText : false,
        viewConfig: { emptyText: Config.grid_no_data },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: false,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        autoExpandColumn : "Role",
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        {
            id : "Role", 
            resizable : false,
            menuDisabled : true,
            dataIndex: CmFinoFIX.message.JSRole.Entries.DisplayText._name            
        }
        ]
    });

    mFino.widget.RoleList.superclass.constructor.call(this, localConfig);    
    };

Ext.extend(mFino.widget.RoleList, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
        	this.store.on("beforeload", this.clearSection.createDelegate(this));
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
            this.store.on("add", function(item, records, index){
                this.addIndex = index;
            }.createDelegate(this));
        }
        mFino.widget.RoleList.superclass.initComponent.call(this);
    },
    clearSection: function(){        
        this.getSelectionModel().clearSelections();
        this.fireEvent("clearSelected");
    },

    onStoreChange : function(){
        if(this.store.getAt(0) && (this.addIndex >= 0 || !(this.getSelectionModel().getSelected()))){
            if(this.addIndex >= 0){
                this.getSelectionModel().selectRow(this.addIndex);
                delete this.addIndex;
            }
        }
    }
});
