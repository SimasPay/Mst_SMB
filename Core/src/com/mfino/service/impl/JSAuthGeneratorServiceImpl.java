/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CmFinoFIX;
import com.mfino.service.AuthorizationService;
import com.mfino.service.JSAuthGeneratorService;
import com.mfino.service.UserDetailsServiceImpl;
import com.mfino.service.UserService;
import com.mfino.util.ConfigurationUtil;

/**
 *
 * @author Siddhartha Chinthapally
 */
@Service("JSAuthGeneratorServiceImpl")
public class JSAuthGeneratorServiceImpl implements JSAuthGeneratorService{
    private static final String JS_NAMESPACE = "mFino.auth";
    private static final String FN_TAB_ENABLED = "isEnabledTab";
    private static final String FN_ITEM_ENABLED = "isEnabledItem";
    private static final String FN_IS_MERCHANT = "isMerchant";
    private static final String FN_IS_SYSTEM_USER = "isSystemUser";
   // private static final String FN_IS_ALLOWEDFORLOP = "isAllowedForLOP";
    private static final String FN_GET_TREE_ROOT_ID = "getTreeRootId";
    private static final String FN_GET_SMART_PARTNER_CODE = "getSmartPartnerCode";
    private static final String FN_GET_TREE_ROOT_TEXT = "getTreeRootText";
    private static final String FN_GET_USER_NAME = "getUsername";
    private static final String FN_GET_COMPANY_NAME = "getCompanyName";
    private static final String FN_GET_PARTNER_ID = "getPartnerId";
    private static final String FN_GET_COMPANY_ID = "getCompanyId";
    private static final String FN_GET_USER_ROLE = "getUserRole";

    private static final String ELSE = " else ";

    private static final String SUBSCRIBER_TAB = "subscribers";
    private static final String MERCHANT_TAB = "merchant";
    private static final String TRANSACTIONS_TAB = "transactions";
    private static final String TRANSACTION_TAB = "transaction";
    private static final String LOP_TAB = "lop";
    private static final String POCKET_TEMPLATE_TAB = "pocketTemplate";
    private static final String USER_TAB = "user";
    private static final String DISTRIBUTION_TEMPLATE_TAB = "distributionTemplate";
    private static final String NOTIFICATION_TAB = "notification";
    //private static final String ENUM_TAB = "enumPage";
    private static final String TEMPLATES_TAB = "templates";
	private static final String SETTINGS_TAB = "settings";
	private static final String TRANSACTIONS_MAIN_TAB = "transactionsMain";
    private static final String PRODUCT_INDICATOR_TAB = "productIndicator";
    private static final String REGION_TAB = "region";
    private static final String MNO_PARAMS_TAB = "mnoParams";
    //private static final String BILLER_TAB = "biller";
    private static final String BANKADMIN_TAB = "bankAdmin";
    private static final String BULKUPLOAD_TAB = "bulkUpload";
    private static final String SERVICECHARGETEMPLATE_TAB = "serviceChargeTemplate";
    private static final String SMS_CODES_TAB = "smsCodes";
    private static final String SMS_PARTNER_TAB = "smsPartner";
    private static final String Channel_CODES_TAB = "channelCodes";
    private static final String Merchant_CODES_TAB = "merchantCodes";
    private static final String Merchant_PREFIX_CODES_TAB = "merchantPrefixCodes";
    private static final String CREDIT_CARD_REVIEWER_TAB = "creditCardReviewer";
    private static final String ROOT_TEXT_ONE = "Smart";
    private static final String ROOT_TEXT_TWO = "Mobile8";
    private static final String PARTNERS_TAB = "partners";
    private static final String SERVICE_PROVIDER_TAB = "partner";
    private static final String INTEGRATION_PARTNER_TAB = "integrationPartner";
    private static final String BUSINESS_PARTNER_TAB = "businessPartner";
    private static final String MFS_BILLER_TAB = "mfsBiller";
    private static final String CHARGE_TRANSACTION_TAB = "chargeTransactions";    
    private static final String CHARGE_TYPE_TAB = "chargeType";
    private static final String CHARGE_DEFINITION_TAB = "chargeDefinition";
    private static final String TRANSACTION_RULE_TAB = "transactionRule";
    private static final String TRANSACTION_CHARGE_TAB = "transactionCharge";
    private static final String REPORT_TAB = "report";
    private static final String AGENT_TAB = "agent";
    private static final String BULK_TRANSFER_TAB = "bulkTransfer";
    private static final String GROUPS_TAB = "groups";
    private static final String OLAP_TAB = "OLAP";
    private static final String REPORTS_TAB = "reports";
    private static final String POCKETTEMPLATECONFIG_TAB = "pocketTemplateConfig";
    private static final String HIERARCHIES_TAB = "hierarchies";
    private static final String DISTRIBUTION_HIERARCHY_TAB = "distributionHierarchy";
    private static final String SYSTEM_PARAMETERS_TAB = "systemParameters";
    private static final String INTEGRATIONS_TAB = "integrations";
    private static final String TELLER_TAB = "teller";
    private static final String FUNDING_FOR_AGENT_TAB="fundingForAgent";
    private static final String SCHEDULER_CONFIG_TAB = "schedulerConfig";
    private static final String FUND_DEFINITIONS_TAB="fundDefinitions";
    private static final String MONITOR_TAB="monitor";
    private static final String TRANSACTION_MONITOR_TAB="transactionMonitor";
    private static final String ACTOR_CHANNEL_MAPPING_TAB="actorChannelMapping";
    private static final String APPUPLOADER_TAB="appUploader";
    private static final String ADJUSTMENTS_TAB="adjustments";
    private static final String PERMISSIONS_TAB="permissions";
    private static final String PROMOS_TAB="promos";
    

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public String generateScript() {
        StringBuffer sb = new StringBuffer(4096);

        sb.append(getHeader());

        sb.append(getIsEnabledTab());
        sb.append(getIsMerchant());
        sb.append(getIsSystemUser());
        sb.append(getIsItemEnabled());
        sb.append(getGetTreeRootId());
        sb.append(getGetTreeRootText());
        sb.append(getGetUsername());
      //  sb.append(getisAllowedForLOP());
        sb.append(getCompanyId());
        sb.append(getCompanyName());
        sb.append(getPartnerCode());
        sb.append(getPartnerId());
        sb.append(getUserRole());

        return sb.toString();
    }

