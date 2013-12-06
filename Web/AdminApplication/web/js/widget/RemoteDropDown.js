/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.RemoteDropDown = function (config) {
    var localConfig = Ext.apply({}, config);
    mFino.widget.RemoteDropDown.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.RemoteDropDown, Ext.form.ComboBox, {
    initComponent : function () {
        if(!(this.initialConfig.store)){
            this.initialConfig.store = new FIX.FIXStore(mFino.DATA_URL, this.initialConfig.RPCObject);
        }
        Ext.apply(this.initialConfig.store.baseParams, this.initialConfig.params);
        /*
         * The empty item wont be added to combobox when the property addEmpty is set to false
         */
        if(this.initialConfig.addEmpty != false){
        	this.initialConfig.store.on('load',this.addEmptyValue,this);
        }
        if(this.initialConfig.addDefault == true){
        	this.initialConfig.store.on('load',this.addDefaultValue,this);
        }
        // create config
        var config = {
            mode : "local",
            triggerAction:'all',
            // do not allow arbitrary values
            forceSelection:true,
            typeAhead: true,
            editable : false,
            store : this.initialConfig.store
        }; // eo config object

        // apply config
        Ext.apply(this, Ext.applyIf(this.initialConfig, config));

        mFino.widget.RemoteDropDown.superclass.initComponent.call(this);
    },  
    
    
    /*
     * overriding 'collapseIf' method of Ext.form.ComboBox 
     * This is added as a fix(#2449) to avoid the combo-box list from collapsing when clicked on next page or last page button in pagination tool bar
     */
    collapseIf : function(e, element) {
		var clickElement = element.innerHTML;
		if ((clickElement == 'Next Page') || (clickElement == 'Last Page')) {//when clicked on next/last page buttons, return without collapsing the list
			return false;
		}
		mFino.widget.RemoteDropDown.superclass.collapseIf.call(this,e);
	},
    
    addEmptyValue: function(){
    	var emptyData = {};
    	emptyData[this.displayField] = "";
    	emptyData[this.valueField] = null ;
        var r = new this.store.recordType(emptyData, -1); 
        this.store.insert(0, r);
        this.store.commitChanges();
    },
    
    addDefaultValue: function(){
    	var emptyData = {};
    	emptyData[this.displayField] = "any";
    	emptyData[this.valueField] = "any" ;
        var r = new this.store.recordType(emptyData, -1); 
        this.store.insert(0, r);
        this.store.commitChanges();
    },

    reload : function(p){     	
        this.store.load({
            /*
             * Empty item is getting added in this way only on calling reload, which will not be done
             * in all cases like if we add pagination to combobox, when we move to other page the empty 
             * item wont be added to items in the new page. Instead of adding empty item in callback 
             * function, it is added in addEmptyValue method which will be invoked after every store load call 
             * 
             * callback : function( records, options, success){
                if(success){
                    var emptyData = {};
                    emptyData[this.displayField] = "";
                    var r = new this.store.recordType(emptyData, -1); // create new record
                    this.store.insert(0, r); // insert a new record into the store (also see add)
                    this.store.commitChanges();

                    this.setValue(this.value);
                }
            },*/
        	callback : function( records, options, success){ 
                if(success){ 
                	// this.value will be set only when it is not undefined and it exists in store data
                	//(findRecord checks if entry with given value exists in store data, 
                	//if exists it returns displayText else returns undefined value)
                	// This check is helpful to fix below issue:
                	// combobox which is dependent on other combo doesn't send any req params while reload resulting in 
                	// empty store data due to which numeric value is shown in combobox instead of displaytext
                	if(this.value && this.findRecord(this.valueField, this.value)){
                		this.setValue(this.value); 
                	}                    
                }
        	},
            scope : this,
            params : p
        });
    }
});

Ext.reg("remotedropdown", mFino.widget.RemoteDropDown);
