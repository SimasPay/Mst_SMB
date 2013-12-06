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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.LOPDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.LOPQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.LOP;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author sunil
 */
public class SettlementLOPReport extends OfflineReportBase {

    private LOPDAO lopDAO = null;
    private PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
    private SubscriberDAO subDAO = DAOFactory.getInstance().getSubscriberDAO();
   // private EnumTextService enumService = new EnumTextService();
    private static final int NUM_COLUMNS = 25;
    private static final String HEADER_ROW = "#,LOP ID,Reference No,Distributor Code,Distributor name,Distributor MDN,Contact Details First Name," +
                    "Contact Details Last Name,Beginning Balance,Amount Value,Ending Balance,Commission,Amount Paid,Transfer Date,Giro Ref No," +
                    "Creation time,Last Modified,Distributed By,Distributed Date,Approved By,Approval Date, " +
                    "Destination Company Code, Destination Region Code, Destination Pocket ID, Destination Pocket Status";
    @Inject
    public SettlementLOPReport(LOPDAO lpDAO) {
        lopDAO = lpDAO;
    }
    @Override
    public File run(Date start, Date end) {
        return run(start, end , null);
    }
    @Override
    public File run(Date start, Date end, Long companyID) {
        HibernateUtil.getCurrentSession().beginTransaction();
        File reportFile = null;
        try {
            LOPQuery query = new LOPQuery();
            query.setLopstatus(CmFinoFIX.LOPStatus_Distributed);
            query.setLastUpdateTimeGE(start);
            query.setLastUpdateTimeLT(end);
            if(companyID != null){
                CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
                Company company =companyDao.getById(companyID);
                if(company != null){
                    query.setCompany(company);
                }
                reportFile = getReportFilePath(company);
            }
            else {
                reportFile = getReportFilePath();
            }
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
            List<LOP> results = lopDAO.get(query);
            int seq = 1;
            String groupId = StringUtils.EMPTY;
            String subFirstName = StringUtils.EMPTY;
            String subLastName = StringUtils.EMPTY;
            String distributorName = StringUtils.EMPTY;
            String distributorMDN = StringUtils.EMPTY;
            BigDecimal commission = BigDecimal.ZERO;

            writer.println(HEADER_ROW);

            for (LOP lop : results) {
                groupId = StringUtils.EMPTY;
                subFirstName = StringUtils.EMPTY;
                subLastName = StringUtils.EMPTY;
                distributorName = StringUtils.EMPTY;
                commission = BigDecimal.ZERO;
                String destRegionCode=StringUtils.EMPTY;
                Long destinationPocketId = null;
                Integer destinationPocketStatus = null;
                Integer destCompanyCode = null;
                Set<CommodityTransfer> ct=lop.getCommodityTransferFromLOPID();
                Iterator<CommodityTransfer> iter=ct.iterator();
                CommodityTransfer ctRecord=iter.next();
//                commission = ctRecord.getAmount() - lop.getActualAmountPaid();
//                commission= ((commission*100)/ctRecord.getAmount());
                commission = ctRecord.getAmount().subtract(lop.getActualAmountPaid());
                commission= commission.multiply(new BigDecimal(100)).divide(ctRecord.getAmount());

//                Pocket pocket = ctRecord.getPocketBySourcePocketID();
                if(ctRecord.getDestPocketID()!= null) {
                    Pocket destPocket = pocketDAO.getById(ctRecord.getDestPocketID());
                    if(destPocket != null) {
                        destinationPocketId = destPocket.getID();
                        destinationPocketStatus = destPocket.getStatus();
                    }
                }
                if (ctRecord.getDestSubscriberID() != null) {
                    Long destSubID = ctRecord.getDestSubscriberID();
                    Subscriber destSubscriber = subDAO.getById(destSubID);
                    if (destSubscriber != null) {
                        if (destSubscriber.getMerchant() != null && destSubscriber.getMerchant().getRegion() != null) {
                            destRegionCode = destSubscriber.getMerchant().getRegion().getRegionCode();
                        }
                        if (destSubscriber.getCompany() != null) {
                            destCompanyCode = destSubscriber.getCompany().getCompanyCode();
                        }
                    }
                }
                if (lop.getSubscriberMDNByMDNID() != null) {
                    if (lop.getSubscriberMDNByMDNID().getSubscriber().getMerchant() != null) {
                        if (lop.getSubscriberMDNByMDNID().getSubscriber().getMerchant().getGroupID() != null) {
                            groupId = lop.getSubscriberMDNByMDNID().getSubscriber().getMerchant().getGroupID();
                        }
                    }
                    distributorName= lop.getSubscriberMDNByMDNID().getSubscriber().getUser().getUsername();
                    distributorMDN= lop.getSubscriberMDNByMDNID().getMDN();
                    if (lop.getSubscriberMDNByMDNID().getSubscriber().getFirstName() != null) {
                        subFirstName = lop.getSubscriberMDNByMDNID().getSubscriber().getFirstName();
                    }
                    if (lop.getSubscriberMDNByMDNID().getSubscriber().getLastName() != null) {
                        subLastName = lop.getSubscriberMDNByMDNID().getSubscriber().getLastName();
                    }
                }

                DateFormat df = getDateFormat();

                writer.println(String.format(getFormatString(NUM_COLUMNS),
                        seq,
                        lop.getID(),
                        ctRecord.getID(),
                        groupId,
                        distributorName,
                        OfflineReportUtil.stripRx(distributorMDN),
                        subFirstName,
                        subLastName,
                        ctRecord.getDestPocketBalance(),
                        lop.getAmountDistributed(),
//                        ctRecord.getDestPocketBalance()+lop.getAmountDistributed(),
                        ctRecord.getDestPocketBalance().add(lop.getAmountDistributed()),                        
                        commission,
                        lop.getActualAmountPaid(),
                        lop.getTransferDate() != null ? df.format(df.parse(lop.getTransferDate())) : StringUtils.EMPTY,
                        lop.getGiroRefID(),
                        df.format(lop.getCreateTime()),
                        df.format(lop.getLastUpdateTime()),
                        lop.getDistributedBy(),
                        df.format(lop.getDistributeTime()),
                        lop.getApprovedBy(),
                        df.format(lop.getApprovalTime()),
                        (destCompanyCode != null) ? destCompanyCode : StringUtils.EMPTY,
                        destRegionCode,
                        (destinationPocketId != null)? destinationPocketId : StringUtils.EMPTY,
                        (destinationPocketStatus != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null,destinationPocketStatus) : StringUtils.EMPTY
                        ));
                seq++;
            }
            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        }catch(Throwable t){
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in SettlementLOPReport", t);
        }
        return reportFile;
    }

    @Override
    public String getReportName() {
        return "SettlementLOPReport";

    }
}