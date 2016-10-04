package com.mfino.uicore.fix.processor.impl;

import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.ScheduleTemplateDAO;
import com.mfino.dao.query.ScheduleTemplateQuery;
import com.mfino.domain.ScheduleTemplate;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSScheduleTemplate;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ScheduleTemplateProcessor;
import com.mfino.uicore.util.CronExpressionCreator;
import com.mfino.uicore.util.CronExpressionTranslator;

@Service("ScheduleTemplateProcessorImpl")
public class ScheduleTemplateProcessorImpl extends BaseFixProcessor implements ScheduleTemplateProcessor{
    public CMJSError handleChannelCodes(ConstraintViolationException error) {
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        String message = MessageText._(error.getCause().getMessage());
        errorMsg.setErrorDescription(message);
        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        log.warn(message, error);
        return errorMsg;
    }

    private void updateEntity(ScheduleTemplate scheduleTemplate, CmFinoFIX.CMJSScheduleTemplate.CGEntries e) throws ParseException {

        if (e.getName() != null) {
            scheduleTemplate.setName(e.getName());
        }

        if (e.getModeType() != null && !(("").equals(e.getModeType()))) {
            scheduleTemplate.setModetype(e.getModeType());
        }
        
        if (e.getDayOfWeek() != null && !(("").equals(e.getDayOfWeek()))) {
            scheduleTemplate.setDayofweek(e.getDayOfWeek());
        }
        if (e.getDayOfMonth() != null && !(("").equals(e.getDayOfMonth())) ) {
            scheduleTemplate.setDayofmonth(e.getDayOfMonth());
        }
        if(e.getTimerValueHH()!=null && !(("").equals(e.getTimerValueHH()))){
        	scheduleTemplate.setTimervaluehh(Long.valueOf(e.getTimerValueHH()));
        }
        if(e.getTimerValueMM()!=null && !(("").equals(e.getTimerValueMM()))){
        	scheduleTemplate.setTimervaluemm(Long.valueOf(e.getTimerValueMM()));
        }
      if(e.getMonth()!=null && !(("").equals(e.getMonth()))){
    	  scheduleTemplate.setMonth(Long.valueOf(e.getMonth()));
      }
        
        
        MfinoServiceProviderDAO mfspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
        scheduleTemplate.setmFinoServiceProviderByMSPID(mfspDAO.getById(1L));
        CronExpressionCreator tCron = new CronExpressionCreator();
        if(e.getModeType()!=null){
        tCron.setRecurring(e.getModeType().equals(CmFinoFIX.ModeType_Minutes) || e.getModeType().equals(CmFinoFIX.ModeType_Hourly) || e.getModeType().equals(CmFinoFIX.ModeType_Daily));
        tCron.setMode(e.getModeType().toString());
        }
        if(e.getTimerValueMM()!=null){
    	tCron.setMinutes(e.getTimerValueMM());
        }
        if(e.getTimerValueHH()!=null){
    	tCron.setHours(e.getTimerValueHH());
        }
        if(e.getDayOfMonth()!=null){
        tCron.setDayOfMonth(e.getDayOfMonth());
        }
        if(e.getDayOfWeek()!=null){
    	tCron.setDayOfWeek(e.getDayOfWeek());
        }
        if(e.getMonth()!=null){
    	tCron.setMonth(e.getMonth());
        }
        if(tCron.getDayOfWeek()!=null){
        	if(tCron.getDayOfWeek().contains("MON"))
        	{
    		tCron.setMON(true);
        	}
        	if(tCron.getDayOfWeek().contains("TUE"))
        	{
    		tCron.setTUE(true);
        	}
        	if(tCron.getDayOfWeek().contains("WED"))
        	{
    		tCron.setWED(true);
        	}
        	if(tCron.getDayOfWeek().contains("THU"))
        	{
    		tCron.setTHU(true);
        	}
        	if(tCron.getDayOfWeek().contains("FRI"))
        	{
    		tCron.setFRI(true);
        	}
        	if(tCron.getDayOfWeek().contains("SAT"))
        	{
    		tCron.setSAT(true);
        	}
        	if(tCron.getDayOfWeek().contains("SUN"))
        	{
    		tCron.setSUN(true);
        	}
        }
        String expression = tCron.getCronExpression();
        if(expression!=null){
        scheduleTemplate.setCron(expression);
        }
        CronExpressionTranslator pCron= new CronExpressionTranslator();
        if(pCron.humanReadable(expression)!=null){
        scheduleTemplate.setDescription(pCron.humanReadable(expression));
        }
        
    }

