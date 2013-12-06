/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BulkTransferSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 5,
        labelWidth : 75,
        labelSeparator : '',
        frame:true,
        title: _('Bulk Transfer Search'),
        bodyStyle:'padding:5px 5px 0',
        items : [
              {
                  xtype : 'textfield',
                  fieldLabel: _('Reference Id'),
                  maxLength:255,
                  anchor:'98%',
                  itemId: 'bulktransfer.searchform.idsearch',
                  name: CmFinoFIX.message.JSBulkUpload.IDSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },
              {
                  xtype : 'textfield',
                  fieldLabel: _('File Name'),
                  maxLength:255,
                  anchor:'98%',
                  itemId: 'bulktransfer.searchform.filename',
                  name: CmFinoFIX.message.JSBulkUpload.NameSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },
              {
                  xtype:'datefield',
                  fieldLabel:_('Created On'),
                  itemId: 'bulktransfer.searchform.createdate',
                  anchor:'98%',
                  name: CmFinoFIX.message.JSBulkUpload.StartDateSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },
              {
                  xtype:'datefield',
                  fieldLabel:_('Payment Date'),
                  anchor:'98%',
                  itemId: 'bulktransfer.searchform.paymentdate',
                  name: CmFinoFIX.message.JSBulkUpload.PaymentDateSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },
              {
                  xtype:'enumdropdown',
                  fieldLabel:_('Status'),
                  anchor:'98%',
                  itemId: 'bulktransfer.searchform.status',
                  enumId:CmFinoFIX.TagID.BulkUploadDeliveryStatus,
                  name: CmFinoFIX.message.JSBulkUpload.FileStatusSearch._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              }
              ]
        
    });

    mFino.widget.BulkTransferSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkTransferSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        
        mFino.widget.BulkTransferSearchForm.superclass.initComponent.call(this);
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

Ext.reg("BulkTransferSearchForm", mFino.widget.BulkTransferSearchForm);