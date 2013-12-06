window.BankToBankMenu = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"ToZenith",text:"To Zenith"})+
        					   addMenuItem({id:"ToOther",text:"To Other"}));
        $('#logout', this.el).remove(); 
        this.events=getEvents({
     	   'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'click #ToZenith':   'showToZenith',
            'click #ToOther':   'showToOthers'
        },{
     	   'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart #ToZenith':   'showToZenith',
            'touchstart #ToOther':   'showToOthers'
        });   

       },
       

    render:function (eventName) {    	
      return this;
    },
    
    
    logout:function (e) {
      app.logout();    	  
    },
    
    home:function(e){
  	  app.menu();  
    },
    
    back:function(e){
    	 var transferMenu = new OthersTransferMenu();
         app.changePage(transferMenu);  
    },
    
    showToZenith:function(){
    	 var back = new BankToBankMenu();
    	 var transferForm = new TransferInquiryForm({srcPocketCode:POCKET_CODE_BANK,destPocketCode:POCKET_CODE_BANK,isSelfTransfer:false,back:back,interbank:false});
          app.changePage(transferForm);  	
    },
    
    showToOthers:function(){
    	var back = new BankToBankMenu();
    	 var transferForm = new TransferInquiryForm({srcPocketCode:POCKET_CODE_BANK,destPocketCode:POCKET_CODE_BANK,isSelfTransfer:false,back:back,interbank:true,txnName:TRANSACTION_INTERBANK_TRANSFER_INQUIRY,confirmTxnName:TRANSACTION_INTERBANK_TRANSFER});
           app.changePage(transferForm); 
    }
    
});