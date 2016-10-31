/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.PTC_Group_MapDAO;
import com.mfino.dao.PocketTemplateConfigDAO;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.dao.query.PocketTemplateConfigQuery;
import com.mfino.domain.Groups;
import com.mfino.domain.KycLevel;
import com.mfino.domain.PtcGroupMapping;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.PocketTemplateConfig;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSPocketTemplateConfig;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.PocketTemplateConfigProcessor;

/**
 * 
 * @author xchen
 */
@Service("PocketTemplateConfigProcessorImpl")
public class PocketTemplateConfigProcessorImpl extends BaseFixProcessor implements PocketTemplateConfigProcessor{

	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	private void updateEntity(PocketTemplateConfig p,
			CMJSPocketTemplateConfig.CGEntries e) {
		String ID = String.valueOf(p.getId());
		if (ID == null) {
			ID = e.getID().toString();
		}
		if (e.getSubscriberType() != null) {
			if (!e.getSubscriberType().equals(p.getSubscribertype())) {
				log.info("PocketTemplateConfig:" + ID
						+ " Subscriber Type updated to "
						+ e.getSubscriberType() + " by user:"
						+ getLoggedUserNameWithIP());
			}
			p.setSubscribertype(e.getSubscriberType());
		}
		if (e.getBusinessPartnerType() != null) {
			if (!e.getBusinessPartnerType().equals(p.getBusinesspartnertype())) {
				log.info("PocketTemplateConfig:" + ID
						+ " Business Partner Type updated to "
						+ e.getBusinessPartnerType() + " by user:"
						+ getLoggedUserNameWithIP());
			}
			p.setBusinesspartnertype(e.getBusinessPartnerType());
		}
		// else {
		// p.setBusinessPartnerType(-1);
		// }
		if (e.getKYCLevel() != null) {
			if (p.getKycLevel() != null
					&& !e.getKYCLevel().equals(p.getKycLevel().getId())) {
				log.info("PocketTemplateConfig:" + ID + " KYCLevel updated to "
						+ e.getKYCLevel() + " by user:" + getLoggedUserNameWithIP());
			}
			KYCLevelDAO kycLevelDAO = DAOFactory.getInstance().getKycLevelDAO();
			KycLevel kyclevel = kycLevelDAO.getByKycLevel(e.getKYCLevel());
			p.setKycLevel(kyclevel);
		}
		if (e.getPocketType() != null) {
			if (!e.getPocketType().equals(p.getPockettype())) {
				log.info("PocketTemplateConfig:" + ID
						+ " Pocket Type updated to " + e.getPocketType()
						+ " by user:" + getLoggedUserNameWithIP());
			}
			p.setPockettype(e.getPocketType());
		}
		if (e.getCommodity() != null) {
			if (!e.getCommodity().equals(p.getCommodity())) {
				log.info("PocketTemplateConfig:" + ID
						+ " Commodity updated to " + e.getCommodity()
						+ " by user:" + getLoggedUserNameWithIP());
			}
			p.setCommodity(e.getCommodity());
		}
		if (e.getIsSuspencePocket() != null) {
			if (!e.getIsSuspencePocket().equals(p.getIssuspencepocket())) {
				log.info("PocketTemplateConfig:" + ID
						+ " IsSuspencePocket updated to "
						+ e.getIsSuspencePocket() + " by user:"
						+ getLoggedUserNameWithIP());
			}
			p.setIssuspencepocket(e.getIsSuspencePocket());
		}
		if (e.getIsCollectorPocket() != null) {
			if (!e.getIsCollectorPocket().equals(p.getIscollectorpocket())) {
				log.info("PocketTemplateConfig:" + ID
						+ " IsCollectorPocket updated to "
						+ e.getIsCollectorPocket() + " by user:"
						+ getLoggedUserNameWithIP());
			}
			p.setIscollectorpocket(e.getIsCollectorPocket());
		}
		if (e.getIsDefault() != null) {
			if (!e.getIsDefault().equals(p.getIsdefault())) {
				log.info("PocketTemplateConfig:" + ID
						+ " IsCollectorPocket updated to " + e.getIsDefault()
						+ " by user:" + getLoggedUserNameWithIP());
			}
			p.setIsdefault(e.getIsDefault());
		}
		if (e.getPocketTemplateID() != null) {
			if (p.getPocketTemplate() != null
					&& !e.getPocketTemplateID().equals(
							p.getPocketTemplate().getId())) {
				log.info("PocketTemplateConfig:" + ID
						+ " PocketTemplateID updated to "
						+ e.getPocketTemplateID() + " by user:"
						+ getLoggedUserNameWithIP());
			}
			PocketTemplateDAO pocketTemplateDAO = DAOFactory.getInstance()
					.getPocketTemplateDao();
			PocketTemplate pocketTemplate = pocketTemplateDAO.getById(e
					.getPocketTemplateID());
			p.setPocketTemplate(pocketTemplate);
		}

	}

