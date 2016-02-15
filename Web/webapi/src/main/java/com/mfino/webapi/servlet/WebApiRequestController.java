package com.mfino.webapi.servlet;
 
import static com.mfino.fix.CmFinoFIX.NotificationCode_Failure;
import static com.mfino.fix.CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.MDC;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.crypto.CryptographyService;
import com.mfino.domain.ChannelCode;
import com.mfino.exceptions.CoreException;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.exceptions.InvalidMDNException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.integrations.vo.IntegrationDetails;
import com.mfino.result.XMLResult;
import com.mfino.service.MfinoService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PartnerService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.service.AccountAPIServices;
import com.mfino.transactionapi.service.ActorChannelValidationService;
import com.mfino.transactionapi.service.AgentAPIServices;
import com.mfino.transactionapi.service.BankAPIService;
import com.mfino.transactionapi.service.BaseAPIService;
import com.mfino.transactionapi.service.BuyAPIService;
import com.mfino.transactionapi.service.MShoppingAPIService;
import com.mfino.transactionapi.service.NFCAPIService;
import com.mfino.transactionapi.service.PaymentAPIService;
import com.mfino.transactionapi.service.WalletAPIService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.webapi.services.GenericWebAPIService;
import com.mfino.webapi.services.RequestValidationService;
import com.mfino.webapi.services.WEBAPISecurityManagementService;
import com.mfino.webapi.services.WebAPIUtilsService;
import com.mfino.webapi.utilities.IUserDataContainer;
import com.mfino.webapi.utilities.IntegrationDetailsExtractor;
import com.mfino.webapi.utilities.InvalidWeabpiSessionException;
import com.mfino.webapi.utilities.UserDataToTxnDetailsConverter;
@Controller
public class WebApiRequestController {

	protected static final long	serialVersionUID	= 1L;
	private   static final Logger	log = LoggerFactory.getLogger(WebApiRequestController.class);

	@Autowired
	@Qualifier("AgentAPIServicesImpl")
	private AgentAPIServices agentAPIServices;
	
	@Autowired
	@Qualifier("AccountAPIServicesImpl")
	private AccountAPIServices accountAPIServices;
	
	@Autowired
	@Qualifier("GenericWebAPIServiceImpl")
	private GenericWebAPIService genericWebAPIService;
	
	@Autowired
	@Qualifier("RequestValidationServiceImpl")
	private RequestValidationService requestValidationService;
 	 
	@Autowired
	@Qualifier("WalletAPIServiceImpl")
	private WalletAPIService walletAPIService;
	
	@Autowired
	@Qualifier("BankAPIServiceImpl")
	private BankAPIService bankAPIService;
	
	@Autowired
	@Qualifier("PaymentAPIServiceImpl")
	private PaymentAPIService paymentAPIService;
	
	@Autowired
	@Qualifier("BuyAPIServiceImpl")
	private BuyAPIService buyAPIService;
	
	@Autowired
	@Qualifier("MShoppingAPIServiceImpl")
	private MShoppingAPIService mShoppingAPIService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("NFCAPIServiceImpl")
	private NFCAPIService nfcAPIService;
	
	@Autowired
	@Qualifier("WEBAPISecurityManagementServiceImpl")
	private WEBAPISecurityManagementService webAPISecurityManagementService;
	
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
	@Qualifier("WebAPIUtilsServiceImpl")
	private WebAPIUtilsService webAPIUtilsService;
	
