Ext.namespace('Mfino.ux.form');

/**
 * @class Ext.ux.form.DateRangeField
 * @extends Ext.form.TriggerField
 * @constructor
 *
 * @param {Object}
 *            config The configuration options
 */
Mfino.ux.form.DateRangeField = function(config){
    Mfino.ux.form.DateRangeField.superclass.constructor.call(this, config);
    
}
Ext.extend(Mfino.ux.form.DateRangeField, Ext.Container, {

    initComponent: function(){
    
        this.autoEl = {};
        this.layout = 'fit';
        this.isFormField = true;
        this.editable = false;
        this.addEvents("change");
        this.addEvents("specialkey");
        
        var scope = this;
        
        this.triggerField = new Mfino.ux.form.DateRangeTriggerField({
            isFormField: false,
            name : 'dateRange',
            editable: false,
            emptyText: _('<select dates...>'),
            listeners: {
                change: function(field, value){
                    scope.startDateField.setValue(this.startDate);
                    scope.startDateField.onBlur();
                    
                    scope.endDateField.setValue(this.endDate);
                    scope.endDateField.onBlur();
                    
                    scope.fireEvent("change", scope, value);
                },
                specialkey : function(f, e) {
                    scope.fireEvent("specialkey", f, e);
                }
            }
        });
        
        this.startDateField = new Mfino.ux.form.DateRangeHiddenField({
            name: 'startDate',
            dataIndex: this.startDataIndex
        });
        
        this.endDateField = new Mfino.ux.form.DateRangeHiddenField({
            name: 'endDate',
            dataIndex: this.endDataIndex
         
     /*unwanted code
                setValue: function(value){
                Mfino.ux.form.DateRangeHiddenField.prototype.setValue.call(this, value);
                scope.triggerField.setValue({
//                    start: Date.parseDate(scope.startDateField.getValue(),'Ymd-00:00:00:00'),
//                    end: Date.parseDate(scope.endDateField.getValue(),'Ymd-00:00:00:00')
                    start: Date.parseDate(scope.startDateField.getValue(),'Ymd-H:i:s:00'),
                    end: Date.parseDate(scope.endDateField.getValue(),'Ymd-H:i:s:00')
                });
                this.setValue = Mfino.ux.form.DateRangeHiddenField.prototype.setValue;
            }
            */
        }); 
        
        
        this.items = [this.triggerField, this.startDateField, this.endDateField]
        
        Mfino.ux.form.DateRangeField.superclass.initComponent.call(this);
    },
    
    triggerField: null,
    startDateField: null,
    endDateField: null
});

Mfino.ux.form.DateRangeHiddenField = function(config){
    this.hidden = true;
    Mfino.ux.form.DateRangeHiddenField.superclass.constructor.call(this, config);
}

Ext.extend(Mfino.ux.form.DateRangeHiddenField, Ext.form.TextField, {
    setValue: function(value){
        if (Ext.isDate(value)) {
          //  value = value.format('Ymd-00:00:00:00');
              value = value.format('Ymd-H:i:s:u');
        }
		
        Mfino.ux.form.DateRangeHiddenField.superclass.setValue.call(this, value);
    }
});

Mfino.ux.form.DateRangeTriggerField = function(config){
    Mfino.ux.form.DateRangeTriggerField.superclass.constructor.call(this, config);
}

Ext.extend(Mfino.ux.form.DateRangeTriggerField, Ext.form.TriggerField, {
    onTriggerClick: function(){
        var scope = this;
        
        if (this.disabled) {
            return;
        }
        if (this.menu == null) {
            this.menu = new Mfino.ux.menu.FlexiDateMenu({
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
                            scope.setValue(null)
                        }
                    },
                    show: function(){
                        scope.onFocus()
                    },
                    hide: function(){
                        scope.focus.defer(10, scope);
                    }
                }
            });
        }
        
        this.menu.show(this.el, "tl-bl");
    },
    
    initEvents: function(){
        Mfino.ux.form.DateRangeTriggerField.superclass.initEvents.call(this);
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
                dateRange.start.getYear() == dateRange.end.getYear())
                return dateRange.start.dateFormat(this.dateformat);
            else 
                return dateRange.start.dateFormat(this.dateformat) +
                ' to ' +
                dateRange.end.dateFormat(this.dateformat);
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

Ext.reg('daterangefield', Mfino.ux.form.DateRangeField);
