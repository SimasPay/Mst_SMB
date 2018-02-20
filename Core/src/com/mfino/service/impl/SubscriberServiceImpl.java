package com.mfino.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.AddressDAO;
import com.mfino.dao.BrandDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.SubscriberQuery;
import com.mfino.domain.Address;
import com.mfino.domain.Brand;
import com.mfino.domain.Company;
import com.mfino.domain.Groups;
import com.mfino.domain.MfinoUser;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberSyncRecord;
import com.mfino.errorcodes.Codes;
import com.mfino.exceptions.NoSubscriberFoundException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.EnumTextService;
import com.mfino.service.MDNRangeService;
import com.mfino.service.MailService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

/**
 *
 * @author xchen
 */
@Service("SubscriberServiceImpl")
public class SubscriberServiceImpl implements SubscriberService{

	public Logger log = LoggerFactory.getLogger(this.getClass());
	public static Logger sLog = LoggerFactory.getLogger(SubscriberServiceImpl.class);
	// public final String STATUSRETIRED = "RETIRED";
	public final String PREPAID = "PREPAID";
	public final String POSTPAID = "POSTPAID";
	public final String DEFAULT_CURRENCY = "IDR";
	public final String DEFAULT_ADDRESS = StringUtils.EMPTY;
	public final String DEFAULT_LINE2 = null;
	public final String DEFAULT_CITY = StringUtils.EMPTY;
	public final String DEFAULT_STATE = StringUtils.EMPTY;
	public final String DEFAULT_COUNTRY = StringUtils.EMPTY;
	public final String DEFAULT_ZIPCODE = StringUtils.EMPTY;
	public static Map<Integer, String> errorCodesMap = new HashMap<Integer, String>();
	public final int Sucess = 0;
	public final int Failed = 1;
	public final int Registered = 1;
	public static DAOFactory daoFactory = DAOFactory.getInstance();
	public static SubscriberMDNDAO subscriberMDNDAO = daoFactory.getSubscriberMdnDAO();
	public static PocketTemplateDAO pocketTemplateDAO = daoFactory.getPocketTemplateDao();
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("MDNRangeServiceImpl")
	private MDNRangeService mdnRangeService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;

