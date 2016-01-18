/**
 * 
 */
package com.mfino.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.hibernate.session.HibernateSessionHolder;

/**
 * This class should be used to get an instance of any BaseDAO object.
 * This class can be used to inject session and use the session while creating BaseDAO objects.
 * 
 * 
 * @author Chaitanya
 *
 */
public class DAOFactory {

	private ServiceChargeTransactionLogDAO sctlDAO = null;
	
	private TransactionMonitoringDAO tmDAO = null;

	private TransactionChargeLogDAO tclDAO = null;

	private TransactionAmountDistributionLogDAO tadlDAO = null;

	private MFSBillerPartnerDAO mfsBPDAO = null;

	private PartnerDAO partnerDAO = null;

	private PartnerServicesDAO partnerServicesDAO = null;

	private ServiceAuditDAO auditDAO = null;

	private ServiceDAO serviceDAO = null;

	private SubscriberMDNDAO subMdnDAO = null;

	private TransactionRuleDAO txnRuleDAO = null;

	private TransactionTypeDAO txnTypeDAO = null;

	private MfinoServiceProviderDAO mspDAO = null;

	private SystemParametersDao systemParameterDao = null;

	private PocketDAO pocketDAO = null;

	private PendingCommodityTransferDAO pendingCommodityTransferDAO = null;

	private CommodityTransferDAO commodityTransferDAO = null;

	private SettlementSchedulerLogsDao settlementSchedulerLogsDao = null;

	private ServiceSettlementConfigDAO serviceSettlementConfigDAO = null;

	private SubscriberDAO subscriberDAO = null; 

	private SubscribersAdditionalFieldsDAO subscribersAdditionalFieldsDAO = null;

	private  AuthorizingPersonDAO authorizingPersonDAO = null;

	private PocketTemplateDAO pocketTemplateDao = null;
	
	private PocketTemplateConfigDAO pocketTemplateConfigDao = null;

	private BulkUploadFileDAO bulkUploadFileDAO = null;

	private BulkUploadFileEntryDAO bulkUploadFileEntryDAO = null;
	
	private KYCLevelDAO kycLevelDAO = null; 

	private CompanyDAO companyDAO = null;

	private HibernateSessionHolder hibernateSessionHolder = null;

	private AddressDAO addressDAO;

	private OfflineReportDAO offlineReportDao = null;

	private SettlementTransactionLogsDao settlementTransactionLogDao = null; 

	private TransactionsLogDAO transactionsLogDAO = null;

	private LedgerDAO ledgerDAO = null;

	private TransactionChargeDAO transactionChargeDAO = null;

	private ChargeDefinitionDAO chargeDefinitionDAO =null;

	private ChannelCodeDAO channelCodeDao = null;

	private ChannelSessionManagementDAO channelSessionManagementDAO = null;

	private ActivitiesLogDAO activitiesLogDAO;

	private BankDAO bankDao;

	private BillPaymentTransactionDAO billPaymentTransactionDAO;

	private NotificationDAO notificationDAO;

	private EnumTextDAO enumTextDAO;

	private UserDAO userDAO;

	private MFSBillerDAO mfsbDAO; 

	private DenominationDAO denominationDAO;

	private BillerDAO billerDAO;	

	private SMSTransactionsLogDAO smsLogDao;

	private BrandDAO brandDAO;

	private SMSCDAO smscDAO;
	
	private BillPaymentsDAO billPaymentsDAO;
	
	private ReportParametersDao reportParameterDao = null;
	
	private ChargeTxnCommodityTransferMapDAO txnTransferMap = null;

	private AirtimePurchaseDAO airtimePurchaseDao = null;
	
	private VisafoneTxnGeneratorDao visafoneTxnGeneratorDao = null;
	
	private InterBankTransfersDao interBankTransferDao = null;
	
	private InterbankCodesDao nigeriaBankCodesDao = null;
	
	private UnRegisteredTxnInfoDAO unRegisteredTxnInfoDAO = null;
	
	private ChargeTypeDAO chargeTypeDAO = null;
	
	private GroupDao groupDao = null;
	
	private SubscriberGroupDao subscriberGroupDao = null;
	
	private ServiceTransactionDAO serviceTransactionDAO = null;
	
	private PermissionItemsDAO permissionItemsDAO = null;
	
	private static DAOFactory factory;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private MerchantDAO merchantDAO;

	private DistributionChainLevelDAO distributionChainLevelDAO;

	private DistributionChainTemplateDAO distributionChainTemplateDAO;

	private BankAdminDAO bankAdminDAO;

	private BulkUploadEntryDAO bulkUploadEntryDAO;

	private BulkUploadDAO bulkUploadDAO;

	private ChargePricingDAO chargePricingDAO;

	private KYCFieldsDAO kycFieldsDAO;

	private LOPHistoryDAO lopHistoryDAO;

	private LOPDAO lopDAO;

	private PendingTransactionsFileDAO pendingTransactionsFileDAO;

	private BulkBankAccountDAO bulkBankAccountDAO;

	private PendingTransactionsEntryDAO pendingTransactionsEntryDAO;

	private Person2PersonDAO person2PersonDAO;

	private CardInfoDAO cardInfoDAO;

	private CreditCardDestinationDAO creditCardDestinationDAO;

