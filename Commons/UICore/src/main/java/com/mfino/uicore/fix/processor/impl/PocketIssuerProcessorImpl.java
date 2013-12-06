/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketTemplateConfigDAO;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.PocketTemplateConfigQuery;
import com.mfino.dao.query.PocketTemplateQuery;
import com.mfino.domain.Group;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Partner;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.PocketTemplateConfig;
import com.mfino.domain.SubscriberGroup;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSPocketTemplate;
import com.mfino.i18n.MessageText;
import com.mfino.service.AuthorizationService;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.PocketIssuerProcessor;

/**
 * 
 * @author Raju
 */
@Service("PocketIssuerProcessorImpl")
public class PocketIssuerProcessorImpl extends BaseFixProcessor implements PocketIssuerProcessor{

	private MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance()
			.getMfinoServiceProviderDAO();
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

	private void updateEntity(PocketTemplate p,
			CmFinoFIX.CMJSPocketTemplate.CGEntries e) {

		// currently there is always 1 MSP
		p.setmFinoServiceProviderByMSPID(mspDAO.getById(1l));

		if (e.getType() != null) {
			p.setType(e.getType());
		}
		if (e.getBankAccountCardType() != null) {
			p.setBankAccountCardType(e.getBankAccountCardType());
		}
		if (e.getDescription() != null) {
			p.setDescription(e.getDescription());
		}
		if (e.getCommodity() != null) {
			p.setCommodity(e.getCommodity());
		}
		if (e.getTypeOfCheck() != null) {
			p.setTypeOfCheck(e.getTypeOfCheck());
		}
		if (e.getCardPANSuffixLength() != null) {
			p.setCardPANSuffixLength(e.getCardPANSuffixLength());
		}
		if (e.getRegularExpression() != null) {
			p.setRegularExpression(e.getRegularExpression());
		}
		if (e.getUnits() != null) {
			p.setUnits(e.getUnits());
		}
		if (e.getAllowance() != null) {
			p.setAllowance(e.getAllowance());
		}
		if (e.getMaximumStoredValue() != null) {
			p.setMaximumStoredValue(e.getMaximumStoredValue());
		}
		if (e.getMinimumStoredValue() != null) {
			p.setMinimumStoredValue(e.getMinimumStoredValue());
		}
		if (e.getMaxAmountPerTransaction() != null) {
			p.setMaxAmountPerTransaction(e.getMaxAmountPerTransaction());
		}
		if (e.getMinAmountPerTransaction() != null) {
			p.setMinAmountPerTransaction(e.getMinAmountPerTransaction());
		}
		if (e.getMaxAmountPerDay() != null) {
			p.setMaxAmountPerDay(e.getMaxAmountPerDay());
		}
		if (e.getMaxAmountPerWeek() != null) {
			p.setMaxAmountPerWeek(e.getMaxAmountPerWeek());
		}
		if (e.getMaxAmountPerMonth() != null) {
			p.setMaxAmountPerMonth(e.getMaxAmountPerMonth());
		}
		if (e.getMaxTransactionsPerDay() != null) {
			p.setMaxTransactionsPerDay(e.getMaxTransactionsPerDay());
		}
		if (e.getMaxTransactionsPerWeek() != null) {
			p.setMaxTransactionsPerWeek(e.getMaxTransactionsPerWeek());
		}
		if (e.getMaxTransactionsPerMonth() != null) {
			p.setMaxTransactionsPerMonth(e.getMaxTransactionsPerMonth());
		}
		if (e.getMinTimeBetweenTransactions() != null) {
			p.setMinTimeBetweenTransactions(e.getMinTimeBetweenTransactions());
		}
		if (e.getBankCode() != null) {
			p.setBankCode(e.getBankCode());
		}
		if (e.getOperatorCode() != null) {
			p.setOperatorCode(e.getOperatorCode());
		}
		if (e.getBillingType() != null) {
			p.setBillingType(e.getBillingType());
		}
		if (e.getLastUpdateTime() != null) {
			p.setLastUpdateTime(e.getLastUpdateTime());
		}
		if (e.getUpdatedBy() != null) {
			p.setUpdatedBy(e.getUpdatedBy());
		}
		if (e.getCreateTime() != null) {
			p.setCreateTime(e.getCreateTime());
		}
		if (e.getCreatedBy() != null) {
			p.setCreatedBy(e.getCreatedBy());
		}
		if (e.getLowBalanceNotificationEnabled() != null) {
			p.setLowBalanceNotificationEnabled(e
					.getLowBalanceNotificationEnabled());
			p.setLowBalanceNtfcThresholdAmt(e.getLowBalanceNtfcThresholdAmt());
		}
		if (e.getLowBalanceNtfcThresholdAmt() != null) {
			p.setLowBalanceNtfcThresholdAmt(e.getLowBalanceNtfcThresholdAmt());
		}
		if (e.getWebTimeInterval() != null) {
			p.setWebTimeInterval(e.getWebTimeInterval());
		}
		if (e.getWebServiceTimeInterval() != null) {
			p.setWebServiceTimeInterval(e.getWebServiceTimeInterval());
		}
		if (e.getUTKTimeInterval() != null) {
			p.setUTKTimeInterval(e.getUTKTimeInterval());
		}
		if (e.getBankChannelTimeInterval() != null) {
			p.setBankChannelTimeInterval(e.getBankChannelTimeInterval());
		}
		if (e.getDenomination() != null) {
			p.setDenomination(e.getDenomination());
		}
		if (e.getPocketCode() != null) {
			p.setPocketCode(e.getPocketCode());
		}
		if (e.getIsCollectorPocket() != null) {
			p.setIsCollectorPocket(e.getIsCollectorPocket());
		}
		if (e.getNumberOfPocketsAllowedForMDN() != null) {
			p.setNumberOfPocketsAllowedForMDN(e
					.getNumberOfPocketsAllowedForMDN());
		}
		if (e.getIsSuspencePocket() != null) {
			p.setIsSuspencePocket(e.getIsSuspencePocket());
		}
	}

