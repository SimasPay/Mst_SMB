Ext.define('Mfino.ux.form.DateRangeTriggerField',{
	extend : 'Ext.form.TriggerField',
	onTriggerClick: function(){
        var scope = this;        
        if (this.disabled) {
            return;
        }
        if (this.menu == null) {
            this.menu = Ext.create('Mfino.ux.menu.FlexiDateMenu',{
                enableScrolling: false,
                width: 150,
                listeners: {
                    select: function(menu, dateRange){
                        if(dateRange != null)
                        {
                            scope.setValue(dateRange);
                            scope.triggerBlur();
                        }
                        else
                        {
                            scope.setValue(null);
                        }
                    },
                    show: function(){
                        scope.onFocus();
                    },
                    hide: function(){
                        //scope.focus.defer(10, scope);
                    	Ext.defer(scope.focus, 10, scope);
                    }
                }
            });
        }        
        this.menu.showBy(this.el, "tl-bl");
	},
	initEvents: function(){
		this.callParent();
    },
    setValue: function(value){
        if (value != null) {
            this.startDate = value.start;
            this.endDate = value.end;
            
            var fmtDateRange = this.formatDate(value);
            Mfino.ux.form.DateRangeTriggerField.superclass.setValue.call(this, fmtDateRange);
        }else{
            this.startDate = "";
            this.endDate = "";
            Mfino.ux.form.DateRangeTriggerField.superclass.setValue.call(this, '');
        }
    },
    
    getValue: function(){
        if (this.rendered) {
            return this.value;
        }
        var v = this.el.getValue();
        if (v === this.emptyText || v === undefined) {
            v = '';
        }
        return v;
    },
    
    formatDate: function(dateRange){
        if (Ext.isDate(dateRange.start) && Ext.isDate(dateRange.end)) {
            if (dateRange.start.getDate() == dateRange.end.getDate() &&
                dateRange.start.getMonth() == dateRange.end.getMonth() &&
                dateRange.start.getYear() == dateRange.end.getYear() /*&&
                dateRange.start.getHours() == dateRange.end.getHours() &&
                dateRange.start.getMinutes() == dateRange.end.getMinutes()*/)
                //return dateRange.start.dateFormat(this.dateformat);
            	return Ext.Date.format(dateRange.start, this.dateformat);
            else 
                /*return dateRange.start.dateFormat(this.dateformat) +
                ' to ' +
                dateRange.end.dateFormat(this.dateformat);*/
            	return Ext.Date.format(dateRange.start,this.dateformat) +
                ' to ' +
                Ext.Date.format(dateRange.end,this.dateformat);
        }
        else {
            return "";
        }
    },
    
    getStartDate: function(){
        return startDate;
    },
    
    getEndDate: function(){
        return endDate;
    },
    
    isExpanded: false,
    
    startDate: null,
    endDate: null,
    dateformat: 'd-M-y'	
});