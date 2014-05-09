
package com.mfino.web.admin.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.View;

import com.mfino.fix.CmFinoFIX;
import com.mfino.i18n.MessageText;
import com.mfino.service.AuthorizationService;
import com.mfino.uicore.web.JSONView;
import com.mfino.util.ConfigurationUtil;

/**
 * @author Amar
 * 
 */
@Controller
public class UploadPromoImageFileController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

	@RequestMapping("/uploadpromoimage.htm")
	protected View handleUpload(HttpServletRequest request, HttpServletResponse response) {
		log.debug("Uploading Promo image ");   
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("success", false);
		if (request instanceof MultipartHttpServletRequest) {
			int maxFileSizeMB = Integer.parseInt(ConfigurationUtil.getUploadFileSizeLimit());
			long maxFileSize = maxFileSizeMB * 1024L * 1024L;
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			Iterator<String> fileNames = multipartRequest.getFileNames();
			try {
				if (authorizationService.isAuthorized(CmFinoFIX.Permission_Promos)) {
					while (fileNames.hasNext()) {
						String fileName = (String) fileNames.next();
						MultipartFile file = multipartRequest.getFile(fileName);
						if (file == null || file.getSize() == 0) {
							responseMap.put("Error", "");
						}else if(!isValidFileFormat(file)){
							responseMap.put("Error", MessageText._("Please upload a file with valid format"));
						} else if (file.getSize() > maxFileSize) {
							responseMap.put("Error", String.format(MessageText._("Sorry, file size exceeds the max limit") + " %s" + MessageText._("MB."), maxFileSizeMB));
						} else {
							String promoImagepath = ConfigurationUtil.getPromoImagepath();
							File catalinaBase = new File( System.getProperty( "catalina.base" ) ).getAbsoluteFile();
							File destFile = new File( catalinaBase, "webapps/" +  promoImagepath);
							file.transferTo(destFile);                        	
							responseMap.put("success", true);
							responseMap.put("file", file.getOriginalFilename());
						}
					}
				} else {
					responseMap.put("success", false);
					responseMap.put("Error", String.format(MessageText._("You are not authorized to perform this operation.")));
					return new JSONView(responseMap);
				}
			} catch (Throwable throwable) {
				log.error(throwable.getMessage(), throwable);               
				responseMap.put("Error", "Sorry, " + throwable.toString());
			} 
		}
		return new JSONView(responseMap);
	}
	
	private boolean isValidFileFormat(MultipartFile file)
	{
		String fileName = file.getOriginalFilename().toLowerCase();
		if(fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".gif") || fileName.endsWith(".tif"))
			return true;
		return false;
	}
}