	private SettlementTemplateDAO settlementTemplateDAO;

	private SharePartnerDAO sharePartnerDAO;

	private RegionDAO regionDAO;

	private CreditCardTransactionDAO creditCardTransactionDAO;

	private SMSPartnerDAO smsPartnerDAO;

	private SMSCodeDAO smsCodeDAO;

	private SAPGroupIDDAO sapGroupIDDAO;

	private ProductIndicatorDAO productIndicatorDAO;

	private MobileNetworkOperatorDAO mobileNetworkOperatorDAO;

	private MerchantCodeDAO merchantCodeDAO;

	private MerchantPrefixCodeDAO merchantPrefixCodeDAO;

	private MDNRangeDAO mdnRangeDAO;

	private BulkLOPDAO bulkLOPDAO;
	
	private RolePermissionDAO rolePermissionDAO;
	
	private DCTRestrictionsDao dctRestrictionsDao;
	
	private PartnerRestrictionsDao partnerRestrictionsDao;
	
	private CreditCardProductDAO creditCardProductDAO;
	
	private IntegrationPartnerMappingDAO integrationPartnerMappingDAO;
	
	private IPMappingDAO ipMappingDao;
	
	private MFSDenominationsDAO mfsDenominationsDAO;
	
	private AutoReversalsDao autoReversalDao;
	
	private ExcludeSubscriberLifeCycleDAO excludeSubscriberLifeCycelDAO;
	
	private RoleDAO roleDAO;

	private PermissionGroupDAO permissionGroupDAO;

	private IntegrationSummaryDao integrationSummaryDao;
	
    private MoneyClearanceGravedDAO moneyClearanceGravedDao;
	
	private ClosedAccountSettlementMDNDAO closedAccountSettlementMdnDao;
	
	private PTC_Group_MapDAO ptc_group_mapDAO;
	
	private FundDefinitionDAO fundDefinitionDAO;
	
	private PurposeDAO purposeDAO;
		
	private ExpirationTypeDAO expirationTypeDAO;
		
	private FundDistributionInfoDAO fundDistributionInfoDAO;
		
	private FundEventsDAO fundEventsDAO;
	
	private NotificationLogDAO notificationLogDAO;
	
	private NotificationLogDetailsDAO notificationLogDetailsDAO;
	
	private ScheduleTemplateDAO scheduleTemplateDao;
	
	private SCTLSettlementMapDAO sctlSettlementMapDao;
	
	private SettlementTransactionSCTLMapDAO settlementTransactionSCTLMapDao;
	
	private AgentCashinTransactionLogDAO agentCashinTransactionLogDAO;
	
	private TransactionPendingSummaryDAO transactionPendingSummaryDao;
	
	private TransactionIdentifierDAO transactionIdentifierDAO;
	
	private ActorChannelMappingDAO actorChannelMappingDao;
	
	private AdjustmentsDAO adjustmentsDao;
	
	private RuleKeyDAO ruleKeyDao;
	
	private TransactionRuleAddnInfoDAO transactionRuleAddnInfoDao;
	
	private ZTEDataPushDAO zteDataPushDAO = null;
	
	private MFATransactionInfoDAO mfaTransactionInfoDAO;
	
	private MFAAuthenticationDAO mfaAuthenticationDAO;
	
	private MFSLedgerDAO mfsLedgerDAO;
	
	private BookingDatedBalanceDAO bookingDatedBalanceDAO;
	
	private SubscriberStatusEventDAO subscriberStatusEventDAO;
	
	private AuditLogDAO auditLogDAO;
	
	private RetiredCardPANInfoDAO retiredCardPANInfoDAO;
	
	private MdnOtpDAO mdnOtpDAO;
	
	private CurrentBalanceInfoDAO currentBalanceInfoDAO;
	private PartnerDefaultServicesDAO partnerDefaultServicesDAO;
	
	private ServiceDefaultConfigurationDAO serviceDefaultConfigurationDAO;
	
	private CashinFirstTimeDAO cftDAO;
	

	private SubscriberFavoriteDAO subscriberFavoriteDAO;
	
	private FavoriteCategoryDAO favoriteCategoryDAO;
	
	private BranchCodeDAO branchCodeDAO;
	
	private KtpDetailsDAO ktpDetailsDAO;
	
	public RetiredCardPANInfoDAO getRetiredCardPANInfoDAO() {
		retiredCardPANInfoDAO = new RetiredCardPANInfoDAO();
		retiredCardPANInfoDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return retiredCardPANInfoDAO;
	}
	
	public MFSLedgerDAO getMFSLedgerDAO() {
		mfsLedgerDAO = new MFSLedgerDAO();
		mfsLedgerDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return mfsLedgerDAO;
	}
	public MFATransactionInfoDAO getMfaTransactionInfoDAO() {
		mfaTransactionInfoDAO = new MFATransactionInfoDAO();
		mfaTransactionInfoDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return mfaTransactionInfoDAO;
	}

	public MFAAuthenticationDAO getMfaAuthenticationDAO() {
		mfaAuthenticationDAO = new MFAAuthenticationDAO();
		mfaAuthenticationDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return mfaAuthenticationDAO;
	}	
	

