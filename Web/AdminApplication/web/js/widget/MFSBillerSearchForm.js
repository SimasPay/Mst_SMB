/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MFSBillerSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Biller Search'),
        bodyStyle:'padding:5px 5px 0',
        items : [
              {
                  xtype : 'textfield',
                  fieldLabel: _('Biller Name'),
                  labelSeparator : '',
                  anchor:'98%',
                  maxLength:255,
                  name: CmFinoFIX.message.JSMFSBiller.BillerNameSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },
              {
                  xtype : 'textfield',
                  fieldLabel: _('Biller Code'),
                  labelSeparator : '',
                  anchor:'98%',
                  maxLength:25,
                  name: CmFinoFIX.message.JSMFSBiller.BillerCodeSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },
              {
                  xtype : 'textfield',
                  fieldLabel: _('Biller Type'),
                  labelSeparator : '',
                  anchor:'98%',
                  maxLength:25,
                  name: CmFinoFIX.message.JSMFSBiller.BillerTypeSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },              
              {
                  xtype : 'daterangefield',
                  fieldLabel: _('Date range'),
                  labelSeparator : '',
                  anchor :'98%',
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              }
              ]
        
    });

    mFino.widget.MFSBillerSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MFSBillerSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        
        mFino.widget.MFSBillerSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
        
        this.on("render", function(){
            this.reloadRemoteDropDown();
        });
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
        } else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    },
    
    reloadRemoteDropDown : function(){
    	this.getForm().items.each(function(item) {
            if(item.getXType() == 'remotedropdown') {
                item.reload();
            }
        });
    }
});

Ext.reg("mfsbillersearchform", mFino.widget.MFSBillerSearchForm);