/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.BankAdmin;
import com.mfino.domain.Company;
import com.mfino.domain.Merchant;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.MfinoUser;
import com.mfino.errorcodes.Codes;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSForwardNotificationRequest;
import com.mfino.fix.CmFinoFIX.CMJSPocket;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.AuthorizationService;
import com.mfino.service.DefaultPocketMaintainerService;
import com.mfino.service.EnumTextService;
import com.mfino.service.MailService;
import com.mfino.service.PocketService;
import com.mfino.service.PocketTemplateService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.PocketProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.ValidationUtil;

/**
 * 
 * @author Venkata Krishna Teja D
 */
@Service("PocketProcessorImpl")
public class PocketProcessorImpl extends BaseFixProcessor implements PocketProcessor{

	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
    private SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
    
	@Autowired
	@Qualifier("DefaultPocketMaintainerServiceImpl")
	private DefaultPocketMaintainerService defaultPocketMaintainerService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	
	@Autowired
	@Qualifier("PocketTemplateServiceImpl")
	private PocketTemplateService pocketTemplateService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CmFinoFIX.CMJSPocket realMsg = (CmFinoFIX.CMJSPocket) msg;
        PocketDAO dao = DAOFactory.getInstance().getPocketDAO();

        int response = Codes.FAILURE;

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSPocket.CGEntries[] entries = realMsg.getEntries();

