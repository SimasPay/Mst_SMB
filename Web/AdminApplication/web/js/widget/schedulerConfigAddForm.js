Ext.ns("mFino.widget");

mFino.widget.schedulerConfigAddForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });
    mFino.widget.schedulerConfigAddForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.schedulerConfigAddForm, Ext.form.FormPanel, {
    initComponent : function (){	
        this.labelWidth = 120;
        this.labelPad = 20;
        this.items = [{
            layout: 'form',
            autoHeight: true,
            items : [{
	                xtype : 'textfield',	                
	                fieldLabel: _("ScheduleTemplate Name"),
	                itemId : 'schedulerConfigAdd.form.schedulerconfigname',	                
	                anchor : '95%',
	                allowBlank: false,
	                blankText: _('Enter ScheduleTemplate Name'),
	                name : CmFinoFIX.message.JSScheduleTemplate.Entries.Name._name
            	},
            	{
	                xtype : 'enumdropdown',
	                fieldLabel: _("ScheduleTemplate ModeType"),
	                enumId: CmFinoFIX.TagID.ModeType,
	                itemId : 'schedulerConfigAdd.form.modetype',	                
	                anchor : '95%',
	                allowBlank: false,
	                blankText: _('Enter ScheduleTemplate ModeType'),
	                addEmpty: false,
	                name : CmFinoFIX.message.JSScheduleTemplate.Entries.ModeType._name,
					listeners: {
	                    select : function(field) {
	                       this.findParentByType('schedulerConfigAddForm').onModeTypeSelect(field);
	                    }
					}
            	}
            ]
        },
        {

            columnWidth: 1,
            layout: 'form',
            labelWidth : 30,
            labelPad : 3,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel: _("Note"),
                labelSeparator:':',
                renderer: function(value) {
                	return 'If the template is for Cutoff-Time, then name the template accordingly. Ex: Daily@23 for 2300HRS';
                }
            }
			]
        
        },
        
        
        {
            xtype:'tabpanel',
            itemId:'schedulerConfigAdd.form.tabpanel',
            frame:true,
            activeTab: 0,
            border : false,
            deferredRender:false,
            defaults:{
                layout:'column',
                columnWidth: 1,
                bodyStyle:'padding:10px'
            },
            items:[{
			       title: _('Minutes'),
			       frame:true,
			       autoHeight: true,
			       padding: '0 0 0 0',
			       itemId : 'schedulerConfigAdd.form.minutes',
	               layout: 'form',
	               labelWidth : 175,
	               labelPad : 5,
			       items:[{
		                xtype : 'numberfield',
		                width:20,
						maxLength: 2,
						maxValue: 59,
						minValue: 0,
		                fieldLabel: _("Every Minutes"),
		                itemId : 'schedulerConfigAdd.form.minutes.timervaluemm',
		                allowBlank: false,
		                blankText: _('Enter Minutes field value'),
						name : CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueMM._name,
		                anchor : '95%'
			       	}]
		    	},
				{
			       title: _('Hourly'),
			       frame:true,
			       autoHeight: true,
			       padding: '0 0 0 0',
			       itemId : 'schedulerConfigAdd.form.hourly',
	               layout: 'form',
	               labelWidth : 175,
	               labelPad : 5,
			       items:[{
		                xtype : 'numberfield',
		                width:20,
						maxLength: 2,
						maxValue: 23,
						minValue: 0,
		                fieldLabel: _("Every Hours"),
		                itemId : 'schedulerConfigAdd.form.hourly.timervaluehh',
		                allowBlank: false,
		                blankText: _('Enter Hours field value'),
						name : CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueHH._name,
		                anchor : '95%'               
			       	}]
				},
				{
				   title: _('Daily'),
			       frame:true,
			       autoHeight: true,
			       padding: '0 0 0 0',
			       itemId : 'schedulerConfigAdd.form.daily',
	               layout: 'form',
	               labelWidth : 100,
	               labelPad : 5,
			       items:[{
				    	     xtype : 'numberfield',
							 width:20,
							 maxLength: 2,
							 maxValue: 31,
							 minValue: 1,
			                 fieldLabel: _("Every Days"),
			                 itemId : 'schedulerConfigAdd.form.daily.dayofmonth',
			                 allowBlank: false,
				             blankText: _('Enter Days field value'),
							 name : CmFinoFIX.message.JSScheduleTemplate.Entries.DayOfMonth._name,
			                 anchor : '50%'
					    },
					    {
				            xtype : 'numberfield',
							width:20,
							maxLength: 2,
							maxValue: 23,
							minValue: 0,
	                        fieldLabel: _("At HH"),
	                        itemId : 'schedulerConfigAdd.form.daily.timervaluehh',
	                        allowBlank: false,
			                blankText: _('Enter HH field value'),
							name : CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueHH._name,
	                        anchor : '50%'
	                    },
	                    {
	                        xtype : 'numberfield',
							width:20,
							maxLength: 2,
							maxValue: 59,
							minValue: 0,
	                        fieldLabel: _("At MM"),
	                        itemId : 'schedulerConfigAdd.form.daily.timervaluemm',
	                        allowBlank: false,
			                blankText: _('Enter MM field value'),
							name : CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueMM._name,
	                        anchor : '50%'
	                    }
			            /*{
			            	layout: 'column',
			            	items: [{
			            		columnWidth: 0.3,
			            		items: [{
			            			layout: 'form',
			                        labelWidth : 40,
			                        items: [{
				                        xtype : 'textfield',
				                        fieldLabel: _("At HH"),
				                        itemId : 'schedulerConfigAdd.form.daily.timervaluehh',
				                        allowBlank: true,
				                        blankText : _('Channel Name is required'),
				                        anchor : '90%'
				                    }]
			            		}]
			            	},{
			            		columnWidth: 0.3,
			            		items: [{
			            			layout: 'form',
			                        labelWidth : 40,
			                        items: [{
				                        xtype : 'textfield',
				                        fieldLabel: _("At MM"),
				                        itemId : 'schedulerConfigAdd.form.daily.timervaluemm',
				                        allowBlank: true,
				                        blankText : _('Channel Name is required'),
				                        anchor : '90%'
				                    }]
			            		}]
			            	}]
			            }*/]
				},
				{
				   title: _('Weekly'),
			       frame:true,
			       autoHeight: true,
			       padding: '0 0 0 0',
			       itemId : 'schedulerConfigAdd.form.weekly',
	               layout: 'form',
	               labelWidth : 50,
	               labelPad : 5,
	               items : [{
	            	   xtype: 'checkboxgroup',
	            	   fieldLabel: _("Day"),
	            	   itemId : 'schedulerConfigAdd.form.weekly.dayOfWeek',
	            	   columns: 2,	            	   
	                   vertical: true,
	                   allowBlank: false,	                  
	                   blankText: _('Select atleast one item in the group'),
	                   items: [{	                       
	                	   boxLabel: 'Monday',
	                       inputValue: 'MON'           			
	                   	},{
	                   		boxLabel: 'Tuesday',
		                    inputValue: 'TUE'           			
	                   	},{
	                   		boxLabel: 'Wednesday',
		                    inputValue: 'WED'           			
	                   	},{
	                   		boxLabel: 'Thursday',
		                    inputValue: 'THU'           			
	                   	},{
	                   		boxLabel: 'Friday',
		                    inputValue: 'FRI'           			
	                   	},{
	                   		boxLabel: 'Saturday',
		                    inputValue: 'SAT'           			
	                   	},{
	                   		boxLabel: 'Sunday',
		                    inputValue: 'SUN'           			
	                   	}]
	            	   /*
	                   layout : 'column',
	                   autoHeight: true,
	                   columns: 2,
	                   style : {
	                       margin: '5px'
	                   },
	                   items: [{
	                       columnWidth: 0.5,
	                       xtype : 'checkbox',
	                       itemId : 'schedulerConfigAdd.form.weekly.monday',
	                       boxLabel: 'Monday'           			
	                   	},{
	                       columnWidth: 0.5,
	                       xtype : 'checkbox',
	                       itemId : 'schedulerConfigAdd.form.weekly.tuesday',
	                       boxLabel: 'Tuesday'           			
	                   	},{
	                       columnWidth: 0.5,
	                       xtype : 'checkbox',
	                       itemId : 'schedulerConfigAdd.form.weekly.wednesday',
	                       boxLabel: 'Wednesday'           			
	                   	},{
	                       columnWidth: 0.5,
	                       xtype : 'checkbox',
	                       itemId : 'schedulerConfigAdd.form.weekly.thursday',
	                       boxLabel: 'Thursday'           			
	                   	},{
	                       columnWidth: 0.5,
	                       xtype : 'checkbox',
	                       itemId : 'schedulerConfigAdd.form.weekly.friday',
	                       boxLabel: 'Friday'
	                   	},{
	                       columnWidth: 0.5,
	                       xtype : 'checkbox',
	                       itemId : 'schedulerConfigAdd.form.weekly.saturday',
	                       boxLabel: 'Saturday'           			
	           			},{
	                       columnWidth: 0.5,
	                       xtype : 'checkbox',
	                       itemId : 'schedulerConfigAdd.form.weekly.sunday',
	                       boxLabel: 'Sunday'           			
	           			}]
	               	*/},
	               {
	                   xtype : 'numberfield',
					   width:20,
					   maxLength: 2,
					   maxValue: 23,
					   minValue: 0,
	                   fieldLabel: _("At HH"),
	                   itemId : 'schedulerConfigAdd.form.weekly.timervaluehh',
	                   allowBlank: false,
		               blankText: _('Enter HH value'),
					   name : CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueHH._name,
	                   anchor : '50%'
	               },
	   			   {
	                   xtype : 'numberfield',
					   width:20,
					   maxLength: 2,
					   maxValue: 59,
					   minValue: 0,
	                   fieldLabel: _("At MM"),
	                   itemId : 'schedulerConfigAdd.form.weekly.timervaluemm',
	                   allowBlank: false,
		               blankText: _('Enter MM value'),
					   name : CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueMM._name,
	                   anchor : '50%'
	               }]
			},
			{
			   title: _('Monthly'),
		       frame:true,
		       autoHeight: true,
		       padding: '0 0 0 0',
		       itemId : 'schedulerConfigAdd.form.monthly',
               layout: 'form',
               labelWidth : 175,
               labelPad : 5,
		       items:[{
			    	    xtype : 'numberfield',
					    width:20,
					    maxLength: 2,
					    maxValue: 31,
					    minValue: 1,
		                fieldLabel: _("Day Of Month"),
		                itemId : 'schedulerConfigAdd.form.monthly.dayofmonth',
		                allowBlank: false,
			            blankText: _('Enter Day value'),
               			name : CmFinoFIX.message.JSScheduleTemplate.Entries.DayOfMonth._name,
		                anchor : '95%'
		       		},{
		       			xtype : 'numberfield',
						width:20,
						maxLength: 2,
						maxValue: 12,
						minValue: 1,
		                fieldLabel: _("Month"),
		                itemId : 'schedulerConfigAdd.form.monthly.month',		               
		                anchor : '95%',
		                allowBlank: false,
			            blankText: _('Enter Month value'),
		                name : CmFinoFIX.message.JSScheduleTemplate.Entries.Month._name
		       		},{
		                xtype : 'numberfield',
						width:20,
						maxLength: 2,
						maxValue: 23,
						minValue: 0,
		                fieldLabel: _("AT HH"),
		                itemId : 'schedulerConfigAdd.form.monthly.timervaluehh',
		                allowBlank: false,
			            blankText: _('Enter HH value'),
						name : CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueHH._name,
		                anchor : '95%'
		       		},{
		                xtype : 'numberfield',
						width:20,
						maxLength: 2,
						maxValue: 59,
						minValue: 0,
		                fieldLabel: _("AT MM"),
		                itemId : 'schedulerConfigAdd.form.monthly.timervaluemm',
		                allowBlank: false,
			            blankText: _('Enter MM value'),
               			name : CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueMM._name,
		                anchor : '95%'
		       		}]
				},
				{
				   title: _('Advanced'),
			       frame:true,
			       autoHeight: true,
			       padding: '0 0 0 0',
			       itemId : 'schedulerConfigAdd.form.advanced',
	               layout: 'form',
	               labelWidth : 175,
	               labelPad : 5,
			       items:[{
			                xtype : 'textfield',
			                fieldLabel: _("Cron Expression"),
			                itemId : 'schedulerConfigAdd.form.advanced.cron',			                
			                anchor : '95%',
			                allowBlank: false,
				            blankText: _('Enter Cron expression'),
			                name : CmFinoFIX.message.JSScheduleTemplate.Entries.Cron._name
			            }]
				}
			]
        }];

        mFino.widget.schedulerConfigAddForm.superclass.initComponent.call(this);        
        markMandatoryFields(this.form);        
    },
    
    disableAllTabItems: function(){
    	var tabPanel = this.items.get('schedulerConfigAdd.form.tabpanel');
    	tabPanel.items.each(function(tab){
    		tab.setDisabled(true);
    		tab.items.each(function(item){
    			item.disable();
    		})
    	});
    },
    
    enableSingleTabItems: function(){
    	var modeType = this.form.items.get("schedulerConfigAdd.form.modetype").getRawValue();
    	if(modeType){
			 modeType = modeType.toLowerCase();
		} else return;
		var enableTabName = 'schedulerConfigAdd.form.' + modeType;
    	var tabPanel = this.items.get('schedulerConfigAdd.form.tabpanel');
    	var enabledTab = tabPanel.items.get(enableTabName);
    	enabledTab.setDisabled(false);
    	enabledTab.items.each(function(item){
    		item.enable();
    	});    			 
    	tabPanel.setActiveTab(enabledTab);
    },
    
    save : function(){
        if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            this.record.beginEdit();
            var modeType = this.form.items.get("schedulerConfigAdd.form.modetype").getValue();
			if(modeType == CmFinoFIX.ModeType.Minutes){
				this.record.set(CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueMM._name, this.form.items.get("schedulerConfigAdd.form.minutes.timervaluemm").getRawValue());
			} else if(modeType == CmFinoFIX.ModeType.Hourly){
				this.record.set(CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueHH._name, this.form.items.get("schedulerConfigAdd.form.hourly.timervaluehh").getRawValue());
			} else if(modeType == CmFinoFIX.ModeType.Daily){
				this.record.set(CmFinoFIX.message.JSScheduleTemplate.Entries.DayOfMonth._name, this.form.items.get("schedulerConfigAdd.form.daily.dayofmonth").getRawValue());			
				this.record.set(CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueHH._name, this.form.items.get("schedulerConfigAdd.form.daily.timervaluehh").getRawValue());
				this.record.set(CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueMM._name, this.form.items.get("schedulerConfigAdd.form.daily.timervaluemm").getRawValue());
			} else if(modeType == CmFinoFIX.ModeType.Weekly){
				this.record.set(CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueHH._name, this.form.items.get("schedulerConfigAdd.form.weekly.timervaluehh").getRawValue());
				this.record.set(CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueMM._name, this.form.items.get("schedulerConfigAdd.form.weekly.timervaluemm").getRawValue());
				var dayOfWeek = "";
				var dayCheckBoxGroup = this.form.items.get("schedulerConfigAdd.form.weekly.dayOfWeek");
				Ext.each(dayCheckBoxGroup.getValue(),function(checkbox){
					dayOfWeek = dayOfWeek + checkbox.inputValue + ",";
				});
				/*	
				if(this.form.items.get("schedulerConfigAdd.form.weekly.monday").checked){
					dayOfWeek = dayOfWeek + "MON,";								
				}
				if(this.form.items.get("schedulerConfigAdd.form.weekly.tuesday").checked){
					dayOfWeek = dayOfWeek + "TUE,";					
				}
				if(this.form.items.get("schedulerConfigAdd.form.weekly.wednesday").checked){
					dayOfWeek = dayOfWeek + "WED,";	
				}
				if(this.form.items.get("schedulerConfigAdd.form.weekly.thursday").checked){
					dayOfWeek = dayOfWeek + "THU,";	
				}
				if(this.form.items.get("schedulerConfigAdd.form.weekly.friday").checked){
					dayOfWeek = dayOfWeek + "FRI,";
				}
				if(this.form.items.get("schedulerConfigAdd.form.weekly.saturday").checked){
					dayOfWeek = dayOfWeek + "SAT,";
				}
				if(this.form.items.get("schedulerConfigAdd.form.weekly.sunday").checked){
					dayOfWeek = dayOfWeek + "SUN";						
				}
				*/
				this.record.set(CmFinoFIX.message.JSScheduleTemplate.Entries.DayOfWeek._name,dayOfWeek);
			} else if(modeType == CmFinoFIX.ModeType.Monthly){
				this.record.set(CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueHH._name, this.form.items.get("schedulerConfigAdd.form.monthly.timervaluehh").getRawValue());
				this.record.set(CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueMM._name, this.form.items.get("schedulerConfigAdd.form.monthly.timervaluemm").getRawValue());
				this.record.set(CmFinoFIX.message.JSScheduleTemplate.Entries.DayOfMonth._name, this.form.items.get("schedulerConfigAdd.form.monthly.dayofmonth").getRawValue());
			}
            this.record.endEdit();			
            if(this.store){
                if(this.record.phantom && !(this.record.store)){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
    },
    
    validate : function() {
    	if(this.getForm().isValid()) {
    		return true;
    	} else {
    		return false;
    	}
    },
    
    setRecord : function(record){    	
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);
		var modeType = this.record.get(CmFinoFIX.message.JSScheduleTemplate.Entries.ModeType._name);
		var timerValueMM = this.record.get(CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueMM._name);
		var timerValueHH = this.record.get(CmFinoFIX.message.JSScheduleTemplate.Entries.TimerValueHH._name);
		var dayOfMonth = this.record.get(CmFinoFIX.message.JSScheduleTemplate.Entries.DayOfMonth._name);
		var month = this.record.get(CmFinoFIX.message.JSScheduleTemplate.Entries.Month._name);
		if(modeType == CmFinoFIX.ModeType.Minutes) {
			this.form.items.get("schedulerConfigAdd.form.minutes.timervaluemm").setValue(timerValueMM);
		} else if(modeType == CmFinoFIX.ModeType.Hourly) {
			this.form.items.get("schedulerConfigAdd.form.hourly.timervaluehh").setValue(timerValueHH);
		} else if(modeType == CmFinoFIX.ModeType.Daily) {
			this.form.items.get("schedulerConfigAdd.form.daily.dayofmonth").setValue(dayOfMonth);
			this.form.items.get("schedulerConfigAdd.form.daily.timervaluehh").setValue(timerValueHH);
			this.form.items.get("schedulerConfigAdd.form.daily.timervaluemm").setValue(timerValueMM);
		} else if(modeType == CmFinoFIX.ModeType.Weekly) {			
			this.form.items.get("schedulerConfigAdd.form.weekly.timervaluehh").setValue(timerValueHH);
			this.form.items.get("schedulerConfigAdd.form.weekly.timervaluemm").setValue(timerValueMM);
			var dayCheckBoxGroup = this.form.items.get("schedulerConfigAdd.form.weekly.dayOfWeek");
			var dayOfWeek = this.record.get(CmFinoFIX.message.JSScheduleTemplate.Entries.DayOfWeek._name);
			Ext.each(dayCheckBoxGroup.items.items, function(checkbox){
				if(dayOfWeek.indexOf(checkbox.inputValue) != -1){
					checkbox.setValue(1);
				}
			});			
		} else if(modeType == CmFinoFIX.ModeType.Monthly) {		
			this.form.items.get("schedulerConfigAdd.form.monthly.dayofmonth").setValue(dayOfMonth);
			this.form.items.get("schedulerConfigAdd.form.monthly.month").setValue(month);
			this.form.items.get("schedulerConfigAdd.form.monthly.timervaluehh").setValue(timerValueHH);
			this.form.items.get("schedulerConfigAdd.form.monthly.timervaluemm").setValue(timerValueMM);
		}
        this.getForm().clearInvalid();
    },
    setStore : function(store){
        if(this.store){
            this.store.un("update", this.onStoreUpdate, this);
        }
        this.store = store;
        this.store.on("update", this.onStoreUpdate, this);
    },
    onStoreUpdate: function(){
        this.setRecord(this.record);
    },
	onModeTypeSelect: function(field){
		 this.disableAllTabItems();			 		 
		 this.enableSingleTabItems();		 
	}
});

Ext.reg("schedulerConfigAddForm", mFino.widget.schedulerConfigAddForm);