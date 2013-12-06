/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ChargeDefinitionSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Charge Definition Search'),
        bodyStyle:'padding:5px 5px 0',
        items : [
              {
                  xtype : 'textfield',
                  fieldLabel: _('Name'),
                  labelSeparator : '',
                  anchor:'98%',
                  maxLength:255,
                  name: CmFinoFIX.message.JSChargeDefinition.NameSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },
              {
                  xtype : "remotedropdown",
                  anchor : '98%',
                  fieldLabel :_("Charge Type"),
				  emptyText : _('<select one..>'),
                  labelSeparator : '',
                  itemId : 'chargedefinition.form.chargetypesearch',
                  RPCObject : CmFinoFIX.message.JSChargeType,
                  displayField: CmFinoFIX.message.JSChargeType.Entries.Name._name,
                  valueField : CmFinoFIX.message.JSChargeType.Entries.ID._name,
                  hiddenName : CmFinoFIX.message.JSChargeDefinition.ChargeTypeSearch._name,
                  name: CmFinoFIX.message.JSChargeDefinition.ChargeTypeSearch._name,
                  pageSize: 10 // added for #2301
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

    mFino.widget.ChargeDefinitionSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ChargeDefinitionSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        
        mFino.widget.ChargeDefinitionSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
        
        this.on("render", function(){
            this.reloadRemoteDropDown();
        });        
    },
    
    reloadRemoteDropDown : function(){
    	this.getForm().items.each(function(item) {
            if(item.getXType() == 'remotedropdown') {
                item.reload();
            }
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
    }
});

Ext.reg("chargedefinitionsearchform", mFino.widget.ChargeDefinitionSearchForm);