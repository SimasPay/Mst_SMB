package com.mfino.uicore.fix.processor.impl;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.AddressDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.Address;
import com.mfino.domain.Company;
import com.mfino.domain.Partner;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSPartner;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.UserService;
import com.mfino.service.impl.UserServiceImpl;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.PartnerProcessor;
import com.mfino.util.ConfigurationUtil;

/**
 * @author sasidhar
 *
 */
@Service("PartnerProcessorImpl")
public class PartnerProcessorImpl extends BaseFixProcessor implements PartnerProcessor{
	
	private PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
	private MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
	private  SubscriberDAO subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
	private SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
	private AddressDAO addressDao = DAOFactory.getInstance().getAddressDAO();
	private  boolean sendOTPOnIntialized;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("PartnerProcessor::process() method BEGIN");
		
		CMJSPartner realMsg = (CMJSPartner) msg;
		
		log.info("PartnerProcessor::process() :: realMsg.getaction()="+realMsg.getaction());

		
		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {

			CMJSPartner.CGEntries[] entries = realMsg.getEntries();

            for (CMJSPartner.CGEntries entry : entries) {
                Partner objPartner = partnerDao.getById(entry.getID());
                Subscriber objSubscriber = objPartner.getSubscriber();
                SubscriberMDN objSubscriberMdn = ((objSubscriber != null) && (objSubscriber.getSubscriberMDNFromSubscriberID().size() > 0))? objSubscriber.getSubscriberMDNFromSubscriberID().iterator().next() : null;
                
                // Check for Stale Data
                if (!entry.getRecordVersion().equals(objPartner.getVersion())) {
                    handleStaleDataException();
                }

/*              TODO : Need to check about this. 
 * 				boolean isAuthorized = Authorization.isAuthorized(CmFinoFIX.Permission_Subscriber_Other_Fields_Edit);

                if (!isAuthorized) {
                    CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    errorMsg.setErrorDescription(MessageText._("Not Authorized to edit the Subscriber details"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;
                }*/
                if(CmFinoFIX.BusinessPartnerType_ServicePartner.equals(objPartner.getBusinessPartnerType())&&entry.getBusinessPartnerType()!=null){
                	CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    errorMsg.setErrorDescription(MessageText._("Service Partner cannot be changed"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    return errorMsg;
                }
                
                updateEntity(objPartner, objSubscriber, objSubscriberMdn, entry);
                
                Address outletAddress = objPartner.getAddressByFranchiseOutletAddressID();
                if(outletAddress != null){
                	addressDao.save(outletAddress);
                }

                Address merchantAddress = objPartner.getAddressByMerchantAddressID();
                if(merchantAddress != null){
                	addressDao.save(merchantAddress);
                }
                
                sendOTPOnIntialized = ConfigurationUtil.getSendOTPOnIntialized();
				if(!sendOTPOnIntialized){
					objSubscriberMdn.setStatus(CmFinoFIX.SubscriberStatus_Active);
					objSubscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
					objPartner.setPartnerStatus(CmFinoFIX.SubscriberStatus_Active);
					subscriberStatusEventService.upsertNextPickupDateForStatusChange(objSubscriber,true);
				}
                subscriberMdnDao.save(objSubscriberMdn);
                subscriberDao.save(objSubscriber);
                partnerDao.save(objPartner);
                
                updateMessage(objPartner, objSubscriber, objSubscriberMdn, entry);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        
			
		}else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
			
            CMJSPartner.CGEntries[] entries = realMsg.getEntries();

            for (CMJSPartner.CGEntries e : entries) {
                Partner partner = new Partner();
                Subscriber subscriber = new Subscriber();
                SubscriberMDN subscriberMdn = new SubscriberMDN();
                
                updateEntity(partner, subscriber, subscriberMdn, e);
                
                subscriberMdn.setSubscriber(subscriber);
                partner.setSubscriber(subscriber);
                
                Company company = userService.getUserCompany();
                subscriber.setCompany(company);
                
                Address outletAddress = partner.getAddressByFranchiseOutletAddressID();
                if(outletAddress != null){
                	addressDao.save(outletAddress);
                }

                Address merchantAddress = partner.getAddressByMerchantAddressID();
                if(merchantAddress != null){
                	addressDao.save(merchantAddress);
                }
                
                subscriberDao.save(subscriber);
                subscriberMdnDao.save(subscriberMdn);
                partnerDao.save(partner);
                
                updateMessage(partner,subscriber,subscriberMdn, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
            
		}else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())){
				
			PartnerQuery partnerQuery = new PartnerQuery();

			if((null != realMsg.getTradeNameSearch()) && !("".equals(realMsg.getTradeNameSearch()))){
				partnerQuery.setTradeName(realMsg.getTradeNameSearch());
			}
			if((null != realMsg.getAuthorizedEmailSearch()) && !("".equals(realMsg.getAuthorizedEmailSearch()))){
				partnerQuery.setAuthorizedEmail(realMsg.getAuthorizedEmailSearch());
			}
			if((null != realMsg.getPartnerCodeSearch()) && !("".equals(realMsg.getPartnerCodeSearch()))){
				partnerQuery.setPartnerCode(realMsg.getPartnerCodeSearch());
			}
			if((null != realMsg.getCardPAN()) && !("".equals(realMsg.getCardPAN()))){
				partnerQuery.setCardPAN(realMsg.getCardPAN());
			}
			if((null != realMsg.getPartnerIDSearch()) && !("".equals(realMsg.getPartnerIDSearch()))){
				partnerQuery.setId(Long.valueOf(realMsg.getPartnerIDSearch()));
			}
			
			// *FindbugsChange*
        	// Previous -- if((null != realMsg.getPartnerTypeSearch()) && !("".equals(realMsg.getPartnerTypeSearch()))){
			// Null Comparison is enough
			if((null != realMsg.getPartnerTypeSearch())){
				partnerQuery.setPartnerType(realMsg.getPartnerTypeSearch());
			}
			
			// *FindbugsChange*
        	// Previous -- if((null != realMsg.getNotPartnerTypeSearch()) && !("".equals(realMsg.getNotPartnerTypeSearch()))){
			// Null Comparison is enough
			if((null != realMsg.getNotPartnerTypeSearch())){
				partnerQuery.setNotPartnerType(realMsg.getNotPartnerTypeSearch());
			}		
			// *FindbugsChange*
        	// Previous -- if ((realMsg.getServiceIDSearch() != null) && !("".equals(realMsg.getServiceIDSearch()))) {
			// Null Comparison is enough
			if ((realMsg.getServiceIDSearch() != null)) {
				partnerQuery.setServiceId(realMsg.getServiceIDSearch());
			}
			// *FindbugsChange*
        	// Previous -- if ((realMsg.getServiceProviderIDSearch() != null) && !("".equals(realMsg.getServiceProviderIDSearch()))) {
			// Null comparison enough
			if ((realMsg.getServiceProviderIDSearch() != null)) {
				partnerQuery.setServiceProviderId(realMsg.getServiceProviderIDSearch());
			}
			if (StringUtils.isNotBlank(realMsg.getTransactionRuleSearch())) {
				partnerQuery.setTransactionRuleId(new Long(realMsg.getTransactionRuleSearch()));
			}
			partnerQuery.setStart(realMsg.getstart());
			partnerQuery.setLimit(realMsg.getlimit());
			
            List<Partner> results = partnerDao.get(partnerQuery);
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                Partner objPartner = results.get(i);
                Subscriber objSubscriber = objPartner.getSubscriber();
                SubscriberMDN objSubscriberMdn = ((objSubscriber != null) && (objSubscriber.getSubscriberMDNFromSubscriberID().size() > 0))? objSubscriber.getSubscriberMDNFromSubscriberID().iterator().next() : null;

                CMJSPartner.CGEntries entry = new CMJSPartner.CGEntries();

                updateMessage(objPartner, objSubscriber, objSubscriberMdn, entry);
                realMsg.getEntries()[i] = entry;
            }
            
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(partnerQuery.getTotal());			
		}
		
		log.info("PartnerProcessor::process() method END");
		return realMsg;
	}
	
	
    private void updateEntity(Partner partner,Subscriber subscriber, SubscriberMDN subscriberMdn,  CMJSPartner.CGEntries entry) {
    	
    	/*TODO 
    	 * Not sure if this is reqd for creating partner, setting default values
    	 * */
        subscriberMdn.setAuthenticationPhrase("mFino360");
        subscriberMdn.setMDN(generateRandomMdn());
        subscriberMdn.setRestrictions(0);
        subscriber.setRestrictions(0);
        
        if (entry.getPartnerStatus() != null) {
        	// *FindbugsChange*
        	// Previous --  if (entry.getPartnerStatus() != partner.getPartnerStatus()) {
            if (entry.getPartnerStatus()!= null && !(entry.getPartnerStatus().equals( partner.getPartnerStatus()))) {
                subscriberMdn.setStatus(entry.getPartnerStatus());
                subscriber.setStatus(entry.getPartnerStatus());
                
                subscriberMdn.setStatusTime(new Timestamp());
                subscriber.setStatusTime(new Timestamp());
				subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
                partner.setPartnerStatus(entry.getPartnerStatus());
            }
        }
        
        if (subscriberMdn.getStatusTime() == null) {
        	subscriberMdn.setStatusTime(new Timestamp());
        }

        if (subscriber.getStatusTime() == null) {
        	subscriber.setStatusTime(new Timestamp());
        }
        
        subscriberMdn.setWrongPINCount(5);
        
        subscriber.setType(0);
        
        // subscriber related fields
        if (entry.getFirstName() != null) {
            subscriber.setFirstName(entry.getFirstName());
        }
        if (entry.getLastName() != null) {
            subscriber.setLastName(entry.getLastName());
        }
        if (entry.getLanguage() != null) {
            subscriber.setLanguage(entry.getLanguage());
        }
        else
        {
        	subscriber.setLanguage(systemParametersService.getSubscribersDefaultLanguage());
        }

        subscriber.setmFinoServiceProviderByMSPID(mspDAO.getById(1));
        
        if (entry.getSubscriberType() != null) {
            subscriber.setType(entry.getSubscriberType());
        }
        if (entry.getTimezone() != null) {
            subscriber.setTimezone(entry.getTimezone());
        }
        if (entry.getCurrency() != null) {
            subscriber.setCurrency(entry.getCurrency());
        }
        
        
        if(entry.getPartnerCode() != null){
        	partner.setPartnerCode(entry.getPartnerCode());
        }
        
        //populate address information
        if(entry.getAuthorizedEmail() != null){
        	partner.setAuthorizedEmail(entry.getAuthorizedEmail());
        }
        
        //TODO why should i do a null check?
        if(entry.getAuthorizedFaxNumber() != null){
        	partner.setAuthorizedFaxNumber(entry.getAuthorizedFaxNumber());
        }
        if(entry.getAuthorizedRepresentative() != null){
        	partner.setAuthorizedRepresentative(entry.getAuthorizedRepresentative());
        }
        if(entry.getClassification() != null){
        	partner.setClassification(entry.getClassification());
        }
        if(entry.getFaxNumber() != null){
        	partner.setFaxNumber(entry.getFaxNumber());
        }
        if(entry.getDesignation() != null){
        	partner.setDesignation(entry.getDesignation());
        }
        if(entry.getFranchisePhoneNumber() != null){
        	partner.setFranchisePhoneNumber(entry.getFranchisePhoneNumber());
        }
        if(entry.getIndustryClassification() != null){
        	partner.setIndustryClassification(entry.getIndustryClassification());
        }
        
        partner.setmFinoServiceProviderByMSPID(mspDAO.getById(1));
        
        if(entry.getNumberOfOutlets() != null){
        	partner.setNumberOfOutlets(entry.getNumberOfOutlets());
        }
        
        if(entry.getRepresentativeName() != null){
        	partner.setRepresentativeName(entry.getRepresentativeName());
        }
        if(entry.getTradeName() != null){
        	partner.setTradeName(entry.getTradeName());
        }
        if(entry.getTypeOfOrganization() != null){
        	partner.setTypeOfOrganization(entry.getTypeOfOrganization());
        }
        if(entry.getWebSite() != null){
        	partner.setWebSite(entry.getWebSite());
        }
        if(entry.getYearEstablished() != null){
        	partner.setYearEstablished(entry.getYearEstablished());
        }
        
        //for merchant and outlet addresses
        if(((entry.getMerchantAddressLine1() != null) && !("".equals(entry.getMerchantAddressLine1()))) &&
        	((entry.getMerchantAddressCity() != null) && !("".equals(entry.getMerchantAddressCity()))) &&
        	((entry.getMerchantAddressState() != null) && !("".equals(entry.getMerchantAddressState()))) &&
        	((entry.getMerchantAddressZipcode() != null) && !("".equals(entry.getMerchantAddressZipcode()))) &&
        	((entry.getMerchantAddressCountry() != null) && !("".equals(entry.getMerchantAddressCountry()))))
        {
        	Address merchantAddress = partner.getAddressByMerchantAddressID();
        	
        	if(merchantAddress == null){
        		merchantAddress = new Address();
        	}
        	
        	merchantAddress.setLine1(entry.getMerchantAddressLine1());
        	merchantAddress.setLine2(entry.getMerchantAddressLine2());
        	merchantAddress.setCity(entry.getMerchantAddressCity());
        	merchantAddress.setState(entry.getMerchantAddressState());
        	merchantAddress.setZipCode(entry.getMerchantAddressZipcode());
        	merchantAddress.setCountry(entry.getMerchantAddressCountry());
        	
        	partner.setAddressByMerchantAddressID(merchantAddress);
        	
        }
        
        if(((entry.getOutletAddressLine1() != null) && !("".equals(entry.getOutletAddressLine1()))) &&
            	((entry.getOutletAddressCity() != null) && !("".equals(entry.getOutletAddressCity()))) &&
            	((entry.getOutletAddressState() != null) && !("".equals(entry.getOutletAddressState()))) &&
            	((entry.getOutletAddressZipcode() != null) && !("".equals(entry.getOutletAddressZipcode()))) &&
            	((entry.getOutletAddressCountry() != null) && !("".equals(entry.getOutletAddressCountry()))))
            {
            	Address outletAddress = partner.getAddressByFranchiseOutletAddressID();
            	
            	if(outletAddress == null){
            		outletAddress = new Address();
            	}
            	
            	outletAddress.setLine1(entry.getOutletAddressLine1());
            	outletAddress.setLine2(entry.getOutletAddressLine2());
            	outletAddress.setCity(entry.getOutletAddressCity());
            	outletAddress.setState(entry.getOutletAddressState());
            	outletAddress.setZipCode(entry.getOutletAddressZipcode());
            	outletAddress.setCountry(entry.getOutletAddressCountry());
            	
            	partner.setAddressByFranchiseOutletAddressID(outletAddress);
        }
    }

