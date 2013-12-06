/* */

Ext.ns("mFino.page");

mFino.page.fundDefinitions = function(config){
	
	var detailsForm = new mFino.widget.FundDefinitionDetails(Ext.apply({
		height: 180
	}, config));


	var tabPanel = new mFino.widget.FundDefinitionTab(Ext.apply({
		myform : detailsForm
    }, config));
	
	var panelCenter = new Ext.Panel({
		layout: 'anchor',
		items : [
			{
				autoScroll : true,
				anchor : "100%",
				tbar : [
					'<b class= x-form-tbar>' + _('Fund Definition Details') + '</b>',
					'->'
					],
				items : [detailsForm ]
			},
			{
				layout: "fit",
				height:500,
				items: [ tabPanel ]
			}
		]
	});
	
	var panel = new Ext.Panel({
        broder: false,
        width : 1020,
		items: [ panelCenter]
	});
	return panel;
};