window.IndexMenuPage = Backbone.View.extend({ 
  
      initialize:function () {
          this.template = _.template(tpl.get('basepage'));
          $(this.el).html(this.template());
          $('#content', this.el).append(addMenuItem({id:"Login",text:"Login"})+
                                        addMenuItem({id:"Activation",text:"Activation"}));
          $('#back', this.el).remove();
          $('#home', this.el).remove();
          $('#logout', this.el).remove();
          this.events=getEvents({
        	  'click #Login':   'showLoginForm',
              'click #Activation':   'showActivationForm'
          },{
        	  'touchstart #Login':   'showLoginForm',
              'touchstart #Activation':   'showActivationForm'
          });
         
      
      },
  
      render:function (eventName) {    	
        return this;
      },
      
     
      showLoginForm:function (e) {
       var loginForm = new LoginPage();
        app.changePage(loginForm);    	  
      },
      
      showActivationForm:function(e){
    	  var activationForm = new ActivationPage();
          app.changePage(activationForm);   
      }
  });


    

  