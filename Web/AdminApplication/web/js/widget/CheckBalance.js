/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CheckBalance = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        layout:'fit',
        floating: true,
        width:360,
        height:300,
        plain:true,
        closable: true,
        resizable: false
    });
    mFino.widget.CheckBalance.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CheckBalance, Ext.Window, {
    initComponent : function(){
        this.buttons = [
        {
            itemId:'mer.balance.emptyButton',
            text: _('EmptyBalance'),
            handler: this.emptyBalance.createDelegate(this)
        },
        {
            text: _('Close'),
            handler: this.close.createDelegate(this)
        }
        ];

        this.form = new Ext.form.FormPanel({
            frame : true,
            labelWidth : 120,
            labelPad : 20,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel : _('Pocket Description'),
                itemId: "checkBalancePocketType",
                labelSeparator :''
            },
            {
                xtype : 'displayfield',
                fieldLabel : _('Balance'),
                labelSeparator :'',
                itemId: "checkBalanceBalance",
                renderer : 'money'
            },
            {
                xtype : 'displayfield',
                fieldLabel : _('Date'),
                labelSeparator :'',
                renderer: "date",
                itemId: "checkBalanceDate"
            }
            ]
        });
        this.items = [ this.form ];
        mFino.widget.CheckBalance.superclass.initComponent.call(this);
    },

    close : function(){
        this.hide();
    },

    emptyBalance : function(){
        Ext.Msg.confirm(_("Confirm?"), _("Air time balance will be reset to 0. Are you sure you want to continue?"),
            function(btn){
                if(btn !== "yes"){
                    return;
                }
                var message= new CmFinoFIX.message.JSEmptySVAPocket();
                message.m_pCommodity = this.commodity;
                message.m_pSourceMDN = this.mdn;
                var params = mFino.util.showResponse.getDisplayParam();
                params.formWindow = this;
                mFino.util.fix.send(message, params);
                this.buttons[0].disable();
            }, this);
    },
    
    setRecord : function(response, record){
        this.form.getForm().reset();
        
        this.form.items.get("checkBalanceDate").setValue(response.m_pLastUpdateTime.m_Date);
        this.form.items.get("checkBalancePocketType").setValue(response.m_pPocketTypeText);
        this.form.items.get("checkBalanceBalance").setValue(response.m_pBalance);

        //keep these information to use when sending empty sva request
        this.commodity = response.m_pCommodity;
        this.mdn = response.m_pMDN;
        
        this.record = record;
    }
});
