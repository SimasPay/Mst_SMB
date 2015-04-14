Ext.define('Mfino.view.dashboard.HeaderCombo', {
    extend: 'Ext.panel.Panel',    
    
    alias: 'widget.headerCombo', 
    
    initComponent: function() {
    	Ext.apply(this, {
            layout: 'fit', 
            align: 'stretch',
            html: '<div class="custom-header">'+
            		'<div id="profile-logo"></div>'+
            		'<div id="header-combo"></div>'+
            		'<div class="header-buttons">'+
            			'<div id="settings" style="float:left;display:inline;"></div>'+
            			'<div id="logout"><a href="j_spring_security_logout"></a></div>'+
            		'</div>'+
            	  '</div>'
        });
        this.callParent(arguments);
    },
    afterRender: function(){
    	this.callParent(arguments);
    	var headerComboRef = Ext.DomQuery.selectNode('div#header-combo', this.el.dom);
    	var settingsRef = Ext.DomQuery.selectNode('div#settings', this.el.dom);
    	var logoutRef = Ext.get(Ext.DomQuery.selectNode('div#logout', this.el.dom));
    	logoutRef.on('click', function() {
    		Ext.Ajax.request({					
				url: 'j_spring_security_logout',
				method: 'post'			
			});
    	});
    	
    	var comboList = Ext.create('Ext.data.Store', {
            fields: ['display', 'value'],
            data : [
                {"display":"Last 15 min", "value":"1"},
                {"display":"Last 1 hr", "value":"2"},
                {"display":"Last 5 hr", "value":"3"},
                {"display":"Last 24 hr", "value":"4"},
                {"display":"Last 1 week", "value":"5"},
                {"display":"Last 1 month", "value":"6"}
            ]
        });
    	
    	var headerCombo = Ext.create('Ext.form.ComboBox', {
    		id: 'headerCombo',
            fieldLabel: 'Monitoring Period',            
            store: comboList,
            renderTo: headerComboRef,
            queryMode: 'local',
            displayField: 'display',
            valueField: 'value',
            value: '2'
        });
    	
    	var settingsButton = Ext.create('Ext.Button', {
        	id: 'settings',
        	renderTo: settingsRef,
        	text: 'Settings',            	
        	width: 130,
        	height: 30
        });
    	
    	/*var logoutButton = Ext.create('Ext.Button', {
        	id: 'logout',
        	renderTo: logoutRef,
        	text: '',            	
        	width: 130,
        	height: 30
        });*/
    }
});