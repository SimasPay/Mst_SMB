/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.EnumGrid = function (config) {
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSEnumText);
    }

    if(mFino.auth.isEnabledItem('enum.grid.add') && mFino.auth.isEnabledItem('enum.grid.edit')) {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                    iconCls: 'mfino-button-add',
                    itemId: 'enum.grid.add',
                    tooltip: _('Add Enum Language'),
                    align:'right'
                },{
                    iconCls:'mfino-button-edit',
                    itemId: 'enum.grid.edit',
                    tooltip: _('Edit Enum Language')
                }
            ]
        });
    } else if(mFino.auth.isEnabledItem('enum.grid.add')) {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                    iconCls: 'mfino-button-add',
                    itemId: 'enum.grid.add',
                    tooltip: _('Add Enum Language'),
                    align:'right'
                }
            ]
        });
    } else if(mFino.auth.isEnabledItem('enum.grid.edit')) {
        this.action = new Ext.ux.grid.RowActions({
            header:'',
            keepSelection:true,
            actions:[{
                    iconCls:'mfino-button-edit',
                    itemId: 'enum.grid.edit',
                    tooltip: _('Edit Enum Language')
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

    localConfig = Ext.apply(localConfig, {
        dataUrl             : "fix.htm",
        loadMask : true,
        viewConfig: { emptyText: Config.grid_no_data },
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        plugins:[this.action],
  //      enableHdMenu : false,    // this property used for hiding menu in the grid
        listeners : {
            render : function(){
                this.fireEvent("defaultSearch");
            }
        },
        columns: [
            this.action,
            {
                header: _("ID"),
                dataIndex: CmFinoFIX.message.JSEnumText.Entries.ID._name
            },
            {
                header: _("TagName"),
                dataIndex: CmFinoFIX.message.JSEnumText.Entries.TagName._name
            },
            {
                header: _("Tag ID"),
                dataIndex: CmFinoFIX.message.JSEnumText.Entries.TagID._name
            },
            {
                header: _("Field Name"),
                width:250,
                dataIndex: CmFinoFIX.message.JSEnumText.Entries.EnumValue._name
            },
            {
                header: _("Language"),
                dataIndex: CmFinoFIX.message.JSEnumText.Entries.LanguageText._name
            },
            {
                header: _("DisplayText"),
                width:400,
                dataIndex: CmFinoFIX.message.JSEnumText.Entries.DisplayText._name
            }
        ]
    });

    mFino.widget.EnumGrid.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.EnumGrid, Ext.grid.GridPanel, {
    initComponent : function () {
        if(this.store){
            this.store.on("load", this.onStoreChange.createDelegate(this));
            this.store.on("update", this.onStoreChange.createDelegate(this));
        }
        
        mFino.widget.EnumGrid.superclass.initComponent.call(this);
     
    },
    onStoreChange : function(){
        if(this.store.getAt(0) && (!(this.getSelectionModel().getSelected()))){
            this.getSelectionModel().selectFirstRow();
        }
    }
});

Ext.reg("EnumGrid", mFino.widget.EnumGrid);

