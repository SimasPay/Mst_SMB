window.TransferMenu = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"SelfTransfer",text:"Self"})+
                                      addMenuItem({id:"OthersTransfer",text:"Others"}));
        $('#logout', this.el).remove();
        $('#home', this.el).remove();
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'click #SelfTransfer':   'showSelfTransferMenu',
            'click #OthersTransfer':   'showOthersTransferMenu'
        },{
        	'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart #SelfTransfer':   'showSelfTransferMenu',
            'touchstart #OthersTransfer':   'showOthersTransferMenu'
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
    	app.menu();
    },
    
    showSelfTransferMenu:function(){
    	 var transferMenu = new SelfTransferMenu();
         app.changePage(transferMenu);     	
    },
    
    showOthersTransferMenu:function(){
    	 var transferMenu = new OthersTransferMenu();
         app.changePage(transferMenu);     	
    }
    
});
  
