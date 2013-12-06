window.OthersTransferMenu = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"EmoneyToEmoeny",text:"EmoneyToEmoeny"})+
                                      addMenuItem({id:"EmoneyToBankOther",text:"EmoneyToBank"})+
                                      addMenuItem({id:"BankToEmoneyOther",text:"BankToEmoney"})+
                                      addMenuItem({id:"BankToBank",text:"BankToBank"}));
        $('#logout', this.el).remove();
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'click #EmoneyToEmoeny':   'showEmoneyToEmoenyForm',
            'click #EmoneyToBankOther':   'showEmoneyToBankForm',
            'click #BankToEmoneyOther':   'showBankToEmoneyForm',
            'click #BankToBank':   'showBankToBankForm'
        },{
        	'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart #EmoneyToEmoeny':   'showEmoneyToEmoenyForm',
            'touchstart #EmoneyToBankOther':   'showEmoneyToBankForm',
            'touchstart #BankToEmoneyOther':   'showBankToEmoneyForm',
            'touchstart #BankToBank':   'showBankToBankForm'
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
    	 var transferMenu = new TransferMenu();
         app.changePage(transferMenu);  
    },
    
    showEmoneyToEmoenyForm:function(){
    	 var transferMenu = new OthersTransferMenu();
    	 var transferInquiryForm = new TransferInquiryForm({srcPocketCode:POCKET_CODE_SVA,destPocketCode:POCKET_CODE_SVA,isSelfTransfer:false,back:transferMenu});
         app.changePage(transferInquiryForm);     	
    },
    
    showEmoneyToBankForm:function(){
    	var transferMenu = new OthersTransferMenu();
    	 var transferInquiryForm = new TransferInquiryForm({srcPocketCode:POCKET_CODE_SVA,destPocketCode:POCKET_CODE_BANK,isSelfTransfer:false,back:transferMenu});
         app.changePage(transferInquiryForm);     	
    },
    
    showBankToEmoneyForm:function(){
    	var transferMenu = new OthersTransferMenu();
    	 var transferInquiryForm = new TransferInquiryForm({srcPocketCode:POCKET_CODE_BANK,destPocketCode:POCKET_CODE_SVA,isSelfTransfer:false,back:transferMenu});
         app.changePage(transferInquiryForm); 
    },
    
    showBankToBankForm:function(){
    	var banktobankmenu = new BankToBankMenu();
    	app.changePage(banktobankmenu);     	
    }
    
});
