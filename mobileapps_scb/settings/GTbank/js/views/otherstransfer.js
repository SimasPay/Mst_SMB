window.OthersTransferMenu = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"Frommkuza",text:"From mKuza"})+
        							  addMenuItem({id:"Frombank",text:"From Bank"}));
        $('#logout', this.el).remove();
        this.events=getEvents({
        	
            'click #home':   'home',
            'click #back':   'back',
            'click #Frommkuza':   'showmKuzaMenu',
            'click #Frombank':   'showBankMenu',
        },{
        	
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart #Frommkuza':   'showmKuzaMenu',
            'touchstart #Frombank':   'showBankMenu',
        });         
      },

    render:function (eventName) {    	
      return this;
    },
    
   home:function(e){
  	  app.menu();  
    },
    
    back:function(e){
    	 var transferMenu = new TransferMenu();
         app.changePage(transferMenu);  
    },
    
    showmKuzaMenu:function(){
    	var mKuza=new mKuzaMenu();
    	app.changePage(mKuza);
    },
    
    showBankMenu:function(){
    	var BankMenu=new mKuzaMenu();
    	app.changePage(BankMenu);
    },
    
    
});
window.mKuzaMenu = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"Tophone",text:"To Phone"})+
        							  addMenuItem({id:"Tobankacc",text:"To Bank Account"}));
        $('#logout', this.el).remove();
        this.events=getEvents({
        	
            'click #home':   'home',
            'click #back':   'back',
            'click #Tophone':   'showmKuzaToPhoneform',
            'click #Tobankacc':   'showmKuzaToBankAcc'
        },{
        	
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart #Tophone':   'showmKuzaToPhoneform',
            'touchstart #Tobankacc':   'showmKuzaToBankAcc'
        });         
      },

    render:function (eventName) {    	
      return this;
    },
    
   
    home:function(e){
  	  app.menu();  
    },
    
    back:function(e){
    	 var transferMenu = new OthersTransferMenu();
         app.changePage(transferMenu);  
    },
    
    showmKuzaToPhoneform:function(){
    	var transferMenu=new mKuzaMenu();
    	var mKuzaToPhone=new TransferInquiryForm({srcPocketCode:POCKET_CODE_SVA,destPocketCode:POCKET_CODE_SVA,isOthersTransfer:true,back:transferMenu});
    	app.changePage(mKuzaToPhone);
    },
    
    showmKuzaToBankAcc:function(){
    	var transferMenu=new mKuzaMenu();
    	var mKuzaToBank=new TransferInquiryForm({srcPocketCode:POCKET_CODE_SVA,destPocketCode:POCKET_CODE_BANK,isOthersTransfer:true,back:transferMenu});
    	app.changePage(mKuzaToBank);
    }
    
    
});
