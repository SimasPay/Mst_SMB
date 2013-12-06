var validations={};
validations[PARAMETER_SOURCE_MDN]="required  minlength='10' maxlength='13' digits=true";
validations[PARAMETER_DEST_MDN]="required  minlength='10' maxlength='13' digits=true";
validations[PARAMETER_SUB_MDN]="required  minlength='10' maxlength='13' digits=true";
validations[PARAMETER_AUTHENTICATION_STRING]="required minlength='4' maxlength='4' digits=true";
validations[PARAMETER_ACTIVATION_NEWPIN]="required minlength='4' maxlength='4' digits=true";
validations[PARAMETER_ACTIVATION_CONFIRMPIN]="required minlength='4' maxlength='4' digits=true  equalTo='#"+PARAMETER_ACTIVATION_NEWPIN+"'";
validations[PARAMETER_OLD_PIN]="required minlength='4' maxlength='4' digits=true";
validations[PARAMETER_OTP]="required minlength='4' maxlength='4' digits=true";
validations[PARAMETER_NEW_PIN]="required minlength='4' maxlength='4' digits=true";
validations[PARAMETER_CONFIRM_PIN]="required minlength='4' maxlength='4' digits=true  equalTo='#"+PARAMETER_NEW_PIN+"'";
validations[PARAMETER_SOURCE_PIN]="required minlength='4' maxlength='4' digits=true";
validations[PARAMETER_DESTINATION_BANK_ACCOUNT_NO]="required minlength='10' maxlength='10' digits=true ";
validations[PARAMETER_DEST_ACCOUNT_NO]="required minlength='10' maxlength='10' digits=true ";
validations[PARAMETER_AGENT_CODE]="required ";
validations[PARAMETER_BILLER_CODE]="required ";
validations[PARAMETER_PARTNER_CODE]="required ";
validations[PARAMETER_AMOUNT]="required digits=true";
validations[PARAMETER_BILL_NO]="required minlength='10' maxlength='10'";
validations[PARAMETER_SUB_FIRSTNAME]="required ";
validations[PARAMETER_SUB_LASTNAME]="required ";
validations[PARAMETER_DOB]="required date=true ";
validations[PARAMETER_APPLICATION_ID]="required maxlength='13' digits=true ";
validations[PARAMETER_SECRETE_CODE]="required minlength='4' maxlength='4' digits=true ";
validations[PARAMETER_DEST_BANK_CODE]="required  digits=true ";
validations[PARAMETER_TRANSFER_ID]="required  digits=true ";

var fieldMasks={};
fieldMasks[PARAMETER_SOURCE_MDN]="9?999999999999";
fieldMasks[PARAMETER_DEST_MDN]="9?999999999999";
fieldMasks[PARAMETER_SUB_MDN]="9?999999999999";
fieldMasks[PARAMETER_AUTHENTICATION_STRING]="9?999";
fieldMasks[PARAMETER_ACTIVATION_NEWPIN]="9?999";
fieldMasks[PARAMETER_ACTIVATION_CONFIRMPIN]="9?999";
fieldMasks[PARAMETER_OLD_PIN]="9?999";
fieldMasks[PARAMETER_NEW_PIN]="9?999";
fieldMasks[PARAMETER_CONFIRM_PIN]="9?999";
fieldMasks[PARAMETER_SOURCE_PIN]="9?999";
fieldMasks[PARAMETER_DESTINATION_BANK_ACCOUNT_NO]="9?999999999";
fieldMasks[PARAMETER_DEST_ACCOUNT_NO]="9?999999999";
fieldMasks[PARAMETER_AGENT_CODE]='*?************';
fieldMasks[PARAMETER_BILLER_CODE]='*?************';
fieldMasks[PARAMETER_PARTNER_CODE]='*?************';
fieldMasks[PARAMETER_AMOUNT]="9?9999999";
fieldMasks[PARAMETER_BILL_NO]="9?999999999";
fieldMasks[PARAMETER_SUB_FIRSTNAME]='*?************';
fieldMasks[PARAMETER_SUB_LASTNAME]='*?************';
fieldMasks[PARAMETER_DOB]="99/99/9999";
fieldMasks[PARAMETER_APPLICATION_ID]="9?999999999999";
fieldMasks[PARAMETER_SECRETE_CODE]="9?999";
fieldMasks[PARAMETER_DEST_BANK_CODE]="9?999";
fieldMasks[PARAMETER_TRANSFER_ID]="9?99999999999";
fieldMasks[PARAMETER_OTP]="9?999";







