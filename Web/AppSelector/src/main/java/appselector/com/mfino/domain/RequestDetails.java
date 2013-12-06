package appselector.com.mfino.domain;
/**
 * 
 * @author Shashank
 *
 */
public class RequestDetails {

	String apposname;
	String appname;
	String appversion;
	String appfilename;
	String SysParaPropertyName;
	 
	public String getSysParaPropertyName() {
		return SysParaPropertyName;
	}
	public void setSysParaPropertyName(String sysParaPropertyName) {
		SysParaPropertyName = sysParaPropertyName;
	}
	public String getAppfilename() {
		return appfilename;
	}
	public void setAppfilename(String appfilename) {
		this.appfilename = appfilename;
	}
	public String getApposname() {
		return apposname;
	}
	public void setApposname(String apposname) {
		this.apposname = apposname;
	}
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public String getAppversion() {
		return appversion;
	}
	public void setAppversion(String appversion) {
		this.appversion = appversion;
	}
	
}
