/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.ServicePartnerListsp = function (config) {

    var localConfig = Ext.apply({}, config);
    var sbun = new Ext.Toolbar.Button({
//        pressed: true,
        enableToggle: false,
        iconCls: 'mfino-button-excel',
        tooltip : _('Export data to Excel Sheet'),
        handler : this.excelView.createDelegate(this)
    });
    if(mFino.auth.isEnabledItem('ServicePartnerListsp.download.excel')){
		sbun.show();
    }
	else{
		sbun.hide();
	}
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSAgent);
    }

    localConfig = Ext.applyIf(localConfig, {
        dataUrl : "fix.htm",
        frame: true,
        loadMask : true,
        height : 350,
        id:"ServicePartnerListsp",
       // title: _("Partner Search Results "),
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
        autoExpandColumn : "ID",
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
        {
            id : "ID", 
            resizable : false,
            menuDisabled : true,
            dataIndex: CmFinoFIX.message.JSAgent.Entries.ID._name,
            renderer : function (value,p,r){
                var m = CmFinoFIX.message.JSAgent.Entries;
                var image;
                if(r.data[m.UpgradeState._name]===CmFinoFIX.UpgradeState.Upgradable){
                    image = '<img src="resources/images/customer_white.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if(r.data[m.UpgradeState._name]===CmFinoFIX.UpgradeState.Rejected)
                {
                    image = '<img src="resources/images/customer_brown.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if(r.data[m.PartnerStatus._name]===CmFinoFIX.MDNStatus.Initialized)
                {
                    image = '<img src="resources/images/customer.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if( (r.data[m.PartnerStatus._name]===CmFinoFIX.MDNStatus.Active) && (r.data[m.Restrictions._name]>CmFinoFIX.SubscriberRestrictions.None))
                {
                    image = '<img src="resources/images/customer_orange.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if( (r.data[m.PartnerStatus._name]===CmFinoFIX.MDNStatus.Active))
                {
                    image = '<img src="resources/images/customer_green.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if( (r.data[m.PartnerStatus._name]===CmFinoFIX.MDNStatus.InActive))
                {
                    image = '<img src="resources/images/customer_yellow.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if( (r.data[m.PartnerStatus._name]===CmFinoFIX.MDNStatus.Suspend))
                {
                    image = '<img src="resources/images/customer_red.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                else if( (r.data[m.PartnerStatus._name]===CmFinoFIX.MDNStatus.Retired))
                {
                    image = '<img src="resources/images/customer_grey.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }                
                else if(r.data[m.PartnerStatus._name]>CmFinoFIX.MDNStatus.Active){
                    image = '<img src="resources/images/customer_black.png" width="16" height="19" style="padding:3px 6px 0 0;float:left"/>';
                }
                         
                return String.format("{0} <b>{1}</b> <br/>{2}", image ,r.data[m.TradeName._name], r.data[m.PartnerCode._name] || _("No Code"));
            }            
        }
        ]
    });

    mFino.widget.ServicePartnerListsp.superclass.constructor.call(this, localConfig);
    this.getBottomToolbar().add('->',sbun);
    };

Ext.extend(mFino.widget.ServicePartnerListsp, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
        	this.store.on("beforeload", this.clearSection.createDelegate(this));
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
            this.store.on("add", function(item, records, index){
                this.addIndex = index;
            }.createDelegate(this));
        }
        mFino.widget.ServicePartnerListsp.superclass.initComponent.call(this);
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
            /*else{
                this.getSelectionModel().selectFirstRow();
            }*/
        }
    },
    excelView: function(){
        this.fireEvent("download");
    }
});
