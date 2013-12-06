package com.mfino.provision.tools.propertymanager;

public class LoginDetails {
	String UsernameForSystem;
	String PasswordForSystem;
	String SYSTEMNAME;
	String IPADDRESS;

	public String getSYSTEMNAME() {
		return this.SYSTEMNAME;
	}

	public void setSYSTEMNAME(String sYSTEMNAME) {
		this.SYSTEMNAME = sYSTEMNAME;
	}

	public String getIPADDRESS() {
		return this.IPADDRESS;
	}

	public void setIPADDRESS(String iPADDRESS) {
		this.IPADDRESS = iPADDRESS;
	}

	public String getUSERNAME() {
		return this.UsernameForSystem;
	}

	public void setUSERNAME(String uSERNAME) {
		this.UsernameForSystem = uSERNAME;
	}

	public String getPASSWORD() {
		return this.PasswordForSystem;
	}

	public void setPASSWORD(String pASSWORD) {
		this.PasswordForSystem = pASSWORD;
	}

}
