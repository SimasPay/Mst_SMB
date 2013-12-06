Ext.define('Ext.chart.theme.CustomTheme', {

    extend: 'Ext.chart.theme.Base',

    requires: [
        'Ext.chart.theme.Base'              
    ],
    
    constructor: function(config) {
        Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({        	
        	axisTitleLeft: {
                font: '15px Arial',
                fill: '#444',
                rotate: {
                    x:0, y:0,
                    degrees: 270
                }
            },
            axisLabelBottom: {
                fill: '#444',
                font: '10px Arial, Helvetica, sans-serif',
                spacing: 2,
                padding: 5,
                renderer: function(v) { return v; },
                rotate: {
                    degrees: 315
                }
            },
            seriesLabel: {
                font: '12px Arial',
                fill: '#B4CDCD'
            },
            colors: ['#f3b641', '#7fa6ae', '#b3669e', '#c93331', '#8B5742', '#d0e5af', '#71a1db']            
        }, config));
    }
});
