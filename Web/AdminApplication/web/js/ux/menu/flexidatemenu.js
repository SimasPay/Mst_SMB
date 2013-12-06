Ext.namespace('Mfino.ux.menu');

/**
 * @class Mfino.ux.menu.DateRangeMenu
 * @extends Ext.Menu
 * @constructor
 * 
 * @param {Object}
 *            config The configuration options
 */
Mfino.ux.menu.FlexiDateMenu = function(config) {
    Mfino.ux.menu.FlexiDateMenu.superclass.constructor.call(this, config);
    this.ignoreParentClicks = true, this.addEvents("select");

    var me = this;

    var item = new Ext.menu.Item({
        text : _('Last 24 Hours'),
        handler : me.setRangeFromItem,
        scope : me,
        startDate : new Date().add(Date.DAY, -1),
        endDate : new Date()
    });
    this.add(item);

    item = new Ext.menu.Item({
        text : _('Last 7 Days'),
        handler : me.setRangeFromItem,
        scope : me,
        //startDate : new Date().add(Date.DAY, -7).clearTime(false),
        startDate : new Date().add(Date.DAY, -7),
        endDate : new Date()
    });
    this.add(item);

    item = new Ext.menu.Item({
        text : _('Last 30 Days'),
        handler : me.setRangeFromItem,
        scope : me,
        //startDate : new Date().add(Date.DAY, -30).clearTime(false),
        startDate : new Date().add(Date.DAY, -30),
        endDate : new Date()
    });
    this.add(item);
    //
    //    item = new Ext.menu.Item({
    //        text : 'Last 12 Months',
    //        handler : me.setRangeFromItem,
    //        scope : me,
    //        startDate : new Date().add(Date.MONTH, -12).clearTime(false),
    //        endDate : new Date()
    //
    //    });
    //    this.add(item);
    //
    //    item = new Ext.menu.Item({
    //        text : 'Current Year',
    //        handler : me.setRangeFromItem,
    //        scope : me,
    //        startDate : new Date().add(Date.DAY,
    //            -new Date().getDayOfYear()).clearTime(false),
    //        endDate : new Date()
    //    });
    //    this.add(item);
    //
    //    item = new Ext.menu.Item({
    //        text : 'All Dates',
    //        handler : me.setRangeFromItem,
    //        scope : me,
    //        startDate : new Date(0),
    //        endDate : new Date()
    //    });
    //    this.add(item);
    //
    //    this.add(new Ext.menu.Separator());
    //
    //    item = new Ext.menu.Item({
    //        text : 'All Dates Before...',
    //        menu : new Ext.menu.DateMenu({
    //            listeners : {
    //                select : function(picker, date) {
    //                    me.setDateRange(new Date(0), date);
    //                }
    //            }
    //        })
    //
    //    });
    //    this.add(item);
    //
    //    item = new Ext.menu.Item({
    //        text : 'All Dates After...',
    //        menu : new Ext.menu.DateMenu({
    //            listeners : {
    //                select : function(picker, date) {
    //                    me.setDateRange(date, new Date());
    //                }
    //            }
    //        })
    //    });
    //    this.add(item);
    //
    //    item = new Ext.menu.Item({
    //        text : 'Specific Date...',
    //        menu : new Ext.menu.DateMenu({
    //            listeners : {
    //                select : function(picker, date) {
    //                    me.setDateRange(date, date.add(Date.DAY, 1).add(Date.SECOND, -1));
    //                }
    //            }
    //        })
    //
    //    });
    //    this.add(item);

    item = new Ext.menu.Item({
        text : 'Date Range...',
        menu : new Mfino.ux.menu.DateRangeMenu({
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

}

Ext.extend(Mfino.ux.menu.FlexiDateMenu, Ext.menu.Menu, {
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

Ext.reg('flexidatemenu', Mfino.ux.menu.FlexiDateMenu);
