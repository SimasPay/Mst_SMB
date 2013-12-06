/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.fix.processor;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.AddressDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MerchantCodeDAO;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.RegionDAO;
import com.mfino.dao.SAPGroupIDDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.MerchantCodeQuery;
import com.mfino.dao.query.MerchantQuery;
import com.mfino.dao.query.SAPGroupIDQuery;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.Address;
import com.mfino.domain.Company;
import com.mfino.domain.Merchant;
import com.mfino.domain.MerchantCode;
import com.mfino.domain.Region;
import com.mfino.domain.SAPGroupID;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.exceptions.AddressLine1RequiredException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSForwardNotificationRequest;
import com.mfino.fix.CmFinoFIX.CMJSMerchant;
import com.mfino.fix.CmFinoFIX.CMJSMerchant.CGEntries;
import com.mfino.fix.CmFinoFIX.CMJSParentGroupIdCheck;
import com.mfino.fix.CmFinoFIX.CMJSUsernameCheck;
import com.mfino.fix.CmFinoFIX.CMJSUsers;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.service.MDNRangeService;
import com.mfino.service.MerchantService;
import com.mfino.service.SubscriberService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ForwardNotificationRequestProcessor;
import com.mfino.uicore.fix.processor.UsernameCheckProcessor;
import com.mfino.uicore.security.Authorization;
import com.mfino.uicore.service.DistributionChainTemplateService;
import com.mfino.uicore.service.UserService;
import com.mfino.uicore.web.WebContextError;
import com.mfino.util.PasswordGenUtil;

/**
 * 
 * @author sunil
 */
public class MerchantProcessor extends BaseFixProcessor {

    
    private EnumTextService enumTextService = new EnumTextService();
    private AddressDAO addressDAO = DAOFactory.getInstance().getAddressDAO();
    private Company company = null;

    public CMJSError handleMerchantStatusChangeNotAuthrorised() {
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        errorMsg.setErrorDescription(MessageText._("Not Authorized to change the Merchant Status"));
        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
        newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
        newEntries[0].setErrorName(MessageText._("Merchant Status"));
        newEntries[0].setErrorDescription(MessageText._("Not allowed"));
        return errorMsg;
    }

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSMerchant realMsg = (CMJSMerchant) msg;

