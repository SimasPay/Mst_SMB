/**
 * 
 */
package com.mfino.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.BulkUploadFileDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.BulkUploadFile;
import com.mfino.domain.MerchantSyncRecord;
import com.mfino.domain.SubscriberSyncRecord;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.SubscriberService;
import com.mfino.util.HibernateUtil;

/**
 * @author Deva
 *
 */
public class BulkUploadTool {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String FIELD_SEPARATOR = "[|]";
    
	static {
		// this is required before start decoding fix messages
		CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
	}

    public void uploadData() throws Throwable {
        HibernateUtil.getCurrentSession().beginTransaction();
        BulkUploadFileDAO bulkUploadFileDAO = DAOFactory.getInstance().getBulkUploadFileDAO();
        List<BulkUploadFile> pendingFiles = bulkUploadFileDAO.getPendingFiles();
        log.debug("Number Pending Files to be processed = " + pendingFiles.size());
        HibernateUtil.getCurrentSession().getTransaction().commit();
        for (BulkUploadFile bulkUploadFile : pendingFiles) {
            HibernateUtil.getCurrentSession().beginTransaction();
            bulkUploadFile.setUploadFileStatus(CmFinoFIX.UploadFileStatus_Processing);
            bulkUploadFileDAO.save(bulkUploadFile);
            String currentDir = System.getProperty("user.dir");
            File reportFile = new File(currentDir + "/reportTemp." + bulkUploadFile.getID() + ".txt");
            FileOutputStream fstream = new FileOutputStream(reportFile);
            PrintStream out = new PrintStream(fstream);
            BufferedReader bufferedReader = new BufferedReader(new StringReader(bulkUploadFile.getFileData()));
            String strLine = null;
            int linecount = 0;
            int errorLineCount = 0;
            while ((strLine = bufferedReader.readLine()) != null) {
                if (strLine.length() == 0) {
                    // skip empty lines
                    continue;
                }
                linecount++;
                if (bulkUploadFile.getRecordType().equals(CmFinoFIX.RecordType_Subscriber)) {
                    int responseCode = handleSubscriberStr(strLine, bulkUploadFile.getCompany().getID());
                    if (responseCode == CmFinoFIX.SynchError_Success) {
                        out.println(strLine + "," + responseCode + ",Success");
                    } else {
                        String responseMessage = SubscriberService.getErrorMessage(responseCode);
                        if (responseMessage != null) {
                            responseMessage = responseMessage.replace(',', ':');
                        }
                        out.println(strLine + "," + responseCode + "," + responseMessage);
                        errorLineCount++;
                    }
                } else if (bulkUploadFile.getRecordType().equals(CmFinoFIX.RecordType_Merchant)) {
                    String errorMessage = null;
                    // We add special case for this since the MerchantProcessor is always returning the Generic Error Code
                    int responseCode = handleMerchantStr(strLine, bulkUploadFile.getCompany().getID(), errorMessage);
                    if (responseCode == CmFinoFIX.SynchError_Success) {
                        out.println(strLine + "," + responseCode + ",Success");
                    } else if (errorMessage != null) {
                        out.println(strLine + "," + responseCode + "," + errorMessage);
                        errorLineCount++;
                    } else {
                        String responseMessage = SubscriberService.getErrorMessage(responseCode);
                        if (responseMessage != null) {
                            responseMessage = responseMessage.replace(',', ':');
                        }
                        out.println(strLine + "," + responseCode + "," + responseMessage);
                        errorLineCount++;
                    }
                }
            }
            out.close();
            bulkUploadFile.setFileProcessedDate(new Timestamp());
            bulkUploadFile.setTotalLineCount(linecount);
            bulkUploadFile.setErrorLineCount(errorLineCount);
            bulkUploadFile.setUploadFileStatus(CmFinoFIX.UploadFileStatus_Processed);
            String reportString = IOUtils.toString(new FileReader(reportFile));
            bulkUploadFile.setUploadReport(reportString);
            bulkUploadFileDAO.save(bulkUploadFile);
            log.info("Processing Completed " + linecount);
//            try {
//                UserDAO userDAO = new UserDAO();
//                User user = userDAO.getByUserName(bulkUploadFile.getCreatedBy());
//                // send mail
//                String emailMsg = String.format(
//                        "Dear %s %s,\n\t File titled %s uploaded on %s has been processed. please check the report online.",
//                        user.getFirstName(), user.getLastName(),
//                        bulkUploadFile.getFileName(), bulkUploadFile.getCreateTime());
//                MailUtil.sendMailMultiX(user.getEmail(), user.getFirstName() + " " + user.getLastName(), "BSS Bulk Upload file processing Complete",
//                        emailMsg);
//            } catch (Exception ee) {
//                log.error("Failed to send User Add information.", ee);
//            }

            HibernateUtil.getCurrentSession().getTransaction().commit();
        }
    }