            for (CMJSPocket.CGEntries entry : entries) {
                Pocket pocketObj = dao.getById(entry.getID());
                log.info("Requested Pocket:"+pocketObj.getId()+" details update by user:"+getLoggedUserNameWithIP());
                // Check for Stale Data
                if (!entry.getRecordVersion().equals(pocketObj.getVersion())) {
                    handleStaleDataException();
                }

                Integer oldRestrictions = (pocketObj.getRestrictions()).intValue();
                Integer newRestrictions = entry.getRestrictions();
                
                // Check if the restrictions are edited or not.
                if (newRestrictions != null && !newRestrictions.equals(oldRestrictions)) {
                	pocketObj.setRestrictions(newRestrictions);
                }
                boolean isCompatible = false;
                PocketTemplate oldTemplate = null, newTemplate = null;
                oldTemplate = pocketObj.getPocketTemplateByOldpockettemplateid();

                if (entry.isRemoteModifiedPocketTemplateID()) {
                    boolean isAuthorized = authorizationService.isAuthorized(CmFinoFIX.Permission_Subscriber_Pocket_Template_Edit);
                    if (!isAuthorized) {
                    	log.warn("Authorization failed for pocket template details update by user:"+getLoggedUserNameWithIP());
                        return getErrorMessage(MessageText._("Not Authorized to change the Pocket Template"), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketTemplateID, MessageText._("Not allowed"));
                    }

                    Long newId = entry.getPocketTemplateID();
                    newTemplate = DAOFactory.getInstance().getPocketTemplateDao().getById(newId);
                    isCompatible = pocketTemplateService.areCompatible(oldTemplate, newTemplate);

                    if (!isCompatible) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("The new pocket template is not compatible with the old one."));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        log.warn("Compatible check failed between New Pocket Template:"+newId+" Old Pocket Template:"+oldTemplate.getId()+" by user:"+getLoggedUserNameWithIP());
                        return errorMsg;
                    }else {
                        pocketObj.setPocketTemplateByOldpockettemplateid(oldTemplate);
                        pocketObj.setPockettemplatechangedby(userService.getCurrentUser().getUsername());
                        pocketObj.setPockettemplatechangetime(new Timestamp());
                    }
                }

                boolean isAuthorized = authorizationService.isAuthorized(CmFinoFIX.Permission_Subscriber_Pocket_Status_Edit);
                if (isAuthorized) {
                    if (entry.getPocketStatus() != null && ((pocketObj.getStatus()).equals(CmFinoFIX.PocketStatus_PendingRetirement) 
                    		|| (pocketObj.getStatus()).equals(CmFinoFIX.PocketStatus_Retired))) {
                        if (entry.getPocketStatus() != CmFinoFIX.PocketStatus_Retired && entry.getPocketStatus() != CmFinoFIX.PocketStatus_PendingRetirement) {
                            CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                            errorMsg.setErrorDescription(MessageText._("Can't Change Pocket Status"));
                            log.warn("Failed change pocket status to"+entry.getPocketStatus()+" for Pocket:"+pocketObj.getId()+" by user:"+getLoggedUserNameWithIP());
                            return errorMsg;
                        }
                    }
                } else if(entry.getPocketStatus() != null){
                	log.warn("Not Authorized to change pocket status to"+entry.getPocketStatus()+" for Pocket:"+pocketObj.getId()+" by user:"+getLoggedUserNameWithIP());
                    return getErrorMessage(MessageText._("Not Authorized to change the Pocket Status"), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketStatus, MessageText._("Not allowed"));
                }

                // Currently checking only for isDefault and CardPAN
                boolean canEditOtherFields = authorizationService.isAuthorized(CmFinoFIX.Permission_Subscriber_Pocket_Other_Fields_Edit);
                if (!canEditOtherFields && (entry.getIsDefault() != null || StringUtils.isNotEmpty(entry.getCardPAN()))) {
                	log.warn("Failed edit other fields for Pocket:"+pocketObj.getId()+" by user:"+getLoggedUserNameWithIP());
                    return getErrorMessage(MessageText._("Not Authorized to edit other fields"), CmFinoFIX.ErrorCode_Generic, null, MessageText._("Not allowed"));
                }
                //check for cardpan if update request comes

                if (StringUtils.isNotBlank(entry.getCardPAN())) {
                    CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    try {
                        if (CmFinoFIX.TypeOfCheck_LuhnCheck.equals(pocketObj.getPocketTemplateByPockettemplateid().getTypeofcheck()) && ValidationUtil.ValidateBankAccount(entry.getCardPAN()) == false) {
                            errorMsg.setErrorDescription(MessageText._("Cardpan Luhn check failed."));
                            log.warn("Failed CardPan Luhn check for Pocket:"+pocketObj.getId()+" by user:"+getLoggedUserNameWithIP());
                            return errorMsg;
                        } else if (CmFinoFIX.TypeOfCheck_RegularExpressionCheck.equals(pocketObj.getPocketTemplateByPockettemplateid().getTypeofcheck()) && ValidationUtil.validateRegularExpression(entry.getCardPAN(), pocketObj.getPocketTemplateByOldpockettemplateid().getRegularexpression()) == false) {
                            errorMsg.setErrorDescription(MessageText._("Cardpan Regular Expression check failed."));
                            log.warn("Failed Cardpan Regular Expression check for Pocket:"+pocketObj.getId()+" by user:"+getLoggedUserNameWithIP());
                            return errorMsg;
                        }
                    } catch (PatternSyntaxException pse) {
                    	String message = MessageText._("Invalid Regular Expression, in the PocketTemplate");
                        errorMsg.setErrorDescription(message);
                        log.error(message, pse);
                        return errorMsg;
                    }
                }
                
                // Card Pan Changed.
                if (entry.isRemoteModifiedCardPAN() && entry.getCardPAN() != null && StringUtils.isNotEmpty(entry.getCardPAN())) {
                	if (pocketService.getByCardPan(entry.getCardPAN()) == null) {
                    	log.info("CardPan modified for Pocket:"+pocketObj.getId()+" by user:"+getLoggedUserNameWithIP());
                        pocketService.handleCardPanChange(pocketObj);
                	}
                	else {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        String message = MessageText._("Account Number already exists in the system. It has to be unique.");
                        errorMsg.setErrorDescription(message);
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                        newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                        newEntries[0].setErrorName(CmFinoFIX.CMJSPocket.CGEntries.FieldName_CardPAN);
                        newEntries[0].setErrorDescription(MessageText._("Account Number already exists."));
                        return errorMsg;
                	}
                }                

                if (entry.isRemoteModifiedPocketStatus() && entry.getPocketStatus() != null) {
                    isAuthorized = authorizationService.isAuthorized(CmFinoFIX.Permission_Subscriber_Pocket_Status_Edit);
                    if (isAuthorized) {
                        if ((pocketObj.getPocketTemplateByPockettemplateid().getCommodity()).equals(CmFinoFIX.Commodity_Money) 
                        		&& entry.getPocketStatus().equals(CmFinoFIX.PocketStatus_Retired)) {
                            entry.setPocketStatus(CmFinoFIX.PocketStatus_PendingRetirement);
                            updateEntity(pocketObj, entry);
                        } else {
                            updateEntity(pocketObj, entry);
                        }
                    } else {
                    	log.warn("Not Authorized to change pocket status to"+entry.getPocketStatus()+" for Pocket:"+pocketObj.getId()+" by user:"+getLoggedUserNameWithIP());
                        return getErrorMessage(MessageText._("Not Authorized to change the Pocket Status"), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketStatus, MessageText._("Not allowed"));
                    }
                } else {
                    updateEntity(pocketObj, entry);
                }
                
                response = defaultPocketMaintainerService.setDefaultPocket(pocketObj, false);
                if (response == Codes.SUCCESS) {
                    updateMessage(pocketObj, entry);
                } else {
                    CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    errorMsg.setErrorDescription(MessageText._("Cannot change it from being default"));
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                    newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                    newEntries[0].setErrorName(CmFinoFIX.CMJSPocket.CGEntries.FieldName_IsDefault);
                    newEntries[0].setErrorDescription(MessageText._("Not allowed"));
                    log.warn("Failed setting as default pocket for Pocket:"+pocketObj.getId()+" by user:"+getLoggedUserNameWithIP());
                    return errorMsg;
                }

                try {
                    dao.save(pocketObj);
                } catch (ConstraintViolationException ex) {
                    CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    String message = MessageText._("Account Number already exists in the system. It has to be unique.");
                    errorMsg.setErrorDescription(message);
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                    newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                    newEntries[0].setErrorName(CmFinoFIX.CMJSPocket.CGEntries.FieldName_CardPAN);
                    newEntries[0].setErrorDescription(MessageText._("Account Number already exists."));
                    message = "Unable to save Pocket:"+pocketObj.getId()+" by user:"+getLoggedUserNameWithIP();
                    log.warn(message, ex);
                    return errorMsg;
                }

                if (newRestrictions != null) {
                    CMJSForwardNotificationRequest forwardMsg = new CMJSForwardNotificationRequest();
                    updateAndForwardMessage(forwardMsg, entry, oldRestrictions);
                }
                if (isCompatible && oldTemplate != null && newTemplate != null) {
                    if ((newTemplate.getType()).equals(CmFinoFIX.PocketType_SVA) 
                    		&& (newTemplate.getCommodity()).equals(CmFinoFIX.Commodity_Money)) {
                        Integer oldAllowance = (oldTemplate.getAllowance()).intValue();
                        Integer newAllowance = (newTemplate.getAllowance()).intValue();
                        CMJSForwardNotificationRequest forwardMsg = new CMJSForwardNotificationRequest();
                        updateTemplateAndForwardMessage(forwardMsg, entry, oldAllowance, newAllowance);
                    }
                }
                log.info("Completed Pocket:"+pocketObj.getId()+" details update by user:"+getLoggedUserNameWithIP());
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            PocketQuery query = new PocketQuery();
            
            query.setLimit(realMsg.getlimit());
            query.setStart(realMsg.getstart());
            
            if(realMsg.getMDNIDSearch()!=null){
	            query.setMdnIDSearch(realMsg.getMDNIDSearch());
            }
            
            if (StringUtils.isNotBlank(realMsg.getStatusSearch())) {
            	query.setStatusSearchString(realMsg.getStatusSearch());
            }
            
			/*
			 * ##
			 * Code introduced to support pocket tab in partner ui(service partner, integration partner etc) 
			 */ 
			if(realMsg.getSubscriberIDSearch()!=null){
				log.info("PocketProcessor :: select action SubscriberIDSearch:"+realMsg.getSubscriberIDSearch()+" by user:"+getLoggedUserNameWithIP());
				 SubscriberDAO subdao = DAOFactory.getInstance().getSubscriberDAO();
				 Subscriber sub = subdao.getById(realMsg.getSubscriberIDSearch());
				 log.info("PocketProcessor :: select action sub="+sub);
				 if(sub != null){
					 Set<SubscriberMdn> subscriberMdnCol = sub.getSubscriberMdns();
					 log.info("PocketProcessor :: select action servicePartnerCol="+subscriberMdnCol);
					 if((subscriberMdnCol != null) && (subscriberMdnCol.size() > 0)){
						 SubscriberMdn mdn = subscriberMdnCol.iterator().next(); 
						 Long id = mdn.getId().longValue();
						 log.info("PocketProcessor :: select action id(MdnIDSearch):"+id+" Mdn:"+mdn.getMdn()+" by user:"+getLoggedUserNameWithIP());
						 query.setMdnIDSearch(id);
					 }
				 }
			 }
			
			if (StringUtils.isNotBlank(realMsg.getMDNSearch())) {
				log.info("PocketProcessor :: select action MDNSearch:"+realMsg.getMDNSearch()+" by user:"+getLoggedUserNameWithIP());
				SubscriberMDNDAO mdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
				SubscriberMdn subscriberMDN = mdnDao.getByMDN(realMsg.getMDNSearch());
				if (subscriberMDN != null) {
					log.info("PocketProcessor :: MDN ID for the MDNSearch: " + realMsg.getMDNSearch() + " is: " + subscriberMDN.getId());
					query.setMdnIDSearch(subscriberMDN.getId().longValue());
				}
			}
			
			if (StringUtils.isNotBlank(realMsg.getPartnerIDSearch())) {
				log.info("PocketProcessor :: select action PartnerID:" + realMsg.getPartnerIDSearch()+" by user:"+getLoggedUserNameWithIP());
				PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
				Partner partner = partnerDAO.getById(new Long(realMsg.getPartnerIDSearch()));
				if (partner != null) {
					Subscriber subscriber = partner.getSubscriber();
					if (subscriber != null) {
						 Set<SubscriberMdn> subscriberMdnCol = subscriber.getSubscriberMdns();
						 if((subscriberMdnCol != null) && (subscriberMdnCol.size() > 0)){
							 SubscriberMdn mdn = subscriberMdnCol.iterator().next(); 
							 Long id = mdn.getId().longValue();
							 log.info("PocketProcessor :: select action id(MdnIDSearch):"+id+" Mdn:"+mdn.getMdn()+" by user:"+getLoggedUserNameWithIP());
							 query.setMdnIDSearch(id);
						 }
					}
				}
			}
			
            if (realMsg.getMerchantIDSearch() != null) {
				log.info("PocketProcessor :: select action MerchantID:" + realMsg.getMerchantIDSearch()+" by user:"+getLoggedUserNameWithIP());
                MerchantDAO mDao = DAOFactory.getInstance().getMerchantDAO();
                Merchant m = mDao.getById(realMsg.getMerchantIDSearch());
                SubscriberMdn mdn = (SubscriberMdn) m.getSubscriber().getSubscriberMdns().toArray()[0];
                Long id = mdn.getId().longValue();
				 log.info("PocketProcessor :: select action id(MdnIDSearch):"+id+" Mdn:"+mdn.getMdn()+" by user:"+getLoggedUserNameWithIP());
                query.setMdnIDSearch(mdn.getId().longValue());
            }
            if (realMsg.getCommodity() != null) {
                query.setCommodity(realMsg.getCommodity());
				 log.info("PocketProcessor :: Query on commodity:"+realMsg.getCommodity()+" by user:"+getLoggedUserNameWithIP());
            }
            if (realMsg.getPocketType() != null) {
            	log.info("PocketProcessor :: Query on PocketType:"+realMsg.getPocketType()+" by user:"+getLoggedUserNameWithIP());
                query.setPocketType(realMsg.getPocketType());
            }
            if (userService.getUserCompany() != null) {
            	log.info("PocketProcessor :: Query on company:"+userService.getUserCompany().getCompanyname()+" by user:"+getLoggedUserNameWithIP());
                query.setCompany(userService.getUserCompany());
            }
            if(realMsg.getNoCompanyFilter()!=null && realMsg.getNoCompanyFilter()){
            	log.info("PocketProcessor :: Query on company set to null by user:"+getLoggedUserNameWithIP());
            	query.setCompany(null);
            }
            if (realMsg.getIsCollectorPocket() != null) {
            	log.info("PocketProcessor :: Query on collectorPocket:"+realMsg.getIsCollectorPocket()+" by user:"+getLoggedUserNameWithIP());
            	query.setIsCollectorPocket(realMsg.getIsCollectorPocket());
            }
            if(realMsg.getIsCollectorPocketAllowed()!=null){
            	log.info("PocketProcessor :: Query on isCollectorPocketAllowed:"+realMsg.getIsCollectorPocketAllowed()+" by user:"+getLoggedUserNameWithIP());
            	query.setIsCollectorPocketAllowed(realMsg.getIsCollectorPocketAllowed());
            }
            if(realMsg.getIsSuspencePocketAllowed()!=null){
            	log.info("PocketProcessor :: Query on isSuspencePocketAllowed:"+realMsg.getIsSuspencePocketAllowed()+" by user:"+getLoggedUserNameWithIP());
            	query.setIsSuspencePocketAllowed(realMsg.getIsSuspencePocketAllowed());
            }
            if (authorizationService.isAuthorized(CmFinoFIX.Permission_Transaction_OnlyBank_View)) {
                MfinoUser user = userService.getCurrentUser();
                Set<BankAdmin> admins = user.getBankAdmins();

                if (admins != null && admins.size() > 0) {
                    BankAdmin admin = (BankAdmin) admins.toArray()[0];
                    if (admin != null && admin.getBank() != null) {
                    	log.info("PocketProcessor :: Query on bank code:"+admin.getBank().getBankcode()+" by user:"+getLoggedUserNameWithIP());
                        query.setBankCode(admin.getBank().getBankcode().intValue());
                        // setting company as null for bank roles..
                        query.setCompany(null);
                    }
                }
            }
            List<Pocket> results = dao.get(query);
            log.info("PocketProcessor :: select action results "+results+" by user:"+getLoggedUserNameWithIP());
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                Pocket p = results.get(i);
                pocketService.changeStatusBasedOnMerchantAndSubscriber(p);
                log.info("PocketProcessor :: pocket:"+p.getId()+" status changed to:"+p.getStatus()+" by user:"+getLoggedUserNameWithIP());
                CMJSPocket.CGEntries entry = new CMJSPocket.CGEntries();

                updateMessage(p, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSPocket.CGEntries[] entries = realMsg.getEntries();

            for (CMJSPocket.CGEntries e : entries) {
                Pocket pocket = new Pocket();
                Integer type = pocketTemplateService.getPocketType(e.getPocketTemplateID());

                // NOTE: This shouldn't happen
                if (type == null) {
                    log.error("Unknown pocket type encountered. Pocket template ID = " + e.getPocketTemplateID()+" by user:"+getLoggedUserNameWithIP());
                    return getErrorMessage(MessageText._("Not authorized to add this type of pocket"), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketTypeText, MessageText._("Not allowed"));
                }
                
                SubscriberMdn subMdn = subscriberMDNDAO.getById(e.getMDNID());
                PocketTemplateDAO pocketTemplateDAO = DAOFactory.getInstance().getPocketTemplateDao();
                PocketTemplate pocketTemplate = pocketTemplateDAO.getById(e.getPocketTemplateID());
                boolean isallowed=pocketService.isAllowed(pocketTemplate,subMdn);
                if(!isallowed){
                	log.error("PocketProcessor :: Pocket addition for template:"+e.getPocketTemplateID()+" for MDN:"+subMdn.getMdn()+" by user:"+getLoggedUserNameWithIP());
                	 return getErrorMessage(MessageText._(" Pocket addition with this template  not allowed for this subscriber"), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketTypeText, MessageText._("Not allowed"));
                   		
                }
                
                boolean flag = false;

                if (StringUtils.isNotBlank(e.getCardPAN())) {
                    CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    try {
                        if (CmFinoFIX.TypeOfCheck_LuhnCheck.equals(pocketTemplate.getTypeofcheck()) && ValidationUtil.ValidateBankAccount(e.getCardPAN()) == false) {
                        	log.error("PocketProcessor :: CardPan Luhn check failed for MDN:"+subMdn.getMdn()+" by user:"+getLoggedUserNameWithIP());
                            errorMsg.setErrorDescription(MessageText._("Cardpan Luhn check failed."));
                            return errorMsg;
                        } else if (CmFinoFIX.TypeOfCheck_RegularExpressionCheck.equals(pocketTemplate.getTypeofcheck()) && ValidationUtil.validateRegularExpression(e.getCardPAN(), pocketTemplate.getRegularexpression()) == false) {
                        	log.error("PocketProcessor :: CardPan regular expression check failed for MDN:"+subMdn.getMdn()+" by user:"+getLoggedUserNameWithIP());
                            errorMsg.setErrorDescription(MessageText._("Cardpan Regular Expression check failed."));
                            return errorMsg;
                        }
                    } catch (PatternSyntaxException pse) {
                    	log.error("PocketProcessor :: Invalid Regular Expression in pocket template for MDN:"+subMdn.getMdn()+" by user:"+getLoggedUserNameWithIP());
                    	String message = MessageText._("Invalid Regular Expression, in the PocketTemplate");
                        errorMsg.setErrorDescription(message);
                        log.warn(message, pse);
                        return errorMsg;
                    }
                    
                    if (pocketService.getByCardPan(e.getCardPAN()) != null) {
                    	log.error("PocketProcessor :: Account Number already exists in the system for MDN:"+subMdn.getMdn()+" by user:"+getLoggedUserNameWithIP());
                        errorMsg = new CmFinoFIX.CMJSError();
                        String message = MessageText._("Account Number already exists in the system. It has to be unique.");
                        errorMsg.setErrorDescription(message);
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                        newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                        newEntries[0].setErrorName(CmFinoFIX.CMJSPocket.CGEntries.FieldName_CardPAN);
                        newEntries[0].setErrorDescription(MessageText._("Account Number already exists."));
                        return errorMsg;
                    }
                } else if (CmFinoFIX.PocketType_SVA.equals(pocketTemplate.getType()) && CmFinoFIX.Commodity_Money.equals(pocketTemplate.getCommodity()) &&
                        ConfigurationUtil.getSMARTEMoneyPartnerCode().equals(pocketTemplate.getBankcode())) {
                	if(e.getSubsMDN()!=null){
                    e.setCardPAN(pocketService.generateSVAEMoney16DigitCardPAN(e.getSubsMDN()));
                	}else if(e.getMDNID()!=null){
                		SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
                		SubscriberMdn subscriberMdn = subscriberMdnDao.getById(e.getMDNID());
                		 e.setCardPAN(pocketService.generateSVAEMoney16DigitCardPAN(subscriberMdn.getMdn()));
                	  	}
                    log.debug("System generated Card PAN is " + e.getCardPAN());
                    log.info("PocketProcessor :: Generated card pan for pocket with template:"+e.getPocketTemplateID()+" for MDN:"+subMdn.getMdn()+" by user:"+getLoggedUserNameWithIP());
                    flag = true;
                }
                isallowed=pocketService.checkCount(pocketTemplate,subMdn);
                if(!isallowed){
                	log.error("PocketProcessor :: Pocket count limit reached for template:"+e.getPocketTemplateID()+" for MDN:"+subMdn.getMdn()+" by user:"+getLoggedUserNameWithIP());	
               	 return getErrorMessage(MessageText._(" Pocket count Limit reached for this template  "), CmFinoFIX.ErrorCode_Generic, CmFinoFIX.CMJSPocket.CGEntries.FieldName_PocketTypeText, MessageText._("Pocket count Limit reached for this template"));
                 		
               } 
                updateEntity(pocket, e);

                defaultPocketMaintainerService.setDefaultPocket(pocket, true);

                if (userService.getUserCompany() != null) {
                    pocket.setCompany(userService.getUserCompany());
                }
                try {
                    dao.save(pocket);
                } catch (ConstraintViolationException ex) {
                    CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    String message = MessageText._("Account Number already exists in the system. It has to be unique.");
                    errorMsg.setErrorDescription(message);
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                    newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                    newEntries[0].setErrorName(CmFinoFIX.CMJSPocket.CGEntries.FieldName_CardPAN);
                    newEntries[0].setErrorDescription(MessageText._("Account Number already exists."));
                    log.info("Trying to create a new account with an existing accout no.Returning error");
                    log.error("PocketProcessor :: Pocket addition failed as account number already exists:"+e.getPocketTemplateID()+" for MDN:"+subMdn.getMdn()+" by user:"+getLoggedUserNameWithIP());
                    return errorMsg;
                }

                updateMessage(pocket, e);
                //This falg is for Sending Account Number. if flag is true then we will send sms and email to Subscriber's MDN
                if (flag) {
                    if (pocket != null && pocket.getSubscriberMdn() != null && pocket.getSubscriberMdn().getSubscriber() != null) {
                        Subscriber sub = pocket.getSubscriberMdn().getSubscriber();

                        String fulleName = sub.getFirstname() + " " + sub.getLastname();
                        // send mail
                        String emailMsg =
                                String.format(
                                "Dear %s ,\n\tYour MDN is %s \n\tYour CardPAN is %s" + ".\n" + ConfigurationUtil.getAdditionalMsg() + "\n" + ConfigurationUtil.getEmailSignature(),
                                fulleName, e.getSubsMDN(), e.getCardPAN());
                        String emailSubject = ConfigurationUtil.getUserInsertSubject();
                        String email=sub.getEmail();

                        try {
							if(subscriberServiceExtended.isSubscriberEmailVerified(sub)) {
                            mailService.asyncSendEmail(email, fulleName, emailSubject, emailMsg);
							log.info("Email sent to " + sub.getEmail());
                        	} else {
                        		log.info("Email not sent to " + sub.getEmail() + " as it not verified");
                        	}    
                        } catch (Exception ee) {
                            log.error("Failed to send CardPAN information.", ee);
                            realMsg.setsuccess(CmFinoFIX.Boolean_False);
                            realMsg.seterrorCodes("Couldn't send email to " + sub.getEmail());
                            return realMsg;
                        }                        
                    }
                    String message = "Your SVA Money CardPAN is " + e.getCardPAN();
                    try {
                    	String mdn=e.getSubsMDN();
                        smsService.setDestinationMDN(mdn);

                        smsService.setMessage(message);
                        smsService.asyncSendSMS();
                    } catch (Exception ex) {    // ClientProtocolException
                        log.error("Error " + ex.getMessage(), ex);
                    }
                }
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
        }

        return realMsg;
    }

    public void updateTemplateAndForwardMessage(CMJSForwardNotificationRequest newMsg, CMJSPocket.CGEntries e, Integer oldAlloance, Integer newAllowance) {

        newMsg.setSourceMDN(e.getSubsMDN());
        newMsg.setDestMDN(e.getSubsMDN());
        newMsg.setFormatOnly(Boolean.FALSE);
        SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
        Long mdnId = e.getMDNID();
        if (null != mdnId) {
            SubscriberMdn mdn = subscriberMDNDAO.getById(mdnId);
            newMsg.setMSPID(mdn.getSubscriber().getMfinoServiceProvider().getId().longValue());
        }

        ForwardNotificationRequestProcessorImpl notifySubscriber = new ForwardNotificationRequestProcessorImpl();

        boolean isOldRegistered = ((oldAlloance & CmFinoFIX.PocketAllowance_Registered) == CmFinoFIX.PocketAllowance_Registered);
        boolean isNewRegistered = ((newAllowance & CmFinoFIX.PocketAllowance_Registered) == CmFinoFIX.PocketAllowance_Registered);

        if (isNewRegistered && !isOldRegistered) {
            newMsg.setCode(CmFinoFIX.NotificationCode_UpgradeEMoneyPocket);
            notifySubscriber.process(newMsg);
        } else if (isOldRegistered && !isNewRegistered) {
            newMsg.setCode(CmFinoFIX.NotificationCode_DowngradeEMoneyPocket);
            notifySubscriber.process(newMsg);
        }
    }

    public void updateAndForwardMessage(CMJSForwardNotificationRequest newMsg, CMJSPocket.CGEntries e, Integer oldRestristions) {

        newMsg.setDestMDN(e.getSubsMDN());
        newMsg.setFormatOnly(Boolean.FALSE);
        SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
        Long mdnId = e.getMDNID();
        if (null != mdnId) {
            SubscriberMdn mdn = subscriberMDNDAO.getById(mdnId);
            newMsg.setMSPID(mdn.getSubscriber().getMfinoServiceProvider().getId().longValue());
        }

        newMsg.setSourceMDN(e.getSubsMDN());
        // This is done with purpose, Please talk to Sunil or Moshe before making
        // any changes.
        // newMsg.setServletPath();
        ForwardNotificationRequestProcessorImpl notifySubscriber = new ForwardNotificationRequestProcessorImpl();
        Integer newRestrictions = e.getRestrictions();
        Boolean isNewAbsolutLocked = ((newRestrictions & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) == CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
        Boolean isNewRestrictionsNone = (newRestrictions == CmFinoFIX.SubscriberRestrictions_None);
        Boolean isNewSecurityLocked = ((newRestrictions & CmFinoFIX.SubscriberRestrictions_SecurityLocked) == CmFinoFIX.SubscriberRestrictions_SecurityLocked);
        Boolean isNewSelfSuspended = ((newRestrictions & CmFinoFIX.SubscriberRestrictions_SelfSuspended) == CmFinoFIX.SubscriberRestrictions_SelfSuspended);
        Boolean isNewSuspended = ((newRestrictions & CmFinoFIX.SubscriberRestrictions_Suspended) == CmFinoFIX.SubscriberRestrictions_Suspended);

        Boolean isOldAbsolutLocked = ((oldRestristions & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) == CmFinoFIX.SubscriberRestrictions_AbsoluteLocked);
        Boolean isOldRestrictionsNone = (oldRestristions == CmFinoFIX.SubscriberRestrictions_None);
        Boolean isOldSecurityLocked = ((oldRestristions & CmFinoFIX.SubscriberRestrictions_SecurityLocked) == CmFinoFIX.SubscriberRestrictions_SecurityLocked);
        Boolean isOldSelfSuspended = ((oldRestristions & CmFinoFIX.SubscriberRestrictions_SelfSuspended) == CmFinoFIX.SubscriberRestrictions_SelfSuspended);
        Boolean isOldSuspended = ((oldRestristions & CmFinoFIX.SubscriberRestrictions_Suspended) == CmFinoFIX.SubscriberRestrictions_Suspended);

        Boolean isAbsolutLockedChanged = (isOldAbsolutLocked ^ isNewAbsolutLocked);
        Boolean isRestrictionsNoneChanged = (isOldRestrictionsNone ^ isNewRestrictionsNone);
        Boolean isSecurityLockedChanged = (isNewSecurityLocked ^ isOldSecurityLocked);
        Boolean isSelfSuspendedChanged = (isOldSelfSuspended ^ isNewSelfSuspended);
        Boolean isSuspendedChanged = (isOldSuspended ^ isNewSuspended);

        PocketTemplateDAO pocketTemplateDAO = DAOFactory.getInstance().getPocketTemplateDao();
        Long templateID = e.getPocketTemplateID();
        PocketTemplate pt = pocketTemplateDAO.getById(templateID);
        Integer templateType = (pt.getType()).intValue();
        Integer commodity = (pt.getCommodity()).intValue();

        if ((isAbsolutLockedChanged && isNewAbsolutLocked) || (isSecurityLockedChanged && isNewSecurityLocked) || (isSelfSuspendedChanged && isNewSelfSuspended) || (isSuspendedChanged && isNewSuspended)) {
            newMsg.setCode(getNotificationMessageCode(templateType, commodity, true));
            notifySubscriber.process(newMsg);
        } else if (isRestrictionsNoneChanged && isNewRestrictionsNone) {
            newMsg.setCode(getNotificationMessageCode(templateType, commodity, false));
            notifySubscriber.process(newMsg);
        }
    }

    private Integer getNotificationMessageCode(Integer pocketType, Integer commodity, Boolean isActive) {
        if (CmFinoFIX.PocketType_BOBAccount.equals(pocketType)) {
            if (isActive) {
                return CmFinoFIX.NotificationCode_CBOSS_Restriction_Activate;
            } else {
                return CmFinoFIX.NotificationCode_CBOSSRestriction_Release;
            }

        } else if (CmFinoFIX.PocketType_BankAccount.equals(pocketType)) {
            if (isActive) {
                return (CmFinoFIX.NotificationCode_DompetRestriction_Activate);
            } else {
                return (CmFinoFIX.NotificationCode_DompetRestriction_Release);
            }

        } else if (CmFinoFIX.PocketType_SVA.equals(pocketType)) {
            if (CmFinoFIX.Commodity_Money.equals(commodity)) {
                if (isActive) {
                    return (CmFinoFIX.NotificationCode_EMoneyRestriction_Activate);
                } else {
                    return (CmFinoFIX.NotificationCode_EMoneyRestriction_Release);
                }
            } else {
                if (isActive) {
                    return (CmFinoFIX.NotificationCode_MerchantRestriction_Activate);
                } else {
                    return (CmFinoFIX.NotificationCode_MerchantRestriction_Release);
                }
            }
        } else {
            log.error(String.format("Received bad pockettype (%s)", pocketType));
            return null;
        }
    }

    private void updateEntity(Pocket thePocket, CmFinoFIX.CMJSPocket.CGEntries theEntries) {
        PocketTemplateDAO pocketTemplateDAO = DAOFactory.getInstance().getPocketTemplateDao();
       
        String pocketDetail = "";
        if(thePocket.getId()!=null){
        	pocketDetail = String.valueOf(thePocket.getId());
        }
        else
        {
        	pocketDetail = " for MDN ID:"+theEntries.getMDNID();
        }
        Long pocketIssuerId = theEntries.getPocketTemplateID();
        if (null != pocketIssuerId) {
            PocketTemplate pt = pocketTemplateDAO.getById(pocketIssuerId);
            if(pt==null){
            	log.info("Pocket:"+pocketDetail+" PocketTemplate is set to null, by user:"+getLoggedUserNameWithIP());
            }
            else if(pt!=thePocket.getPocketTemplateByPockettemplateid()){
            	log.info("Pocket:"+pocketDetail+" PocketTemplate updated to Desc:"+pt.getDescription()+" ID:"+pt.getId()+" by user:"+getLoggedUserNameWithIP());
            }
            thePocket.setPocketTemplateByPockettemplateid(pt);
        }

        Long mdnId = theEntries.getMDNID();
        if (null != mdnId) {
            SubscriberMdn mdn = subscriberMDNDAO.getById(mdnId);
            thePocket.setSubscriberMdn(mdn);
            if(mdn != null && mdn.getSubscriber() != null){
            	Company subCompany = mdn.getSubscriber().getCompany();
            	
          	  //smart#805 setting pocket company to subscriber company
            	if(subCompany==null){
            		log.info("Pocket:"+pocketDetail+" Company is set to null, by user:"+getLoggedUserNameWithIP());
            	}
            	else if(thePocket.getCompany()!=subCompany){
            		log.info("Pocket:"+pocketDetail+" Company updated to "+subCompany.getId()+" by user:"+getLoggedUserNameWithIP());
            	}
            thePocket.setCompany(subCompany);
            }
        }

        String cardPan = theEntries.getCardPAN();
        if (null != cardPan) {
        	boolean isSame = cardPan.equals(thePocket.getCardpan()); 
        	
            if (!(cardPan.isEmpty())) {
            	if(!isSame){
            		log.info("Pocket:"+pocketDetail+" CardPan is updated, by user:"+getLoggedUserNameWithIP());
            	}
                thePocket.setCardpan(cardPan);
            } else {
            	if(!isSame){
            		log.info("Pocket:"+pocketDetail+" CardPan is set to null, by user:"+getLoggedUserNameWithIP());
            	}
                thePocket.setCardpan(null);
            }
        }
        
        String cardAlias = theEntries.getCardAlias();
        if (null != cardAlias) {
        	boolean isSame = cardAlias.equals(thePocket.getCardpan()); 
        	
            if (!(cardAlias.isEmpty())) {
            	if(!isSame){
            		log.info("Pocket:"+pocketDetail+" CardAlias is updated, by user:"+getLoggedUserNameWithIP());
            	}
                thePocket.setCardalias(cardAlias);
            } else {
            	if(!isSame){
            		log.info("Pocket:"+pocketDetail+" CardAlias is set to null, by user:"+getLoggedUserNameWithIP());
            	}
                thePocket.setCardalias(null);
            }
        }

        Integer pocketRestrictions = theEntries.getRestrictions();
        if (null != pocketRestrictions) {
        	// *FindbugsChange*
        	// Previous -- if(thePocket.getRestrictions()!=pocketRestrictions){
        	if (thePocket.getRestrictions()!= null && !((thePocket.getRestrictions()).equals(pocketRestrictions))){
        		log.info("Pocket:"+pocketDetail+" restrictions updated to:"+pocketRestrictions+" by user:"+getLoggedUserNameWithIP());
        	}
            thePocket.setRestrictions(pocketRestrictions);
        }

        Boolean isDefault = theEntries.getIsDefault();
        if (null != isDefault) {
        	if((thePocket.getIsdefault() != null && thePocket.getIsdefault())!= isDefault){
        		log.info("Pocket:"+pocketDetail+" isDefault is updated to:"+isDefault+" by user:"+getLoggedUserNameWithIP());
        	}
            thePocket.setIsdefault(CmFinoFIX.Boolean_True);
        }

        Integer pocketStatus = theEntries.getPocketStatus();
        if (pocketStatus == null) {
            if (thePocket.getStatus() == null) {
            	log.info("Pocket:"+pocketDetail+" pocket status updated to:"+CmFinoFIX.PocketStatus_Initialized+" by user:"+getLoggedUserNameWithIP());
                thePocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
            }
        } else {
        	// *FindbugsChange*
        	// Previous -- if(pocketStatus!=thePocket.getStatus()){
        	if (pocketStatus!= null && !(pocketStatus.equals(thePocket.getStatus()))) {
        		log.info("Pocket:"+pocketDetail+" pocket status updated to:"+pocketStatus+" by user:"+getLoggedUserNameWithIP());
        	}
            thePocket.setStatus(pocketStatus);
        }

        if (thePocket.getStatustime() == null) {
            thePocket.setStatustime(new Timestamp());
            log.info("Pocket:"+pocketDetail+" StatusTime is updated to:"+thePocket.getStatustime().toString()+" by user:"+getLoggedUserNameWithIP());
        }

        if (null != theEntries.getCurrentBalance()) {
        	if(theEntries.getCurrentBalance().compareTo(thePocket.getCurrentbalance()) != 0){
        		log.info("Pocket:"+pocketDetail+" current balance is updated by user:"+getLoggedUserNameWithIP());
        	}
            thePocket.setCurrentbalance(theEntries.getCurrentBalance());
        }

        if (null != theEntries.getOldPocketTemplateID()) {
            PocketTemplate pt = pocketTemplateDAO.getById(theEntries.getOldPocketTemplateID());
            if(pt!=thePocket.getPocketTemplateByOldpockettemplateid()){
            	log.info("Pocket:"+pocketDetail+" old pocket template is updated to:"+pt.getId()+" by user:"+getLoggedUserNameWithIP());
            }
            thePocket.setPocketTemplateByOldpockettemplateid(pt);
        }

        if (null != theEntries.getPocketTemplateChangedBy()) {
        	if(!theEntries.getPocketTemplateChangedBy().equals(thePocket.getPockettemplatechangedby())){
            	log.info("Pocket:"+pocketDetail+" pocket template changedby is updated to:"+theEntries.getPocketTemplateChangedBy()+" by user:"+getLoggedUserNameWithIP());
            }
        	thePocket.setPockettemplatechangedby(theEntries.getPocketTemplateChangedBy());
        }

        if (null != theEntries.getPocketTemplateChangeTime()) {
        	if(theEntries.getPocketTemplateChangeTime()!=thePocket.getPockettemplatechangetime()){
            	log.info("Pocket:"+pocketDetail+" pocket template change time is updated to:"+theEntries.getPocketTemplateChangeTime().toString()+" by user:"+getLoggedUserNameWithIP());
            }
            thePocket.setPockettemplatechangetime(theEntries.getPocketTemplateChangeTime());
        }

    }

    private void updateMessage(Pocket thePocket, CmFinoFIX.CMJSPocket.CGEntries theEntries) {

        theEntries.setID(thePocket.getId().longValue());
        // Here get the values from Pocket and set them in the entry.
        if (null != thePocket.getPocketTemplateByPockettemplateid()) {
            theEntries.setPocketTemplateID(thePocket.getPocketTemplateByPockettemplateid().getId().longValue());
            theEntries.setPocketTemplDescription(thePocket.getPocketTemplateByPockettemplateid().getDescription());
            if (thePocket.getPocketTemplateByPockettemplateid().getDenomination() != null) {
                theEntries.setDenomination(thePocket.getPocketTemplateByPockettemplateid().getDenomination().longValue());
            } else {
                theEntries.setDenomination(1L);
            }
	    theEntries.setPocketType(thePocket.getPocketTemplateByPockettemplateid().getType());
            theEntries.setPocketTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketType, null, thePocket.getPocketTemplateByPockettemplateid().getType()));
            theEntries.setCommodityText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, thePocket.getPocketTemplateByPockettemplateid().getCommodity()));
            theEntries.setPartnerCode(thePocket.getPocketTemplateByPockettemplateid().getBankcode().intValue());
        }

        if (null != thePocket.getPocketTemplateByOldpockettemplateid()) {
            theEntries.setOldPocketTemplDescription(thePocket.getPocketTemplateByOldpockettemplateid().getDescription());
        }

        if (null != thePocket.getPockettemplatechangedby()) {
            theEntries.setPocketTemplateChangedBy(thePocket.getPockettemplatechangedby());
        }

        if (null != thePocket.getPockettemplatechangetime()) {
            theEntries.setPocketTemplateChangeTime(thePocket.getPockettemplatechangetime());
        }

        if (null != thePocket.getSubscriberMdn()) {
            theEntries.setMDNID(thePocket.getSubscriberMdn().getId().longValue());
            theEntries.setSubsMDN(thePocket.getSubscriberMdn().getMdn());
        }


        Timestamp lastTransactionTime = thePocket.getLasttransactiontime();
        if (null != lastTransactionTime) {
            theEntries.setLastTransactionTime(lastTransactionTime);
        }

       /* BigDecimal currentBalance = new BigDecimal(thePocket.getCurrentbalance());
        if (null != currentBalance) {
            theEntries.setCurrentBalance(currentBalance);
        }
*/
        BigDecimal currentDailyExpenditure = thePocket.getCurrentdailyexpenditure();
        if (null != currentDailyExpenditure) {
            theEntries.setCurrentDailyExpenditure(currentDailyExpenditure);
        }

        BigDecimal currentWeeklyExpenditure = thePocket.getCurrentweeklyexpenditure();
        if (null != currentWeeklyExpenditure) {
            theEntries.setCurrentWeeklyExpenditure(currentWeeklyExpenditure);
        }

        BigDecimal currentMontlyExpenditure = thePocket.getCurrentmonthlyexpenditure();
        if (null != currentMontlyExpenditure) {
            theEntries.setCurrentMonthlyExpenditure(currentMontlyExpenditure);
        }

        Integer currentDailyTransactionCount = (thePocket.getCurrentdailytxnscount()).intValue();
        theEntries.setCurrentDailyTxnsCount(currentDailyTransactionCount);

        Integer currentWeeklyTransactionCount = (thePocket.getCurrentweeklytxnscount()).intValue();
        theEntries.setCurrentWeeklyTxnsCount(currentWeeklyTransactionCount);

        Integer currentMonthlyTransactionCount = (thePocket.getCurrentmonthlytxnscount()).intValue();
        theEntries.setCurrentMonthlyTxnsCount(currentMonthlyTransactionCount);
        if (null != thePocket.getLastbankresponsecode()) { 
        Integer lastBankResponseCode = thePocket.getLastbankresponsecode().intValue();
        theEntries.setLastBankResponseCode(lastBankResponseCode);
        }
        
        
        String lastBankAuthCode = thePocket.getLastbankauthorizationcode();
        if (null != lastBankAuthCode) {
            theEntries.setLastBankAuthorizationCode(lastBankAuthCode);
        }

        if(null !=thePocket.getLastbankrequestcode())
        {
        Integer lastBankRequestCode = thePocket.getLastbankrequestcode().intValue();
        theEntries.setLastBankRequestCode(lastBankRequestCode);
        }
        String cardPan = thePocket.getCardpan();
        if (null != cardPan) {
            theEntries.setCardPAN(cardPan);
        }
        if (null != thePocket.getCardalias()) {
            theEntries.setCardAlias(thePocket.getCardalias());
        }

        Integer pocketRestrictions = (thePocket.getRestrictions()).intValue();
        theEntries.setRestrictions(pocketRestrictions);

        theEntries.setPocketRestrictionsText(enumTextService.getRestrictionsText(CmFinoFIX.TagID_PocketRestrictions, null, pocketRestrictions.toString()));

        Boolean isDefault = (thePocket.getIsdefault() != null && thePocket.getIsdefault());
        if (isDefault != null) {
            theEntries.setIsDefault(isDefault);
        }

        Integer pocketStatus = (thePocket.getStatus()).intValue();
        if (pocketStatus == null) {
            pocketStatus = CmFinoFIX.PocketStatus_Initialized;
        }

        theEntries.setPocketStatus(pocketStatus);
        theEntries.setPocketStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, null, pocketStatus));

        theEntries.setStatusTime(thePocket.getStatustime());
        theEntries.setCreateTime(thePocket.getCreatetime());
        theEntries.setActivationTime(thePocket.getActivationtime());
        theEntries.setLastUpdateTime(thePocket.getLastupdatetime());

        String updatedBy = thePocket.getUpdatedby();
        if (null != updatedBy) {
            theEntries.setUpdatedBy(updatedBy);
        }

        if (null !=thePocket.getVersion()) {
            theEntries.setRecordVersion(thePocket.getVersion());
        }
        
        if (StringUtils.isNotBlank(thePocket.getCardpan()) && thePocket.getPocketTemplateByPockettemplateid() != null) {
        	String cPan = thePocket.getCardpan();
        	if (cPan.length() > 6) {
        		cPan = cPan.substring(cPan.length()-6);
        	} 
        	theEntries.setPocketDispText(thePocket.getPocketTemplateByPockettemplateid().getDescription() + " - " + cPan);
        } else if (thePocket.getPocketTemplateByPockettemplateid() != null) {
        	theEntries.setPocketDispText(thePocket.getPocketTemplateByPockettemplateid().getDescription());
        }
    }
}
