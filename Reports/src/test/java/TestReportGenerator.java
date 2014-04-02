import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dimo.fuse.reports.ReportParameters;
import com.dimo.fuse.reports.ReportTool;


public class TestReportGenerator {
	
	public static void main(String[] args) 
	{
		ReportTool reportUtil = new ReportTool();
		ReportParameters reportparams = new ReportParameters();
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = dateFormat.parse("2014-01-01");
			Date endDate = dateFormat.parse("2014-02-14");
			reportparams.setStartTime(startDate);
			reportparams.setEndTime(endDate);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//reportparams.setStartTime("2014-01-01");
		//reportparams.setEndTime("2014-02-14");
		reportparams.setUserName("System");
		reportparams.setDestinationFolder("D:\\Reports\\test");
		//ReportTool.generateReports("src\\main\\resources\\reports\\subscriberReport-Basic", reportparams);
		ReportTool.generateReports("src\\main\\resources\\reports\\subscriberReport-Detailed", reportparams);
		
		//reportUtil.generateReports("src\\main\\resources\\subscriberReport-Basic", "D:\\Reports\\test", "2014-01-01", "2014-02-14", "System");
	}
}
