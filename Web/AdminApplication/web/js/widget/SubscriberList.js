/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.SubscriberList = function (config) {
    var localConfig = Ext.apply({}, config);
    var sbun = new Ext.Toolbar.Button({
//        pressed: true,
        enableToggle: false,
        iconCls: 'mfino-button-excel',
        tooltip : _('Export data to Excel Sheet'),
        handler : this.excelView.createDelegate(this)
    });
    if(mFino.auth.isEnabledItem('subscriberlist.download.excel')){
		sbun.show();
	}
	else{
		sbun.hide();
	}
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSSubscriberMDN);
        localConfig.store.baseParams[CmFinoFIX.message.JSSubscriberMDN.SubscriberSearch._name] = true;
    }

    localConfig = Ext.applyIf(localConfig, {
        dataUrl : "fix.htm",
        frame: true,
        loadMask : true,
        id:"subscriberlist",
        title: _(" Subscriber Search Results "),
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
            dataIndex: CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name,
            renderer : function (value,p,r){
                var m = CmFinoFIX.message.JSSubscriberMDN.Entries;
                var image;
                if((r.data[m.UpgradeState._name]===CmFinoFIX.UpgradeState.Upgradable) && 
    					(r.data[m.Status._name] != CmFinoFIX.MDNStatus.Retired) && (r.data[m.Status._name] != CmFinoFIX.MDNStatus.PendingRetirement)){
                        image = '<img src="resources/images/customer_white.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if(r.data[m.UpgradeState._name]===CmFinoFIX.UpgradeState.Rejected&&r.data[m.Status._name]<=CmFinoFIX.MDNStatus.Active)
                {
                    image = '<img src="resources/images/customer_brown.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if(r.data[m.Status._name]===CmFinoFIX.MDNStatus.Initialized)
                {
                    image = '<img src="resources/images/customer.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if( (r.data[m.Status._name]===CmFinoFIX.MDNStatus.Active) && (r.data[m.MDNRestrictions._name]===CmFinoFIX.SubscriberRestrictions.None))
                {
                    image = '<img src="resources/images/customer_green.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if( (r.data[m.Status._name]===CmFinoFIX.MDNStatus.Active) && (r.data[m.MDNRestrictions._name]>CmFinoFIX.SubscriberRestrictions.None))
                {
                    image = '<img src="resources/images/customer_orange.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if(r.data[m.Status._name]===CmFinoFIX.MDNStatus.InActive)
                {
                    image = '<img src="resources/images/customer_yellow.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if(r.data[m.Status._name]===CmFinoFIX.MDNStatus.Suspend)
                {
                    image = '<img src="resources/images/customer_red.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if(r.data[m.Status._name]===CmFinoFIX.MDNStatus.Retired)
                {
                    image = '<img src="resources/images/customer_grey.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if(r.data[m.Status._name]==CmFinoFIX.MDNStatus.NotRegistered)
                {
                	image = '<img src="resources/images/customer_purple.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if(r.data[m.Status._name]>CmFinoFIX.MDNStatus.Active)
                {
                    image = '<img src="resources/images/customer_black.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                
                return String.format("{0} <b>{1} {2} </b> <br/>{3}", image ,r.data[m.FirstName._name] || "", r.data[m.LastName._name] || "", value || _("No MDN"));
            }
        }
        ]
    });

    mFino.widget.SubscriberList.superclass.constructor.call(this, localConfig);
    this.getBottomToolbar().add('->',sbun);
    };

Ext.extend(mFino.widget.SubscriberList, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
        	this.store.on("beforeload", this.clearSection.createDelegate(this));
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
            this.store.on("add", function(item, records, index){
                this.addIndex = index;
            }.createDelegate(this));
        }
        mFino.widget.SubscriberList.superclass.initComponent.call(this);
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
           /* else{
                this.getSelectionModel().selectFirstRow();
            }*/
        }
    },
    excelView: function(){
        this.fireEvent("download");
    }
});
