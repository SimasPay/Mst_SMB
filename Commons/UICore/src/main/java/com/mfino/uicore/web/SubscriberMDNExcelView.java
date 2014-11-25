/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberMDN;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.PocketService;
import com.mfino.uicore.fix.processor.SubscriberMdnProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

/**
 *
 * @author sunil
 */
@Component("SubscriberMDNExcelView")
public class SubscriberMDNExcelView extends AbstractExcelView {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private DateFormat df = DateUtil.getExcelDateFormat();
    private HashMap<Long, String> accountMap = null;
    
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService ;
    
    @Autowired
    @Qualifier("SubscriberMdnProcessorImpl")
    private SubscriberMdnProcessor subscriberMdnProcessor;
    void initializeWorkBook(HSSFSheet sheet, int currentRow) {
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, " ID");

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, "First Name");

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, "Last Name");

        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, "MDN");

        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, "Registration Time");

        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, "Account Number");

        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, "Status");

        HSSFCell header8 = getCell(sheet, currentRow, 7);
        setText(header8, "Self Suspended");

        HSSFCell header9 = getCell(sheet, currentRow, 8);
        setText(header9, "Suspended");

        HSSFCell header10 = getCell(sheet, currentRow, 9);
        setText(header10, "Security Locked");

        HSSFCell header11 = getCell(sheet, currentRow, 10);
        setText(header11, "Absolute Locked");
    }

    private void fillSubscriberMDNCells(CMJSSubscriberMDN.CGEntries smdn, HSSFRow row) {
        row.createCell(0).setCellValue(smdn.getID());
            row.createCell(1).setCellValue(smdn.getFirstName());
            row.createCell(2).setCellValue(smdn.getLastName());
            row.createCell(3).setCellValue(smdn.getMDN());
            if (smdn.getCreateTime() != null) {
                row.createCell(4).setCellValue(df.format(smdn.getCreateTime()));
            } else {
                row.createCell(4).setCellValue("");
            }
            
            if (accountMap != null) {
            	row.createCell(5).setCellValue(accountMap.get(smdn.getID()));
            }
            
            row.createCell(6).setCellValue(smdn.getSubscriberStatusText());
            int restrictions = smdn.getMDNRestrictions();
            if ((restrictions & CmFinoFIX.SubscriberRestrictions_SelfSuspended) > 0) {
                row.createCell(7).setCellValue(Boolean.TRUE);
            } else {
                row.createCell(7).setCellValue(Boolean.FALSE);
            }

            if ((restrictions & CmFinoFIX.SubscriberRestrictions_Suspended) > 0) {
                row.createCell(8).setCellValue(Boolean.TRUE);
            } else {
                row.createCell(8).setCellValue(Boolean.FALSE);
            }
            if ((restrictions & CmFinoFIX.SubscriberRestrictions_SecurityLocked) > 0) {
                row.createCell(9).setCellValue(Boolean.TRUE);
            } else {
                row.createCell(9).setCellValue(Boolean.FALSE);
            }

            if ((restrictions & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) > 0) {
                row.createCell(10).setCellValue(Boolean.TRUE);
            } else {
                row.createCell(10).setCellValue(Boolean.FALSE);
            }
    }

    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.setHeader("Content-Disposition", "attachment;filename=Subscribers.xls");
    	CMJSSubscriberMDN subMdn=new CMJSSubscriberMDN();
        String firstNameSearch = request.getParameter(CmFinoFIX.CMJSSubscriberMDN.FieldName_FirstNameSearch);
        if (firstNameSearch != null && firstNameSearch.trim().length() > 0) {
            subMdn.setFirstNameSearch(firstNameSearch);
        }
        String lastNameSearch = request.getParameter(CmFinoFIX.CMJSSubscriberMDN.FieldName_LastNameSearch);
        if (lastNameSearch != null && lastNameSearch.trim().length() > 0) {
            subMdn.setLastNameSearch(lastNameSearch);
        }
        String mdn = request.getParameter(CmFinoFIX.CMJSSubscriberMDN.FieldName_MDNSearch);
        if (mdn != null && mdn.trim().length() > 0) {
            subMdn.setMDNSearch(mdn);
        }
        
        String startTimeTxt = request.getParameter(CmFinoFIX.CMJSSubscriberMDN.FieldName_StartDateSearch);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (startTimeTxt != null) {
          Date parsedDate = dateFormat.parse(startTimeTxt);
          Timestamp ts = new Timestamp(parsedDate);
          subMdn.setStartDateSearch(ts);
        }
        String endTimeTxt = request.getParameter(CMJSSubscriberMDN.FieldName_EndDateSearch);
        if (endTimeTxt != null) {
          Date parsedDate = dateFormat.parse(endTimeTxt);
          Timestamp ts = new Timestamp(parsedDate);
          subMdn.setEndDateSearch(ts);
        }
        String cardPan = request.getParameter(CMJSSubscriberMDN.FieldName_CardPAN);
        if(StringUtils.isNotBlank(cardPan)){
        	subMdn.setCardPAN(cardPan);
        }
        String state = request.getParameter(CMJSSubscriberMDN.FieldName_UpgradeStateSearch);
        if(StringUtils.isNotBlank(state)){
        	subMdn.setUpgradeStateSearch(Integer.parseInt(state));
        }
        String status = request.getParameter(CMJSSubscriberMDN.FieldName_MDNStatus);
        if(StringUtils.isNotBlank(status)){
        	subMdn.setMDNStatus(Integer.parseInt(status));
        }
        subMdn.setaction(CmFinoFIX.JSaction_Select);
        subMdn.setSubscriberSearch(true);
        subMdn.setlimit(ConfigurationUtil.getExcelRowLimit());
        subMdn.setIsExcelDownload(true);
        
        try {
            HSSFSheet sheet = workbook.createSheet("Subscriber Excel Document");
            sheet.setDefaultColumnWidth(16);
            int currentRow = 0;
            initializeWorkBook(sheet, currentRow);
            CMJSSubscriberMDN processedList = (CMJSSubscriberMDN) subscriberMdnProcessor.process(subMdn);
            if (processedList.getEntries() != null) {
            	getMDNAccountMap(processedList);
	            for (CMJSSubscriberMDN.CGEntries subMdnLs : processedList.getEntries()) {
	                currentRow++;
	                HSSFRow row = sheet.createRow(currentRow);
	                fillSubscriberMDNCells(subMdnLs, row);
	            }
           }
        } catch (Exception error) {
            log.error("Error in Subscriber Excel Dowload ::" , error);
        }
    }
    
    private void getMDNAccountMap(CMJSSubscriberMDN processedList) {
    	List<Long> mdnLst = new ArrayList<Long>();
        for (CMJSSubscriberMDN.CGEntries subMdn : processedList.getEntries()) {
        	mdnLst.add(subMdn.getID());
        }
        
        List<Pocket> pocketLst = pocketService.getDefaultBankPocketByMdnList(mdnLst);
        if (CollectionUtils.isNotEmpty(pocketLst)) {
        	accountMap = new HashMap<Long, String>();
        	for (Pocket p: pocketLst) {
        		accountMap.put(p.getSubscriberMDNByMDNID().getID(), p.getCardPAN());
        	}
        }
    }
}
