/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PocketDetails = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        defaultType: 'textfield',
        autoScroll: true,
        frame : true
    });

    mFino.widget.PocketDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketDetails,mFino.widget.Form, {
    initComponent : function () {
        this.labelWidth = 220;
        this.labelPad = 20;
        
        this.items = [
        {
            xtype : "displayfield",
            fieldLabel :_("Pocket Id"),
            name: CmFinoFIX.message.JSPocket.Entries.ID._name
        },
        {
            xtype : "displayfield",
            fieldLabel :_("Pocket Template"),
            name: CmFinoFIX.message.JSPocket.Entries.PocketTemplDescription._name
        },
        {
            fieldLabel: _('MDN'),
            xtype: "displayfield",
            name: CmFinoFIX.message.JSPocket.Entries.SubsMDN._name
        },
        {
            xtype : "displayfield",
            fieldLabel :_("Current Balance"),
            renderer : "money",
            name: CmFinoFIX.message.JSPocket.Entries.CurrentBalance._name
        },
        {
            xtype: "displayfield",
            fieldLabel: 'Subscriber ID',
            name: CmFinoFIX.message.JSPocket.Entries.SubscriberID._name
        },
        {
            fieldLabel: _("Account Number"),
            xtype: "displayfield",
            name: CmFinoFIX.message.JSPocket.Entries.CardPAN._name
//            renderer: function(value)
//            {
//                if(!value){
//                    return "";
//                }
//                var substring = value.substring(value.length-6,value.length);
//                var retval="",i;
//                for(i=0;i<(value.length-6);i++){
//                    retval += 'X';
//                }
//                retval +=substring;
//                return retval;
//            }
        },
        {
            fieldLabel: _("Card Alias"),
            xtype: "displayfield",
            name: CmFinoFIX.message.JSPocket.Entries.CardAlias._name
        },
        {
            fieldLabel: _("Pocket Restrictions"),
            xtype: "displayfield",
            name: CmFinoFIX.message.JSPocket.Entries.PocketRestrictionsText._name
        },
        {
            fieldLabel: _("Is Default"),
            xtype : "displayfield",
            name: CmFinoFIX.message.JSPocket.Entries.IsDefault._name
        },
        {
            fieldLabel: _("Pocket Status"),
            xtype:'displayfield',
            name: CmFinoFIX.message.JSPocket.Entries.PocketStatusText._name
        },
        {
            fieldLabel: _("Create Time"),
            xtype:'displayfield',
            renderer:"date",
            name: CmFinoFIX.message.JSPocket.Entries.CreateTime._name
        },
        {
            fieldLabel: _("Activation Time"),
            xtype:'displayfield',
            renderer: "date",
            name: CmFinoFIX.message.JSPocket.Entries.ActivationTime._name
        },
        {
            fieldLabel: _("Last Update Time"),
            xtype:'displayfield',
            renderer: "date",
            name: CmFinoFIX.message.JSPocket.Entries.LastUpdateTime._name
        },
        {
            fieldLabel: _("Last Transaction Time"),
            xtype:'displayfield',
            renderer: "date",
            name: CmFinoFIX.message.JSPocket.Entries.LastTransactionTime._name
        },
        {
            fieldLabel: _("Updated By"),
            xtype:'displayfield',
            name: CmFinoFIX.message.JSPocket.Entries.UpdatedBy._name
        },
        {
            fieldLabel: _("Current Daily Expenditure"),
            xtype:'displayfield',
            renderer : "money",
            name: CmFinoFIX.message.JSPocket.Entries.CurrentDailyExpenditure._name
        },
        {
            fieldLabel: _("Current Weekly Expenditure"),
            xtype:'displayfield',
            renderer : "money",
            name: CmFinoFIX.message.JSPocket.Entries.CurrentWeeklyExpenditure._name
        },
        {
            fieldLabel: _("Current Monthly Expenditure"),
            xtype:'displayfield',
            renderer : "money",
            name: CmFinoFIX.message.JSPocket.Entries.CurrentMonthlyExpenditure._name
        },
        {
            fieldLabel: _("Current Daily Transactions Count"),
            xtype:'displayfield',
            name: CmFinoFIX.message.JSPocket.Entries.CurrentDailyTxnsCount._name
        },
        {
            fieldLabel: _("Current Weekly Transactions Count"),
            xtype:'displayfield',
            name: CmFinoFIX.message.JSPocket.Entries.CurrentWeeklyTxnsCount._name
        },
        {
            fieldLabel: _("Current Monthly Transactions Count"),
            xtype:'displayfield',
            name: CmFinoFIX.message.JSPocket.Entries.CurrentMonthlyTxnsCount._name
        },
        {
            fieldLabel: _("Old Pocket Template Description"),
            xtype:'displayfield',
            name: CmFinoFIX.message.JSPocket.Entries.OldPocketTemplDescription._name
        },
        {
            fieldLabel: _("Pocket Upgrade/Downgrade Time"),
            xtype:'displayfield',
            renderer: 'date',
            name: CmFinoFIX.message.JSPocket.Entries.PocketTemplateChangeTime._name
        },
        {
            fieldLabel: _("Pocket Upgraded/Downgraded By"),
            xtype:'displayfield',
            name: CmFinoFIX.message.JSPocket.Entries.PocketTemplateChangedBy._name
        }
        ];

        mFino.widget.PocketDetails.superclass.initComponent.call(this);
    }
});

Ext.reg("pocketdetails", mFino.widget.PocketDetails);