    public TransactionIdentifierDAO getTransactionIdentifierDAO() {
        transactionIdentifierDAO = new TransactionIdentifierDAO();
        transactionIdentifierDAO.setHibernateSessionHolder(getHibernateSessionHolder());
        return transactionIdentifierDAO;
    }
	
	public AgentCashinTransactionLogDAO getAgentCashinTransactionLogDAO() {
		agentCashinTransactionLogDAO = new AgentCashinTransactionLogDAO();
		agentCashinTransactionLogDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return agentCashinTransactionLogDAO;
	}
	
	public ClosedAccountSettlementMDNDAO getClosedAccountSettlementMdnDao() {
		closedAccountSettlementMdnDao = new ClosedAccountSettlementMDNDAO();
		closedAccountSettlementMdnDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return closedAccountSettlementMdnDao;
	}

	public MoneyClearanceGravedDAO getMoneyClearanceGravedDao() {
		moneyClearanceGravedDao = new MoneyClearanceGravedDAO();
		moneyClearanceGravedDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return moneyClearanceGravedDao;
	}

	public IntegrationPartnerMappingDAO getIntegrationPartnerMappingDAO() {
		integrationPartnerMappingDAO = new IntegrationPartnerMappingDAO();
		integrationPartnerMappingDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return integrationPartnerMappingDAO;
    }
	
	public IPMappingDAO getIPMappingDAO() {
		ipMappingDao = new IPMappingDAO();
		ipMappingDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return ipMappingDao;
    }

	public static DAOFactory getInstance(){
		if(factory==null){
			factory = new DAOFactory();
		}
		return factory;
	}

	public MfinoServiceProviderDAO getMfinoServiceProviderDAO(){
		mspDAO = new MfinoServiceProviderDAO();
		mspDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return mspDAO;
	}

	public ServiceChargeTransactionLogDAO getServiceChargeTransactionLogDAO(){
		sctlDAO = new ServiceChargeTransactionLogDAO();
		sctlDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return sctlDAO;
	}
	
	public TransactionMonitoringDAO getTransactionMonitoringDAO(){
		tmDAO = new TransactionMonitoringDAO();
		tmDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return tmDAO;
	}
	
	public HibernateSessionHolder getHibernateSessionHolder() {
		return hibernateSessionHolder;
	}

	public void setHibernateSessionHolder(
			HibernateSessionHolder hibernateSessionHolder) {
		this.hibernateSessionHolder = hibernateSessionHolder;
	}

	public TransactionAmountDistributionLogDAO getTransactionAmountDistributionLogDAO(){
		tadlDAO = new TransactionAmountDistributionLogDAO();
		tadlDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return tadlDAO;
	}

	public TransactionChargeLogDAO getTransactionChargeLogDAO(){
		tclDAO = new TransactionChargeLogDAO();
		tclDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return tclDAO;
	}

	public MFSBillerPartnerDAO getMFSBillerPartnerDAO(){
		mfsBPDAO = new MFSBillerPartnerDAO();
		mfsBPDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return mfsBPDAO;
	}

	public PartnerDAO getPartnerDAO(){
		partnerDAO = new PartnerDAO();
		partnerDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return partnerDAO;
	}

	public PartnerServicesDAO getPartnerServicesDAO(){
		partnerServicesDAO = new PartnerServicesDAO();
		partnerServicesDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return partnerServicesDAO;
	}

	public ServiceAuditDAO getServiceAuditDAO() {
		auditDAO = new ServiceAuditDAO();
		auditDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return auditDAO;
	}


	public ServiceDAO getServiceDAO() {
		serviceDAO = new ServiceDAO(); 
		serviceDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return serviceDAO;
	}


	public SubscriberMDNDAO getSubscriberMdnDAO() {
		subMdnDAO = new SubscriberMDNDAO();
		subMdnDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return subMdnDAO;
	}


	public TransactionRuleDAO getTransactionRuleDAO() {
		txnRuleDAO = new TransactionRuleDAO();
		txnRuleDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return txnRuleDAO;
	}


	public TransactionTypeDAO getTransactionTypeDAO() {
		txnTypeDAO = new TransactionTypeDAO();
		txnTypeDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return txnTypeDAO;
	}

	public SystemParametersDao getSystemParameterDao() {
		systemParameterDao = new SystemParametersDao();
		systemParameterDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return systemParameterDao;
	}

	/**
	 * @return the pocketDAO
	 */
	 public PocketDAO getPocketDAO() {
		 pocketDAO = new PocketDAO();
		 pocketDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return pocketDAO;
	 }

	 /**
	  * @return the pendingCommodityTransferDAO
	  */
	 public PendingCommodityTransferDAO getPendingCommodityTransferDAO() {
		 pendingCommodityTransferDAO = new PendingCommodityTransferDAO();
		 pendingCommodityTransferDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return pendingCommodityTransferDAO;
	 }

	 /**
	  * @return the commodityTransferDAO
	  */
	 public CommodityTransferDAO getCommodityTransferDAO() {
		 commodityTransferDAO = new CommodityTransferDAO();
		 commodityTransferDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return commodityTransferDAO;
	 }

	 /**
	  * @return the settlementSchedulerDao
	  */
	 public SettlementSchedulerLogsDao getSettlementSchedulerLogsDao() {
		 settlementSchedulerLogsDao = new SettlementSchedulerLogsDao();
		 settlementSchedulerLogsDao.setHibernateSessionHolder(getHibernateSessionHolder());
		 return settlementSchedulerLogsDao;
	 }

