/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.query.MerchantQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Merchant;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.service.DistributionChainTemplateService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class DistributionTreeReport extends OfflineReportBase {

    private MerchantDAO merchantDao = DAOFactory.getInstance().getMerchantDAO();
//    private DistributionChainLevelDAO levelDAO = new DistributionChainLevelDAO();
    private static final int NUM_COLUMNS = 17;
    private static final int NUM_LEVELS_REQUIRED = ConfigurationUtil.getDistributionTreeReportLevels();
    private String headerRow = StringUtils.EMPTY;
//    private static final String HEADER_ROW = "#,ID, Distributor Name, Time Zone,Channel Name,Level Number, Group ID, Contact Detail First Name, Contact Detail Last Name, MDN, Parent Distributor Name, Parent MDN No, Level 1 Name, Level 2 Name, Level 3 Name, Level 4 Name, Level 5 Name,"
//            + "Area, Status, Restrictions, Error Text";

    //Sid: Can we Inject multiple DAOs?
    @Inject
    public DistributionTreeReport(MerchantDAO merDAO) {
        merchantDao = merDAO;
    }

    @Override
    public File run(Date start, Date end) {
        return run(start, end, null);
    }

    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = null;
        
        headerRow = "#,ID, Distributor Name, Time Zone,Channel Name,Level Number, Group ID, Contact Detail First Name, Contact Detail Last Name, MDN, Parent Distributor Name, Parent MDN No,";
        for (int i = 1; i <= NUM_LEVELS_REQUIRED; i++) {
            headerRow = headerRow + "Level " + i + " Name,";
        }
        headerRow = headerRow + "Area, Status, Restrictions, Error Text";

        try {
            HibernateUtil.getCurrentSession().beginTransaction();

            //Get all active and pending retirement
            MerchantQuery query = new MerchantQuery();
            query.setMerchantStatusIn(new Integer[]{CmFinoFIX.SubscriberStatus_Initialized, CmFinoFIX.SubscriberStatus_Active, CmFinoFIX.SubscriberStatus_PendingRetirement});
            query.setEndRegistrationDate(end);
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
            List<Merchant> results = merchantDao.get(query);
//            writer.println(HEADER_ROW);
            writer.println(headerRow);
            int seq = 1;
            seq = reportForMerchantList(results, writer, seq, start, end);

            writer.close();
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        } catch (Throwable t) {
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in Merchant Report", t);
        }
        return reportFile;
    }

    @Override
    public String getReportName() {
        return "DistributionTreeReport";

    }

    private int reportForMerchantList(List<Merchant> results, PrintWriter writer, int seq, Date start, Date end) {
        //DateFormat df = getDateFormat();
        String formatStr = getFormatString(NUM_COLUMNS);
        List<String> errorMDNs = new ArrayList<String>();
        List<String> parents; //= new ArrayList<String>();   

        String mdn = StringUtils.EMPTY;
        String parentDistributorName = StringUtils.EMPTY;
        String parentMdn = StringUtils.EMPTY;
        String distributorName = StringUtils.EMPTY;
        String dctName = StringUtils.EMPTY;
        String errorText = StringUtils.EMPTY;
        int levelNumber = 0;
//            String brandName = StringUtils.EMPTY;
//        String parent_2, parent_3, parent_4, parent_5;
//        String level_1, level_2, level_3, level_4, level_5;

        for (Merchant merchant : results) {
            mdn = StringUtils.EMPTY;
            parentDistributorName = StringUtils.EMPTY;
            distributorName = StringUtils.EMPTY;
            dctName = StringUtils.EMPTY;
            levelNumber = 0;
            parentMdn = StringUtils.EMPTY;
//                brandName = StringUtils.EMPTY;
//            parent_2 = parent_3 = parent_4 = parent_5 = StringUtils.EMPTY;
//            level_1 = level_2 = level_3 = level_4 = level_5 = StringUtils.EMPTY;
            long[] dctIDAndLevel = new long[]{-1, -1};

           /* try {
                dctIDAndLevel = MerchantService.getDCTIDAndLevel(merchant.getID());
            } catch (Throwable ex) {
                log.error("Merchant ID = " + merchant.getID(), ex);
                errorMDNs.add(merchant.getID().toString());
                continue;
            }*/

            long templateID = dctIDAndLevel[0];
            levelNumber = (int) dctIDAndLevel[1];

            dctName = DistributionChainTemplateService.getName(templateID);

            Set<SubscriberMDN> mdnSet = merchant.getSubscriber().getSubscriberMDNFromSubscriberID();
            SubscriberMDN subcr = (SubscriberMDN) mdnSet.toArray()[0];
            mdn = subcr.getMDN();

            if (OfflineReportUtil.isSystemMDN(mdn)) {
                continue;
            }

//            DistributionChainLevelQuery dctQuery = new DistributionChainLevelQuery();
//            dctQuery.setDistributionChainTemplateID(templateID);
//            dctQuery.setLevel(new Integer(levelNumber));
//            List<DistributionChainLevel> dctLevels = levelDAO.get(dctQuery);
//            DistributionChainLevel dctLevel = null;
//            if (dctLevels != null && dctLevels.size() > 0) {
//                dctLevel = dctLevels.get(0);
//            }
            parents = new ArrayList<String>();

            if (merchant.getMerchantByParentID() != null) {
                Subscriber sub = merchant.getMerchantByParentID().getSubscriber();
                User user = sub.getUser();

                if (user != null) {
                    parentDistributorName = user.getUsername();
                    parents.add(parentDistributorName);
                }

                subcr = (SubscriberMDN) sub.getSubscriberMDNFromSubscriberID().toArray()[0];
                if (subcr != null) {
                    parentMdn = subcr.getMDN();
                }
            }

            if (merchant.getSubscriber().getUser() != null) {
                distributorName = merchant.getSubscriber().getUser().getUsername();
            }

            errorText = StringUtils.EMPTY;
            if (merchant.getDistributionChainTemplateID() != null && levelNumber > 1) {
                errorText = "Has DCT and Parent";
            }

            if (levelNumber > 1) {
                Merchant parent = merchant.getMerchantByParentID();
                Merchant p = null;
                for (int i = 1; i < levelNumber; i++) {
                    p = parent.getMerchantByParentID();
                    parents.add(p.getSubscriber().getUser().getUsername());
//                    if (levelNumber <= (i + 1)) {
//                        break;
//                    }
                    parent = p;
                }
//                Merchant p2 = parent.getMerchantByParentID();
//                parent_2 = p2.getSubscriber().getUser().getUsername();
//
//                if (levelNumber > 2) {
//                    Merchant p3 = p2.getMerchantByParentID();
//                    parent_3 = p3.getSubscriber().getUser().getUsername();
//
//                    if (levelNumber > 3) {
//                        Merchant p4 = p3.getMerchantByParentID();
//                        parent_4 = p4.getSubscriber().getUser().getUsername();
//
//                        if (levelNumber > 4) {
//                            Merchant p5 = p4.getMerchantByParentID();
//                            parent_5 = p5.getSubscriber().getUser().getUsername();
//                        } // level 4
//                    } // level 3
//                } // level 2
            } //level 1            
            int availableLevels = parents.size();
            int toLevel = 0;
            if (availableLevels > NUM_LEVELS_REQUIRED) {
                toLevel = availableLevels - NUM_LEVELS_REQUIRED;
            }
            String levelString = "";
            for (int i = availableLevels - 1; i >= toLevel; i--) {
                levelString = levelString + parents.get(i) + ",";
            }
            for (int i = availableLevels; i < NUM_LEVELS_REQUIRED; i++) {
                levelString = levelString + ",";
            }
            if (!levelString.equals("") && levelString.charAt(levelString.length() - 1) == ',') {
                levelString = levelString.substring(0, levelString.length() - 1);
            }

//            if(!levelString.equals("") && availableLevels + 1 != NUM_LEVELS_REQUIRED) {
//               
//            }
//            if (parent_5.length() > 0) {
//                level_1 = parent_5;
//                level_2 = parent_4;
//                level_3 = parent_3;
//                level_4 = parent_2;
//                level_5 = parentDistributorName;
//            } else if (parent_4.length() > 0) {
//                level_1 = parent_4;
//                level_2 = parent_3;
//                level_3 = parent_2;
//                level_4 = parentDistributorName;
//            } else if (parent_3.length() > 0) {
//                level_1 = parent_3;
//                level_2 = parent_2;
//                level_3 = parentDistributorName;
//            } else if (parent_2.length() > 0) {
//                level_1 = parent_2;
//                level_2 = parentDistributorName;
//            } else if (parentDistributorName.length() > 0) {
//                level_1 = parentDistributorName;
//            }

//                if(merchant.getSubscriber().getCompany() != null && merchant.getSubscriber().getCompany().getBrandFromCompanyID() != null &&
//                             merchant.getSubscriber().getCompany().getBrandFromCompanyID().size() > 0){
//                    Brand brand = (Brand) merchant.getSubscriber().getCompany().getBrandFromCompanyID().toArray()[0];
//                    brandName = brand.getBrandName();
//                }
            writer.println(String.format(formatStr,
                    seq,
                    merchant.getID(),
                    distributorName,
                    merchant.getSubscriber().getTimezone(),
                    dctName,
                    (levelNumber > -1) ? levelNumber + 1 : "", //Is there any use showing invalid level info?
                    merchant.getGroupID(),
                    merchant.getSubscriber().getFirstName(),
                    merchant.getSubscriber().getLastName(),
                    mdn,
                    parentDistributorName,
                    parentMdn,
                    levelString,
                    //                    level_1,
                    //                    level_2,
                    //                    level_3,
                    //                    level_4,
                    //                    level_5,
                    (merchant.getRegion() != null) ? merchant.getRegion().getRegionName() : "",
                    EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, null, merchant.getStatus()),
                    EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberRestrictions, null, merchant.getSubscriber().getRestrictions()),
                    errorText));
            seq++;
        }
        for (String erroMdn : errorMDNs) {
            writer.println(erroMdn);
        }
        return seq;
    }
}
