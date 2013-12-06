/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.BrandDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.RegionDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.BrandQuery;
import com.mfino.dao.query.MerchantQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.Brand;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.Merchant;
import com.mfino.domain.MerchantSyncRecord;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Region;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSMerchant;
import com.mfino.fix.CmFinoFIX.CMJSMerchant.CGEntries;
import com.mfino.fix.CmFinoFIX.CMJSParentGroupIdCheck;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberMDN;
import com.mfino.fix.processor.MerchantProcessor;
import com.mfino.fix.processor.ParentGroupIDCheckProcessor;
import com.mfino.uicore.fix.processor.SubscriberMdnProcessor;
import com.mfino.uicore.service.DistributionChainLevelService;

/**
 *
 * @author sandeepjs
 */
public class MerchantService {
	private static BigDecimal ZERO = new BigDecimal(0);
    private static MerchantDAO merchantDAO = DAOFactory.getInstance().getMerchantDAO();
    private static Logger log = LoggerFactory.getLogger(MerchantService.class);
    //To check if the user name is valid
    private static final Pattern p = Pattern.compile("^[a-zA-Z][-_.a-zA-Z0-9]{4,30}$");
    private static final Pattern email = Pattern.compile("^[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$");
    private static final Pattern mdn = Pattern.compile("^(62)[0-9]{3,14}$");
    /*
     *
     * This method is to check if the User with MDN is same as the user logged in.
     *
     */

    public static boolean isSelf(String MDN) {
        boolean retValBoolean = false;

        MerchantDAO merchantDAO = DAOFactory.getInstance().getMerchantDAO();
        MerchantQuery query = new MerchantQuery();
        query.setExactMDN(MDN);
        List<Merchant> results = merchantDAO.getByHQL(query);

        Merchant merchant = (Merchant) results.get(0);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        if (userName.equals(merchant.getSubscriber().getUser().getUsername())) {
            retValBoolean = true;
        }

        return retValBoolean;
    }

    public static Long getMerchantIDOfLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        MerchantDAO mdao = DAOFactory.getInstance().getMerchantDAO();
        MerchantQuery query = new MerchantQuery();
        query.setExactUser(userName);
        List<Merchant> results = mdao.getByHQL(query);

        if (results == null || results.size() == 0) {
            return -1l;
        }

        Merchant merchant = (Merchant) results.get(0);
        return merchant.getID();
    }

    public static Merchant getMerchantFromMDN(String merchantMDN) {

        MerchantDAO mdao = DAOFactory.getInstance().getMerchantDAO();
        MerchantQuery query = new MerchantQuery();
        query.setExactMDN(merchantMDN);
        List<Merchant> results = mdao.getByHQL(query);

        if (results == null || results.size() == 0) {
            return null;
        }

        Merchant merchant = (Merchant) results.get(0);
        return merchant;

    }

    public static Boolean checkMDNRestrictions(Merchant merchant) {

        if (merchant == null) {
            return false;
        }
        if (!((merchant.getSubscriber().getRestrictions()).equals(CmFinoFIX.SubscriberRestrictions_None))) {
            return false;
        }
        if (!(merchant.getStatus().equals(CmFinoFIX.SubscriberStatus_Active) || merchant.getStatus().equals(CmFinoFIX.SubscriberStatus_Initialized))) {
            return false;
        }
        return true;

    }

    public static boolean isDecendentOfLoggedInMerchant(Long merchantId) {
        Long loggedInMerchantId = getMerchantIDOfLoggedInUser();
        if (merchantId.longValue() == loggedInMerchantId.longValue()) {
            return true;
        }

        MerchantDAO merchantDAO = DAOFactory.getInstance().getMerchantDAO();
        Long idToUse = merchantId;

        while (true) {
            MerchantQuery merchantQuery = new MerchantQuery();
            merchantQuery.setId(idToUse);

            List<Merchant> merchantsList = merchantDAO.getByHQL(merchantQuery);
            if (null == merchantsList || 0 == merchantsList.size()) {
                break;
            }

            Merchant eachMerchant = merchantsList.get(0);

            if (null == eachMerchant) {
                break;
            }

            Long parentId = null;
            if (eachMerchant.getMerchantByParentID() != null) {
                parentId = eachMerchant.getMerchantByParentID().getID();
            }

            if (null == parentId) {
                break;
            }

            if (loggedInMerchantId.longValue() == parentId.longValue()) {
                return true;
            }

            idToUse = parentId;
        }

        return false;

    }