    /**
     * creates a new merchant object and persist it to the DB.
     *
     * Have to decide on the creation of subscriber for the merchant, if
     * subscriber record is not available
     *
     * @param strLine
     * @param errorMessage 
     * @return
     */
    private int handleMerchantStr(String strLine, Long companyID, String errorMessage) {
        String strTokens[] = strLine.split(FIELD_SEPARATOR);
        log.info("Result length = " + strTokens.length);
        if (strTokens.length < 36) {
            log.debug(String.format("Bad line format: %s", strLine));
            return CmFinoFIX.SynchError_Failed_Subscriber_Bad_Line_Format;
        }
        MerchantSyncRecord syncRecord = createMerchantSyncRecord(strTokens);

        // Mandatory fields check
        if (StringUtils.isBlank(syncRecord.getMdn())) {
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (MDN)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }
        if (StringUtils.isBlank(syncRecord.getUserName())) {
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (Username)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }
        if (StringUtils.isBlank(syncRecord.getLanguage())) {
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (Language)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }
        if (StringUtils.isBlank(syncRecord.getTimezone())) {
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (Timezone)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getCurrency())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (Currency)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getStatus())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (Status)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getParentId())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (ParentID)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getPartnerType())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (PartnerType)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getRegion())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (Region)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getLine1())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (Line1)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getCity())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (City)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getZip())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (ZIP)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getContactNumber())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (Contact Number)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getOrgType())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (Organization)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getYearEstablished())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (Year)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getOutletZip())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (outletzip)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }if(StringUtils.isBlank(syncRecord.getIndustryClassification())){
            log.info(MessageText._("Registration of Merchant failed, Missing mandatory fields (industry classification)"));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }
        /*MerchantService merchantService = new MerchantService();
        int responseCode = merchantService.createNewMerchant(syncRecord, companyID, errorMessage);*/
        int responseCode = 0;
        return responseCode;
    }

    /**
     * @param strTokens
     * @return
     */
    private MerchantSyncRecord createMerchantSyncRecord(String[] strTokens) {
        int index = 0;
        MerchantSyncRecord syncRecord = new MerchantSyncRecord();
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setMdn(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setUserName(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setLanguage(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setTimezone(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setGroupId(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setCurrency(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setDistChainTemplate(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setStatus(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setParentId(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setPartnerType(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setRegion(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setFirstName(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setLastName(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setTradeName(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setEmail(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setAdminComment(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setNotificationMethod(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setLine1(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setLine2(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setCity(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setState(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setCountry(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setZip(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setOutletType(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setContactNumber(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setFaxNumber(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setOrgType(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setIndustryClassification(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setWebsiteURL(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setOutletCnt(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setYearEstablished(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setOutletLine1(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setOutletLine2(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setOutletCity(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setOutletState(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setOutletCountry(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setOutletZip(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setRepresentativeName(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setPosition(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setRepContactNumber(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setOutletFaxNumber(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setOutletEmail(strTokens[index++]);
        if (!hasNextToken(strTokens, index)) {
            return syncRecord;
        }
        syncRecord.setSrcIP(strTokens[index++]);
        return syncRecord;
    }

    /**
     *
     * @param strLine
     * @return
     */
    private int handleSubscriberStr(String strLine, Long companyID) {
        String strTokens[] = strLine.split(FIELD_SEPARATOR);
        int responseCode = CmFinoFIX.SynchError_Failed_Other;
        if (strTokens.length < 12) {
            log.debug(String.format("Bad line format: %s", strLine));
            return CmFinoFIX.SynchError_Failed_Subscriber_Bad_Line_Format;
        } else {
            SubscriberSyncRecord subscriberSyncRecord = createSubscriberSyncRecord(strTokens);
            if (subscriberSyncRecord != null) {
                responseCode = createSubscriber(subscriberSyncRecord, companyID);
//                if ("1001".equals(subscriberSyncRecord.getServiceType())) {
//                    responseCode = createSubscriber(subscriberSyncRecord);
//                } else if ("1002".equals(subscriberSyncRecord.getServiceType())) {
//                    responseCode = updateSubscriber(subscriberSyncRecord);
//                } else if ("1003".equals(subscriberSyncRecord.getServiceType())) {
//                    responseCode = retireSubscriber(subscriberSyncRecord);
//                } else if ("1004".equals(subscriberSyncRecord.getServiceType())) {
//                    responseCode = updateMDN(subscriberSyncRecord);
//                } else if ("1005".equals(subscriberSyncRecord.getServiceType())) {
//                    responseCode = handleLowBalanceNotif(subscriberSyncRecord);
//                } else {
//                    responseCode = CmFinoFIX.SynchError_Failed_Subscriber_Invalid_Service_Type;
//                }
            }
        }
        return responseCode;
    }

    /**
     * @param subscriberSyncRecord
     * @return
     */
//    private int handleLowBalanceNotif(SubscriberSyncRecord subscriberSyncRecord) {
//        if (StringUtils.isBlank(subscriberSyncRecord.getMdn())) {
//            log.error("Updation of subscriber failed, Missing mandatory fields - " + subscriberSyncRecord.getMdn());
//            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
//        }
//        SubscriberService subscriberService = new SubscriberService();
//        int errorCode = subscriberService.lowBalanceNotif(subscriberSyncRecord);
//        return errorCode;
//    }
    /**
     * @param subscriberSyncRecord
     * @return
     */
    private int createSubscriber(SubscriberSyncRecord syncRecord, Long companyID) {
        int responseCode = 1;
        if (StringUtils.isBlank(syncRecord.getFirstName()) || StringUtils.isBlank(syncRecord.getLastName()) || syncRecord.getLanguage()==null || StringUtils.isBlank(syncRecord.getMdn()) // || StringUtils.isBlank(syncRecord.getImsi())
                || StringUtils.isBlank(syncRecord.getMarketingCategory()) || StringUtils.isBlank(syncRecord.getProduct()) || StringUtils.isBlank(syncRecord.getStatus()) || StringUtils.isBlank(syncRecord.getActiveAccountType())) {
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        } else {
            if (!"ACTIVE".equalsIgnoreCase(syncRecord.getStatus())) {
                return CmFinoFIX.SynchError_Failed_Missing_Status;
            }
            if (syncRecord.getMdn().length() <= 2 || syncRecord.getMdn().length() > 16) {
                return CmFinoFIX.SynchError_Failed_Invalid_MDN;
            }
            SubscriberService subscriberService = new SubscriberService();
            try {
                responseCode = subscriberService.createNewSubscriber(syncRecord, companyID);
            } catch (Exception exp) {
                responseCode = CmFinoFIX.SynchError_Failed_Other;
                log.warn("SynchError Failed Other", exp);
            }
        }
        return responseCode;
    }

    /**
     * @param subscriberSyncRecord
     * @return
     */
    private int updateSubscriber(SubscriberSyncRecord syncRecord) {
        int responseCode = 1;
        if (StringUtils.isBlank(syncRecord.getFirstName()) || StringUtils.isBlank(syncRecord.getLastName()) || StringUtils.isBlank(syncRecord.getLastName()) || syncRecord.getLanguage()==null || StringUtils.isBlank(syncRecord.getMdn()) || StringUtils.isBlank(syncRecord.getImsi()) || StringUtils.isBlank(syncRecord.getMarketingCategory())) {
            log.error(MessageText._("Registration of subscriber failed, Missing mandatory fields "));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        } else {
            SubscriberService subscriberService = new SubscriberService();
            try {
                responseCode = subscriberService.updateSubscriber(syncRecord);
            } catch (Exception exp) {
                responseCode = CmFinoFIX.SynchError_Failed_Other;
                log.warn("SynchError Failed Other", exp);
            }
        }
        return responseCode;
    }

    /**
     * @param subscriberSyncRecord
     * @return
     */
    private int retireSubscriber(SubscriberSyncRecord syncRecord) {
        if (StringUtils.isBlank(syncRecord.getMdn())) {
            log.error("Updation of subscriber failed, Missing mandatory fields - " + syncRecord.getMdn());
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }
        SubscriberService subscriberService = new SubscriberService();
        int errorCode = subscriberService.updateSubscriberRetiered(syncRecord);
        return errorCode;
    }

    /**
     * @param subscriberSyncRecord
     * @return
     */
    private int updateMDN(SubscriberSyncRecord subscriberSyncRecord) {
        if (StringUtils.isBlank(subscriberSyncRecord.getMdn()) || StringUtils.isBlank(subscriberSyncRecord.getOrgMDN()) || StringUtils.isBlank(subscriberSyncRecord.getNewMDN())) {
            log.error("Updation of subscriber failed, Missing mandatory fields ");
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }
        SubscriberService subscriberService = new SubscriberService();
        int errorCode = subscriberService.updateSubscriberMDN(subscriberSyncRecord);
        return errorCode;
    }

    /**
     * @param result
     */
    private SubscriberSyncRecord createSubscriberSyncRecord(String[] result) {
        int index = 1;
//        String serviceType = result[index++];
        String mdn = result[index++];
        String imsi = result[index++];
        String marketingCategory = result[index++];
        String product = result[index++];
        String firstName = result[index++];
        String lastName = result[index++];
        String email = result[index++];
        Integer language = Integer.valueOf(result[index++]);
        String currency = result[index++];
        String activeAccountType = result[index++];
        String mdnStatus = result[index++];
        SubscriberSyncRecord subscriberSyncRecord = new SubscriberSyncRecord(mdn, firstName, lastName, email,
                language, currency, activeAccountType, null, null,
                null, null, null, null, null, imsi,
                marketingCategory, product);
        subscriberSyncRecord.setBulkUploadRecord(true);
        subscriberSyncRecord.setServiceType(1001);
        subscriberSyncRecord.setStatus(mdnStatus);
        if (!hasNextToken(result, index)) {
            return subscriberSyncRecord;
        }
        subscriberSyncRecord.setIdType(result[index++]);
        if (!hasNextToken(result, index)) {
            return subscriberSyncRecord;
        }
        subscriberSyncRecord.setIdNumber(result[index++]);
        if (!hasNextToken(result, index)) {
            return subscriberSyncRecord;
        }
        subscriberSyncRecord.setGender(result[index++]);
        if (!hasNextToken(result, index)) {
            return subscriberSyncRecord;
        }
        subscriberSyncRecord.setAddress(result[index++]);
        if (!hasNextToken(result, index)) {
            return subscriberSyncRecord;
        }
        subscriberSyncRecord.setCity(result[index++]);
        if (!hasNextToken(result, index)) {
            return subscriberSyncRecord;
        }
        subscriberSyncRecord.setBirthPlace(result[index++]);
        if (!hasNextToken(result, index)) {
            return subscriberSyncRecord;
        }
        String birthDate = result[index++];
        try {
            Date dateOfBirth = SimpleDateFormat.getInstance().parse(birthDate);
            subscriberSyncRecord.setDateOfBirth(dateOfBirth);
        } catch (ParseException parseExp) {
            log.error(parseExp.getMessage(), parseExp);
        }
        if (!hasNextToken(result, index)) {
            return subscriberSyncRecord;
        }
        subscriberSyncRecord.setOrgMDN(result[index++]);
        if (!hasNextToken(result, index)) {
            return subscriberSyncRecord;
        }
        subscriberSyncRecord.setNewMDN(result[index++]);
        if (!hasNextToken(result, index)) {
            return subscriberSyncRecord;
        }
        subscriberSyncRecord.setBalance(result[index++]);

        return subscriberSyncRecord;
    }

    private static boolean hasNextToken(String[] array, int index) {
        return index < array.length;
    }
}
