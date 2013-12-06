/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("FIX");
FIX.FIXStore = function(Url,RPCObject) {
    var Config={};
    Config.proxy = new FIX.FIXProxy({
        prettyUrls: false,
        url: Url
    });
    Config.reader = new FIX.FIXReader({},    Ext.data.Record.create(RPCObject.Entries.JSONFields())  );
    Config.reader.store = this;
    Config.writer = new FIX.FIXWriter({
        writeAllFields: true
    });
    Config.RPCObject    =   RPCObject;
    Config.paramsAsHash= true;
    Config.autoSave= false;
    Config.autoLoad= false;
    
    FIX.FIXStore.superclass.constructor.call(this, Config);
};

Ext.extend(FIX.FIXStore, Ext.data.Store, {

    loadRecords : function(o, options, success){
        var mfinoaction;
        if(options.params !== undefined){
            mfinoaction =  options.params[CmFinoFIX.message.JSBase.mfinoaction._name];
        }
        
        if(mfinoaction === undefined){
            FIX.FIXStore.superclass.loadRecords.call(this, o, options, success);
        }else if(mfinoaction === CmFinoFIX.JSmFinoAction.Update){
            Ext.each(o.records, function(item){
                var oldRecord = this.getById(item.get("ID"));
                if(oldRecord){
                    Ext.apply(oldRecord.data, item.data);
                    var index = this.indexOfId(item.get("ID"));
                    //TODO: this is to notify all the widget on the screen that record at index has changed, there maybe a better way to reload "add" event.
                    this.fireEvent("add", this, oldRecord, index); 
                }
            }, this);

            this.fireEvent('load', this, o.records, options);
            if(options.callback){
                options.callback.call(options.scope || this, o.records, options, true);
            }
        }
    }
});
