/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.mfino.domain.MfinoUser;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSUsers;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.fix.processor.UserProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

/**
 *
 * @author sunil
 */
@Component("UserExcelView")
public class UserExcelView extends AbstractExcelView {

    private static final Integer start = 0;
    private DateFormat df = DateUtil.getExcelDateFormat();
    
    @Autowired
    @Qualifier("UserProcessorImpl")
    private UserProcessor userProcessor;
    
    void initializeWorkBook(HSSFSheet sheet, int currentRow) {

        //WRITE ROW FOR HEADER
        HSSFCell header1 = getCell(sheet, currentRow, 0);
        setText(header1, MfinoUser.FieldName_Username);

        HSSFCell header2 = getCell(sheet, currentRow, 1);
        setText(header2, MfinoUser.FieldName_FirstName);

        HSSFCell header3 = getCell(sheet, currentRow, 2);
        setText(header3, MfinoUser.FieldName_LastName);
        
        HSSFCell header4 = getCell(sheet, currentRow, 3);
        setText(header4, MfinoUser.FieldName_Role);
        
        HSSFCell header5 = getCell(sheet, currentRow, 4);
        setText(header5, MfinoUser.FieldName_UserStatus);
        
        HSSFCell header6 = getCell(sheet, currentRow, 5);
        setText(header6, MfinoUser.FieldName_CreateTime);
        
        HSSFCell header7 = getCell(sheet, currentRow, 6);
        setText(header7, MfinoUser.FieldName_CreatedBy);
        
        HSSFCell header8 = getCell(sheet, currentRow, 7);
        setText(header8, "Security Locked");
        
        HSSFCell header9 = getCell(sheet, currentRow, 8);
        setText(header9, "Suspend");
        
        HSSFCell header10 = getCell(sheet, currentRow, 9);
        setText(header10, "ConfirmationTime");
        
        HSSFCell header11 = getCell(sheet, currentRow, 10);
        setText(header11, "RejectionTime");
        
        HSSFCell header12 = getCell(sheet, currentRow, 11);
        setText(header12, "ExpirationTime");
        
        HSSFCell header13 = getCell(sheet, currentRow, 12);
        setText(header13, "ActivationTime");       
    }

    private void fillUserCells(CMJSUsers.CGEntries user, HSSFRow row) {
        row.createCell(0).setCellValue(user.getUsername());
        
        row.createCell(1).setCellValue(user.getFirstName());
        if(user.getLastName()!=null){
        row.createCell(2).setCellValue(user.getLastName());
        }
        if(user.getRole()!=null){
            row.createCell(3).setCellValue(user.getRoleText());
        }
        if(user.getUserStatus()!=null){
            row.createCell(4).setCellValue(user.getUserStatusText());
        }
        if(user.getCreateTime()!=null){
            row.createCell(5).setCellValue(df.format(user.getCreateTime()));
        }
        if(user.getCreatedBy()!=null){
            row.createCell(6).setCellValue(user.getCreatedBy());
        }
        if(user.getUserSecurityLocked()!=null){
            row.createCell(7).setCellValue(""+user.getUserSecurityLocked());
        }
        if(user.getUserSecurityLocked()!=null){
            row.createCell(8).setCellValue(""+user.getUserSuspended());
        }
        if(user.getConfirmationTime()!=null){
            row.createCell(9).setCellValue(df.format(user.getConfirmationTime()));
        }
        if(user.getRejectionTime()!=null){
            row.createCell(10).setCellValue(df.format(user.getRejectionTime()));
        }
        if(user.getExpirationTime()!=null){
            row.createCell(11).setCellValue(df.format(user.getExpirationTime()));
        }
        if(user.getUserActivationTime()!=null){
            row.createCell(12).setCellValue(df.format(user.getUserActivationTime()));
        }
        
        
    }

    @SuppressWarnings("rawtypes")
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	response.setHeader("Content-Disposition", "attachment;filename=Users.xls");
    	CMJSUsers jsUser = new CMJSUsers();
        if(StringUtils.isNotBlank(request.getParameter(CMJSUsers.FieldName_FirstNameSearch))){
        jsUser.setFirstNameSearch(request.getParameter(CMJSUsers.FieldName_FirstNameSearch));
        }
        if(StringUtils.isNotBlank(request.getParameter(CMJSUsers.FieldName_LastNameSearch))){
        jsUser.setLastNameSearch(request.getParameter(CMJSUsers.FieldName_LastNameSearch));
        }
        if(StringUtils.isNotBlank(request.getParameter(CMJSUsers.FieldName_UsernameSearch))){
        jsUser.setUsernameSearch(request.getParameter(CMJSUsers.FieldName_UsernameSearch));
        }
        if(StringUtils.isNotBlank(request.getParameter(CMJSUsers.FieldName_IsRequestFromCCReviewerTab))){
        	jsUser.setIsRequestFromCCReviewerTab(Boolean.parseBoolean(request.getParameter(CMJSUsers.FieldName_IsRequestFromCCReviewerTab)));
        }
        String status = request.getParameter(CMJSUsers.FieldName_StatusSearch);
        if (StringUtils.isNotBlank(status)) {
            jsUser.setStatusSearch(status);
        }
        
