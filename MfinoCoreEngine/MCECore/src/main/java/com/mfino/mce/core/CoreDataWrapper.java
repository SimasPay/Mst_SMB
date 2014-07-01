package com.mfino.mce.core;

import static com.mfino.mce.core.util.MCEUtil.isNullOrEmpty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.ActivitiesLogDAO;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.CommodityTransferNextIDDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.FundDistributionInfoDAO;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.LedgerDAO;
import com.mfino.dao.MFSLedgerDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.NotificationDAO;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PendingTransactionsEntryDAO;
import com.mfino.dao.PendingTransactionsFileDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SystemParametersDao;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.ActivitiesLogQuery;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.dao.query.NotificationQuery;
import com.mfino.dao.query.PendingTransactionsEntryQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.FundDistributionInfo;
import com.mfino.domain.Ledger;
import com.mfino.domain.MFSLedger;
import com.mfino.domain.Notification;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.PendingTransactionsEntry;
import com.mfino.domain.PendingTransactionsFile;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.SystemParameters;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.util.MCEUtil;

/**
 * @author sasidhar
 *
 */
public class CoreDataWrapper 
{
	private SessionFactory sessionFactory;

	private SubscriberDAO subscriberDAO;
	private SubscriberMDNDAO subscriberMDNDAO;
	private ActivitiesLogDAO activitiesLogDAO;
	private PocketDAO pocketDAO;
	private NotificationDAO notificationDAO;
	private MfinoServiceProviderDAO mfinoServiceProviderDAO;
	private TransactionsLogDAO transactionLogDao;
	private PendingCommodityTransferDAO pendingCommodityTransferDAO;

	private CommodityTransferDAO commodityTransferDao;
	private CompanyDAO companyDao;
	private SystemParametersDao systemParameterDao;
	private LedgerDAO ledgerDao;
	private PendingTransactionsFileDAO pendingTransactionsFileDao;
	private PendingTransactionsEntryDAO pendingTransactionsEntryDAO;
	private UserDAO userDAO;
	private ServiceChargeTransactionLogDAO sctlDao;
	private CommodityTransferNextIDDAO commodityTransferNextIDDAO;
	private UnRegisteredTxnInfoDAO unRegisteredTxnInfoDAO;
	private ChargeTxnCommodityTransferMapDAO chargeTxnCommodityTransferMapDAO;
	private FundDistributionInfoDAO fundDistributionInfoDAO;
	private MFSLedgerDAO mfsLedgerDAO;
	private IntegrationSummaryDao integrationSummaryDao;
	
	
	private String globalAccountNumber = null;

	public PendingCommodityTransferDAO getPendingCommodityTransferDAO() {
		return pendingCommodityTransferDAO;
	}

