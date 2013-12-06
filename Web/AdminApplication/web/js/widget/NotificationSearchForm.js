/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.NotificationSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
   
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        layout:'column',
        labelWidth : 70,
        frame:true,
        title: _('Notifications'),
        bodyStyle:'padding:5px 5px 0',

        items: [
//        {
//            columnWidth:0.20,
//            layout:'form',
//            labelWidth:30,
//            items:[
//            {
//                xtype : 'numberfield',
//                allowDecimals:false,
//                fieldLabel: _("ID"),
//                labelSeparator : '',
//                maxLength:16,
//                minValue:0,
//                name: CmFinoFIX.message.JSNotification.NotificationID._name,
//                anchor : '95%',
//                listeners   : {
//                    specialkey: this.enterKeyHandler.createDelegate(this)
//                }
//            }
//            ]
//        },
        {
            columnWidth:0.15,
            layout:'form',
            labelWidth:30,
            items:[
            {
                xtype : 'numberfield',
                allowDecimals:false,
                fieldLabel: _("Code"),
                labelSeparator : '',
                maxValue:2000000000,
                minValue:0,
                name: CmFinoFIX.message.JSNotification.NotificationCode._name,
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
              allowDecimals:false,
              fieldLabel: _("CodeName"),
              labelSeparator : '',
              maxLength:50000,
              minValue:0,
              name: CmFinoFIX.message.JSNotification.NotificationCodeName._name,
              anchor : '95%',
              listeners   : {
                  specialkey: this.enterKeyHandler.createDelegate(this)
              }
          }
          ]
      },
        {
            columnWidth:0.20,
            layout:'form',
            labelWidth:50,
            items:[
            {
                xtype : "enumdropdown",
                fieldLabel: _('Language'),
                itemId: "language",
                labelSeparator : '',
                anchor : '90%',
                enumId : CmFinoFIX.TagID.Language,
                name: CmFinoFIX.message.JSNotification.Language._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.20,
            layout:'form',
            labelWidth:30,
            items:[
            {
                xtype : "enumdropdown",
                fieldLabel: _('Mode'),
                labelSeparator : '',
                itemId: "mode",
                anchor : '95%',
                enumId : CmFinoFIX.TagID.NotificationMethod,
                name: CmFinoFIX.message.JSNotification.NotificationMethod._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
            }
            ]
        },
        {
            columnWidth:0.05,
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
                text:'Search',
                anchor:'60%',
                handler : this.searchHandler.createDelegate(this)
            }
            ]
        }
        ]
    });

    mFino.widget.NotificationSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.NotificationSearchForm, Ext.FormPanel, {

    initComponent : function () {
        mFino.widget.NotificationSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },

    searchHandler : function(){
        if(this.getForm().isValid()){
            var values = this.getForm().getValues();
            this.fireEvent("search", values);
        }else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    }
});
