/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.FormWindowLOP = function (config){
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        layout:'fit',
        floating: true,
        width: 800,
        height:400,
        closable:true,
        resizable: false,
        plain:true,
        fileUpload: true
    });
    mFino.widget.FormWindowLOP.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.FormWindowLOP, Ext.Window, {
    initComponent : function(){
        this.buttons = [
        {
            itemId : "rechargeButton",
            text: _('Recharge'),
            handler: this.onRecharge.createDelegate(this)
        }
        ,{
            itemId : "send",
            text: _('Send'),
            handler: this.send.createDelegate(this)
        },{
            itemId: "upload",
            text: _('Upload'),
            handler: this.onUpload.createDelegate(this)
        }
        ,{
            itemId: "transfer",
            text: _('Transfer'),
            handler: this.onTransfer.createDelegate(this)
        }
        ,{
            itemId: "cashoutsearch",
            text: _('Submit'),
            handler: this.onCashoutSearch.createDelegate(this)
        }
        ,{
            itemId: "cashoutconfirm",
            text: _('Approve'),
            handler: this.onCashoutConfirm.createDelegate(this)
        }
        ,{
            itemId: "reverseTransaction",
            text: _('Reverse Transaction'),
            handler: this.onReverseTransaction.createDelegate(this)
        }        
        ,{
            itemId: "cancel",
            text: _('Cancel'),
            handler: this.cancel.createDelegate(this)
        },
        {
            itemId: "changePin",
            text: _('Change'),
            handler: this.changePin.createDelegate(this)
        }
        ,{
            itemId: "cancelTransfer",
            text: _('Cancel'),
            handler: this.onCancelTransfer.createDelegate(this)
        },{
            itemId: "confirm",
            text: _('Confirm'),
            handler: this.onConfirm.createDelegate(this)
        },{
            itemId: "closeaccount",
            text: _('Close Account'),
            handler: this.onCloseAccount.createDelegate(this)
        },
        {
            itemId: "uploadTransfer",
            text: _('Submit'),
            handler: this.onUploadTransfer.createDelegate(this)
        },
        {
            itemId: "cancelConfirm",
            text: _('Cancel'),
            handler: this.onCancelConfirm.createDelegate(this)
        },        
        {
            itemId: "confirmTransfer",
            text: _('Confirm & Proceed'),
            handler: this.onConfirmTransfer.createDelegate(this)
        },
        {
        	itemId: "upgradekyc",
        	text: _('Upgrade'),
        	handler: this.onUpgradeKyc.createDelegate(this)
        },
        {
        	itemId: "addpocket",
        	text: _('Submit'),
        	handler: this.onAddPocket.createDelegate(this)
        },
        {
        	itemId: "proceed",
        	text: _('Proceed'),
        	handler: this.onProceed.createDelegate(this)
        },
		{
        	itemId: "subeditmaker",
        	text: _('Submit'),
        	handler: this.onSubscriberEdit.createDelegate(this)
        }
        ];

        this.items = [this.form];

        mFino.widget.FormWindowLOP.superclass.initComponent.call(this);

        this.on("render", function(){
            if(this.initialConfig.mode){
                this.setMode(this.initialConfig.mode);
            }
        });

        this.on("show", function(){
            this.reloadRemoteDropDown();
        });
    },

    setMode : function(mode){
        Ext.each(this.buttons, function(item){
            item.hide();
            if(mode === "lop" && (item.itemId === "send"|| item.itemId === "cancel")){
                item.show();
            }
            if(mode === "transfer" && (item.itemId === "transfer" || item.itemId === "cancel")){
                item.show();
            }
            if(mode === "recharge" && (item.itemId === "rechargeButton"|| item.itemId === "cancel")){
                item.show();
            }
            if(mode === "bulk" && (item.itemId === "upload" || item.itemId === "cancel")){
                item.show();
            }
            if(mode === "cashout" && (item.itemId === "cashoutsearch" || item.itemId === "cancel")){
                item.show();
            }
            if(mode === "cashoutview" && (item.itemId === "cashoutconfirm" || item.itemId === "cancel")){
                item.show();
            }
            if(mode === "reverseTransaction" && (item.itemId === "reverseTransaction" || item.itemId === "cancel")){
                item.show();
            }
            if(mode === "changepin" && item.itemId === "changePin"){
                item.show();
            }
            if(mode === "bulkTransfer" && (item.itemId === "uploadTransfer" || item.itemId === "cancelTransfer")){
                item.show();
            }
            if(mode === "closeaccount" && (item.itemId === "closeaccount" || item.itemId === "cancel")){
                item.show();
            }
            if(mode === "confirm" && (item.itemId === "confirm" || item.itemId === "cancel")){
                item.show();
            }
            if(mode === "confirmTransfer" && (item.itemId === "confirmTransfer" || item.itemId === "cancelConfirm")){
                item.show();
            }

            if(mode === "upgradekyc" && (item.itemId === "upgradekyc" || item.itemId === "cancel")){
                item.show();
            }
            if(mode === "addpocket" && (item.itemId === "addpocket" || item.itemId === "cancel")){
                item.show();
            }
            if(mode === "proceed" && (item.itemId === "proceed" || item.itemId === "cancel")){
            	item.show();
            }
            if(mode === "subeditmaker" && (item.itemId === "subeditmaker" || item.itemId === "cancel")){
                item.show();
            }
            if(mode === "cancel" && (item.itemId === "cancel")){
                item.show();
            }             
        });
    },
    setFormWindow : function(){
    	this.form.setFormWindow(this);
    }, 
    apply : function(isCloseWindow){
        if(this.form.getForm().isValid()){
            this.form.store.un("write", this.successNotify);
            this.form.store.on("write", this.successNotify, this,
            {
                single : true
            });

            this.form.save();
            if(isCloseWindow === true){
                if(this.form.store.modified.length > 0){
                    this.form.store.on("write", function(){
                        this.hide();
                    }, this,
                    {
                        single : true
                    });
                }else{
                    this.hide();
                }
            }
        }
    },

    successNotify : function(){
        Ext.ux.Toast.msg(_("Message"), _("Record saved successfully"));
    },

    ok : function(){
        this.apply(true);
    },
    onUpload: function(){
        
        if(this.form.getForm().isValid()){
            var title = this.title;
            if(title.search('Upload File')>=0 || title.search('Bulk Resolve')>=0 || title.search('Upload Transfer File')>=0 ){
                var status;
                var urlpattern;
                if(title.search('Upload File')>=0 )
                {
                    urlpattern = 'uploadsubscribers.htm';                    
                }
                else if (title.search('Upload Transfer File')>=0 ) 
                {
                	urlpattern = 'bulkTransfer.htm';     
                }
                else
                {
                    urlpattern = 'bulkresolve.htm';
                    if(this.form.find('itemId','complete')[0].checked){
                         status = "successful";
                    }else if(this.form.find('itemId','cancel')[0].checked){
                        status ="fail";
                    }
                }
                this.form.getForm().submit({
                    url: urlpattern,
                    waitMsg: _('Uploading your file...'),
                    reset: false,
                    success : function(fp, action){
                    	var msg="";
                    	if(action.result.totalCount){
                    		msg = 'Success, Uploaded '+ action.result.file+  ' the file to the server '+ 'TotalCount : '+action.result.totalCount;
                    	}else{
                    		msg= 'Success, Uploaded '+ action.result.file+  ' the file to the server ';
                    	}
                        Ext.Msg.show({
                            title: _('Info'),
                            minProgressWidth:250,
                            msg: _(msg),
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
                    },
                    failure : function(fp, action){
                        Ext.Msg.show({
                            title: _('Error'),
                            minProgressWidth:250,
                            msg: action.result.Error,
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
                    }
                    ,
                    params: {
                        markAs:status
                    }
                });
                this.hide();
                return;
            }
            var Field;
            if(this.form.find("itemId","futuredate")[0].getValue()){
                Field = this.form.find("itemId","futuredate")[0].getValue();
                if(Field < new Date()){
                    Ext.ux.Toast.msg(_("Error"), _("Please Enter Valid Future Date"), 3);
                    return;
                }
            }else if(this.form.find("itemId","immediate")[0].getValue()) {
                Field = new Date();
            }
           
            var RetVal = new FIX.CUTCTimeStamp();
            var fp=this.form;
            RetVal.m_Date = Field ;
            var cuctime = RetVal.ToString();
            this.form.getForm().submit({
                url: 'upload.htm',
                waitMsg: _('Uploading your file...'),
                reset: false,
                success : function(fp, action){
                    Ext.Msg.show({
                        title: _('Info'),
                        minProgressWidth:250,
                        msg: _('Success, Uploaded ')+ action.result.file +  _(' the file to the server '),
                        buttons: Ext.MessageBox.OK,
                        multiline: false
                    });
                },
                failure : function(fp, action){
                    Ext.Msg.show({
                        title: _('Error'),
                        minProgressWidth:250,
                        msg: action.result.Error,
                        buttons: Ext.MessageBox.OK,
                        multiline: false
                    });
                },
                params: {
                    subscriberid: this.merchantId,
                    mdn: this.merchantMdn,
                    filedeliverydate: cuctime,
                    bulkfiletype: this.form.getComponent("bulkfiletype").getValue()
                }
            });
            this.hide();
        }
    },

    onRecharge: function(){
        if(this.form.getForm().isValid()){
            this.form.recharge(this);
            this.buttons[0].disable();
        }
    },

    /*onTransfer:function(){
        if(this.form.getForm().isValid()){
            this.form.transfer(this);
            this.buttons[3].disable();
        }
    }*/
    onTransfer:function(){
        if(this.form.getForm().isValid()){
        	this.form.transfer(this);
           }
    },
    
    onConfirm:function(){
        if(this.form.getForm().isValid()){
        	this.form.confirm(this);
           }
    },
    
    onCloseAccount:function(){
        if(this.form.getForm().isValid()){
        	this.form.closeaccount(this);
           }
    },
    
    onCashoutSearch:function(){
        if(this.form.getForm().isValid()){
        	this.form.search(this);
         }
    },
    
    onReverseTransaction:function(){
        if(this.form.getForm().isValid()){
        	this.form.reverseTransaction(this);
         }
    },
    
    onCashoutConfirm:function(){
        if(this.form.getForm().isValid()){
        	this.form.confirm(this);
           }
    },
    
    changePin:function(){
        if(this.form.getForm().isValid()){
        	this.form.change(this);
           }
    },

    send : function(){
        var actualPaid = this.form.getForm().items.get("actualpaid").getValue();
        actualPaid = parseInt(actualPaid.replace(/\,/g,''),10);
        if(this.form.getForm().isValid()){
            if(parseInt(this.form.getForm().items.get("AvailableforLOP").getValue(),10) > actualPaid){
                this.form.save(this, actualPaid);
                this.buttons[1].disable();
            }else{
                Ext.Msg.show({
                    title: _('Error'),
                    minProgressWidth:250,
                    msg: _('Sorry, LOP Amount is more than the Available LOP'),
                    buttons: Ext.MessageBox.OK,
                    multiline: false
                });
            }
        }
    },
    cancel : function(){
        this.hide();
        this.form.getForm().reset();
    },
    
    onCancelTransfer : function(){
        Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to cancel the Bulk Transfer?"),
            function(btn){
                if(btn !== "yes"){
                    return;
                }
                this.hide();
                this.form.getForm().reset();
            }, this);
    },
    
    onUploadTransfer: function(){
        if(this.form.getForm().isValid()){
        	this.form.onUploadTransfer(this);
        }
    },
    
    onConfirmTransfer: function(){
        if(this.form.getForm().isValid()){
        	this.form.onConfirmTransfer(this);
        }
    },
    
    onCancelConfirm: function() {
        Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to cancel the Bulk Transfer?"),
            function(btn){
                if(btn !== "yes"){
                    return;
                }
                this.form.onCancelConfirm(this);
            }, this);
    },
    
    onUpgradeKyc : function(){
    	if(this.form.getForm().isValid()){
    		Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to Upgrade the KYC Level?"),
		        function(btn){
		            if(btn !== "yes"){
		                return;
		            }
		            this.form.onUpgradeKyc(this);
		        }, this);
        }else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before submit"),5);
        }
    },
    onAddPocket : function(){
    	if(this.form.getForm().isValid()){
        	this.form.onAddPocket(this);
        }else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before submit"),5);
        }
    },
    onProceed : function(){
    	if(this.form.getForm().isValid()){
    		if(this.form.find("itemId","bapprove")[0].checked){
    		Ext.Msg.confirm(_("Confirm?"), _("Are you sure want to approve the request to add Bank pocket for this subscriber ?"),
		        function(btn){
		            if(btn !== "yes"){
		                return;
		            }
		            this.form. onProceed(this);
		        }, this);
    		}else if(this.form.find("itemId","breject")[0].checked){
    			Ext.Msg.confirm(_("Confirm?"), _("Are you sure want to reject the request to add Bank pocket for this subscriber? ?"),
    			        function(btn){
    			            if(btn !== "yes"){
    			                return;
    			            }
    			            this.form. onProceed(this);
    			        }, this);
    		}
    		}else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before submit"),5);
        }
    },
    
    onSubscriberEdit : function(){
    	if(this.form.getForm().isValid()){
    		Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to edit Subscriber data?"),
		        function(btn){
		            if(btn !== "yes"){
		                return;
		            }
		            this.form.onSubscriberEdit(this);
		        }, this);
        }else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before submit"),5);
        }
    },
    
    setRecord : function(record){
        this.form.setRecord(record);
    },

    setMerchantFields: function(mdn,id){
        this.merchantMdn = mdn;
        this.merchantId = id;
    },
    
    setStore : function(store){
        this.form.store = store;
    },
    reloadRemoteDropDown : function(){
        this.form.getForm().items.each(function(item) {
            if(item.getXType() == 'remotedropdown'){
                item.reload();
            }
            else if (item.getXType() == 'editableremotedropdown') {
                item.reload();
            }
        });
    },
    close: function(){
    	this.hide();
    	 this.form.getForm().reset();
    }
});
