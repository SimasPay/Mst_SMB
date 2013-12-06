Ext.define('Mfino.ux.form.DateRangeHiddenField',{
	extend: 'Ext.form.TextField',
	hidden: true,
	setValue: function(value){
        if (Ext.isDate(value)) {
          //  value = value.format('Ymd-00:00:00:00');
              value = Ext.Date.format(value,'Ymd-H:i:s:u');
        }		
        Mfino.ux.form.DateRangeHiddenField.superclass.setValue.call(this, value);
    }
});