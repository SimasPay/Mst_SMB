package com.mfino.util;

import java.util.HashMap;
import java.util.Map;

public class SubscriberSyncErrors {
		
	  //Before Correcting errors reported by Findbugs:
		//none of the fiels were final
	
	  //After Correcting the errors reported by Findbugs
		//all fields are final
	  public static final Map<Integer, String> errorCodesMap = new HashMap<Integer, String>();
	  public static final Integer Success=0;
	  public static final Integer Failure=1;
	  public static final Integer Invalid_Field_Count=2;
	  public static final Integer CardPan_Already_Exist=3;
	  public static final Integer MDN_Already_Exist=4;
	  public static final Integer Invalid_DateFormat=5;
	  public static final Integer Invalid_Field=6;
	  public static final Integer Mandatory_Field_Missing=7;
	  public static final Integer EmoneyPocketNotFound=8;
	  public static final Integer BankPocketNotFound=9;
	  public static final Integer PocketAlreadyUpgraded=10;
	  public static final Integer PocketTemplatesIncompatible=11;
	  public static final Integer InvalidLanguage=12;
	  public static final Integer InvalidAccountType=13;
	  public static final Integer InvalidBankAccountType=14;
	  public static final Integer Invalid_PocketTemplate=15;
	  public static final Integer Notregistered_MDN=16;
	  public static final Integer Invalid_CardPan = 17;
	  public static final Integer Invalid_GroupName = 18;	
	  
	  static {
		  	errorCodesMap.put(Success, "Success");
		  	errorCodesMap.put(Failure, "Failure");
		  	errorCodesMap.put(Invalid_Field_Count, "BadLine format or invalid field count");
	        errorCodesMap.put(CardPan_Already_Exist, "CardPan already exist in DB");
	        errorCodesMap.put(MDN_Already_Exist, "MDN already exist in DB");
	        errorCodesMap.put(Invalid_DateFormat, "Invalid DateFormat");
	        errorCodesMap.put(Invalid_Field, "Invalid field value ");
	        errorCodesMap.put(Mandatory_Field_Missing,"Mandatory field is missing");
	        errorCodesMap.put(EmoneyPocketNotFound,"Emoney pocket for subscriber not found");
	        errorCodesMap.put(BankPocketNotFound,"Bank pocket for subscriber not found");
	        errorCodesMap.put(PocketAlreadyUpgraded,"Pocket already upgraded");
	        errorCodesMap.put(PocketTemplatesIncompatible,"Existing Pocket template is incompatible with upgradable pockettemplate");
	        errorCodesMap.put(InvalidLanguage,"Invlid value for Language");
	        errorCodesMap.put(InvalidAccountType,"Invlid value for AccountType");
	        errorCodesMap.put(InvalidBankAccountType,"Invlid value for BankAccountType");
	        errorCodesMap.put(Invalid_PocketTemplate,"Invlid value for PocketTemplate");
	        errorCodesMap.put(Notregistered_MDN,"Not registeredMDN");
	        errorCodesMap.put(Invalid_CardPan, "Invalid CardPan");
	        errorCodesMap.put(Invalid_GroupName, "Group Name Does not exist");
	        }


}
