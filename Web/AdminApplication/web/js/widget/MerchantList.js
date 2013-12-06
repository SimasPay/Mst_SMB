/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */

mFino.widget.MerchantList = function (config) {
    var localConfig = Ext.apply({}, config);

    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSMerchant);
    }
   
    localConfig = Ext.applyIf(localConfig, {
        dataUrl             : "fix.htm",
        frame: true,
        loadMask : true,
        height : 385,
        id:"merchantlist",
        title: _("Merchant Search Results"),
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
        autoExpandColumn : "MDN",
        //header : false,
        //hideHeaders : true,
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        {
            id : "MDN",
            resizable : false,
            menuDisabled : true,
            dataIndex: CmFinoFIX.message.JSMerchant.Entries.MDN._name,
            renderer : function (value,p,r){
                var m = CmFinoFIX.message.JSMerchant.Entries;
                var image;

                if(r.data[m.SubscriberStatus._name]===CmFinoFIX.MDNStatus.Initialized)
                {
                    image = '<img src="resources/images/customer.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if( (r.data[m.SubscriberStatus._name]===CmFinoFIX.MDNStatus.Active) && (r.data[m.SubscriberRestrictions._name]===CmFinoFIX.SubscriberRestrictions.None))
                {
                    image = '<img src="resources/images/customer_green.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if( (r.data[m.SubscriberStatus._name]===CmFinoFIX.MDNStatus.Active) && (r.data[m.SubscriberRestrictions._name]>CmFinoFIX.SubscriberRestrictions.None))
                {
                    image = '<img src="resources/images/customer_orange.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if(r.data[m.SubscriberStatus._name]>CmFinoFIX.MDNStatus.Active)
                {
                    image = '<img src="resources/images/customer_red.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }

                return String.format("{0} <b> {3} </b>  &nbsp; &nbsp; &nbsp; (<i>{1} {2}</i>) <br/>{4}", image ,r.data[m.FirstName._name], r.data[m.LastName._name], r.data[m.Username._name],value || _("No Merchant"));
            }
        }
        ]
    });

    mFino.widget.MerchantList.superclass.constructor.call(this, localConfig);

};

Ext.extend(mFino.widget.MerchantList, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
            this.store.on("add", function(item, records, index){
                this.addIndex = index;
            }.createDelegate(this));
        }
        mFino.widget.MerchantList.superclass.initComponent.call(this);
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
    },
    getCurrentlySelectedMerchantId: function() {
        if(!this.getSelectionModel() || !this.getSelectionModel().getSelected()) {
            return null;
        }
        return this.getSelectionModel().getSelected().get(CmFinoFIX.message.JSMerchant.Entries.ID._name);
    }
});
