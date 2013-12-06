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
import com.mfino.domain.Group;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.PTC_Group_Map;
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
		String ID = String.valueOf(p.getID());
		if (ID == null) {
			ID = e.getID().toString();
		}
		if (e.getSubscriberType() != null) {
			if (!e.getSubscriberType().equals(p.getSubscriberType())) {
				log.info("PocketTemplateConfig:" + ID
						+ " Subscriber Type updated to "
						+ e.getSubscriberType() + " by user:"
						+ getLoggedUserNameWithIP());
			}
			p.setSubscriberType(e.getSubscriberType());
		}
		if (e.getBusinessPartnerType() != null) {
			if (!e.getBusinessPartnerType().equals(p.getBusinessPartnerType())) {
				log.info("PocketTemplateConfig:" + ID
						+ " Business Partner Type updated to "
						+ e.getBusinessPartnerType() + " by user:"
						+ getLoggedUserNameWithIP());
			}
			p.setBusinessPartnerType(e.getBusinessPartnerType());
		}
		// else {
		// p.setBusinessPartnerType(-1);
		// }
		if (e.getKYCLevel() != null) {
			if (p.getKYCLevelByKYCLevel() != null
					&& !e.getKYCLevel().equals(
							p.getKYCLevelByKYCLevel().getID())) {
				log.info("PocketTemplateConfig:" + ID + " KYCLevel updated to "
						+ e.getKYCLevel() + " by user:" + getLoggedUserNameWithIP());
			}
			KYCLevelDAO kycLevelDAO = DAOFactory.getInstance().getKycLevelDAO();
			KYCLevel kyclevel = kycLevelDAO.getByKycLevel(e.getKYCLevel());
			p.setKYCLevelByKYCLevel(kyclevel);
		}
		if (e.getPocketType() != null) {
			if (!e.getPocketType().equals(p.getPocketType())) {
				log.info("PocketTemplateConfig:" + ID
						+ " Pocket Type updated to " + e.getPocketType()
						+ " by user:" + getLoggedUserNameWithIP());
			}
			p.setPocketType(e.getPocketType());
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
			if (!e.getIsSuspencePocket().equals(p.getIsSuspencePocket())) {
				log.info("PocketTemplateConfig:" + ID
						+ " IsSuspencePocket updated to "
						+ e.getIsSuspencePocket() + " by user:"
						+ getLoggedUserNameWithIP());
			}
			p.setIsSuspencePocket(e.getIsSuspencePocket());
		}
		if (e.getIsCollectorPocket() != null) {
			if (!e.getIsCollectorPocket().equals(p.getIsCollectorPocket())) {
				log.info("PocketTemplateConfig:" + ID
						+ " IsCollectorPocket updated to "
						+ e.getIsCollectorPocket() + " by user:"
						+ getLoggedUserNameWithIP());
			}
			p.setIsCollectorPocket(e.getIsCollectorPocket());
		}
		if (e.getIsDefault() != null) {
			if (!e.getIsDefault().equals(p.getIsDefault())) {
				log.info("PocketTemplateConfig:" + ID
						+ " IsCollectorPocket updated to " + e.getIsDefault()
						+ " by user:" + getLoggedUserNameWithIP());
			}
			p.setIsDefault(e.getIsDefault());
		}
		if (e.getPocketTemplateID() != null) {
			if (p.getPocketTemplate() != null
					&& !e.getPocketTemplateID().equals(
							p.getPocketTemplate().getID())) {
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
		entry.setID(p.getID());

		if (p.getSubscriberType() != null) {
			entry.setSubscriberType(p.getSubscriberType());
		}
		if (p.getBusinessPartnerType() != null) {
			entry.setBusinessPartnerType(p.getBusinessPartnerType());
		}

		if (p.getPocketType() != null) {
			entry.setPocketType(p.getPocketType());
		}

		if (p.getCommodity() != null) {
			entry.setCommodity(p.getCommodity());
		}

		if (p.getIsSuspencePocket() != null) {
			entry.setIsSuspencePocket(p.getIsSuspencePocket());
		}

		if (p.getIsCollectorPocket() != null) {
			entry.setIsCollectorPocket(p.getIsCollectorPocket());
		}

		if (p.getIsDefault() != null) {
			entry.setIsDefault(p.getIsDefault());
		}
		//setting value for group while editing from database
		if(p.getID()!=null){
			PTC_Group_MapDAO pgmDao = DAOFactory.getInstance().getPTC_Group_MapDAO();
			PTC_Group_Map pgm=pgmDao.getByPtcId(p.getID());
			if(pgm!=null){
				if(pgm.getGroup()!=null){
					if(pgm.getGroup().getID()!=null){
						entry.setGroupID(Long.toString(pgm.getGroup().getID()));

					}
					if(pgm.getGroup().getGroupName()!=null){
						entry.setGroupName(pgm.getGroup().getGroupName());
					}
				}
			}

		}

		entry.setSubscriberTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_SubscriberType, null, p.getSubscriberType()));
		entry.setBusinessPartnerTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_BusinessPartnerType, null,
				p.getBusinessPartnerType()));
		entry.setKYCLevelText(p.getKYCLevelByKYCLevel().getKYCLevelName());
		entry.setKYCLevel(p.getKYCLevelByKYCLevel().getKYCLevel());
		entry.setPocketTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_PocketType, null, p.getPocketType()));
		entry.setCommodityTypeText(enumTextService.getEnumTextValue(
				CmFinoFIX.TagID_Commodity, null, p.getCommodity()));
		entry.setPocketTemplateID(p.getPocketTemplate().getID());
		entry.setPocketTemplateDescription(p.getPocketTemplate()
				.getDescription());

	}
	//method for updating column in ptc_group_map table while editing or creating ptc
	private void update_PTC_Group_Map(PocketTemplateConfig p,CMJSPocketTemplateConfig.CGEntries e) {
		PTC_Group_MapDAO mapdao = DAOFactory.getInstance().getPTC_Group_MapDAO();
		PTC_Group_Map m= new PTC_Group_Map();
		if(e.getGroupID()!=null){
			GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
			Group group = groupDao.getById(Long.parseLong(e.getGroupID()));
			m.setGroup(group);
			m.setPocketTemplateConfigByPtcID(p);
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

				log.info("PocketTemplateConfig:" + p.getID()
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
					if(e.getIsDefault() != null && p.getIsDefault() == true && e.getIsDefault() == false  ){
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
					mapdao.deleteById(p.getID());//delete any previous existing row with same ptcid in ptc_group_mapping table
				}
				update_PTC_Group_Map(p,e);


				log.info("PocketTemplateConfig:" + p.getID()
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

	private KYCLevel getKycLevelObject(Long kycLevel) {
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
			query.set_KYCLevel(getKycLevelObject(realMsg.getKYCLevel()).getKYCLevel());
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
			log.info("PocketTemplateConfig:" + p.getID()
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
			query.set_KYCLevel(getKycLevelObject(realMsg.getKYCLevel()).getKYCLevel());
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
			query.set_pocketTemplateID(pocketTemplate.getID());
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
			log.info("PocketTemplateConfig:" + p.getID()
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
				if (p.getSubscriberType() != null) {
					query.set_subscriberType(p.getSubscriberType());
				}
				if(p.getBusinessPartnerType() != null){
					query.set_businessPartnerType(p.getBusinessPartnerType());
				}

				if(p.getCommodity() != null){
					query.set_commodity(p.getCommodity());
				}        

				if(p.getKYCLevelByKYCLevel() != null){
					query.set_KYCLevel(p.getKYCLevelByKYCLevel().getKYCLevel());
				}
				if(p.getPocketType() != null){
					query.set_pocketType(p.getPocketType());
				}
				if(p.getIsCollectorPocket() != null){
					query.set_isCollectorPocket(p.getIsCollectorPocket());
				}
				if(p.getIsSuspencePocket() != null){
					query.set_isSuspensePocket(p.getIsSuspencePocket());
				}
				if(p.getPTC_Group_MapFromPtcID() != null){
					Group pocketTemplateGroup = null;
					Set<PTC_Group_Map> ptcGroupMapSet = p.getPTC_Group_MapFromPtcID();
					Iterator<PTC_Group_Map> iterator = ptcGroupMapSet.iterator();
					if(iterator.hasNext()){
						pocketTemplateGroup = iterator.next().getGroup();
						query.set_GroupID(pocketTemplateGroup.getID());
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
				ptc.setIsDefault(false);
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
		Group defaultGroup = DAOFactory.getInstance().getGroupDao().getSystemGroup();
		if(entries.getGroupID() != null)
		{
			query.set_GroupID(Long.parseLong(entries.getGroupID()));
		}
		else
		{
			query.set_GroupID(defaultGroup.getID());
		}

		results = dao.get(query);

		if(results.size()> 0){
			PocketTemplateConfig ptc = null;
			for(int index=0 ; index < results.size() ; index++){
				ptc=results.get(index);
				ptc.setIsDefault(false);
				dao.save(ptc);				
			}			
		}
	}

}

