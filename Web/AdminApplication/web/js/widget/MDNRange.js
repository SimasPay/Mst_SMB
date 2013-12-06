/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */
mFino.widget.MDNRange = function (config) {
    this.LOPPermissionEnabled = false;
    var localConfig = Ext.apply({}, config);
    if(!localConfig.store){
        localConfig.store = new FIX.FIXStore("fix.htm", CmFinoFIX.message.JSMDNRange);
    }
 
    var editor = new Ext.ux.grid.RowEditor({
        saveText: 'Save'
    });
    editor.on('afteredit',this.saveRecord.createDelegate(this));
    editor.on('beforeedit',this.checkRole.createDelegate(this));
    editor.on('validateedit',this.validateRecord.createDelegate(this));
    this.action = new Ext.ux.grid.RowActions({
        header:'',
        keepSelection:true,
        actions:[
        {
            iconCls:'mfino-button-remove',
            hideIndex: CmFinoFIX.message.JSMDNRange.Entries.GridHideIndex._name,
            align:'right'
        }
        ],
        getEditor : function(){
            return null;
        }
    });
    
    localConfig = Ext.apply(localConfig, {
        dataUrl: "fix.htm",
        itemId : 'mer.form.mdnRange',
        loadMask : true,
        height : 250,
        frame: true,
        viewConfig: {
            emptyText: Config.grid_no_data
        },
        plugins: [editor, this.action],
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true
        }),
        bbar: new Ext.PagingToolbar({
            store: localConfig.store,
            displayInfo: true,
            pageSize: CmFinoFIX.PageSize.Default
        }),
        tbar: [{
            iconCls: 'mfino-button-add',
            text: _('Add new MDN range'),
            ref: '../addBtn',
            handler: function(){
                var record = new localConfig.store.recordType();
                editor.stopEditing();
                if((mFino.auth.isEnabledItem('mer.form.mdnRange') != "undefined" && mFino.auth.isEnabledItem('mer.form.mdnRange'))){
                    record.set(CmFinoFIX.message.JSMDNRange.Entries.GridHideIndex._name,'false');
                }
                else {
                    record.set(CmFinoFIX.message.JSMDNRange.Entries.GridHideIndex._name,'true');
                }
                localConfig.store.insert(0, record);
                editor.startEditing(0);
            }
        }
        ],

        columns: [
        this.action,
        {
            header: 'Prefix',
            dataIndex: CmFinoFIX.message.JSMDNRange.Entries.PrefixCode._name,
            width: 250,
            editor: {
                xtype: 'combo',
                itemId : "prefixCode",
                addEmpty : false,
                editable : false,
                anchor : '100%',
                triggerAction: "all",
                minChars : 2,
                forceSelection : true,
                pageSize : 20,
                store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSBrand),
                RPCObject : CmFinoFIX.message.JSBrand,
                displayField: CmFinoFIX.message.JSBrand.Entries.PrefixCode._name,
                valueField : CmFinoFIX.message.JSBrand.Entries.PrefixCode._name,
                name: CmFinoFIX.message.JSMDNRange.Entries.PrefixCode._name,
                listeners : {
                    select :  function(field,record){
                        var value = record.get(CmFinoFIX.message.JSBrand.Entries.ID._name);
                        this.findParentByType('mdnrange').getBrandId(value);
                    }
                }
            }
        },{
            header: 'From',
            dataIndex: CmFinoFIX.message.JSMDNRange.Entries.StartPrefix._name,
            width: 200,
            editor: {
                xtype: 'textfield',
                itemId : 'startPrefix',
                name: CmFinoFIX.message.JSMDNRange.Entries.StartPrefix._name
            }
        },{
            header: 'To',
            dataIndex: CmFinoFIX.message.JSMDNRange.Entries.EndPrefix._name,
            width: 150,
            editor: {
                xtype: 'textfield',
                itemId : 'endPrefix',
                name: CmFinoFIX.message.JSMDNRange.Entries.EndPrefix._name
            }
        }
        ]
    });
    mFino.widget.MDNRange.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MDNRange, Ext.grid.GridPanel, {
    initComponent : function () {
        mFino.widget.MDNRange.superclass.initComponent.call(this);
        this.store.proxy.on('write',function(proxy, type, action) {
        	Ext.ux.Toast.msg(_("Info"), 'Changes has been saved successfully.');
            this.store.reload();
            //isRangeCheckChanged field is used to update the MerchantProcessor column RANGECHECK
            this.findParentByType("merchantform").isRangeCheckChanged(true);
        },this);
        this.store.proxy.on('exception',function(proxy, type, action) {
            this.store.reload();
        },this);
    },
    setMdnId : function(mid){
        this.merchantId = mid;
    },
    getBrandId : function(value){
        this.brandId = value;
    },
    validateRecord : function(obj, changes, record, rowIndex){
        if(!(mFino.auth.isEnabledItem('mer.form.mdnRange') != "undefined" && mFino.auth.isEnabledItem('mer.form.mdnRange') && this.LOPPermissionEnabled)){
            Ext.ux.Toast.msg(_("Message"), _("Permission denied or merchant should have LOP"));
            return false;
        }

        var startPrefix=record.get(CmFinoFIX.message.JSMDNRange.Entries.StartPrefix._name);
        var endPrefix=record.get(CmFinoFIX.message.JSMDNRange.Entries.EndPrefix._name);
        
        if(typeof(changes['StartPrefix']) != "undefined")
        {
            startPrefix=changes['StartPrefix'];
        }
        if(typeof(changes['EndPrefix']) != "undefined")
        {
            endPrefix=changes['EndPrefix'];
        }
        if(record.phantom && (typeof(changes['PrefixCode']) == "undefined" || changes['PrefixCode'].length<1))
        {
                Ext.ux.Toast.msg(_("Message"), _("Invalid entry"));
                if(record.phantom){
                    this.getStore().remove(record);
                }
                return false;
        }
        if(isNaN(startPrefix) || startPrefix.length<3 ||startPrefix.length>10
            ||isNaN(endPrefix) || endPrefix.length<3 ||endPrefix.length>10
            || parseInt(startPrefix,10) > parseInt(endPrefix,10))
            {
                Ext.ux.Toast.msg(_("Message"), _("Invalid entry"));
                if(record.phantom){
                    this.getStore().remove(record);
                }
                return false;
            }
            
        return true;
    },
    saveRecord : function( obj, changes, record, rowIndex){
        record.beginEdit();
        record.set(CmFinoFIX.message.JSMDNRange.Entries.MerchantID._name,this.merchantId);
        record.set(CmFinoFIX.message.JSMDNRange.Entries.BrandID._name,this.brandId);
        record.endEdit();
        this.store.save();
   },
    checkRole : function(){
        if(!(mFino.auth.isEnabledItem('mer.form.mdnRange') != "undefined" && mFino.auth.isEnabledItem('mer.form.mdnRange'))){
            Ext.ux.Toast.msg(_("Message"), _("Permission denied"));
            return false;
        }
        return true;
    },
    setLOPPermission:function(checkPermission){        
        this.LOPPermissionEnabled = checkPermission;
    }
});

Ext.reg("mdnrange", mFino.widget.MDNRange);

