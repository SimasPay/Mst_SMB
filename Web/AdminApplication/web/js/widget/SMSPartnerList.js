/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.SMSPartnerList = function (config) {
    var localConfig = Ext.apply({}, config);
//    var sbun = new Ext.Toolbar.Button({
////        pressed: true,
//        enableToggle: false,
//        iconCls: 'mfino-button-excel',
//        tooltip : _('Export data to Excel Sheet'),
//        handler : this.excelView.createDelegate(this)
//    });
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSSMSPartner);
    }

    localConfig = Ext.applyIf(localConfig, {
        dataUrl : "fix.htm",
        frame: true,
        loadMask : true,
        height : 350,
        title: _("SMS Partner Search Results "),
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
        autoExpandColumn : "PartnerName",
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        {
            id : "PartnerName",
            resizable : false,
            menuDisabled : true,
            dataIndex: CmFinoFIX.message.JSSMSPartner.Entries.PartnerName._name,
            renderer : function (value,p,r){
                var m = CmFinoFIX.message.JSSMSPartner.Entries;
                var image = '<img src="resources/images/customer_green.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
//                if(r.data[m.Status._name]===CmFinoFIX.MDNStatus.Initialized){
//                    image = '<img src="resources/images/customer.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
//                }
//                else if( (r.data[m.Status._name]===CmFinoFIX.MDNStatus.Active) && (r.data[m.MDNRestrictions._name]===CmFinoFIX.SubscriberRestrictions.None))
//                {
//                    image = '<img src="resources/images/customer_green.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
//                }
//                else if( (r.data[m.Status._name]===CmFinoFIX.MDNStatus.Active) && (r.data[m.MDNRestrictions._name]>CmFinoFIX.SubscriberRestrictions.None))
//                {
//                    image = '<img src="resources/images/customer_orange.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
//                }
//                else if(r.data[m.Status._name]>CmFinoFIX.MDNStatus.Active){
//                    image = '<img src="resources/images/customer_red.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
//                }

                return String.format("{0} <b>{1} {2} </b> <br/>{3}", image ,r.data[m.ContactName._name], r.data[m.Username._name], value || _("No Partner"));
            }
        }
        ]
    });

    mFino.widget.SMSPartnerList.superclass.constructor.call(this, localConfig);
//    this.getBottomToolbar().add('->',sbun);
    };

Ext.extend(mFino.widget.SMSPartnerList, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
            this.store.on("add", function(item, records, index){
                this.addIndex = index;
            }.createDelegate(this));
        }
        mFino.widget.SMSPartnerList.superclass.initComponent.call(this);
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