	@Autowired
	@Qualifier("ActorChannelValidationServiceImpl")
	private ActorChannelValidationService actorChannelValidationService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@RequestMapping(value = "/sdynamic", method = {RequestMethod.GET, RequestMethod.POST})
	public void getHttpsRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		boolean isHTTPS = true;
		processWebAPITransactions(request, response, isHTTPS);
	}
	
	@RequestMapping(value = "/dynamic", method = {RequestMethod.GET, RequestMethod.POST})
	public void getHttpRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		boolean isHTTPS = false;
		processWebAPITransactions(request, response, isHTTPS);
	}
	
	public void processWebAPITransactions(HttpServletRequest request, HttpServletResponse response, boolean isHTTPS) throws ServletException, IOException {
		
		String sourceMDN = request.getParameter(ApiConstants.PARAMETER_SOURCE_MDN);
		String serviceName = request.getParameter(ApiConstants.PARAMETER_SERVICE_NAME);
		String transactionName = request.getParameter(ApiConstants.PARAMETER_TRANSACTIONNAME);
 		String institutionID =  request.getParameter(ApiConstants.PARAMETER_INSTITUTION_ID);
 		String sourcePocketCode = request.getParameter(ApiConstants.PARAMETER_SRC_POCKET_CODE);
 		String str_isSimaspayActivity = request.getParameter(ApiConstants.IS_SIMASPAY_ACTIVITY);
 		boolean isSimaspayActivity=false;
 		if(str_isSimaspayActivity!=null&&str_isSimaspayActivity.equalsIgnoreCase("true")){
 			isSimaspayActivity=true;
 		}
 		boolean isLoginEnabled = true;

		ServletOutputStream servletOutputWriter = response.getOutputStream();
		IUserDataContainer udcontainer = null;
		XMLResult xmlResult = null;
  
		String trxnIdentifier = genericWebAPIService.generateTransactionIdentifier(request);
 		MDC.put("transactionIdentifier", trxnIdentifier);

 		log.info("Transaction Identifier created in DispatcherServlet with ID -->"+trxnIdentifier);
 		log.info("doPost: Begin for MDN:"+sourceMDN+" serviceName: "+serviceName+" transactionName: "+transactionName+" Source IP: "+request.getRemoteAddr());
		 
		dumpRequest(request);
	
		if(!requestValidationService.validateRequest(request, servletOutputWriter)){
			return;
		}
		if(StringUtils.isNotBlank(institutionID)){
			IntegrationDetails integrationDetails = IntegrationDetailsExtractor.getIntegrationDetails(request);
			isLoginEnabled = requestValidationService.isLoginEnabledForIntegration(integrationDetails);
		}
		
		try {
//			sourceMDN = subscriberService.normalizeMDN(sourceMDN);
 			try {
 				if(isHTTPS){
 					udcontainer = getUserDataContainerForHttps(request, sourceMDN, servletOutputWriter, isLoginEnabled);
 				}else{
 					udcontainer = getUserDataContainerForHttp(request, sourceMDN, servletOutputWriter, isLoginEnabled);
 				}
			}
			catch (InvalidMDNException ex) {
				webAPIUtilsService.sendError(CmFinoFIX.NotificationCode_MDNNotFound, servletOutputWriter, sourceMDN, ApiConstants.PARAMETER_SOURCE_MDN);
				return ;
			}
			catch (InvalidWeabpiSessionException ex) {
				log.info("Inavlid Webapi session for "+sourceMDN+".A webapi request is received when the session of the user timedout");
				webAPIUtilsService.sendSessionTimeoutError(servletOutputWriter, sourceMDN);
				return ;
			}
			catch (CoreException coreEx){
				webAPIUtilsService.sendError(coreEx.getNotificationCode(), servletOutputWriter, sourceMDN, coreEx.getMessage());
				return;
			}
 
			UserDataToTxnDetailsConverter converter = new UserDataToTxnDetailsConverter();

			TransactionDetails transactionDetails = converter.getTransactionDetails(udcontainer);
  			transactionDetails.setTransactionIdentifier(trxnIdentifier);
  			transactionDetails.setSimpaspayActivity(isSimaspayActivity);
  			
//  			transactionDetails.setSourceMDN(sourceMDN);

  			ChannelCode channelCode = genericWebAPIService.getChannelCode(transactionDetails.getChannelCode());
  			transactionDetails.setCc(channelCode);
			if(!String.valueOf(1).equals(transactionDetails.getDestPocketCode()) && StringUtils.isNotBlank(transactionDetails.getDestinationBankAccountNo()))	{
 
				String destinationMDN = genericWebAPIService.getDestinationMDNFromAccountNumber(transactionDetails.getDestinationBankAccountNo());
				transactionDetails.setDestMDN(destinationMDN);
 			}
			BaseAPIService service = null ;
			
			 if (!(ApiConstants.TRANSACTION_LOGIN.equalsIgnoreCase(transactionName))){
				 genericWebAPIService.activateInactiveSubscriber(transactionDetails);
			 }
			 //check to restrict buy airtime using bank pocket
			if (ApiConstants.BANK_POCKET_CODE.equals(sourcePocketCode)
					&& (ApiConstants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY.equalsIgnoreCase(transactionName)
							|| ApiConstants.TRANSACTION_AIRTIME_PURCHASE.equalsIgnoreCase(transactionName)
							|| ApiConstants.TRANSACTION_AIRTIME_PIN_PURCHASE_INQUIRY.equalsIgnoreCase(transactionName) 
							|| ApiConstants.TRANSACTION_AIRTIME_PIN_PURCHASE.equalsIgnoreCase(transactionName))
							&& "true".equalsIgnoreCase(systemParametersService.getString(SystemParameterKeys.RESTRICT_BANKPOCKET_TOBUY_AIRTIME))) {
				webAPIUtilsService.sendError(
						CmFinoFIX.NotificationCode_FeatureNotAvailable,
						servletOutputWriter, sourceMDN,
						ApiConstants.PARAMETER_SRC_POCKET_CODE);
				return;
			}
			if(transactionDetails!=null){
				
				if(StringUtils.isNotBlank(transactionDetails.getSourcePIN())) {
					
					transactionDetails.setSourcePIN(CryptographyService.decryptWithPrivateKey(transactionDetails.getSourcePIN()));
				}
				
				if(StringUtils.isNotBlank(transactionDetails.getNewPIN())) {
				
					transactionDetails.setNewPIN(CryptographyService.decryptWithPrivateKey(transactionDetails.getNewPIN()));
				}
				
				if(StringUtils.isNotBlank(transactionDetails.getConfirmPIN())) {
				
					transactionDetails.setConfirmPIN(CryptographyService.decryptWithPrivateKey(transactionDetails.getConfirmPIN()));
				}
				
				if(StringUtils.isNotBlank(transactionDetails.getActivationOTP())) {
				
					transactionDetails.setActivationOTP(CryptographyService.decryptWithPrivateKey(transactionDetails.getActivationOTP()));
				}
				
				if(StringUtils.isNotBlank(transactionDetails.getTransactionOTP())) {
				
					transactionDetails.setTransactionOTP(CryptographyService.decryptWithPrivateKey(transactionDetails.getTransactionOTP()));
				}
				
				if(StringUtils.isNotBlank(transactionDetails.getAuthenticationString())) {
					
					transactionDetails.setAuthenticationString(CryptographyService.decryptWithPrivateKey(transactionDetails.getAuthenticationString()));
				}
				
				if(StringUtils.isNotBlank(transactionDetails.getCardPAN())) {
					
					transactionDetails.setCardPAN(CryptographyService.decryptWithPrivateKey(transactionDetails.getCardPAN()));
				}
				
				if (ServiceAndTransactionConstants.SERVICE_ACCOUNT.equals(transactionDetails.getServiceName())) {
					
					service = (BaseAPIService) accountAPIServices;
				}
				else if (ServiceAndTransactionConstants.SERVICE_WALLET.equals(transactionDetails.getServiceName())) {
				
					service = (BaseAPIService) walletAPIService;
				}
				else if (ServiceAndTransactionConstants.SERVICE_AGENT.equals(transactionDetails.getServiceName())) {
					
					service = (BaseAPIService) agentAPIServices;
				}
				else if (ServiceAndTransactionConstants.SERVICE_BANK.equals(transactionDetails.getServiceName())) {
					
					service = (BaseAPIService) bankAPIService;
				}
				else if (ServiceAndTransactionConstants.SERVICE_SHOPPING.equals(transactionDetails.getServiceName())){
					
					service = (BaseAPIService) mShoppingAPIService;
				}
				else if(ApiConstants.SERVICE_BUY.equals(transactionDetails.getServiceName()))
				{
					service = (BaseAPIService) buyAPIService;
				}
				else if(ApiConstants.SERVICE_PAYMENT.equals(transactionDetails.getServiceName()))
				{
					service = (BaseAPIService) paymentAPIService;
				}
				else if (ServiceAndTransactionConstants.SERVICE_NFC.equals(transactionDetails.getServiceName())){
					service = (BaseAPIService) nfcAPIService;

				}	
				else{
					// Not a valid mode provided by the requester.
					throw new InvalidDataException("InvalidService", CmFinoFIX.NotificationCode_FeatureNotAvailable,transactionDetails.getServiceName());
				}

				//actor channel mapping
				boolean isTransactionApproved = actorChannelValidationService.validateTransaction(transactionDetails);
			
				if(isTransactionApproved){
					xmlResult = service.handleRequest(transactionDetails);
				}else if(!isTransactionApproved){
					throw new InvalidDataException("ActorChannelMapping", CmFinoFIX.NotificationCode_ActorChannelMapping,transactionDetails.getServiceName());
				}
			}
			if (xmlResult == null) {
 				return;
			}

 			xmlResult=genericWebAPIService.updateSubscriberDetails(transactionDetails.getSourceMDN(), xmlResult);
			xmlResult.setKeyParameter(udcontainer.getKeyParameter());
			xmlResult.setWriter(servletOutputWriter);

			if(ServiceAndTransactionConstants.TRANSACTION_GET_THIRD_PARTY_DATA.equals(transactionName) && ServiceAndTransactionConstants.SERVICE_PAYMENT.equals(serviceName)){
				FileInputStream input;
				if(StringUtils.isNotBlank(xmlResult.getMessage())){			
					input = new FileInputStream(xmlResult.getMessage());
				}
				else{
					File errorFile = new File("../webapps/webapi/WEB-INF", "errorJson.txt");
					input = new FileInputStream(errorFile);
				}
				IOUtils.copy(input, servletOutputWriter);
				response.setContentType("application/json");
				servletOutputWriter.flush();
				servletOutputWriter.close();
			} else if(ServiceAndTransactionConstants.TRANSACTION_GET_THIRD_PARTY_LOCATION.equals(transactionName) && ServiceAndTransactionConstants.SERVICE_PAYMENT.equals(serviceName)){
					FileInputStream input;
					if(StringUtils.isNotBlank(xmlResult.getMessage())){			
						servletOutputWriter.print(xmlResult.getMessage());
					}
					else{
						File errorFile = new File("../webapps/webapi/WEB-INF", "errorJson.txt");
						input = new FileInputStream(errorFile);
						IOUtils.copy(input, servletOutputWriter);
					}
					response.setContentType("application/json");
					servletOutputWriter.flush();
					servletOutputWriter.close();
			} else if(ServiceAndTransactionConstants.SERVICE_ACCOUNT.equals(serviceName) && ServiceAndTransactionConstants.TRANSACTION_GENERATE_FAVORITE_JSON.equals(transactionName)) {
				if(xmlResult.getNotificationCode() != null) {
					xmlResult.setNotificationMessageParserService(notificationMessageParserService);
					xmlResult.setNotificationService(notificationService);
					xmlResult.render();
				} else {
					response.setContentType("application/json");
					if(StringUtils.isNotBlank(xmlResult.getMessage())){	
						servletOutputWriter.print(xmlResult.getMessage());
					}				
					servletOutputWriter.flush();
					servletOutputWriter.close();
				}				
			}
			/*else if(ServiceAndTransactionConstants.SERVICE_WALLET.equals(serviceName) && ServiceAndTransactionConstants.TRANSACTION_DOWNLOAD_HISTORY_AS_PDF.equals(transactionName)
					&& xmlResult.getNotificationCode() == CmFinoFIX.NotificationCode_TransactionHistoryDownloadSuccessful) 
			{
				FileInputStream input;
				File pdfFile = new File(xmlResult.getFilePath());
				input = new FileInputStream(pdfFile);
				IOUtils.copy(input, servletOutputWriter);
				response.setContentType("application/pdf");
				servletOutputWriter.flush();
				servletOutputWriter.close();

			}*/ 
			else{
				try {
					if(xmlResult.getMultixResponse()==null){
						log.info("Response NotificationCode: "+xmlResult.getNotificationCode()+"  for SourceMDN:"+sourceMDN+" ServiceName:"+serviceName+" TransactionName:"+transactionName);
					}
					xmlResult.setNotificationMessageParserService(notificationMessageParserService);
					xmlResult.setMfinoService(mfinoService);
					xmlResult.setPartnerService(partnerService);
					xmlResult.setNotificationService(notificationService);
					xmlResult.render();
				}
				
				catch (Exception e) {
					log.error("Error occurred while rendering result", e);
					webAPIUtilsService.sendError(NotificationCode_Failure, servletOutputWriter, sourceMDN, "error");
					return;
				}
			}
		}
		catch (InvalidDataException dataEx) {
			log.error(dataEx.getLogMessage());
			webAPIUtilsService.sendError(dataEx.getNotificationCode(), servletOutputWriter, sourceMDN, dataEx.getParameterName());
			return;
		}
		catch (NullPointerException ex) {
			log.error("Error occurred while handling request", ex);
			webAPIUtilsService.sendError(NotificationCode_InvalidWebAPIRequest_ParameterMissing, servletOutputWriter, sourceMDN, "Error occured");
			return;
		}  catch (DataException de) {
			log.error("Exception occured while creating the Service Charge Transaction Log",de);
			webAPIUtilsService.sendError(CmFinoFIX.NotificationCode_TransactionFailedDueToInvalidAmount, servletOutputWriter, sourceMDN, ApiConstants.PARAMETER_AMOUNT);
			return;
		}
		catch (Exception ex) {
			log.error("Error occurred while handling request", ex);
			webAPIUtilsService.sendError(CmFinoFIX.NotificationCode_Failure, servletOutputWriter, sourceMDN, "Error occured");
			return;
		}
		finally{
			log.info("doPost: End for MDN:"+sourceMDN+" serviceName: "+serviceName+" transactionName: "+transactionName);
			MDC.remove("transactionIdentifier"); 
		}
	}

	/**
	 * @param request
	 * @param sourceMDN
	 * @param writer
	 * @return
	 * @throws InvalidMDNException 
	 * @throws InvalidWeabpiSessionException 
	 * @throws CoreException 
	 */
	protected IUserDataContainer getUserDataContainerForHttp(HttpServletRequest request, String sourceMDN,
			ServletOutputStream writer, boolean isLoginEnabled) throws InvalidWeabpiSessionException, InvalidMDNException, CoreException {
		if(!ApiConstants.SCHEME_HTTP.equals(request.getScheme())) {
			throw new CoreException("Not an HTTP request.Could be an https request", CmFinoFIX.NotificationCode_NotHTTPRequest);
		}
		IUserDataContainer udcontainer = webAPISecurityManagementService.getRequestData(request,writer, isLoginEnabled);
		udcontainer.setIsHttps(false);
		
		return udcontainer;
	}
	protected IUserDataContainer getUserDataContainerForHttps(HttpServletRequest request, String sourceMDN,
			ServletOutputStream writer, boolean isLoginEnabled) throws InvalidWeabpiSessionException, InvalidMDNException, CoreException {
		if(!ApiConstants.SCHEME_HTTPS.equals(request.getScheme())) {
			throw new CoreException("Not an HTTPS request.", CmFinoFIX.NotificationCode_NotHTTPSRequest);
		}
		IUserDataContainer udcontainer = webAPISecurityManagementService.getHttpsRequestData(request,writer, isLoginEnabled);
		udcontainer.setIsHttps(true);
		
		return udcontainer;
	}
	protected void dumpRequest(HttpServletRequest request){
		if(request!=null){
			@SuppressWarnings("unchecked")
			Map<String, String[]> paramMap = request.getParameterMap();
			if(paramMap==null){
				log.error("ParameterMap is null in Request");
			}else{
				Set<Entry<String, String[]>> entrySet = paramMap.entrySet();
				StringBuffer buffer = new StringBuffer();
				StringBuffer debugBuffer = new StringBuffer();
				int size = entrySet.size();
				int count = 0;
				for(Entry<String, String[]> entry: entrySet){
					String paramName = entry.getKey();
					buffer.append(paramName);
					buffer.append('=');
								
					String[] value = entry.getValue();
					
					debugBuffer.append(paramName);
					debugBuffer.append('=');					
					debugBuffer.append(value[0]);
					
					if(ApiConstants.isSecuredParameter(paramName)){
						buffer.append(getDumpString(value[0]));
					}else{
						buffer.append(value[0]);
					}
					count++;
					if(count<size){
						buffer.append('&');
						debugBuffer.append('&');
					}
				}
				log.info("Received Request: "+buffer.toString());
				log.debug("Received Request: "+debugBuffer.toString());
			}
		}else{
			log.error("Request is null");
		}
	}
	
	private String getDumpString(String value){
		StringBuffer buffer = new StringBuffer();
		if(value!=null){
			int size = value.length();
			for(int count=0; count<size; count++){
				buffer.append('x');
			}
		}
		return buffer.toString();
	}
}