Ext.define('Mfino.ux.form.DateRangeField',{
	extend: 'Ext.Container',
	alias: 'widget.daterangefield',
	
	initComponent: function(){
        this.autoEl = {};
        this.layout = 'form';
        this.isFormField = false;
        this.editable = false;
        this.addEvents("change");
        this.addEvents("specialkey");
        this.isValid = function(){
        	return true;
        };
        
        var scope = this;
        
        this.triggerField = Ext.create('Mfino.ux.form.DateRangeTriggerField', Ext.applyIf({
            //isFormField: true,
            name : 'dateRange',            
            editable: false,
            emptyText: '<select dates...>',
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
        },this.initialConfig));
        
        var triggerField = this.triggerField;
        
        this.triggerField.on('render', function(obj) {        
            obj.tip = Ext.create('Ext.tip.ToolTip', {
                        target : obj.getEl().getAttribute("id"),
                        trackMouse : true,
                        renderTo : document.body,
                        listeners: {                            
                            beforeshow: function updateTipBody(tip) {
                            	if(triggerField.getValue()){
                            		tip.update(triggerField.getValue());
                            	} else 
                            		return false;                                
                            }
                        }
                    });
        });
        
        this.startDateField = Ext.create('Mfino.ux.form.DateRangeHiddenField',{
            name: 'startDate',
            dataIndex: this.startDataIndex
        });
        
        this.endDateField = Ext.create('Mfino.ux.form.DateRangeHiddenField',{
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
        
        
        this.items = [this.triggerField, this.startDateField, this.endDateField];
        
        this.callParent(arguments);
	}	
});