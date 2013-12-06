package appselector.com.mfino.service.Impl;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import appselector.com.mfino.dao.SystemParametersDaoImpl;
import appselector.com.mfino.domain.AppDetails;
import appselector.com.mfino.domain.RequestDetails;
import appselector.com.mfino.service.AppManagerService;
import appselector.com.mfino.util.AppSelectorConstants;
import appselector.com.mfino.util.MessageResolver;
/**
 * 
 * @author Shashank
 *
 */

@Service("AppManagerServiceImpl")
public class AppManagerServiceImpl implements AppManagerService{

	@Autowired
	SystemParametersDaoImpl systemParametersDao;
	
	@Autowired
	@Qualifier("MessageResolver")
	private MessageResolver messageResolver;
	
	private final Log log = LogFactory.getLog (AppManagerServiceImpl.class);

	@Override
 	public String getFilePath(RequestDetails requestDetails) {
		
		String fileSep= File.separator;
		String homeDirectory = messageResolver.getMessage(AppSelectorConstants.APP_LOCATION);
		
		 String fileRelativePath = requestDetails.getAppname()+fileSep
								 +requestDetails.getApposname()+fileSep
							     +requestDetails.getAppversion()+fileSep
							     +requestDetails.getAppfilename();
 		log.debug(requestDetails);
		return homeDirectory+fileRelativePath ;
	}
	
	//app version is updated by doing query since we know parameter value !! 
	public String getAppVersion(RequestDetails requestDetails){
		
 		AppDetails appDetails = systemParametersDao.getAppDetails(requestDetails.getSysParaPropertyName());
 		log.debug("Name:"+appDetails.getParameterName()+" value:"+appDetails.getParameterValue());
 		return appDetails.getParameterValue();
		
	}
	
	// To fill other values in request details using appos and apptype. Version is not updated here.
	 public RequestDetails updateRequestDetails(RequestDetails requestDetails)
	 {	log.info("updating request details ...");
		 if(AppSelectorConstants.SUBSCRIBERAPP.equalsIgnoreCase(requestDetails.getAppname()))
			{	log.info(requestDetails.getAppname());
				if(AppSelectorConstants.ANDROIDAPP.equalsIgnoreCase(requestDetails.getApposname())) {
					log.info(requestDetails.getApposname());
					requestDetails.setAppfilename(messageResolver.getMessage(AppSelectorConstants.ANDROID_SUBAPPFILENAME));
					requestDetails.setSysParaPropertyName(AppSelectorConstants.ANDRIOD_SUBSCRIBERAPP);
					return requestDetails; 	
					
				}
				else if(AppSelectorConstants.BLACKBERRYAPP.equalsIgnoreCase(requestDetails.getApposname()))
				{	
					log.info(requestDetails.getApposname());
					requestDetails.setAppfilename(messageResolver.getMessage(AppSelectorConstants.BLACKBERRY_SUBAPPFILENAME));
					requestDetails.setSysParaPropertyName(AppSelectorConstants.BLACKBERRY_SUBSCRIBERAPP);
					return requestDetails; }
				else 
				{	
					log.info(requestDetails.getApposname());
					requestDetails.setAppfilename(messageResolver.getMessage(AppSelectorConstants.J2ME_SUBAPPFILENAME));
					requestDetails.setSysParaPropertyName(AppSelectorConstants.JAVAME_SUBSCRIBERAPP);
 					return requestDetails; 				}
			}
			if(AppSelectorConstants.AGENTAPP.equalsIgnoreCase(requestDetails.getAppname())) {
				
				if(AppSelectorConstants.ANDROIDAPP.equalsIgnoreCase(requestDetails.getApposname())) {	
		 	
					log.info(requestDetails.getApposname());
				  	requestDetails.setAppfilename(messageResolver.getMessage(AppSelectorConstants.ANDROID_AGENTAPPFILENAME));
					requestDetails.setSysParaPropertyName(AppSelectorConstants.ANDRIOD_AGENTAPP);
				 	return requestDetails; 
				 	
				}
				else if(AppSelectorConstants.BLACKBERRYAPP.equalsIgnoreCase(requestDetails.getApposname())) {	
					log.info(requestDetails.getApposname());
					requestDetails.setAppfilename(messageResolver.getMessage(AppSelectorConstants.BLACKBERRY_AGENTAPPFILENAME));
					requestDetails.setSysParaPropertyName(AppSelectorConstants.BLACKBERRY_AGENTAPP);
				  	return requestDetails; 
				 }
				else {
					log.info(requestDetails.getApposname());
					requestDetails.setAppfilename(messageResolver.getMessage(AppSelectorConstants.J2ME_AGENTAPPFILENAME));
					requestDetails.setSysParaPropertyName(AppSelectorConstants.JAVAME_AGENTAPP);
					return requestDetails; 	
			 	}
			}
			return requestDetails;
		 
	 }
	 //process the agent-type header extracted from request to find out mobile OS
	 public String getAppType(String header){
		log.info("agentType header : "+header);
		if(header.toLowerCase().indexOf(AppSelectorConstants.ANDROIDAPP) != -1){ 
			
			return AppSelectorConstants.ANDROIDAPP;
	 	}
		else if(header.indexOf(AppSelectorConstants.BLACKBERRYAPP) != -1){ 

			return AppSelectorConstants.BLACKBERRYAPP;	 
 		}
		else{
			
			return AppSelectorConstants.J2MEAPP; 
 		}
		
	}

	@Override
	public void saveNewAppDetails(RequestDetails requestDetails) {
		AppDetails appD= new AppDetails();
		appD = systemParametersDao.getAppDetails(requestDetails.getSysParaPropertyName());
		appD.setParameterValue(requestDetails.getAppversion());
		 systemParametersDao.saveAppDetails(appD);
	  	}
	   }
 