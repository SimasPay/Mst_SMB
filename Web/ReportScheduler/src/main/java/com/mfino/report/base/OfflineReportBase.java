/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report.base;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.domain.ChargeType;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionChargeLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.HibernateService;
import com.mfino.util.ConfigurationUtil;

/**
 *
 * @author xchen
 */
public abstract class OfflineReportBase {
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected abstract List<File> runAndGetMutlipleReports(Date start, Date end,ReportBaseData data); 

	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected abstract File run(Date start, Date end, ReportBaseData data);
	
	public abstract String getFileName();
	
	public abstract  void setReportName(String reportName);
	
	public boolean hasMultipleReports() {
        return false;
    }

       
    public static String getTimeStamp() {
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
        return sf.format(d);
    }

    protected static String getFormatString(int columns){
        String formatStr = "%s";
        for(int i = 0; i < columns - 1; i++){
            formatStr += ",%s";
        }
        return formatStr;
    }
    
    protected DateFormat getDateFormat(){
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	// Making timezone as a configurable property
        TimeZone zone = ConfigurationUtil.getLocalTimeZone();
        df.setTimeZone(zone);
        return df;
    }

    protected Map<Long, List<ServiceChargeTransactionLog>> getServiceSCTLMap(List<ServiceChargeTransactionLog> sctlogs) {
		Map<Long, List<ServiceChargeTransactionLog>> servicesctlMap = new HashMap<Long, List<ServiceChargeTransactionLog>>();
		for (ServiceChargeTransactionLog st : sctlogs) {
			List<ServiceChargeTransactionLog> sctllist;
			if (servicesctlMap.containsKey(st.getServiceID())) {
				sctllist = servicesctlMap.get(st.getServiceID());
			} else {
				sctllist = new ArrayList<ServiceChargeTransactionLog>();
			}
			sctllist.add(st);
			servicesctlMap.put(st.getServiceID(), sctllist);
		}
		
		return servicesctlMap;
	}

    protected Map<Long, Map<Long, List<ServiceChargeTransactionLog>>> getServiceTransactionSCTLMap(
			Map<Long, List<ServiceChargeTransactionLog>> serviceSCTLMap) {
		Map<Long, Map<Long, List<ServiceChargeTransactionLog>>> sTSCTLMap = new HashMap<Long, Map<Long, List<ServiceChargeTransactionLog>>>();
		Set<Long> serviceIds = serviceSCTLMap.keySet();
		for (Long service : serviceIds) {
			List<ServiceChargeTransactionLog> sctl = serviceSCTLMap.get(service);
			Map<Long, List<ServiceChargeTransactionLog>> transactionsctlMap = new HashMap<Long, List<ServiceChargeTransactionLog>>();
			for (ServiceChargeTransactionLog st : sctl) {
				List<ServiceChargeTransactionLog> sctllist;
				if (transactionsctlMap.containsKey(st.getTransactionTypeID())) {
					sctllist = transactionsctlMap.get(st.getTransactionTypeID());
				} else {
					sctllist = new ArrayList<ServiceChargeTransactionLog>();
				}
				sctllist.add(st);
				transactionsctlMap.put(st.getTransactionTypeID(), sctllist);
			}			
			sTSCTLMap.put(service, transactionsctlMap);
		}
		return sTSCTLMap;
	}

    protected Map<Long, Map<Long, Map<Long, List<ServiceChargeTransactionLog>>>> getServiceTransactionChannelSCTLMap(
			Map<Long, Map<Long, List<ServiceChargeTransactionLog>>> sTSCTLMap) {
		Map<Long, Map<Long, Map<Long, List<ServiceChargeTransactionLog>>>> sTACSCTLMap = new HashMap<Long, Map<Long, Map<Long, List<ServiceChargeTransactionLog>>>>();
		Set<Long> serviceIds = sTSCTLMap.keySet();
		for (Long service : serviceIds) {
			Map<Long, List<ServiceChargeTransactionLog>> tSCTLMap = sTSCTLMap.get(service);
			Set<Long> transactionTypeids = tSCTLMap.keySet();
			Map<Long, Map<Long, List<ServiceChargeTransactionLog>>> sTSCTL1Map = new HashMap<Long, Map<Long, List<ServiceChargeTransactionLog>>>();
			
			for (Long transactionType : transactionTypeids) {
				List<ServiceChargeTransactionLog> sctl = tSCTLMap.get(transactionType);
				Map<Long, List<ServiceChargeTransactionLog>> channelsctlMap = new HashMap<Long, List<ServiceChargeTransactionLog>>();
				
				for (ServiceChargeTransactionLog st : sctl) {
					List<ServiceChargeTransactionLog> sctllist;
					if (channelsctlMap.containsKey(st.getChannelCodeID())) {
						sctllist = channelsctlMap.get(st.getChannelCodeID());
					} else {
						sctllist = new ArrayList<ServiceChargeTransactionLog>();
					}
					sctllist.add(st);
					channelsctlMap.put(st.getChannelCodeID(), sctllist);
				}
				
				sTSCTL1Map.put(transactionType, channelsctlMap);
			}
			sTACSCTLMap.put(service, sTSCTL1Map);
		}
		return sTACSCTLMap;
	}

    protected Map<Long, Map<Long, Map<Long, List<ServiceChargeTransactionLog>>>> getSTCSCTLMap(
			List<ServiceChargeTransactionLog> sctlogs) {
		return getServiceTransactionChannelSCTLMap(getServiceTransactionSCTLMap(getServiceSCTLMap(sctlogs)));
	}