	private void updateMessage(PocketTemplate p,
			CmFinoFIX.CMJSPocketTemplate.CGEntries entry) {
		entry.setID(p.getID());
		entry.setMSPID(p.getmFinoServiceProviderByMSPID().getID());
		entry.setType(p.getType());
		if (p.getBankAccountCardType() != null) {
			entry.setBankAccountCardType(p.getBankAccountCardType());
		}
		if (p.getDescription() != null) {
			entry.setDescription(p.getDescription());
		}
		if (p.getTypeOfCheck() != null) {
			entry.setTypeOfCheck(p.getTypeOfCheck());
		}
		if (p.getRegularExpression() != null) {
			entry.setRegularExpression(p.getRegularExpression());
		}
		entry.setCommodity(p.getCommodity());
		if (p.getCardPANSuffixLength() != null) {
			entry.setCardPANSuffixLength(p.getCardPANSuffixLength());
		}
		if (p.getUnits() != null) {
			entry.setUnits(p.getUnits());
		}
		if (p.getAllowance() != null) {
			entry.setAllowance(p.getAllowance());
		}
		if (p.getMaximumStoredValue() != null) {
			entry.setMaximumStoredValue(p.getMaximumStoredValue());
		}
		if (p.getMinimumStoredValue() != null) {
			entry.setMinimumStoredValue(p.getMinimumStoredValue());
		}
		if (p.getDenomination() != null) {
			entry.setDenomination(p.getDenomination());
		}
		if (p.getWebTimeInterval() != null) {
			entry.setWebTimeInterval(p.getWebTimeInterval());
		}
		if (p.getWebServiceTimeInterval() != null) {
			entry.setWebServiceTimeInterval(p.getWebServiceTimeInterval());
		}
		if (p.getUTKTimeInterval() != null) {
			entry.setUTKTimeInterval(p.getUTKTimeInterval());
		}
		if (p.getBankChannelTimeInterval() != null) {
			entry.setBankChannelTimeInterval(p.getBankChannelTimeInterval());
		}
		entry.setMaxAmountPerTransaction(p.getMaxAmountPerTransaction());
		entry.setMinAmountPerTransaction(p.getMinAmountPerTransaction());
		entry.setMaxAmountPerDay(p.getMaxAmountPerDay());
		entry.setMaxAmountPerWeek(p.getMaxAmountPerWeek());
		entry.setMaxAmountPerMonth(p.getMaxAmountPerMonth());
		entry.setMaxTransactionsPerDay(p.getMaxTransactionsPerDay());
		entry.setMaxTransactionsPerWeek(p.getMaxTransactionsPerWeek());
		entry.setMaxTransactionsPerMonth(p.getMaxTransactionsPerMonth());
		entry.setMinTimeBetweenTransactions(p.getMinTimeBetweenTransactions());
		if (p.getBankCode() != null) {
			entry.setBankCode(p.getBankCode());
		}
		if (p.getOperatorCode() != null) {
			entry.setOperatorCode(p.getOperatorCode());
		}
		if (p.getBillingType() != null) {
			entry.setBillingType(p.getBillingType());
		}
		if (p.getLastUpdateTime() != null) {
			entry.setLastUpdateTime(p.getLastUpdateTime());
		}
		if (p.getUpdatedBy() != null) {
			entry.setUpdatedBy(p.getUpdatedBy());
		}
		if (p.getCreateTime() != null) {
			entry.setCreateTime(p.getCreateTime());
		}
		if (p.getCreatedBy() != null) {
			entry.setCreatedBy(p.getCreatedBy());
		}
		entry.setTypeOfCheckText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_TypeOfCheck, null, p.getTypeOfCheck()));
		entry.setPocketTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_PocketType, null, p.getType()));
		entry.setOperatorCodeForRoutingText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_OperatorCodeForRouting, null,
				p.getOperatorCode()));
		// entry.setBankCodeForRoutingText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankCodeForRouting,
		// null, p.getBankCode()));
		entry.setBillingTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_BillingType, null, p.getBillingType()));
		entry.setCommodityTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_Commodity, null, p.getCommodity()));
		entry.setPocketSubTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_BankAccountCardType, null,
				p.getBankAccountCardType()));

		if (p.getVersion() != null) {
			entry.setRecordVersion(p.getVersion());
		}
		if (p.getLowBalanceNotificationEnabled() != null) {
			entry.setLowBalanceNotificationEnabled(p
					.getLowBalanceNotificationEnabled());
			entry.setLowBalanceNtfcThresholdAmt(p
					.getLowBalanceNtfcThresholdAmt());
		}
		if (p.getPocketCode() != null) {
			entry.setPocketCode(p.getPocketCode());
		}
		if (p.getIsCollectorPocket() != null) {
			entry.setIsCollectorPocket(p.getIsCollectorPocket());
		}
		if (p.getNumberOfPocketsAllowedForMDN() != null) {
			entry.setNumberOfPocketsAllowedForMDN(p
					.getNumberOfPocketsAllowedForMDN());
		}
		if (p.getIsSuspencePocket() != null) {
			entry.setIsSuspencePocket(p.getIsSuspencePocket());
		}
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CmFinoFIX.CMJSPocketTemplate realMsg = (CmFinoFIX.CMJSPocketTemplate) msg;

		PocketTemplateDAO dao = DAOFactory.getInstance().getPocketTemplateDao();

		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			CMJSPocketTemplate.CGEntries[] entries = realMsg.getEntries();

			PocketTemplateQuery query = new PocketTemplateQuery();
			for (CMJSPocketTemplate.CGEntries entry : entries) {
				PocketTemplate pocketTemplateObj = dao.getById(entry.getID());

				// Check for Stale Data
				if (!entry.getRecordVersion().equals(
						pocketTemplateObj.getVersion())) {
					handleStaleDataException();
				}

				boolean isAuthorized = authorizationService
						.isAuthorized(CmFinoFIX.Permission_PocketTemplate_RiskManagement_Edit);

				if (!isAuthorized) {
					CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
					errorMsg.setErrorDescription(MessageText
							._("Not Authorized to change the Pocket Template"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					return errorMsg;
				}
				// checking whether the pocket code is already exists or not..
				if (StringUtils.isNotBlank(entry.getPocketCode())) {
					query.setPocketCode(entry.getPocketCode());
					List<PocketTemplate> results = dao.get(query);
					if (results != null && results.size() > 0) {
						CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
						error.allocateEntries(1);
						error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
						error.setErrorDescription("Pocket Code already exists");
						return error;
					}
				}
				updateEntity(pocketTemplateObj, entry);
				dao.save(pocketTemplateObj);
				updateMessage(pocketTemplateObj, entry);
			}

			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg
				.getaction())) {
			if (realMsg.getMDNSearch() != null
					&& StringUtils.isNotBlank(realMsg.getMDNSearch())) {
				String subMdn = realMsg.getMDNSearch();
				getPocketTemplatesForMdn(realMsg, dao, subMdn);

			} else {
				getPocketTemplates(realMsg, dao);
			}

		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg
				.getaction())) {
			CMJSPocketTemplate.CGEntries[] entries = realMsg.getEntries();

			PocketTemplateQuery query = new PocketTemplateQuery();

			for (CMJSPocketTemplate.CGEntries e : entries) {
				if (e == null) {
					continue;
				}

				PocketTemplate pt = new PocketTemplate();

				// checking whether the pocket code is already exists or not..
				if (StringUtils.isNotBlank(e.getPocketCode())) {
					query.setPocketCode(e.getPocketCode());
					List<PocketTemplate> results = dao.get(query);
					if (results != null && results.size() > 0) {
						return getErrorMessage(
								MessageText._("PocketCode already exist"),
								CmFinoFIX.ErrorCode_Generic,
								CmFinoFIX.CMJSPocketTemplate.CGEntries.FieldName_PocketCode,
								MessageText._("PocketCode already exist"));

					}
				}

				updateEntity(pt, e);
				dao.save(pt);
				updateMessage(pt, e);
			}

			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		}

		return realMsg;
	}

	private void getPocketTemplates(CmFinoFIX.CMJSPocketTemplate realMsg,
			PocketTemplateDAO dao) {
		PocketTemplateQuery query = new PocketTemplateQuery();

		// getquery is for search dropdown.
		if (StringUtils.isNotBlank(realMsg.getquery())) {
			query.setDescriptionSearch(realMsg.getquery());
		}

		if (realMsg.getPocketTypeSearch() != null
				&& !realMsg.getPocketTypeSearch().equals(
						GeneralConstants.EMPTY_STRING)) {
			query.setPocketType(Integer.parseInt(realMsg.getPocketTypeSearch()));
		}

		if (realMsg.getCommodityTypeSearch() != null
				&& !realMsg.getCommodityTypeSearch().equals(
						GeneralConstants.EMPTY_STRING)) {
			query.setCommodityType(Integer.parseInt(realMsg
					.getCommodityTypeSearch()));
		}

		if (realMsg.getStartDateSearch() != null) {
			query.setStartDate(realMsg.getStartDateSearch());
		}
		if (realMsg.getEndDateSearch() != null) {
			query.setEndDate(realMsg.getEndDateSearch());
		}

		if (realMsg.getDescriptionSearch() != null
				&& !realMsg.getDescriptionSearch().equals(
						GeneralConstants.EMPTY_STRING)) {
			query.setDescriptionSearch(realMsg.getDescriptionSearch());
		}

		if (realMsg.getIsCollectorPocketAllowed() != null) {
			query.set_isCollectorPocket(realMsg.getIsCollectorPocketAllowed());
		}
		if (realMsg.getIsSuspencePocketAllowed() != null) {
			query.set_isSuspencePocket(realMsg.getIsSuspencePocketAllowed());
		}

		query.setStart(realMsg.getstart());
		query.setLimit(realMsg.getlimit());

		List<PocketTemplate> results = dao.get(query);
		realMsg.allocateEntries(results.size());

		for (int i = 0; i < results.size(); i++) {
			PocketTemplate pt = results.get(i);
			CMJSPocketTemplate.CGEntries entry = new CMJSPocketTemplate.CGEntries();

			updateMessage(pt, entry);
			realMsg.getEntries()[i] = entry;
		}
		realMsg.setsuccess(CmFinoFIX.Boolean_True);
		realMsg.settotal(query.getTotal());
	}

	public void getPocketTemplatesForMdn(CmFinoFIX.CMJSPocketTemplate realMsg,
			PocketTemplateDAO dao, String subMdn) {
		SubscriberMDNDAO mdnDao = DAOFactory.getInstance()
				.getSubscriberMdnDAO();
		PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();

		SubscriberMDN smdn = mdnDao.getByMDN(subMdn);
		Integer subscriberType = smdn.getSubscriber().getType();
	
		//Getting group for subscriber
		Set<SubscriberGroup> group=smdn.getSubscriber().getSubscriberGroupFromSubscriberID();
		Group subscriberGroup =null;
		Iterator<SubscriberGroup> iterator = group.iterator();
		while(iterator.hasNext()){			
		 subscriberGroup = iterator.next().getGroup();
		}
		
		Integer businessPartnerType = null;
		

		if (subscriberType.intValue() != 0) {
			Partner p = partnerDao.getPartnerBySubscriber(smdn.getSubscriber());
			businessPartnerType = p.getBusinessPartnerType();

		}
		Long kycLevelNo = null;
		if(null != smdn.getSubscriber().getUpgradableKYCLevel())
		{
			kycLevelNo = smdn.getSubscriber().getUpgradableKYCLevel();
		}
		else
		{
			KYCLevel kyclevel = smdn.getSubscriber().getKYCLevelByKYCLevel();
			kycLevelNo = kyclevel.getKYCLevel();
		}

		PocketTemplateConfigQuery ptcq = new PocketTemplateConfigQuery();
		ptcq.set_subscriberType(subscriberType);
		ptcq.set_businessPartnerType(businessPartnerType);
		ptcq.set_KYCLevel(kycLevelNo);
		ptcq.set_GroupID(subscriberGroup != null ? subscriberGroup.getID() : 1);//TODO for default group
		
				
		PocketTemplateConfigDAO ptcDao = new PocketTemplateConfigDAO();
		List<PocketTemplateConfig> ptcresults = ptcDao.get(ptcq);
		/*
		 * if the result set returned for the given Group is null,
		 * then fetch the results using default System Group
		 */
		if(ptcresults == null || ptcresults.size() ==0)
		{
			Group systemGroup = DAOFactory.getInstance().getGroupDao().getSystemGroup();
			if(systemGroup != null)
			{
				ptcq.set_GroupID(systemGroup.getID());
				ptcresults = ptcDao.get(ptcq);
			}
		}		

		List<PocketTemplate> results = new ArrayList<PocketTemplate>();
		for (int i = 0; i < ptcresults.size(); i++) {
			PocketTemplate pt = ptcresults.get(i).getPocketTemplate();
			results.add(pt);
		}

		realMsg.allocateEntries(results.size());

		for (int i = 0; i < results.size(); i++) {
			PocketTemplate pt = results.get(i);
			CMJSPocketTemplate.CGEntries entry = new CMJSPocketTemplate.CGEntries();

			updateMessage(pt, entry);
			realMsg.getEntries()[i] = entry;
		}
		realMsg.setsuccess(CmFinoFIX.Boolean_True);
		realMsg.settotal(ptcq.getTotal());
	}
}
