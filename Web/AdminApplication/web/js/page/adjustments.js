/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.adjustments = function(config){
	var searchBox = new mFino.widget.AdjustmentsTransactionSearchForm({
		height: 75
	});
	
	var detailsForm = new mFino.widget.AdjustmentDetails(Ext.apply({
        height : 223
    }, config));
	
	var adjustmentsWindow = new mFino.widget.AdjustmentsWindow(Ext.apply({
        //title :_( 'Adjustments Window')
    },config));
	
	var approveWindow = new mFino.widget.ApproveRejectAdjustmentWindow(config);		
	
	var grid = new mFino.widget.AdjustmentsGrid(Ext.apply({
        layout:'fit',
        title : _('Adjustments Search Results'),
        frame:true,
        loadMask:true,
        height: 455,
        width: 925
    }, config));
	
	var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            autoScroll : true,
            anchor : "100%",
            tbar : [
            '<b class= x-form-tbar>' + _('Adjsutment Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-currency',
                tooltip: _('Do Adjustments'),
            	itemId : 'adjustments.apply',	
                text : _('Do Adjustments'),
                handler: function(){
                    adjustmentsWindow.resetWindow();
                    adjustmentsWindow.setMode('add');
                    adjustmentsWindow.searchForm.setEditable(true);
                    adjustmentsWindow.adjustmentForm.setEditable(false);
                    adjustmentsWindow.show();
                }
            }
            ],
            items: [ detailsForm ]
        }
        ]
    });
	
    searchBox.on("search", function(values){
    	detailsForm.getForm().reset();
        grid.store.baseParams = values;
        grid.store.baseParams[CmFinoFIX.message.JSAdjustments.StartDateSearch._name] = values.startDate;
        grid.store.baseParams[CmFinoFIX.message.JSAdjustments.EndDateSearch._name] = values.endDate;

        grid.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(grid.store.lastOptions.params, values);
        grid.store.load(grid.store.lastOptions);    	
    });
    
    grid.on("defaultSearch", function() {
        searchBox.searchHandler();
    });
    
    grid.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(grid.store);
    });
    
    grid.action.on({
        action:function(grid, record, action, row, col) {
        	if(action === 'mfino-button-history'){
        		adjustmentsWindow.show();
        		adjustmentsWindow.setMode('view');
        		adjustmentsWindow.searchForm.find('itemId','adjustments.sctlsearch.sctlid')[0].setValue(record.get('SctlId'));
        		adjustmentsWindow.searchForm.setEditable(false);
        		adjustmentsWindow.adjustmentForm.resetHandler();
        		adjustmentsWindow.adjustmentForm.setEditable(false);
        		adjustmentsWindow.searchForm.fireEvent("search", 
        				{ 
        					'SctlId' : record.get('SctlId')
        				});
        	} else if(action === 'mfino-button-resolve'){
            	if(record.data.AdjustmentStatus != null && record.data.AdjustmentStatus == CmFinoFIX.AdjustmentStatus.Requested) {
            		approveWindow.show();
                    approveWindow.setRecord(record);
            	} else {
            		Ext.MessageBox.alert(_("Alert"), _("Can approve/reject only the transaction whose Adjustment status is 'Requested'"));
            	}            	
            }
        }
    });
    
    adjustmentsWindow.on("adjustmentFormSubmit", function() {
    	grid.store.reload();
    });
    
    approveWindow.on("approveRejectFormSubmit", function() {   	
    	grid.store.reload();
    });
    
    var mainItem = panelCenter.items.get(0);
    mainItem.on('render', function(){
        var tb = mainItem.getTopToolbar();
        var itemIDs = [];
        //0, 1 are the header and alignment items
        for(var i = 2; i < tb.items.length; i++){
            itemIDs.push(tb.items.get(i).itemId);
        }
        for(i = 0; i < itemIDs.length; i++){
            var itemID = itemIDs[i];
            if(!mFino.auth.isEnabledItem(itemID)){
                var item = tb.getComponent(itemID);
                tb.remove(item);
            }
        }
    });
    
    var panel = new Ext.Panel({
        layout: "border",
        broder: false,
        width : 1020,
        items: [
        {
            region: 'west',
            width : 250,
            layout : "fit",
            items:[ searchBox ]
        },
        {
            region: 'center',
            layout : "fit",
            items: [ panelCenter ]
        },
        {
            region: 'south',
            height: 455,
            layout : "fit",
            items: [ grid ]
        }]
    });
    return panel;
};

