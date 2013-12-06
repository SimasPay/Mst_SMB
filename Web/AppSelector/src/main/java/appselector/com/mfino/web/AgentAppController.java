package appselector.com.mfino.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import appselector.com.mfino.domain.RequestDetails;
import appselector.com.mfino.service.AppManagerService;
import appselector.com.mfino.util.AppSelectorConstants;
   
/** 
 * 
 * @author Shashank
 *
 */
@Controller
public class AgentAppController{

	 @Autowired
	 @Qualifier("AppManagerServiceImpl")
	 private AppManagerService appManagerService;
	  
	 private final Log log = LogFactory.getLog ( AgentAppController.class );
 
	 @RequestMapping(value = { "/agentapp" })
	 public void downloadhandleRequest(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
		 
		 String userAgentHeader = request.getHeader("User-Agent");
 		 RequestDetails requestDetails = new RequestDetails();
		 requestDetails.setAppname(AppSelectorConstants.AGENTAPP);
		 requestDetails.setApposname(appManagerService.getAppType(userAgentHeader));
		 requestDetails=appManagerService.updateRequestDetails(requestDetails);
	  	 
		 requestDetails.setAppversion(appManagerService.getAppVersion(requestDetails));
 
	  	 File appLocation = new File(appManagerService.getFilePath(requestDetails))	;
	  	 log.info("Donwload File location : "+ appLocation);
       
         InputStream inputStream = new FileInputStream(appLocation);
		 ServletOutputStream servletOutputStream = response.getOutputStream();
		 response.setHeader("Content-Disposition", "attachment;filename=" +requestDetails.getAppfilename());
        
	 	
	     try {
	    	 if(AppSelectorConstants.ANDROIDAPP.equalsIgnoreCase(requestDetails.getApposname())){
	    		
 	    			int length = 0;int sumBytes = 0;
	    			byte[] buffer = new byte[(int) appLocation.length()];

	    			response.setContentLength((int) appLocation.length());
	    			response.setBufferSize(99999);

	    			while ((length = inputStream.read(buffer)) > 0) {
	    				sumBytes = sumBytes + length;
	    				servletOutputStream.write(buffer, 0, length);
	    			}
	    			servletOutputStream.flush();
	    			inputStream.close();
	    	 }
	    	 else{
	    		    response.setContentType("application/octet-stream");
	    		 	FileCopyUtils.copy(inputStream, servletOutputStream);
				}
				
			} catch (Exception e) {
				System.out.println(e);
				response.setContentType("text/plain");
				PrintWriter out = response.getWriter();
				out.write("Unable to download file try again");
			}
     }
	 
 }