        MerchantDAO merchantDAO = DAOFactory.getInstance().getMerchantDAO();
        SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMerchant.CGEntries[] entries = realMsg.getEntries();
            for (CMJSMerchant.CGEntries entry : entries) {
                Merchant merchantObj = merchantDAO.getById(entry.getID());

                 Merchant parentMerchant = merchantObj.getMerchantByParentID();
               if (!entry.getRecordVersion().equals(merchantObj.getVersion())) {
                    handleStaleDataException();
                }
                if (   (entry.getMDN() == null && entry.getParentID() != null && !isValidMDNPresentInParentsRange(entry))
                        ||(entry.getMDN() != null && entry.getParentID() != null && !isValidMDNPresentInParentsRange(entry, entry.getMDN()))
                        ) {
                    CMJSError err = new CMJSError();
                    err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    err.setErrorDescription(MessageText._("MDN Not Present in Parent's MDN Range"));
                    return err;
                }
                // parent is not edited and only mdn is edited
                if(entry.getMDN()!=null)
                {
                    if(!entry.isRemoteModifiedParentID() && entry.getParentID() == null && parentMerchant!=null && parentMerchant.getMerchantByParentID() !=null)
                    {
                        // an exception for top level guys
                        //if(parentMerchant.getMerchantByParentID()!=null)
                       // {
                            if(!MDNRangeService.isMDNInParentsRange(Long.parseLong(entry.getMDN().substring(2)), parentMerchant))
                            {
                                CMJSError err = new CMJSError();
                                err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                                err.setErrorDescription(MessageText._("MDN Not Present in Parent's MDN Range"));
                                return err;
                            }
                        //}
                    }
                    String mNumber = entry.getMDN();
                    company = SubscriberService.getCompanyFromMDN(mNumber);
                    if (company != null && company.getID() != UserService.getUserCompany().getID()) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("Cannot Edit MDN of other Brands"));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        return errorMsg;
                    } else if (company == null) {
                        // return failure message saying invalid mdn
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("Invalid MDN"));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        return errorMsg;
                    }
                }
               
                if (entry.getMDN() != null) {
                    if (UserService.isMerchant()) {
                        boolean isDecendentOfLoggedInMerchant = MerchantService.isDecendentOfLoggedInMerchant(merchantObj.getID());
                        if (!isDecendentOfLoggedInMerchant) {
                            CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                            errorMsg.setErrorDescription(MessageText._("Not Authorized to change the Merchant MDN"));
                            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                            CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                            newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                            newEntries[0].setErrorName(MessageText._("Merchant MDN"));
                            newEntries[0].setErrorDescription(MessageText._("Not allowed"));
                            return errorMsg;
                        }
                    }
                    SubscriberMdnQuery mdnQuery = new SubscriberMdnQuery();
                    mdnQuery.setExactMDN(entry.getMDN());
                    SubscriberMDNDAO mdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
                    List<SubscriberMDN> results = mdnDao.get(mdnQuery);
                    if (results.size() > 0) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("MDN Already exists in DB, Please Change"));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        return errorMsg;
                    }
                }

                // NOTE: have to do both check because the isRemoteModified only works
                // for update not insert
                if (entry.isRemoteModifiedParentID() && entry.getParentID() != null) {
                    boolean isAuthorized = Authorization.isAuthorized(CmFinoFIX.Permission_Merchant_ParentID_Edit);
                    if (isAuthorized == false) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("Not Authorized to change the Merchant ParentID"));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                        newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                        newEntries[0].setErrorName(MessageText._("Merchant ParentID"));
                        newEntries[0].setErrorDescription(MessageText._("Not allowed"));
                        return errorMsg;
                    }

                    // Here check for the Tree Cycle.
                    // Check if entry.getID() is in the hierarchy of the edited Parent ID.
                    if (true == MerchantService.hasTreeCycle(entry.getID(), entry.getParentID())) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("Recursive Cycle found. Cannot set this Merchant ParentID."));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                        newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                        newEntries[0].setErrorName(MessageText._("Merchant ParentID"));
                        newEntries[0].setErrorDescription(MessageText._("Not allowed"));
                        return errorMsg;
                    }

                    if (merchantObj.getStatus().equals(CmFinoFIX.SubscriberStatus_Retired)) {
                        Set<Merchant> childrens = merchantObj.getMerchantFromParentID();
                        if (childrens.size() != 0) {
                            Object mer[] = childrens.toArray();
                            for (int i = 0; i < childrens.size(); i++) {
                                if (!((Merchant) mer[i]).getStatus().equals(CmFinoFIX.SubscriberStatus_Retired)) {
                                    CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                                    errorMsg.setErrorDescription(MessageText._("Cannot retire this merchant because it's children are not Retired"));
                                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                                    CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                                    newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                                    newEntries[0].setErrorName(MessageText._("Merchant Retire"));
                                    newEntries[0].setErrorDescription(MessageText._("Not allowed"));
                                    return errorMsg;
                                }
                            }
                        }
                    }
                }

                if (entry.isRemoteModifiedSubscriberStatus() && entry.getSubscriberStatus() != null) {

                    boolean isAuthorized = Authorization.isAuthorized(CmFinoFIX.Permission_Merchant_Status_Edit);

                    if (isAuthorized == false) {
                        CMJSError errorMsg = handleMerchantStatusChangeNotAuthrorised();
                        return errorMsg;
                    } else {
                        // Should not allow a pending or retired merchant to change status
                        // #465 ticket
                        // https://mfino.devguard.com/trac/mfino/ticket/465
                        if (merchantObj.getStatus().equals(CmFinoFIX.SubscriberStatus_Retired) || merchantObj.getStatus().equals(CmFinoFIX.SubscriberStatus_PendingRetirement)) {
                            if (entry.getSubscriberStatus() != CmFinoFIX.SubscriberStatus_Retired && entry.getSubscriberStatus() != CmFinoFIX.SubscriberStatus_PendingRetirement) {
                                CMJSError errorMsg = handleMerchantStatusChangeNotAuthrorised();
                                return errorMsg;
                            }
                        }
                    }
                }

                if (entry.getSubscriberStatus() != null) {
                    if (entry.getSubscriberStatus() == CmFinoFIX.SubscriberStatus_PendingRetirement || entry.getSubscriberStatus() == CmFinoFIX.SubscriberStatus_Retired) {
                        CMJSMerchant.CGEntries newEntry = new CMJSMerchant.CGEntries();
                        newEntry.setSubscriberStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
                        newEntry.setID(entry.getID());
                        newEntry.setRecordVersion(entry.getRecordVersion());
                        updateEntity(merchantObj, newEntry);
                    } else {
                        updateEntity(merchantObj, entry);
                    }
                } else {
                    updateEntity(merchantObj, entry);
                }
                merchantDAO.save(merchantObj);
                updateMessage(merchantObj, entry);
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {

            logMessage(realMsg);
            MerchantQuery query = new MerchantQuery();
            if (UserService.getUserCompany() != null) {
                query.setCompany(UserService.getUserCompany());
            }
            if (realMsg.getMDNSearch() != null && !realMsg.getMDNSearch().trim().equals("")) {
                query.setMdn(realMsg.getMDNSearch());
            }

            if (realMsg.getLastNameSearch() != null && !realMsg.getLastNameSearch().trim().equals("")) {
                query.setLastName(realMsg.getLastNameSearch());
            }

            if (realMsg.getFirstNameSearch() != null && !realMsg.getFirstNameSearch().trim().equals("")) {
                query.setFirstName(realMsg.getFirstNameSearch());
            }

            if (realMsg.getStartDateSearch() != null) {
                query.setStartRegistrationDate(realMsg.getStartDateSearch());
            }

            if (realMsg.getEndDateSearch() != null) {
                query.setEndRegistrationDate(realMsg.getEndDateSearch());
            }

            if (realMsg.getStatusSearch() != null && !realMsg.getStatusSearch().trim().equals("")) {
                query.setMerchantStatus(Integer.parseInt(realMsg.getStatusSearch()));
            }

            if (realMsg.getRestrictionsSearch() != null && !realMsg.getRestrictionsSearch().trim().equals("")) {
                query.setMerchantRestrictions(Integer.parseInt(realMsg.getRestrictionsSearch()));
            }

            if (realMsg.getUsernameSearch() != null && !realMsg.getUsernameSearch().trim().equals("")) {
                query.setUserName(realMsg.getUsernameSearch());
            }

            if (StringUtils.isNotBlank(realMsg.getExactUsernameSearch())) {
                query.setExactUser(realMsg.getExactUsernameSearch());
            }

            if (StringUtils.isNotBlank(realMsg.getExactGroupIDSearch())) {
                query.setExactGroupID(realMsg.getExactGroupIDSearch());
            }

            // getquery is for search dropdown.
            if (StringUtils.isNotBlank(realMsg.getquery())) {
                query.setUserName(realMsg.getquery());
            }

            // If Merchant then check if we need to retrive
            // only Self and Downline merchants.
            if (UserService.isMerchant()) {
                if (true == realMsg.getSelfAndDownlineSearch().booleanValue()) {
                    query.setParentAndSelfID(MerchantService.getMerchantIDOfLoggedInUser());
                }

                if (null != query.getId()) {
                    // Check if this ID is a decendent of this logged in merchant.
                    if (false == MerchantService.isDecendentOfLoggedInMerchant(query.getId())) {
                        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
                        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        error.setErrorDescription(MessageText._("Unauthorized operation."));
                        return error;
                    }
                }
            }

            query.setParentID(realMsg.getParentIDSearch());

            if (!CmFinoFIX.JSmFinoAction_Update.equals(realMsg.getmfinoaction())) {
                query.setStart(realMsg.getstart());
                query.setLimit(realMsg.getlimit());
            }

            query.setId(realMsg.getIDSearch());

            List<Merchant> results = merchantDAO.get(query);

            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                Merchant s = (Merchant) results.get(i);
                CMJSMerchant.CGEntries entry = new CMJSMerchant.CGEntries();
                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

            log.info(" results size = " + results.size());
            log.info(" total results size = " + query.getTotal());
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
            CMJSMerchant.CGEntries[] entries = realMsg.getEntries();
            CMJSUsers users = new CMJSUsers();
            users.setaction(CmFinoFIX.JSaction_Insert);

            for (CMJSMerchant.CGEntries e : entries) {
                if (e == null) {
                    CMJSError err = new CMJSError();
                    err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    err.setErrorDescription(MessageText._("Please refresh and try again."));
                    return err;
                }
                if (!isValidGroupAndParent(e)) {
                    CMJSError err = new CMJSError();
                    err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    err.setErrorDescription(MessageText._("Not a Valid Combination of Group and Parent ID"));
                    return err;
                }
                if (UserService.isMerchant()) {
                    Long parentid = MerchantService.getMerchantIDOfLoggedInUser();
                    MerchantDAO mdao = DAOFactory.getInstance().getMerchantDAO();
                    Merchant m = mdao.getById(parentid);
                    e.setParentID(m.getID());
                }
                if (e.getParentID() != null && !isValidMDNPresentInParentsRange(e)) {
                    CMJSError err = new CMJSError();
                    err.setErrorCode(CmFinoFIX.SynchError_Failed_MDN_Does_Not_Exists_In_Range);
                    err.setErrorDescription(MessageText._("MDN Not Present in Parent's MDN Range"));
                    return err;
                }
                //null mean it was from the webapplicatio
                //if some value is being sent then the request is coming from the bulkupload merchant schedular
                if (e.getCompanyID() == null) {
                    //set the company id before creating the user object.
                    company = SubscriberService.getCompanyFromMDN(e.getMDN());
                    if (UserService.getUserCompany() != null && company != null && company.getID() == UserService.getUserCompany().getID()) {
                        e.setCompanyID(company.getID());
                    } else if (company != null && company.getID() != UserService.getUserCompany().getID()) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("Cannot Add MDNs of other Brands"));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        return errorMsg;
                    } else if (company == null) {
                        // return failure message saying invalid mdn
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("Invalid MDN"));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        return errorMsg;
                    }
                }
                // Add User to the UserTable
                User userEntry = new User();
                //validate the username before saving the object
                if(!validateUsername(e))
                {
                    CMJSError err = new CMJSError();
                    err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    err.setErrorDescription(MessageText._("Username already exist in DB, try with other username"));
                    return err;
                }
                initializeUserObject(userEntry, e);
                String genPwd = PasswordGenUtil.generate();
                PasswordEncoder encoder = new ShaPasswordEncoder(1);
                String encPassword = encoder.encodePassword(genPwd, userEntry.getUsername());
                userEntry.setPassword(encPassword);

                userDAO.save(userEntry);

                log.debug("ID = " + e.getSubscriberID());

                Merchant merchantObj = new Merchant();
                Subscriber subscriber = subscriberDAO.getById(e.getSubscriberID());
                SubscriberService subscriberService = new SubscriberService();
                subscriberService.createDefaultPocketForMerchant(subscriber, e.getMDN());

                merchantObj.setSubscriber(subscriber);

                e.setSubscriberType(CmFinoFIX.SubscriberType_Merchant_E_Load);
                updateEntity(merchantObj, e);

                merchantDAO.save(merchantObj);
                updateMessage(merchantObj, e);
                ForwardNotificationRequestProcessor notifySubscriber = new ForwardNotificationRequestProcessor();
                CMJSForwardNotificationRequest forwardMsg = new CMJSForwardNotificationRequest();
                updateForwardMessage(forwardMsg, e);
                notifySubscriber.process(forwardMsg);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
        }
        return realMsg;
    }
   

    public void updateForwardMessage(CMJSForwardNotificationRequest newMsg, CMJSMerchant.CGEntries e) {
        newMsg.setDestMDN(e.getMDN());
        Boolean hasBobPocket = false;
        if (e.getSubscriberID() != null) {
            SubscriberDAO subsDAO = DAOFactory.getInstance().getSubscriberDAO();
            Subscriber sub = subsDAO.getById(e.getSubscriberID());
            Set<SubscriberMDN> subsMDNList = sub.getSubscriberMDNFromSubscriberID();
            SubscriberMDN subsMDN = (SubscriberMDN)subsMDNList.toArray()[0];
            hasBobPocket = SubscriberService.hasBOBPocket(subsMDN);
        }
        if (hasBobPocket) {
            newMsg.setCode(CmFinoFIX.NotificationCode_MerchantActivationforBOBPocketAlreadyActive);
        } else {
            newMsg.setCode(CmFinoFIX.NotificationCode_MerchantMCommActivationRequest);
        }
        newMsg.setFormatOnly(Boolean.FALSE);
        if (e.getMSPID() != null) {
            newMsg.setMSPID(e.getMSPID());
        }
        newMsg.setSourceMDN(e.getMDN());
        // This is done with purpose.
        // newMsg.setServletPath();
    }

    private void initializeUserObject(User user, CMJSMerchant.CGEntries e) {
        if (e.getUsername() != null) {
            user.setUsername(e.getUsername());
        }
        if (e.getCompanyID() != null) {
            if (company == null) {
                company = SubscriberService.getCompanyFromMDN(e.getMDN());
            }
            user.setCompany(company);
        }
        if (e.getEmail() != null) {
            user.setEmail(e.getEmail());
        }

        if (e.getFirstName() != null) {
            user.setFirstName(e.getFirstName());
        }

        if (e.getLastName() != null) {
            user.setLastName(e.getLastName());
        }

        if (e.getCreatedBy() != null) {
            user.setCreatedBy(e.getCreatedBy());
        }

        if (e.getLanguage() != null) {
            user.setLanguage(e.getLanguage());
        }

        if (e.getTimezone() != null) {
            user.setTimezone(e.getTimezone());
        }
        if (e.getMSPID() != null) {
            MfinoServiceProviderDAO mspdao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            user.setmFinoServiceProviderByMSPID(mspdao.getById(e.getMSPID()));
        }

        user.setStatus(CmFinoFIX.SubscriberRestrictions_SecurityLocked);
        user.setRole(CmFinoFIX.Role_Merchant);
        user.setAdminComment("User Created from Merchat Add");
        user.setStatusTime(new Timestamp());

        if (e.getUpdatedBy() != null) {
            user.setUpdatedBy(e.getUpdatedBy());
        }
    }

