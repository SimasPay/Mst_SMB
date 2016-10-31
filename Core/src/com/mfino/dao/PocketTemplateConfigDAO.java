/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.PocketTemplateConfigQuery;
import com.mfino.domain.Groups;
import com.mfino.domain.KycLevel;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.PocketTemplateConfig;
import com.mfino.domain.PtcGroupMapping;

/**
 * 
 * @author pradeep
 */
public class PocketTemplateConfigDAO extends BaseDAO<PocketTemplateConfig> {

	public static final String ID_COLNAME = "id";
	public static final String POCKETTEMPLATETABLE = "PocketTemplate";
    public static final String POCKETTEMPLATE_ALIAS = "PocketTemplateAlias";
    public static final String KYCLEVELTABLE = "KYCLevel";
    public static final String KYCLEVEL_ALIAS = "KYCLevelAlias";

	public List<PocketTemplateConfig> get(PocketTemplateConfigQuery query) {

		Criteria criteria = createCriteria();

		if (query.get_pocketTemplateConfigID() != null) {
			criteria.add(Restrictions.eq(ID_COLNAME,
					query.get_pocketTemplateConfigID()));
		}
		if (query.get_subscriberType() != null) {
			criteria.add(Restrictions.eq(
					PocketTemplateConfig.FieldName_SubscriberType,
					query.get_subscriberType()));
		}
		if (query.get_businessPartnerType() != null) {
			criteria.add(Restrictions
					.eq(PocketTemplateConfig.FieldName_BusinessPartnerType,
							query.get_businessPartnerType()));
		}
		if (query.get_KYCLevel() != null) {
			/*criteria.createAlias(KYCLEVELTABLE, KYCLEVEL_ALIAS);
			criteria.add(Restrictions.eq(KYCLEVEL_ALIAS + DAOConstants.ALIAS_COLNAME_SEPARATOR + KYCLevel.FieldName_RecordID, query.get_KYCLevel()));*/
			criteria.createAlias(PocketTemplateConfig.FieldName_KYCLevelByKYCLevel, "kbk");
			criteria.add(Restrictions.eq("kbk."+KycLevel.FieldName_KYCLevel, query.get_KYCLevel()));
		}
		if (query.get_pocketType() != null) {
			criteria.add(Restrictions.eq(
					PocketTemplateConfig.FieldName_PocketType,
					query.get_pocketType()));
		}
		if (query.get_commodity() != null) {
			criteria.add(Restrictions.eq(
					PocketTemplateConfig.FieldName_Commodity,
					query.get_commodity()));
		}
		if (query.get_pocketTemplateID() != null) {
//			/*criteria.createAlias(PocketTemplateConfig.FieldName_PocketTemplate, "pt");
//			criteria.add(Restrictions
//					.eq("pt."+ID_COLNAME,
//							query.get_pocketTemplateID()));*/
//			criteria.add(Restrictions.eq(
//					PocketTemplateConfig.FieldName_PocketTemplateID,
//					query.get_pocketTemplateID()));
			criteria.createAlias(POCKETTEMPLATETABLE, POCKETTEMPLATE_ALIAS);
			criteria.add(Restrictions.eq(POCKETTEMPLATE_ALIAS + DAOConstants.ALIAS_COLNAME_SEPARATOR + PocketTemplate.FieldName_RecordID, query.get_pocketTemplateID()));
			
		}
		
		if (query.get_isDefault() != null) {
			criteria.add(Restrictions
					.eq(PocketTemplateConfig.FieldName_IsDefault,
							query.get_isDefault()));
		}
		
		if (query.get_isSuspensePocket() != null) {
			criteria.add(Restrictions.eq(PocketTemplateConfig.FieldName_IsSuspencePocket, query.get_isSuspensePocket()));
		}
		
		if (query.get_isCollectorPocket() != null) {
			criteria.add(Restrictions.eq(PocketTemplateConfig.FieldName_IsCollectorPocket, query.get_isCollectorPocket()));
		}
		
		//joining ptc and ptc_group_mapping tables
		if (query.get_GroupID() != null) {
			Criteria ptcMapCriteria = criteria.createCriteria(PocketTemplateConfig.FieldName_PTC_Group_MapFromPtcID);
			Criteria groupCriteria = ptcMapCriteria.createCriteria(PtcGroupMapping.FieldName_Group);
			groupCriteria.add(Restrictions.eq(Groups.FieldName_RecordID, query.get_GroupID()));
		}

		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<PocketTemplateConfig> results = criteria.list();

		return results;
	}

	@Override
	public void save(PocketTemplateConfig p) {
		// update information related to every action here
		// if (p.getmFinoServiceProviderByMSPID() == null) {
		// MfinoServiceProviderDAO mspDao =
		// DAOFactory.getInstance().getMfinoServiceProviderDAO();
		// mFinoServiceProvider msp = mspDao.getById(1L);
		// p.setmFinoServiceProviderByMSPID(msp);
		// }
		
		

		super.save(p);
	}
}
