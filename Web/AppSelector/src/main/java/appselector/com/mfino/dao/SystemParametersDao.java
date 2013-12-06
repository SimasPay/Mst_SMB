package appselector.com.mfino.dao;

import appselector.com.mfino.domain.AppDetails;
/**
 * 
 * @author Shashank
 *
 */

public interface SystemParametersDao {

	public AppDetails getAppDetails(String ParameterName);
	
	public void saveAppDetails(AppDetails appDetails);
 
}
