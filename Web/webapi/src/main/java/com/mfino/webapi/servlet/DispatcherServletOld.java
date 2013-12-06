//package com.mfino.webapi.servlet;
//
//import static com.mfino.fix.CmFinoFIX.NotificationCode_Failure;
//import static com.mfino.fix.CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing;
//
//import java.io.IOException;
//
//import javax.servlet.ServletException;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.mfino.fix.CmFinoFIX;
//import com.mfino.result.XMLResult;
//import com.mfino.webapi.services.AccountAPIServices;
//import com.mfino.webapi.services.BankAPIService;
//import com.mfino.webapi.services.BaseAPIService;
//import com.mfino.webapi.services.BillsPaymentAPIService;
//import com.mfino.webapi.services.MerchantAPIService;
//import com.mfino.webapi.services.MobileShoppingAPIService;
//import com.mfino.webapi.services.SubscriberAPIService;
//import com.mfino.webapi.utilities.ApiConstantsOld;
//import com.mfino.webapi.utilities.WebAPIUtils;
//
///**
// * Servlet implementation class DispatcherServlet
// */
//public class DispatcherServletOld extends HttpServlet {
//
//	private static final long	serialVersionUID	= 1L;
//
//	private static Logger log = LoggerFactory.getLogger(DispatcherServletOld.class);
//
//	/**
//	 * @see HttpServlet#HttpServlet()
//	 */
//	public DispatcherServletOld() {
//		super();
//	}
//
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
//	 *      response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		doPost(request, response);
//	}
//
//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
//	 *      response)
//	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		String sourceMDN = request.getParameter(ApiConstantsOld.PARAMETER_SOURCE_MDN);
//		String mode = request.getParameter(ApiConstantsOld.PARAMETER_MODE);
//		ServletOutputStream writer = response.getOutputStream();
//
//		try {
//
//			
//			XMLResult xmlResult = null;
//			BaseAPIService service = null;
//
//			if (ApiConstantsOld.MODE_SUBSCRIBER.equals(mode)) {
//				
//				service = new SubscriberAPIService(request, writer);
//				
//			} else if (ApiConstantsOld.MODE_MERCHANT.equals(mode)) {
//				
//				service = new MerchantAPIService(request, writer);
//				
//			} else if (ApiConstantsOld.MODE_BANK_ACCOUNT.equals(mode)) {
//
//				service = new BankAPIService(request, writer);
//				
//			} else if (ApiConstantsOld.MODE_BILLPAYMENTS.equals(mode)) {
//				
//				service = new BillsPaymentAPIService(request, writer);
//				
//			} else if (ApiConstantsOld.MODE_MOBILE_SHOPPING.equals(mode)) {
//				
//				service = new MobileShoppingAPIService(request, writer);
//				
//			} else if (ApiConstantsOld.MODE_MFS.equals(mode)) {
//				
//				service = new AccountAPIServices(request, writer);
//				
//			} else {
//				// Not a valid mode provided by the requester.
//				WebAPIUtils.sendError(NotificationCode_Failure, writer, sourceMDN, "InvalidMode");
//				return;
//			}
//			xmlResult = service.handleRequest();
//			if(xmlResult==null){
//				//An error would have been sent by the service, so just return;
//				return;
//			}
//			
//			xmlResult.setWriter(writer);
//			try {
//				xmlResult.render();
//			}catch (Exception e) {
//				log.error("Error occurred while rendering result", e);
//				WebAPIUtils.sendError(NotificationCode_Failure, writer, sourceMDN, "error");
//				return;
//			}
//		}catch (NullPointerException ex) {
//			log.error("Error occurred while handling request", ex);
//			WebAPIUtils.sendError(NotificationCode_InvalidWebAPIRequest_ParameterMissing, writer, sourceMDN, "Error occured");
//			return;
//		}catch (Exception ex) {
//			log.error("Error occurred while handling request", ex);
//			WebAPIUtils.sendError(CmFinoFIX.NotificationCode_Failure, writer, sourceMDN, "Error occured");
//			return;
//		}
//	}
//
//}