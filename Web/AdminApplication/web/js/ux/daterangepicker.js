Ext.ns("Mfino.ux");

/**
 * @class Mfino.ux.DateRangePicker
 * @extends Ext.Component
 * @constructor
 * 
 * @param {Object}
 *            config The configuration options
 */
Ext.apply(Ext.form.VTypes, {
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
});

Mfino.ux.DateRangePicker = function(config) {
    this.addEvents("select");

    this.startPicker = new Ext.DatePicker({
        showToday : false,
        cls : 'mfino-datepicker'
    });

    this.startHour = new Ext.form.NumberField(
    {
        itemId : 'stime',
        width:20,
        maxLength: 2,
        maxValue: 23,
        minValue: 0
    });
    this.endPicker = new Ext.DatePicker({
        showToday : false,
        cls : 'mfino-datepicker'
    });
    this.endHour = new Ext.form.NumberField(
    {
        itemId : 'etime',
        width:20,
        maxLength: 2,
        maxValue: 23,
        minValue: 0
    });

    Mfino.ux.DateRangePicker.superclass.constructor.call(this, config);
}

Ext.extend(Mfino.ux.DateRangePicker, Ext.Panel, {

    layout: 'fit',
    layoutConfig: {
        defaultMargins : {
            left:5,
            top:5,
            right:5,
            bottom:5
        }
    },
    height:215,
    width:393,
    buttonAlign: 'center',

    initComponent : function() {
        var scope = this;
        this.items = [{
            xtype : 'container',
            autoEl : {},
            plain : true,
            layout : 'table',
            defaults : {
                border : false
            },
            layoutConfig : {
                columns : 2
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
        }]


        this.bbar= new Ext.Toolbar({
            items:[
            {
                xtype:"spacer",
                width:80
            },
            this.startHour,
            'Hour',
            {
                xtype:"spacer",
                width:50
            },
            {
                xtype : 'button',
                text : 'Done',
                hideParent: true,
                listeners : {
                    click : function() {
                        var start = scope.startPicker.getValue();
                        start = start.add(Date.HOUR, scope.startHour.getValue());
                        var end = scope.endPicker.getValue();
                        end = end.add(Date.HOUR, scope.endHour.getValue());
                        if(start <= end) 
                        {
                            scope.fireEvent("select", scope, {
                                start : start,
                                end : end
                            });
                        }
                        else {
                            Ext.ux.Toast.msg(_("Error"), _("Please select valid start and end dates..."));
                            scope.fireEvent("select",scope,{
                                start : null,
                                end: null
                            });
                        }
                    }
                }
            },
            {
                xtype:"spacer",
                width:70
            },
            this.endHour,
            "Hour"
            ]
        });

        Mfino.ux.DateRangePicker.superclass.initComponent.call(this);
    },

    startPicker : null,
    startHour : null,
    endPicker : null,
    endHour : null
});

Ext.reg('daterangepicker', Mfino.ux.DateRangePicker);
