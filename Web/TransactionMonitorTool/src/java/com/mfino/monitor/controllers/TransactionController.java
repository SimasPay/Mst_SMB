package com.mfino.monitor.controllers;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.domain.Partner;
import com.mfino.monitor.constants.MonitorPeriodConstants;
import com.mfino.monitor.model.ChannelTransactionsResult;
import com.mfino.monitor.model.FailedTransactionsResult;
import com.mfino.monitor.model.FloatBalanceResult;
import com.mfino.monitor.model.FloatWalletTransaction;
import com.mfino.monitor.model.PerRcTransactionResults;
import com.mfino.monitor.model.PerTransactionResults;
import com.mfino.monitor.model.ServiceTransactionsResult;
import com.mfino.monitor.model.Transaction;
import com.mfino.monitor.model.TransactionSummaryResult;
import com.mfino.monitor.processor.ChannelTransactionsProcessor;
import com.mfino.monitor.processor.FailedTransactionsProcessor;
import com.mfino.monitor.processor.PerRcTransactionsProcessor;
import com.mfino.monitor.processor.PerTransactionsProcessor;
import com.mfino.monitor.processor.ServiceTransactionsProcessor;
import com.mfino.monitor.processor.TransactionSummaryProcessor;
import com.mfino.monitor.processor.Interface.FloatBalanceProcessorI;
import com.mfino.monitor.processor.Interface.FloatWalletTransactionProcessorI;
import com.mfino.monitor.processor.Interface.TransactionSearchProcessorI;
import com.mfino.monitor.util.FileReaderUtil;
import com.mfino.service.impl.SystemParametersServiceImpl;
import com.mfino.util.DateTimeUtil;

/**
 * @author Srikanth
 * 
 */

@Controller("TransactionController")
public class TransactionController {
	
    @Autowired
    @Qualifier("FloatBalanceProcessor")
	private FloatBalanceProcessorI floatBalanceProcessor ;
    
    @Autowired
    @Qualifier("FloatWalletTransactionProcessor")
	private FloatWalletTransactionProcessorI floatWalletTransactionProcessor ;

    @Autowired
    @Qualifier("TransactionSearchProcessor")
	TransactionSearchProcessorI transactionSearchProcessor;

	private DAOFactory daoFactory = DAOFactory.getInstance();
	private PartnerDAO partnerDao = daoFactory.getPartnerDAO();
	private Logger log = Logger.getLogger(TransactionController.class);
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyyMMdd-HH:mm:ss:SSS");
	
	static ClassLoader loader = Thread.currentThread().getContextClassLoader();
    static URL url = loader.getResource("sqlQueries.json");
	static JSONObject SQL_QUERIES_JSON_OBJECT = null;
	