	static {

		errorCodesMap.put(CmFinoFIX.SynchError_Success, "Success");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Subscriber_Bad_Line_Format,"Invalid Line");
		errorCodesMap.put(CmFinoFIX.SynchError_Missing_Mandatory_Fields, "Missing Mandatory Fields");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Other, "Failed Other");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Subscriber_MDN_does_not_exist, "Failed, Subscriber MDN does not exist");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Subscriber_does_not_exist, "Failed, Subscriber does not exist");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Subscriber_MDN_status_pending_retired_or_retired, "Failed, Subscriber MDN status is retired or pending retired");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Subscriber_is_registered_as_an_active_merchant, "Failed, This subscriber is registered as an active merchant, Hence MDN will be suspended to prevent merchant performing transactions");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Subscriber_already_exists, "Failed, Subscriber already exists");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_Values, "Failed, Invalid parameter value");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Pocket_Template_Not_Found, "Failed, Pocket template not found");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_MDN_Does_Not_Exists_In_Range, "Failed, MDN does not exists in range");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_MDN, "Failed, Invalid MDN");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_UserName, "Failed, Invalid Username");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Missing_Subscriber, "Failed, Subscriber does not exist");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Missing_Status, "Failed, Invalid MDN Status");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_Company, "Failed, Invalid prefix in MDN");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Merchant_already_exists, "Failed, Merchant Already Exists");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_AccountType, "Failed, Invalid Subscriber Status");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_ParentID, "Failed, Invalid ParentID");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_DCT, "Failed, Invalid DCT");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_GroupID_Cannot_Be_Null, "Failed, GroupID cannot be null");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_GroupID_Should_Be_Null, "Failed, GroupID should be null");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_PartnerType, "Failed, Invalid PartnerType");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_Subscriber_Not_Active, "Failed, Subscriber is not Active");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Username_Already_Exists, "Failed, This username already exists please try another");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_LowBalance_Notification_Not_Registered, "Failed, Low Balance Notification Not Registered");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_Product, "Failed, Invalid Product provided");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_IDType, "Failed, Invalid ID Type");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_Region, "Failed, Invalid Region");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_EmailID_Missing, "Failed, Email Missing");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_Invalid_Email, "Failed, Invalid EmailID");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_DCT_Should_Be_NULL, "Failed, DCT Should be Null");
		errorCodesMap.put(CmFinoFIX.SynchError_Failed_IP_Should_Be_NULL, "Failed, Source IP Should be Null");
		
	}
	//Before Correcting errors reported by Findbugs:
	//public static List<Integer> languages=new ArrayList<Integer>();

	//After Correcting the errors reported by Findbugs
	public static final List<Integer> languages=new ArrayList<Integer>();
	public void getLanguages(){
		HashMap<String, String> results = enumTextService.getEnumTextSet(CmFinoFIX.TagID_Language, null);
		if(!results.isEmpty()){
			Iterator<String> iterator=results.keySet().iterator();
			while(iterator.hasNext()){
				languages.add(Integer.valueOf(iterator.next()));
			}
		}
	}
	
	/*
	 * 
	 * New Retire Subscriber As per ticket #486
	 * 
	 */
	public int retireSubscriber(SubscriberMdn subscriberMDN) {
		retireSubscriberAndPockets(subscriberMDN);
		return Codes.SUCCESS;
		//        }
	}

	public void retireSubscriberAndPockets(SubscriberMdn subscriber) {
		Set<Pocket> pokects = subscriber.getPockets();
		Iterator<Pocket> iterator = pokects.iterator();
		while (iterator.hasNext()) {
			Pocket pocket = iterator.next();
			Long a =pocket.getStatus().longValue();
			Integer b = a.intValue();
			
			if (!b.equals(CmFinoFIX.PocketStatus_Retired)) {
				pocket.setStatus(CmFinoFIX.PocketStatus_PendingRetirement);
				pocket.setIsdefault(CmFinoFIX.Boolean_True);
				PocketDAO pocDAO = daoFactory.getPocketDAO();
				pocDAO.save(pocket);
			}
		}
	}

	public Pocket getDefaultPocket(Long subscriberMDNId, Integer pocketType, Integer commodity) {
		sLog.info("SubscriberService getDefaultPocket subscriberMDNId="+subscriberMDNId+", pocketType="+pocketType+", commodity="+commodity);
		PocketDAO dao = daoFactory.getPocketDAO();

		PocketQuery query = new PocketQuery();
		query.setMdnIDSearch(subscriberMDNId);
		query.setIsDefault(Boolean.TRUE);
		query.setPocketType(pocketType);
		query.setCommodity(commodity);

		List<Pocket> results = dao.get(query);
		Pocket p = null;
		if (results != null && results.size() > 0) {
			p = results.get(0);
		}

		return p;
	}

	public BigDecimal checkBalance(Long subscriberMDNId, Integer pocketType, Integer commodity) {
		Pocket p = getDefaultPocket(subscriberMDNId, pocketType, commodity);
		return (BigDecimal) ((p != null && p.getCurrentbalance() != null) ? p.getCurrentbalance() : new BigDecimal(0));
	}
	

	public Company getCompanyFromMDN(String mdn) {
		Company company = null;
		if (mdn == null || mdn.length() < 4) {
			return company;
		}
		BrandDAO dao = daoFactory.getBrandDAO();
		List<Brand> results = dao.getAll();
		if (results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				if (mdn.startsWith(getCountryCode()+ results.get(i).getPrefixcode())) {
					company = results.get(i).getCompany();
					break;
				}
			}
		}

		return company;
	}

	public int fillSubscriberMDN(SubscriberMdn mdn, SubscriberSyncRecord subscriberSyncRecord) {
		//mdn feilds
		if (StringUtils.isNotBlank(subscriberSyncRecord.getIdType())) {
			mdn.setIdtype(subscriberSyncRecord.getIdType());
		}
		if (StringUtils.isNotBlank(subscriberSyncRecord.getIdNumber())) {
			mdn.setIdnumber(subscriberSyncRecord.getIdNumber());
		}
		if (StringUtils.isNotBlank(subscriberSyncRecord.getImsi())) {
			mdn.setImsi(subscriberSyncRecord.getImsi());
		}
		if (StringUtils.isNotBlank(subscriberSyncRecord.getMarketingCategory())) {
			try
			{
				Integer mrkCat = Integer.parseInt(subscriberSyncRecord.getMarketingCategory());
				if(mrkCat>0 && mrkCat <= ConfigurationUtil.getMaxMarketingCategory())
				{
					mdn.setMarketingcategory(subscriberSyncRecord.getMarketingCategory());
				}
				else
				{
					log.info("Marketing Category is invalid for MDN = " + subscriberSyncRecord.getMdn());
					return Failed;
				}
			}
			catch(Exception error)
			{
				log.error("Marketing Category is invalid for MDN = " + subscriberSyncRecord.getMdn(), error);
				return Failed;

			}
		}
		return Sucess;
	}

	public int createNewSubscriber(SubscriberSyncRecord subscriberSyncRecord) {
		return createNewSubscriber(subscriberSyncRecord, null);
	}

	public int createNewSubscriber(SubscriberSyncRecord subscriberSyncRecord, Long companyID) {
		PocketTemplate cbossPrepaid;
		PocketTemplate cbossPostpaid;
		cbossPrepaid = pocketTemplateDAO.getById(ConfigurationUtil.getDefaultPocketTemplateCBOSSPrepaid());
		cbossPostpaid = pocketTemplateDAO.getById(ConfigurationUtil.getDefaultPocketTemplateCBOSSPostpaid());
		
		try {
			SubscriberDAO subDAO = daoFactory.getSubscriberDAO();
			AddressDAO addressDao = daoFactory.getAddressDAO();
			if (subscriberSyncRecord.getMdn() == null || !subscriberSyncRecord.getMdn().startsWith("62")) {
				return CmFinoFIX.SynchError_Failed_Invalid_MDN;
			}
			SubscriberMdn mdn = subscriberMDNDAO.getByMDN(subscriberSyncRecord.getMdn());
			if (mdn != null) {
				log.info("Create New subscriber failed. Subscriber MDN already exists in DB - " + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Subscriber_already_exists;
			}
			Subscriber subscriber = new Subscriber();
			subscriber.setType(CmFinoFIX.SubscriberType_Subscriber);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			subscriber.setNotificationmethod(CmFinoFIX.NotificationMethod_SMS);
			subscriber.setTimezone(CmFinoFIX.Timezone_West_Indonesia_Time);
			subscriber.setStatustime(new Timestamp());
			if (subscriberSyncRecord.isBulkUploadRecord()) {
				// Product is Mandatory
				if (!(CmFinoFIX.MDNBrand_Fren.equals(subscriberSyncRecord
						.getProduct())
						|| CmFinoFIX.MDNBrand_Hepi.equals(subscriberSyncRecord
								.getProduct())
								|| CmFinoFIX.MDNBrand_Mobi.equals(subscriberSyncRecord
										.getProduct())
										|| CmFinoFIX.MDNBrand_SMART_Postpaid
										.equals(subscriberSyncRecord.getProduct())
										|| CmFinoFIX.MDNBrand_SMART_Prepaid
										.equals(subscriberSyncRecord.getProduct()))) {
					log.info("Product is invalid for MDN = " + subscriberSyncRecord.getMdn());
					return CmFinoFIX.SynchError_Failed_Invalid_Product;
				}
			}
			// IDType is Not Mandatory with set of values
			if ( StringUtils.isNotBlank(subscriberSyncRecord.getIdType()) && 
					!(CmFinoFIX.IDType_Employee_ID.equals(subscriberSyncRecord
							.getIdType())
							|| CmFinoFIX.IDType_KIMS.equals(subscriberSyncRecord
									.getIdType())
									|| CmFinoFIX.IDType_KITAS.equals(subscriberSyncRecord
											.getIdType())
											|| CmFinoFIX.IDType_KMS.equals(subscriberSyncRecord
													.getIdType())
													|| CmFinoFIX.IDType_KPJ.equals(subscriberSyncRecord
															.getIdType())
															|| CmFinoFIX.IDType_KTP.equals(subscriberSyncRecord
																	.getIdType())
																	|| CmFinoFIX.IDType_Passport.equals(subscriberSyncRecord
																			.getIdType())
																			|| CmFinoFIX.IDType_SIM.equals(subscriberSyncRecord
																					.getIdType()))) {
				log.info("IDType is invalid for MDN = " + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Invalid_IDType;
			}
			Company company = getCompanyFromMDN(subscriberSyncRecord.getMdn());
			if (companyID != null) {
				//             UserDAO userdao = new UserDAO();
				//           User user = userdao.getByUserName(createdBy);
				if (company != null && (company.getId().equals(companyID))) {
					subscriber.setCompany(company);
				}                
				else {
					log.info("Cannot Add MDN of other Brands" + subscriberSyncRecord.getMdn());
					return CmFinoFIX.SynchError_Failed_Invalid_Company;
				}
			}
			else
			{
				if (company == null) {
					log.info("Create New subscriber failed. Invalid Company - " + subscriberSyncRecord.getMdn());
					return CmFinoFIX.SynchError_Failed_Invalid_Company;
				}
				else
				{
					subscriber.setCompany(company);	            	
				}
			}
			Address address = new Address();
			if (StringUtils.isNotBlank(subscriberSyncRecord.getAddress()) && StringUtils.isNotBlank(subscriberSyncRecord.getCity())) {
				address.setLine1(subscriberSyncRecord.getAddress());
				address.setCity(subscriberSyncRecord.getCity());
			} else if (StringUtils.isNotBlank(subscriberSyncRecord.getAddress())) {
				address.setLine1(subscriberSyncRecord.getAddress());
				address.setCity(DEFAULT_CITY);
			} else if (StringUtils.isNotBlank(subscriberSyncRecord.getCity())) {
				address.setLine1(DEFAULT_ADDRESS);
				address.setCity(subscriberSyncRecord.getCity());
			} else {
				address.setLine1(DEFAULT_ADDRESS);
				address.setCity(DEFAULT_CITY);
			}
			address.setState(DEFAULT_STATE);
			address.setZipcode(DEFAULT_ZIPCODE);
			address.setCountry(DEFAULT_COUNTRY);
			addressDao.saveWithoutFlush(address);
			subscriber.setAddressBySubscriberaddressid(address);
			int subscriberCreation = fillSubscriber(subscriber, subscriberSyncRecord, true);
			//TODO: NEED TO GET THE MSPID
			if (subscriber.getMfinoServiceProvider() == null) {
				MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
				subscriber.setMfinoServiceProvider(mspDAO.getById(1L));
			}
			if (subscriberCreation != Sucess) {
				log.info("Create New subscriber failed. Invalid parameters - " + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Invalid_Values;
			}
			subDAO.saveWithoutFlush(subscriber);
			if(StringUtils.isNotBlank(subscriber.getEmail()) && systemParametersService.getIsEmailVerificationNeeded()) {//send Email verification mail 
				mailService.generateEmailVerificationMail(subscriber, subscriber.getEmail());
			}
			SubscriberMdn subscriberMDN = new SubscriberMdn();
			subscriberMDN.setAuthenticationphrase("");
			subscriberMDN.setSubscriber(subscriber);
			subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Initialized);
			subscriberMDN.setStatustime(new Timestamp());
			subscriberMDN.setMdn(subscriberSyncRecord.getMdn());
			subscriberMDN.setMdnbrand(subscriberSyncRecord.getProduct());
			int subscriberMDNCreation = fillSubscriberMDN(subscriberMDN, subscriberSyncRecord);
			if (subscriberMDNCreation != Sucess) {
				log.info("Create New subscriber failed. Invalid parameters - " + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Invalid_Values;
			}
			subscriberMDNDAO.saveWithoutFlush(subscriberMDN);

			if (POSTPAID.equalsIgnoreCase(subscriberSyncRecord.getActiveAccountType())) {
				createPocket(cbossPostpaid, subscriberMDN);
			} else if (PREPAID.equalsIgnoreCase(subscriberSyncRecord.getActiveAccountType())) {
				createPocket(cbossPrepaid, subscriberMDN);
			} else {
				if (subscriberSyncRecord.isBulkUploadRecord()) {
					return CmFinoFIX.SynchError_Failed_Invalid_AccountType;
				}
				log.info("Invalid Account Type " + subscriberSyncRecord.getActiveAccountType() + " for MDN = " + subscriberSyncRecord.getMdn() + ". Using PREPAID as default.");
				createPocket(cbossPrepaid, subscriberMDN);
			}
		} catch (Exception error) {
			log.error("Registration of subscriber failed ", error);
			return CmFinoFIX.SynchError_Failed_Other;
		}
		return CmFinoFIX.SynchError_Success;
	}

	public int updateSubscriber(SubscriberSyncRecord subscriberSyncRecord) {
		try {
			SubscriberDAO subDAO = daoFactory.getSubscriberDAO();
			AddressDAO addressDao = daoFactory.getAddressDAO();
			SubscriberMdn mdn = subscriberMDNDAO.getByMDN(subscriberSyncRecord.getMdn());
			if (mdn == null) {
				log.info("Update subscriber failed. There is no subscriber MDN in DB -" + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Subscriber_MDN_does_not_exist;
			}
			if (StringUtils.isNotBlank(subscriberSyncRecord.getActiveAccountType())) {
				if (POSTPAID.equalsIgnoreCase(subscriberSyncRecord.getActiveAccountType()) || PREPAID.equalsIgnoreCase(subscriberSyncRecord.getActiveAccountType())) {
					int updation = updatePocket(subscriberSyncRecord, mdn);
					if (updation != Sucess) {
						log.info("Update to subscriber failed, Pocket template does not exists" + subscriberSyncRecord.getMdn());
						return CmFinoFIX.SynchError_Failed_Pocket_Template_Not_Found;

					}
				} else {
					log.info("Update subscriber failed. Pocket type invalid -" + subscriberSyncRecord.getMdn());
					return CmFinoFIX.SynchError_Failed_Invalid_Values;
				}
			}
			if (StringUtils.isNotBlank(subscriberSyncRecord.getProduct())) {
				if (CmFinoFIX.MDNBrand_Fren.equals(subscriberSyncRecord.getProduct()) || CmFinoFIX.MDNBrand_Hepi.equals(subscriberSyncRecord.getProduct()) || CmFinoFIX.MDNBrand_Mobi.equals(subscriberSyncRecord.getProduct()) || CmFinoFIX.MDNBrand_SMART_Postpaid.equals(subscriberSyncRecord.getProduct()) || CmFinoFIX.MDNBrand_SMART_Prepaid.equals(subscriberSyncRecord.getProduct())) {
					mdn.setMdnbrand(subscriberSyncRecord.getProduct());
				} else {
					log.info("Product is invalid for MDN = " + subscriberSyncRecord.getMdn());
					return CmFinoFIX.SynchError_Failed_Invalid_Values;
				}
			}
			Subscriber subscriber = mdn.getSubscriber();
			if (subscriber == null) {
				log.info("Update subscriber failed. There is no subscriber in DB -" + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Subscriber_does_not_exist;
			}
			if (CmFinoFIX.MDNStatus_PendingRetirement.equals(mdn.getStatus()) || CmFinoFIX.MDNStatus_Retired.equals(mdn.getStatus())) {
				log.info("Update subscriber failed. Subscriber MDN status is retired in DB -" + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Subscriber_MDN_status_pending_retired_or_retired;
			}

			int subscriberMDNCreation = fillSubscriberMDN(mdn, subscriberSyncRecord);
			if (subscriberMDNCreation != Sucess) {
				log.info("Update subscriber failed. Invalid parameters - " + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Invalid_Values;
			}

			int subscriberUpdation = fillSubscriber(subscriber, subscriberSyncRecord, false);
			if (subscriberUpdation != Sucess) {
				log.info("Update subscriber failed. Junk Values -" + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Invalid_Values;
			}
			subDAO.saveWithoutFlush(subscriber);
			if(StringUtils.isNotBlank(subscriber.getEmail()) && systemParametersService.getIsEmailVerificationNeeded()) {//send Email verification mail 
				mailService.generateEmailVerificationMail(subscriber, subscriber.getEmail());
			}
			Address address = subscriber.getAddressBySubscriberaddressid();
			if (address != null) {
				if (StringUtils.isNotBlank(subscriberSyncRecord.getAddress())) {
					address.setLine1(subscriberSyncRecord.getAddress());
					address.setLine2(DEFAULT_LINE2);
				}
				if (StringUtils.isNotBlank(subscriberSyncRecord.getCity())) {
					address.setCity(subscriberSyncRecord.getCity());
				}
				addressDao.saveWithoutFlush(address);
			} else {
				if (StringUtils.isNotBlank(subscriberSyncRecord.getAddress()) || StringUtils.isNotBlank(subscriberSyncRecord.getCity())) {
					Address newAddress = new Address();
					if (StringUtils.isNotBlank(subscriberSyncRecord.getAddress())) {
						newAddress.setLine1(subscriberSyncRecord.getAddress());
						newAddress.setCity(DEFAULT_CITY);
					} else {
						newAddress.setLine1(DEFAULT_ADDRESS);
					}
					if (StringUtils.isNotBlank(subscriberSyncRecord.getCity())) {
						newAddress.setCity(subscriberSyncRecord.getCity());
					}
					newAddress.setState(DEFAULT_STATE);
					newAddress.setZipcode(DEFAULT_ZIPCODE);
					newAddress.setCountry(DEFAULT_COUNTRY);
					addressDao.saveWithoutFlush(newAddress);
					subscriber.setAddressBySubscriberaddressid(newAddress);
				}
			}
		} catch (Exception error) {
			log.error("Update to subscriber failed ", error);
			return CmFinoFIX.SynchError_Failed_Other;
		}
		return CmFinoFIX.SynchError_Success;
	}

	public int updateSubscriberRetiered(SubscriberSyncRecord subscriberSyncRecord) {
		try {
			SubscriberDAO subscriberDAO = daoFactory.getSubscriberDAO();

			SubscriberMdn mdn = subscriberMDNDAO.getByMDN(subscriberSyncRecord.getMdn());
			if (mdn == null) {
				log.info("Update subscriber retirement failed. There is no subscriber MDN in DB - " + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Subscriber_MDN_does_not_exist;
			}
			Subscriber subscriber = mdn.getSubscriber();
			if (subscriber == null) {
				log.info("Update subscriber retirement failed. There is no subscriber in DB" + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Subscriber_does_not_exist;
			}
			if (CmFinoFIX.MDNStatus_PendingRetirement.equals(mdn.getStatus()) || CmFinoFIX.MDNStatus_Retired.equals(mdn.getStatus())) {
				log.info("Update subscriber retirement failed. Subscriber MDN status is retired in DB" + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Subscriber_MDN_status_pending_retired_or_retired;
			}
			int code = retireSubscriber(mdn);
			subscriberDAO.saveWithoutFlush(subscriber);
			if (Codes.OPERATION_NOT_ALLOWED == code) {
				log.info("Subscriber retirement failed. This subscriber is registered as an active merchant. " + "MDN will be suspended to prevent merchant performing transactions." + subscriberSyncRecord.getMdn());
				return CmFinoFIX.SynchError_Failed_Subscriber_is_registered_as_an_active_merchant;
			}
		} catch (Exception error) {
			log.error("Retiering subscriber failed ", error);
			return CmFinoFIX.SynchError_Failed_Other;
		}
		return CmFinoFIX.SynchError_Success;
	}
	
	public int updatePocket(SubscriberSyncRecord subscriberSyncRecord, SubscriberMdn mdn) {
		PocketTemplate cbossPrepaid;
		PocketTemplate cbossPostpaid;
		cbossPrepaid = pocketTemplateDAO.getById(ConfigurationUtil.getDefaultPocketTemplateCBOSSPrepaid());
		cbossPostpaid = pocketTemplateDAO.getById(ConfigurationUtil.getDefaultPocketTemplateCBOSSPostpaid());
		
		PocketDAO pocketDAO = daoFactory.getPocketDAO();
		if (StringUtils.isNotBlank(subscriberSyncRecord.getActiveAccountType())) {
			Pocket p = getDefaultPocket(mdn.getId().longValue(), CmFinoFIX.PocketType_BOBAccount, CmFinoFIX.Commodity_Airtime);
			if (p != null) {
				PocketTemplate old = p.getPocketTemplateByPockettemplateid();
				if (CmFinoFIX.BillingType_PrePaid.equals(old.getBillingtype()) && subscriberSyncRecord.getActiveAccountType().equalsIgnoreCase(POSTPAID)) {
					p.setPocketTemplateByPockettemplateid(cbossPostpaid);
					p.setPocketTemplateByOldpockettemplateid(cbossPrepaid);
					p.setPockettemplatechangetime(new Timestamp());
				} else if (CmFinoFIX.BillingType_PostPaid.equals(old.getBillingtype()) && subscriberSyncRecord.getActiveAccountType().equalsIgnoreCase(PREPAID)) {
					p.setPocketTemplateByPockettemplateid(cbossPrepaid);
					p.setPocketTemplateByOldpockettemplateid(cbossPostpaid);
					p.setPockettemplatechangetime(new Timestamp());
				} else if ((CmFinoFIX.BillingType_PrePaid.equals(old.getBillingtype()) && subscriberSyncRecord.getActiveAccountType().equalsIgnoreCase(PREPAID)) || (CmFinoFIX.BillingType_PostPaid.equals(old.getBillingtype()) && subscriberSyncRecord.getActiveAccountType().equalsIgnoreCase(POSTPAID))) {
					// do nothing as they are as required
				} else {
					log.info("Invalid Active Account Type " + subscriberSyncRecord.getActiveAccountType() + " for MDN = " + subscriberSyncRecord.getMdn());
					return Failed;
				}
				pocketDAO.saveWithoutFlush(p);
			} else {
				log.info("Invalid Account Type " + subscriberSyncRecord.getActiveAccountType() + " for MDN = " + subscriberSyncRecord.getMdn());
				return Failed;
			}
		}
		return Sucess;
	}

	public Integer getLanguage(Integer language) {
		if(languages==null||languages.isEmpty()){
			getLanguages();
		}
		for(Integer lan:languages){
			if(lan.equals(language)){
				return lan;
			}
		}
		return -1;      
	}

	public int fillSubscriber(Subscriber subscriber, SubscriberSyncRecord subscriberSyncRecord, boolean isNew) {
		if (StringUtils.isNotBlank(subscriberSyncRecord.getFirstName())) {
			subscriber.setFirstname(subscriberSyncRecord.getFirstName());
		}
		if (StringUtils.isNotBlank(subscriberSyncRecord.getLastName())) {
			subscriber.setLastname(subscriberSyncRecord.getLastName());
		}
		if (subscriberSyncRecord.getLanguage()!=null) {
			Integer lang = getLanguage(subscriberSyncRecord.getLanguage());
			if (lang == -1) {
				log.info("Language is invalid for MDN = " + subscriberSyncRecord.getMdn());
				return Failed;
			} else {
				subscriber.setLanguage(lang);
			}
		}
		if (StringUtils.isNotBlank(subscriberSyncRecord.getGender())) {
			if (CmFinoFIX.Gender_Female.equals(subscriberSyncRecord.getGender()) || CmFinoFIX.Gender_Male.equals(subscriberSyncRecord.getGender())) {
				subscriber.setGender(subscriberSyncRecord.getGender());
			} else {
				log.info("Gender is invalid for MDN = " + subscriberSyncRecord.getMdn());
				return Failed;
			}
		}
		if (StringUtils.isNotBlank(subscriberSyncRecord.getProduct())) {
			if (CmFinoFIX.MDNBrand_Fren.equals(subscriberSyncRecord.getProduct()) || CmFinoFIX.MDNBrand_Hepi.equals(subscriberSyncRecord.getProduct()) || CmFinoFIX.MDNBrand_Mobi.equals(subscriberSyncRecord.getProduct()) || CmFinoFIX.MDNBrand_SMART_Postpaid.equals(subscriberSyncRecord.getProduct()) || CmFinoFIX.MDNBrand_SMART_Prepaid.equals(subscriberSyncRecord.getProduct())) {
				subscriber.setMdnbrand(subscriberSyncRecord.getProduct());
			} else {
				log.info("Product is invalid for MDN = " + subscriberSyncRecord.getMdn());
				return Failed;
			}
		}
		if (subscriberSyncRecord.getDateOfBirth() != null) {          
			subscriber.setDateofbirth(new Timestamp(subscriberSyncRecord.getDateOfBirth()));
		}
		if (StringUtils.isNotBlank(subscriberSyncRecord.getBirthPlace())) {
			subscriber.setBirthplace(subscriberSyncRecord.getBirthPlace());
		}

		if (StringUtils.isNotBlank(subscriberSyncRecord.getEmail())) {
			subscriber.setEmail(subscriberSyncRecord.getEmail());
		}
		subscriber.setIsemailverified(CmFinoFIX.Boolean_True);
		// If "isNew" is true then he is newSubscriber otherwise he is existing subscriber.
		if (isNew) {
			if (StringUtils.isNotBlank(subscriberSyncRecord.getCurrency())) {
				if (CmFinoFIX.Currency_IDR.equals(subscriberSyncRecord.getCurrency()) || CmFinoFIX.Currency_USD.equals(subscriberSyncRecord.getCurrency()) || CmFinoFIX.Currency_UnKnown.equals(subscriberSyncRecord.getCurrency())) {
					subscriber.setCurrency(subscriberSyncRecord.getCurrency());
				} else {
					log.info("Currency is invalid for MDN = " + subscriberSyncRecord.getMdn());
					return Failed;
				}
			} else {
				log.info("Currency is null for MDN = " + subscriberSyncRecord.getMdn() + ". Using " + DEFAULT_CURRENCY + " as default.");
				subscriber.setCurrency(DEFAULT_CURRENCY);
			}
		} else {
			if (StringUtils.isNotBlank(subscriberSyncRecord.getCurrency())) {
				if (CmFinoFIX.Currency_IDR.equals(subscriberSyncRecord.getCurrency()) || CmFinoFIX.Currency_USD.equals(subscriberSyncRecord.getCurrency()) || CmFinoFIX.Currency_UnKnown.equals(subscriberSyncRecord.getCurrency())) {
					subscriber.setCurrency(subscriberSyncRecord.getCurrency());
				} else {
					log.info("Currency is invalid for MDN = " + subscriberSyncRecord.getMdn());
					return Failed;
				}
			}
		}
		return Sucess;
	}

	public void createPocket(PocketTemplate pTemplate, SubscriberMdn subMDN) {
		Pocket pocket = new Pocket();
		PocketDAO pocketDAO = daoFactory.getPocketDAO();
		pocket.setPocketTemplateByPockettemplateid(pTemplate);
		pocket.setSubscriberMdn(subMDN);
		pocket.setStatustime(new Timestamp());
		pocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
		pocket.setIsdefault(CmFinoFIX.Boolean_True);
		if(subMDN.getSubscriber().getCompany() != null) {
			pocket.setCompany(subMDN.getSubscriber().getCompany());
		}
		pocketDAO.saveWithoutFlush(pocket);
	}

	public String getErrorMessage(int errorCode) {
		return errorCodesMap.get(errorCode);
	}

	public String getErrorMessage(int errorCode, String separator) {
		return errorCode + separator + errorCodesMap.get(errorCode);
	}

	public Pocket getDefaultPocket(String mdn,
			Integer pockettype, Integer commodity) {
		SubscriberMdn subscriberMDN= subscriberMDNDAO.getByMDN(normalizeMDN(mdn));
		if(subscriberMDN==null){
			return null;
		}
		return getDefaultPocket(subscriberMDN.getId().longValue(), pockettype, commodity);
	}

	public Pocket getDefaultPocket(Long subscriberMDNId, Long templateId) {
		PocketDAO dao = DAOFactory.getInstance().getPocketDAO();

		PocketQuery query = new PocketQuery();
		query.setMdnIDSearch(subscriberMDNId);
		query.setIsDefault(Boolean.TRUE);
		query.setPocketTemplateID(templateId);


		List<Pocket> results = dao.get(query);
		Pocket p = null;
		if (results != null && results.size() > 0) {
			p = results.get(0);
		}

		return p;
	}


	
	/**
	 * Fetches the e-money pocket for a given subscriber and configuration.
	 * 
	 * @param subscriberMDNId
	 * @param isDefault 
	 * @param isCollectorPocket
	 * @param isSuspensePocket
	 * @return e-money pocket that satisfies the given criteria
	 */
	public Pocket getEmoneyPocket(Long subscriberMDNId, boolean isDefault, boolean isCollectorPocket, boolean isSuspensePocket) {
		PocketDAO dao = DAOFactory.getInstance().getPocketDAO();
		PocketQuery query = new PocketQuery();
		query.setMdnIDSearch(subscriberMDNId);
		query.setIsDefault(isDefault);
		query.setIsCollectorPocket(isCollectorPocket);
		query.setIsSuspencePocketAllowed(isSuspensePocket);
		query.setPocketType(CmFinoFIX.PocketType_SVA);
		
		List<Pocket> results = dao.get(query);
		Pocket pocket = null;
		if (results != null && results.size() > 0) {
			pocket = results.get(0);
		}

		return pocket;
	}

	/**
	 * Saves the Subscriber to the database
	 * @param subscriber
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void saveSubscriber(Subscriber subscriber){
		SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
		subscriberDAO.save(subscriber);
}
	
	/**
	 * Gets the Subscriber from table by the subscriber ID
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Subscriber getSubscriberbySubscriberId(Long subscriberId){
		Subscriber subscriber = null;
		if(subscriberId!=null){
			log.info("getting subscriber with id: "+subscriberId);
			SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
			subscriber = subscriberDAO.getById(subscriberId);
		}
		return subscriber;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public boolean isPasswordExpired(MfinoUser user) {
		int expiredays = systemParametersService.getInteger(SystemParameterKeys.DAYS_TO_EXPIRE_PASSWORD);
		if(expiredays>0){
			if(user.getLastpasswordchangetime()==null){
				return true;
			}
			Date expiryDate = DateUtil.addDays(user.getLastpasswordchangetime(), expiredays);
			if(DateUtils.truncate(new Date(),  Calendar.DATE).after(DateUtils.truncate(expiryDate,Calendar.DATE))){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Normalize mdn with the country code
	 * @param MDN
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public String normalizeMDN(String MDN) {
		if (StringUtils.isBlank(MDN))
			return StringUtils.EMPTY;
		
		String countryCode = getCountryCode();
		boolean startsWithCounrtyCode = false;
		int start = 0;

		MDN = MDN.trim();

		if (MDN.startsWith("00")) {
			MDN = MDN.substring(2);
		}
		
		if(MDN.startsWith(countryCode)){
			MDN = MDN.substring(countryCode.length());
			startsWithCounrtyCode = true;
		}
				
		while (start < MDN.length()) {
			if ('0' == MDN.charAt(start)) {
				startsWithCounrtyCode = true;
				start++;
			}
			else
				break;
		}

		if (startsWithCounrtyCode) 
			return countryCode + MDN.substring(start);
		else 
			return countryCode + MDN;
	}

	
	private String getCountryCode() {
		String countryCode = systemParametersService.getString(SystemParameterKeys.COUNTRY_CODE);
		if(countryCode==null)
		{
			countryCode = ConfigurationUtil.getCountryCode();
		}
		return countryCode;
	}
	
	public String deNormalizeMDN(String MDN) {
		String countryCode = getCountryCode();

		int start = 0;
		if (StringUtils.isBlank(MDN))
			return StringUtils.EMPTY;

		MDN = MDN.trim();

		while (start < MDN.length()) {
			if ('0' == MDN.charAt(start))
				start++;
			else
				break;
		}

		if (MDN.startsWith(countryCode, start)) {
			start += countryCode.length();
		}

		return MDN.substring(start);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<Subscriber> getByQuery(SubscriberQuery query){
		SubscriberDAO sDAO = DAOFactory.getInstance().getSubscriberDAO();
		return sDAO.get(query);
  }
	public void verifyEmail(Long subscriberID, String email) throws Exception{
		SubscriberDAO subscriberDAO = daoFactory.getSubscriberDAO();
		Subscriber subscriber = subscriberDAO.getById(subscriberID);
		if(subscriber == null) {
			throw new NoSubscriberFoundException("Subscriber with ID " + subscriberID + " not found");
		}
		if(!email.equals(subscriber.getEmail())) {
			throw new Exception("Invalid email");
		}
		subscriber.setIsemailverified(CmFinoFIX.Boolean_True);
		subscriberDAO.save(subscriber);		
	}

	public Company getDefaultCompanyForSubscriber() {
		CompanyDAO companyDAO = daoFactory.getCompanyDAO();
		Company company = companyDAO.getById(1L);
		return company;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<Object[]> getNewSubscribersCount(Date startDate, Date endDate) {
		SubscriberDAO sDao = daoFactory.getSubscriberDAO();
		return sDao.getNewSubscribersCount(startDate, endDate);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(Subscriber subscriber) {
		SubscriberDAO sDao = daoFactory.getSubscriberDAO();
		sDao.save(subscriber);
	}
	
	public Long getSubscriberGroupId(Subscriber subscriber) {
		SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
		SubscriberGroups sg = subscriberGroupDao.getBySubscriberID(subscriber.getId());
		return sg.getGroupid();
	}
}
