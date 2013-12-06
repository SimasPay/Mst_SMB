tpl = {         
   
	templates:{}, 
    
    loadTemplates:function (names, callback) {

        var that = this;

        var loadTemplate = function (index) {
            var name = names[index];
            console.log('Loading template: ' + name);
            $.get('tpl/' + name + '.html', function (data) {
                that.templates[name] = data;
                index++;
                if (index < names.length) {
                    loadTemplate(index);
                } else {
                    callback();
                }
            });
        };
        
       loadTemplate(0);
    },
    
    loadConfig:function (configFile,callback) {
          var that = this;
          console.log('Loading config: ' + name);
            $.get(configFile, function (data,status,response) {
            	 that.templates['Config'] = response.responseText;
            	 if(that.get('Config')){
            		 loadAppConfig();
          	        url=appConfig['url'];
                  }
                 labels = new Labels();
                 buttonLabels=new ButtonLabel();
                 callback();
            }).error(function() {
                alert("Config file is missing please reinstall application");
               /* labels = new Labels();
                buttonLabels=new ButtonLabel();
                callback();*/
            });        
    },

    // Get template by name from hash of preloaded templates
    get:function (name) {
        return this.templates[name];
    }

};

function loadAppConfig(){	
	    var config = tpl.get('Config');
        var appConfigPairs = config.split('\n');
        for(i=0;i<appConfigPairs.length;i++){
       	 if(appConfigPairs[i].indexOf(':')!=-1){
       	    var param = appConfigPairs[i].substring(0,appConfigPairs[i].indexOf(':'));
       	    var value =  appConfigPairs[i].substring(appConfigPairs[i].indexOf(':')+1,appConfigPairs[i].length);
       	    appConfig[param.trim()]=value.trim(); 
       	 	}
       	  } 
 }


//save request data before sending request
function setRequestData(data){
  app.request.clear();
  var paramValuePairs = data.split('&');
  for(i=0;i<paramValuePairs.length;i++){
    var paramValue = paramValuePairs[i].split('=');
    app.request.set(paramValue[0],paramValue[1]); 
  } 
}

//send request to webapi
function sendRequest(data,view)  {	
 disablePage();
 setRequestData(data);
 app.response.clear();
  $.ajax({
                    type: "POST",
                    dataType: 'jsonp',
                    jsonpCallback: "callback",
                    url: url,
                    cache: false,
                    data: data,
                    timeout:10000,
                    success: function(response){
                    	enablePage();
                    	if(response.data.code ==CODE_SESSION_EXPIRED){
                    		alert("Your session has expired please login again");
                    		app.user.clear();
                    		app.request.clear();
                    		app.main();
                    	}else{
                    		view.onSuccess(response);
                    	}
                    },
                    error: onError
   });
}

function sendLogoutRequest()  {	
	  disablePage();
	  app.response.clear();
	  var data = PARAMETER_SERVICE_NAME+"="+SERVICE_ACCOUNT+"&"+PARAMETER_TRANSACTIONNAME+"="+TRANSACTION_LOGOUT+"&"+
	             PARAMETER_SOURCE_MDN+"="+app.user.get(PARAMETER_SOURCE_MDN)+"&"+PARAMETER_AUTHENTICATION_STRING+"="+app.user.get(PARAMETER_AUTHENTICATION_STRING);
	  $.ajax({
	                    type: "POST",
	                    dataType: 'jsonp',
	                    jsonpCallback: "callback",
	                    url: url,
	                    cache: false,
	                    data: data,
	                    timeout:60000,
	                    success: onLogout,
	                    error: onLogout
	   });
}

function onLogout(){
	enablePage();
	app.user.clear();
	app.request.clear();
	app.main();
}

