/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.util");

mFino.util.enumStoreFactory = function(){

    var storeCache = {};
    var emptyData = {};
    emptyData[CmFinoFIX.message.JSEnumTextSimple.Entries.DisplayText._name] = "";
    emptyData[CmFinoFIX.message.JSEnumTextSimple.Entries.EnumCode._name] = null ;
    var addEmptyOption = function(store){
        var r = new store.recordType(emptyData, -1); // create new record
        store.insert(0, r); // insert a new record into the store (also see add)
        store.commitChanges();
    };
    
    return {
        get : function(tagId, addEmpty){
            var cacheID = tagId + ":" + addEmpty;
            if(storeCache[cacheID]){
                return storeCache[cacheID];
            }

            var store = new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSEnumTextSimple);
            store.baseParams[CmFinoFIX.message.JSEnumTextSimple.TagIDSearch._name] = tagId;
            if(addEmpty === undefined){
                addEmpty = true;
            }
            if(addEmpty){
                store.on("load", addEmptyOption);
            }
            store.load();
            store.sort(CmFinoFIX.message.JSEnumTextSimple.Entries.DisplayText._name);
            storeCache[cacheID] = store;
            return store;
        }
    };
}();