    private String getHeader() {
        return "Ext.ns(\"" + JS_NAMESPACE + "\");";
    }

    private String getCompanyName() {
        String companyName = "";
        if(userService.getUserCompany() != null){
            companyName = userService.getUserCompany().getCompanyName();
        }
        companyName = "'" + companyName + "'";
        return getGetterFunction(FN_GET_COMPANY_NAME, companyName);
    }
    
    private String getPartnerId() {
        Integer partnerId = -1;
        if(userService.getPartner() != null){
        	partnerId = userService.getPartner().getID().intValue();
        }
        return getGetterFunction(FN_GET_PARTNER_ID, partnerId + "");
    }
    
    private String getCompanyId() {
        long companyId = 0;
        if(userService.getUserCompany() != null){
            companyId = userService.getUserCompany().getID();
        }
        return getGetterFunction(FN_GET_COMPANY_ID, companyId + "");
    }
    private String getIsEnabledTab() {

        StringBuffer jsBuf = new StringBuffer(JS_NAMESPACE + "." + FN_TAB_ENABLED + " = function(tabName){");
        jsBuf.append(getCodeForSingleTabEnabled(SUBSCRIBER_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(MERCHANT_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(TRANSACTIONS_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(TRANSACTION_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(LOP_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(POCKET_TEMPLATE_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(USER_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(DISTRIBUTION_TEMPLATE_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(NOTIFICATION_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(PRODUCT_INDICATOR_TAB));
        jsBuf.append(ELSE);        
        jsBuf.append(getCodeForSingleTabEnabled(SETTINGS_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(TRANSACTIONS_MAIN_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(TEMPLATES_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(REGION_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(MNO_PARAMS_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(SMS_CODES_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(SMS_PARTNER_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(Channel_CODES_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(Merchant_CODES_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(Merchant_PREFIX_CODES_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(BULKUPLOAD_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(CREDIT_CARD_REVIEWER_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(BANKADMIN_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(PARTNERS_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(SERVICE_PROVIDER_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(INTEGRATION_PARTNER_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(BUSINESS_PARTNER_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(SERVICECHARGETEMPLATE_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(MFS_BILLER_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(CHARGE_TRANSACTION_TAB)); 
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(CHARGE_TYPE_TAB)); 
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(CHARGE_DEFINITION_TAB));  
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(TRANSACTION_RULE_TAB)); 
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(AGENT_TAB)); 
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(TELLER_TAB)); 
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(TRANSACTION_CHARGE_TAB)); 
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(REPORT_TAB)); 
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(BULK_TRANSFER_TAB)); 
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(GROUPS_TAB)); 
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(OLAP_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(REPORTS_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(POCKETTEMPLATECONFIG_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(HIERARCHIES_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(DISTRIBUTION_HIERARCHY_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(SYSTEM_PARAMETERS_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(INTEGRATIONS_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(FUNDING_FOR_AGENT_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(SCHEDULER_CONFIG_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(FUND_DEFINITIONS_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(MONITOR_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(TRANSACTION_MONITOR_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(ACTOR_CHANNEL_MAPPING_TAB)); 
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(APPUPLOADER_TAB)); 
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(ADJUSTMENTS_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(PERMISSIONS_TAB));
        jsBuf.append(ELSE);
        jsBuf.append(getCodeForSingleTabEnabled(PROMOS_TAB));
		jsBuf.append("};");
        return jsBuf.toString();
    }

    private String getCodeForSingleTabEnabled(String tabName) {
        return "if (tabName === '" + tabName + "') {\n return " + isTabEnabled(tabName) + ";}";
    }

    private boolean isTabEnabled(String tabName) {
        if (tabName.equals(SUBSCRIBER_TAB)) {
            return authorizationService.isAuthorized(CmFinoFIX.Permission_Subscriber_Details_View);
        } else if (tabName.equals(POCKET_TEMPLATE_TAB)) {
            return authorizationService.isAuthorized(CmFinoFIX.Permission_PocketTemplate_Details_View);
        } else if (tabName.equals(USER_TAB)) {
            return authorizationService.isAuthorized(CmFinoFIX.Permission_User_Details_View);
        } else if (tabName.equals(DISTRIBUTION_TEMPLATE_TAB)) {
            return authorizationService.isAuthorized(CmFinoFIX.Permission_DCT_View);
        } else if (tabName.equals(NOTIFICATION_TAB)) {
            return authorizationService.isAuthorized(CmFinoFIX.Permission_Notification_View);
        } else if (tabName.equals(SETTINGS_TAB)) {
            return (authorizationService.isAuthorized(CmFinoFIX.Permission_Brand_View) || 
            			authorizationService.isAuthorized(CmFinoFIX.Permission_Bulk_Upload_View) ||
            				authorizationService.isAuthorized(CmFinoFIX.Permission_Notification_View) ||            					
            					authorizationService.isAuthorized(CmFinoFIX.Permission_Groups) ||
            						authorizationService.isAuthorized(CmFinoFIX.Permission_Channel_Codes_View) ||
            							authorizationService.isAuthorized(CmFinoFIX.Permission_ChargeType_View) ||
            								authorizationService.isAuthorized(CmFinoFIX.Permission_ChargeDefinition_View) ||
            									authorizationService.isAuthorized(CmFinoFIX.Permission_TransactionRule_View) ||
            										authorizationService.isAuthorized(CmFinoFIX.Permission_TransactionCharge_View) ||
            											authorizationService.isAuthorized(CmFinoFIX.Permission_PocketTemplate_Details_View) ||
            												authorizationService.isAuthorized(CmFinoFIX.Permission_DCT_View) ||
            													authorizationService.isAuthorized(CmFinoFIX.Permission_pocketTemplateConfig) ||
            														authorizationService.isAuthorized(CmFinoFIX.Permission_SystemParameters) ||
            															authorizationService.isAuthorized(CmFinoFIX.Permission_Integrations) ||
            																authorizationService.isAuthorized(CmFinoFIX.Permission_SchedulerConfig) ||
            																	authorizationService.isAuthorized(CmFinoFIX.Permission_FundDefinitions) ||
            																		authorizationService.isAuthorized(CmFinoFIX.Permission_ActorChannelMapping) ||
            																			authorizationService.isAuthorized(CmFinoFIX.Permission_AppUploader) ||
            																				authorizationService.isAuthorized(CmFinoFIX.Permission_Permissions)||
                																				authorizationService.isAuthorized(CmFinoFIX.Permission_Promos)); 
        } else if (tabName.equals(Channel_CODES_TAB)) {
            return authorizationService.isAuthorized(CmFinoFIX.Permission_Channel_Codes_View);
        }else if (tabName.equals(MNO_PARAMS_TAB)) {
            return authorizationService.isAuthorized(CmFinoFIX.Permission_Brand_View);
        } else if (tabName.equals(BULKUPLOAD_TAB)) {
            return authorizationService.isAuthorized(CmFinoFIX.Permission_Bulk_Upload_View);
        } else if (tabName.equals(PARTNERS_TAB)) {
        	return (authorizationService.isAuthorized(CmFinoFIX.Permission_ServicePartner_View) ||        		
            		authorizationService.isAuthorized(CmFinoFIX.Permission_BusinessPartner_View) ||
            		authorizationService.isAuthorized(CmFinoFIX.Permission_MFSBiller_View) ||
            		authorizationService.isAuthorized(CmFinoFIX.Permission_BankTeller_View) ||
            		authorizationService.isAuthorized(CmFinoFIX.Permission_FundingForAgent));
        } else if (tabName.equals(SERVICE_PROVIDER_TAB)) {
        	 return authorizationService.isAuthorized(CmFinoFIX.Permission_ServicePartner_View);
        } else if (tabName.equals(BUSINESS_PARTNER_TAB)) {
        	 return authorizationService.isAuthorized(CmFinoFIX.Permission_ServicePartner_View);
        } else if (tabName.equals(MFS_BILLER_TAB)) {
        	 return authorizationService.isAuthorized(CmFinoFIX.Permission_MFSBiller_View);
        } else if (tabName.equals(CHARGE_TRANSACTION_TAB)) {
        	 return authorizationService.isAuthorized(CmFinoFIX.Permission_ServiceChargeTransaction_View);
        } else if (tabName.equals(CHARGE_TYPE_TAB)) {
        	 return authorizationService.isAuthorized(CmFinoFIX.Permission_ChargeType_View);
        } else if (tabName.equals(CHARGE_DEFINITION_TAB)) {
        	 return authorizationService.isAuthorized(CmFinoFIX.Permission_ChargeDefinition_View);
        } else if (tabName.equals(TRANSACTION_RULE_TAB)) {
        	 return authorizationService.isAuthorized(CmFinoFIX.Permission_TransactionRule_View);
        } else if (tabName.equals(TRANSACTION_CHARGE_TAB)) {
        	 return authorizationService.isAuthorized(CmFinoFIX.Permission_TransactionCharge_View);
        } else if (tabName.equals(AGENT_TAB)) {
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_BusinessPartner_View);
        } else if (tabName.equals(TELLER_TAB)) {
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_BankTeller_View);
        } else if (tabName.equals(REPORT_TAB)) {
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_Report_View);
        } else if (tabName.equals(BULK_TRANSFER_TAB)) {
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_BulkTransfer);
        } else if (tabName.equals(GROUPS_TAB)) {
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_Groups);
        } else if (tabName.equals(OLAP_TAB)) {
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_Olap);
        } else if (tabName.equals(REPORTS_TAB)) {
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_Olap) || 
        				authorizationService.isAuthorized(CmFinoFIX.Permission_Report_View);
        } else if (tabName.equals(POCKETTEMPLATECONFIG_TAB)) {
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_pocketTemplateConfig);
        } else if (tabName.equals(HIERARCHIES_TAB)) {
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_DistributionHierarchy);
        } else if (tabName.equals(DISTRIBUTION_HIERARCHY_TAB)) {
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_DistributionHierarchy);
        } else if (tabName.equals(SYSTEM_PARAMETERS_TAB)) {
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_SystemParameters);
        } else if (tabName.equals(INTEGRATIONS_TAB)) {
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_Integrations);
        } else if(tabName.equals(FUNDING_FOR_AGENT_TAB)){
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_FundingForAgent);
        } else if(tabName.equals(SCHEDULER_CONFIG_TAB)){
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_SchedulerConfig);
        } else if(tabName.equals(FUND_DEFINITIONS_TAB)){
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_FundDefinitions);
        } /*else if(tabName.equals(MONITOR_TAB)){
        	return Authorization.isAuthorized(CmFinoFIX.Permission_TransactionMonitor);
        } else if(tabName.equals(TRANSACTION_MONITOR_TAB)){
        	return Authorization.isAuthorized(CmFinoFIX.Permission_TransactionMonitor);
        } */else if(tabName.equals(APPUPLOADER_TAB)){
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_AppUploader);
        } else if(tabName.equals(PROMOS_TAB)){
            return authorizationService.isAuthorized(CmFinoFIX.Permission_Promos);
        } else if(tabName.equals(ACTOR_CHANNEL_MAPPING_TAB)){
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_ActorChannelMapping);
        } else if(tabName.equals(ADJUSTMENTS_TAB)){
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_Adjustments);
        } else if(tabName.equals(PERMISSIONS_TAB)){
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_Permissions);
        } else if(tabName.equals(TRANSACTIONS_MAIN_TAB)){
        	return authorizationService.isAuthorized(CmFinoFIX.Permission_ServiceChargeTransaction_View) || 
        				authorizationService.isAuthorized(CmFinoFIX.Permission_Adjustments);
        }
        return false;
    }

    private String getIsMerchant() {

        boolean isMerchant = userService.isMerchant();

        StringBuffer jsBuf = new StringBuffer("\n" + JS_NAMESPACE + "." + FN_IS_MERCHANT + " = function(){\n");
        jsBuf.append("return " + isMerchant + ";\n");
        jsBuf.append("};");
        return jsBuf.toString();
    }
    
	/**
	 * @return a js function that returns true if current user is of type system user else returns false
	 */
	private String getIsSystemUser() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		Integer enumCode = ((UserDetailsServiceImpl) auth.getPrincipal()).getRole();
		boolean isSystemUser = userService.isSystemUser(enumCode);
		StringBuffer jsBuf = new StringBuffer("\n" + JS_NAMESPACE + "."
				+ FN_IS_SYSTEM_USER + " = function(){\n");
		jsBuf.append("return " + isSystemUser + ";\n");
        jsBuf.append("};");
        return jsBuf.toString();
    }

    private static String getPartnerCode() {
        Integer partnerCode = ConfigurationUtil.getSMARTEMoneyPartnerCode();
        return getGetterFunction(FN_GET_SMART_PARTNER_CODE, partnerCode + "");
    }

//    private static String getisAllowedForLOP() {
//        boolean isMerchant = UserService.isMerchant();
//        boolean isallowedforlop = false;
//        StringBuffer jsBuf;
//        jsBuf = new StringBuffer("\n" + JS_NAMESPACE + "." + FN_IS_ALLOWEDFORLOP + " = function(){\n");
//        if (isMerchant) {
//            isallowedforlop = MerchantService.isAuthorizedForLOP(MerchantService.getMerchantIDOfLoggedInUser());
//        }
//        jsBuf.append("return " + isallowedforlop + ";\n");
//        jsBuf.append("};");
//        return jsBuf.toString();
//    }

    private String getGetTreeRootId() {
        //boolean isMerchant = userService.isMerchant();
        long id = 0;
        /*if (isMerchant) {
            id = MerchantService.getMerchantIDOfLoggedInUser();
        }*/
        return getGetterFunction(FN_GET_TREE_ROOT_ID, id + "");
    }

    private String getGetTreeRootText() {
        //boolean isMerchant = userService.isMerchant();
        String rootText;
        if (ROOT_TEXT_ONE.equalsIgnoreCase(userService.getUserCompany().getCompanyName())) {
            rootText = ROOT_TEXT_ONE;
        } else {
            rootText = ROOT_TEXT_TWO;
        }
        /*if (isMerchant) {
            long id = MerchantService.getMerchantIDOfLoggedInUser();
            if (id != -1) {
                MerchantDAO merchantDAO = new MerchantDAO();

                MerchantQuery query = new MerchantQuery();
                query.setId(id);
                List results = merchantDAO.getByHQL(query);
                if (null != results && results.size() > 0) {
                    Merchant merchant = (Merchant) results.get(0);
                    //Smart has requested to show the user name instead of first name and last name
//                    rootText = merchant.getSubscriber().getFirstName() + GeneralConstants.SINGLE_SPACE + merchant.getSubscriber().getLastName();
                    rootText = merchant.getSubscriber().getUser().getUsername();
                }
            }
        }*/
        rootText = "'" + rootText + "'";
        return getGetterFunction(FN_GET_TREE_ROOT_TEXT, rootText);
    }

    private static String getGetUsername() {
        String username = "";
        username = SecurityContextHolder.getContext().getAuthentication().getName();
        username = "'" + username + "'";
        return getGetterFunction(FN_GET_USER_NAME, username);
    }
    
    private static String getUserRole() {
        String userrole = "";
        userrole = SecurityContextHolder.getContext().getAuthentication().getName();
        userrole = "'" + userrole + "'";
        return getGetterFunction(FN_GET_USER_ROLE, userrole);
    }

    private static String getGetterFunction(String functionName, String value) {
        StringBuffer jsBuf = new StringBuffer("\n" + JS_NAMESPACE + "." + functionName + " = function(){\n");
        jsBuf.append("return " + value + ";\n");
        jsBuf.append("};");
        return jsBuf.toString();
    }

    private String getIsItemEnabled() {
        StringBuffer jsBuf = new StringBuffer("\n" + JS_NAMESPACE + "." + FN_ITEM_ENABLED + " = function(itemId){\n");
        jsBuf.append("var enabledItems = [];");
        String[] enabledItems = authorizationService.getEnabledItemIds(1);
        for (String enabledItem : enabledItems) {
            jsBuf.append("\nenabledItems['" + enabledItem + "'] = true;");
        }
        jsBuf.append("\nreturn enabledItems[itemId]; };");
        return jsBuf.toString();
    }
    
    
}