//    private void initializeUserObject(User user, User sourceUser) {
//        if (sourceUser.getFirstName() != null) {
//            user.setFirstName(sourceUser.getFirstName());
//        }
//
//        if (sourceUser.getLastName() != null) {
//            user.setLastName(sourceUser.getLastName());
//        }
//
//        if (sourceUser.getCreatedBy() != null) {
//            user.setCreatedBy(sourceUser.getCreatedBy());
//        }
//
//        if (sourceUser.getLanguage() != null) {
//            user.setLanguage(sourceUser.getLanguage());
//        }
//
//        if (sourceUser.getTimezone() != null) {
//            user.setTimezone(sourceUser.getTimezone());
//        }
//        if (sourceUser.getmFinoServiceProviderByMSPID() != null) {
//            user.setmFinoServiceProviderByMSPID(sourceUser.getmFinoServiceProviderByMSPID());
//        }
//
//        user.setStatus(CmFinoFIX.SubscriberRestrictions_SecurityLocked);
//        user.setRole(CmFinoFIX.Role_Merchant);
//        user.setAdminComment("User Created from Merchat Add");
//        user.setStatusTime(new Timestamp());
//
//        if (sourceUser.getUpdatedBy() != null) {
//            user.setUpdatedBy(sourceUser.getUpdatedBy());
//        }
//    }

    public void updateEntity(Merchant merchant, CMJSMerchant.CGEntries entry) throws Exception {
        if (entry.getAuthorizedRepresentative() != null) {
            merchant.setAuthorizedRepresentative(entry.getAuthorizedRepresentative());
        }
        if (entry.isRemoteModifiedDistributionChainTemplateID() || entry.getDistributionChainTemplateID() != null) {
            merchant.setDistributionChainTemplateID(entry.getDistributionChainTemplateID());
        }

        if (entry.isRemoteModifiedParentID() || entry.getParentID() != null) {
            if (entry.getParentID() == null) {
                merchant.setMerchantByParentID(null);
                merchant.setRangeCheck(CmFinoFIX.RangeCheck_None);
            } else {
                MerchantDAO mdao = DAOFactory.getInstance().getMerchantDAO();
                Merchant m = mdao.getById(entry.getParentID());
                merchant.setMerchantByParentID(m);
                if(MDNRangeService.processMDNRangesInParentMDNRange(merchant))
                {
                	merchant.setRangeCheck(CmFinoFIX.RangeCheck_None);
                }
                else
                {
                	merchant.setRangeCheck(merchant.getRangeCheck() == null ? CmFinoFIX.RangeCheck_MDNRangeNotInParentsRange : (merchant.getRangeCheck() | CmFinoFIX.RangeCheck_MDNRangeNotInParentsRange) &  CmFinoFIX.RangeCheck_MDNRangeNotInParentsRange);
                }
            }
        }

        if (entry.isRemoteModifiedSubscriberStatus() || entry.getSubscriberStatus() != null) {
            merchant.setStatus(entry.getSubscriberStatus());
            merchant.setStatusTime(new Timestamp());
        }

        if (entry.getGroupID() != null) {
            merchant.setGroupID(entry.getGroupID());
        }
        if (entry.getClassification() != null) {
            merchant.setClassification(entry.getClassification());
        }

        if (entry.getDesignation() != null) {
            merchant.setDesignation(entry.getDesignation());
        }

        if (entry.getFaxNumber() != null) {
            merchant.setFaxNumber(entry.getFaxNumber());
        }

        if (entry.getFirstName() != null) {
            merchant.getSubscriber().setFirstName(entry.getFirstName());
        }

        if (entry.getFranchisePhoneNumber() != null) {
            merchant.setFranchisePhoneNumber(entry.getFranchisePhoneNumber());
        }

        if (entry.getIndustryClassification() != null) {
            merchant.setIndustryClassification(entry.getIndustryClassification());
        }

        if (entry.getLastName() != null) {
            merchant.getSubscriber().setLastName(entry.getLastName());
        }

        if (entry.getNumberOfOutlets() != null) {
            merchant.setNumberOfOutlets(entry.getNumberOfOutlets());
        }
        if (entry.getRepresentativeName() != null) {
            merchant.setRepresentativeName(entry.getRepresentativeName());
        }
        if (entry.getPartnerType() != null) {
            merchant.getSubscriber().setPartnerType(entry.getPartnerType());
        }
        if (entry.getSecurityAnswer() != null) {
            merchant.getSubscriber().setSecurityAnswer(entry.getSecurityAnswer());
        }

        if (entry.getSecurityQuestion() != null) {
            merchant.getSubscriber().setSecurityQuestion(entry.getSecurityQuestion());
        }
        if (entry.getRegionID() != null) {
            RegionDAO dao = DAOFactory.getInstance().getRegionDAO();
            Region reg = dao.getById(entry.getRegionID());
            merchant.setRegion(reg);
            //get all the childrens who doesnot have lop permission and change the region to the new one.
            updateRegion(merchant, reg);
        }
        if (StringUtils.isNotBlank(entry.getTradeName())) {
            merchant.setTradeName(entry.getTradeName());
        } else {
            String pFirstName = GeneralConstants.SINGLE_SPACE;
            String pLastName = GeneralConstants.SINGLE_SPACE;

            if (merchant.getSubscriber().getFirstName() != null) {
                pFirstName = merchant.getSubscriber().getFirstName();
            }

            if (merchant.getSubscriber().getLastName() != null) {
                pLastName = merchant.getSubscriber().getLastName();
            }

            String pTradeName = pFirstName + GeneralConstants.SINGLE_SPACE + pLastName;

            merchant.setTradeName(pTradeName);
        }

        if (entry.getTypeOfOrganization() != null) {
            merchant.setTypeOfOrganization(entry.getTypeOfOrganization());
        }

        if (entry.getWebSite() != null) {
            merchant.setWebSite(entry.getWebSite());
        }

        if (entry.getSubscriberType() != null) {
            merchant.getSubscriber().setType(entry.getSubscriberType());
        }

        if (entry.getLanguage() != null) {
            merchant.getSubscriber().setLanguage(entry.getLanguage());
        }

        if (entry.getTimezone() != null) {
            merchant.getSubscriber().setTimezone(entry.getTimezone());
        }

        if (entry.getCurrency() != null) {
            merchant.getSubscriber().setCurrency(entry.getCurrency());
        }

        if (entry.getEmail() != null) {
            merchant.getSubscriber().setEmail(entry.getEmail());
        }
        if (entry.getNotificationMethod() != null) {
            merchant.getSubscriber().setNotificationMethod(entry.getNotificationMethod());
        }
        if (entry.getNotificationMethod() != null) {
            merchant.getSubscriber().setNotificationMethod(entry.getNotificationMethod());
        }

        Set<SubscriberMDN> subscriberMDNs = merchant.getSubscriber().getSubscriberMDNFromSubscriberID();
        SubscriberMDN subscriberMDN = (SubscriberMDN) subscriberMDNs.toArray()[0];

        if (entry.getH2HAllowedIP() != null) {
            subscriberMDN.setH2HAllowedIP(entry.getH2HAllowedIP());
        }

        if (entry.getAuthenticationPhrase() != null) {
            subscriberMDN.setAuthenticationPhrase(entry.getAuthenticationPhrase());
        }

        if (entry.getSubscriberRestrictions() != null) {
            merchant.getSubscriber().setRestrictions(entry.getSubscriberRestrictions());
            subscriberMDN.setRestrictions(entry.getSubscriberRestrictions());
        }

        if (entry.getWrongPINCount() != null) {
            subscriberMDN.setWrongPINCount(entry.getWrongPINCount());
        }

        if (entry.getMDN() != null) {
            subscriberMDN.setMDN(entry.getMDN());
            if(entry.getIsRangeCheckUpdated() != null && !entry.isRemoteModifiedParentID() && MDNRangeService.processMDNRangesInParentMDNRange(merchant))
            {
            	merchant.setRangeCheck(merchant.getRangeCheck() == null ? null : CmFinoFIX.RangeCheck_None);
            }
            else
            {
            merchant.setRangeCheck(merchant.getRangeCheck() == null ? null : merchant.getRangeCheck() & CmFinoFIX.RangeCheck_MDNRangeNotInParentsRange);
            }
        }
        else if(entry.getIsRangeCheckUpdated() != null && !entry.isRemoteModifiedParentID() && MDNRangeService.processMDNRangesInParentMDNRange(merchant))
        {
        	merchant.setRangeCheck(merchant.getRangeCheck() == null ? null : merchant.getRangeCheck() & CmFinoFIX.RangeCheck_MDNNotInParentsRange);
        }
        if ((entry.getMerchantAddressLine1() != null && !entry.getMerchantAddressLine1().trim().equals(GeneralConstants.EMPTY_STRING)) || (entry.getMerchantAddressLine2() != null && !entry.getMerchantAddressLine2().trim().equals(GeneralConstants.EMPTY_STRING)) || (entry.getMerchantAddressState() != null && !entry.getMerchantAddressState().trim().equals(GeneralConstants.EMPTY_STRING)) || (entry.getMerchantAddressCity() != null && !entry.getMerchantAddressCity().trim().equals(GeneralConstants.EMPTY_STRING)) || (entry.getMerchantAddressCountry() != null && !entry.getMerchantAddressCountry().trim().equals(GeneralConstants.EMPTY_STRING)) || (entry.getMerchantAddressZipcode() != null && !entry.getMerchantAddressZipcode().trim().equals(GeneralConstants.EMPTY_STRING))) {

            logMerchantAddress(entry);

            Address addr = merchant.getAddressByMerchantAddressID();
            if (addr == null) {
                addr = new Address();
                merchant.setAddressByMerchantAddressID(addr);
            }

            if (entry.getMerchantAddressLine1() != null) {
                addr.setLine1(entry.getMerchantAddressLine1());
            }

            if (entry.getMerchantAddressLine2() != null) {
                addr.setLine2(entry.getMerchantAddressLine2());
            }

            if (entry.getMerchantAddressCity() != null) {
                addr.setCity(entry.getMerchantAddressCity());
            }

            if (entry.getMerchantAddressState() != null) {
                addr.setState(entry.getMerchantAddressState());
            }

            if (entry.getMerchantAddressCountry() != null) {
                addr.setCountry(entry.getMerchantAddressCountry());
            }
            if (entry.getMerchantAddressZipcode() != null) {
                addr.setZipCode(entry.getMerchantAddressZipcode());
            }

            // FIXME: This is swallowing all exceptions! Any exception will result in
            // "AddressLine1..,Zip required" message
            try {
                addressDAO.save(addr);
            } catch (Exception ex) {
            	log.error(ex.getMessage(), ex);
                this.handleAddressException();
            }
        }

        logOutLetAddress(entry);

        if ((entry.getOutletAddressLine1() != null && !entry.getOutletAddressLine1().trim().equals(GeneralConstants.EMPTY_STRING)) || (entry.getOutletAddressLine2() != null && !entry.getOutletAddressLine2().trim().equals(GeneralConstants.EMPTY_STRING)) || (entry.getOutletAddressState() != null && !entry.getOutletAddressState().trim().equals(GeneralConstants.EMPTY_STRING)) || (entry.getOutletAddressCity() != null && !entry.getOutletAddressCity().trim().equals(GeneralConstants.EMPTY_STRING)) || (entry.getOutletAddressCountry() != null && !entry.getOutletAddressCountry().trim().equals(GeneralConstants.EMPTY_STRING)) || (entry.getOutletAddressZipcode() != null && !entry.getOutletAddressZipcode().trim().equals(GeneralConstants.EMPTY_STRING))) {


            Address addr = merchant.getAddressByFranchiseOutletAddressID();
            if (addr == null) {
                addr = new Address();
                merchant.setAddressByFranchiseOutletAddressID(addr);
            }

            if (entry.getOutletAddressLine1() != null) {
                addr.setLine1(entry.getOutletAddressLine1());
            }

            if (entry.getOutletAddressLine2() != null) {
                addr.setLine2(entry.getOutletAddressLine2());
            }

            if (entry.getOutletAddressCity() != null) {
                addr.setCity(entry.getOutletAddressCity());
            }

            if (entry.getOutletAddressState() != null) {
                addr.setState(entry.getOutletAddressState());
            }

            if (entry.getOutletAddressCountry() != null) {
                addr.setCountry(entry.getOutletAddressCountry());
            }

            if (entry.getOutletAddressZipcode() != null) {
                addr.setZipCode(entry.getOutletAddressZipcode());
            }

            try {
                addressDAO.save(addr);
            } catch (Exception ex) {
            	log.error(ex.getMessage(), ex);
                this.handleAddressException();
            }
        }
        if (entry.getYearEstablished() != null) {
            merchant.setYearEstablished(entry.getYearEstablished());
        }
        if (entry.getAuthenticationPhoneNumber() != null) {
            subscriberMDN.setAuthenticationPhoneNumber(entry.getAuthenticationPhoneNumber());
        }
        if (entry.getAuthorizedEmail() != null) {
            merchant.setAuthorizedEmail(entry.getAuthorizedEmail());
        }
        if (entry.getAuthorizedFaxNumber() != null) {
            merchant.setAuthorizedFaxNumber(entry.getAuthorizedFaxNumber());
        }

        UserDAO userDao = DAOFactory.getInstance().getUserDAO();
        if (entry.getUsername() != null) {
            UserQuery query = new UserQuery();
            query.setUserName(entry.getUsername());
            List<User> results = userDao.get(query);
            if (results.size() > 0) {
                User userObj = results.get(0);
                merchant.getSubscriber().setUser(userObj);
            }
        }
        if (entry.getCurrentWeeklyPurchaseAmount() != null) {
            merchant.setCurrentWeeklyPurchaseAmount(entry.getCurrentWeeklyPurchaseAmount());
        }

        if (entry.getLastUpdateTime() != null) {
            merchant.setLastUpdateTime(entry.getLastUpdateTime());
        }

        if (entry.getUpdatedBy() != null) {
            merchant.setUpdatedBy(entry.getUpdatedBy());
        }

        if (entry.getCreateTime() != null) {
            merchant.setCreateTime(entry.getCreateTime());
        }

        if (entry.getCreatedBy() != null) {
            merchant.setCreatedBy(entry.getCreatedBy());
        }
        if (entry.getAdminComment() != null) {
            merchant.setAdminComment(entry.getAdminComment());
        }

    }

    public void updateRegion(Merchant merchant, Region reg) {
        Set<Merchant> childrens = merchant.getMerchantFromParentID();
        if (childrens.size() != 0) {
            Object mer[] = childrens.toArray();
            for (int i = 0; i < childrens.size(); i++) {
                Merchant Merchant = (Merchant) mer[i];
                if (!MerchantService.isAuthorizedForLOP(Merchant.getID())) {
                    Merchant.setRegion(reg);
                    updateRegion(Merchant, reg);
                }
            }
        }
    }

    public void updateMessage(Merchant merchant, CMJSMerchant.CGEntries entry) {
        if (merchant.getID() != null) {
            entry.setID(merchant.getID());
        }
        if (merchant.getRegion() != null) {
            entry.setRegionName(String.format("%s (%s)", merchant.getRegion().getRegionCode(), merchant.getRegion().getRegionName()));
            entry.setRegionID(merchant.getRegion().getID());
        }
        if (merchant.getSubscriber().getPartnerType() != null) {
            entry.setPartnerType(merchant.getSubscriber().getPartnerType());
        }
        if (merchant.getGroupID() != null && merchant.getGroupID().trim().length() > 0) {
            entry.setGroupID(merchant.getGroupID());
            SAPGroupIDDAO groupIDDAO = DAOFactory.getInstance().getSAPGroupIDDAO();
            SAPGroupIDQuery query = new SAPGroupIDQuery();
            query.setGroupID(merchant.getGroupID());
            List<SAPGroupID> groupIDs = groupIDDAO.get(query);
            if (groupIDs.size() <= 0) {
            	log.error(String.format("GroupID (%s) does not exist in SAPGroupID table", merchant.getGroupID()));
                entry.setGroupIDDisplayText(merchant.getGroupID());
            } else {
                entry.setGroupIDDisplayText(String.format("%s (%s)", merchant.getGroupID(), groupIDs.get(0).getGroupIDName()));
            }
        }
        if (merchant.getDistributionChainTemplateID() != null) {
            entry.setDistributionChainTemplateID(merchant.getDistributionChainTemplateID());
        }
        if (merchant.getMerchantByParentID() != null) {
            entry.setParentID(merchant.getMerchantByParentID().getID());
            Subscriber sub = merchant.getMerchantByParentID().getSubscriber();
            User user = sub.getUser();

            if (user != null) {
                entry.setParentName(user.getUsername());
            } else {
                entry.setUsername("N/A");
            }
        }
        
        if (merchant.getAuthorizedRepresentative() != null) {
            entry.setAuthorizedRepresentative(merchant.getAuthorizedRepresentative());
        }

        if (merchant.getClassification() != null) {
            entry.setClassification(merchant.getClassification());
        }

        if (merchant.getDesignation() != null) {
            entry.setDesignation(merchant.getDesignation());
        }

        if (merchant.getFaxNumber() != null) {
            entry.setFaxNumber(merchant.getFaxNumber());
        }

        if (merchant.getSubscriber().getFirstName() != null) {
            entry.setFirstName(merchant.getSubscriber().getFirstName());
        }

        if (merchant.getFranchisePhoneNumber() != null) {
            entry.setFranchisePhoneNumber(merchant.getFranchisePhoneNumber());
        }

        if (merchant.getIndustryClassification() != null) {
            entry.setIndustryClassification(merchant.getIndustryClassification());
        }

        if (merchant.getSubscriber().getLastName() != null) {
            entry.setLastName(merchant.getSubscriber().getLastName());
        }

        if (merchant.getNumberOfOutlets() != null) {
            entry.setNumberOfOutlets(merchant.getNumberOfOutlets());
        }

        if (merchant.getRepresentativeName() != null) {
            entry.setRepresentativeName(merchant.getRepresentativeName());
        }

        if (merchant.getSubscriber().getSecurityAnswer() != null) {
            entry.setSecurityAnswer(merchant.getSubscriber().getSecurityAnswer());
        }

        if (merchant.getSubscriber().getSecurityQuestion() != null) {
            entry.setSecurityQuestion(merchant.getSubscriber().getSecurityQuestion());
        }

        if (merchant.getTradeName() != null) {
            entry.setTradeName(merchant.getTradeName());
        }

        if (merchant.getTypeOfOrganization() != null) {
            entry.setTypeOfOrganization(merchant.getTypeOfOrganization());
        }

        if (merchant.getWebSite() != null) {
            entry.setWebSite(merchant.getWebSite());
        }

        if (merchant.getVersion() != null) {
            entry.setRecordVersion(merchant.getVersion());
        }
        if (merchant.getCurrentWeeklyPurchaseAmount() != null) {
            entry.setCurrentWeeklyPurchaseAmount(merchant.getCurrentWeeklyPurchaseAmount());
        }
        Set<SubscriberMDN> subscriberMDNs = merchant.getSubscriber().getSubscriberMDNFromSubscriberID();
        SubscriberMDN subscriberMDN = (SubscriberMDN) subscriberMDNs.toArray()[0];

        if (subscriberMDN.getAuthenticationPhrase() != null) {
            entry.setAuthenticationPhrase(subscriberMDN.getAuthenticationPhrase());
        }
        if (merchant.getSubscriber().getmFinoServiceProviderByMSPID().getID() != null) {
            entry.setMSPID(merchant.getSubscriber().getmFinoServiceProviderByMSPID().getID());
        }

        if (subscriberMDN.getMDN() != null) {
            entry.setMDN(subscriberMDN.getMDN());
        }

        if (subscriberMDN.getH2HAllowedIP() != null) {
            entry.setH2HAllowedIP(subscriberMDN.getH2HAllowedIP());
        }

        if (merchant.getSubscriber().getLanguage() != null) {
            entry.setLanguage(merchant.getSubscriber().getLanguage());
        }
        if (merchant.getSubscriber().getTimezone() != null) {
            entry.setTimezone(merchant.getSubscriber().getTimezone());
        }
        if (merchant.getSubscriber().getCurrency() != null) {
            entry.setCurrency(merchant.getSubscriber().getCurrency());
        }
        if (merchant.getSubscriber().getEmail() != null) {
            entry.setEmail(merchant.getSubscriber().getEmail());
        }
        if (merchant.getSubscriber().getNotificationMethod() != null) {
            entry.setNotificationMethod(merchant.getSubscriber().getNotificationMethod());
        }
        if (merchant.getYearEstablished() != null) {
            entry.setYearEstablished(merchant.getYearEstablished());
        }
        if (subscriberMDN.getAuthenticationPhoneNumber() != null) {
            entry.setAuthenticationPhoneNumber(subscriberMDN.getAuthenticationPhoneNumber());
        }
        if (merchant.getAuthorizedEmail() != null) {
            entry.setAuthorizedEmail(merchant.getAuthorizedEmail());
        }
        if (merchant.getAuthorizedFaxNumber() != null) {
            entry.setAuthorizedFaxNumber(merchant.getAuthorizedFaxNumber());
        }

        if (merchant.getStatus() != null) {
            entry.setSubscriberStatus(merchant.getStatus());
        }

        if (merchant.getSubscriber().getRestrictions() != null) {
            entry.setSubscriberRestrictions(merchant.getSubscriber().getRestrictions());
        }

        if (merchant.getAddressByMerchantAddressID() != null) {
            Address addr = merchant.getAddressByMerchantAddressID();

            entry.setMerchantAddressLine1(addr.getLine1());
            entry.setMerchantAddressLine2(addr.getLine2());
            entry.setMerchantAddressCity(addr.getCity());
            entry.setMerchantAddressState(addr.getState());
            entry.setMerchantAddressCountry(addr.getCountry());
            entry.setMerchantAddressZipcode(addr.getZipCode());
        }

        if (merchant.getAddressByFranchiseOutletAddressID() != null) {
            Address addr = merchant.getAddressByFranchiseOutletAddressID();

            entry.setOutletAddressLine1(addr.getLine1());
            entry.setOutletAddressLine2(addr.getLine2());
            entry.setOutletAddressCity(addr.getCity());
            entry.setOutletAddressState(addr.getState());
            entry.setOutletAddressCountry(addr.getCountry());
            entry.setOutletAddressZipcode(addr.getZipCode());
        }

        entry.setSubscriberRestrictionsText(enumTextService.getRestrictionsText(CmFinoFIX.TagID_SubscriberRestrictions, merchant.getSubscriber().getLanguage(), merchant.getSubscriber().getRestrictions().toString()));

        // Status comes from Merchant Table now.
        entry.setSubscriberStatusText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, merchant.getSubscriber().getLanguage(), merchant.getStatus().toString()));

        if (merchant.getDistributionChainTemplateID() != null) {
            entry.setDistributionChainName(DistributionChainTemplateService.getName(merchant.getDistributionChainTemplateID()));
        } else {
            entry.setDistributionChainName("");
        }

        if (merchant.getSubscriber().getUser() != null) {
            entry.setUsername(merchant.getSubscriber().getUser().getUsername());
        } else {
            entry.setUsername("");
        }

        if (merchant.getSubscriber().getID() != null) {
            entry.setSubscriberID(merchant.getSubscriber().getID());
        }
        if (merchant.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(merchant.getLastUpdateTime());
        }

        if (merchant.getUpdatedBy() != null) {
            entry.setUpdatedBy(merchant.getUpdatedBy());
        }

        if (merchant.getCreateTime() != null) {
            entry.setCreateTime(merchant.getCreateTime());
        }

        if (merchant.getCreatedBy() != null) {
            entry.setCreatedBy(merchant.getCreatedBy());
        }
        if (merchant.getAdminComment() != null) {
            entry.setAdminComment(merchant.getAdminComment());
        }
        MerchantCodeDAO mcdao = DAOFactory.getInstance().getMerchantCodeDAO();
        MerchantCodeQuery query = new MerchantCodeQuery();
        query.setMdn(entry.getMDN());
        List<MerchantCode> results = mcdao.get(query);
        if(results != null && results.size() == 1) {
            entry.setMerchantCode(results.get(0).getMerchantCode());
        }
            
        entry.setPartnerTypeText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PartnerType, merchant.getSubscriber().getLanguage(), merchant.getSubscriber().getPartnerType()));
    }

    private void logMessage(CMJSMerchant realMsg) {
        log.info("MDN = " + realMsg.getMDNSearch());
        log.info("First Name = " + realMsg.getFirstNameSearch());
        log.info("Last Name = " + realMsg.getLastNameSearch());
        log.info("Channel " + realMsg.getChannelSearch());
        log.info("Start Date " + realMsg.getStartDateSearch());
        log.info("End Date " + realMsg.getEndDateSearch());
        log.info("status " + realMsg.getStatusSearch());
        log.info("restrictions " + realMsg.getRestrictionsSearch());
    }

    private void logOutLetAddress(CGEntries entry) {
        log.info("entry.getOutletAddressLine1() = " + entry.getOutletAddressLine1());
        log.info("entry.getOutletAddressLine2() = " + entry.getOutletAddressLine2());
        log.info("entry.getOutletAddressCity() = " + entry.getOutletAddressCity());
        log.info("entry.getOutletAddressCountry() = " + entry.getOutletAddressCountry());
        log.info("entry.entry.getOutletAddressState() = " + entry.getOutletAddressState());
        log.info("entry.entry.getOutletAddressZipcode() = " + entry.getOutletAddressZipcode());
    }

    private void handleAddressException() throws AddressLine1RequiredException {
        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        error.setErrorDescription(MessageText._("Address Line1,City,State,Country,Zip Required"));
        WebContextError.addError(error);
        throw new AddressLine1RequiredException(MessageText._(" AddressLine1Required "));
    }

    private void logMerchantAddress(CGEntries entry) {
    	log.info("l1 =" + entry.getMerchantAddressLine1());
    	log.info("l2 = " + entry.getMerchantAddressLine2());
    	log.info("city= " + entry.getMerchantAddressCity());
    	log.info("state = " + entry.getMerchantAddressState());
    	log.info("country = " + entry.getMerchantAddressCountry());
    	log.info("zip = " + entry.getMerchantAddressZipcode());
    }

    private boolean validateUsername(CMJSMerchant.CGEntries e)
    {
        CMJSUsernameCheck chkUsername = new CmFinoFIX.CMJSUsernameCheck();
        chkUsername.setUsername(e.getUsername());
        chkUsername.setCheckIfExists(true);
        UsernameCheckProcessor processor = new UsernameCheckProcessor();
        CMJSError err = (CMJSError)processor.process(chkUsername);
        if(CmFinoFIX.ErrorCode_NoError.equals(err.getErrorCode())){
            return true;
        }
        return false;
    }
    private boolean isValidGroupAndParent(CMJSMerchant.CGEntries e) {
        if (UserService.isMerchant()) {
            return true;
        }
        ParentGroupIDCheckProcessor mcp = new ParentGroupIDCheckProcessor();
        CMJSParentGroupIdCheck msg = new CMJSParentGroupIdCheck();
        if (e == null) {
            return true;
        }
        if (e.getParentID() == null) {
            if (StringUtils.isEmpty(e.getGroupID())) {
                return true;
            } else {
                return false;
            }
        }
        msg.setID(e.getParentID());
        CMJSParentGroupIdCheck response = (CMJSParentGroupIdCheck) mcp.process(msg);
        if (!response.getAllowedForLOP()) {
            if (StringUtils.isEmpty(e.getGroupID())) {
                return true;
            } else {
                return false;
            }
        } else {
            if (StringUtils.isEmpty(e.getGroupID())) {
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean isValidMDNPresentInParentsRange(CMJSMerchant.CGEntries entry) {
        MerchantDAO dao = DAOFactory.getInstance().getMerchantDAO();
        Merchant parent = dao.getById(entry.getParentID());
        if(parent == null)
        {
            return true;
        }
        Long merchantid = entry.getID();
        String mdn = entry.getMDN();
        if (mdn == null) {
            Merchant merchant = dao.getById(merchantid);
            mdn = MerchantService.getMDNFromMerchant(merchant);
        }// Assumption is international code is of substring 2.
        return MDNRangeService.isMDNInParentsRange(Long.parseLong(mdn.substring(2)), parent);
    }
    private boolean isValidMDNPresentInParentsRange(CMJSMerchant.CGEntries entry, String mdn) {
        MerchantDAO dao = DAOFactory.getInstance().getMerchantDAO();
        Merchant parent = dao.getById(entry.getParentID());
        if(parent == null)
        {
            return true;
        }
        Long merchantid = entry.getID();
       // String mdn = entry.getMDN();
        if (mdn == null) {
            Merchant merchant = dao.getById(merchantid);
            mdn = MerchantService.getMDNFromMerchant(merchant);
        }// Assumption is international code is of substring 2.
        return MDNRangeService.isMDNInParentsRange(Long.parseLong(mdn.substring(2)), parent);
    }
}