	 /**
	  * @return the serviceSettlementConfigDAO
	  */
	 public ServiceSettlementConfigDAO getServiceSettlementConfigDAO() {
		 serviceSettlementConfigDAO = new ServiceSettlementConfigDAO();
		 serviceSettlementConfigDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return serviceSettlementConfigDAO;
	 }

	 /**
	  * @return the subscriberDAO
	  */
	 public SubscriberDAO getSubscriberDAO() {
		 subscriberDAO = new SubscriberDAO();
		 subscriberDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return subscriberDAO;
	 }

	 /**
	  * @return the subscribersAdditionalFieldsDAO
	  */
	 public SubscribersAdditionalFieldsDAO getSubscribersAdditionalFieldsDAO() {
		 subscribersAdditionalFieldsDAO = new SubscribersAdditionalFieldsDAO();
		 subscribersAdditionalFieldsDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return subscribersAdditionalFieldsDAO;
	 }

	 /**
	  * @return the authorizingPersonDAO
	  */
	 public AuthorizingPersonDAO getAuthorizingPersonDAO() {
		 authorizingPersonDAO = new AuthorizingPersonDAO();
		 authorizingPersonDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return authorizingPersonDAO;
	 }

	 /**
	  * @return the pocketTemplateDao
	  */
	 public PocketTemplateDAO getPocketTemplateDao() {
		 pocketTemplateDao = new PocketTemplateDAO();
		 pocketTemplateDao.setHibernateSessionHolder(getHibernateSessionHolder());
		 return pocketTemplateDao;
	 }
	 
	 /**
	  * @return the pocketTemplateConfigDao
	  */
	 public PocketTemplateConfigDAO getPocketTemplateConfigDao() {
		 pocketTemplateConfigDao = new PocketTemplateConfigDAO();
		 pocketTemplateConfigDao.setHibernateSessionHolder(getHibernateSessionHolder());
		 return pocketTemplateConfigDao;
	 }

	 public BulkUploadFileDAO getBulkUploadFileDAO(){
		 bulkUploadFileDAO = new BulkUploadFileDAO();
		 bulkUploadFileDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return bulkUploadFileDAO;
	 }

	 public BulkUploadFileEntryDAO getBulkUploadFileEntryDAO(){
		 bulkUploadFileEntryDAO = new BulkUploadFileEntryDAO();
		 bulkUploadFileEntryDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return bulkUploadFileEntryDAO;
	 }
	 
	 public KYCLevelDAO getKycLevelDAO(){
		 kycLevelDAO = new KYCLevelDAO();
		 kycLevelDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return kycLevelDAO;
	 }

	 public CompanyDAO getCompanyDAO(){
		 companyDAO = new CompanyDAO();
		 companyDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return companyDAO;
	 }

	 public AddressDAO getAddressDAO(){
		 addressDAO = new AddressDAO();
		 addressDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return addressDAO;
	 }

	 public OfflineReportDAO getOfflineReportDAO(){
		 offlineReportDao = new OfflineReportDAO();
		 offlineReportDao.setHibernateSessionHolder(getHibernateSessionHolder());
		 return offlineReportDao;
	 }

	 /**
	  * @return the settlementTransactionLogDao
	  */
	 public SettlementTransactionLogsDao getSettlementTransactionLogDao() {
		 settlementTransactionLogDao = new SettlementTransactionLogsDao();
		 settlementTransactionLogDao.setHibernateSessionHolder(getHibernateSessionHolder());
		 return settlementTransactionLogDao;
	 }

	 public TransactionsLogDAO getTransactionsLogDAO(){
		 transactionsLogDAO = new TransactionsLogDAO();
		 transactionsLogDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return transactionsLogDAO;
	 }

	 public LedgerDAO getLedgerDAO() {
		 ledgerDAO = new LedgerDAO();
		 ledgerDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return ledgerDAO;
	 }

	 public TransactionChargeDAO getTransactionChargeDAO(){
		 transactionChargeDAO = new TransactionChargeDAO();
		 transactionChargeDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return transactionChargeDAO;
	 }

	 public ChannelCodeDAO getChannelCodeDao() {
		 channelCodeDao = new ChannelCodeDAO();
		 channelCodeDao.setHibernateSessionHolder(getHibernateSessionHolder());
		 return channelCodeDao;
	 }

	 public ChargeDefinitionDAO getChargeDefinitionDAO() {
		 chargeDefinitionDAO = new ChargeDefinitionDAO();
		 chargeDefinitionDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return chargeDefinitionDAO;
	 }


	 /**
	  * @return the channelSessionManagementDAO
	  */
	 public ChannelSessionManagementDAO getChannelSessionManagementDAO() {
		 channelSessionManagementDAO = new ChannelSessionManagementDAO();
		 channelSessionManagementDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return channelSessionManagementDAO;
	 }

	 public ActivitiesLogDAO getActivitiesLogDAO() {
		 activitiesLogDAO = new ActivitiesLogDAO();
		 activitiesLogDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return activitiesLogDAO;
	 }

