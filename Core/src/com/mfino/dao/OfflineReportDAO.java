/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.OfflineReportQuery;
import com.mfino.domain.OfflineReport;

/**
 *
 * @author xchen
 */
public class OfflineReportDAO extends BaseDAO<OfflineReport> {
	 	 
	@SuppressWarnings("unchecked")
	public List<OfflineReport> getOfflineReport(OfflineReportQuery query) {
		  Criteria criteria = createCriteria();
		  if(query.getReprtClass()!=null){
			  criteria.add(Restrictions.eq(OfflineReport.FieldName_ReportClass, query.getReprtClass()).ignoreCase());
		  }
		  List<OfflineReport> results = criteria.list();
	        return results;
			  
	  }

	@SuppressWarnings("unchecked")
	public OfflineReport getByReportName(String reportName) {
		if(StringUtils.isBlank(reportName)){
			return null;
		}
		 Criteria criteria = createCriteria();
		 criteria.add(Restrictions.eq(OfflineReport.FieldName_ReportName, reportName).ignoreCase());
		 List<OfflineReport> results=criteria.list();
		 if(results==null||results.isEmpty()){
			 log.info("No entry found with reportClass :"+reportName);
			 return null;
		 }
		return results.get(0);
	}

	@SuppressWarnings("unchecked")
	public OfflineReport getByReportClass(String className) {
		if(StringUtils.isBlank(className)){
			return null;
		}
		 Criteria criteria = createCriteria();
		 criteria.add(Restrictions.eq(OfflineReport.FieldName_ReportClass, className).ignoreCase());
		 List<OfflineReport> results=criteria.list();
		 if(results==null||results.isEmpty()){
			 log.info("No entry found with reportClass :"+className);
			 return null;
		 }
		return results.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public List<OfflineReport> getOnlineReports() {
		 Criteria criteria = createCriteria();
		 criteria.add(Restrictions.eq(OfflineReport.FieldName_IsOnlineReport, true));
		 List<OfflineReport> results=criteria.list();
		 if(results==null||results.isEmpty()){
			 log.info("No entry found with IsOnlineReport true");
			 return null;
		 }
		return results;
	}

}
