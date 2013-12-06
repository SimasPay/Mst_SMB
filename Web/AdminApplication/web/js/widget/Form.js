/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.Form = function (config) {
    var localConfig = Ext.apply({
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    }, config);
    mFino.widget.Form.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.Form, Ext.FormPanel, {
    initComponent : function () {
        // create config
        var config = {
        }; // eo config object

        // apply config
        Ext.apply(this, Ext.applyIf(this.initialConfig, config));

        mFino.widget.Form.superclass.initComponent.call(this);
    },

    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            if(this.store){
                if(this.record.phantom){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
    },

    setStore : function(store){
        if(this.store){
            this.store.un("update", this.onStoreUpdate, this);
        }
        this.store = store;
        this.store.on("update", this.onStoreUpdate, this);
    },

    onStoreUpdate: function(){
        this.setRecord(this.record);
    }
});

Ext.reg("mfinoform", mFino.widget.Form);
