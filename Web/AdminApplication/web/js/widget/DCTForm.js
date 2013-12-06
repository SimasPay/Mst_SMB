/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DCTForm = function (config) {
    var localConfig = Ext.apply({}, config);
    
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle: 'padding:5px 5px 0',
        width:840,
        frame : true,
        layout : "anchor"
    });

    mFino.widget.DCTForm.superclass.constructor.call(this, localConfig);
};
 
Ext.extend(mFino.widget.DCTForm, Ext.Panel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 10;
        var config = this.initialConfig;

        this.dctDetails = new Ext.FormPanel({
            bodyStyle:'padding:5px',            
            frame:true,
            border:true,
            itemId:'dctdetails',
            layout:'column',
            height: 100,
            anchor: "100%",
            items:[
            {
                layout:"form",
                columnWidth:0.4,
                items:[
                {
                    fieldLabel: _('DCT Name'),
                    xtype: 'textfield',
                    allowBlank:false,
                    vtype:'numberchar',
                    emptyText:'Template 1',
                    maxLength : 255,
                    name: CmFinoFIX.message.JSDistributionChainTemplate.Entries.DistributionChainName._name,
                    listeners: {
                        change: function(field) {
                            this.findParentByType('dctForm').onDCT(field);
                        }
                    }
                },
   				{
					xtype : "remotedropdown",
					anchor : '80%',
					allowBlank: false,
					itemId : 'dctform.form.service',
					id : 'dctform.form.id.service',
					fieldLabel :"Service",
					addEmpty: false,
					emptyText:_('<select one..>'),
					RPCObject : CmFinoFIX.message.JSService,
					displayField: CmFinoFIX.message.JSService.Entries.ServiceName._name,
					valueField : CmFinoFIX.message.JSService.Entries.ID._name,
					name: CmFinoFIX.message.JSDistributionChainTemplate.Entries.ServiceID._name,
                    listeners: {
                        select: function(field) {
							//alert(this.findParentByType('dctForm').levelGrid);
							this.findParentByType('dctForm').levelGrid.setServiceId(field.getValue());
                        }                        
                    }
				}
                ]
            },
            {
                layout:"form",
                columnWidth:0.6,
                labelWidth:150,
                items:[
                {
                    fieldLabel: _('DCT Description'),
                    xtype: 'textarea',
                    width: 250,
                    allowBlank:false,
                    maxLength : 255,
                    emptyText:'Description of the DCT',
                    name: CmFinoFIX.message.JSDistributionChainTemplate.Entries.Description._name
                }
                ]
            }]
        });

        this.levelGrid = new mFino.widget.DCTLevelGrid(
        {
            itemId:'levelgrid',
            bodyStyle:'padding:5px',
            anchor: "100% -100",
            frame:true,
            border:true,
            dataUrl:config.dataUrl
        }
        );

        this.items = [this.dctDetails, this.levelGrid];

        mFino.widget.DCTForm.superclass.initComponent.call(this);
        markMandatoryFields(this.dctDetails.form);
        if(this.record){
            this.getForm().loadRecord(this.record);
        }
    },
    save : function(){
        var detailsForm = this.find('itemId', 'dctdetails')[0];
        if (detailsForm.getForm().isValid()) {
            detailsForm.getForm().updateRecord(this.record);
            if(this.store){
                if(this.record.phantom) {
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
    },

    saveLevels: function(){
        var grid = this.find('itemId', 'levelgrid')[0];
        grid.setTemplateID(this.record.get('ID'));
        grid.calculatePermissions();
        grid.store.save();
    },

    setRecord: function(record) {
        var detailsForm = this.find('itemId', 'dctdetails')[0];
        var levelGrid = this.find('itemId', 'levelgrid')[0];
        
        detailsForm.getForm().reset();
        levelGrid.reset();
        this.record = record;
        this.templateID = null;
        if(!this.record.phantom){
            this.templateID = record.data[CmFinoFIX.message.JSDistributionChainTemplate.Entries.ID._name];
            levelGrid.setTemplateID(this.templateID);
            levelGrid.reloadGrid();
        }
        detailsForm.getForm().loadRecord(record);
    },
    onDCT : function(field){
        var value = this.record.data[CmFinoFIX.message.JSDistributionChainTemplate.Entries.DistributionChainName._name];
        if(field.getValue() !== value){
            var msg = new CmFinoFIX.message.JSDistributionTemplateCheck();
            msg.m_pDCTName = field.getValue();
            var checkForExists=true;
            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
        }
    },
    validateLevels : function(){
    	 var grid = this.find('itemId', 'levelgrid')[0];
    	 return grid.validateLevels();
    },
	setMode: function(mode){
		this.mode = mode;
        if(this.mode == "add"){
        	Ext.getCmp("dctform.form.id.service").getStore().reload();
        	Ext.getCmp("dctform.form.id.service").setDisabled(false);
        }
        else{
        	Ext.getCmp("dctform.form.id.service").getStore().reload();
        	Ext.getCmp("dctform.form.id.service").setDisabled(true);
        }
	}
});

Ext.reg("dctForm", mFino.widget.DCTForm);