	private void updateMessage(PocketTemplateConfig p,
			CMJSPocketTemplateConfig.CGEntries entry) {
		entry.setID(p.getId().longValue());

		if (Integer.valueOf(p.getSubscribertype()) != null) {
			entry.setSubscriberType(p.getSubscribertype());
		}
		if (p.getBusinesspartnertype() != null) {
			entry.setBusinessPartnerType(p.getBusinesspartnertype().intValue());
		}

		if (p.getPockettype() != null) {
			entry.setPocketType(p.getPockettype());
		}

		if (p.getCommodity() != null) {
			entry.setCommodity(p.getCommodity());
		}

		if (p.getIssuspencepocket() != null) {
			entry.setIsSuspencePocket(p.getIssuspencepocket() != null 
					&& p.getIssuspencepocket());
		}

		if (p.getIscollectorpocket() != null) {
			entry.setIsCollectorPocket(p.getIscollectorpocket() != null 
					&& p.getIscollectorpocket());
		}

		if (p.getIsdefault() != null) {
			entry.setIsDefault(p.getIsdefault() != null && p.getIsdefault());
		}
		//setting value for group while editing from database
		if(p.getId()!=null){
			PTC_Group_MapDAO pgmDao = DAOFactory.getInstance().getPTC_Group_MapDAO();
			PtcGroupMapping pgm = pgmDao.getByPtcId(p.getId().longValue());
			if(pgm!=null){
				if(pgm.getGroups()!=null){
					if(pgm.getGroups().getId() != null){
						entry.setGroupID(String.valueOf(pgm.getGroups().getId()));

					}
					if(pgm.getGroups().getGroupname()!=null){
						entry.setGroupName(pgm.getGroups().getGroupname());
					}
				}
			}

		}

		entry.setSubscriberTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_SubscriberType, null, p.getSubscribertype()));
		entry.setBusinessPartnerTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_BusinessPartnerType, null,
				p.getBusinesspartnertype()));
		entry.setKYCLevelText(p.getKycLevel().getKyclevelname());
		entry.setKYCLevel(p.getKycLevel().getKyclevel().longValue());
		entry.setPocketTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_PocketType, null, p.getPockettype()));
		entry.setCommodityTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_Commodity, null, p.getCommodity()));
		entry.setPocketTemplateID(p.getPocketTemplate().getId().longValue());
		entry.setPocketTemplateDescription(p.getPocketTemplate()
				.getDescription());

	}
	//method for updating column in ptc_group_map table while editing or creating ptc
	private void update_PTC_Group_Map(PocketTemplateConfig p,CMJSPocketTemplateConfig.CGEntries e) {
		PTC_Group_MapDAO mapdao = DAOFactory.getInstance().getPTC_Group_MapDAO();
		PtcGroupMapping m= new PtcGroupMapping();
		if(e.getGroupID()!=null){
			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
			Groups group = groupDao.getById(Long.parseLong(e.getGroupID()));
			m.setGroups(group);
			m.setPocketTemplateConfig(p);
			mapdao.save(m);
		}

	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSPocketTemplateConfig realMsg = (CMJSPocketTemplateConfig) msg;

		PocketTemplateConfigDAO dao = DAOFactory.getInstance()
				.getPocketTemplateConfigDao();
		PTC_Group_MapDAO mapdao = DAOFactory.getInstance().getPTC_Group_MapDAO();

		if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			if (realMsg.getFindDefaultPocketTemplate() != null
					&& realMsg.getFindDefaultPocketTemplate().equalsIgnoreCase(
							"yes")) {
				defaultSet(realMsg, dao);
			} else {
				defaultNotSet(realMsg, dao);
			}

		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg
				.getaction())) {
			CMJSPocketTemplateConfig.CGEntries[] entries = realMsg.getEntries();

			for (CMJSPocketTemplateConfig.CGEntries e : entries) {
				if (e.getIsDefault() != null && e.getIsDefault()) {
					processIsDefaultForSimillarPTCs(e);
				}
			}

			for (CMJSPocketTemplateConfig.CGEntries e : entries) {
				PocketTemplateConfig p = new PocketTemplateConfig();
				updateEntity(p, e);
				dao.save(p);

				// Create PTC_Group_MAP entry

				update_PTC_Group_Map(p,e);

				log.info("PocketTemplateConfig:" + p.getId()
						+ " created by user:" + getLoggedUserNameWithIP());
				updateMessage(p, e);
			}

			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg
				.getaction())) {
			CMJSPocketTemplateConfig.CGEntries[] entries = realMsg.getEntries();


			for (CMJSPocketTemplateConfig.CGEntries e : entries) {
				PocketTemplateConfigQuery ptcq = new PocketTemplateConfigQuery();
				ptcq.set_pocketTemplateConfigID(e.getID());
				List<PocketTemplateConfig> results = dao.get(ptcq);
				PocketTemplateConfig p = null;
				if (results.size() > 0) {
					p = results.get(0);
					if(e.getIsDefault() != null && p.getIsdefault() && e.getIsDefault() == false  ){
						CMJSError err = new CMJSError();
						err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
						err.setErrorDescription(MessageText._("Atleast one configuration need to be default"));
						return err;
					}
				}
			}

			for (CMJSPocketTemplateConfig.CGEntries e : entries) {
				if (e.getIsDefault() != null && e.getIsDefault()) {
					processDefaultRecord(e);
				}
			}

			for (CMJSPocketTemplateConfig.CGEntries e : entries) {
				PocketTemplateConfigQuery ptcq = new PocketTemplateConfigQuery();
				ptcq.set_pocketTemplateConfigID(e.getID());
				List<PocketTemplateConfig> results = dao.get(ptcq);
				PocketTemplateConfig p = null;
				if (results.size() > 0) {
					p = results.get(0);
				}
				updateEntity(p, e);
				dao.save(p);

				// Create PTC_Group_MAP entry
				if(e.getGroupID()!=null){
					mapdao.deleteById(p.getId().longValue());//delete any previous existing row with same ptcid in ptc_group_mapping table
				}
				update_PTC_Group_Map(p,e);


				log.info("PocketTemplateConfig:" + p.getId()
						+ " created by user:" + getLoggedUserNameWithIP());
				updateMessage(p, e);
			}

			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg
				.getaction())) {
		}

		return realMsg;
	}

	private KycLevel getKycLevelObject(Long kycLevel) {
		KYCLevelDAO kycDAO = new KYCLevelDAO();
		return kycDAO.getByKycLevel(kycLevel);
	}

	private void defaultSet(CMJSPocketTemplateConfig realMsg,
			PocketTemplateConfigDAO dao) {
		PocketTemplateConfigQuery query = new PocketTemplateConfigQuery();
		query.setStart(realMsg.getstart());
		query.setLimit(realMsg.getlimit());
		if (realMsg.getSubscriberType() != null) {
			query.set_subscriberType(realMsg.getSubscriberType());
		}
		if (realMsg.getBusinessPartnerType() != null) {
			query.set_businessPartnerType(realMsg.getBusinessPartnerType());
		}
		if (realMsg.getKYCLevel() != null) {
			query.set_KYCLevel(getKycLevelObject(realMsg.getKYCLevel()).getKyclevel().longValue());
		}
		if (realMsg.getCommodityTypeSearch() != null
				&& !realMsg.getCommodityTypeSearch().equalsIgnoreCase("")) {
			query.set_commodity(Integer.parseInt(realMsg
					.getCommodityTypeSearch()));
		}
		if (realMsg.getPocketTypeSearch() != null
				&& !realMsg.getPocketTypeSearch().equalsIgnoreCase("")) {
			query.set_pocketType(Integer.parseInt(realMsg.getPocketTypeSearch()));
		}
		if(StringUtils.isNotBlank(realMsg.getGroupID())){
			query.set_GroupID(Long.parseLong(realMsg.getGroupID()));
		}


		query.set_isDefault(true);

		List<PocketTemplateConfig> results = dao.get(query);
		realMsg.allocateEntries(results.size());

		for (int i = 0; i < results.size(); i++) {
			PocketTemplateConfig p = results.get(i);
			CMJSPocketTemplateConfig.CGEntries entry = new CMJSPocketTemplateConfig.CGEntries();

			updateMessage(p, entry);
			realMsg.getEntries()[i] = entry;
			log.info("PocketTemplateConfig:" + p.getId()
					+ " details viewed completed by user:"
					+ getLoggedUserNameWithIP());
		}
		realMsg.setsuccess(CmFinoFIX.Boolean_True);
		realMsg.settotal(query.getTotal());

	}

	private void defaultNotSet(CMJSPocketTemplateConfig realMsg,
			PocketTemplateConfigDAO dao) {
		PocketTemplateConfigQuery query = new PocketTemplateConfigQuery();
		query.setStart(realMsg.getstart());
		query.setLimit(realMsg.getlimit());
		if (realMsg.getSubscriberType() != null) {
			query.set_subscriberType(realMsg.getSubscriberType());
		}
		if (realMsg.getBusinessPartnerType() != null) {
			query.set_businessPartnerType(realMsg.getBusinessPartnerType());
		}
		if (realMsg.getKYCLevel() != null) {
			query.set_KYCLevel(getKycLevelObject(realMsg.getKYCLevel()).getKyclevel().longValue());
		}
		if (realMsg.getCommodityTypeSearch() != null
				&& !realMsg.getCommodityTypeSearch().equalsIgnoreCase("")) {
			query.set_commodity(Integer.parseInt(realMsg
					.getCommodityTypeSearch()));
		}
		if (realMsg.getPocketTypeSearch() != null
				&& !realMsg.getPocketTypeSearch().equalsIgnoreCase("")) {
			query.set_pocketType(Integer.parseInt(realMsg.getPocketTypeSearch()));
		}
		if (realMsg.getPocketTemplateID() != null) {
			PocketTemplateDAO pocketTemplateDAO = DAOFactory.getInstance()
					.getPocketTemplateDao();
			PocketTemplate pocketTemplate = pocketTemplateDAO.getById(realMsg.getPocketTemplateID());
			query.set_pocketTemplateID(pocketTemplate.getId().longValue());
		}
		if(StringUtils.isNotBlank(realMsg.getGroupID())){
			query.set_GroupID(Long.parseLong(realMsg.getGroupID()));
		}


		List<PocketTemplateConfig> results = dao.get(query);
		realMsg.allocateEntries(results.size());

		for (int i = 0; i < results.size(); i++) {
			PocketTemplateConfig p = results.get(i);
			CMJSPocketTemplateConfig.CGEntries entry = new CMJSPocketTemplateConfig.CGEntries();

			updateMessage(p, entry);
			realMsg.getEntries()[i] = entry;
			log.info("PocketTemplateConfig:" + p.getId()
					+ " details viewed completed by user:"
					+ getLoggedUserNameWithIP());
		}
		realMsg.setsuccess(CmFinoFIX.Boolean_True);
		realMsg.settotal(query.getTotal());
	}

	private void processDefaultRecord(CMJSPocketTemplateConfig.CGEntries entries){
		PocketTemplateConfigDAO dao = DAOFactory.getInstance().getPocketTemplateConfigDao();
		PocketTemplateConfigQuery ptcq = new PocketTemplateConfigQuery();

		List<PocketTemplateConfig> results = null;
		PocketTemplateConfigQuery query = new PocketTemplateConfigQuery();
		if(entries.getID() != null)
		{
			ptcq.set_pocketTemplateConfigID(entries.getID());
			results = dao.get(ptcq);
			PocketTemplateConfig p = null;
			if (results.size() > 0) {
				p = results.get(0);				
				if ((Integer)p.getSubscribertype() != null) {
					query.set_subscriberType(p.getSubscribertype());
				}
				if(p.getBusinesspartnertype() != null){
					query.set_businessPartnerType(p.getBusinesspartnertype());
				}

				if(p.getCommodity() != null){
					query.set_commodity(p.getCommodity());
				}        

				if(p.getKycLevel() != null){
					query.set_KYCLevel(p.getKycLevel().getKyclevel().longValue());
				}
				if( p.getPockettype() != null){
					query.set_pocketType(p.getPockettype());
				}
				if(p.getIscollectorpocket() != null){
					query.set_isCollectorPocket(p.getIscollectorpocket() != null 
							&& p.getIscollectorpocket());
				}
				if(p.getIssuspencepocket() != null){
					query.set_isSuspensePocket(p.getIssuspencepocket() != null 
							&& p.getIssuspencepocket());
				}
				if(p.getPtcGroupMappings() != null){
					Groups pocketTemplateGroup = null;
					Set<PtcGroupMapping> ptcGroupMapSet = p.getPtcGroupMappings();
					Iterator<PtcGroupMapping> iterator = ptcGroupMapSet.iterator();
					if(iterator.hasNext()){
						pocketTemplateGroup = iterator.next().getGroups();
						query.set_GroupID(pocketTemplateGroup.getId().longValue());
					}				
				}			

				//				if(entries.getPocketTemplateID() != null){
				//					query.set_pocketTemplateID(entries.getPocketTemplateID());
				//				}
				query.set_isDefault(true);
			}
		}
		results = dao.get(query);
		if(results.size()> 0){
			PocketTemplateConfig ptc = null;
			for(int index=0 ; index < results.size() ; index++){
				ptc=results.get(index);
				ptc.setIsdefault(Boolean.FALSE);
				dao.save(ptc);				
			}			
		}
	}




	/*
	 * This is called at the time of new PTC insertion.
	 */
	private void processIsDefaultForSimillarPTCs(CMJSPocketTemplateConfig.CGEntries entries){
		PocketTemplateConfigDAO dao = DAOFactory.getInstance().getPocketTemplateConfigDao();
		List<PocketTemplateConfig> results = null;
		PocketTemplateConfigQuery query = new PocketTemplateConfigQuery();
		
		if (entries.getSubscriberType() != null) {
			query.set_subscriberType(entries.getSubscriberType());
		}
		if(entries.getBusinessPartnerType() != null){
			query.set_businessPartnerType(entries.getBusinessPartnerType());
		}

		if(entries.getCommodity() != null){
			query.set_commodity(entries.getCommodity());
		}        

		if(entries.getKYCLevel() != null){
			query.set_KYCLevel(entries.getKYCLevel());
		}
		if(entries.getPocketType() != null){
			query.set_pocketType(entries.getPocketType());
		}
		if(entries.getIsCollectorPocket() != null){
			query.set_isCollectorPocket(entries.getIsCollectorPocket());
		}
		else
		{
			query.set_isCollectorPocket(false);
		}
		if(entries.getIsSuspencePocket() != null){
			query.set_isSuspensePocket(entries.getIsSuspencePocket());
		}
		else
		{
			query.set_isSuspensePocket(false);
		}
		Groups defaultGroup = DAOFactory.getInstance().getGroupDao().getSystemGroup();
		if(entries.getGroupID() != null)
		{
			query.set_GroupID(Long.parseLong(entries.getGroupID()));
		}
		else
		{
			query.set_GroupID(defaultGroup.getId().longValue());
		}

		results = dao.get(query);

		if(results.size()> 0){
			PocketTemplateConfig ptc = null;
			for(int index=0 ; index < results.size() ; index++){
				ptc=results.get(index);
				ptc.setIsdefault(Boolean.FALSE);
				dao.save(ptc);				
			}			
		}
	}

}