	 public BankDAO getBankDao(){
		 bankDao = new BankDAO();
		 bankDao.setHibernateSessionHolder(getHibernateSessionHolder());
		 return bankDao;
	 }

	 /**
	  * @return the billPaymentTransactionDAO
	  */
	 public BillPaymentTransactionDAO getBillPaymentTransactionDAO() {
		 billPaymentTransactionDAO = new BillPaymentTransactionDAO();
		 billPaymentTransactionDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return billPaymentTransactionDAO;
	 }

	 public NotificationDAO getNotificationDAO(){
		 notificationDAO = new NotificationDAO();
		 notificationDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return notificationDAO;
	 }

	 public EnumTextDAO getEnumTextDAO(){
		 enumTextDAO = new EnumTextDAO();
		 enumTextDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return enumTextDAO;
	 }

	 /**
	  * @return the userDAO
	  */
	 public UserDAO getUserDAO() {
		 userDAO = new UserDAO();
		 userDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return userDAO;
	 }

	 public MFSBillerDAO getMFSBillerDAO(){
		 mfsbDAO = new MFSBillerDAO();
		 mfsbDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return mfsbDAO; 
	 }

	 /**
	  * @return the denominationDAO
	  */
	 public DenominationDAO getDenominationDAO() {
		 denominationDAO = new DenominationDAO();
		 denominationDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return denominationDAO;
	 }

	 /**
	  * @return the billerDAO
	  */
	 public BillerDAO getBillerDAO() {
		 billerDAO = new BillerDAO();
		 billerDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return billerDAO;
	 }

	 public SMSTransactionsLogDAO getSMSTransactionsLogDAO(){
		 smsLogDao = new SMSTransactionsLogDAO();
		 smsLogDao.setHibernateSessionHolder(getHibernateSessionHolder());
		 return smsLogDao;
	 }

	 /**
	  * @return the brandDAO
	  */
	 public BrandDAO getBrandDAO() {
		 brandDAO = new BrandDAO();
		 brandDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return brandDAO;
	 }

	 /**
	  * @return the smscDAO
	  */
	 public SMSCDAO getSmscDAO() {
		 smscDAO = new SMSCDAO();
		 smscDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return smscDAO;
	 }
	 
	 public BillPaymentsDAO getBillPaymentDAO(){
		 billPaymentsDAO = new BillPaymentsDAO();
		 billPaymentsDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		 return billPaymentsDAO;
	 }

	public ReportParametersDao getReportParametersDao() {
		reportParameterDao = new ReportParametersDao();
		reportParameterDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return reportParameterDao;
	}

	/**
	 * @return the txnTransferMap
	 */
	public ChargeTxnCommodityTransferMapDAO getTxnTransferMap() {
		txnTransferMap = new ChargeTxnCommodityTransferMapDAO();
		txnTransferMap.setHibernateSessionHolder(getHibernateSessionHolder());
		return txnTransferMap;
	}
	
	 public AirtimePurchaseDAO getAirtimePurchaseDao(){
		 airtimePurchaseDao = new AirtimePurchaseDAO();
		 airtimePurchaseDao.setHibernateSessionHolder(getHibernateSessionHolder());
		 return airtimePurchaseDao;
	 }
	 
	 public VisafoneTxnGeneratorDao getVisafoneTxnGeneratorDao(){
		 visafoneTxnGeneratorDao = new VisafoneTxnGeneratorDao();
		 visafoneTxnGeneratorDao.setHibernateSessionHolder(getHibernateSessionHolder());
		 return visafoneTxnGeneratorDao;
	 }

	public InterBankTransfersDao getInterBankTransferDao() {
		interBankTransferDao = new InterBankTransfersDao();
		interBankTransferDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return interBankTransferDao;
	}

	public InterbankCodesDao getInterbankCodesDao() {
		nigeriaBankCodesDao = new InterbankCodesDao();
		nigeriaBankCodesDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return nigeriaBankCodesDao;
	}
	
	public UnRegisteredTxnInfoDAO getUnRegisteredTxnInfoDAO(){
		unRegisteredTxnInfoDAO = new UnRegisteredTxnInfoDAO();
		unRegisteredTxnInfoDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return unRegisteredTxnInfoDAO;
	}
	
	/**
	 * @return the chargeTypeDAO
	 */
	public ChargeTypeDAO getChargeTypeDAO() {
		chargeTypeDAO = new ChargeTypeDAO();
		chargeTypeDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return chargeTypeDAO;
	}

	/**
	 * @return the serviceTransactionDAO
	 */
	public ServiceTransactionDAO getServiceTransactionDAO() {
		serviceTransactionDAO = new ServiceTransactionDAO();
		serviceTransactionDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return serviceTransactionDAO;
	}

	/**
	 * @return the permissionItemsDAO
	 */
	public PermissionItemsDAO getPermissionItemsDAO() {
		permissionItemsDAO = new PermissionItemsDAO();
		permissionItemsDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return permissionItemsDAO;
	}

	public MerchantDAO getMerchantDAO(){
		merchantDAO = new MerchantDAO();
		merchantDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return merchantDAO;
	}
	
	public DistributionChainLevelDAO getDistributionChainLevelDAO(){
		distributionChainLevelDAO = new DistributionChainLevelDAO();
		distributionChainLevelDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return distributionChainLevelDAO;
	}
	
