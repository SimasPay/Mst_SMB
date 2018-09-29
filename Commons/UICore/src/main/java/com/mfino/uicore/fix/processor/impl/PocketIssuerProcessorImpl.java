/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.PocketTemplateConfigQuery;
import com.mfino.dao.query.PocketTemplateQuery;
import com.mfino.domain.Groups;
import com.mfino.domain.KycLevel;
import com.mfino.domain.Partner;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.PocketTemplateConfig;
import com.mfino.domain.SubscriberGroups;
import com.mfino.domain.SubscriberMdn;
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
		p.setMfinoServiceProvider(mspDAO.getById(1l));

		if (e.getType() != null) {
			p.setType(e.getType());
		}
		if (e.getBankAccountCardType() != null) {
			p.setBankaccountcardtype(e.getBankAccountCardType());
		}
		if (e.getDescription() != null) {
			p.setDescription(e.getDescription());
		}
		if (e.getCommodity() != null) {
			p.setCommodity(e.getCommodity());
		}
		if (e.getTypeOfCheck() != null) {
			p.setTypeofcheck(e.getTypeOfCheck());
		}
		if (e.getCardPANSuffixLength() != null) {
			p.setCardpansuffixlength(e.getCardPANSuffixLength());
		}
		if (e.getRegularExpression() != null) {
			p.setRegularexpression(e.getRegularExpression());
		}
		if (e.getUnits() != null) {
			p.setUnits(e.getUnits());
		}
		if (e.getAllowance() != null) {
			p.setAllowance(e.getAllowance());
		}
		if (e.getMaximumStoredValue() != null) {
			p.setMaximumstoredvalue(e.getMaximumStoredValue());
		}
		if (e.getMinimumStoredValue() != null) {
			p.setMinimumstoredvalue(e.getMinimumStoredValue());
		}
		if (e.getMaxAmountPerTransaction() != null) {
			p.setMaxamountpertransaction(e.getMaxAmountPerTransaction());
		}
		if (e.getMinAmountPerTransaction() != null) {
			p.setMinamountpertransaction(e.getMinAmountPerTransaction());
		}
		if (e.getMaxAmountPerDay() != null) {
			p.setMaxamountperday(e.getMaxAmountPerDay());
		}
		if (e.getMaxAmountPerWeek() != null) {
			p.setMaxamountperweek(e.getMaxAmountPerWeek());
		}
		if (e.getMaxAmountPerMonth() != null) {
			p.setMaxamountpermonth(e.getMaxAmountPerMonth());
		}
		if (e.getMaxTransactionsPerDay() != null) {
			p.setMaxtransactionsperday(e.getMaxTransactionsPerDay());
		}
		if (e.getMaxTransactionsPerWeek() != null) {
			p.setMaxtransactionsperweek(e.getMaxTransactionsPerWeek());
		}
		if (e.getMaxTransactionsPerMonth() != null) {
			p.setMaxtransactionspermonth(e.getMaxTransactionsPerMonth());
		}
		if (e.getMinTimeBetweenTransactions() != null) {
			p.setMintimebetweentransactions(e.getMinTimeBetweenTransactions());
		}
		if (e.getBankCode() != null) {
			p.setBankcode(e.getBankCode());
		}
		if (e.getOperatorCode() != null) {
			p.setOperatorcode(e.getOperatorCode());
		}
		if (e.getBillingType() != null) {
			p.setBillingtype(e.getBillingType());
		}
		if (e.getLastUpdateTime() != null) {
			p.setLastupdatetime(e.getLastUpdateTime());
		}
		if (e.getUpdatedBy() != null) {
			p.setUpdatedby(e.getUpdatedBy());
		}
		if (e.getCreateTime() != null) {
			p.setCreatetime(e.getCreateTime());
		}
		if (e.getCreatedBy() != null) {
			p.setCreatedby(e.getCreatedBy());
		}
		if (e.getLowBalanceNotificationEnabled() != null) {
			p.setLowbalancenotificationenabled(e.getLowBalanceNotificationEnabled());
			p.setLowbalancentfcthresholdamt(e.getLowBalanceNtfcThresholdAmt());
		}
		if (e.getLowBalanceNtfcThresholdAmt() != null) {
			p.setLowbalancentfcthresholdamt(e.getLowBalanceNtfcThresholdAmt());
		}
		if (e.getWebTimeInterval() != null) {
			p.setWebtimeinterval(e.getWebTimeInterval());
		}
		if (e.getWebServiceTimeInterval() != null) {
			p.setWebservicetimeinterval(e.getWebServiceTimeInterval());
		}
		if (e.getUTKTimeInterval() != null) {
			p.setUtktimeinterval(e.getUTKTimeInterval());
		}
		if (e.getBankChannelTimeInterval() != null) {
			p.setBankchanneltimeinterval(e.getBankChannelTimeInterval());
		}
		if (e.getDenomination() != null) {
			p.setDenomination(e.getDenomination());
		}
		if (e.getPocketCode() != null) {
			p.setPocketcode(e.getPocketCode());
		}
		if (e.getIsCollectorPocket() != null) {
			p.setIscollectorpocket(e.getIsCollectorPocket());
		}
		if (e.getNumberOfPocketsAllowedForMDN() != null) {
			p.setNumberofpocketsallowedformdn(e.getNumberOfPocketsAllowedForMDN());
		}
		if (e.getIsSuspencePocket() != null) {
			p.setIssuspencepocket(e.getIsSuspencePocket());
		}
		if (e.getIsSystemPocket() != null) {
			p.setIssystempocket(e.getIsSystemPocket());
		}
		if (e.getInterestRate() != null) {
			p.setInterestrate(e.getInterestRate());
		}
	}

	private void updateMessage(PocketTemplate p,
			CmFinoFIX.CMJSPocketTemplate.CGEntries entry) {
		entry.setID(p.getId().longValue());
		entry.setMSPID(p.getMfinoServiceProvider().getId().longValue());
		entry.setType((p.getType()).intValue());
		if (p.getBankaccountcardtype() != null) {
			entry.setBankAccountCardType(p.getBankaccountcardtype().intValue());
		}
		if (p.getDescription() != null) {
			entry.setDescription(p.getDescription());
		}
		if (p.getTypeofcheck() != null) {
			entry.setTypeOfCheck((p.getTypeofcheck()).intValue());
		}
		if (p.getRegularexpression() != null) {
			entry.setRegularExpression(p.getRegularexpression());
		}
		entry.setCommodity((p.getCommodity()).intValue());
		if (p.getCardpansuffixlength() != null) {
			entry.setCardPANSuffixLength(p.getCardpansuffixlength().intValue());
		}
		if (p.getUnits() != null) {
			entry.setUnits(p.getUnits());
		}
		if (p.getAllowance() != null) {
			entry.setAllowance((p.getAllowance()).intValue());
		}
		if (p.getMaximumstoredvalue() != null) {
			entry.setMaximumStoredValue(p.getMaximumstoredvalue());
		}
		if (p.getMinimumstoredvalue() != null) {
			entry.setMinimumStoredValue(p.getMinimumstoredvalue());
		}
		if (p.getDenomination() != null) {
			entry.setDenomination(p.getDenomination().longValue());
		}
		if (p.getWebtimeinterval() != null) {
			entry.setWebTimeInterval(p.getWebtimeinterval().intValue());
		}
		if (p.getWebservicetimeinterval() != null) {
			entry.setWebServiceTimeInterval(p.getWebservicetimeinterval().intValue());
		}
		if (p.getUtktimeinterval() != null) {
			entry.setUTKTimeInterval(p.getUtktimeinterval().intValue());
		}
		if (p.getBankchanneltimeinterval() != null) {
			entry.setBankChannelTimeInterval(p.getBankchanneltimeinterval().intValue());
		}
		entry.setMaxAmountPerTransaction(p.getMaxamountpertransaction());
		entry.setMinAmountPerTransaction(p.getMinamountpertransaction());
		entry.setMaxAmountPerDay(p.getMaxamountperday());
		entry.setMaxAmountPerWeek(p.getMaxamountperweek());
		entry.setMaxAmountPerMonth(p.getMaxamountpermonth());
		entry.setMaxTransactionsPerDay((p.getMaxtransactionsperday()).intValue());
		entry.setMaxTransactionsPerWeek((p.getMaxtransactionsperweek()).intValue());
		entry.setMaxTransactionsPerMonth((p.getMaxtransactionspermonth()).intValue());
		entry.setMinTimeBetweenTransactions((p.getMintimebetweentransactions()).intValue());
		if (p.getBankcode() != null) {
			entry.setBankCode(p.getBankcode().intValue());
		}
		if (p.getOperatorcode() != null) {
			entry.setOperatorCode(p.getOperatorcode().intValue());
		}
		if (p.getBillingtype() != null) {
			entry.setBillingType(p.getBillingtype().intValue());
		}
		if (p.getLastupdatetime() != null) {
			entry.setLastUpdateTime(p.getLastupdatetime());
		}
		if (p.getUpdatedby() != null) {
			entry.setUpdatedBy(p.getUpdatedby());
		}
		if (p.getCreatetime() != null) {
			entry.setCreateTime(p.getCreatetime());
		}
		if (p.getCreatedby() != null) {
			entry.setCreatedBy(p.getCreatedby());
		}
		entry.setTypeOfCheckText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_TypeOfCheck, null, p.getTypeofcheck()));
		entry.setPocketTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_PocketType, null, p.getType()));
		entry.setOperatorCodeForRoutingText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_OperatorCodeForRouting, null,
				p.getOperatorcode()));
		// entry.setBankCodeForRoutingText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankCodeForRouting,
		// null, p.getBankCode()));
		entry.setBillingTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_BillingType, null, p.getBillingtype()));
		entry.setCommodityTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_Commodity, null, p.getCommodity()));
		entry.setPocketSubTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_BankAccountCardType, null,
				p.getBankaccountcardtype()));

		if (p.getVersion() != null) {
			entry.setRecordVersion(p.getVersion());
		}
		if (p.getLowbalancenotificationenabled() != null) {
			entry.setLowBalanceNotificationEnabled(p.getLowbalancenotificationenabled() != null 
					&& p.getLowbalancenotificationenabled());
			entry.setLowBalanceNtfcThresholdAmt(p.getLowbalancentfcthresholdamt());
		}
		if (p.getPocketcode() != null) {
			entry.setPocketCode(p.getPocketcode());
		}
		if (p.getIscollectorpocket() != null) {
			entry.setIsCollectorPocket(p.getIscollectorpocket() != null 
					&& p.getIscollectorpocket() );
		}
		if (p.getNumberofpocketsallowedformdn() != null) {
			entry.setNumberOfPocketsAllowedForMDN(p.getNumberofpocketsallowedformdn().intValue());
		}
		if (p.getIssuspencepocket() != null) {
			entry.setIsSuspencePocket(p.getIssuspencepocket() != null && p.getIssuspencepocket());
		}
		if (p.getIscollectorpocket() != null) {
			entry.setIsSystemPocket(p.getIscollectorpocket() != null && p.getIscollectorpocket());
		}
		if (p.getInterestrate() != null) {
			entry.setInterestRate(p.getInterestrate());
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

		SubscriberMdn smdn = mdnDao.getByMDN(subMdn);
		Integer subscriberType = smdn.getSubscriber().getType();
	
		//Getting group for subscriber
		SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
		List<SubscriberGroups> group = subscriberGroupDao.getAllBySubscriberID(smdn.getSubscriber().getId());
		
		Long subscriberGroupId =null;
		Iterator<SubscriberGroups> iterator = group.iterator();
		while(iterator.hasNext()){			
			subscriberGroupId = iterator.next().getGroupid();
		}
		
		Integer businessPartnerType = null;
		

		if (subscriberType.intValue() != 0) {
			Partner p = partnerDao.getPartnerBySubscriber(smdn.getSubscriber());
			businessPartnerType = p.getBusinesspartnertype().intValue();

		}
		Long kycLevelNo = null;
		if(null != smdn.getSubscriber().getUpgradablekyclevel())
		{
			kycLevelNo = smdn.getSubscriber().getUpgradablekyclevel().longValue();
		}
		else
		{
			KycLevel kyclevel = smdn.getSubscriber().getKycLevel();
			kycLevelNo = kyclevel.getKyclevel().longValue();
		}

		PocketTemplateConfigQuery ptcq = new PocketTemplateConfigQuery();
		ptcq.set_subscriberType(subscriberType);
		ptcq.set_businessPartnerType(businessPartnerType);
		ptcq.set_KYCLevel(kycLevelNo);
		ptcq.set_GroupID(subscriberGroupId != null ? subscriberGroupId : 1);//TODO for default group
		
				
		PocketTemplateConfigDAO ptcDao = new PocketTemplateConfigDAO();
		List<PocketTemplateConfig> ptcresults = ptcDao.get(ptcq);
		/*
		 * if the result set returned for the given Groups is null,
		 * then fetch the results using default System Groups
		 */
		if(ptcresults == null || ptcresults.size() ==0)
		{
			Groups systemGroup = DAOFactory.getInstance().getGroupDao().getSystemGroup();
			if(systemGroup != null)
			{
				ptcq.set_GroupID(systemGroup.getId().longValue());
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
