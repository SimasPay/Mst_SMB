/**
 * This AML Report executes on first of every month.
 * The report gives the cumulative total transfers/transfer to unregistered done by all subscribers (grouped by idnumber) in the previous month.
 * Criteria1 in the report tells whether the current month total is >= 100million.
 * Criteria2 in the report tells whether the previous month total is < 50m and current month total >=50m 
 */

package com.mfino.rtscheduler.schedule;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.hibernate.session.HibernateSessionHolder;

public class AMLReport {
	private static Logger log = LoggerFactory.getLogger("AMLReport");
	private static String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", 
			"October", "November", "December"};
	private static BigDecimal hundred_million = new BigDecimal("100000000");
	private static BigDecimal fifty_million = new BigDecimal("50000000");
	private Properties prop = new Properties();
	private String dailyReportsOutputDir = null;
	private String emailRecipients = null;
	private FileOutputStream out = null;
	private Session session = null;
	private String outputFilename = null;
	private File outputDir = null;

	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public void generateAMLReport(){
		log.info("BEGIN:: generateAMLReport");
		try {
			loadProperties();
			String sqlQuery = "select sm.IDNumber, count(distinct(sm.MDN)), sum(sctl.TransactionAmount) " +
					"from subscriber_mdn sm, service_charge_txn_log sctl, transaction_type tt " +
					"where sctl.SourceMDN = sm.MDN and sctl.TransactionTypeID = tt.ID " +
					"and tt.TransactionName in ('Transfer','TransferToUnRegistered') " +
					"and sctl.status in (2,3,4,6,7,8,9,10,11,12,13,14,15) " +
					"and month(sctl.createtime) = :month and year(sctl.createtime) = :year " +
					"group by sm.IDNumber;";
			
			SessionFactory sessionFactory = null;
			
			HibernateSessionHolder hibernateSessionHolder = null;
			ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("..\\spring-datasource-beans.xml");
			sessionFactory = appContext.getBean(SessionFactory.class);
			hibernateSessionHolder = appContext.getBean(HibernateSessionHolder.class);
			session = sessionFactory.openSession();
			hibernateSessionHolder.setSession(session);	
			
			Query curr_mon_query = session.createSQLQuery(sqlQuery);
			Calendar cal = Calendar.getInstance();
			int currMonth = cal.get(Calendar.MONTH)+1;
			int currYear = cal.get(Calendar.YEAR);
			curr_mon_query.setParameter("month", currMonth);
			curr_mon_query.setParameter("year", currYear);
			
			Query prev_mon_query = session.createSQLQuery(sqlQuery);
			int prevMonth = (currMonth == 1) ? 12 : (currMonth - 1);
			int prevYear = (currMonth == 1) ? (currYear -1) : currYear;
			prev_mon_query.setParameter("month", prevMonth);
			prev_mon_query.setParameter("year", prevYear);
			
			int rowNum = 0;
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("AML Report");
//			sheet.setDefaultColumnWidth(6);
			HSSFRow row = sheet.createRow(rowNum++);
			HSSFCell hcell0 = row.createCell(0);
			hcell0.setCellValue("KTP ID");
			HSSFCell hcell1 = row.createCell(1);
			hcell1.setCellValue("MDNs Used");
			HSSFCell hcell2 = row.createCell(2);
			hcell2.setCellValue("Current Month Total");
			HSSFCell hcell3 = row.createCell(3);
			hcell3.setCellValue("Previous Month Total");
			HSSFCell hcell4 = row.createCell(4);
			hcell4.setCellValue("Criteria1");
			HSSFCell hcell5 = row.createCell(5);
			hcell5.setCellValue("Criteria2");
			
			List curr_mon_list = curr_mon_query.list();
			List prev_mon_list = prev_mon_query.list();

			if (CollectionUtils.isNotEmpty(curr_mon_list)) {
				for (int i=0; i<curr_mon_list.size(); i++) {
					String ktpId = null;
					BigDecimal currTotal = BigDecimal.ZERO;
					BigDecimal prevTotal = BigDecimal.ZERO;
					Object[] objArr = (Object[]) curr_mon_list.get(i);
					row = sheet.createRow(rowNum++);
					hcell0 = row.createCell(0);
					ktpId = (objArr[0]!=null) ? (String)objArr[0] : "";
					hcell0.setCellValue(ktpId);
					
					hcell1 = row.createCell(1);
					hcell1.setCellValue(((BigInteger)objArr[1]).intValue());
					
					hcell2 = row.createCell(2);
					currTotal = (objArr[2]!=null ) ? ((BigDecimal)objArr[2]).setScale(0) : BigDecimal.ZERO;
					hcell2.setCellValue( currTotal.toPlainString());
					
					hcell3 = row.createCell(3);
					prevTotal = getPreviousMonthTotal(prev_mon_list, ktpId);
					hcell3.setCellValue(prevTotal.toPlainString());
					
					hcell4 = row.createCell(4);
					boolean criteria1 = (hundred_million.compareTo(currTotal)<0) ? true : false;
					hcell4.setCellValue(criteria1);
					
					hcell5 = row.createCell(5);
					boolean criteria2 = (fifty_million.compareTo(prevTotal)<0 && fifty_million.compareTo(currTotal)>=0) ? true : false;
					hcell5.setCellValue(criteria2);
				}
			}
			else {
				row = sheet.createRow(rowNum++);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue("No Data Found");
			}
			
			outputFilename = "AMLReport_" + monthNames[currMonth-1] + currYear + ".xls";
			outputDir = new File(dailyReportsOutputDir + File.separator + currYear + monthNames[currMonth-1]);
			if (! outputDir.exists()) 
				outputDir.mkdirs();
			out = new FileOutputStream(new File(outputDir.getAbsolutePath() + File.separator + outputFilename ));
			workbook.write(out);
		} catch (BeansException e) {
			log.error("Error: BeansException while creating the AML Report",e);
		} catch (HibernateException e) {
			log.error("Error: HibernateException while creating the AML Report",e);
		} catch (FileNotFoundException e) {
			log.error("Error: FileNotFoundException while creating the AML Report",e);
		} catch (IOException e) {
			log.error("Error: IOException while creating the AML Report",e);
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					log.error("Error: IOException while closing the file object",e);
				}
			if (session != null) 	
				session.close();
		}
		log.info("END:: generateAMLReport");
		sendMail(emailRecipients, outputDir.getAbsolutePath(), outputFilename);
	}
	
	private void sendMail(String emailRecipients, String outputPath, String outputFilename) {
		log.info("sending AML Report Mail function statred");
		try{
			String[] emailRecipientsList = emailRecipients.split(",");
			File attachmentFile = new File(outputPath+File.separator+outputFilename);
			MailUtil mailUtil = new MailUtil();
			for(int i=0; i< emailRecipientsList.length; i++){
				if(mailUtil.isValidEmailAddress(emailRecipientsList[i])){
					mailUtil.sendMail(emailRecipientsList[i], "", outputFilename, "Attached AML Report", attachmentFile);
				}
			}
			log.info("sending AML Report Mail function finished");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private BigDecimal getPreviousMonthTotal(List prev_mon_list, String ktpId) {
		BigDecimal prevTotal = BigDecimal.ZERO;
		if (CollectionUtils.isNotEmpty(prev_mon_list)) {
			for (int j=0; j<prev_mon_list.size(); j++) {
				Object[] pobjArr = (Object[]) prev_mon_list.get(j);
				String pkid = (pobjArr[0]!=null) ? (String)pobjArr[0] : "";
				if (ktpId.equals(pkid)) {
					prevTotal = (pobjArr[2]!=null ) ? ((BigDecimal)pobjArr[2]).setScale(0) : BigDecimal.ZERO;
					return prevTotal;
				}
			}
		}
		return prevTotal;
	}

	private void loadProperties() throws IOException {
		log.info("Loading Properties Started");
		InputStream ins = this.getClass().getResourceAsStream("/rtscheduler.properties");
		prop.load(ins);
		ins.close();
		dailyReportsOutputDir = prop.getProperty("dailyReportsOutputDir");
		emailRecipients = prop.getProperty("email.recipient.list");
		log.info("dailyReportsOutputDir :" + dailyReportsOutputDir);
		log.info("Loading Properties Finished");
	}
}
