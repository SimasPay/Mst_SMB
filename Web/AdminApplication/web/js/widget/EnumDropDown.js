/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.EnumDropDown = function (config) {
    var localConfig = Ext.apply({}, config);

    mFino.widget.EnumDropDown.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.EnumDropDown, Ext.form.ComboBox, {
    initComponent : function () {
        var store = mFino.util.enumStoreFactory.get(this.initialConfig.enumId, this.initialConfig.addEmpty);

        // create config
        var config = {
            mode : "local",
            triggerAction:'all',
            // do not allow arbitrary values
            forceSelection:true,
            displayField:  CmFinoFIX.message.JSEnumTextSimple.Entries.DisplayText._name,
            valueField: CmFinoFIX.message.JSEnumTextSimple.Entries.EnumCode._name,
            store : store,
            typeAhead: true,
            editable : false,
            hiddenName : this.initialConfig.name //required for form submit
        }; // eo config object

        // apply config
        Ext.apply(this, Ext.applyIf(this.initialConfig, config));

        if(this.initialConfig.value){
        	store.on("load", function(){
        		try{
        			this.setValue(this.initialConfig.value);
        		}catch(err){
        			//it seems when the dropdown is not shown, the "this.store" reference is delete by someone
        			//the setValue will fail in that case. the real solution would be not creating those dropdowns
        			//in the first place, we need some delayed rendering 
        		}
        	}, this,
        	{
        		delay: 1,
        		single: true
        	});
        }
        
        mFino.widget.EnumDropDown.superclass.initComponent.call(this);
    }
});

Ext.reg("enumdropdown", mFino.widget.EnumDropDown);
