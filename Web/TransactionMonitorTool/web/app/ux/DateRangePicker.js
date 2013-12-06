Ext.define('Mfino.ux.DateRangePicker',{
	extend: 'Ext.Panel',
	layout: 'fit',
    layoutConfig: {
        defaultMargins : {
            left:5,
            top:5,
            right:5,
            bottom:5
        }
    },
    height:230,
    width:393,
    buttonAlign: 'center',
    startPicker : null,
    startHour : null,
    endPicker : null,
    endHour : null,
	initComponent : function() {
		
		this.addEvents("select");

	    this.startPicker = Ext.create('Ext.DatePicker',{
	        showToday : false,
	        cls : 'mfino-datepicker'
	    });

	    /*this.startHour = Ext.create('Ext.form.NumberField',{
	        itemId : 'shour',
	        width:40,
	        maxLength: 2,
	        maxValue: 23,
	        minValue: 0
	    });
	    this.startMinute = Ext.create('Ext.form.NumberField',{
	        itemId : 'smin',
	        width:40,
	        maxLength: 2,
	        maxValue: 59,
	        minValue: 0
	    });*/
	    this.endPicker = Ext.create('Ext.DatePicker',{
	        showToday : false,
	        cls : 'mfino-datepicker'
	    });
	    /*this.endHour = Ext.create('Ext.form.NumberField',{
	        itemId : 'ehour',
	        width:40,
	        maxLength: 2,
	        maxValue: 23,
	        minValue: 0
	    });
	    this.endMinute = Ext.create('Ext.form.NumberField',{
	        itemId : 'emin',
	        width:40,
	        maxLength: 2,
	        maxValue: 59,
	        minValue: 0
	    });*/


        var scope = this;
        this.items = [{
            xtype : 'container',            
            plain : true,
            layout: {
            	type: 'table',
            	columns: 2            	
            },
            defaults : {
                border : false
            },  
            items : [{
                html : '<center>Start Date</center>',
                cellCls : 'mfino-daterangepicker-cell mfino-daterangepicker-label'
            }, {
                cellCls : 'mfino-daterangepicker-cell mfino-daterangepicker-label',
                html : '<center>End Date</center>'
            }, {
                xtype : 'container',
                cellCls : 'mfino-daterangepicker-cell',
                items : [this.startPicker]
            }, {
                xtype : 'container',
                cellCls : 'mfino-daterangepicker-cell',
                items : [this.endPicker]
            }]
        }];


        this.bbar= Ext.create('Ext.Toolbar',{        	
            items:[
            {
                xtype:"tbspacer",
                width: 175
            },
            /*this.startHour,
            'Hr',
            this.startMinute,
            'Min',
            {
                xtype:"tbspacer",
                width:15
            },*/
            {
                xtype : 'button',
                text : 'Done',
                hideParent: true,
                listeners : {
                    click : function() {
                        var start = scope.startPicker.getValue();
                        /*start = Ext.Date.add(start, Ext.Date.HOUR, scope.startHour.getValue());
                        start = Ext.Date.add(start, Ext.Date.MINUTE, scope.startMinute.getValue());*/
                        var end = scope.endPicker.getValue();
                        /*end = Ext.Date.add(end, Ext.Date.HOUR, scope.endHour.getValue());
                        end = Ext.Date.add(end, Ext.Date.MINUTE, scope.endMinute.getValue());*/
                        if(start <= end) 
                        {
                            scope.fireEvent("select", scope, {
                                start : start,
                                end : end
                            });
                        }
                        else {
                            alert("Please select valid start and end dates...");
                            scope.fireEvent("select",scope,{
                                start : null,
                                end: null
                            });
                        }
                    }
                }
            }/*,
            {
                xtype:"tbspacer",
                width:30
            },
            this.endHour,
            "Hr",
            this.endMinute,
            "Min"*/
            ]
        });  
        this.callParent(arguments);    
	}
});

/**
 * @class Mfino.ux.DateRangePicker
 * @extends Ext.Component
 * @constructor
 * 
 * @param {Object}
 *            config The configuration options
 */
/*Ext.apply(Ext.form.VTypes, {
    daterange : function(val, field) {
        var date = field.parseDate(val);

        if (!date) {
            return false;
        }
        if (field.startDateField
            && (!this.dateRangeMax || (date.getTime() != this.dateRangeMax
                .getTime()))) {
            var start = Ext.getCmp(field.startDateField);
            start.setMaxValue(date);
            start.validate();
            this.dateRangeMax = date;
        } else if (field.endDateField
            && (!this.dateRangeMin || (date.getTime() != this.dateRangeMin
                .getTime()))) {
            var end = Ext.getCmp(field.endDateField);
            end.setMinValue(date);
            end.validate();
            this.dateRangeMin = date;
        }

        return true;
    }
});*/