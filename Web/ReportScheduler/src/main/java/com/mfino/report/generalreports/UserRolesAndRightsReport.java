package com.mfino.report.generalreports;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.RoleDAO;
import com.mfino.dao.UserDAO;
import com.mfino.domain.Role;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.EnumTextService;
import com.mfino.util.ReportUtil;

public class UserRolesAndRightsReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 5;
	private String HEADER_ROW = "#,UserName,Role,LastUpdatedTime,UpdatedBy";
	private UserDAO userdao = DAOFactory.getInstance().getUserDAO();
	private RoleDAO roledao = DAOFactory.getInstance().getRoleDAO();
	private File report;
	private String reportName;
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)	
	protected File run(Date start, Date end, ReportBaseData basedata) {
		report =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		DateFormat df = getDateFormat();
		String formatStr = getFormatString(NUM_COLUMNS);
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" StartTime:"+startTime);
		try{
		//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(report)));
		//writer.println(HEADER_ROW);
			
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
		
		List<User> users =  userdao.getAll();
		int seq = 1;
		for(User user:users){
			Role role = roledao.getById(user.getRole());
			String rowContent = String.format(formatStr, 
					seq,
					user.getUsername(),
					role.getDisplayText(),
					df.format(user.getLastUpdateTime()),
					user.getUpdatedBy() 
					);
			xlsReport.addRowContent(rowContent);
			seq++;
		}
		xlsReport.writeToFileStream(report, HEADER_ROW, reportName);
		}catch (Exception e) {
			log.error("Error in "+reportName+":",e);
		}		
		log.info("Processing "+reportName+" StartTime:"+startTime+" EndTime:"+df.format(new Date()));
		return report;		
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
		// TODO Auto-generated method stub
		return report.getName();
	}
	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}

	
	/*public static void main(String a[]){
		UserRolesAndRightsReport urrr = new UserRolesAndRightsReport();
		urrr.run(null, null, null);
	}*/
	
}