	public void setPendingCommodityTransferDAO(PendingCommodityTransferDAO pctDao) {
		this.pendingCommodityTransferDAO = pctDao;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private static Log log = LogFactory.getLog(CoreDataWrapper.class);

	public ActivitiesLogDAO getActivitiesLogDAO() {
		return activitiesLogDAO;
	}

	public void setActivitiesLogDAO(ActivitiesLogDAO activitiesLogDao) {
		this.activitiesLogDAO = activitiesLogDao;
	}

	public PocketDAO getPocketDAO() {
		return pocketDAO;
	}

	public void setPocketDAO(PocketDAO pocketDao) {
		this.pocketDAO = pocketDao;
	}

	public NotificationDAO getNotificationDAO() {
		return notificationDAO;
	}

	public void setNotificationDAO(NotificationDAO notificationDao) {
		this.notificationDAO = notificationDao;
	}

	public MfinoServiceProviderDAO getMfinoServiceProviderDAO() {
		return mfinoServiceProviderDAO;
	}

	public void setMfinoServiceProviderDAO(
			MfinoServiceProviderDAO mfinoServiceProviderDAO) 
	{
		this.mfinoServiceProviderDAO = mfinoServiceProviderDAO;
	}

	public TransactionsLog saveTransactionsLog(Integer messageCode, String data) throws RuntimeException{
		try
		{
			mFinoServiceProvider msp = mfinoServiceProviderDAO.getById(1);
			TransactionsLog transactionsLog = new TransactionsLog();
			transactionsLog.setMessageCode(messageCode);
			transactionsLog.setMessageData(data);
			transactionsLog.setmFinoServiceProviderByMSPID(msp);
			transactionsLog.setTransactionTime(new Timestamp(new Date()));
			transactionLogDao.save(transactionsLog);
			return transactionsLog;
		}catch (Exception e) {
			log.error("Error saving transaction log", e);
			throw new RuntimeException(e);
		}
	}

	public TransactionsLog saveTransactionsLog(Integer messageCode, String data, Long parentTxnId) throws RuntimeException{
		try
		{
			mFinoServiceProvider msp = mfinoServiceProviderDAO.getById(1);
			TransactionsLog transactionsLog = new TransactionsLog();
			transactionsLog.setMessageCode(messageCode);
			transactionsLog.setMessageData(data);
			transactionsLog.setmFinoServiceProviderByMSPID(msp);
			transactionsLog.setTransactionTime(new Timestamp(new Date()));
			if (parentTxnId != null) {
				if (transactionLogDao.getById(parentTxnId) != null) {
					log.info("Setting the Parent Transaction Id --> " + parentTxnId);
					transactionsLog.setParentTransactionID(parentTxnId);
				} else {
					log.info("Parent Transaction Id not found --> " + parentTxnId);
				}
			}
			transactionLogDao.save(transactionsLog);
			return transactionsLog;
		}catch (Exception e) {
			log.error("Error saving transaction log", e);
			throw new RuntimeException(e);
		}
	}

	public Subscriber getSubscriberByMdn(String mdn)
	{
		return getSubscriberByMdn(mdn,null);
	}
	
	public Subscriber getSubscriberByMdn(String mdn, LockMode lockmode){
		if(isNullOrEmpty(mdn)) return null;

		Subscriber objSubscriber = null;

/*		SubscriberMdnQuery mdnQuery = new SubscriberMdnQuery(); 
		mdnQuery.setExactMDN(mdn);

		List<SubscriberMDN> subscriberMdnList = subscriberMDNDAO.get(mdnQuery,lockmode);
		if(!isNullOrEmpty(subscriberMdnList)){
			SubscriberMDN objSubscriberMdn = subscriberMdnList.iterator().next();
			return objSubscriberMdn.getSubscriber();
		}
*/
		SubscriberMDN subscriberMdn = subscriberMDNDAO.getByMDN(mdn, lockmode);
		
		if(null != subscriberMdn){
			objSubscriber = subscriberMdn.getSubscriber();
		}
		
		return objSubscriber;
	}
	
	public SubscriberMDN getSubscriberMdn(String mdn)
	{
		return getSubscriberMdn(mdn,null);
	}
	
	public SubscriberMDN getSubscriberMdn(String mdn, LockMode lockMode){
		if(isNullOrEmpty(mdn)) return null;

		SubscriberMDN objSubscriberMdn = null;

		/*SubscriberMdnQuery mdnQuery = new SubscriberMdnQuery(); 
		mdnQuery.setExactMDN(mdn);

		List<SubscriberMDN> subscriberMdnList;
		if(lockMode==null)
			subscriberMdnList= subscriberMDNDAO.get(mdnQuery);
		else
			subscriberMdnList = subscriberMDNDAO.get(mdnQuery, lockMode);
		if(!isNullOrEmpty(subscriberMdnList)){
			return subscriberMdnList.iterator().next();
		}*/
        SubscriberMDN subscriberMdn = subscriberMDNDAO.getByMDN(mdn, lockMode);
		
		if(null != subscriberMdn){
			objSubscriberMdn = subscriberMdn;
		}

		return objSubscriberMdn;
	}

	public Pocket getPocketById(Long pocketId){
		if(pocketId != null)
		{
			return (Pocket)pocketDAO.getById(pocketId);
		}
		return null;
	}

	public Pocket getPocketById(Long pocketId, LockMode lockMode){
		if(pocketId != null)
		{
			return (Pocket)pocketDAO.getById(pocketId, lockMode);
		}
		return null;
	}
	
	public Pocket getDefaultBobPocketByMdnId(Long mdnId){
		Pocket defaultBobPocket = null;

		PocketQuery pocketQuery = new PocketQuery();
		pocketQuery.setMdnIDSearch(mdnId);
		pocketQuery.setPocketType(CmFinoFIX.PocketType_BOBAccount);

		List<Pocket> pocketList = pocketDAO.get(pocketQuery);

		if(!isNullOrEmpty(pocketList)){
			for(Pocket bobPocket : pocketList){
				if(bobPocket.getIsDefault()){
					defaultBobPocket = bobPocket;
					break;
				}
			}
		}

		return defaultBobPocket;
	}
	public List<ChargeTxnCommodityTransferMap> getBySctlID(Long sctlID) {
		ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
		query.setSctlID(sctlID);
		return chargeTxnCommodityTransferMapDAO.get(query);
	}
	
	public ActivitiesLog getActivitiesLogByParentTransactionId(Long parentTransactionId)
	{
		ActivitiesLog activitiesLog = null;
		ActivitiesLogQuery query = new ActivitiesLogQuery();
		query.setParentTransactionId(parentTransactionId);

		List<ActivitiesLog> activitiesLogs = activitiesLogDAO.get(query);

		if(!isNullOrEmpty(activitiesLogs)){
			activitiesLog = activitiesLogs.iterator().next();
		}

		return activitiesLog;
	}

	public Notification getNotification(Integer notificationCode, Integer language, Integer notificationMethod){
		NotificationQuery query = new NotificationQuery();
		query.setNotificationCode(notificationCode);
		if(language == null) {
			SystemParameters langSysparam = systemParameterDao.getSystemParameterByName(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
			language = Integer.parseInt(langSysparam.getParameterValue());
		}
		query.setLanguage(language);
		query.setNotificationMethod(notificationMethod);

		List<Notification> notifications = notificationDAO.getLanguageBasedNotifications(query);

		if(!isNullOrEmpty(notifications)){
			return notifications.get(0);
		}

		return null;
	}

	public mFinoServiceProvider getMSPID(Long id){
		return mfinoServiceProviderDAO.getById(id);		
	}

	public TransactionsLog getTransactionsLogById(Long id){
		log.info("CoreDataWrapper : transactionLogsDao = "+transactionLogDao);
		return transactionLogDao.getById(id);
	}

	public PendingCommodityTransfer getPCTById(Long id){
		return pendingCommodityTransferDAO.getById(id);
	}
	
	public PendingCommodityTransfer getPCTById(long transferID, LockMode upgrade) {
		return pendingCommodityTransferDAO.getById(transferID, upgrade);
	}


	public Pocket getSuspensePocket(){
		SystemParameters systemParameter = systemParameterDao.getSystemParameterByName(MCEUtil.SUSPENSE_POCKET_ID_KEY);

		if(systemParameter != null){
			Long suspensePocketId = Long.valueOf(systemParameter.getParameterValue());
			Pocket suspensePocket = pocketDAO.getById(suspensePocketId);

			if(null == suspensePocket.getCurrentBalance()){
				suspensePocket.setCurrentBalance(BigDecimal.valueOf(0));
			}

			return suspensePocket;
		}

		return null;
	}

	public Pocket getSuspensePocketWithLock(){
		SystemParameters systemParameter = systemParameterDao.getSystemParameterByName(MCEUtil.SUSPENSE_POCKET_ID_KEY);

		if(systemParameter != null){
			Long suspensePocketId = Long.valueOf(systemParameter.getParameterValue());
			Pocket suspensePocket = pocketDAO.getById(suspensePocketId, LockMode.UPGRADE);

			if(null == suspensePocket.getCurrentBalance()){
				suspensePocket.setCurrentBalance(BigDecimal.valueOf(0));
			}

			return suspensePocket;
		}

		return null;
	}
	
	public Pocket getGlobalSVAPocket(){
		SystemParameters systemParameter = systemParameterDao.getSystemParameterByName(MCEUtil.GLOBAL_SVA_POCKET_ID_KEY);

		if(systemParameter != null){
			Long globalSVAPocketId = Long.valueOf(systemParameter.getParameterValue());
			Pocket globalSVAPocket = pocketDAO.getById(globalSVAPocketId);

			if(null == globalSVAPocket.getCurrentBalance()){
				globalSVAPocket.setCurrentBalance(BigDecimal.valueOf(0));
			}

			return globalSVAPocket;
		}

		return null;
	}

	public Pocket getGlobalSVAPocketWithLock(){
		SystemParameters systemParameter = systemParameterDao.getSystemParameterByName(MCEUtil.GLOBAL_SVA_POCKET_ID_KEY);

		if(systemParameter != null){
			Long globalSVAPocketId = Long.valueOf(systemParameter.getParameterValue());
			Pocket globalSVAPocket = pocketDAO.getById(globalSVAPocketId, LockMode.UPGRADE);

			if(null == globalSVAPocket.getCurrentBalance()){
				globalSVAPocket.setCurrentBalance(BigDecimal.valueOf(0));
			}

			return globalSVAPocket;
		}

		return null;
	}
	
	public Pocket getChargesPocket(){
		SystemParameters systemParameter = systemParameterDao.getSystemParameterByName(MCEUtil.CHARGES_POCKET_ID_KEY);

		if(systemParameter != null){
			Long chargesPocketId = Long.valueOf(systemParameter.getParameterValue());
			Pocket chargesPocket =  pocketDAO.getById(chargesPocketId);

			if(null == chargesPocket.getCurrentBalance()){
				chargesPocket.setCurrentBalance(BigDecimal.valueOf(0));
			}

			return chargesPocket;
		}

		return null;
	}

	public Pocket getChargesPocketWithLock(){
		SystemParameters systemParameter = systemParameterDao.getSystemParameterByName(MCEUtil.CHARGES_POCKET_ID_KEY);

		if(systemParameter != null){
			Long chargesPocketId = Long.valueOf(systemParameter.getParameterValue());
			Pocket chargesPocket =  pocketDAO.getById(chargesPocketId, LockMode.UPGRADE);

			if(null == chargesPocket.getCurrentBalance()){
				chargesPocket.setCurrentBalance(BigDecimal.valueOf(0));
			}

			return chargesPocket;
		}

		return null;
	}
	
	public Pocket getPocket(String key, LockMode lockmode) {
		if (StringUtils.isNotBlank(key)) {
			SystemParameters systemParameter = systemParameterDao.getSystemParameterByName(key);

			if(systemParameter != null){
				Long chargesPocketId = Long.valueOf(systemParameter.getParameterValue());
				Pocket chargesPocket;
				
				if( lockmode==null )
				   chargesPocket =  pocketDAO.getById(chargesPocketId);
				else
				   chargesPocket =  pocketDAO.getById(chargesPocketId,lockmode);

				if(null == chargesPocket.getCurrentBalance()){
					chargesPocket.setCurrentBalance(BigDecimal.valueOf(0));
				}
				return chargesPocket;
			}			
		}
		return null;
	}
	
	public Pocket getPocket(String key) {
		return getPocket(key,null);
	}

	public void save(Pocket pocket) 
	{
		pocketDAO.save(pocket);
	}

	public void delete(PendingCommodityTransfer pct) {
		pendingCommodityTransferDAO.delete(pct);
	}

	public void save(PendingCommodityTransfer pct) {
		pendingCommodityTransferDAO.save(pct);
	}

	public void save(CommodityTransfer ct) 
	{
		commodityTransferDao.save(ct);
	}

	public void save(SubscriberMDN objSrcSubMdn) {
		subscriberMDNDAO.save(objSrcSubMdn);
	}
	
	public void save(Subscriber objSrcSubscriber) {
		subscriberDAO.save(objSrcSubscriber);
	}

	public void save(ActivitiesLog activitiesLog) {
		activitiesLogDAO.save(activitiesLog);
	}

	public void save(Ledger ledger) {
		ledgerDao.save(ledger);
	}

	public CommodityTransferDAO getCommodityTransferDao() {
		return commodityTransferDao;
	}

	public void setCommodityTransferDao(CommodityTransferDAO commodityTransferDao) {
		this.commodityTransferDao = commodityTransferDao;
	}

	public SubscriberMDNDAO getSubscriberMDNDAO() {
		return subscriberMDNDAO;
	}

	public void setSubscriberMDNDAO(SubscriberMDNDAO subscriberMDNDAO) {
		this.subscriberMDNDAO = subscriberMDNDAO;
	}

	public SubscriberDAO getSubscriberDAO() {
		return subscriberDAO;
	}

	public void setSubscriberDAO(SubscriberDAO subscriberDAO) {
		this.subscriberDAO = subscriberDAO;
	}

	public CompanyDAO getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDAO companyDao) {
		this.companyDao = companyDao;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDao) {
		this.userDAO = userDao;
	}
	
	public TransactionsLogDAO getTransactionLogDao() {
		return transactionLogDao;
	}

	public void setTransactionLogDao(TransactionsLogDAO transactionLogDao) {
		this.transactionLogDao = transactionLogDao;
	}

	public SystemParametersDao getSystemParameterDao() {
		return systemParameterDao;
	}

	public void setSystemParameterDao(SystemParametersDao systemParameterDao) {
		this.systemParameterDao = systemParameterDao;
	}

	public SystemParameters getSystemParameterByName(String parameterName){
		return systemParameterDao.getSystemParameterByName(parameterName);
	}

	public String getPlatformMdn(){ 
		SystemParameters systemParameter = systemParameterDao.getSystemParameterByName(MCEUtil.PLATFORM_DUMMY_MDN_KEY);

		if(systemParameter != null){
			String mdn = systemParameter.getParameterValue();
			return mdn;
		}

		return null;
	}

	public String getGlobalAccountNumber(){
		//This number does not change and fixed for the system so loading it once should be fine.
		//Also as subscribers increase the pocket table size increases so querying database for every request
		//is unnecessary
		if(globalAccountNumber==null){
			SystemParameters systemParameter = systemParameterDao.getSystemParameterByName(MCEUtil.GLOBAL_ACCOUNT_KEY);
			if(systemParameter != null){
				Long globalPocketId = Long.valueOf(systemParameter.getParameterValue());
				Pocket globalPocket =  pocketDAO.getById(globalPocketId);
				globalAccountNumber = globalPocket.getCardPAN();
			}
		}

		return globalAccountNumber;
	}

	public void setLedgerDao(LedgerDAO ledgerDao) {
		this.ledgerDao = ledgerDao;
	}

	public LedgerDAO getLedgerDao() {
		return ledgerDao;
	}

	public List<PendingCommodityTransfer> getAllPendingTransfers() {
		return pendingCommodityTransferDAO.getAll();
	}

	public List<Ledger> getLedgerEntriesByTransferID(Long commodityTransferId) {
		return ledgerDao.getByCommmodityTransferID(commodityTransferId);
	}
	
	public PendingTransactionsFileDAO getPendingTransactionsFileDao() {
		return pendingTransactionsFileDao;
	}

	public void setPendingTransactionsFileDao(PendingTransactionsFileDAO pendingTransactionsFileDao) {
		this.pendingTransactionsFileDao = pendingTransactionsFileDao;
	}
	
	public void save(PendingTransactionsFile resolvePendingFile) {
		pendingTransactionsFileDao.save(resolvePendingFile);
	}

	public List<PendingTransactionsFile> getPendingUploadedFiles() {
		return pendingTransactionsFileDao.getPendingUploadedFiles();
	}

	public List<PendingTransactionsFile> getProcessingFiles() {
		return pendingTransactionsFileDao.getProcessingFiles();
	}
	
	public PendingTransactionsEntryDAO getPendingTransactionsEntryDAO() {
		return pendingTransactionsEntryDAO;
	}

	public void setPendingTransactionsEntryDAO(
			PendingTransactionsEntryDAO pendingTransactionsEntryDAO) {
		this.pendingTransactionsEntryDAO = pendingTransactionsEntryDAO;
	}

	public int getProcessedLineCount(PendingTransactionsFile resolvePendingFile) {
		return pendingTransactionsEntryDAO.getProcessedLineCount(resolvePendingFile);
	}

	public void save(PendingTransactionsEntry pendingTransactionsEntry) {
		pendingTransactionsEntryDAO.save(pendingTransactionsEntry);
		
	}

	public List<PendingTransactionsEntry> get(
			PendingTransactionsEntryQuery query) {
		return pendingTransactionsEntryDAO.get(query);
	}

	public ServiceChargeTransactionLog getSCTLById(Long sctlId) {
		return sctlDao.getById(sctlId);
	}

	public ServiceChargeTransactionLogDAO getSctlDao() {
		return sctlDao;
	}

	public void setSctlDao(ServiceChargeTransactionLogDAO sctlDao) {
		this.sctlDao = sctlDao;
	} 

	public CommodityTransferNextIDDAO getCommodityTransferNextIDDAO() {
		return commodityTransferNextIDDAO;
	}

	public void setCommodityTransferNextIDDAO(CommodityTransferNextIDDAO commodityTransferNextIDDAO) {
		this.commodityTransferNextIDDAO = commodityTransferNextIDDAO;
	}
	
	public void save(UnRegisteredTxnInfo unRegTxnInfo)
	{
		unRegisteredTxnInfoDAO.save(unRegTxnInfo);
	}

	/**
	 * @param unRegistrationTxnInfoDAO the unRegistrationTxnInfoDAO to set
	 */
	public void setUnRegisteredTxnInfoDAO(UnRegisteredTxnInfoDAO unRegistrationTxnInfoDAO) {
		this.unRegisteredTxnInfoDAO = unRegistrationTxnInfoDAO;
	}

	/**
	 * @return the unRegistrationTxnInfoDAO
	 */
	public UnRegisteredTxnInfoDAO getUnRegisteredTxnInfoDAO() {
		return unRegisteredTxnInfoDAO;
	}
	
	public void save(FundDistributionInfo fundDistributionInfo)
	{
		fundDistributionInfoDAO.save(fundDistributionInfo);
	}

	/**
	 * 
	 * @return fundDistributionInfoDAO the fundDistributionInfoDAO to set
	 */
	public FundDistributionInfoDAO getFundDistributionInfoDAO() {
		return fundDistributionInfoDAO;
	}
	
	/**
	 * 
	 * @param fundDistributionInfoDAO
	 */
	public void setFundDistributionInfoDAO(
			FundDistributionInfoDAO fundDistributionInfoDAO) {
		this.fundDistributionInfoDAO = fundDistributionInfoDAO;
	}

	public ChargeTxnCommodityTransferMapDAO getChargeTxnCommodityTransferMapDAO() {
		return chargeTxnCommodityTransferMapDAO;
	}

	public void setChargeTxnCommodityTransferMapDAO(
			ChargeTxnCommodityTransferMapDAO chargeTxnCommodityTransferMapDAO) {
		this.chargeTxnCommodityTransferMapDAO = chargeTxnCommodityTransferMapDAO;
	}

	public ServiceChargeTransactionLog getSCTLById(long transferID,LockMode upgrade) {
		return this.sctlDao.getById(transferID, upgrade);
	}	
	
	public MFSLedgerDAO getMfsLedgerDAO() {
		return mfsLedgerDAO;
	}

	public void setMfsLedgerDAO(MFSLedgerDAO mfsLedgerDAO) {
		this.mfsLedgerDAO = mfsLedgerDAO;
	}
	
	public void save(MFSLedger mfsLedger) {
		mfsLedgerDAO.save(mfsLedger);
	}
	
	public void save(List<MFSLedger> lstMfsLedgers) {
		if (lstMfsLedgers != null) { 
			mfsLedgerDAO.save(lstMfsLedgers);
		}
	}
	
	public List<MFSLedger> getLedgerEntriesByCommodityTransferId(Long ctId) {
		return mfsLedgerDAO.getLedgerEntriesByCommodityTransferId(ctId);
	}
	
	public Long getSCTLIdByCommodityTransferId(Long ctId) {
		return chargeTxnCommodityTransferMapDAO.getSCTLIdByCommodityTransferId(ctId);
	}

	public IntegrationSummaryDao getIntegrationSummaryDao() {
		return integrationSummaryDao;
	}

	public void setIntegrationSummaryDao(IntegrationSummaryDao integrationSummaryDao) {
		this.integrationSummaryDao = integrationSummaryDao;
	}
}
