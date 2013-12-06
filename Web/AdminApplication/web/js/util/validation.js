/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.util");


//Hybrid charge validations
/*
* Validating against the below exprs:
* 
	d +/- amount*d%
	(amount +/- d) +/- (amount +/- d1)*d2%
	(amount +/- d) +/- amount*d1%
	amount +/- d
	d
	amount*d%
	(amount +/- d)*d1%
	(amount +/- d)*d1% +/- d2
	(amount +/- d)*d1% +/- (amount +/- d2)
	amount*d% +/- d1
	amount*d% +/- (amount +/- d1)
*/
Ext.form.VTypes['chargeDefValidationMask'] = /[\damount\*\+\-\(\)%\.]/;
Ext.form.VTypes['chargeDefValidationText'] = 'Charge should be of pattern :<br/>' +
											'* d +/- amount*d% <br/>' +
											'* (amount +/- d) +/- (amount +/- d1)*d2%<br/>' +
											'* (amount +/- d) +/- amount*d1%<br/>' +
											'* amount +/- d<br/>' +
											'* d<br/>' +
											'* amount*d%<br/>' +
											'* (amount +/- d)*d1%<br/>' +
											'* (amount +/- d)*d1% +/- d2<br/>' +
											'* (amount +/- d)*d1% +/- (amount +/- d2)<br/>' +
											'* amount*d% +/- d1<br/>' +
											'* amount*d% +/- (amount +/- d1)<br/>';
Ext.form.VTypes['chargeDefValidation'] = function(v, field){	
	var charge = field.getValue();	
	var regExps = [/^(\d+(\.\d+)?)[\+,\-]amount\*(\d+(\.\d+)?)%$/,
	               /^\(amount[\+,\-](\d+(\.\d+)?)\)[\+,\-]\(amount[\+,\-](\d+(\.\d+)?)\)\*(\d+(\.\d+)?)%$/,
	               /^\(amount[\+,\-](\d+(\.\d+)?)\)[\+,\-]amount\*(\d+(\.\d+)?)%$/,
	               /^amount[\+,\-](\d+(\.\d+)?)$/,
	               /^(\d+(\.\d+)?)$/,
	               /^amount\*(\d+(\.\d+)?)%$/,
	               /^\(amount[\+,\-](\d+(\.\d+)?)\)\*(\d+(\.\d+)?)%$/,
	               /^\(amount[\+,\-](\d+(\.\d+)?)\)\*(\d+(\.\d+)?)%[\+,\-](\d+(\.\d+)?)$/,
	               /^\(amount[\+,\-](\d+(\.\d+)?)\)\*(\d+(\.\d+)?)%[\+,\-]\(amount[\+,\-](\d+(\.\d+)?)\)$/,
	               /^amount\*(\d+(\.\d+)?)%[\+,\-](\d+(\.\d+)?)$/,
	               /^amount\*(\d+(\.\d+)?)%[\+,\-]\(amount[\+,\-](\d+(\.\d+)?)\)$/];
	var isValid = false;
	for(var i = 0; i < regExps.length; i++) {
		if(regExps[i].test(charge)) {
			isValid = true;
			break;
		}
	}	
	return isValid;    
};

//Charge share validations
Ext.form.VTypes['shareValidationMask'] = /[\d\.%]/;
Ext.form.VTypes['shareValidation'] = function(v, field){	
	var share = field.getValue();	
	var regExp = /^(\d+(\.\d+)?)%$/;
	if(!regExp.test(share)) {
		this.shareValidationText = "Enter share in %";
		return false;
	}
	share = share.replace('%','');
	share = Number(share);
	if(share > 100) {
		this.shareValidationText = "Share should not be greater than 100%";
		return false;
	}
	return true;    
};
