/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.AdjustmentsSctlSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px',
        frame:true,
        border:true,
        items: [{
        	 layout:'column',
             border:false,
             labelWidth : 90,
             border:false,                   
                items: [{
                        columnWidth:0.4,
                        layout: 'form',
                        labelWidth : 90,
                        border:false,
                        items: [
                                {
					            	xtype : 'numberfield',
					            	allowDecimals:false,
					            	minValue:0,
						            fieldLabel: _('Sctl ID'),
						            labelSeparator : '',
						            anchor: '80%',
						            itemId: 'adjustments.sctlsearch.sctlid',
						            name: CmFinoFIX.message.JSAdjustments.SctlId._name,
						            listeners   : {
						                specialkey: this.enterKeyHandler.createDelegate(this)
						            }
					            }
                             ]
        			},
        			{
        	            columnWidth:0.4,
        	            layout:'form',
        	            items:[
        	            {
        	                xtype:'button',
        	                text:'Search',
        	                anchor:'30%',
        	                handler : this.searchHandler.createDelegate(this)
        	            }
        	            ]
        	        },
        	        {
        	            columnWidth:0.2,
        	            layout:'form',
        	            items:[
        	            {
        	                xtype:'displayfield',
        	                anchor:'90%'
        	            }
        	            ]
        	        }
            ]
        }]
    });

    mFino.widget.AdjustmentsSctlSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.AdjustmentsSctlSearchForm, Ext.FormPanel, {

    initComponent : function () {
        /*this.buttons = [
        {
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        },
        ' ',' ', ' '
        ];*/
        mFino.widget.AdjustmentsSctlSearchForm.superclass.initComponent.call(this);
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    searchHandler : function(){
        console.log("@kris: AdjustmentsSctlSearchForm.searchHandler");
        if(this.getForm().isValid()){
        	 var values = this.getForm().getValues();        	 
              this.fireEvent("search", values);
        }
        else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    },
    setEditable : function(isEditable){
        if(isEditable === undefined || isEditable){
            this.items.each(function(item) {
            	//enable the item
                item.enable();
            });
        }else{
            this.items.each(function(item) {
            	//Disable the item
                item.disable();
            });
        }
    }
});
