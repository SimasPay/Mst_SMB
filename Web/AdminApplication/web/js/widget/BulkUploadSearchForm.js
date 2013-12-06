/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BulkUploadSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        frame : true,
        title: _('Bulk Upload'),
        height:60,
        items:[
        {
            columnWidth:0.25,
            layout:'form',
            labelWidth:80,
            items:[
            {
                xtype:'enumdropdown',
                fieldLabel:'Customer Type',
                mode: 'local',
                itemId:'filetype',
                triggerAction: 'all',
                emptyText : _('<Select one..>'),
                enumId : CmFinoFIX.TagID.RecordType,
                name: CmFinoFIX.message.JSBulkUploadFile.FileTypeSearch._name,
                anchor:'90%',
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.25,
            layout:'form',
            labelWidth:90,
            items:[
            {
                xtype:'datefield',
                fieldLabel:'Created On',
                itemId: 'createdate',
                name: CmFinoFIX.message.JSBulkUploadFile.StartDateSearch._name,
                anchor:'90%',
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.25,
            layout:'form',
            labelWidth:90,
            items:[
            {
                xtype:'enumdropdown',
                fieldLabel:'Status',
                itemId: 'status',
                enumId:CmFinoFIX.TagID.UploadFileStatus,
                name: CmFinoFIX.message.JSBulkUploadFile.UploadFileStatusSearch._name,
                anchor:'90%',
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.20,
            layout:'form',
            items:[
            {
                xtype:'button',
                text:'Search',
                anchor:'60%',
                handler: this.searchHandler.createDelegate(this),
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        }
        ]
    });
    mFino.widget.BulkUploadSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkUploadSearchForm, Ext.FormPanel, {

    initComponent : function () {
        mFino.widget.BulkUploadSearchForm.superclass.initComponent.call(this);
        this.addEvents("bulkuploadSearch");
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    searchHandler : function(){
        var values = this.getForm().getValues();
        this.fireEvent("bulkuploadSearch", values);
    }
});