var mboptions = {
    el: null,
    controller: null,
    angle: 0,
    startAngle: 0,
    slices: Math.PI/3,
    originX: 160,
    originY: 160,
    values: {
        5: 'Trade',
        4: 'Alerts',
        3: 'Pay',
        2: 'Transfer',
        1: 'Deposit',
        0: 'Accounts'
    },

    handleEvent: function (e) {
        if (e.type == 'touchstart') {
            this.rotateStart(e);
        } else if (e.type == 'touchmove') {
            this.rotateMove(e);
        } else if (e.type == 'touchend') {
            this.rotateStop(e);
        }
        else{
            e.preventDefault();
        }
    },

    init: function() {
        this.el = document.getElementById('mboptions');
        this.controller = document.getElementById('display1');
        this.el.style.webkitTransitionDuration = '0';
		
        this.controller.addEventListener('touchstart', this, false);
        this.controller.addEventListener('gesturestart', this, false);
        this.controller.addEventListener('gesturechange', this, false);
    },
	
    rotateStart: function(e) {
        e.preventDefault();
	e.stopPropagation();

        this.el.style.webkitTransitionDuration = '0';
		
        var startX = e.touches[0].pageX - this.originX;
        var startY = e.touches[0].pageY - this.originY;
        this.startAngle = Math.atan2(startY, startX) - this.angle;
		
        this.controller.addEventListener('touchmove', this, false);
        this.controller.addEventListener('touchend', this, false);
    },
	
    rotateMove: function(e) {
        var dx = e.touches[0].pageX - this.originX;
        var dy = e.touches[0].pageY - this.originY;
        this.angle = Math.atan2(dy, dx) - this.startAngle;

        this.el.style.webkitTransform = 'rotateZ(' + this.angle + 'rad)';
    },
	
    rotateStop: function(e) {
        this.controller.removeEventListener('touchmove', this, false);
        this.controller.removeEventListener('touchend', this, false);
		
        if( this.angle%this.slices ) {
            this.angle = Math.round(this.angle/this.slices) * this.slices;
            this.el.style.webkitTransitionDuration = '150ms';
            this.el.style.webkitTransform = 'rotateZ(' + this.angle + 'rad)';
        }
    },
	
    getSelectedValue: function() {
        var selected = Math.floor(Math.abs(this.angle)/this.slices/6);
        if (this.angle < 0)
            selected = -selected;
		
        selected = Math.round(this.angle/this.slices) - selected*6;
        if (selected < 0)
            selected = 6 + selected;
			
        return [selected, this.values[selected]];
    }
};

function loaded() {
    window.scrollTo(0,0);
    var i, lis, theTransform, matrix;
	
    lis = document.getElementsByTagName('li');
    for(i=0; i<lis.length; i+=1) {
        theTransform = window.getComputedStyle(lis[i]).webkitTransform;
        matrix = new WebKitCSSMatrix(theTransform).translate(0, 100);
        lis[i].style.webkitTransform = matrix;
    }


    function detectSelected() {

        if(mboptions.getSelectedValue() == "5,Trade")
        {
            goToPage('depositdetails.html');
        }
        else if(mboptions.getSelectedValue() == "4,Alerts")
        {
            goToPage('transfers.html');
        }
        else if(mboptions.getSelectedValue() == "3,Pay")
        {
            goToPage('payments.html');
        }
        else if(mboptions.getSelectedValue() == "2,Transfer")
        {
            goToPage('alerts.html');
        }
        else if(mboptions.getSelectedValue() == "1,Deposit")
        {
            goToPage('trading.html');
        }
        else if(mboptions.getSelectedValue() == "0,Accounts")
        {
            goToPage('accounts.html');
        }
        else
        {
            alert("Select an Option & Click OK.");
        }
    }
	
    mboptions.init();
	
    document.getElementById('ok').addEventListener('click', detectSelected , false);
}
window.addEventListener("load", function() { 
    setTimeout(loaded, 100);
}, true);