/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.mfino.constants.GeneralConstants;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.EnumTextService;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author sunil
 */
public class SettlementReport extends OfflineReportBase {

    private CommodityTransferDAO commodityTransferDAO = null;
    private PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
    private SubscriberDAO subDAO = DAOFactory.getInstance().getSubscriberDAO();
    private HashMap<Integer, String> channelcodes;
 //   private SubscriberMDNDAO subMdnDAO = new SubscriberMDNDAO();
    private static final int NUM_COLUMNS = 31;
    private static final String HEADER_ROW = "#,Reference number,Status,Date/Time,Type, Bucket Type, Distributor/Agent ID,Source MDN,Source pocket,Destination MDN," +
                    "Destination pocket,Source Beginning Balance,Paid Amount,Commission,Value Amount,Source Ending Balance,Destination Beginning Balance," +
                    "Destination Ending Balance,Currency,Channel Name, Operator RRN, Source Region Code, Destination Region Code, Source Company Code, " +
                    "Destination Company Code, Source Pocket ID, Destination Pocket ID, Source Pocket Status, Destination Pocket Status, Bank Code, ISO8583_Variant";
    @Inject
    public SettlementReport(CommodityTransferDAO ctDAO) {
        commodityTransferDAO = ctDAO;
    }

    @Override
    public File run(Date start, Date end) {
        return run(start, end, null);
    }

