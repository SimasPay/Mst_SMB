/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.creditCardReviewer = function(config){

	var detailsForm = new mFino.widget.CCReviewerDetails(Ext.apply({
        height:276
    }, config));


    var searchBox = new mFino.widget.CCReviewerSearchForm(Ext.apply({
    }, config));

     
    var listBox = new mFino.widget.CreditCardUserList(Ext.apply({
    	 height : 375
    }, config));
   
    listBox.store.form = detailsForm.form;
    
    var tabPanel = new mFino.widget.CreditCardUserTab(config);
    tabPanel.checkEnabledItems();

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            height : 300,
            anchor : "100%",
            autoScroll : true,
            tbar : [
            '<b class= x-form-tbar>' + _('CreditCardUser Details') + '</b>',
            '->'
            ],
            items: [ detailsForm ]
        },
        {
            anchor : "100%, -200",
            layout: "fit",
            items: [ tabPanel ]
        }
        ]
    });

    var mainItem = panelCenter.items.get(0);
    mainItem.on('render', function(){
        var tb = mainItem.getTopToolbar();
        var itemIDs = [];
        for(var i = 0; i < tb.items.length; i++){
            itemIDs.push(tb.items.get(i).itemId);
        }
//        for(i = 0; i < itemIDs.length; i++){
//            var itemID = itemIDs[i];
//            if(!mFino.auth.isEnabledItem(itemID)){
//                var item = tb.getComponent(itemID);
//                tb.remove(item);
//            }
//        }
    });

    searchBox.on("search", function(values){
    	 detailsForm.getForm().reset();
         detailsForm.record = null;
         if(values.RoleSearch === "undefined"){
             values.RoleSearch =null;
         }
         if(values.StatusSearch === "undefined"){
             values.StatusSearch =null;
         }
         if(values.RestrictionsSearch === "undefined"){
             values.RestrictionsSearch =null;
         }
         values.IsRequestFromCCReviewerTab = true;
         listBox.store.baseParams = values;
         
         listBox.store.baseParams[CmFinoFIX.message.JSUsers.CreationDateStartTime._name] = Ext.getCmp('ccRegistrationTime').startDateField.getValue();
         listBox.store.baseParams[CmFinoFIX.message.JSUsers.CreationDateEndTime._name] = Ext.getCmp('ccRegistrationTime').endDateField.getValue();

         listBox.store.baseParams[CmFinoFIX.message.JSUsers.ConfirmationDateStartTime._name] = Ext.getCmp('ccConfirmationTime').startDateField.getValue();
         listBox.store.baseParams[CmFinoFIX.message.JSUsers.ConfirmationDateEndTime._name] = Ext.getCmp('ccConfirmationTime').endDateField.getValue();

         listBox.store.baseParams[CmFinoFIX.message.JSUsers.UserActivationStartTime._name] = Ext.getCmp('ccActivationTime').startDateField.getValue();
         listBox.store.baseParams[CmFinoFIX.message.JSUsers.UserActivationEndTime._name] = Ext.getCmp('ccActivationTime').endDateField.getValue();

         listBox.store.baseParams[CmFinoFIX.message.JSUsers.LastUpdateStartTime._name] = Ext.getCmp('ccLastChangeTime').startDateField.getValue();
         listBox.store.baseParams[CmFinoFIX.message.JSUsers.LastUpdateEndTime._name] = Ext.getCmp('ccLastChangeTime').endDateField.getValue();
         listBox.store.lastOptions = {
             params : {
                 start : 0,                
                 limit : 10
             }
         };
         Ext.apply(listBox.store.lastOptions.params, values);
         listBox.store.load(listBox.store.lastOptions);
        tabPanel.setUserRecord(null); //clear the tab panels
    });
    
    listBox.on("download", function() {
        var queryString;
        var values = searchBox.getForm().getValues();
        var firstNameSearch = values[CmFinoFIX.message.JSSubscriberMDN.FirstNameSearch._name];
        var lastNameSearch = values[CmFinoFIX.message.JSSubscriberMDN.LastNameSearch._name];
        var mdnSearch =  values[CmFinoFIX.message.JSSubscriberMDN.MDNSearch._name];
        var startDateSearch = getUTCdate(values.startDate);
        var endDateSearch = getUTCdate(values.endDate);
        queryString = "dType=subscriberMDN";
       
        if(firstNameSearch){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.FirstNameSearch._name+"="+firstNameSearch;
        }
        if(lastNameSearch){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.LastNameSearch._name+"="+lastNameSearch;
        }
        if(startDateSearch){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.StartDateSearch._name+"="+startDateSearch;
        }
        if(endDateSearch){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.EndDateSearch._name+"="+endDateSearch;
        }
        if(mdnSearch){
            queryString += "&"+CmFinoFIX.message.JSSubscriberMDN.MDNSearch._name+"="+mdnSearch;
        }
        var URL = "download.htm?" + queryString;
        window.open(URL,'mywindow','width=400,height=200');
    });
    
    listBox.on("defaultSearch", function() {
        searchBox.searchHandler();
    });

    listBox.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(listBox.store);
        tabPanel.setUserRecord(record);
    });

    var panel = new Ext.Panel({
        layout: "border",
        broder: false,
        width : 1020,
        items: [
        {
            region: 'west',
            width : 266,
            layout : "anchor",
            items:[ searchBox , listBox ]
        },
        {
            region: 'center',
            layout : "fit",
            items: [ panelCenter ]
        }
        ]
    });
    return panel;
};
