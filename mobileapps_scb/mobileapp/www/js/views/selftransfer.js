window.SelfTransferMenu = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"EmoneyToBank",text:"EmoneyToBank"})+
                                      addMenuItem({id:"BankToEmoney",text:"BankToEmoney"}));
        $('#logout', this.el).remove();   
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'click #EmoneyToBank':   'showEmoneyToBankForm',
            'click #BankToEmoney':   'showBankToEmoneyForm'
        },{
        	'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart #EmoneyToBank':   'showEmoneyToBankForm',
            'touchstart #BankToEmoney':   'showBankToEmoneyForm'
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
    
    showEmoneyToBankForm:function(){
    	 var back = new SelfTransferMenu();
    	 var transferInquiryForm = new TransferInquiryForm({srcPocketCode:POCKET_CODE_SVA,destPocketCode:POCKET_CODE_BANK,isSelfTransfer:true,back:back});
         app.changePage(transferInquiryForm);  	
    },
    
    showBankToEmoneyForm:function(){
    	var back = new SelfTransferMenu();
    	 var transferInquiryForm = new TransferInquiryForm({srcPocketCode:POCKET_CODE_BANK,destPocketCode:POCKET_CODE_SVA,isSelfTransfer:true,back:back});
         app.changePage(transferInquiryForm); 
    }
    
});
