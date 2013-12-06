window.AirtimeMenu = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"MTN",text:"MTN",name:2})+
        							  addMenuItem({id:"Glo",text:"Glo",name:3})+
        							  addMenuItem({id:"Visafone",text:"Visafone",name:4})+
        							  addMenuItem({id:"Starcomm",text:"Starcomm",name:5})+
        							  addMenuItem({id:"Etisalaat",text:"Etisalaat",name:6})+
        							  addMenuItem({id:"Airtel",text:"Airtel",name:1}));
        $('#logout', this.el).remove();
        this.events=getEvents({
        	'click #logout':   'logout',
	        'click #home':   'home',
	        'click #back':   'back',
	        'click #MTN':   'showAirtimeMenu',
	        'click #Glo':   'showAirtimeMenu',
	        'click #Visafone':   'showAirtimeMenu',
	        'click #Starcomm':   'showAirtimeMenu',
	        'click #Etisalaat':   'showAirtimeMenu',
	        'click #Airtel':   'showAirtimeMenu'
        },{
        	'touchstart #logout':   'logout',
	        'touchstart #home':   'home',
	        'touchstart #back':   'back',
	        'touchstart #MTN':   'showAirtimeMenu',
	        'touchstart #Glo':   'showAirtimeMenu',
	        'touchstart #Visafone':   'showAirtimeMenu',
	        'touchstart #Starcomm':   'showAirtimeMenu',
	        'touchstart #Etisalaat':   'showAirtimeMenu',
	        'touchstart #Airtel':   'showAirtimeMenu'
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
    	      var menu=new SubscriberMenu();
    	      app.changePage(menu);
    	    },
    	    
    	    showAirtimeMenu:function(e){
    	    	var clickeMenu = $(e.currentTarget);
    	    	var cmpid = clickeMenu.attr("name");
    	   	  var Menu = new AirtimesubMenu();
    	        app.changePage(Menu);     	
    	   }
    	   
    	});

window.AirtimesubMenu = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"Frommkuza",text:"From mkuza"})+
        							  addMenuItem({id:"Frombank",text:"From bank"}));
        $('#logout', this.el).remove();
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'click #Frommkuza':   'showmkuzaForm',
            'click #Frombank':   'showbankForm'
        },{
        	'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart #Frommkuza':   'showmkuzaForm',
            'touchstart #Frombank':   'showbankForm'
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
    	 var transferMenu = new AirtimeMenu();
         app.changePage(transferMenu);  
    },
    
    showmkuzaForm:function(){
    	 var mkuzaForm = new mkuzaMenu();
         app.changePage(mkuzaForm);  	
    },
    
    showbankForm:function(){
    	 var bankForm = new bankMenu();
         app.changePage(bankForm); 
    }
    
});
window.mkuzaMenu = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"Self",text:"For Self"})+
        							  addMenuItem({id:"Others",text:"For Others"}));
        $('#logout', this.el).remove();
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'click #Self':   'showSelfForm',
            'click #Others':   'showOtherForm'
        },{
        	'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart #Self':   'showSelfForm',
            'touchstart #Others':   'showOtherForm'
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
    	 var transferMenu = new AirtimeMenu();
         app.changePage(transferMenu);  
    },
    
    showSelfForm:function(){
    	 var selfForm = new AirtimePurchaseInquiryForm({isSelfBuy:true,visafone:true,cmpid:1});
         app.changePage(selfForm);  	
    },
    
    showOtherForm:function(){
    	 var otherForm = new AirtimePurchaseInquiryForm({isSelfBuy:false,visafone:true,cmpid:1});
         app.changePage(otherForm); 
    }
    
});

window.AirtimePurchaseInquiryForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
       
        this.config = config;
        
        var service = app.user.get('type')==USER_SUBSCRIBER?SERVICE_BUY:SERVICE_AGENT
       
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_AIRTIME_PURCHASE_INQUIRY})+
                                       addHiddenField({name:PARAMETER_SERVICE_NAME,value:service })+
                                       addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.user.get(PARAMETER_SOURCE_MDN)})+
                                       addHiddenField({name:PARAMETER_SRC_POCKET_CODE,value:POCKET_CODE_SVA})+
                                       addHiddenField({name:COMPANY_ID,value:config.cmpid}));
        if(config.isSelfBuy)  {
	        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_DEST_MDN,value:app.user.get(PARAMETER_SOURCE_MDN)}));	        
	    }else{
	    	  $('#baseForm', this.el).append(addField({label:"destMDN",name:PARAMETER_DEST_MDN,type:"text",required:true,validations:true}));
	    }
        $('#baseForm', this.el).append(addField({label:"Amount",name:PARAMETER_AMOUNT,type:"text",required:true,validations:true})+
                                addField({label:"PIN",name:PARAMETER_SOURCE_PIN,type:"password",required:true,validations:true})+
                                addButton({id:"Buy",value:"BuyAirtime",type:"submit",inline:true}));
        $('#logout', this.el).remove();
        this.events=getEvents({
        	 'click #logout':   'logout',
             'click #home':   'home',
             'click #back':   'back',
             'submit form':   'submit'
        },{
        	 'touchstart #logout':   'logout',
             'touchstart #home':   'home',
             'touchstart #back':   'back',
             'submit form':   'submit'
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
    	var menu=new AirtimesubMenu();
    	
    	app.changePage(menu);
    },
    submit:function(){
    	if($('#baseForm').valid()){
    		var self = this;
    		var data = $('#baseForm').serialize();
    		sendRequest(data,self);
    	}
         return false;  
     },
    onSuccess:function(response){       
 	   if(response.data.code==CODE_AIRTIME_INQUIRY_SUCCESS){
 	          app.response.set(response.data);
 	          var buyConfirmation = new AirtimePurchaseConfirmationForm(this.config);
 	          app.changePage(buyConfirmation);
 	        }else{
 	        	var error = new ResponseView(response);
 	        	app.changePage(error);
 	      }
    }    
    
   
});

window.AirtimePurchaseConfirmationForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_AIRTIME_PURCHASE})+
        							   addHiddenField({name:PARAMETER_SERVICE_NAME,value:app.request.get(PARAMETER_SERVICE_NAME)})+
        							   addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.request.get(PARAMETER_SOURCE_MDN)})+
        							   addHiddenField({name:PARAMETER_DEST_MDN,value:app.request.get(PARAMETER_DEST_MDN)})+
        							   addHiddenField({name:PARAMETER_SRC_POCKET_CODE,value:app.request.get(PARAMETER_SRC_POCKET_CODE)})+
        							   addHiddenField({name:COMPANY_ID,value:app.request.get(COMPANY_ID)})+
        							   addHiddenField({name:PARAMETER_TRANSFER_ID,value:app.response.get(PARAMETER_TRANSFER_ID)})+
        							   addHiddenField({name:PARAMETER_PARENTTXN_ID,value:app.response.get(PARAMETER_PARENTTXN_ID)})+
        							   addHiddenField({name:PARAMETER_AMOUNT,value:app.request.get(PARAMETER_AMOUNT)})); 
        if(config.isSelfBuy)
        	{
        	 $('#baseForm', this.el).append("<center><b>Buying N"+app.request.get(PARAMETER_AMOUNT)+ " for self  from mKuza </b>");
        	}
        else
        	{
        	 $('#baseForm', this.el).append("<center><b>Buying N"+app.request.get(PARAMETER_AMOUNT)+ "for"+app.request.get(PARAMETER_DEST_MDN)+ "from mKuza </b>");
        	}
        var debtamt=parseInt(app.response.get(PARAMETER_CREDIT_AMT))+ parseInt(app.response.get(PARAMETER_CHARGES));
        $('#baseForm', this.el).append("<center><b>Fees:\n"+app.response.get(PARAMETER_CHARGES)+
    			"<br><b> Amount to be debited:\n </b>"+debtamt+"</center>");
        $('#baseForm', this.el).append(addButton({id:"cancel",value:"Cancel",inline:true})+
        							   addButton({id:"confirm",value:"Confirm",type:"submit",inline:true}));
        $('#back', this.el).remove(); 
        this.events=getEvents({
        	 'click #logout':   'logout',
             'click #home':   'home',
             'click #cancel':   'cancel',
             'submit form':   'submit'
        },{
        	 'touchstart #logout':   'logout',
             'touchstart #home':   'home',
             'touchstart #cancel':   'cancel',
             'submit form':   'submit'
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
    
    cancel:function(e){
      	 var self = this;
           var data = $('#baseForm').serialize();
           data = data+"&confirmed="+CONSTANT_VALUE_FALSE;
           sendRequest(data,self); 
      },
      
      submit:function(){
          var self = this;
           var data = $('#baseForm').serialize();
           data = data+"&confirmed="+CONSTANT_VALUE_TRUE;
           sendRequest(data,self);
           return false;  
        },
      
       onSuccess:function(response){       
    	         var error = new ResponseView(response);
      	         app.changePage(error);
       }    
    
});


