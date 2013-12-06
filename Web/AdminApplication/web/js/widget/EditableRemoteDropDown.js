/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.EditableRemoteDropDown = function (config) {
    var localConfig = Ext.apply({}, config);
    mFino.widget.EditableRemoteDropDown.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.EditableRemoteDropDown, Ext.form.ComboBox, {
    initComponent : function () {
        if(!(this.initialConfig.store)){
            this.initialConfig.store = new FIX.FIXStore(mFino.DATA_URL, this.initialConfig.RPCObject);
        }
        Ext.apply(this.initialConfig.store.baseParams, this.initialConfig.params);

        // create config
        var config = {
            mode : "local",
            triggerAction:'all',
            forceSelection:false,
            editable : true,
            store : this.initialConfig.store
        }; // eo config object

        // apply config
        Ext.apply(this, Ext.apply(this.initialConfig, config));

        mFino.widget.EditableRemoteDropDown.superclass.initComponent.call(this);
    },

    reload : function(p){
        this.store.load({
            callback : function( records, options, success){
                if(success){
                    var emptyData = {};
                    emptyData[this.displayField] = "";
                    var r = new this.store.recordType(emptyData, -1); // create new record
                    this.store.insert(0, r); // insert a new record into the store (also see add)
                    this.store.commitChanges();

                    this.setValue(this.value);
                }
            },
            scope : this,
            params : p
        });
    }
});

Ext.reg("editableremotedropdown", mFino.widget.EditableRemoteDropDown);