	public DistributionChainTemplateDAO getDistributionChainTemplateDAO(){
		distributionChainTemplateDAO = new DistributionChainTemplateDAO();
		distributionChainTemplateDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return distributionChainTemplateDAO;
	}
	
	public BankAdminDAO getBankAdminDAO(){
		bankAdminDAO = new BankAdminDAO();
		bankAdminDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return bankAdminDAO;
	}
	
	public BulkUploadEntryDAO getBulkUploadEntryDAO(){
		bulkUploadEntryDAO = new BulkUploadEntryDAO();
		bulkUploadEntryDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return bulkUploadEntryDAO;
	}
	
	public BulkUploadDAO getBulkUploadDAO(){
		bulkUploadDAO = new BulkUploadDAO();
		bulkUploadDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return bulkUploadDAO;
	}
	
	public ChargePricingDAO getChargePricingDAO(){
		chargePricingDAO = new ChargePricingDAO();
		chargePricingDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return chargePricingDAO;
	}
	
	public MFSDenominationsDAO getMfsDenominationsDAO() {
		mfsDenominationsDAO = new MFSDenominationsDAO();
		mfsDenominationsDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return mfsDenominationsDAO;
	}
	public KYCFieldsDAO getKYCFieldsDAO(){
		kycFieldsDAO = new KYCFieldsDAO();
		kycFieldsDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return kycFieldsDAO;
	}
	
	public LOPHistoryDAO getLOPHistoryDAO(){
		lopHistoryDAO = new LOPHistoryDAO();
		lopHistoryDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return lopHistoryDAO;
	}
	
	public LOPDAO getLopDAO(){
		lopDAO = new LOPDAO();
		lopDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return lopDAO;
	}
	
	public PendingTransactionsFileDAO getPendingTransactionsFileDAO(){
		pendingTransactionsFileDAO = new PendingTransactionsFileDAO();
		pendingTransactionsFileDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return pendingTransactionsFileDAO;
	}
	
	public BulkBankAccountDAO getBulkBankAccountDAO(){
		bulkBankAccountDAO = new BulkBankAccountDAO();
		bulkBankAccountDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return bulkBankAccountDAO;
	}
	
	public PendingTransactionsEntryDAO getPendingTransactionsEntryDAO(){
		pendingTransactionsEntryDAO = new PendingTransactionsEntryDAO();
		pendingTransactionsEntryDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return pendingTransactionsEntryDAO;
	}
	
	public Person2PersonDAO getPerson2PersonDAO(){
		person2PersonDAO = new Person2PersonDAO();
		person2PersonDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return person2PersonDAO;
	}
	
	public CardInfoDAO getCardInfoDAO(){
		cardInfoDAO = new CardInfoDAO();
		cardInfoDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return cardInfoDAO;
	}
	
	public CreditCardDestinationDAO getCreditCardDestinationDAO(){
		creditCardDestinationDAO = new CreditCardDestinationDAO();
		creditCardDestinationDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return creditCardDestinationDAO;
	}
	
	public SettlementTemplateDAO getSettlementTemplateDAO(){
		settlementTemplateDAO = new SettlementTemplateDAO();
		settlementTemplateDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return settlementTemplateDAO;
	}
	
	public SharePartnerDAO getSharePartnerDAO(){
		sharePartnerDAO = new SharePartnerDAO();
		sharePartnerDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return sharePartnerDAO;
	}
	
	public RegionDAO getRegionDAO(){
		regionDAO = new RegionDAO();
		regionDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return regionDAO;
	}
	
	public CreditCardTransactionDAO getCreditCardTransactionDAO(){
		creditCardTransactionDAO = new CreditCardTransactionDAO();
		creditCardTransactionDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return creditCardTransactionDAO;
	}
	
	public SMSPartnerDAO getSMSPartnerDAO(){
		smsPartnerDAO = new SMSPartnerDAO();
		smsPartnerDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return smsPartnerDAO;
	}
	
	public SMSCodeDAO getSMSCodeDAO(){
		smsCodeDAO = new SMSCodeDAO();
		smsCodeDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return smsCodeDAO;
	}
	
	public SAPGroupIDDAO getSAPGroupIDDAO(){
		sapGroupIDDAO = new SAPGroupIDDAO();
		sapGroupIDDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return sapGroupIDDAO;
	}
	
	public ProductIndicatorDAO getProductIndicatorDAO(){
		productIndicatorDAO = new ProductIndicatorDAO();
		productIndicatorDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return productIndicatorDAO;
	}
	
	public MobileNetworkOperatorDAO getMobileNetworkOperatorDAO(){
		mobileNetworkOperatorDAO = new MobileNetworkOperatorDAO();
		mobileNetworkOperatorDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return mobileNetworkOperatorDAO;
	}
	
	public MerchantCodeDAO getMerchantCodeDAO(){
		merchantCodeDAO = new MerchantCodeDAO();
		merchantCodeDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return merchantCodeDAO;
	}
	
	public MerchantPrefixCodeDAO getMerchantPrefixCodeDAO(){
		merchantPrefixCodeDAO = new MerchantPrefixCodeDAO();
		merchantPrefixCodeDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return merchantPrefixCodeDAO;
	}
	