    @Override
    public File run(Date start, Date end, Long companyID) {

        File reportFile = null;
        try {
            HibernateUtil.getCurrentSession().beginTransaction();
            channelcodes = ChannelCodeService.getChannelCodeMap();
            CommodityTransferQuery query = new CommodityTransferQuery();
            query.setTransferStatus(CmFinoFIX.TransferStatus_Completed);

            //query.setTransferFailureReason(CmFinoFIX.TransferFailureReason_CompletedSuccessfuly);
            query.setStartTimeGE(start);
            query.setStartTimeLT(end);
            query.setHasCSRAction(false);
            if (companyID != null) {
                CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
                Company company = companyDao.getById(companyID);
                if (company != null) {
                    query.setCompany(company);
                }
                reportFile = getReportFilePath(company);
            }
            else {
                reportFile = getReportFilePath();
            }
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
            List<CommodityTransfer> results = commodityTransferDAO.get(query);
            int seq = 1;
            BigDecimal actualAmountPaid = BigDecimal.ZERO;
            BigDecimal sourcePocketBalance = BigDecimal.ZERO;
            BigDecimal destinationPocketBalance = BigDecimal.ZERO;
            BigDecimal commission = BigDecimal.ZERO;
            String transferStatusText = StringUtils.EMPTY;
            String transactionTypeText = StringUtils.EMPTY;
            String sourcePocketTypeText = StringUtils.EMPTY;
            String destinationPocketTypeText = StringUtils.EMPTY;

            Long agentID;

            writer.println(HEADER_ROW);

            DateFormat df = getDateFormat();
            for (CommodityTransfer ct : results) {
            	if(ct!=null && CmFinoFIX.SourceApplication_BankChannel.equals(ct.getSourceApplication()) && CmFinoFIX.ResponseCode_Success.equals(ct.getOperatorReversalResponseCode())){
//   smart #725             	transferStatusText ="Reversed";
                	continue;
                }
                transactionTypeText = StringUtils.EMPTY;
                transferStatusText = StringUtils.EMPTY;
                sourcePocketTypeText = StringUtils.EMPTY;
                destinationPocketTypeText = StringUtils.EMPTY;
                commission = BigDecimal.ZERO;

                actualAmountPaid = BigDecimal.ZERO;
                sourcePocketBalance = BigDecimal.ZERO;
                destinationPocketBalance = BigDecimal.ZERO;
                boolean isSourceEndingBalanceValid = true;
                boolean showAgentID = false;
                agentID = null;
                String sourceRegionCode = GeneralConstants.EMPTY_STRING;
                String destinationRegionCode = GeneralConstants.EMPTY_STRING;
                Integer sourceCompanyCode = null;
                Integer destinationCompanyCode = null;
                Long sourcePocketId = null;
                Long destinationPocketId = null;
                Integer sourcePocketStatus = null;
                Integer destinationPocketStatus = null;
                if (ct.getLOP() != null) {
                    actualAmountPaid = ct.getLOP().getActualAmountPaid();
//                    commission = ct.getAmount() - actualAmountPaid;
                    commission = ct.getAmount().subtract(actualAmountPaid);
                }
//                if (ct.getDistributionChainLevelByDCTLevelID() != null) {
//                    if(ct.getDistributionChainLevelByDCTLevelID().getCommission() != null)
//                        commission = ct.getDistributionChainLevelByDCTLevelID().getCommission();
//                } else {
//                    commission = BigDecimal.ZERO;
//                }
                if (ct.getSubscriberBySourceSubscriberID() != null && ct.getSubscriberBySourceSubscriberID().getMerchant() != null) {
                    agentID = ct.getSubscriberBySourceSubscriberID().getMerchant().getID();
                    if (OfflineReportUtil.isMerchantTxn(ct.getUICategory())) {
                        if (ct.getSubscriberBySourceSubscriberID().getMerchant().getRegion() != null) {
                            sourceRegionCode = ct.getSubscriberBySourceSubscriberID().getMerchant().getRegion().getRegionCode();
                        }
                    }
                }

                if (ct.getSubscriberBySourceSubscriberID() != null && ct.getSubscriberBySourceSubscriberID().getCompany() != null) {
                    sourceCompanyCode = ct.getSubscriberBySourceSubscriberID().getCompany().getCompanyCode();
                }

                if (ct.getDestSubscriberID() != null) {
                    Long destSubID = ct.getDestSubscriberID();
                    Subscriber destSubscriber = subDAO.getById(destSubID);
                    if (destSubscriber != null) {
                        if (CmFinoFIX.TransactionUICategory_MA_Transfer.equals(ct.getUICategory()) ||
                                CmFinoFIX.TransactionUICategory_BulkTransfer.equals(ct.getUICategory()) ||
                                CmFinoFIX.TransactionUICategory_Distribute_LOP.equals(ct.getUICategory())) {
                            if (destSubscriber.getMerchant() != null && destSubscriber.getMerchant().getRegion() != null) {
                                destinationRegionCode = destSubscriber.getMerchant().getRegion().getRegionCode();
                            }
                        }
                        if (destSubscriber.getCompany() != null) {
                            destinationCompanyCode = destSubscriber.getCompany().getCompanyCode();
                        }
                    }
                }
//                SubscriberMDN subMdn = subMdnDAO.getByMDN(ct.getDestMDN());
//                if (subMdn != null) {
//                    destinationCompanyCode = subMdn.getSubscriber().getCompany().getCompanyCode();
//                    if (subMdn.getSubscriber().getMerchant() != null) {
//                        destinationRegionCode = subMdn.getSubscriber().getMerchant().getRegion().getRegionCode();
//                    }
//                }
                if (ct.getSourcePocketBalance() != null && ct.getSourcePocketBalance().compareTo(BigDecimal.ZERO) > 0) {
                    sourcePocketBalance = ct.getSourcePocketBalance();
                }

                if (ct.getDestPocketBalance() != null && ct.getDestPocketBalance().compareTo(BigDecimal.ZERO) > 0) {
                    destinationPocketBalance = ct.getDestPocketBalance();
                }

//                if (ct.getTransferStatus().equals(CmFinoFIX.TransferStatus_Completed)){// &&
//             //           ct.getTransferFailureReason().equals(CmFinoFIX.TransferFailureReason_CompletedSuccessfuly)) {
//                    transferStatusText = "Successful"; //enumService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason, null, ct.getTransferFailureReason());
//                } else {
//                    transferStatusText = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, null, CmFinoFIX.TransferStatus_Failed);
//                }

                transferStatusText = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, null, ct.getTransferStatus());

                transactionTypeText = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory());
                sourcePocketTypeText = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SourcePocketType, null, ct.getSourcePocketType());
                destinationPocketTypeText = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_DestPocketType, null, ct.getDestPocketType());
                if (StringUtils.isEmpty(destinationPocketTypeText)) {
                    destinationPocketTypeText = OfflineReportUtil.getDestinationPocketType(ct.getUICategory());
                }
                Pocket pocket = ct.getPocketBySourcePocketID();
                sourcePocketId = pocket.getID();
                sourcePocketStatus = pocket.getStatus();
                if (ct.getDestPocketID() != null) {
                    Pocket destPocket = pocketDAO.getById(ct.getDestPocketID());
                    if (destPocket != null) {
                        destinationPocketId = destPocket.getID();
                        destinationPocketStatus = destPocket.getStatus();
                    }
                }
                Integer uiCat = ct.getUICategory();
                if(CmFinoFIX.TransactionUICategory_Distribute_LOP.equals(uiCat) || CmFinoFIX.TransactionUICategory_Bank_Channel_Payment.equals(uiCat)
                       || CmFinoFIX.TransactionUICategory_Bank_Channel_Topup.equals(uiCat) || CmFinoFIX.PocketType_BankAccount.equals(ct.getSourcePocketType())
                       || CmFinoFIX.TransactionUICategory_Shareload.equals(uiCat)) {
                    isSourceEndingBalanceValid = false;
                }

                showAgentID = OfflineReportUtil.isMerchantTxn(uiCat);
                writer.println(String.format(getFormatString(NUM_COLUMNS),
                        seq,
                        ct.getID(),
                        transferStatusText,
                        df.format(ct.getLastUpdateTime()),
                        transactionTypeText,
                        ct.getBucketType() != null ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_BucketType,null,ct.getBucketType()) : StringUtils.EMPTY,
                        (showAgentID) ? agentID : StringUtils.EMPTY,
                        OfflineReportUtil.stripRx(ct.getSourceMDN()),
                        sourcePocketTypeText,
                        OfflineReportUtil.stripRx(ct.getDestMDN()),
                        destinationPocketTypeText,
                        sourcePocketBalance,
                        (actualAmountPaid.compareTo(BigDecimal.ZERO) == 0) ? ct.getAmount() : actualAmountPaid,
                        commission,
                        ct.getAmount(),
//                        (isSourceEndingBalanceValid) ? sourcePocketBalance - ct.getAmount() : 0,
                        (isSourceEndingBalanceValid) ? sourcePocketBalance.subtract(ct.getAmount()) : 0,
                        destinationPocketBalance,
//                        destinationPocketBalance + ct.getAmount(),
                        destinationPocketBalance.add(ct.getAmount()),
                        ct.getCurrency(),
                        channelcodes.get(ct.getSourceApplication()),
                        StringUtils.isNotBlank(ct.getOperatorRRN())? ct.getOperatorRRN() : StringUtils.EMPTY,
                        sourceRegionCode,
                        destinationRegionCode,
                        (sourceCompanyCode != null) ? sourceCompanyCode : StringUtils.EMPTY,
                        (destinationCompanyCode != null) ? destinationCompanyCode : StringUtils.EMPTY,
                        (sourcePocketId != null) ? sourcePocketId : StringUtils.EMPTY,
                        (destinationPocketId != null) ? destinationPocketId : StringUtils.EMPTY,
                        (sourcePocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null, sourcePocketStatus) : StringUtils.EMPTY,
                        (destinationPocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null, destinationPocketStatus) : StringUtils.EMPTY,
                        (ct.getBankCode() != null) ? ct.getBankCode() : StringUtils.EMPTY,
                        EnumTextService.getEnumTextValue(CmFinoFIX.TagID_ISO8583_Variant, null, ct.getISO8583_Variant())
                        ));
                seq++;
            }
            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        } catch (Throwable t) {
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in SettlementReport", t);
        }
        return reportFile;
    }

    @Override
    public String getReportName() {
        return "SettlementReport";
    }
}

