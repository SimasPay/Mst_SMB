/**
 * 
 */
package com.mfino.uicore.fix.processor.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.BulkUploadFileDAO;
import com.mfino.dao.BulkUploadFileEntryDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.BulkUploadFileQuery;
import com.mfino.domain.BulkUploadFile;
import com.mfino.domain.BulkUploadFileEntry;
import com.mfino.domain.MerchantSyncRecord;
import com.mfino.domain.SubscriberSyncRecord;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBulkUploadFile;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.service.SubscriberService;
import com.mfino.service.UserService;
import com.mfino.service.impl.SubscriberServiceImpl;
import com.mfino.service.impl.UserServiceImpl;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.BulkUploadFileProcessor;
import com.mfino.util.DateUtil;

/**
 * @author Deva
 * 
 */
@Service("BulkUploadFileProcessorImpl")
public class BulkUploadFileProcessorImpl extends BaseFixProcessor implements BulkUploadFileProcessor{

    
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String FIELD_SEPARATOR = "[|]";
    private static final String FIELD_SEPARATOR_COMMA = ",";

    @Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
    
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

    public BulkUploadFileProcessorImpl() {
    }


	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

    /*
     * (non-Javadoc)
     *
     * @see com.mfino.fix.processor.IFixProcessor#process(com.mfino.fix.CFIXMsg)
     */
    @Override
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        // TODO Auto-generated method stub
        BulkUploadFileDAO dao = DAOFactory.getInstance().getBulkUploadFileDAO();
        CMJSBulkUploadFile realMsg = (CMJSBulkUploadFile) msg;

        if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
            BulkUploadFileQuery query = new BulkUploadFileQuery();
            query.setAssociationOrdered(true);
            if (realMsg.getUploadFileStatusSearch() != null) {
                query.setUploadFileStatusSearch(realMsg.getUploadFileStatusSearch());
            }
            if (realMsg.getStartDateSearch() != null) {
                query.setStartDate(realMsg.getStartDateSearch());
                Date endDate = DateUtil.addDays(query.getStartDate(), 1);
                query.setEndDate(endDate);
            }
            if (realMsg.getFileTypeSearch() != null) {
                query.setRecordType(realMsg.getFileTypeSearch());
            }
            if (userService.getUserCompany() != null) {
                query.setCompany(userService.getUserCompany());
            }
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());

            List<BulkUploadFile> results = dao.get(query);
            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                BulkUploadFile s = results.get(i);
                CMJSBulkUploadFile.CGEntries entry =
                        new CMJSBulkUploadFile.CGEntries();
                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        }
        return realMsg;
    }

    public void updateMessage(BulkUploadFile bu, CMJSBulkUploadFile.CGEntries entry) {
        entry.setID(bu.getID());

        if (bu.getDescription() != null) {
            entry.setDescription(bu.getDescription());
        }
        if (bu.getFileName() != null) {
            entry.setFileName(bu.getFileName());
        }
        if (bu.getUploadFileStatus() != null) {
            entry.setUploadFileStatus(bu.getUploadFileStatus());
        }
        if (bu.getRecordType() != null) {
            entry.setRecordType(bu.getRecordType());
        }
        if (bu.getCreateTime() != null) {
            entry.setCreateTime(bu.getCreateTime());
        }
        if (bu.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(bu.getLastUpdateTime());
        }
        if (bu.getCreatedBy() != null) {
            entry.setCreatedBy(bu.getCreatedBy());
        }
        if (bu.getUpdatedBy() != null) {
            entry.setUpdatedBy(bu.getUpdatedBy());
        }
        if (bu.getTotalLineCount() != null) {
            entry.setTotalLineCount(bu.getTotalLineCount());
        }
        if (bu.getErrorLineCount() != null) {
            entry.setErrorLineCount(bu.getErrorLineCount());
        }
        entry.setUploadStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_UploadFileStatus, null, entry.getUploadFileStatus()));
        entry.setRecordTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_RecordType, null, entry.getRecordType()));
    }

