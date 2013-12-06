package appselector.com.mfino.service;
 
import appselector.com.mfino.domain.RequestDetails;
/**
 * 
 * @author Shashank
 *
 */
public interface AppManagerService {
 
	public String getFilePath(RequestDetails requestDetails);
	public String getAppVersion(RequestDetails requestDetails);
	public String getAppType(String header);
    public RequestDetails updateRequestDetails(RequestDetails requestDetails);
 	public void saveNewAppDetails(RequestDetails requestDetails);
  }
