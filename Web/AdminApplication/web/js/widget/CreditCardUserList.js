/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.CreditCardUserList = function (config) {
    var localConfig = Ext.apply({}, config);
    
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSUsers);
    }

    localConfig = Ext.applyIf(localConfig, {
        dataUrl : "fix.htm",
        layout : 'fit',
        frame:true,
        loadMask : true,
        height : 200,
//        id:"creditcarduserlist",
        title: _(" CreditCardUser Search Results "),
        emptyText : _("No Results"),
        deferEmptyText : false,
        viewConfig: { emptyText: Config.grid_no_data },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: false,
            pageSize: 10
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        Column : "UserName",
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        {
//            id : "UserName", 
            resizable : false,
            menuDisabled : true,
            width :235,
            dataIndex: CmFinoFIX.message.JSUsers.Entries.Username._name,
            renderer : function (value,p,r){
                var m = CmFinoFIX.message.JSUsers.Entries;
                var image;
                if(r.data[m.UserStatus._name]===CmFinoFIX.UserStatus.Registered){
                    image = '<img src="resources/images/customer.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if( (r.data[m.UserStatus._name]===CmFinoFIX.UserStatus.Active))
                {
                    image = '<img src="resources/images/customer_green.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if( (r.data[m.UserStatus._name]===CmFinoFIX.UserStatus.Expired))
                {
                    image = '<img src="resources/images/customer_orange.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if(r.data[m.UserStatus._name]>CmFinoFIX.UserStatus.Active){
                    image = '<img src="resources/images/customer_red.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                         
                return String.format("{0} <b>{1} </b><br/> {2} {3}", image ,value, r.data[m.FirstName._name], r.data[m.LastName._name] || _("No User"));
            }
        }
        ]
    });

    mFino.widget.CreditCardUserList.superclass.constructor.call(this, localConfig);
//    this.getBottomToolbar().add('->',sbu);
    };

Ext.extend(mFino.widget.CreditCardUserList, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
            this.store.on("add", function(item, records, index){
                this.addIndex = index;
            }.createDelegate(this));
        }
        mFino.widget.CreditCardUserList.superclass.initComponent.call(this);
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
    excelView: function(){
        this.fireEvent("download");
    }
});
