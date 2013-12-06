package com.mfino.report.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.ChargeTypeDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.ServiceTransactionDAO;
import com.mfino.dao.TransactionChargeLogDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ChargeType;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.ServiceTransaction;
import com.mfino.domain.TransactionChargeLog;
import com.mfino.domain.TransactionType;

public class ReportBaseData {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());	
	private  List<CommodityTransfer> ctList = null;
	private  List<PendingCommodityTransfer> pctList = null;
	private  List<ServiceChargeTransactionLog> sctllogs = null;
	private  List<TransactionChargeLog> tclogs = null;
	
	public  List<ServiceTransaction> stList;
	public  List<Service> services;
	public  List<TransactionType> transactionTypes;
	public  List<ChannelCode> ccList;
	public  List<ChargeType> chargeTypes;
	public  String chargeTypesHeader;
		
	private  Map<Long,CommodityTransfer> ctMap= new HashMap<Long,CommodityTransfer>();
	private  Map<Long,PendingCommodityTransfer> pctMap= new HashMap<Long,PendingCommodityTransfer>();
	private  Map<Long,ServiceChargeTransactionLog> sctlMap= new HashMap<Long,ServiceChargeTransactionLog>();
	private  Map<Long,TransactionChargeLog> tclMap= new HashMap<Long,TransactionChargeLog>();
	private  Map<Long,List<Long>> sctltclMap= new HashMap<Long,List<Long>>();
	// *FindbugsChange*
	// Previous -- public static final Map<Long,Service> serviceMap= new HashMap<Long,Service>();
	//			   public static final Map<Long,TransactionType> transactiontypeMap= new HashMap<Long,TransactionType>();
	//			   public static final Map<Long,ServiceTransaction> servicetransactionMap= new HashMap<Long,ServiceTransaction>();
	
	public static final Map<Long,Service> serviceMap= new HashMap<Long,Service>();
	public static final Map<Long,TransactionType> transactiontypeMap= new HashMap<Long,TransactionType>();
	public static final Map<Long,ServiceTransaction> servicetransactionMap= new HashMap<Long,ServiceTransaction>();
	
	
	public void getCommodityTransactions(Date start, Date end){
		CommodityTransferQuery query = new CommodityTransferQuery();
        CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
        query.setStartTimeGE(start);
        query.setStartTimeLT(end);
        query.setIDOrdered(true);
        try {
       	 this.ctList= ctDao.get(query);
       	 for(CommodityTransfer ct:ctList){
       		 this.ctMap.put(ct.getID(), ct);
       	 }
		} catch (Exception e) {
			log.error("failed to get transactions",e);
		}			 
	}
	
	public void getPendingCommodityTransactions(Date start, Date end){
		CommodityTransferQuery query = new CommodityTransferQuery();
       PendingCommodityTransferDAO ptDao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
       query.setStartTimeGE(start);
       query.setStartTimeLT(end);
       query.setBankReversalRequired(true);
       query.setIDOrdered(true);
       try {
       	this.pctList= ptDao.get(query);
       	 for(PendingCommodityTransfer pct:pctList){
       		 pctMap.put(pct.getID(), pct);
       	 }
		} catch (Exception e) {
			log.error("failed to get transactions",e);
		}		 
	}
	
	private void getServices(){
		 ServiceDAO servicedao = DAOFactory.getInstance().getServiceDAO();
		  services =servicedao.getAll();
		 for(Service service:services){
			 serviceMap.put(service.getID(), service);
		 }		 
	}
	
	public void getServiceTransactionLogs(Date start, Date end){
		 ServiceChargeTransactionLogDAO serviceChargeTransactionLogDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		 TransactionChargeLogDAO tclDao = DAOFactory.getInstance().getTransactionChargeLogDAO();
		 ServiceChargeTransactionsLogQuery query = new ServiceChargeTransactionsLogQuery();
		 query.setCreateTimeGE(start);
		 query.setCreateTimeLT(end);
		 query.setIDOrdered(true);
		 sctllogs =serviceChargeTransactionLogDao.get(query);
		 for(ServiceChargeTransactionLog  sctl:sctllogs){
			 sctlMap.put(sctl.getID(), sctl);
		 }
		 if(sctllogs!=null&&!sctllogs.isEmpty()){
			 tclogs = tclDao.getByServiceChargeTransactionLogIDs(sctllogs.get(sctllogs.size()-1).getID(),sctllogs.get(0).getID());
		 }
		 if(tclogs!=null){
		 for(TransactionChargeLog  tcl:tclogs){
			 tclMap.put(tcl.getID(), tcl);
			 List<Long> tclids;
			 if(sctltclMap.containsKey(tcl.getServiceChargeTransactionLogID())){
				 tclids = sctltclMap.get(tcl.getServiceChargeTransactionLogID());
			 }else{
				 tclids = new ArrayList<Long>();
			 }
			 tclids.add(tcl.getID());
			 sctltclMap.put(tcl.getServiceChargeTransactionLogID(), tclids);
		 }
		 }
	}
	
	private void getTransactionsTypes(){
		 TransactionTypeDAO transactiontypeDao = DAOFactory.getInstance().getTransactionTypeDAO();
		 transactionTypes =transactiontypeDao.getAll();
		 for(TransactionType transaction:transactionTypes){
			 transactiontypeMap.put(transaction.getID(), transaction);
		 }
	}
	
	private void getServiceTransactions(){
		 ServiceTransactionDAO transactiontypeDao = DAOFactory.getInstance().getServiceTransactionDAO();
		 stList =transactiontypeDao.getAll();
		 for(ServiceTransaction st:stList){
			 servicetransactionMap.put(st.getID(), st);
		 }		 		 
	}
	
	private void getChannelCodes(){
		 ChannelCodeDAO ccdao =DAOFactory.getInstance().getChannelCodeDao();
		 ccList =ccdao.getAll();		 
	}
	
	
	private void getChargeHeader(){
		ChargeTypeDAO ctd=DAOFactory.getInstance().getChargeTypeDAO();
		chargeTypesHeader="";
		chargeTypes = ctd.getAll();
		for(ChargeType ct:chargeTypes){
			chargeTypesHeader = chargeTypesHeader+","+ct.getName();
		}
		}
	
	public void intializeStaticData(){
		getChannelCodes();
		getServiceTransactions();
		getServices();
		getTransactionsTypes();
		getChargeHeader();
	}
	
	
	public List<CommodityTransfer> getCtList() {
		return ctList;
	}

	public void setCtList(List<CommodityTransfer> ctList) {
		this.ctList = ctList;
	}

	public List<PendingCommodityTransfer> getPctList() {
		return pctList;
	}

	public void setPctList(List<PendingCommodityTransfer> pctList) {
		this.pctList = pctList;
	}

	public Map<Long, CommodityTransfer> getCtMap() {
		return ctMap;
	}

	public void setCtMap(Map<Long, CommodityTransfer> ctMap) {
		this.ctMap = ctMap;
	}

	public Map<Long, PendingCommodityTransfer> getPctMap() {
		return pctMap;
	}

	public void setPctMap(Map<Long, PendingCommodityTransfer> pctMap) {
		this.pctMap = pctMap;
	}
	public List<ServiceChargeTransactionLog> getServiceChargeTransactionLogs() {
		return sctllogs;
	}

	public void setServiceChargeTransactionLog(List<ServiceChargeTransactionLog> sctllogs) {
		this.sctllogs = sctllogs;
	}

	public Map<Long, ServiceChargeTransactionLog> getSctlMap() {
		return sctlMap;
	}

	public void setSctlMap(Map<Long, ServiceChargeTransactionLog> sctlMap) {
		this.sctlMap = sctlMap;
	}

	public List<TransactionChargeLog> getTclogs() {
		return tclogs;
	}

	public void setTclogs(List<TransactionChargeLog> tclogs) {
		this.tclogs = tclogs;
	}

	public Map<Long, TransactionChargeLog> getTclMap() {
		return tclMap;
	}

	public void setTclMap(Map<Long, TransactionChargeLog> tclMap) {
		this.tclMap = tclMap;
	}

	public Map<Long, List<Long>> getSctltclMap() {
		return sctltclMap;
	}

	public void setSctltclMap(Map<Long, List<Long>> sctltclMap) {
		this.sctltclMap = sctltclMap;
	}

	
}