    protected Map<Long, List<ServiceChargeTransactionLog>> getTransactionSCTLMap(List<ServiceChargeTransactionLog> sctlogs) {
		Map<Long, List<ServiceChargeTransactionLog>> transactionSCTLMap = new HashMap<Long, List<ServiceChargeTransactionLog>>();
		for (ServiceChargeTransactionLog st : sctlogs) {
			List<ServiceChargeTransactionLog> sctllist;
			if (transactionSCTLMap.containsKey(st.getTransactionTypeID())) {
				sctllist = transactionSCTLMap.get(st.getTransactionTypeID());
			} else {
				sctllist = new ArrayList<ServiceChargeTransactionLog>();
			}
			sctllist.add(st);
			transactionSCTLMap.put(st.getTransactionTypeID(), sctllist);
		}
		
		return transactionSCTLMap;
	}
	
	
    protected int getDistinctMDNCount(List<ServiceChargeTransactionLog> sctlogs) {
		Set<String> distinctMDNS = new HashSet<String>();
		for (ServiceChargeTransactionLog st : sctlogs) {
			distinctMDNS.add(st.getSourceMDN());
		}
		return distinctMDNS.size();
	}

    protected BigDecimal getTotalCollectedFunds(List<ServiceChargeTransactionLog> sctlogs) {
		BigDecimal totalFunds = BigDecimal.ZERO;
		if(sctlogs!=null){			
		for (ServiceChargeTransactionLog st : sctlogs) {
			if(st.getStatus().equals(CmFinoFIX.SCTLStatus_Confirmed)
					||st.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Started)
					||st.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Completed)){
			totalFunds = totalFunds.add(st.getCalculatedCharge());
			}
		}
		}
		return totalFunds;
	}

    protected HashMap<Long, BigDecimal> getChargeTypeFunds(ReportBaseData data,List<ServiceChargeTransactionLog> sctl) {
		HashMap<Long, BigDecimal> chargeFunds = new HashMap<Long, BigDecimal>();
		
		BigDecimal totalFunds;
		Map<Long,List<Long>> sctltclMap=data.getSctltclMap();
		Map<Long,TransactionChargeLog> tclMap = data.getTclMap();
		if(sctl!=null){
			for (ServiceChargeTransactionLog st : sctl) {
				if(st.getStatus().equals(CmFinoFIX.SCTLStatus_Confirmed)
						||st.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Started)
						||st.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Completed)){					
				List<Long> tclList = sctltclMap.get(st.getID());
				
				if(tclList!=null){
				for(Long tclid:tclList){
				totalFunds = BigDecimal.ZERO;
				TransactionChargeLog tc = tclMap.get(tclid);
				if (chargeFunds.containsKey(tc.getTransactionCharge().getChargeType().getID())) {
					totalFunds = chargeFunds.get(tc.getTransactionCharge().getChargeType().getID()).add(tc.getCalculatedCharge());
				} else {
					totalFunds = tc.getCalculatedCharge();
				}
//				System.out.println(tc.getTransactionCharge().getID()+"  "+totalFunds);
				chargeFunds.put(tc.getTransactionCharge().getChargeType().getID(), totalFunds);
				}
				}
				}
			}
		}
		return chargeFunds;
	}
	
    protected String getChargeTypeFundsString(ReportBaseData data,HashMap<Long, BigDecimal> chageTypeFunds) {
		String funds = "";
		for(ChargeType ct:data.chargeTypes){
			if(chageTypeFunds.containsKey(ct.getID())){
				funds = funds+chageTypeFunds.get(ct.getID())+",";
			}else{
				funds = funds+BigDecimal.ZERO+",";
				}
			}
		return funds.substring(0, funds.length()-1);
	}

    protected Map<Long, List<ServiceChargeTransactionLog>> getTransactionTypeSCTLMap(
			List<ServiceChargeTransactionLog> sctlogs) {
		Map<Long, List<ServiceChargeTransactionLog>> servicesctlMap = new HashMap<Long, List<ServiceChargeTransactionLog>>();
		for (ServiceChargeTransactionLog st : sctlogs) {
			List<ServiceChargeTransactionLog> sctllist;
			if (servicesctlMap.containsKey(st.getTransactionID())) {
				sctllist = servicesctlMap.get(st.getTransactionID());
			} else {
				sctllist = new ArrayList<ServiceChargeTransactionLog>();
			}
			servicesctlMap.put(st.getTransactionID(), sctllist);
		}
		return servicesctlMap;
	}
    
    
	public List<File> executeMutlipleReports(Date start, Date end,ReportBaseData data){
		List<File> reportFiles = new ArrayList<File>();
		HibernateService hbnService = CoreServiceFactory.getInstance().getHibernateService();
		Session session = hbnService.getSessionFactory().openSession();
		HibernateSessionHolder sessionHolder = hbnService.getHibernateSessionHolder();
		sessionHolder.setSession(session);
		DAOFactory.getInstance().setHibernateSessionHolder(sessionHolder);
		try
		{
			reportFiles = runAndGetMutlipleReports(start, end, data);
		} 
		finally
		{
			if(session!=null && session.isOpen())
			{
				session.close();
			}
		}
		return reportFiles;
	}

	public File executeReport(Date start, Date end, ReportBaseData data){
		File report = null;
		HibernateService hbnService = CoreServiceFactory.getInstance().getHibernateService();
		Session session = hbnService.getSessionFactory().openSession();
		HibernateSessionHolder sessionHolder = hbnService.getHibernateSessionHolder();
		sessionHolder.setSession(session);
		DAOFactory.getInstance().setHibernateSessionHolder(sessionHolder);
		try
		{
			report = run(start, end, data);
		} 
		finally
		{
			if(session!=null && session.isOpen())
			{
				session.close();
			}
		}
		
		return report;
	}
    

}
