Ext.define('Mfino.ux.menu.FlexiDateMenu',{
	extend: 'Ext.menu.Menu',
	initComponent: function(){
		this.callParent(arguments);
		this.ignoreParentClicks = true, this.addEvents("select");
	    var me = this;

	    var item = new Ext.menu.Item({
	        text : 'Last 24 Hours',
	        handler : me.setRangeFromItem,
	        scope : me,
	        startDate : Ext.Date.add(new Date(), Ext.Date.DAY, -1),
	        endDate : new Date()
	    });
	    this.add(item);

	    item = new Ext.menu.Item({
	        text : 'Last 7 Days',
	        handler : me.setRangeFromItem,
	        scope : me,
	        //startDate : new Date().add(Date.DAY, -7).clearTime(false),
	        startDate : Ext.Date.add(new Date(), Ext.Date.DAY, -7),
	        endDate : new Date()
	    });
	    this.add(item);

	    item = new Ext.menu.Item({
	        text : 'Last 30 Days',
	        handler : me.setRangeFromItem,
	        scope : me,
	        //startDate : new Date().add(Date.DAY, -30).clearTime(false),
	        startDate : Ext.Date.add(new Date(), Ext.Date.DAY, -30),
	        endDate : new Date()
	    });
	    this.add(item);
	    item = new Ext.menu.Item({
	        text : 'Date Range...',
	        menu : Ext.create('Mfino.ux.menu.DateRangeMenu',{
	            enableScrolling: false,
	            height:220,
	            listeners : {
	                select : function(picker, date) {
	                    var newenddate = new Date(date.end);
	                    if(date.end != null && newenddate.getHours() === 00)
	                    {
	                        newenddate.setHours(23,59,59,9);
	                        date.end = newenddate;
	                    }
	                    me.setDateRange(date.start, date.end);
	                    me.hide();
	                }
	            }
	        })
	    });
	    
	    this.add(item);

	    this.add(new Ext.menu.Separator());

	    item = new Ext.menu.Item({
	        text : 'Reset',
	        handler : me.resetDateRange,
	        scope : me,
	        startDate : null,
	        endDate : null
	    });
	    this.add(item);
        
    },
    setRangeFromItem : function(item) {
        this.setDateRange(item.startDate, item.endDate);
    },

    resetDateRange : function() {
        this.startDate = null;
        this.endDate = null;
        this.fireEvent("select", this, {
            start : null,
            end : null
        });
    },

    setDateRange : function(start, end) {

        this.startDate = start;
        this.endDate = end;

        this.fireEvent("select", this, {
            start : start,
            end : end
        });
    },

    startDate : null,
    endDate : null
});