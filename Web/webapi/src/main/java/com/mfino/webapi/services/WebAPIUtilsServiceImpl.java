/**
 * 
 */
package com.mfino.webapi.services;

import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Company;
import com.mfino.domain.IntegrationPartnerMapping;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.Partner;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.service.MfinoService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PartnerService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.result.xmlresulttypes.XMLError;

/**
 * 
 * @author Chaitanya
 * 
 */
@Service("WebAPIUtilsServiceImpl")
public class WebAPIUtilsServiceImpl implements WebAPIUtilsService {

	private static Logger	log	= LoggerFactory.getLogger(WebAPIUtilsServiceImpl.class);
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	
	@Autowired
	@Qualifier("MfinoServiceImpl")
	private MfinoService mfinoService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;

	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	/**
	 * 
	 */
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public void sendError(Integer notificationCode, ServletOutputStream writer, String SourceMDN, String MissingParam) {
		XMLResult xmlResult = new XMLError();
		setServicesForXMLResult(xmlResult);
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		if (StringUtils.isNotBlank(SourceMDN)) {
			SubscriberMDNDAO smdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			SubscriberMdn smdn = smdnDAO.getByMDN(SourceMDN);
			if (smdn != null) {
				Company c = smdn.getSubscriber().getCompany();
				xmlResult.setCompany(c);
				language = (int)smdn.getSubscriber().getLanguage();
			}
		}

		xmlResult.setLanguage(language);
		xmlResult.setNotificationCode(notificationCode);
		xmlResult.setWriter(writer);
		xmlResult.setTransactionTime(new Timestamp());
		xmlResult.setResponseStatus(GeneralConstants.RESPONSE_CODE_FAILURE);

		try {
			xmlResult.render();
			String msg = xmlResult.getXMlelements().get("message");
			log.error(msg + "; Missing or Invalid Parameter=" + MissingParam);
		}
		catch (Exception e) {
			log.error("Exception occurred while rendering result for error message", e);
		}
	}
	
	private void setServicesForXMLResult(XMLResult xmlResult) {
		xmlResult.setNotificationMessageParserService(notificationMessageParserService);
		xmlResult.setMfinoService(mfinoService);
		xmlResult.setPartnerService(partnerService);
		xmlResult.setNotificationService(notificationService);		
	}

	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public void sendIntegrationValidationError(XMLResult xmlResult, ServletOutputStream writer) {
		setServicesForXMLResult(xmlResult);
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		String institutionID = xmlResult.getInstitutionID();
		IntegrationPartnerMapping  integrationPartnerMapping = DAOFactory.getInstance().getIntegrationPartnerMappingDAO().getByInstitutionID(institutionID);
		if(integrationPartnerMapping != null)
		{
			Partner partner = null;
			if(integrationPartnerMapping.getPartner() != null){
				partner = integrationPartnerMapping.getPartner();			
			}
			else if(integrationPartnerMapping.getMfsBiller() != null)
			{
				Set<MFSBillerPartner>  mfsBillerpartners = integrationPartnerMapping.getMfsBiller().getMfsbillerPartnerMaps();
				Iterator<MFSBillerPartner> it  = mfsBillerpartners.iterator();
				if(it.hasNext())
				{
					MFSBillerPartner mfsBillerPartner =  it.next();
					partner = mfsBillerPartner.getPartner();
				}
			}
			if(partner != null)
			{
				language =(int) partner.getSubscriber().getLanguage();
			}
		}		
		xmlResult.setLanguage(language);
		xmlResult.setWriter(writer);
		xmlResult.setTransactionTime(new Timestamp());

		try {
			xmlResult.render();
			String msg = xmlResult.getXMlelements().get("message");
			log.error(msg);
		}
		catch (Exception e) {
			log.error("Exception occurred while rendering result for error message", e);
		}
	}
	
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public void sendSessionTimeoutError(ServletOutputStream writer, String SourceMDN) {
		XMLResult xmlResult = new XMLError();
		setServicesForXMLResult(xmlResult);
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		if (StringUtils.isNotBlank(SourceMDN)) {
			SubscriberMDNDAO smdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			SubscriberMdn smdn = smdnDAO.getByMDN(SourceMDN);
			if (smdn != null) {
				Company c = smdn.getSubscriber().getCompany();
				xmlResult.setCompany(c);
				language = (int)smdn.getSubscriber().getLanguage();
			}
		}

		xmlResult.setLanguage(language);
		xmlResult.setNotificationCode(CmFinoFIX.NotificationCode_WebapiSessionTimedout);
		xmlResult.setWriter(writer);
		xmlResult.setTransactionTime(new Timestamp());

		try {
			xmlResult.render();
			String msg = xmlResult.getXMlelements().get("message");
			log.error(msg + " session expired for mdn=" + SourceMDN);
		}
		catch (Exception e) {
			log.error("Exception occurred while rendering result for error message", e);
		}
	}
}