	public MDNRangeDAO getMDNRangeDAO(){
		mdnRangeDAO = new MDNRangeDAO();
		mdnRangeDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return mdnRangeDAO;
	}
	
	public BulkLOPDAO getBulkLOPDAO(){
		bulkLOPDAO = new BulkLOPDAO();
		bulkLOPDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return bulkLOPDAO;
	}

	public RolePermissionDAO getRolePermissionDAO() {
		rolePermissionDAO = new RolePermissionDAO();
		rolePermissionDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return rolePermissionDAO;
	}

	public GroupDao getGroupDao() {
		groupDao = new GroupDao();
		groupDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return groupDao;
	}
	
	public SubscriberGroupDao getSubscriberGroupDao() {
		subscriberGroupDao = new SubscriberGroupDao();
		subscriberGroupDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return subscriberGroupDao;
	}

	public CreditCardProductDAO getCreditCardProductDAO(){
		creditCardProductDAO = new CreditCardProductDAO();
		creditCardProductDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return creditCardProductDAO;
	}
	public DCTRestrictionsDao getDctRestrictionsDao(){
		dctRestrictionsDao = new DCTRestrictionsDao();
		dctRestrictionsDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return dctRestrictionsDao;
	}

	public PartnerRestrictionsDao getPartnerRestrictionsDao() {
		partnerRestrictionsDao = new PartnerRestrictionsDao();
		partnerRestrictionsDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return partnerRestrictionsDao;
	}
	
	public AutoReversalsDao getAutoReversalsDao(){
		autoReversalDao = new AutoReversalsDao();
		autoReversalDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return autoReversalDao;
	}
	
	public ExcludeSubscriberLifeCycleDAO getExcludeSubscriberLifeCycleDao(){
		excludeSubscriberLifeCycelDAO = new ExcludeSubscriberLifeCycleDAO();
		excludeSubscriberLifeCycelDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return excludeSubscriberLifeCycelDAO;
	}
	
	public RoleDAO getRoleDAO(){
		roleDAO = new RoleDAO();
		roleDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return roleDAO;
	}
	public PermissionGroupDAO getPermissionGroupDAO(){
		permissionGroupDAO = new PermissionGroupDAO();
		permissionGroupDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return permissionGroupDAO;
	}

	public IntegrationSummaryDao getIntegrationSummaryDao(){
		integrationSummaryDao = new IntegrationSummaryDao();
		integrationSummaryDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return integrationSummaryDao;
	}
	
	public PTC_Group_MapDAO getPTC_Group_MapDAO(){
		ptc_group_mapDAO = new PTC_Group_MapDAO();
		ptc_group_mapDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return ptc_group_mapDAO;
	}
	
	public NotificationLogDAO getNotificationLogDao(){
		notificationLogDAO = new NotificationLogDAO();
		notificationLogDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return notificationLogDAO;
	}
	
	public NotificationLogDetailsDAO getNotificationLogDetailsDao(){
		notificationLogDetailsDAO = new NotificationLogDetailsDAO();
		notificationLogDetailsDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return notificationLogDetailsDAO;
	}
	
	public TransactionPendingSummaryDAO getTransactionPendingSummaryDAO(){
		transactionPendingSummaryDao = new TransactionPendingSummaryDAO();
		transactionPendingSummaryDao.setHibernateSessionHolder(getHibernateSessionHolder());
		return transactionPendingSummaryDao;
	}
	
	public FundEventsDAO getFundEventsDAO(){
		fundEventsDAO = new FundEventsDAO();
		fundEventsDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return fundEventsDAO;
	}

	public FundDistributionInfoDAO getFundDistributionInfoDAO() {
		fundDistributionInfoDAO = new FundDistributionInfoDAO();
		fundDistributionInfoDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return fundDistributionInfoDAO;
	}

	public FundDefinitionDAO getFundDefinitionDAO() {
		fundDefinitionDAO = new FundDefinitionDAO();
		fundDefinitionDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return fundDefinitionDAO;
	}

	public PurposeDAO getPurposeDAO() {
		purposeDAO = new PurposeDAO();
		purposeDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return purposeDAO;
	}

	public ExpirationTypeDAO getExpirationTypeDAO() {
		expirationTypeDAO = new ExpirationTypeDAO();
		expirationTypeDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return expirationTypeDAO;
	}

	public ScheduleTemplateDAO getScheduleTemplateDao() {
		scheduleTemplateDao = new ScheduleTemplateDAO();
		scheduleTemplateDao.setHibernateSessionHolder(hibernateSessionHolder);
		return scheduleTemplateDao;
	}
	
	public SCTLSettlementMapDAO getSCTLSettlementMapDao() {
		sctlSettlementMapDao = new SCTLSettlementMapDAO();
		sctlSettlementMapDao.setHibernateSessionHolder(hibernateSessionHolder);
		return sctlSettlementMapDao;
	}
	
	public SettlementTransactionSCTLMapDAO getSettlementTransactionSCTLMapDao() {
		settlementTransactionSCTLMapDao = new SettlementTransactionSCTLMapDAO();
		settlementTransactionSCTLMapDao.setHibernateSessionHolder(hibernateSessionHolder);
		return settlementTransactionSCTLMapDao;
	}
	public CurrentBalanceInfoDAO getCurrentBalanceInfoDAO() {
		currentBalanceInfoDAO = new CurrentBalanceInfoDAO();
		currentBalanceInfoDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return currentBalanceInfoDAO;
	}
	
