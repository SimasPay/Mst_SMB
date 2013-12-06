/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.EnumSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        layout:'column',
        labelWidth : 80,
        frame:true,
        title: _('Enums'),
        bodyStyle:'padding:5px 5px 0',
        
        items: [
        {
            columnWidth:0.25,
            layout:'form',
            labelWidth:60,
            items:[
            {
                xtype : 'textfield',
                fieldLabel: _('Tag Name'),
                labelSeparator : '',
                maxLength:255,
                name: CmFinoFIX.message.JSEnumText.TagNameSearch._name,
                anchor : '95%',
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.25,
            layout:'form',
            labelWidth:60,
            items:[
            {
                xtype : 'textfield',
                fieldLabel: _('Field Name'),
                labelSeparator : '',
                maxLength:255,
                anchor : '95%',
                name: CmFinoFIX.message.JSEnumText.FieldNameSearch._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.25,
            layout:'form',
            labelWidth:60,
            items:[
            {
                xtype : "enumdropdown",
                fieldLabel: _('Language'),
                itemId: "language",
                labelSeparator : '',
                anchor : '95%',
                width : 130,
                enumId : CmFinoFIX.TagID.Language,
                name: CmFinoFIX.message.JSEnumText.Language._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.10,
            layout:'form',
            items:[
            {
                xtype:'displayfield',
                anchor:'90%'
            }
            ]
        },{
            columnWidth:0.15,
            layout:'form',
            items:[
            {
                xtype:'button',
                text: _('Search'),
                anchor:'60%',
                handler : this.searchHandler.createDelegate(this)
            }
            ]
        }
        ]
    });
    mFino.widget.EnumSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.EnumSearchForm, Ext.FormPanel, {

    initComponent : function () {        
        mFino.widget.EnumSearchForm.superclass.initComponent.call(this);
        this.addEvents("EnumSearchEvent");
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    
    searchHandler : function(){
        if(this.getForm().isValid()){
            var values = this.getForm().getValues();
            this.fireEvent("EnumSearchEvent", values);
        } else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    }
});
