/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.main = function(config){
   
    var tabItems={
        "subscribers":mFino.page.subscriber,
//        "merchant":mFino.page.merchant,
//        "transactions":mFino.page.transaction,
//        "lop":mFino.page.lop,
        "pocketTemplate":mFino.page.pocketTemplate,
        "notification":mFino.page.notification,
        "user":mFino.page.user,
        "distributionTemplate":mFino.page.distribution,
        "enumPage":mFino.page.enumPage,
//        "productIndicator":mFino.page.productIndicator,
        "mnoParams":mFino.page.brand,
        "biller":mFino.page.biller,
//        "region":mFino.page.region,
        "bulkUpload":mFino.page.bulkUpload,
//        "smsCodes":mFino.page.smsCodes,
//        "smsPartner":mFino.page.smsPartner,
        "channelCodes":mFino.page.channelCodes,
//        "merchantCodes":mFino.page.merchantCodes,
//        "merchantPrefixCodes":mFino.page.merchantPrefixCodes,
//        "creditCardReviewer":mFino.page.creditCardReviewer,
//        "bankAdmin":mFino.page.bankAdmin,
        "partner":mFino.page.serviceProvider,
        "teller":mFino.page.teller,
		"mfsBiller":mFino.page.mfsBiller,
		"Partnertransaction":mFino.page.partnertransaction,		
		"chargeType":mFino.page.chargeType,
		"chargeDefinition":mFino.page.chargeDefinition,
		"transactionRule":mFino.page.transactionRule,
		"transactionCharge":mFino.page.transactionCharge,
		"agent":mFino.page.agentsp,
		"agentsp":mFino.page.agentsp,
		"chargeTransactions":mFino.page.chargeTransactions,
		"report":mFino.page.report,
		"bulkTransfer":mFino.page.bulkTransfer,
		"groups":mFino.page.groups,
		"OLAP":mFino.page.OLAP,
		"distributionHierarchy": mFino.page.distributionHierarchy,
		"pocketTemplateConfig":mFino.page.pocketTemplateConfig,
		"systemParameters":mFino.page.systemParameters,
		"integrations":mFino.page.integrations,
		"fundingForAgent":mFino.page.fundingForAgent,
		"schedulerConfig":mFino.page.schedulerConfig,
		"fundDefinitions" :mFino.page.fundDefinitions,
		"actorChannelMapping" :mFino.page.actorChannelMapping,
		"appUploader":mFino.page.appUploader,
		"adjustments":mFino.page.adjustments,
		"permissions":mFino.page.permissions,
		"promos":mFino.page.promos
    };

    var center = new Ext.Panel({
        id : "contentPanel",
        // plugins: [new Ext.ux.plugins.ContainerMask({msg:'Please Wait.....', masked:true})],
        renderTo : config.mainRenderTo,
        width : config.width,
        height : config.height,
        // activeItem : 1,
        layout : {
            type : "card",
            deferredRender : true
        },
        items : [
        {
            id: "subscribers",
            layout : "fit"           
        },
//        {
//            id: "merchant",
//            layout : "fit"                      
//        },
//        {
//            id: 'transactions',
//            layout : "fit"            
//        },
//        {
//            id: 'lop',
//            layout : "fit"           
//        },
        {
            id: 'pocketTemplate',
            layout: "fit"            
        },
        {
            id: 'notification',
            layout: "fit"           
        },
        {
            id: 'user',
            layout : "fit"           
        },
        {
            id: 'distributionTemplate',
            layout : "fit"
        },
        {
            id: 'enumPage',
            layout : "fit"
        },
        {
            id: 'mnoParams',
            layout : "fit"
        },
//        {
//            id: 'biller',
//            layout : "fit"
//        },
//        {
//            id: 'region',
//            layout : "fit"
//        },
//        {
//            id: 'productIndicator',
//            layout : "fit"
//        },
        {
            id: 'bulkUpload',
            layout : "fit"
        },
//        {
//            id: 'smsCodes',
//            layout : "fit"
//        },
//        {
//            id: 'smsPartner',
//            layout : "fit"
//        },
        {
            id: 'channelCodes',
            layout : "fit"
        },
//        {
//            id: 'merchantCodes',
//            layout : "fit"
//        },
//        {
//            id: 'merchantPrefixCodes',
//            layout : "fit"
//        },
//        {
//            id: 'creditCardReviewer',
//            layout : "fit"
//        },
//        {
//            id: 'bankAdmin',
//            layout : "fit"
//        },
        {
            id: 'agent',
            layout : "fit"
        },
        {
            id: 'agentsp',
            layout : "fit"
        },
        {
            id: 'partner',
            layout : "fit"
        },
        {
            id: 'mfsBiller',
            layout : "fit"
        },
        {
            id: 'teller',
            layout : "fit"
        },
        {
            id: 'Partnertransaction',
            layout : "fit"
        },        
        {
            id: 'chargeType',
            layout : "fit"
        },
        {
            id: 'chargeDefinition',
            layout : "fit"
        },
        {
            id: 'transactionRule',
            layout : "fit"
        },
        {
            id: 'transactionCharge',
            layout : "fit"
        },
        {
            id: 'chargeTransactions',
            layout : "fit"
        } ,
        {
            id: 'report',
            layout : "fit"
        },
        {
            id: 'bulkTransfer',
            layout : "fit"
        },
        {
            id: 'groups',
            layout : "fit"
        },
        {
           id: 'OLAP',
           layout : "fit"
        }, 			
	    {
           id: 'pocketTemplateConfig',
           layout : "fit"
        },
        {
            id: 'distributionHierarchy',
            layout : "fit"
        },
        {
            id: 'systemParameters',
            layout : "fit"
        },
        {
            id: 'integrations',
            layout : "fit"
        },
        {
        	id: 'fundingForAgent',
        	layout : "fit"
        },
		{
			id: 'schedulerConfig',
			layout : "fit"
		},
		{
        	id: 'fundDefinitions',
        	layout : "fit"
        },
        {
        	id: 'actorChannelMapping',
        	layout : "fit"
        },
        { 
            id: 'appUploader', 
		    layout : "fit"
        },
        { 
            id: 'adjustments', 
		    layout : "fit"
        },
        { 
            id: 'permissions', 
		    layout : "fit"
        },
        { 
            id: 'promos', 
		    layout : "fit"
        }
        ]
    });
  
    var activeTab;
    for(var i = 0; i < center.items.items.length; i++){
        var tabId = center.items.items[i].getId();
        if(mFino.auth.isEnabledTab(tabId)){
            var tabComponent = center.getComponent(tabId);
            tabComponent.add(tabItems[tabId]({
                dataUrl : config.dataUrl
            }));
            if(!activeTab){
                activeTab = tabId;
            }
    }
    }
    if(activeTab){
        center.layout.setActiveItem(activeTab);
        Ext.get(activeTab + "_tab").addClass("current");
    }
    center.doLayout();
};