//    public void processPendingFiles() throws Throwable {
//        BulkUploadFileDAO bulkUploadFileDAO = DAOFactory.getInstance().getBulkUploadFileDAO();
//        List<BulkUploadFile> pendingFiles = bulkUploadFileDAO.getPendingFiles();
//        log.debug("Number Pending Files to be processed = " + pendingFiles.size());
//        for (BulkUploadFile bulkUploadFile : pendingFiles) {
//            bulkUploadFile.setUploadFileStatus(CmFinoFIX.UploadFileStatus_Processing);
//            bulkUploadFileDAO.save(bulkUploadFile);
//            String currentDir = System.getProperty("user.dir");
//            File reportFile = new File(currentDir + "/reportTemp." + bulkUploadFile.getID() + ".txt");
//            FileOutputStream fstream = new FileOutputStream(reportFile);
//            PrintStream out = new PrintStream(fstream);
//            BufferedReader bufferedReader = new BufferedReader(new StringReader(bulkUploadFile.getFileData()));
//            String strLine = null;
//            int linecount = 0;
//            int errorLineCount = 0;
//            while ((strLine = bufferedReader.readLine()) != null) {
//                if (strLine.length() == 0) {
//                    // skip empty lines
//                    continue;
//                }
//                linecount++;
//                if (this.recordType == CmFinoFIX.RecordType_SubscriberFullyBanked) {
//                    int responseCode = handleSubscriberStr(strLine);
//                    if (responseCode == CmFinoFIX.SynchError_Success) {
//                        out.println(strLine + "," + responseCode + ",Success");
//                    } else {
//                        String responseMessage = subscriberService.getErrorMessage(responseCode);
//                        if (responseMessage != null) {
//                            responseMessage = responseMessage.replace(',', ':');
//                        }
//                        out.println(strLine + GeneralConstants.RESPONSE_SEPARATOR + responseCode + GeneralConstants.RESPONSE_SEPARATOR + responseMessage);
//                        errorLineCount++;
//                    }
//                } else if (this.recordType == CmFinoFIX.RecordType_Agent) {
//                    int responseCode = handleMerchantStr(strLine);
//                    if (responseCode == CmFinoFIX.SynchError_Success) {
//                        out.println(strLine + "," + responseCode + ",Success");
//                    } else {
//                        String responseMessage = subscriberService.getErrorMessage(responseCode);
//                        if (responseMessage != null) {
//                            responseMessage = responseMessage.replace(',', ':');
//                        }
//                        out.println(strLine + GeneralConstants.RESPONSE_SEPARATOR + responseCode +GeneralConstants.RESPONSE_SEPARATOR + responseMessage);
//                        errorLineCount++;
//                    }
//                }
//                log.info("Processing Completed " + linecount);
//            }
//            out.close();
//            bulkUploadFile.setTotalLineCount(linecount);
//            bulkUploadFile.setErrorLineCount(errorLineCount);
//            bulkUploadFile.setUploadFileStatus(CmFinoFIX.UploadFileStatus_Processed);
//            String reportString = IOUtils.toString(new FileReader(reportFile));
//            bulkUploadFile.setUploadReport(reportString);
//            bulkUploadFileDAO.save(bulkUploadFile);
//
//        }
//    }

    /**
     * loops the subscriber bulk upload file and persists the subscriber records
     *
     * @param file
     * @param desc2 
     * @param linecount 
     * @param recordType2 
     */
    public void processFileData(MultipartFile file, int recordType, int count, String desc) throws Throwable {
        // set bulk upload status to 'Uploading' and save an entry
        BulkUploadFileDAO bulkUploadFileDAO = DAOFactory.getInstance().getBulkUploadFileDAO();        
        BulkUploadFile bulkUploadFile = new BulkUploadFile();        
        bulkUploadFile.setFileName(file.getOriginalFilename());
        bulkUploadFile.setRecordType(recordType);
        bulkUploadFile.setDescription(desc);
        bulkUploadFile.setRecordCount(count);
        bulkUploadFile.setUploadFileStatus(CmFinoFIX.UploadFileStatus_Uploading);
        bulkUploadFile.setCompany(userService.getUserCompany());
        bulkUploadFileDAO.save(bulkUploadFile);
        
        // add each line data as entry into bulkupload_entry table
        BulkUploadFileEntryDAO bulkUploadFileEntryDAO = DAOFactory.getInstance().getBulkUploadFileEntryDAO();
        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(bis));
        String strLine = null;
		int linecount = 0;
		while ((strLine = br.readLine()) != null) {
			if (strLine.length() == 0) {
				// skip empty lines
				continue;
			}
			linecount++;
			log.info("Persisting record at line number: " + linecount);
			BulkUploadFileEntry bulkUploadFileEntry = new BulkUploadFileEntry();
			bulkUploadFileEntry.setBulkUploadFile(bulkUploadFile);
			bulkUploadFileEntry.setBulkUploadFileEntryStatus(CmFinoFIX.BulkUploadFileEntryStatus_Initialized);
			bulkUploadFileEntry.setLineNumber(linecount);
			bulkUploadFileEntry.setLineData(strLine);
			bulkUploadFileEntryDAO.save(bulkUploadFileEntry);
		}
		
		log.debug("Persisted the file and line entries");
		//update bulk upload file status
		bulkUploadFile.setUploadFileStatus(CmFinoFIX.UploadFileStatus_Uploaded);
		bulkUploadFile.setTotalLineCount(linecount);
		bulkUploadFileDAO.save(bulkUploadFile);
    }

    /**
     * creates a new merchant object and persist it to the DB.
     *
     * Have to decide on the creation of subscriber for the merchant, if
     * subscriber record is not available
     *
     * @param strLine
     * @return
     */
    private int handleMerchantStr(String strLine) {
        String strTokens[] = strLine.split(FIELD_SEPARATOR);
        if(strTokens.length==1)
        {
        	strTokens = strLine.split(FIELD_SEPARATOR_COMMA);
        }
        String errorMessage = "";
        log.info("Result length = " + strTokens.length);
        if (strTokens.length < 36) {
            log.debug(String.format("Bad line format: %s", strLine));
            return CmFinoFIX.SynchError_Failed_Subscriber_Bad_Line_Format;
        }
        MerchantSyncRecord syncRecord = createMerchantSyncRecord(strTokens);

        // Mandatory fields check
        if (StringUtils.isBlank(syncRecord.getMdn()) || StringUtils.isBlank(syncRecord.getUserName()) || StringUtils.isBlank(syncRecord.getLanguage()) || StringUtils.isBlank(syncRecord.getTimezone()) || StringUtils.isBlank(syncRecord.getCurrency()) || StringUtils.isBlank(syncRecord.getStatus()) || StringUtils.isBlank(syncRecord.getParentId()) || StringUtils.isBlank(syncRecord.getPartnerType()) || StringUtils.isBlank(syncRecord.getRegion()) || StringUtils.isBlank(syncRecord.getLine1()) || StringUtils.isBlank(syncRecord.getCity()) || StringUtils.isBlank(syncRecord.getZip()) || StringUtils.isBlank(syncRecord.getContactNumber()) || StringUtils.isBlank(syncRecord.getOrgType()) || StringUtils.isBlank(syncRecord.getYearEstablished()) || StringUtils.isBlank(syncRecord.getOutletZip()) || StringUtils.isBlank(syncRecord.getIndustryClassification())) {
            log.error(MessageText._("Registration of Merchant failed, Missing mandatory fields "));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        }
        /*MerchantService merchantService = new MerchantService();
        int responseCode = merchantService.createNewMerchant(syncRecord,null,errorMessage);*/
        int responseCode = 0;
        return responseCode;
    }

    /**
     * @param strTokens
     * @return
     */
    private MerchantSyncRecord createMerchantSyncRecord(String[] strTokens) {
        int index = 1;
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
    private int handleSubscriberStr(String strLine) {
        String strTokens[] = strLine.split(FIELD_SEPARATOR);
        if(strTokens.length==1)
        {
        	strTokens = strLine.split(FIELD_SEPARATOR_COMMA);
        }
        int responseCode = CmFinoFIX.SynchError_Failed_Other;
        if (strTokens.length < 12) {
            log.debug(String.format("Bad line format: %s", strLine));
            return CmFinoFIX.SynchError_Failed_Subscriber_Bad_Line_Format;
        } else {
            SubscriberSyncRecord subscriberSyncRecord = createSubscriberSyncRecord(strTokens);
            if (subscriberSyncRecord != null) {
                responseCode = createSubscriber(subscriberSyncRecord);
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
    private int createSubscriber(SubscriberSyncRecord syncRecord) {
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
            try {
                responseCode = subscriberService.createNewSubscriber(syncRecord);
            } catch (Exception exp) {
                responseCode = CmFinoFIX.SynchError_Failed_Other;
                log.error("SynchError ", exp);
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
        if (StringUtils.isBlank(syncRecord.getFirstName()) || StringUtils.isBlank(syncRecord.getLastName()) || StringUtils.isBlank(syncRecord.getLastName()) || syncRecord.getLanguage()==null|| StringUtils.isBlank(syncRecord.getMdn()) || StringUtils.isBlank(syncRecord.getImsi()) || StringUtils.isBlank(syncRecord.getMarketingCategory())) {
            log.error(MessageText._("Registration of subscriber failed, Missing mandatory fields "));
            return CmFinoFIX.SynchError_Missing_Mandatory_Fields;
        } else {

            try {
                responseCode = subscriberService.updateSubscriber(syncRecord);
            } catch (Exception exp) {
                responseCode = CmFinoFIX.SynchError_Failed_Other;
                log.error("SynchError ", exp);
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
        int errorCode = subscriberService.updateSubscriber(subscriberSyncRecord);
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
        } catch (ParseException exp) {
        	log.error("DOB parsing error ", exp);
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


	public static void main(String[] args)
    {
    	String pipe = "AA | BB | CC";
    	String comma = "AA , BB , CC";
    	
    	String strLine = comma;
    	String strTokens[] = strLine.split(FIELD_SEPARATOR);
    	System.out.println("Lengths "+strTokens.length);
        if(strTokens.length==1)
        {
        	strTokens = strLine.split(FIELD_SEPARATOR_COMMA);
        }
        System.out.println("Lengths "+strTokens.length);
    }
}
