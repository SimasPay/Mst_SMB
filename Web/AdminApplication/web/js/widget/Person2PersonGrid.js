/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.Person2PersonGrid = function (config) {
    var localConfig = Ext.apply({}, config);

    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPerson2Person);
    }

    if(mFino.auth.isEnabledItem('sub.p2p.disable')){
        this.action = new Ext.ux.grid.RowActions(
        {
            header:'',
            keepSelection:true,
            actions:[{
                iconCls:'mfino-button-remove',
                tooltip: _('Delete Person'),
                itemId: 'sub.p2p.disable',
                align:'right'
            }]

        });
    } else {
        this.action = new Ext.ux.grid.RowActions(
        {
            header:'',
            keepSelection:true,
            actions:[]

        });
    }

    localConfig = Ext.applyIf(localConfig || {}, {
        layout : 'fit',
        height: 270,
        width: 500,
        loadMask : true,
        viewConfig: { 
            emptyText: Config.grid_no_data
        },
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
            dataIndex: CmFinoFIX.message.JSPerson2Person.Entries.PeerName._name
        },
        {
            header: _("MDN"),
            dataIndex: CmFinoFIX.message.JSPerson2Person.Entries.MDN._name
        }
        ]
    });

    mFino.widget.Person2PersonGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.Person2PersonGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        this.tbar =  [
         '<b class= x-form-tbar>' + _('Favorite')+'</b>', '',
        '',
        {
            xtype:"textfield",
            fieldLabel: _('Buddy'),
            id:"buddytext",
            vtype: 'name',
            emptyText: _('eg: David Andrews'),
            allowBlank:'false'
        },
        '','','',
        '<b class= x-form-tbar>' + _('MDN')+'</b>', '',
        {
            xtype:'numberfield',
            fieldLabel: _('Mobile Number'),
            id:"mobilenumbertext",
            vtype: 'smarttelcophoneAdd',
            maxLength : 16,
            emptyText: _('eg: 6811256874'),
            allowBlank:'false'
        },
        '->',
        {
            iconCls: 'mfino-button-add',
            tooltip: _('Add Person'),
            itemId: 'sub.p2p.add',
            align:'right',
            handler: this.onAdd.createDelegate(this)
        }];
        
        mFino.widget.Person2PersonGrid.superclass.initComponent.call(this);
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
            if(!mFino.auth.isEnabledItem(itemID)){
                var item = tb.getComponent(itemID);
                tb.remove(item);
            }
        }


    },
    onAdd : function(){
        // Ext.MessageBox.alert(Ext.getCmp("buddytext").getValue(), Ext.getCmp("mobilenumbertext").getValue());
        if(this.subID) {
            var proceed = true;
            var record = new this.store.recordType();
            var buddyName = Ext.getCmp("buddytext");
            var buddyNum = Ext.getCmp("mobilenumbertext");
            if(buddyName.getValue() && buddyNum.getValue()){
                if(buddyNum.isValid()){
                    record.data['PeerName'] = buddyName.getValue();
                    record.data['MDN'] = buddyNum.getValue();
                }else{
                    Ext.MessageBox.alert("Message", "Please enter Valid MDN starting with <B>'62'</B>");
                    return;
                }
            }else {
                Ext.MessageBox.alert("Message", "Please enter name and MDN");
                proceed = false;
                return;
            }

            if(proceed){
                record.data['SubscriberID'] = this.subID;
                this.store.add(record);
                this.store.save();
            }
            Ext.getCmp("buddytext").reset();
            Ext.getCmp("mobilenumbertext").reset();
        } else {
            Ext.MessageBox.alert("No Subscriber!", "Please select a subscriber first.");
        }
    
    //this.fireEvent("addclick");
    }
});

Ext.reg("person2persongrid", mFino.widget.Person2PersonGrid);
