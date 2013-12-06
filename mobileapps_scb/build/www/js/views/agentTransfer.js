window.AgentTransferMenu = Backbone.View.extend({ 
	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"FromBank",text:"From Bank"})+
        					   addMenuItem({id:"ToBank",text:"To Bank"})+
        					   addMenuItem({id:"ToAgent",text:"To Agent"}));
        $('#logout', this.el).remove(); 
        this.events=getEvents({
     	   'click #logout':   'logout',
            'click #FromBank':   'showFormBankForm',
            'click #ToBank':   'showToBankForm',
            'click #ToAgent':   'showToAgentForm',
            'click #back': 'back' 
        },{
     	   'touchstart #logout':   'logout',
            'touchstart #FromBank':   'showFormBankForm',
            'touchstart #ToBank':   'showToBankForm',
            'touchstart #ToAgent':   'showToAgentForm',
            'touchstart #back': 'back'  
        });         
    
       },
       

    render:function (eventName) {    	
      return this;
    },
    
   
    back:function(e){
    	app.menu();	
    },
    
    logout:function (e) {
      app.logout();    	  
    },
    
    showFormBankForm:function(e){
      var back = new AgentTransferMenu();
  	  var transferMenu = new TransferInquiryForm({srcPocketCode:POCKET_CODE_BANK,destPocketCode:POCKET_CODE_SVA,isSelfTransfer:true,isAgent:true,back:back});
        app.changePage(transferMenu);   
    },
    showToBankForm:function(e){
    		var back = new AgentTransferMenu();
    	  var transferMenu = new TransferInquiryForm({srcPocketCode:POCKET_CODE_SVA,destPocketCode:POCKET_CODE_BANK,isSelfTransfer:true,isAgent:true,back:back});
          app.changePage(transferMenu);   
    },
    showToAgentForm:function(e){
    		var back = new AgentTransferMenu();
    	  var transferMenu = new TransferInquiryForm({srcPocketCode:POCKET_CODE_SVA,destPocketCode:POCKET_CODE_SVA,isSelfTransfer:false,isAgent:true,txnName:TRANSACTION_AGENT_AGENT_TRANSFER_INQUIRY,confirmTxnName:TRANSACTION_AGENT_TO_AGENT_TRANSFER,back:back});
          app.changePage(transferMenu);   
     }
});