//    public static String getApplicableDCT(Long merchantId) {
//        Merchant merchant = new MerchantDAO().getById(merchantId);
//        Long DCTID = -1l;
//
//        if(merchant == null)
//            return StringUtils.EMPTY;
//
//        while (true) {
//            Merchant parentMerchant = merchant.getMerchantByParentID();
//            if (parentMerchant != null) {
//                if(merchant.getDistributionChainTemplateID() != null) {
//                    DefaultLogger.warn("Inconsistent data encountered. Merchant(" + merchant.getID() + ") has both parent("
//                            + parentMerchant.getID() + ") and DCT(" + merchant.getDistributionChainTemplateID() + "). Ignoring the DCT.");
//                }
//                merchant = parentMerchant;
//            } else {
//                break;
//            }
//        }
//
//        if (merchant != null && merchant.getDistributionChainTemplateID() != null) {
//            DCTID = merchant.getDistributionChainTemplateID();
//        }
//
//        return DistributionChainTemplateService.getName(DCTID);
//    }
    /**
     * Only the top level merchants have a DCT.
     * So any merchant which has a parent shouldn't have a DCT. All the intermediate DCTs are ignored. 
     * @param merchantId
     * @return a number array. The first item is the DCTID and the second item is level.
     * @note The level information is valid only if the DCTID is valid
     */
    public static long[] getDCTIDAndLevel(Long merchantId) {

        long DCTID = -1;
        long level = 0;

        Merchant merchant = merchantDAO.getById(merchantId);

        if (merchant == null) {
            return new long[]{DCTID, (DCTID > 0) ? level : -1};
        }


        //required to identify cycles if any
        List<Long> path = new ArrayList<Long>();
        path.add(merchantId);

        while (true) {
            Merchant parentMerchant = merchant.getMerchantByParentID();
            if (parentMerchant != null) {
                Long parentID = parentMerchant.getID();
                //Check for cycle
                if (path.contains(parentID)) {
                    String cycle = getCycle(path, parentID);
                    log.error("Found cycle: " + cycle);
                    break;
                } else {
                    path.add(parentID);
                }

                if (merchant.getDistributionChainTemplateID() != null) {
                    //  DefaultLogger.warn("Inconsistent data encountered. Merchant(" + merchant.getID() + ") has both parent(" + parentMerchant.getID() + ") and DCT(" + merchant.getDistributionChainTemplateID() + "). Ignoring the DCT.");
                }
                merchant = parentMerchant;
            } else {
                break;
            }
            level++;
        }

        if (merchant != null && merchant.getDistributionChainTemplateID() != null) {
            DCTID = merchant.getDistributionChainTemplateID();
        }

        return new long[]{DCTID, (DCTID > 0) ? level : -1};
    }

    public static boolean isAuthorizedForLOP(Long merchantid) {
        long[] dctIDAndLevel = MerchantService.getDCTIDAndLevel(merchantid);
        long templateID = dctIDAndLevel[0];
        int levelNumber = (int) dctIDAndLevel[1];
        DistributionChainLevelService dclService = new DistributionChainLevelService();
        HashMap hm = dclService.getCommissionandLOP(levelNumber, templateID);
        boolean allowedForLOP = ((Boolean) (hm.get("allowedforlop"))).booleanValue();
        return allowedForLOP;
    }

    public static String getUserNameForMerchant(Merchant merchant) {
        String retUserName = GeneralConstants.EMPTY_STRING;

        Subscriber subscriber = merchant.getSubscriber();
        retUserName = subscriber.getUser().getUsername();

        return retUserName;
    }

    public static BigDecimal checkBalance(Long merchantID) {

        MerchantDAO merDao = DAOFactory.getInstance().getMerchantDAO();
        Merchant mer = merDao.getById(merchantID);
        Set<SubscriberMDN> mdnSet = mer.getSubscriber().getSubscriberMDNFromSubscriberID();
        SubscriberMDN subcrMDN = (SubscriberMDN) mdnSet.toArray()[0];
        PocketDAO dao = DAOFactory.getInstance().getPocketDAO();
        PocketQuery query = new PocketQuery();
        query.setMdnIDSearch(subcrMDN.getID());
        List<Pocket> results = dao.get(query);
        int i = 0;
        Boolean bool_true = Boolean.valueOf(true);
        for (; i < results.size(); i++) {
            Pocket p = results.get(i);
            if (bool_true.equals(p.getIsDefault()) && p.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_SVA) && p.getPocketTemplate().getCommodity().equals(CmFinoFIX.Commodity_Airtime)) {
                if (p.getCurrentBalance() != null) {
                    return p.getCurrentBalance();
                } else {
                    return ZERO;
                }
            }
        }
        return new BigDecimal(-1);
    }

    /**
     * @param merchant The merchant for whom the balance needs to be calculated
     * @param pocket The pocket for which the balance needs to be calculated
     * @param asOfDate The time as of when the pocket balance is retrieved
     * @param rafTxns The RAF txns that got resolved on or after the asOfDate instant
     * @return
     */
    public static BigDecimal getAirtimeSVABalanceAsOf(Merchant merchant, Pocket pocket, Date asOfDate, List<CommodityTransfer> rafTxns) {

        if (null == merchant || null == pocket) {
            return null;
        }

        //pocket never had a Txn so no balance
        if (null == pocket.getCurrentBalance()) {
            return null;
        }


        PocketTemplate pt = pocket.getPocketTemplate();
        if (!CmFinoFIX.PocketType_SVA.equals(pt.getType()) || !CmFinoFIX.Commodity_Airtime.equals(pt.getCommodity())) {
            //DefaultLogger.warn("Not an SVA Airtime pocket.");
            return null;
        }

        if (CmFinoFIX.SubscriberStatus_Retired.equals(merchant.getStatus())) {
            return ZERO;
        }

        return PocketService.getPocketBalanceAsOf3(pocket, asOfDate, rafTxns);
    }

    public static String getCycle(List<Long> path, Long parentID) {
        String cycle = "";
        int firstIndex = path.indexOf(parentID);
        for (int i = firstIndex; i < path.size(); i++) {
            cycle += path.get(i) + "->";
        }
        cycle += parentID;
        return cycle;
    }

    public static String getMDNFromMerchant(Merchant merchant) {
        Set<SubscriberMDN> subscriberMDNs = merchant.getSubscriber().getSubscriberMDNFromSubscriberID();
        SubscriberMDN subscriberMDN = (SubscriberMDN) subscriberMDNs.toArray()[0];
        return subscriberMDN.getMDN();
    }

    public static String getPrefixCodeFromMdn(String mdn) {
        BrandDAO dao = DAOFactory.getInstance().getBrandDAO();
        BrandQuery query = new BrandQuery();
        query.setPrefixCodeLike(mdn.substring(2, 4));
        List<Brand> results = dao.get(query);
        if (results.size() > 0) {
            return results.get(0).getPrefixCode();
        } else {
            return null;
        }
    }

    public static boolean hasTreeCycle(Long merchantID, Long parentID) {
        if (parentID.longValue() == merchantID.longValue()) {
            return true;
        }
        List<Long> path = new ArrayList<Long>();
        MerchantDAO merDAO = DAOFactory.getInstance().getMerchantDAO();
        path.add(merchantID);
        if (path.contains(parentID)) {
            return true;
        }

        while (true) {
            merchantID = parentID;
            path.add(merchantID);
            Merchant merchant = merDAO.getById(merchantID);
            Merchant parent = merchant.getMerchantByParentID();
            if (parent != null) {
                parentID = parent.getID();
                if (path.contains(parentID)) {
                    return true;
                }
            } else {
                //reached the top
                break;
            }
        }

        return false;
    }

    public int createNewMerchant(MerchantSyncRecord syncRecord, Long companyID, String errorMessage) {
        RegionDAO regionDAO = DAOFactory.getInstance().getRegionDAO();
        CMJSMerchant merchantMsg = new CMJSMerchant();
        merchantMsg.setaction(CmFinoFIX.JSaction_Insert);
        CMJSMerchant.CGEntries entry = new CMJSMerchant.CGEntries();
        if (!isValidREUserName(syncRecord.getUserName())) {
            return CmFinoFIX.SynchError_Failed_Invalid_UserName; // Invalid User Name
        }
        Company company = SubscriberService.getCompanyFromMDN(syncRecord.getMdn());
        if (company == null) {
            log.info("Create New Merchant failed. Invalid Company - " + syncRecord.getMdn());
            return CmFinoFIX.SynchError_Failed_Invalid_Company;
        }
        if (!companyID.equals(company.getID())) {
            log.info("Create New Merchant failed.  - Cannot Add MDNs of other Brands" + syncRecord.getMdn());
            return CmFinoFIX.SynchError_Failed_Invalid_Company;
        }
        entry.setCompanyID(company.getID());
        //validate MDN
        if (!isValidREMDN(syncRecord.getMdn())) {
            log.info("Create New Merchant failed.  - MDN RE check failed" + syncRecord.getMdn());
            return CmFinoFIX.SynchError_Failed_Invalid_MDN; // Invalid MDN
        }
        SubscriberMDNDAO dao = DAOFactory.getInstance().getSubscriberMdnDAO();
        SubscriberMDN subMDN = dao.getByMDN(syncRecord.getMdn());
        
        if (null != subMDN) {
            if (subMDN.getSubscriber().getMerchant() != null) {
                log.info("Create New Merchant failed.  - Merchant already exist");
                return CmFinoFIX.SynchError_Failed_Merchant_already_exists; // He is already a merchant
            }
            if (CmFinoFIX.SubscriberStatus_PendingRetirement.equals(subMDN.getStatus()) || 
            		CmFinoFIX.SubscriberStatus_Retired.equals(subMDN.getStatus())) {
                log.info("Create New Merchant failed.  - Subscriber is Retired or Pending Retired");
                return CmFinoFIX.SynchError_Failed_Invalid_Subscriber_Not_Active; // Subscriber is Retired or Pending Retired
            }
            entry.setSubscriberID(subMDN.getSubscriber().getID());
            entry.setSubscriberStatus(subMDN.getSubscriber().getStatus());
        } else {
            log.info("Create New Merchant failed.  - Subscriber doesnot exist with that MDN");
            return CmFinoFIX.SynchError_Failed_Invalid_MDN;
        }
        if (!isValidUserName(syncRecord.getUserName())) {
            log.info("Create New Merchant failed.  - Invalid Username" + syncRecord.getUserName());
            return CmFinoFIX.SynchError_Failed_Username_Already_Exists; // Invalid User Name
        }
        entry.setMDN(syncRecord.getMdn());
//        UserDAO userdao = new UserDAO();
//        User user = userdao.getByUserName(createdBy);
//        Long userid = user.getCompany().getID();

        entry.setUsername(syncRecord.getUserName());
        Integer language = CmFinoFIX.Language_English;//SubscriberService.getLanguage(syncRecord.getLanguage());
        SubscriberMDN smdn =  DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(syncRecord.getMdn());
        if(smdn != null)
        {
        	language = smdn.getSubscriber().getLanguage();
        }
        entry.setLanguage(language);

        entry.setTimezone(syncRecord.getTimezone());
        if (CmFinoFIX.Currency_IDR.equals(syncRecord.getCurrency()) || CmFinoFIX.Currency_USD.equals(syncRecord.getCurrency()) || CmFinoFIX.Currency_UnKnown.equals(syncRecord.getCurrency())) {
            entry.setCurrency(syncRecord.getCurrency());
        } else {
            log.info("Create New Merchant failed.  - Invalid Currency");
            return CmFinoFIX.SynchError_Failed_Invalid_Values;
        }
        try {
            Merchant parentMerchant = merchantDAO.getById(Long.parseLong(syncRecord.getParentId()));
            if (parentMerchant == null) {
                log.info("Create New Merchant failed.  - Invalid ParentID ");
                return CmFinoFIX.SynchError_Failed_Invalid_ParentID;
            }
            
            if (!parentMerchant.getSubscriber().getCompany().getID().equals(companyID)) {
                log.info("Create New Merchant failed.  - ParentID belong to different company");
                return CmFinoFIX.SynchError_Failed_Invalid_ParentID;
            }
            entry.setParentID(Long.parseLong(syncRecord.getParentId()));
        } catch (Exception e) {
            log.error("Create New Merchant failed.  - exception", e);
            return CmFinoFIX.SynchError_Failed_Invalid_ParentID;
        }
        //if parentid exist dct should not be defined for him
        // Long distChainTemplateId = null;
        try {
            if (StringUtils.isNotEmpty(syncRecord.getDistChainTemplate())) {
                //       distChainTemplateId = Long.parseLong(syncRecord.getDistChainTemplate());
                log.info("Create New Merchant failed.  - DCT should be NULL when ParentID is specified");
                return CmFinoFIX.SynchError_Failed_DCT_Should_Be_NULL;
            }
        } catch (Exception error) {
            log.error("Create New Merchant failed.  - exception", error);
            return CmFinoFIX.SynchError_Failed_Invalid_DCT;
        }
        //entry.setDistributionChainTemplateID(distChainTemplateId);
        //Here Test for GroupID is compulsory field or not and return the corresponding error to the user
        CMJSParentGroupIdCheck chklop = new CmFinoFIX.CMJSParentGroupIdCheck();
        chklop.setID(entry.getParentID());
        ParentGroupIDCheckProcessor groupidcheck = new ParentGroupIDCheckProcessor();
        chklop = (CMJSParentGroupIdCheck) groupidcheck.process(chklop);
        if (chklop.getAllowedForLOP()) {
            if (syncRecord.getGroupId() != null && syncRecord.getGroupId().length() > 0) {
                entry.setGroupID(syncRecord.getGroupId());
            } else {
                log.info("Create New Merchant failed.  - GroupID cannot be NULL for this merchant");
                return CmFinoFIX.SynchError_Failed_Invalid_GroupID_Cannot_Be_Null;
            }
        } else {
            if (syncRecord.getGroupId() != null && syncRecord.getGroupId().length() > 0) {
                //return CmFinoFIX.SynchError_Failed_Invalid_Group_Should_Be_Null;
                log.info("Create New Merchant failed.  - GroupID should be NULL for this merchant");
                return CmFinoFIX.SynchError_Failed_Invalid_GroupID_Should_Be_Null;
            } else {
                entry.setGroupID("");
            }
        }
        //Merchant doesnot have LOP Permission cannot own a region of their wish. they should have parent region.
        DistributionChainLevelService dclService = new DistributionChainLevelService();
        long[] dctIDAndLevel = MerchantService.getDCTIDAndLevel(Long.parseLong(syncRecord.getParentId()));
        long templateID = dctIDAndLevel[0];
        int levelNumber = (int) dctIDAndLevel[1];
        HashMap hm = dclService.getCommissionandLOP(levelNumber+1, templateID); //next level will be assigned for current merchant
        Integer permissions = ((Integer) (hm.get("permission")));
        if(!((permissions & CmFinoFIX.DistributionPermissions_LOP) > 0)){
         Merchant parent = merchantDAO.getById(Long.parseLong(syncRecord.getParentId()));
         if(!parent.getRegion().getRegionCode().equals(syncRecord.getRegion())){
          return CmFinoFIX.SynchError_Failed_Invalid_Region;
         }
        }
        // Get Region ID and set it
        List<Region> regionList = regionDAO.getByCode(syncRecord.getRegion());
        Region region = null;
        if (regionList.size() > 0) {
            region = regionList.get(0);
        }
        if (region == null) {
            log.info("Create New Merchant failed.  - Region is NULL");
            return CmFinoFIX.SynchError_Failed_Invalid_Region;
        }
        entry.setRegionID(region.getID());
        int partnerType = getPartnerType(syncRecord.getPartnerType());
        if (partnerType == -1) {
            log.info("Create New Merchant failed.  - Invalid PartnerType");
            return CmFinoFIX.SynchError_Failed_Invalid_PartnerType;
        }
        entry.setPartnerType(partnerType);
        entry.setFirstName(syncRecord.getFirstName());
        entry.setLastName(syncRecord.getLastName());
        entry.setTradeName(syncRecord.getTradeName());
        entry.setAdminComment(syncRecord.getAdminComment());
        if ("EMAIL".equalsIgnoreCase(syncRecord.getNotificationMethod())) {
            entry.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
            if (StringUtils.isNotBlank(syncRecord.getEmail())) {
                String emailchk = syncRecord.getEmail().toUpperCase();
                Matcher m = email.matcher(emailchk);
                if (m.find()) {
                    entry.setEmail(syncRecord.getEmail());
                } else {
                    log.info("Create New Merchant failed.  - Email check failed");
                    return CmFinoFIX.SynchError_Failed_Invalid_Email;
                }
            } else {
                log.info("Create New Merchant failed.  - Email is NULL");
                return CmFinoFIX.SynchError_Failed_EmailID_Missing;
            }
        }
        entry.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS); //SMS is default
        entry.setMerchantAddressLine1(syncRecord.getLine1());
        entry.setMerchantAddressLine2(syncRecord.getLine2());
        entry.setMerchantAddressCity(syncRecord.getCity());
        entry.setMerchantAddressState(syncRecord.getState());
        entry.setMerchantAddressCountry(syncRecord.getCountry());
        entry.setMerchantAddressZipcode(syncRecord.getZip());
        entry.setClassification(syncRecord.getOutletType());
        entry.setFranchisePhoneNumber(syncRecord.getContactNumber());
        entry.setTypeOfOrganization(syncRecord.getOrgType());
        entry.setFaxNumber(syncRecord.getFaxNumber());
        entry.setIndustryClassification(syncRecord.getIndustryClassification());
        entry.setWebSite(syncRecord.getWebsiteURL());
        int outletCnt = 0;
        try {
            outletCnt = Integer.parseInt(syncRecord.getOutletCnt());
        } catch (Exception error) {
        	log.warn("Exception parsing outlet count: ",error);
        }
        entry.setNumberOfOutlets(outletCnt);
        entry.setYearEstablished(Integer.parseInt(syncRecord.getYearEstablished()));
        entry.setOutletAddressLine1(syncRecord.getOutletLine1());
        entry.setOutletAddressLine2(syncRecord.getOutletLine2());
        entry.setOutletAddressCity(syncRecord.getOutletCity());
        entry.setOutletAddressState(syncRecord.getOutletState());
        entry.setOutletAddressCountry(syncRecord.getOutletCountry());
        entry.setOutletAddressZipcode(syncRecord.getOutletZip());
        entry.setRepresentativeName(syncRecord.getRepresentativeName());
        entry.setAuthorizedEmail(syncRecord.getOutletEmail());
        entry.setAuthorizedFaxNumber(syncRecord.getOutletFaxNumber());
        entry.setAuthenticationPhoneNumber(syncRecord.getRepContactNumber());
        if (StringUtils.isNotBlank(syncRecord.getSrcIP())) {
            log.info("Create New Merchant failed.  - IP should be NULL");
            return CmFinoFIX.SynchError_Failed_IP_Should_Be_NULL;
        }
        if (StringUtils.isNotBlank(syncRecord.getPosition())) {
            entry.setDesignation(syncRecord.getPosition());
        }
        CmFinoFIX.CMJSMerchant.CGEntries[] entries = new CMJSMerchant.CGEntries[1];
        entries[0] = entry;
        merchantMsg.setEntries(entries);
        MerchantProcessor merchantProcessor = new MerchantProcessor();
        try {
            CFIXMsg msg = merchantProcessor.process(merchantMsg);
            if (msg instanceof CMJSMerchant) {
                return CmFinoFIX.SynchError_Success;
            } else {
                CMJSError errorMsg = (CMJSError) msg;
                errorMessage = errorMsg.getErrorDescription();
                log.info("Create New Merchant failed.  -" + errorMessage);
                return errorMsg.getErrorCode();
            }
        } catch (Exception error) {
            log.error("Error processing Merchant", error);
        }
        return CmFinoFIX.SynchError_Failed_Other;
    }

    /**
     * @param partnerType
     * @return
     */
    private int getPartnerType(String partnerType) {
        int pType = -1;

        if ("Merchant E-Load".equalsIgnoreCase(partnerType)) {
            return CmFinoFIX.PartnerType_MerchantELoad;
        } else if ("Merchant Dompet".equalsIgnoreCase(partnerType)) {
            return CmFinoFIX.PartnerType_MerchantDompet;
        } else if ("Remittance".equalsIgnoreCase(partnerType)) {
            return CmFinoFIX.PartnerType_Remittance;
        } else if ("Cash-In Agent".equalsIgnoreCase(partnerType)) {
            return CmFinoFIX.PartnerType_CashInAgent;
        } else if ("Cash-Out Agent".equalsIgnoreCase(partnerType)) {
            return CmFinoFIX.PartnerType_CashInAgent;
        } else if ("Gallery Cash-In".equalsIgnoreCase(partnerType)) {
            return CmFinoFIX.PartnerType_CashInAgent;
        } else if ("Gallery Cash-Out".equalsIgnoreCase(partnerType)) {
            return CmFinoFIX.PartnerType_CashInAgent;
        }
        return pType;
    }

    private boolean isValidUserName(String userName) {
        UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
        UserQuery query = new UserQuery();
        query.setUserName(userName);
        List<User> results = userDAO.get(query);
        if (results.size() == 0) {
            return true;
        }
        return false;
    }

    private boolean isValidREUserName(String userName) {
        Matcher m = p.matcher(userName);
        boolean result = m.find();
        return result;

    }

    public static boolean isValidREMDN(String mobileno) {
        Matcher m = mdn.matcher(mobileno);
        boolean result = m.find();
        return result;

    }

    private boolean checkSubscriberMDN(String mdn, CGEntries entry) {
        SubscriberMdnProcessor subscriberMdnProcessor = new SubscriberMdnProcessor();
        CMJSSubscriberMDN realMsg = new CMJSSubscriberMDN();
        realMsg.setMDNSearch(mdn);
        try {
            CmFinoFIX.CMJSSubscriberMDN error = (CMJSSubscriberMDN) subscriberMdnProcessor.process(realMsg);
            if (error.gettotal() != 0) {
                com.mfino.fix.CmFinoFIX.CMJSSubscriberMDN.CGEntries entries = error.getEntries()[0];
                log.info("Subscriber ID " + entries.getSubscriberID());
                entry.setSubscriberID(entries.getSubscriberID());
                entry.setSubscriberStatus(entries.getSubscriberStatus());
                return true;
            }
            return false;
        } catch (Exception error) {
            log.error("Error fetching subscriber", error);
            return false;
        }
    }
}
