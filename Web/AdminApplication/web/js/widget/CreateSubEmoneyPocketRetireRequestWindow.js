/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.CreateSubEmoneyPocketRetireRequestWindow = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Create Emoney pocket/Subscriber Retire Request"),
        layout:'fit',
        floating: true,
        width:400,
        height:175,
        plain:true,
        closable: false,
        resizable: false
    });
    mFino.widget.CreateSubEmoneyPocketRetireRequestWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.CreateSubEmoneyPocketRetireRequestWindow, Ext.Window, {
    initComponent : function(){
        this.buttons = [
        {
            text: _('Ok'),
            handler: this.ok.createDelegate(this)
        },
        {
            text: _('Cancel'),
            handler: this.cancel.createDelegate(this)
        }
        ];

        this.form = new Ext.form.FormPanel({
            frame : true,
            items : [
               {
            	   fieldLabel: 'Reason'
               },{
                xtype : 'textarea',
                itemId :'comment',
                fieldLabel : _('Comments'),
                allowBlank: false,
                hideLabel: true,
                labelSeparator :'',
                anchor : '100%'
               }
			]
        });
        this.items = [ this.form ];
        mFino.widget.CreateSubEmoneyPocketRetireRequestWindow.superclass.initComponent.call(this);
    },

    cancel : function(){
        this.hide();
    },

    ok : function(){
        if(this.form.getForm().isValid()){
			var msg = new CmFinoFIX.message.RetireSubscriberEmoneyPocket();
            msg.m_pComments = this.form.items.get('comment').getValue();
			msg.m_paction = "create";
			msg.m_pMDNID = this.record.data[CmFinoFIX.message.JSSubscriberMDN.Entries.ID._name];

            var params = mFino.util.showResponse.getDisplayParam();
            mFino.util.fix.send(msg, params);
			this.hide();
			Ext.apply(params, {
			   success : function(response){
				   if(response.m_psuccess == true){
				   Ext.Msg.show({
					  title: _('Info'),
					  minProgressWidth:250,
					  msg: response.m_pErrorDescription,
					  buttons: Ext.MessageBox.OK,
					  multiline: false
				   });
				   }else{
					   Ext.MessageBox.alert(_("Error"), _(response.m_pErrorDescription));   	   
				   }
			   }
			});
		}     
        else{
            Ext.ux.Toast.msg(_("Error"), _("Please provide the reason for E-money pocket/Subscriber retirement"),5);
        }
    },
    setRecord : function(record){
        this.form.getForm().reset();
        this.record = record;
        this.form.getForm().loadRecord(record);
        this.form.getForm().clearInvalid();
    },

    setStore : function(store){
        this.store = store;
    }
});
Ext.reg("CreateSubEmoneyPocketRetireRequestWindow", mFino.widget.CreateSubEmoneyPocketRetireRequestWindow);
