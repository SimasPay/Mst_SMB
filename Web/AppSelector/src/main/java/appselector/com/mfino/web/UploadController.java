package appselector.com.mfino.web;
 
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import appselector.com.mfino.domain.RequestDetails;
import appselector.com.mfino.service.AppManagerService;
import appselector.com.mfino.service.DefaultService;
import appselector.com.mfino.util.AppSelectorConstants;

/*@RequestMapping("/uploadFile")
*/
@Controller
public class UploadController {
	
	 @Autowired
	 @Qualifier("AppManagerServiceImpl")
	 private AppManagerService appManagerService;
	
	 @Autowired
	 @Qualifier("DefaultService")
	 private DefaultService defaultService;
	 
	 private final Log log = LogFactory.getLog ( UploadController.class );

	@RequestMapping(value={"/uploadFile"}, method ={ RequestMethod.POST})
	public ModelAndView handleFileUpload(
			@RequestParam(required = true) String appType,
			@RequestParam(required = true) String platform,
			@RequestParam(required = true) String newVersion,
			@RequestParam CommonsMultipartFile sourceFilePath, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
 		Map<String, String> model = new HashMap<String, String>();
		model.put("discription", "");
		RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppname(appType.trim());
		requestDetails.setApposname(platform.trim());
		requestDetails = appManagerService.updateRequestDetails(requestDetails);
		if(!requestDetails.getAppfilename().equalsIgnoreCase(sourceFilePath.getOriginalFilename()))
		{	 
			if(requestDetails.getApposname().equalsIgnoreCase(AppSelectorConstants.BLACKBERRYAPP)&&sourceFilePath.getOriginalFilename().matches("(?i).*zip.*")){
				log.info("Upload request for blackberry app"+sourceFilePath.getOriginalFilename());
			}
			else{return new ModelAndView("failureView","model",model);}
		}
		log.info(newVersion);
		requestDetails.setAppfilename(sourceFilePath.getOriginalFilename());
  		requestDetails.setAppversion(newVersion);
 		String newFilePath = appManagerService.getFilePath(requestDetails);
		
	 	try{
			if (!sourceFilePath.getOriginalFilename().equals("")) {
				File destFile = new File(newFilePath);
				destFile.mkdirs();
				sourceFilePath.transferTo(destFile);
				if(newFilePath.matches("(?i).*zip.*")){
					defaultService.unzipFile(newFilePath,requestDetails.getAppfilename());
				}
				appManagerService.saveNewAppDetails(requestDetails);
	 		}
 		} catch (IllegalStateException e) {
 			
 			e.printStackTrace();
  		} catch (IOException e) {
 			e.printStackTrace();
     }
 		return new ModelAndView("successView","model",model);
 	}

	@RequestMapping(value={"/uploadversion"}, method ={ RequestMethod.POST})
 	public ModelAndView doGet(HttpServletRequest request, HttpServletResponse response) throws
												ServletException, java.io.IOException 
	{
	 	RequestDetails requestDetails = new RequestDetails();
		requestDetails.setAppname(request.getParameter("appType"));
		requestDetails.setApposname(request.getParameter("platform"));
		requestDetails = appManagerService.updateRequestDetails(requestDetails);
	   	String appversion = appManagerService.getAppVersion(requestDetails);
    	Map<String, String> model = new HashMap<String, String>();
		model.put("version", appversion);
		return new ModelAndView("uploadview","model",model);
  
	}
   }
 