function onError(jqXHR, textStatus, errorThrown){
	enablePage();
	var response={};
	var viewResponse = true;
	  if (jqXHR.status === 0) {
		  viewResponse = false;
           alert('Not connected to Server.\n Verify Network connectivity.');
         } else if (jqXHR.status == 404) {
        	 response.message='Requested page not found.';
         } else if (jqXHR.status == 500) {
        	 response.message='Internal Server Error. Try again later';
         } else if (textStatus === 'parsererror') {
        	 response.message='Invalid response please contact customer care.';
         } else if (textStatus === 'timeout') {
        	 response.message='Your request timed out please try again';
         } else if (textStatus === 'abort') {
        	 response.message='Your request aborted please try again';
         } else {
        	 response.message='Unknow error \n' + jqXHR.responseText;
         }
	  if(viewResponse){
			var error = new ResponseView(response);
		      app.changePage(error);
	  }
}

function disablePage(){
	$.mobile.showPageLoadingMsg();
	$('input,button').attr('disabled', true);
	$('.ui-select').bind('click', false);

}

function enablePage(){
	$.mobile.hidePageLoadingMsg();
	$('input,button').attr('disabled', false);
	$('.ui-select').unbind('click', false);
}

//function to add form fields using config
function addField(config){
  var field ="<br>";// "<div data-role='fieldcontain'>";
    if(config.label){
    	var required = config.required?"<span class='requiredColor'>*</span>":"&nbsp;";
     field = field+"<label for="+config.name+" >"+required+"<b>"+labels.getLabel(config.name,config.label)+"</b></label>";
    }
    field=field+" <input  id='"+config.name+"' name='"+config.name+"'  type='"+config.type+"'";
   /* if(config.inline){
     field=field+" data-inline=true";
    }*/
    if(config.value) {
    field=field+" value='"+config.value+"'";
    }
    if(config.validations){
     field=field+" class="+validations[config.name];
    }
    if(config.type=='text'||config.type=='password'){
        field=field+" placeholder='"+getPlaceHolder(config.name)+"'";
       }
   field = field+" />"; //"></div>";
  return field; 
}

//function to add form hiddenfields using config
function addHiddenField(config){
  return "<input data-mini='true' id='"+config.name+"' name='"+config.name+"'  type='hidden' value='"+config.value+"'>";
}

//function to add buttons using config
function addButton(config){
  var type = "button";
  if(config.type){
  type = config.type;
  }
  var button = "<br><input value ='"+buttonLabels.getButtonLabel(config.id,config.value)+"' type='"+type+"'";
  if(config.id){
	  button=button+" id='"+config.id+"'" ; 
  }
  if(config.inline){
//    button=button+" data-inline=true";
  }
  if(config.icon){
    button=button+" data-icon="+config.icon;
  }
  return button+"  class='ui-btn-center' data-mini='true'/>" ; 
}

//function to add menu items using config
function addMenuItem(config){
	/*var menuItem = "<br><button id='"+config.id+"'";
	if(config.name){
		menuItem = menuItem+"name='"+config.name+"'";
	}
	menuItem= menuItem+"data-theme='b' data-mini='true'>" +buttonLabels.getButtonLabel(config.id,config.text);
  return menuItem;*/
	var button = "<br><input value ='"+buttonLabels.getButtonLabel(config.id,config.text)+"' type='button'";
	  if(config.id){
		  button=button+" id='"+config.id+"'" ; 
	  }
	  return button+" class='ui-btn-center' data-mini='true'/>" ; 
}

function isValidDate(date){
		var dayfield=date.substring(0,2);
		var monthfield=date.substring(3,5);	
		var yearfield=date.substring(6,10);
		var dayobj = new Date(yearfield, monthfield-1, dayfield);
		if ((dayobj.getMonth()+1!=monthfield)||(dayobj.getDate()!=dayfield)||(dayobj.getFullYear()!=yearfield)){
			return false;
		}else{
			return true;
		}
	
}

function getEvents(clickevents,touchevents){
	return isTouchDevice?touchevents:clickevents;
}

function getPlaceHolder(name){
	if(fieldMasks[name].indexOf('*')!=-1){
		return 'Enter Alphanumeric values only';
	}else if(fieldMasks[name].indexOf('9')!=-1){
		return 'Enter only digits';
	}else{
		return '';
	}	
}


$.validator.addMethod("noSpecialCharacters", function(value, element) {
	if (/^[A-Za-z0-9 ]{0,100}$/.test(value)) {
       return true
    } else {
       return false
    }
});