    private void updateMessage(ScheduleTemplate scheduleTemplate, CMJSScheduleTemplate.CGEntries entry) {

        entry.setID(scheduleTemplate.getId().longValue());

        if (scheduleTemplate.getName() != null) {
            entry.setName(scheduleTemplate.getName());
        }
        if (scheduleTemplate.getModetype() != null) {
            entry.setModeType(scheduleTemplate.getModetype());
        }
        /*
        if(scheduleTemplate.getTimeType()!= null){
        	entry.setTimeType(scheduleTemplate.getTimeType());
        	//entry.setTimeTypeText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TimeType, null, scheduleTemplate.getTimeType()));
        }
        */
        
        if(scheduleTemplate.getDayofweek()!=null){
        	entry.setDayOfWeek(scheduleTemplate.getDayofweek());
        }
        if(scheduleTemplate.getDayofmonth()!=null){
        	entry.setDayOfMonth(scheduleTemplate.getDayofmonth());
        }
       
        if(scheduleTemplate.getTimervaluehh()!=null)
        {
        	entry.setTimerValueHH(scheduleTemplate.getTimervaluehh().toString());
        }
        if(scheduleTemplate.getTimervaluemm()!=null){
        	entry.setTimerValueMM(scheduleTemplate.getTimervaluemm().toString());
        }
        
        
        if(scheduleTemplate.getCron()!=null){
        	entry.setCron(scheduleTemplate.getCron());
        }
        if(scheduleTemplate.getMonth()!=null){
        	entry.setCron(scheduleTemplate.getMonth().toString());
        }
        if(scheduleTemplate.getDescription()!=null){
        	entry.setDescription(scheduleTemplate.getDescription());
        }
        
        
        
           }
    
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
    	 CMJSScheduleTemplate realMsg = (CMJSScheduleTemplate) msg;

         ScheduleTemplateDAO dao = DAOFactory.getInstance().getScheduleTemplateDao();
    	if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            ScheduleTemplateQuery query = new ScheduleTemplateQuery();

            
          
            if (StringUtils.isNotBlank(realMsg.getNameSearch())) {
                query.setName(realMsg.getNameSearch());
            }
           if (null!=realMsg.getModeSearch()){
        	   query.setModeType(realMsg.getModeSearch());
          }
           
            
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());

            List<ScheduleTemplate> results = dao.get(query);

            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                ScheduleTemplate scheduleTemplate = results.get(i);

                CMJSScheduleTemplate.CGEntries entry = new CMJSScheduleTemplate.CGEntries();
                updateMessage(scheduleTemplate, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
        	CMJSScheduleTemplate.CGEntries[] entries = realMsg.getEntries();
                for (CMJSScheduleTemplate.CGEntries e : entries) {
                    ScheduleTemplate scheduleTemplate = new ScheduleTemplate();
                    
                    updateEntity(scheduleTemplate, e);
                    try {
                        dao.save(scheduleTemplate);
                    } catch (ConstraintViolationException error) {
                        return handleChannelCodes(error);
                    }
                    updateMessage(scheduleTemplate, e);
                 }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSScheduleTemplate.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSScheduleTemplate.CGEntries e: entries) {
				ScheduleTemplate st = dao.getById(e.getID());
				if(!e.isRemoteModifiedModeType()){
				e.setModeType(dao.getById(e.getID()).getModetype());
				}
				if(!e.isRemoteModifiedTimerValueHH()){
					e.setTimerValueHH(dao.getById(e.getID()).getTimervaluehh().toString());
				}
				if(!e.isRemoteModifiedTimerValueMM()){
					e.setTimerValueMM(dao.getById(e.getID()).getTimervaluemm().toString());
				}
				if(!e.isRemoteModifiedDayOfMonth()){
					e.setDayOfMonth(dao.getById(e.getID()).getDayofmonth());
				}
				if(!e.isRemoteModifiedMonth()){
					e.setMonth(dao.getById(e.getID()).getMonth().toString());
				}
				updateEntity(st, e);
				dao.save(st);
				updateMessage(st, e);
				
				
				log.info("Schedule Template: " + st.getId() + " edit completed by user:" +getLoggedUserNameWithIP());
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		} 
		return msg;
		}
    
 
}
