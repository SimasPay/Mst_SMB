package com.mfino.stk;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.service.SubscriberService;

public class BrandValidator{

	private static Logger	   log	= LoggerFactory.getLogger(BrandValidator.class);
	
	private SubscriberService subscriberService;

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	private RequestPermissions	permissions;

//	private String	           company;
//	private String	           mdn;

	public BrandValidator(RequestPermissions rp) {
		this.permissions = rp;
	}

	public boolean validate(String company,String mdn) {

		log.info("validating permissibility of mdn="+mdn+" of company="+company+" for airtime purchase");
		mdn = subscriberService.deNormalizeMDN(mdn);

		log.info("denormalized mdn=" + mdn);

		log.info("getting the permission for the mobile number -->" + mdn);
		return validate(company,mdn,0, 4);

	}

	private boolean validate(String company,String mdn,int s, int e) {

		if(e<=2)
			return false;
		
		String brand = mdn.substring(s, e);

		boolean result = permissions.getPermission(company, brand);

		if (result) 
			return true;
		else 
			return validate(company,mdn,s, e - 1);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		List<String> per = new ArrayList<String>();
		per.add("1,07025,true");
		per.add("1,0704,true");
		per.add("1,07026,true");
		
		per.add("2,809,true");
		per.add("2,817,true");
		per.add("2,818,true");
		
		per.add("3,0805,true");
		per.add("3,0807,true");
		per.add("3,0815,true");
		
		per.add("4,0803,true");
		per.add("4,0806,true");
		per.add("4,0703,true");
		per.add("4,0706,true");
		per.add("4,0810,true");
		per.add("4,0813,true");
		
		per.add("5,07028,true");
		per.add("5,07029,true");
		per.add("5,018,true");
		
		per.add("6,0802,true");
		per.add("6,0808,true");
		per.add("6,0708,true");
		per.add("6,0812,true");
		
		RequestPermissions rp = new RequestPermissions(per);
		
		BrandValidator val = new BrandValidator(rp);
		System.out.println( val.validate("5"	, "7020812706876567"));
		
	}

	public RequestPermissions getPermissions() {
		return permissions;
	}

	public void setPermissions(RequestPermissions permissions) {
		this.permissions = permissions;
	}

}