    private void updateMessage(Partner partner,Subscriber subscriber, SubscriberMDN subscriberMdn,  CMJSPartner.CGEntries entry) {

        entry.setID(partner.getID());
        if(partner != null){
	        if(partner.getID() != null){
	        	entry.setID(partner.getID());
	        }
	        if(partner.getLastUpdateTime() != null){
	        	entry.setLastUpdateTime(partner.getLastUpdateTime());
	        }
	        if(partner.getUpdatedBy() != null){
	        	entry.setUpdatedBy(partner.getUpdatedBy());
	        }
	        if(partner.getCreateTime() != null){
	        	entry.setCreateTime(partner.getCreateTime());
	        }
	        if(partner.getCreatedBy() != null){
	        	entry.setCreatedBy(partner.getCreatedBy());
	        }
	        if(partner.getSubscriber() != null){
	        	entry.setSubscriberID(partner.getSubscriber().getID());
	        }
	        if(partner.getPartnerCode() != null){
	        	entry.setPartnerCode(partner.getPartnerCode());
	        }
	        if(partner.getPartnerStatus() != null){
	        	entry.setPartnerStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_PartnerStatus, subscriber.getLanguage(), partner.getPartnerStatus()));
	        	entry.setPartnerStatus(partner.getPartnerStatus());
	        }
	        if(partner.getTradeName() != null){
	        	entry.setTradeName(partner.getTradeName());
	        }
	        if(partner.getTypeOfOrganization() != null){
	        	entry.setTypeOfOrganization(partner.getTypeOfOrganization());
	        }
	        if(partner.getFaxNumber() != null){
	        	entry.setFaxNumber(partner.getFaxNumber());
	        }
	        if(partner.getWebSite() != null){
	        	entry.setWebSite(partner.getWebSite());
	        }
	        if(partner.getAuthorizedRepresentative() != null){
	        	entry.setAuthorizedRepresentative(partner.getAuthorizedRepresentative());
	        }
	        if(partner.getRepresentativeName() != null){
	        	entry.setRepresentativeName(partner.getRepresentativeName());
	        }
	        if(partner.getDesignation() != null){
	        	entry.setDesignation(partner.getDesignation());
	        }
	        if(partner.getFranchisePhoneNumber() != null){
	        	entry.setFranchisePhoneNumber(partner.getFranchisePhoneNumber());
	        }
	        if(partner.getAddressByFranchiseOutletAddressID() != null){
	        	Address outletAddress = partner.getAddressByFranchiseOutletAddressID();
	        	
	        	if(outletAddress.getLine1() != null){
	        		entry.setOutletAddressLine1(outletAddress.getLine1());
	        	}
	        	if(outletAddress.getLine2() != null){
	        		entry.setOutletAddressLine2(outletAddress.getLine2());
	        	}
	        	if(outletAddress.getCity() != null){
	        		entry.setOutletAddressCity(outletAddress.getCity());
	        	}
	        	if(outletAddress.getCity() != null){
	        		entry.setOutletAddressCity(outletAddress.getCity());
	        	}
	        	if(outletAddress.getState() != null){
	        		entry.setOutletAddressState(outletAddress.getState());
	        	}
	        	if(outletAddress.getCountry() != null){
	        		entry.setOutletAddressCountry(outletAddress.getCountry());
	        	}	        	
	        	if(outletAddress.getZipCode() != null){
	        		entry.setOutletAddressZipcode(outletAddress.getZipCode());
	        	}
	        }
	        if(partner.getAddressByMerchantAddressID() != null){
	        	Address merchantAddress = partner.getAddressByMerchantAddressID();
	        	
	        	if(merchantAddress.getLine1() != null){
	        		entry.setMerchantAddressLine1(merchantAddress.getLine1());
	        	}
	        	if(merchantAddress.getLine2() != null){
	        		entry.setMerchantAddressLine2(merchantAddress.getLine2());
	        	}
	        	if(merchantAddress.getCity() != null){
	        		entry.setMerchantAddressCity(merchantAddress.getCity());
	        	}
	        	if(merchantAddress.getCity() != null){
	        		entry.setMerchantAddressCity(merchantAddress.getCity());
	        	}
	        	if(merchantAddress.getState() != null){
	        		entry.setMerchantAddressState(merchantAddress.getState());
	        	}
	        	if(merchantAddress.getCountry() != null){
	        		entry.setMerchantAddressCountry(merchantAddress.getCountry());
	        	}
	        	if(merchantAddress.getZipCode() != null){
	        		entry.setMerchantAddressZipcode(merchantAddress.getZipCode());
	        	}	        	
	        }
	        if(partner.getClassification() != null){
	        	entry.setClassification(partner.getClassification());
	        }
	        if(partner.getNumberOfOutlets() != null){
	        	entry.setNumberOfOutlets(partner.getNumberOfOutlets());
	        }
	        if(partner.getIndustryClassification() != null){
	        	entry.setIndustryClassification(partner.getIndustryClassification());
	        }
	        if(partner.getYearEstablished() != null){
	        	entry.setYearEstablished(partner.getYearEstablished());
	        }
	        if(partner.getAuthorizedFaxNumber() != null){
	        	entry.setAuthorizedFaxNumber(partner.getAuthorizedFaxNumber());
	        }
	        if(partner.getAuthorizedEmail() != null){
	        	entry.setAuthorizedEmail(partner.getAuthorizedEmail());
	        }
	        if (partner.getVersion() != null) {
	            entry.setRecordVersion(partner.getVersion());
	        }
        }
        if(subscriber != null){
        	if(subscriber.getFirstName() != null){
        		entry.setFirstName(subscriber.getFirstName());
        	}
        	if(subscriber.getLastName() != null){
        		entry.setLastName(subscriber.getLastName());
        	}
	        if(subscriber.getLanguage() != null){
	        	entry.setLanguageText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Language, subscriber.getLanguage(), subscriber.getLanguage()));
	        	entry.setLanguage(subscriber.getLanguage());
	        }
	        if(subscriber.getTimezone() != null){
	        	entry.setTimezone(subscriber.getTimezone());
	        }
	        if(subscriber.getCurrency() != null){
	        	entry.setCurrency(subscriber.getCurrency());	        	
	        }
      
        }
    }
    
	private String generateRandomMdn(){
	    UUID randomMdn = UUID.randomUUID();
	    return randomMdn.toString();
	}
}