        String creationDateStartDate = request.getParameter(CMJSUsers.FieldName_CreationDateStartTime);
        DateFormat df = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SS");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (creationDateStartDate != null && creationDateStartDate.trim().length() > 0) {
            Date stDate = df.parse(creationDateStartDate);
            Timestamp tstDate = new Timestamp(stDate);
            jsUser.setCreationDateStartTime(tstDate);
        }
        String creationDateEndDate = request.getParameter(CMJSUsers.FieldName_CreationDateEndTime);
        if (creationDateEndDate != null && creationDateEndDate.trim().length() > 0) {
            Date enDate = df.parse(creationDateEndDate);
            Timestamp tenDate = new Timestamp(enDate);
            jsUser.setCreationDateEndTime(tenDate);
        }
        
        String confirmationDateStartTime = request.getParameter(CMJSUsers.FieldName_ConfirmationDateStartTime);
        if (confirmationDateStartTime != null && confirmationDateStartTime.trim().length() > 0) {
            Date stDate = df.parse(confirmationDateStartTime);
            Timestamp tstDate = new Timestamp(stDate);
            jsUser.setConfirmationDateStartTime(tstDate);
        }
        String confirmationDateEndTime = request.getParameter(CMJSUsers.FieldName_ConfirmationDateEndTime);
        if (confirmationDateEndTime != null && confirmationDateEndTime.trim().length() > 0) {
            Date enDate = df.parse(confirmationDateEndTime);
            Timestamp tenDate = new Timestamp(enDate);
            jsUser.setConfirmationDateEndTime(tenDate);
        }
        
        String userActivationStartTime = request.getParameter(CMJSUsers.FieldName_UserActivationStartTime);
        if (userActivationStartTime != null && userActivationStartTime.trim().length() > 0) {
            Date stDate = df.parse(userActivationStartTime);
            Timestamp tstDate = new Timestamp(stDate);
            jsUser.setUserActivationStartTime(tstDate);
        }
        String userActivationEndTime = request.getParameter(CMJSUsers.FieldName_UserActivationEndTime);
        if (userActivationEndTime != null && userActivationEndTime.trim().length() > 0) {
            Date enDate = df.parse(userActivationEndTime);
            Timestamp tenDate = new Timestamp(enDate);
            jsUser.setUserActivationEndTime(tenDate);
        }
        
        String lastUpdateStartTime = request.getParameter(CMJSUsers.FieldName_LastUpdateStartTime);
        if (lastUpdateStartTime != null && lastUpdateStartTime.trim().length() > 0) {
            Date stDate = df.parse(lastUpdateStartTime);
            Timestamp tstDate = new Timestamp(stDate);
            jsUser.setLastUpdateStartTime(tstDate);
        }
        String lastUpdateEndTime = request.getParameter(CMJSUsers.FieldName_LastUpdateEndTime);
        if (lastUpdateEndTime != null && lastUpdateEndTime.trim().length() > 0) {
            Date enDate = df.parse(lastUpdateEndTime);
            Timestamp tenDate = new Timestamp(enDate);
            jsUser.setLastUpdateEndTime(tenDate);
        }     
        
//        String restrictions = request.getParameter(CMJSUsers.FieldName_RestrictionsSearch);
//        if (restrictions != null && restrictions.trim().length() > 0) {
//            jsUser.setRestrictionsSearch(restrictions);
//        }
//
//        String roleSearch = request.getParameter(CMJSUsers.FieldName_RoleSearch);
//        if (roleSearch != null && roleSearch.trim().length() > 0) {
//            jsUser.setRoleSearch(roleSearch);
//        }
        jsUser.setstart(start);
        jsUser.setlimit(ConfigurationUtil.getExcelRowLimit());
        jsUser.setaction(CmFinoFIX.JSaction_Select);
        CMJSUsers processedList = (CMJSUsers) userProcessor.process(jsUser);

        HSSFSheet sheet = workbook.createSheet("Users");
        sheet.setDefaultColumnWidth(16);
        int currentRow = 0;
        initializeWorkBook(sheet, currentRow);
        if (processedList.getEntries() != null) {
        for (CMJSUsers.CGEntries user : processedList.getEntries()) {
            currentRow++;
            HSSFRow row = sheet.createRow(currentRow);
            fillUserCells(user, row);
        }
    }
    }
}
