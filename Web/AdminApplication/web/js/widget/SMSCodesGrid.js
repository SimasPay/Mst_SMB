/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SMSCodesGrid = function (config) {
    var localConfig = Ext.apply({}, config);

    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore("fix.htm", CmFinoFIX.message.JSSMSCode);
    }
    if(mFino.auth.isEnabledItem('smsCodes.grid.edit') && mFino.auth.isEnabledItem('smsCodes.grid.delete')) {
    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-edit',
            itemId : 'smsCodes.grid.edit',
            tooltip: _('Edit SMS Code')
        },
        {
            iconCls: 'mfino-button-remove',
            itemId : 'smsCodes.grid.delete',
            tooltip: _('Delete SMS Code')
        }
        ]
    });
    } else if(mFino.auth.isEnabledItem('smsCodes.grid.edit')) {
    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[{
            iconCls:'mfino-button-edit',
            itemId : 'smsCodes.grid.edit',
            tooltip: _('Edit SMS Code')
        }
        ]
    });
    } else if(mFino.auth.isEnabledItem('smsCodes.grid.delete')) {
    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[
        {
            iconCls: 'mfino-button-remove',
            itemId : 'smsCodes.grid.delete',
            tooltip: _('Delete SMS Code')
        }
        ]
    });
    } else {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[]
        });
    }
    var gridAddForm = new mFino.widget.FormWindow(Ext.apply({
        form : new mFino.widget.SMSCodeAddForm(config),
        height : 250,
        width : 350
    },config));
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 80,
        height:460,
        frame:true,
        bodyStyle:'padding:5px 5px 0',
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        tbar:  [{
            iconCls: 'mfino-button-add',
            itemId : 'smsCodes.grid.add',
            text:'New',
            tooltip: _('New SMS Code'),
            handler: function(){
                var record = new localConfig.store.recordType();
                gridAddForm.setTitle(_("Add New SMS Code"));
                gridAddForm.setMode("add");
                gridAddForm.show();
                gridAddForm.setRecord(record);
                gridAddForm.setStore(localConfig.store);
            }
        }],
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        plugins:[
        this.action],
        columns: [
        this.action,
        {
            header: _("SMS Code"),
            width:160,
            dataIndex: CmFinoFIX.message.JSSMSCode.Entries.SMSCodeText._name
        },
        {
            header: _("Brand"),
            width:160,
            dataIndex: CmFinoFIX.message.JSSMSCode.Entries.BrandName._name
        },
        {
            header: _("Service Name"),
            width:160,
            dataIndex: CmFinoFIX.message.JSSMSCode.Entries.ServiceName._name
        },
        {
            header: _("Description"),
            width:210,
            dataIndex: CmFinoFIX.message.JSSMSCode.Entries.Description._name
        },
        {
            header: _("Status"),
            width:160,
            dataIndex: CmFinoFIX.message.JSSMSCode.Entries.SMSCodeStatusText._name
        },
        {
            header: _("Short Codes"),
            width:160,
            dataIndex: CmFinoFIX.message.JSSMSCode.Entries.ShortCodes._name
        }
        ]
    });

    mFino.widget.SMSCodesGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SMSCodesGrid, Ext.grid.GridPanel, {

    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        mFino.widget.SMSCodesGrid.superclass.initComponent.call(this);
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});