	@RequestMapping("/getTransactions.htm")
	public ModelAndView activation(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException, JSONException {
		Map<String, Object> model = new HashMap<String, Object>();
		String portletName = request.getParameter("portlet");
		// holds the "monitor period" combo box value
		String monitorPeriod = request.getParameter("monitoringPeriod");
		Date lastUpdateTimeGE = null;
		
		try {
			
			SQL_QUERIES_JSON_OBJECT = FileReaderUtil.readFileContAsJsonObj(url.getFile());
			
		} catch (Exception e) {
			
			log.info("Unable to read the sqlQueries.json file from the path:" + e);
		}
		
		if (StringUtils.isNotEmpty(monitorPeriod)) {
			lastUpdateTimeGE = getTimeZoneBasedDateTimeGE(calculateLastUpdateTimeGE(monitorPeriod));
			log.info("lastUpdateTimeGE in TransactionController is: "+lastUpdateTimeGE);
		}

		if (portletName.equalsIgnoreCase("transactionSummary")) {
			List<TransactionSummaryResult> results = null;
			log.info("In transactionSummary of TransactionController Begin");
			TransactionSummaryProcessor tsp = new TransactionSummaryProcessor();
			tsp.setQUERY(SQL_QUERIES_JSON_OBJECT.getString("TRANSACTION_SUMMARY"));
			tsp.setLastUpdateTimeGE(lastUpdateTimeGE);
			results = tsp.process();
			model.put("results", results);
			return new ModelAndView("transactionSummaryResults", "model", model);
			
		} else if (portletName.equalsIgnoreCase("perTransactions")) {
			List<PerTransactionResults> results = null;
			log.info("In perTransactions of TransactionController Begin");
			PerTransactionsProcessor ptp = new PerTransactionsProcessor();
			ptp.setLastUpdateTimeGE(lastUpdateTimeGE);
			ptp.setQUERY(SQL_QUERIES_JSON_OBJECT.getString("PER_TRANSACTION"));
			results = ptp.process();
			model.put("results", results);
			return new ModelAndView("perTransactionsResults", "model", model);

		} else if (portletName.equalsIgnoreCase("failedTransactions")) {
			log.info("In failedTransactions of TransactionController Begin");
			int failedTxnsLimit = (Integer.parseInt(null != request.getParameter("failedTxns")?request.getParameter("failedTxns"):"5"));
			List<FailedTransactionsResult> results = null;
			FailedTransactionsProcessor ftp = new FailedTransactionsProcessor();
			ftp.setLastUpdateTimeGE(lastUpdateTimeGE);			
			ftp.setTxnLimit(failedTxnsLimit);
			ftp.setQUERY(SQL_QUERIES_JSON_OBJECT.getString("RECENT_FAILED_TRANSACTION"));
			results = ftp.process();
			model.put("results", results);
			return new ModelAndView("failedTransactionsResults", "model", model);			
			
		} else if (portletName.equalsIgnoreCase("perRcTransactions")) {
			List<PerRcTransactionResults> results = null;
			log.info("In perRcTransactions of TransactionController Begin");
			PerRcTransactionsProcessor prtp = new PerRcTransactionsProcessor();
			prtp.setLastUpdateTimeGE(lastUpdateTimeGE);
			prtp.setQUERY(SQL_QUERIES_JSON_OBJECT.getString("PER_RC_TRANSACTION"));
			results = prtp.process();
			model.put("results", results);
			return new ModelAndView("perRcTransactionsResults", "model", model);
			
		} else if (portletName.equalsIgnoreCase("serviceTransactions")) {
			log.info("In serviceTransactions of TransactionController Begin");
			List<ServiceTransactionsResult> results = null;
			ServiceTransactionsProcessor stp = new ServiceTransactionsProcessor();
			stp.setLastUpdateTimeGE(lastUpdateTimeGE);
			results = stp.process();
			model.put("results", results);
			return new ModelAndView("serviceTransactionsResults", "model", model);
			
		} else if (portletName.equalsIgnoreCase("channelTransactions")) {
			log.info("In channelTransactions of TransactionController Begin");
			List<ChannelTransactionsResult> results = null;
			ChannelTransactionsProcessor ctp = new ChannelTransactionsProcessor();
			ctp.setLastUpdateTimeGE(lastUpdateTimeGE);
			results = ctp.process();
			model.put("results", results);
			return new ModelAndView("channelTransactionsResults", "model", model);			

		} else if (portletName.equalsIgnoreCase("floatBalance")) {			
			FloatBalanceResult result = null;
			result = floatBalanceProcessor.process();
			model.put("result", result);
			return new ModelAndView("floatBalanceResults", "model", model);
			
		} else if (portletName.equalsIgnoreCase("floatWalletTransactions")) {
			List<FloatWalletTransaction> results = null;
			FloatWalletTransaction searchBean = buildFloatWalletSearchBean(request);
			results = floatWalletTransactionProcessor.process(searchBean);
			model.put("results", results);
			model.put("total", searchBean.getTotal());
			return new ModelAndView("floatWalletTransactionsResults", "model", model);
			
		} else if (portletName.equalsIgnoreCase("transactionSearch")) {
			log.info("In transactionSearch of TransactionController Begin");
			List<Transaction> results;
			Transaction searchBean = buildSearchBean(request);
			results = transactionSearchProcessor.process(searchBean);
			model.put("results", results);
			model.put("total", searchBean.getTotal());
			return new ModelAndView("transactionSearchResults", "model", model);
		}
		return null;
	}

	/**
	 * Builds a searchBean with all the params from request object
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return searchBean Transaction
	 */
	private Transaction buildSearchBean(HttpServletRequest request) {
		Transaction searchBean = new Transaction();
		if (StringUtils.isNotBlank(request.getParameter("linkRefID"))) {			
			// case when details window is opened from failed transactions
			// portlet
			searchBean.setID(Long.valueOf(request.getParameter("linkRefID")));
		} else if (StringUtils.isNotBlank(request.getParameter("idSearch"))) {
			// case when search is done with reference id
			searchBean.setID(Long.valueOf(request.getParameter("idSearch")));
		}
		// case when details window is open with monitoringPeriod set to some
		// value
		if (StringUtils.isNotBlank(request.getParameter("monitoringPeriod"))) {
			String monitorPeriod = request.getParameter("monitoringPeriod");
			Date lastUpdateTimeGE = calculateLastUpdateTimeGE(monitorPeriod);
			searchBean.setUpdateTimeGE(lastUpdateTimeGE);
		}
		// case when start, end date are selected in search transactions
		// form(monitoringPeriod will be null in this case)
		else {
			if (StringUtils.isNotBlank(request.getParameter("startDate"))) {
				Date startDate = null;
				try {
					dateFormat.parse(request.getParameter("startDate"));
					startDate = dateFormat.getCalendar().getTime();
				} catch (ParseException e) {
					log.error(e);
				}
				searchBean.setUpdateTimeGE(startDate);
			}
			if (StringUtils.isNotBlank(request.getParameter("endDate"))) {
				Date endDate = null;
				try {
					dateFormat.parse(request.getParameter("endDate"));
					endDate = dateFormat.getCalendar().getTime();
				} catch (ParseException e) {
					log.error(e);
				}
				searchBean.setUpdateTimeLT(endDate);
			}
		}
		if (StringUtils.isNotBlank(request.getParameter("sourcePartnerCode"))) {
			Partner sourcePartner = partnerDao.getPartnerByPartnerCode(request
					.getParameter("sourcePartnerCode"));
			if (sourcePartner == null) {
				searchBean.setSourcePartnerID(new Long("-1"));
			} else {
				searchBean.setSourcePartnerID(sourcePartner.getID());
			}
		}
		if (StringUtils.isNotBlank(request.getParameter("destPartnerCode"))) {
			Partner destPartner = partnerDao.getPartnerByPartnerCode(request
					.getParameter("destPartnerCode"));
			if (destPartner == null) {
				searchBean.setDestPartnerID(new Long("-1"));
			} else {
				searchBean.setDestPartnerID(destPartner.getID());
			}
		}
		if (StringUtils.isNotBlank(request.getParameter("sourceMDN"))) {
			searchBean.setSourceMDN(request.getParameter("sourceMDN"));
		}
		if (StringUtils.isNotBlank(request.getParameter("destMDN"))) {
			searchBean.setDestMDN(request.getParameter("destMDN"));
		}
		if (StringUtils.isNotBlank(request.getParameter("linkStatus"))) {
			String linkStatus = request.getParameter("linkStatus");		
			if (linkStatus.equals("successful")) {
				searchBean.setStatusList(MonitorPeriodConstants.SUCCESSFUL_SCTL);
			} else if (linkStatus.equals("failed")) {
				searchBean.setStatusList(MonitorPeriodConstants.FAILED_SCTL);
			} else if (linkStatus.equals("pending")) {
				searchBean.setStatusList(MonitorPeriodConstants.PENDING_SCTL);
			} else if (linkStatus.equals("processing")) {
				searchBean.setStatusList(MonitorPeriodConstants.PROCESSING_SCTL);
			} else if (linkStatus.equals("reversals")) {
				searchBean.setStatusList(MonitorPeriodConstants.REVERSALS_SCTL);
			} else if (linkStatus.equals("intermediate")) {
				searchBean.setStatusList(MonitorPeriodConstants.INTERMEDIATE_SCTL);
			} else if (linkStatus.equals("intermediate")) {
				searchBean.setStatusList(MonitorPeriodConstants.COUNT_SCTL);
			}
		}
		if (StringUtils.isNotBlank(request.getParameter("status"))) {
			searchBean
					.setStatus(Integer.valueOf(request.getParameter("status")));
		}
		if (StringUtils.isNotBlank(request.getParameter("linkChannel"))) {
			searchBean.setSourceChannelApplication(Integer.valueOf(request
					.getParameter("linkChannel")));
		}
		if (StringUtils.isNotBlank(request.getParameter("linkServiceID"))) {
			searchBean.setServiceID(Long.valueOf(request
					.getParameter("linkServiceID")));
		}
		if (StringUtils.isNotBlank(request.getParameter("linkTxnID"))) {
			searchBean.setTransactionTypeID(Long.valueOf(request.getParameter("linkTxnID")));
		}
		if (StringUtils.isNotBlank(request.getParameter("billerCode"))) {
			searchBean.setMFSBillerCode(request.getParameter("billerCode"));
		}
		if (StringUtils.isNotBlank(request.getParameter("start"))) {
			searchBean.setStart(Integer.valueOf(request.getParameter("start")));
		}
		if (StringUtils.isNotBlank(request.getParameter("limit"))) {
			searchBean.setLimit(Integer.valueOf(request.getParameter("limit")));
		}
		return searchBean;
	}

	/**
	 * Builds a searchBean with all the params from request object
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return searchBean FloatWalletTransaction
	 */
	private FloatWalletTransaction buildFloatWalletSearchBean(
			HttpServletRequest request) {
		FloatWalletTransaction searchBean = new FloatWalletTransaction();
		// set global pocket (Float wallet) id as sourceDestPocketID
		SystemParametersServiceImpl systemParametersServiceImpl = new SystemParametersServiceImpl();
		searchBean.setSourceDestnPocketID(systemParametersServiceImpl
				.getLong(SystemParameterKeys.GLOBAL_SVA_POCKET_ID_KEY));
		if (StringUtils.isNotBlank(request.getParameter("startDate"))) {
			Date startDate = null;
			try {
				startDate = dateFormat.parse(request.getParameter("startDate"));
			} catch (ParseException e) {
				log.error(e);
			}
			searchBean.setCreateTimeGE(startDate);
		}
		if (StringUtils.isNotBlank(request.getParameter("endDate"))) {
			Date endDate = null;
			try {
				endDate = dateFormat.parse(request.getParameter("endDate"));
			} catch (ParseException e) {
				log.error(e);
			}
			searchBean.setCreateTimeLT(endDate);
		}
		if (StringUtils.isNotBlank(request.getParameter("start"))) {
			searchBean.setStart(Integer.valueOf(request.getParameter("start")));
		}
		if (StringUtils.isNotBlank(request.getParameter("limit"))) {
			searchBean.setLimit(Integer.valueOf(request.getParameter("limit")));
		}
		return searchBean;
	}

	/**
	 * Calculates the start date to apply for search criteria based on
	 * monitorPeriod value
	 * 
	 * @param monitorPeriod
	 *            String
	 * @return
	 */
	private Date calculateLastUpdateTimeGE(String monitorPeriod) {
		Date presentDate = Calendar.getInstance().getTime();
		Date lastUpdateTimeGE = null;
		if (MonitorPeriodConstants.LAST_15_MIN.equals(monitorPeriod)) {
			lastUpdateTimeGE = DateTimeUtil.addMinutes(presentDate, -15);
		} else if (MonitorPeriodConstants.LAST_1_HOUR.equals(monitorPeriod)) {
			lastUpdateTimeGE = DateTimeUtil.addHours(presentDate, -1);
		} else if (MonitorPeriodConstants.LAST_5_HOUR.equals(monitorPeriod)) {
			lastUpdateTimeGE = DateTimeUtil.addHours(presentDate, -5);
		} else if (MonitorPeriodConstants.LAST_24_HOUR.equals(monitorPeriod)) {
			lastUpdateTimeGE = DateTimeUtil.addDays(presentDate, -1);
		} else if (MonitorPeriodConstants.LAST_1_WEEK.equals(monitorPeriod)) {
			lastUpdateTimeGE = DateTimeUtil.addDays(presentDate, -7);
		} else if (MonitorPeriodConstants.LAST_1_MONTH.equals(monitorPeriod)) {
			lastUpdateTimeGE = DateTimeUtil.addDays(presentDate, -30);
		}
		return lastUpdateTimeGE;
	}
	private Date getTimeZoneBasedDateTimeGE(Date monitoringDate) {
		
		return new Date(monitoringDate.getTime() - TimeZone.getDefault().getRawOffset());
	}
}