	public ActorChannelMappingDAO getActorChannelMappingDao() {
		actorChannelMappingDao = new ActorChannelMappingDAO();
		actorChannelMappingDao.setHibernateSessionHolder(hibernateSessionHolder);
		return actorChannelMappingDao;
	}
	
	public AdjustmentsDAO getAdjustmentsDao() {
		adjustmentsDao = new AdjustmentsDAO();
		adjustmentsDao.setHibernateSessionHolder(hibernateSessionHolder);
		return adjustmentsDao;
	}
	
	public RuleKeyDAO getRuleKeyDao() {
		ruleKeyDao = new RuleKeyDAO();
		ruleKeyDao.setHibernateSessionHolder(hibernateSessionHolder);
		return ruleKeyDao;
	}
	
	public TransactionRuleAddnInfoDAO getTransactionRuleAddnInfoDao() {
		transactionRuleAddnInfoDao = new TransactionRuleAddnInfoDAO();
		transactionRuleAddnInfoDao.setHibernateSessionHolder(hibernateSessionHolder);
		return transactionRuleAddnInfoDao;
	}
	
	public ZTEDataPushDAO getZTEDataPushDAO(){
		zteDataPushDAO  = new ZTEDataPushDAO();
		zteDataPushDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return zteDataPushDAO;
	}
	
	/**
	public TransactionFlowDAO getTransactionFlowDAO() {
		transactionFlowDAO  = new TransactionFlowDAO();
		transactionFlowDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return transactionFlowDAO;
	}

	public FlowStepDAO getFlowStepDAO() {
		flowStepDAO  = new FlowStepDAO();
		flowStepDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return flowStepDAO;
	}

	public RequestTypeDAO getRequestTypeDAO() {
		requestTypeDAO  = new RequestTypeDAO();
		requestTypeDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return requestTypeDAO;
	}

	public InteractionSummaryDAO getInteractionSummaryDAO() {
		interactionSummaryDAO = new InteractionSummaryDAO();
		interactionSummaryDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return interactionSummaryDAO;
	}

	public InteractionAdditionalDataDAO getInteractionAdditionalDataDAO() {
		interactionAdditionalDataDAO = new InteractionAdditionalDataDAO();
		interactionAdditionalDataDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return interactionAdditionalDataDAO;
	}

	private TransactionFlowDAO transactionFlowDAO;
	
	private FlowStepDAO flowStepDAO;
	
	private RequestTypeDAO requestTypeDAO;
	
	private InteractionSummaryDAO interactionSummaryDAO;
	
	private InteractionAdditionalDataDAO interactionAdditionalDataDAO;
**/
	public BookingDatedBalanceDAO getBookingDatedBalanceDao() {
		bookingDatedBalanceDAO = new BookingDatedBalanceDAO();
		bookingDatedBalanceDAO.setHibernateSessionHolder(hibernateSessionHolder);
		return bookingDatedBalanceDAO;
	}
	public SubscriberStatusEventDAO getSubscriberStatusEventDAO() {
		subscriberStatusEventDAO = new SubscriberStatusEventDAO();
		subscriberStatusEventDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return subscriberStatusEventDAO;
	}
	
	public MdnOtpDAO getMdnOtpDAO(){
		mdnOtpDAO = new MdnOtpDAO();
		mdnOtpDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return mdnOtpDAO;
	}

	public AuditLogDAO getAuditLogDAO(){
		auditLogDAO = new AuditLogDAO();
		auditLogDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return auditLogDAO;
	}
	
	public SubscriberFavoriteDAO getSubscriberFavoriteDAO(){
		subscriberFavoriteDAO = new SubscriberFavoriteDAO();
		subscriberFavoriteDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return subscriberFavoriteDAO;
	}
	
	public FavoriteCategoryDAO getFavoriteCategoryDAO(){
		favoriteCategoryDAO = new FavoriteCategoryDAO();
		favoriteCategoryDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return favoriteCategoryDAO;
	}
	
	public PartnerDefaultServicesDAO getPartnerDefaultServicesDAO(){
		partnerDefaultServicesDAO = new PartnerDefaultServicesDAO();
		partnerDefaultServicesDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return partnerDefaultServicesDAO;
		}
	
	public ServiceDefaultConfigurationDAO getServiceDefaultConfigurationDAO(){
		serviceDefaultConfigurationDAO = new ServiceDefaultConfigurationDAO();
		serviceDefaultConfigurationDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return serviceDefaultConfigurationDAO;
		}
	
	public CashinFirstTimeDAO getCashinFirstTimeDAO() {
		cftDAO = new CashinFirstTimeDAO();
		cftDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return cftDAO;
	}
	
	public BranchCodeDAO getBranchCodeDAO() {
		branchCodeDAO = new BranchCodeDAO();
		branchCodeDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return branchCodeDAO;
	}
	
	public KtpDetailsDAO getKtpDetailsDAO() {
		
		ktpDetailsDAO = new KtpDetailsDAO();
		ktpDetailsDAO.setHibernateSessionHolder(getHibernateSessionHolder());
		return ktpDetailsDAO;
	}
}
