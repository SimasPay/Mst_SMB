/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.MerchantGrid = function (config) {
    var localConfig = Ext.apply({}, config);

    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSMerchant);
    }

    this.action = new Ext.ux.grid.RowActions(
    {
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-remove',
            tooltip: _('Delete Merchant'),
            align:'right'
        }]

    });

    localConfig = Ext.applyIf(localConfig || {}, {
        layout : 'fit',
        height: 270,
        width: 500,
        loadMask : true,
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
            header: _("Favorite"),
            dataIndex: CmFinoFIX.message.JSMerchant.Entries.FirstName._name
        },
        {
            header: _("MDN"),
            dataIndex: CmFinoFIX.message.JSMerchant.Entries.FirstName._name
        }
        ]
    });

    mFino.widget.MerchantGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantGrid, Ext.grid.GridPanel, {
    initComponent : function () {
//        this.tbar =  [
//        '<b>Favorite</b>', '',
//        {
//            xtype:"textfield",
//            fieldLabel:'Buddy',
//            id:"buddytext"
//        },
//        '','','',
//        '<b>Mobile Number</b>', '',
//        {
//            xtype:'numberfield',
//            fieldLabel:'Mobile Number',
//            id:"mobilenumbertext"
//        },
//        '->',
//        {
//            text: 'Add',
//            iconCls: 'mfino-button-add',
//            align:'right',
//            handler: this.onAdd.createDelegate(this)
//        }];

        mFino.widget.MerchantGrid.superclass.initComponent.call(this);
        this.addEvents("addclick");
    },

    onAdd : function(){
        // Ext.MessageBox.alert(Ext.getCmp("buddytext").getValue(), Ext.getCmp("mobilenumbertext").getValue());
        if(this.subID) {
            var proceed = true;
            var record = new this.store.recordType();
            var buddyName = Ext.getCmp("buddytext").getValue();
            var buddyNum = Ext.getCmp("mobilenumbertext").getValue();
            if(buddyName && buddyNum){
                record.data['PeerName'] = buddyName ;
                record.data['MDN'] = buddyNum;
            } else {
                Ext.MessageBox.alert("Message", "Please enter name and number");
                proceed = false;
            }

            if(proceed){
                record.data['SubscriberID'] = this.subID;
                this.store.add(record);
                this.store.save();
            }
            Ext.getCmp("buddytext").reset();
            Ext.getCmp("mobilenumbertext").reset();
        } else {
            Ext.MessageBox.alert(_("No Subscriber!"), _("Please select a subscriber first."));
        }

    //this.fireEvent("addclick");
    }
});

Ext.reg("merchantgrid", mFino.widget.MerchantGrid);
