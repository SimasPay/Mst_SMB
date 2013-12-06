package com.mfino.report.generalreports;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.TransactionAmountDistributionLogDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Partner;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionAmountDistributionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.util.ReportUtil;

public class AgentSalesCommisionReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 14;
	private String reportName;
	private PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
	private TransactionTypeDAO transactionTypeDAO = DAOFactory.getInstance().getTransactionTypeDAO();
	private TransactionAmountDistributionLogDAO tadlDao = DAOFactory.getInstance().getTransactionAmountDistributionLogDAO();
	private SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();

	private HashMap<Long, Integer> totalTransaction = new HashMap<Long, Integer>();
	private HashMap<Long, Integer> cashinTransaction = new HashMap<Long, Integer>();
	private HashMap<Long, Integer> cashoutTransaction = new HashMap<Long, Integer>();
	private HashMap<Long, Integer> billPaymentTransaction = new HashMap<Long, Integer>();
	private HashMap<Long, BigDecimal> cashinAmount = new HashMap<Long, BigDecimal>();
	private HashMap<Long, BigDecimal> cashoutAmount = new HashMap<Long, BigDecimal>();
	private HashMap<Long, BigDecimal> serviceCharge = new HashMap<Long, BigDecimal>();
	private HashMap<Long, BigDecimal> billPaymentAmount = new HashMap<Long, BigDecimal>();
	private HashMap<Long, BigDecimal> taxAmount = new HashMap<Long, BigDecimal>();

	private String HEADER_ROW = "# ,Agent Code , MDN, Agent Name, Total transactions , Cash-in Transactions , Cash-in Amount"
			+ ", Cash-Out Transactions, Cash-Out Amount, Bill Payments, Bill Payment amount, Registrations Done, Total  Service charge earned, Total WHT Paid ";

	private File reportFile;

	public AgentSalesCommisionReport() {
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData basedata) {
		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);

		DateFormat df = getDateFormat();
		String formatStr = getFormatString(NUM_COLUMNS);
		String startTime =  df.format(new Date());
		log.info("Processing "+reportName+" StartTime:"+startTime);

		try {
			//		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
			//		writer.println(HEADER_ROW);

			XLSReport xlsReport = new XLSReport(reportName, end);

			//adding logo
			try{
				xlsReport.addLogo();
			}catch (Exception e) {
				log.error("Failed to load logo",e);
			}

			xlsReport.addMergedRegion();

			xlsReport.addReportTitle(reportName);
			xlsReport.addHeaderRow(HEADER_ROW);

			ReportBaseData data = basedata;
			if (data == null) {
				data = new ReportBaseData();
				data.intializeStaticData();
				data.getServiceTransactionLogs(start, end);
				data.getCommodityTransactions(start, end);
			}

			PartnerQuery query = new PartnerQuery();
			query.setPartnerType(CmFinoFIX.TagID_BusinessPartnerTypeAgent);
			query.setUpgradeStateSearch(CmFinoFIX.UpgradeState_Approved);
			List<Partner> agents = partnerDao.get(query);

			List<Long> transactionTypeIds = new ArrayList<Long>();
			transactionTypeIds.add(transactionTypeDAO.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_CASHIN).getID());
			transactionTypeIds.add(transactionTypeDAO.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT).getID());
			transactionTypeIds.add(transactionTypeDAO.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY).getID());

			getCounts(data, agents, transactionTypeIds);

			int seq = 1;
			for (Partner agent : agents) {
				String rowContent = String.format(
						formatStr,
						seq,
						agent.getPartnerCode(),
						agent.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next().getMDN(),
						agent.getTradeName(),
						totalTransaction.get(agent.getID()) != null ? totalTransaction.get(agent.getID()) : 0,
						cashinTransaction.get(agent.getID()) != null ? cashinTransaction.get(agent.getID()) : 0,
						cashinAmount.get(agent.getID()) != null ? cashinAmount.get(agent.getID()) : 0,
						cashoutTransaction.get(agent.getID()) != null ? cashoutTransaction.get(agent.getID()) : 0,
						cashoutAmount.get(agent.getID()) != null ? cashoutAmount.get(agent.getID()) : 0,
						billPaymentTransaction.get(agent.getID()) != null ? billPaymentTransaction.get(agent.getID()) : 0,
						billPaymentAmount.get(agent.getID()) != null ? billPaymentAmount.get(agent.getID()) : 0,
						subscriberDAO.getSubscribersByRegisteringPartneId(agent.getID(),start,end),								
						serviceCharge.get(agent.getID()) != null ? serviceCharge.get(agent.getID()) : 0,
						taxAmount.get(agent.getID()) != null ? taxAmount.get(agent.getID()) : 0);

				xlsReport.addRowContent(rowContent); 				
				seq++;

			}

			xlsReport.writeToFileStream(reportFile, HEADER_ROW, reportName);
	
		} catch (Exception e) {
			 log.error("Error in "+reportName+" :",e);
		} 
		log.info("Processing "+reportName+" StartTime:"+startTime+"  EndTime:"+ df.format(new Date()));
		return reportFile;
	}

	
	private void getCounts(ReportBaseData data, List<Partner> agents,
			List<Long> transactionTypeIds) {
		List<ServiceChargeTransactionLog> sctlogs = data.getServiceChargeTransactionLogs();
		Map<Long, CommodityTransfer> ctMap = data.getCtMap();
		for (ServiceChargeTransactionLog sctl : sctlogs) {
			CommodityTransfer ct = sctl.getCommodityTransferID() != null ? ctMap.get(sctl.getCommodityTransferID()) : null;

			List<TransactionAmountDistributionLog> tadlogs = tadlDao.getLogEntriesBySCTLID(sctl.getID());

			if (sctl.getTransactionTypeID().equals(transactionTypeIds.get(0))) {
				if (totalTransaction.containsKey(sctl.getSourcePartnerID())) {
					totalTransaction.put(sctl.getSourcePartnerID(), totalTransaction.get(sctl.getSourcePartnerID()) + 1);
				} else {
					totalTransaction.put(sctl.getSourcePartnerID(), 1);
				}

				if (cashinTransaction.containsKey(sctl.getSourcePartnerID())) {
					cashinTransaction.put(sctl.getSourcePartnerID(), cashinTransaction.get(sctl.getSourcePartnerID()) + 1);
				} else {
					cashinTransaction.put(sctl.getSourcePartnerID(), 1);
				}
				if (ct != null
						&& CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())) {
					if (cashinAmount.containsKey(sctl.getSourcePartnerID())) {
						cashinAmount.put(sctl.getSourcePartnerID(),cashinAmount.get(sctl.getSourcePartnerID()).add(sctl.getTransactionAmount()));
					} else {
						cashinAmount.put(sctl.getSourcePartnerID(),sctl.getTransactionAmount());
					}
				}
				if (tadlogs != null) {
					for (TransactionAmountDistributionLog tadl : tadlogs) {
						if (tadl.getPartner().getID().equals(sctl.getSourcePartnerID())) {
							if (serviceCharge.containsKey(sctl.getSourcePartnerID())) {
								serviceCharge.put(sctl.getSourcePartnerID(),serviceCharge.get(sctl.getSourcePartnerID()).add(tadl.getShareAmount()));
							} else {
								serviceCharge.put(sctl.getSourcePartnerID(),tadl.getShareAmount());
							}
						}
						if(tadl.getTaxAmount().compareTo(BigDecimal.ZERO)!=0){
							if(taxAmount.containsKey(tadl.getPartner().getID())){
								taxAmount.put(tadl.getPartner().getID(), taxAmount.get(tadl.getPartner().getID()).add(tadl.getTaxAmount()));
							}else{
								taxAmount.put(tadl.getPartner().getID(), tadl.getTaxAmount());
							}
						}
					}
				}
			} else if (sctl.getTransactionTypeID().equals(transactionTypeIds.get(1))) {
				if (totalTransaction.containsKey(sctl.getDestPartnerID())) {
					totalTransaction.put(sctl.getDestPartnerID(),totalTransaction.get(sctl.getDestPartnerID()) + 1);
				} else {
					totalTransaction.put(sctl.getDestPartnerID(), 1);
				}

				if (cashoutTransaction.containsKey(sctl.getDestPartnerID())) {
					cashoutTransaction.put(sctl.getDestPartnerID(), cashoutTransaction.get(sctl.getDestPartnerID()) + 1);
				} else {
					cashoutTransaction.put(sctl.getDestPartnerID(), 1);
				}
				if (ct != null
						&& CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())) {
					if (cashoutAmount.containsKey(sctl.getDestPartnerID())) {
						cashoutAmount.put(sctl.getDestPartnerID(),cashoutAmount.get(sctl.getDestPartnerID()).add(sctl.getTransactionAmount()));
					} else {
						cashoutAmount.put(sctl.getDestPartnerID(),sctl.getTransactionAmount());
					}
				}
				if (tadlogs != null) {
					for (TransactionAmountDistributionLog tadl : tadlogs) {
						if (tadl.getPartner().getID().equals(sctl.getDestPartnerID())) {
							if (serviceCharge.containsKey(sctl.getDestPartnerID())) {
								serviceCharge.put(sctl.getDestPartnerID(),serviceCharge.get(sctl.getDestPartnerID()).add(tadl.getShareAmount()));
							} else {
								serviceCharge.put(sctl.getDestPartnerID(),tadl.getShareAmount());
							}
						}
						if(tadl.getTaxAmount().compareTo(BigDecimal.ZERO)!=0){
							if(taxAmount.containsKey(tadl.getPartner().getID())){
								taxAmount.put(tadl.getPartner().getID(), taxAmount.get(tadl.getPartner().getID()).add(tadl.getTaxAmount()));
							}else{
								taxAmount.put(tadl.getPartner().getID(), tadl.getTaxAmount());
							}
						}
					}

				}
			} else if (sctl.getTransactionTypeID().equals(transactionTypeIds.get(2))) {
				if (totalTransaction.containsKey(sctl.getDestPartnerID())) {
					totalTransaction.put(sctl.getDestPartnerID(),totalTransaction.get(sctl.getDestPartnerID()) + 1);
				} else {
					totalTransaction.put(sctl.getDestPartnerID(), 1);
				}

				if (billPaymentTransaction.containsKey(sctl.getDestPartnerID())) {
					billPaymentTransaction.put(sctl.getDestPartnerID(), billPaymentTransaction.get(sctl.getDestPartnerID()) + 1);
				} else {
					billPaymentTransaction.put(sctl.getDestPartnerID(), 1);
				}
				if (ct != null
						&& CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())) {
					if (billPaymentAmount.containsKey(sctl.getDestPartnerID())) {
						billPaymentAmount.put(sctl.getDestPartnerID(),billPaymentAmount.get(sctl.getDestPartnerID()).add(sctl.getTransactionAmount()));
					} else {
						billPaymentAmount.put(sctl.getDestPartnerID(),sctl.getTransactionAmount());
					}
				}
				if (tadlogs != null) {
					for (TransactionAmountDistributionLog tadl : tadlogs) {
						if (tadl.getPartner().getID().equals(sctl.getDestPartnerID())) {
							if (serviceCharge.containsKey(sctl.getDestPartnerID())) {
								serviceCharge.put(sctl.getDestPartnerID(),serviceCharge.get(sctl.getDestPartnerID()).add(tadl.getShareAmount()));
							} else {
								serviceCharge.put(sctl.getDestPartnerID(),tadl.getShareAmount());
							}
						}
						if(tadl.getTaxAmount().compareTo(BigDecimal.ZERO)!=0){
							if(taxAmount.containsKey(tadl.getPartner().getID())){
								taxAmount.put(tadl.getPartner().getID(), taxAmount.get(tadl.getPartner().getID()).add(tadl.getTaxAmount()));
							}else{
								taxAmount.put(tadl.getPartner().getID(), tadl.getTaxAmount());
							}
						}
					}

				}
			}else{
				if (tadlogs != null) {
					for (TransactionAmountDistributionLog tadl : tadlogs) {
						if(tadl.getTaxAmount().compareTo(BigDecimal.ZERO)!=0){
							if(taxAmount.containsKey(tadl.getPartner().getID())){
								taxAmount.put(tadl.getPartner().getID(), taxAmount.get(tadl.getPartner().getID()).add(tadl.getTaxAmount()));
							}else{
								taxAmount.put(tadl.getPartner().getID(), tadl.getTaxAmount());
							}
						}
					}

				}
			}
		}

	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected List<File> runAndGetMutlipleReports(Date start, Date end,
			ReportBaseData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileName() {
		return reportFile.getName();
	}

	@Override
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

}
