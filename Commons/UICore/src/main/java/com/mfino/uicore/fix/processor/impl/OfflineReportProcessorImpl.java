/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.OfflineReportDAO;
import com.mfino.domain.OfflineReport;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSOfflineReport;
import com.mfino.fix.CmFinoFIX.CMJSOfflineReport.CGEntries;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.OfflineReportProcessor;

/**
 *
 * @author Maruthi
 */
@Service("OfflineReportProcessorImpl")
public class OfflineReportProcessorImpl extends BaseFixProcessor implements OfflineReportProcessor {
	FormulaEvaluator evaluator;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
    	CMJSOfflineReport realMsg = (CMJSOfflineReport) msg;
    	if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
    		OfflineReportDAO offlineReportDAO = DAOFactory.getInstance().getOfflineReportDAO();
    		List<OfflineReport> reports = offlineReportDAO.getAll();
    		realMsg.allocateEntries(reports.size());
    		for(int i=0;i<reports.size();i++){
    			CMJSOfflineReport.CGEntries entry = new CMJSOfflineReport.CGEntries();
    			OfflineReport report = reports.get(i);
    			updateMessage(report,entry);
    			realMsg.getEntries()[i]=entry;
    		}
    		realMsg.settotal(reports.size());
    	}

		realMsg.setsuccess(CmFinoFIX.Boolean_True);
		
    	return realMsg;
    }
    
	private void updateMessage(OfflineReport report, CGEntries entry) {
		if(report.getID()!=null){
			entry.setID(report.getID());
		}
		if(StringUtils.isNotBlank(report.getName())){
			entry.setName(report.getName());
		}
		if(StringUtils.isNotBlank(report.getReportClass())){
			entry.setReportClass(report.getReportClass());
		}
		if(report.getTriggerEnable()!=null){
			entry.setTriggerEnable(report.getTriggerEnable());
		}
		
	}
}
